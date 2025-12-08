package com.example.wagemanager.global.security.permission;

import com.example.wagemanager.domain.user.entity.User;
import com.example.wagemanager.domain.workrecord.entity.WorkRecord;
import com.example.wagemanager.domain.workrecord.repository.WorkRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("workRecordPermission")
@RequiredArgsConstructor
public class WorkRecordPermission {

    private final WorkRecordRepository workRecordRepository;
    private final WorkplacePermission workplacePermission;

    public boolean canAccessAsEmployer(Long workRecordId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        WorkRecord workRecord = workRecordRepository.findById(workRecordId).orElse(null);

        if (workRecord == null) {
            return false;
        }

        return workRecord.getContract().getWorkplace().getEmployer().getUser().getId().equals(currentUser.getId());
    }

    public boolean canAccessAsWorker(Long workRecordId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        WorkRecord workRecord = workRecordRepository.findById(workRecordId).orElse(null);

        if (workRecord == null) {
            return false;
        }

        return workRecord.getContract().getWorker().getUser().getId().equals(currentUser.getId());
    }

    public boolean canAccessWorkplaceRecords(Long workplaceId) {
        return workplacePermission.canAccess(workplaceId);
    }
}
