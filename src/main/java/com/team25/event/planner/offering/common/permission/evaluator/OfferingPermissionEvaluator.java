package com.team25.event.planner.offering.common.permission.evaluator;

import com.team25.event.planner.offering.common.model.Offering;
import com.team25.event.planner.offering.common.repository.OfferingRepository;
import com.team25.event.planner.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OfferingPermissionEvaluator {
    private final OfferingRepository offeringRepository;

    public boolean canEdit(Authentication authentication, Long offeringId) {
        final Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getUserId();

        final Offering offering = offeringRepository.findById(offeringId).orElse(null);
        return offering == null || offering.getOwner().getId().equals(userId);
    }
}
