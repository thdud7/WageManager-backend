package com.example.wagemanager.global.security.permission;

import com.example.wagemanager.domain.user.entity.User;
import com.example.wagemanager.domain.worker.entity.Worker;
import com.example.wagemanager.domain.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("workerPermission")
@RequiredArgsConstructor
public class WorkerPermission {

    private final WorkerRepository workerRepository;

    public boolean canAccess(Long workerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        Worker worker = workerRepository.findById(workerId).orElse(null);

        if (worker == null) {
            return false;
        }

        return worker.getUser().getId().equals(currentUser.getId());
    }

    public boolean canAccessByUserId(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        return currentUser.getId().equals(userId);
    }
}
