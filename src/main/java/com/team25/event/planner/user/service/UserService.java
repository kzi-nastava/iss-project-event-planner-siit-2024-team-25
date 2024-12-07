package com.team25.event.planner.user.service;

import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.exception.ServerError;
import com.team25.event.planner.common.model.Location;
import com.team25.event.planner.common.util.FileUtils;
import com.team25.event.planner.user.dto.*;
import com.team25.event.planner.user.mapper.EventOrganizerMapper;
import com.team25.event.planner.user.mapper.OwnerMapper;
import com.team25.event.planner.user.mapper.UserMapper;
import com.team25.event.planner.user.model.*;
import com.team25.event.planner.user.repository.AccountRepository;
import com.team25.event.planner.user.repository.OwnerRepository;
import com.team25.event.planner.user.repository.SuspensionRepository;
import com.team25.event.planner.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserMapper userMapper;
    private final EventOrganizerMapper eventOrganizerMapper;
    private final OwnerMapper ownerMapper;
    private final UserRepository userRepository;
    private final SuspensionRepository suspensionRepository;
    private final AccountRepository accountRepository;

    private final Path profilePictureFileStorageLocation;
    private final String profilePictureFilenameTemplate;
    private final Path companyPicturesFileStorageLocation;
    private final OwnerRepository ownerRepository;

    public UserService(
            UserMapper userMapper, EventOrganizerMapper eventOrganizerMapper, OwnerMapper ownerMapper,
            UserRepository userRepository, SuspensionRepository suspensionRepository, AccountRepository accountRepository,
            @Value("${file-storage.images.profile}") String profilePictureSaveDirectory,
            @Value("${filename.template.profile-pic}") String profilePictureFilenameTemplate,
            @Value("${file-storage.images.company}") String companyPicturesSaveDirectory,
            OwnerRepository ownerRepository) {
        this.userMapper = userMapper;
        this.eventOrganizerMapper = eventOrganizerMapper;
        this.ownerMapper = ownerMapper;
        this.userRepository = userRepository;
        this.suspensionRepository = suspensionRepository;
        this.accountRepository = accountRepository;
        this.profilePictureFileStorageLocation = Paths.get(profilePictureSaveDirectory).toAbsolutePath().normalize();
        this.profilePictureFilenameTemplate = profilePictureFilenameTemplate;
        this.companyPicturesFileStorageLocation = Paths.get(companyPicturesSaveDirectory).toAbsolutePath().normalize();
        this.ownerRepository = ownerRepository;
    }

    public UserResponseDTO getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        return createUserResponseDTO(user);
    }

    @Transactional
    public User createUser(@Valid RegisterRequestDTO registerRequestDTO) {
        User user = switch (registerRequestDTO.getUserRole()) {
            case EVENT_ORGANIZER -> eventOrganizerMapper.toEventOrganizer(registerRequestDTO);
            case OWNER -> ownerMapper.toOwner(registerRequestDTO);
            default -> userMapper.toUser(registerRequestDTO);
        };

        if (user instanceof Owner) {
            assert registerRequestDTO.getOwnerFields() != null;
            final List<String> pictureFilenames = saveCompanyPictures(registerRequestDTO.getOwnerFields().getCompanyPictures());
            ((Owner) user).setCompanyPictures(pictureFilenames);
        }

        try {
            final String filename = saveProfilePicture(registerRequestDTO.getProfilePicture());
            user.setProfilePictureUrl(filename);

            return userRepository.save(user);
        } catch (Exception e) {
            // @Transactional rolls back all database changes if an exception occurs, but not the filesystem changes,
            // so this needs to be done manually
            if (user instanceof Owner) {
                deleteFiles(companyPicturesFileStorageLocation, ((Owner) user).getCompanyPictures());
            }
            throw e;
        }
    }

    public UserResponseDTO updateUser(Long userId, @Valid UserRequestDTO userRequestDTO) {
        User user = getDummyUser(userRequestDTO);
        user.setId(userId);
        return createUserResponseDTO(user);
    }

    private UserResponseDTO createUserResponseDTO(User user) {
        if (user instanceof EventOrganizer) {
            return eventOrganizerMapper.toDTO((EventOrganizer) user);
        } else if (user instanceof Owner) {
            return ownerMapper.toDTO((Owner) user);
        }
        return userMapper.toDTO(user);
    }

    private String saveProfilePicture(MultipartFile profilePicture) {
        if (profilePicture == null) {
            return null;
        }

        if (!FileUtils.isImage(profilePicture)) {
            throw new InvalidRequestError("Profile picture is not an image file");
        }

        final String filename = profilePictureFilenameTemplate
                .replace("$ID", UUID.randomUUID().toString())
                .replace("$EXT", FileUtils.getExtensionOrDefault(profilePicture, "png"));

        Path filepath = profilePictureFileStorageLocation.resolve(filename);

        try {
            Files.createDirectories(profilePictureFileStorageLocation);
        } catch (IOException e) {
            logger.error("Profile pictures directory creation failed: {}", companyPicturesFileStorageLocation);
            throw new ServerError("Failed to store image", 500);
        }

        try {
            profilePicture.transferTo(filepath.toFile());
        } catch (IOException e) {
            logger.error("Failed to store profile picture");
            throw new ServerError("Failed to store image", 500);
        }

        return filename;
    }

    private List<String> saveCompanyPictures(List<MultipartFile> companyPictures) {
        if (companyPictures == null) {
            return new ArrayList<>();
        }

        try {
            Files.createDirectories(companyPicturesFileStorageLocation);
        } catch (IOException e) {
            logger.error("Company pictures directory creation failed: {}", companyPicturesFileStorageLocation);
            throw new ServerError("Failed to store image", 500);
        }

        List<String> filenames = new ArrayList<>();
        RuntimeException failException = null;
        for (MultipartFile picture : companyPictures) {
            if (!FileUtils.isImage(picture)) {
                failException = new InvalidRequestError("One or more company images are not valid");
                break;
            }

            final String extension = FileUtils.getExtensionOrDefault(picture, "png");
            final String filename = UUID.randomUUID() + "." + extension;
            Path filepath = companyPicturesFileStorageLocation.resolve(filename);

            try {
                picture.transferTo(filepath.toFile());
            } catch (IOException e) {
                logger.error("Failed to store a company image");
                failException = new ServerError("Failed to store image", 500);
            }

            filenames.add(filename);
        }

        if (failException != null) {
            // clean up all successfully saved pictures if one failed
            deleteFiles(companyPicturesFileStorageLocation, filenames);
            throw failException;
        }

        return filenames;
    }

    private static void deleteFiles(Path location, Collection<String> filenames) {
        for (String filename : filenames) {
            try {
                Files.deleteIfExists(location.resolve(filename));
            } catch (IOException cleanupException) {
                logger.error("Failed to delete file during cleanup: {}", filename);
            }
        }
    }

    public Resource getProfilePicture(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));

        if (user.getProfilePictureUrl() == null) {
            throw new NotFoundError("Profile picture not found");
        }

        Path filePath = profilePictureFileStorageLocation.resolve(user.getProfilePictureUrl());
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new NotFoundError("Image not found");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new NotFoundError("Image not found");
        }
    }

    public Resource getOwnerPicture(Long userId, String pictureFilename) {
        Owner owner = ownerRepository.findById(userId).orElseThrow(() -> new NotFoundError("Owner not found"));

        // Maybe remove the check for performance reasons
        if (!owner.getCompanyPictures().contains(pictureFilename)) {
            throw new NotFoundError("Picture not found");
        }

        Path filePath = companyPicturesFileStorageLocation.resolve(pictureFilename);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new NotFoundError("Picture not found");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new NotFoundError("Picture not found");
        }
    }

    // TODO: replace with repository call
    private User getDummyUser(UserRequestDTO userRequestDTO) {
        return switch (userRequestDTO.getUserRole()) {
            case EVENT_ORGANIZER -> eventOrganizerMapper.toEventOrganizer(userRequestDTO);
            case OWNER -> ownerMapper.toOwner(userRequestDTO);
            default -> userMapper.toUser(userRequestDTO);
        };
    }

    // Dummy user generation based on ID
    private User getDummyUser(Long userId) {
        return switch (userId.intValue() % 3) { // Rotate roles based on user ID
            case 0 -> new EventOrganizer(
                    userId,
                    "OrganizerFirst" + userId,
                    "OrganizerLast" + userId,
                    "/profile/organizer" + userId + ".jpg",
                    UserRole.EVENT_ORGANIZER,
                    null,
                    null,
                    null,
                    null,
                    null,
                    new Location("USA", "New York", "123 Organizer Street", 0L, 0L),
                    new PhoneNumber("+1234567890")
            );
            case 1 -> new Owner(
                    1L,
                    "Alice",
                    "Johnson",
                    "/profile/alice.jpg",
                    UserRole.OWNER,
                    new Account(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    "Alice's Bakery",
                    new Location("Canada", "Toronto", "123 Maple Street", 0L, 0L),
                    new PhoneNumber("+123456789"),
                    "Specializing in artisan baked goods",
                    List.of("/images/pic1.jpg", "/images/pic2.jpg")
            );
            default -> new User(
                    userId,
                    "UserFirst" + userId,
                    "UserLast" + userId,
                    null,
                    UserRole.REGULAR,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        };
    }

    public void blockUser(Long userId, BlockRequestDTO blockRequestDTO) {
//        User user = userRepository.findById(userId).orElseThrow(()->new NotFoundError("User not found"));
//        User blockedUser = userRepository.findById(blockRequestDTO.getBlockedUserId()).orElseThrow(()->new NotFoundError("User not found"));
        User user = getDummyUser(userId);
        User blockedUser = getDummyUser(blockRequestDTO.getBlockedUserId());
        user.getBlockedUsers().add(blockedUser);
        blockedUser.getBlockedByUsers().add(user);

//        userRepository.save(user);
//        userRepository.save(blockedUser);
    }

    public void suspendUser(Long accountId) {

        Suspension suspension = new Suspension();
        suspensionRepository.save(suspension);
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundError("Account not found"));
        account.setSuspension(suspension);
        accountRepository.save(account);
    }
}
