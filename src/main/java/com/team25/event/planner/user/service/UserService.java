package com.team25.event.planner.user.service;

import com.team25.event.planner.common.exception.NotFoundError;
import com.team25.event.planner.common.mapper.LocationMapper;
import com.team25.event.planner.common.model.Location;
import com.team25.event.planner.user.dto.BlockRequestDTO;
import com.team25.event.planner.user.dto.RegisterRequestDTO;
import com.team25.event.planner.user.dto.UserRequestDTO;
import com.team25.event.planner.user.dto.UserResponseDTO;
import com.team25.event.planner.user.mapper.EventOrganizerMapper;
import com.team25.event.planner.user.mapper.OwnerMapper;
import com.team25.event.planner.user.mapper.UserMapper;
import com.team25.event.planner.user.model.*;
import com.team25.event.planner.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final EventOrganizerMapper eventOrganizerMapper;
    private final OwnerMapper ownerMapper;
    private final UserRepository userRepository;

    public UserResponseDTO getUser(Long userId) {
        User user = getDummyUser(userId);
        return createUserResponseDTO(user);
    }

    public User createUser(@Valid RegisterRequestDTO registerRequestDTO) {
        User user = new User();
        user.setFirstName(registerRequestDTO.getFirstName());
        user.setLastName(registerRequestDTO.getLastName());
        return new User();
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

    public Resource getProfilePicture(Long userId) {
        return null;
    }

    public Resource getOwnerPicture(Long userId, Long pictureId) {
        return null;
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
}
