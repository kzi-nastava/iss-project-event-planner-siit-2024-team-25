package com.team25.event.planner.user.service;

import com.team25.event.planner.security.user.UserDetailsImpl;
import com.team25.event.planner.user.model.User;
import com.team25.event.planner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
    private final UserRepository userRepository;

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetailsImpl) authentication.getPrincipal()).getUserId();
        }
        return null;
    }

    public User getCurrentUser() {
        return userRepository.findById(getCurrentUserId()).orElse(null);
    }
}
