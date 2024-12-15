package com.team25.event.planner.user.service;

import com.team25.event.planner.security.user.UserDetailsImpl;
import com.team25.event.planner.user.exception.UnauthenticatedError;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetailsImpl) authentication.getPrincipal()).getUserId();
        }
        throw new UnauthenticatedError("Unauthenticated");
    }
}
