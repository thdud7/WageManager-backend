package com.example.paycheck.global.security.permission;

import com.example.paycheck.domain.correction.entity.CorrectionRequest;
import com.example.paycheck.domain.correction.repository.CorrectionRequestRepository;
import com.example.paycheck.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("correctionRequestPermission")
@RequiredArgsConstructor
public class CorrectionRequestPermission {

    private final CorrectionRequestRepository correctionRequestRepository;
    private final WorkplacePermission workplacePermission;

    public boolean canAccessAsEmployer(Long correctionRequestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        CorrectionRequest correctionRequest = correctionRequestRepository.findById(correctionRequestId).orElse(null);

        if (correctionRequest == null) {
            return false;
        }

        return correctionRequest.getWorkRecord().getContract().getWorkplace().getEmployer().getUser().getId().equals(currentUser.getId());
    }

    public boolean canAccessAsWorker(Long correctionRequestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        CorrectionRequest correctionRequest = correctionRequestRepository.findById(correctionRequestId).orElse(null);

        if (correctionRequest == null) {
            return false;
        }

        return correctionRequest.getRequester().getId().equals(currentUser.getId());
    }

    public boolean canAccessWorkplaceCorrectionRequests(Long workplaceId) {
        return workplacePermission.canAccess(workplaceId);
    }
}
