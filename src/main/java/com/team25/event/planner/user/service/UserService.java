package com.team25.event.planner.user.service;

import com.team25.event.planner.common.dto.LatLongDTO;
import com.team25.event.planner.common.dto.LocationResponseDTO;
import com.team25.event.planner.common.exception.InvalidRequestError;
import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.exception.ServerError;
import com.team25.event.planner.common.mapper.LocationMapper;
import com.team25.event.planner.common.model.Location;
import com.team25.event.planner.common.service.GeocodingService;
import com.team25.event.planner.common.util.FileUtils;
import com.team25.event.planner.communication.service.ChatMessageService;
import com.team25.event.planner.communication.service.ChatRoomService;
import com.team25.event.planner.communication.service.NotificationService;
import com.team25.event.planner.event.service.EventService;
import com.team25.event.planner.user.dto.BlockRequestDTO;
import com.team25.event.planner.user.dto.RegisterRequestDTO;
import com.team25.event.planner.user.dto.UserRequestDTO;
import com.team25.event.planner.user.dto.UserResponseDTO;
import com.team25.event.planner.user.dto.*;
import com.team25.event.planner.user.mapper.EventOrganizerMapper;
import com.team25.event.planner.user.mapper.OwnerMapper;
import com.team25.event.planner.user.mapper.UserMapper;
import com.team25.event.planner.user.model.*;
import com.team25.event.planner.user.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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
    private final OwnerRepository ownerRepository;
    private final LocationMapper locationMapper;
    private final GeocodingService geocodingService;
    private final EventService eventService;

    private final Path profilePictureFileStorageLocation;
    private final String profilePictureFilenameTemplate;
    private final Path companyPicturesFileStorageLocation;
    private final CurrentUserService currentUserService;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final UserFavoritesService userFavoritesService;
    private final NotificationService notificationService;
    private final EventOrganizerRepository eventOrganizerRepository;


    public UserService(
            UserMapper userMapper, EventOrganizerMapper eventOrganizerMapper, OwnerMapper ownerMapper,
            UserRepository userRepository, SuspensionRepository suspensionRepository, AccountRepository accountRepository,
            @Value("${file-storage.images.profile}") String profilePictureSaveDirectory,
            @Value("${filename.template.profile-pic}") String profilePictureFilenameTemplate,
            @Value("${file-storage.images.company}") String companyPicturesSaveDirectory,
            OwnerRepository ownerRepository, LocationMapper locationMapper, GeocodingService geocodingService, EventService eventService, CurrentUserService currentUserService, EventOrganizerRepository eventOrganizerRepository, ChatMessageService chatMessageService, ChatRoomService chatRoomService, UserFavoritesService userFavoritesService, NotificationService notificationService) {
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
        this.locationMapper = locationMapper;
        this.geocodingService = geocodingService;
        this.eventService = eventService;
        this.currentUserService = currentUserService;
        this.chatMessageService = chatMessageService;
        this.chatRoomService = chatRoomService;
        this.userFavoritesService = userFavoritesService;
        this.notificationService = notificationService;
        this.eventOrganizerRepository = eventOrganizerRepository;
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

        if (user instanceof Owner owner) {
            assert registerRequestDTO.getOwnerFields() != null;
            final List<String> pictureFilenames = saveCompanyPictures(registerRequestDTO.getOwnerFields().getCompanyPictures());
            owner.setCompanyPictures(pictureFilenames);

            final LatLongDTO latLong = geocodingService.getLatLong(registerRequestDTO.getOwnerFields().getCompanyAddress());
            owner.getCompanyAddress().setLatitude(latLong.getLatitude());
            owner.getCompanyAddress().setLongitude(latLong.getLongitude());
        } else if (user instanceof EventOrganizer organizer) {
            assert registerRequestDTO.getEventOrganizerFields() != null;
            final LatLongDTO latLong = geocodingService.getLatLong(registerRequestDTO.getEventOrganizerFields().getLivingAddress());
            organizer.getLivingAddress().setLatitude(latLong.getLatitude());
            organizer.getLivingAddress().setLongitude(latLong.getLongitude());
        }

        try {
            final String filename = saveProfilePicture(registerRequestDTO.getProfilePicture());
            user.setProfilePictureUrl(filename);
            return userRepository.save(user);
        } catch (Exception e) {
            // @Transactional rolls back all database changes if an exception occurs, but not the filesystem changes,
            // so this needs to be done manually
            FileUtils.deleteFiles(profilePictureFileStorageLocation, List.of(user.getProfilePictureUrl()));
            if (user instanceof Owner owner) {
                FileUtils.deleteFiles(companyPicturesFileStorageLocation, owner.getCompanyPictures());
            }
            throw e;
        }
    }

    public UserResponseDTO updateUser(Long userId, @Valid UserRequestDTO userDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));

        if(!user.getUserRole().equals(userDto.getUserRole())) {
            throw new InvalidRequestError("Incorrect user role");
        }

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        // Delay delete operations until all other operations are completed successfully
        List<String> profilePicturesToDelete = new ArrayList<>();
        List<String> companyPicturesToDelete = new ArrayList<>();
        // Rollback filesystem changes in case of failure
        List<String> profilePicturesSaved = new ArrayList<>();
        List<String> companyPicturesSaved = new ArrayList<>();

        try {
            if(userDto.getProfilePicture() != null) {
                final String oldProfilePictureUrl = user.getProfilePictureUrl();
                final String filename = saveProfilePicture(userDto.getProfilePicture());
                profilePicturesSaved.add(filename);
                user.setProfilePictureUrl(filename);
                if(oldProfilePictureUrl != null) {
                    profilePicturesToDelete.add(oldProfilePictureUrl);
                }
            } else if(userDto.getRemoveProfilePicture() != null && userDto.getRemoveProfilePicture()) {
                profilePicturesToDelete.add(user.getProfilePictureUrl());
                user.setProfilePictureUrl(null);
            }

            if (user instanceof Owner owner) {
                OwnerRequestDTO ownerDto = userDto.getOwnerFields();
                assert ownerDto != null;
                owner.setCompanyName(ownerDto.getCompanyName());
                final Location companyAddress = locationMapper.toLocation(ownerDto.getCompanyAddress());
                if(!owner.getCompanyAddress().sameLocationName(companyAddress)) {
                    owner.setCompanyAddress(companyAddress);
                    final LatLongDTO latLong = geocodingService.getLatLong(ownerDto.getCompanyAddress());
                    owner.getCompanyAddress().setLatitude(latLong.getLatitude());
                    owner.getCompanyAddress().setLongitude(latLong.getLongitude());
                }
                owner.setContactPhone(ownerDto.getContactPhone());
                owner.setDescription(ownerDto.getDescription());
                if(ownerDto.getCompanyPictures() != null && !ownerDto.getCompanyPictures().isEmpty()) {
                    List<String> filenames = saveCompanyPictures(ownerDto.getCompanyPictures());
                    companyPicturesSaved.addAll(filenames);
                    owner.getCompanyPictures().addAll(filenames);
                }
                if(ownerDto.getPicturesToRemove() != null && !ownerDto.getPicturesToRemove().isEmpty()) {
                    owner.getCompanyPictures().removeAll(ownerDto.getPicturesToRemove());
                    companyPicturesToDelete.addAll(ownerDto.getPicturesToRemove());
                }
            } else if (user instanceof EventOrganizer organizer) {
                EventOrganizerRequestDTO organizerDto = userDto.getEventOrganizerFields();
                assert organizerDto != null;
                final Location livingAddress = locationMapper.toLocation(organizerDto.getLivingAddress());
                if(!organizer.getLivingAddress().sameLocationName(livingAddress)) {
                    organizer.setLivingAddress(livingAddress);
                    final LatLongDTO latLong = geocodingService.getLatLong(organizerDto.getLivingAddress());
                    organizer.getLivingAddress().setLatitude(latLong.getLatitude());
                    organizer.getLivingAddress().setLongitude(latLong.getLongitude());
                }
                organizer.setPhoneNumber(organizerDto.getPhoneNumber());
            }

            userRepository.save(user);

            FileUtils.deleteFiles(profilePictureFileStorageLocation, profilePicturesToDelete);
            FileUtils.deleteFiles(companyPicturesFileStorageLocation, companyPicturesToDelete);

            return createUserResponseDTO(user);
        } catch (Exception e) {
            logger.info("Starting filesystem rollback on user update");
            FileUtils.deleteFiles(profilePictureFileStorageLocation, profilePicturesSaved);
            FileUtils.deleteFiles(companyPicturesFileStorageLocation, companyPicturesSaved);
            throw e;
        }
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
            FileUtils.deleteFiles(companyPicturesFileStorageLocation, filenames);
            throw failException;
        }

        return filenames;
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

    public boolean blockUser(BlockRequestDTO blockRequestDTO) {
        User user = userRepository.findById(blockRequestDTO.getBlockerUserId()).orElseThrow(()->new NotFoundError("User not found"));
        User blockedUser = userRepository.findById(blockRequestDTO.getBlockedUserId()).orElseThrow(()->new NotFoundError("User not found"));
        user.getBlockedUsers().add(blockedUser);
        blockedUser.getBlockedByUsers().add(user);
        userRepository.save(user);
        userRepository.save(blockedUser);
        if(user instanceof EventOrganizer){
            eventService.blockByEventOrganizer(user, blockedUser);
        }
        return true;
    }

    public void suspendUser(Long accountId) {

        Suspension suspension = new Suspension();
        suspensionRepository.save(suspension);
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundError("Account not found"));
        account.setSuspension(suspension);
        accountRepository.save(account);
    }

    public LocationResponseDTO getUserAddress(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        if (user instanceof EventOrganizer) {
            return locationMapper.toDTO(((EventOrganizer) user).getLivingAddress());
        } else if (user instanceof Owner) {
            return locationMapper.toDTO(((Owner) user).getCompanyAddress());
        }
        return null;
    }

    public Boolean isBlocked(Long userId) {
        User currentUser = currentUserService.getCurrentUser();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        return currentUser.getBlockedUsers().contains(user);
    }

    public User upgradeProfile(@Valid RegisterRequestDTO registerRequestDTO) {
        User user = switch (registerRequestDTO.getUserRole()) {
            case EVENT_ORGANIZER -> eventOrganizerMapper.toEventOrganizer(registerRequestDTO);
            case OWNER -> ownerMapper.toOwner(registerRequestDTO);
            default -> userMapper.toUser(registerRequestDTO);
        };
        if (user instanceof Owner owner) {
            assert registerRequestDTO.getOwnerFields() != null;
            final List<String> pictureFilenames = saveCompanyPictures(registerRequestDTO.getOwnerFields().getCompanyPictures());
            owner.setCompanyPictures(pictureFilenames);

            final LatLongDTO latLong = geocodingService.getLatLong(registerRequestDTO.getOwnerFields().getCompanyAddress());
            owner.getCompanyAddress().setLatitude(latLong.getLatitude());
            owner.getCompanyAddress().setLongitude(latLong.getLongitude());
        } else if (user instanceof EventOrganizer organizer) {
            assert registerRequestDTO.getEventOrganizerFields() != null;
            final LatLongDTO latLong = geocodingService.getLatLong(registerRequestDTO.getEventOrganizerFields().getLivingAddress());
            organizer.getLivingAddress().setLatitude(latLong.getLatitude());
            organizer.getLivingAddress().setLongitude(latLong.getLongitude());
        }
        try {
            final String filename = saveProfilePicture(registerRequestDTO.getProfilePicture());
            user.setProfilePictureUrl(filename);
            return user;
        } catch (Exception e) {
            FileUtils.deleteFiles(profilePictureFileStorageLocation, List.of(user.getProfilePictureUrl()));
            if (user instanceof Owner owner) {
                FileUtils.deleteFiles(companyPicturesFileStorageLocation, owner.getCompanyPictures());
            }
            throw e;
        }
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void insertIntoEventOrganizer(Long userId, Location livingAddress, PhoneNumber phoneNumber) {
        String sql = "INSERT INTO event_organizers (id, address, phone_number,city, country,latitude,longitude) VALUES (?, ?, ?, ?,?,?,?)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, userId);
        query.setParameter(2, livingAddress.getAddress());
        query.setParameter(3, phoneNumber.getPhoneNumber());
        query.setParameter(4, livingAddress.getCity());
        query.setParameter(5, livingAddress.getCountry());
        query.setParameter(6, livingAddress.getLatitude());
        query.setParameter(7, livingAddress.getLongitude());
        query.executeUpdate();
    }

    @Transactional
    public void insertIntoOwner(Long userId, String companyName, Location companyAddress, PhoneNumber contactPhone, String description) {
        String sql = "INSERT INTO owners (id, address, contact_phone,city, country,latitude,longitude, description, company_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?,?,?)";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, userId);
        query.setParameter(2, companyAddress.getAddress());
        query.setParameter(3, contactPhone.getPhoneNumber());
        query.setParameter(4, companyAddress.getCity());
        query.setParameter(5, companyAddress.getCountry());
        query.setParameter(6, companyAddress.getLatitude());
        query.setParameter(7, companyAddress.getLongitude());
        query.setParameter(8, description);
        query.setParameter(9, companyName);

        query.executeUpdate();
    }


}
