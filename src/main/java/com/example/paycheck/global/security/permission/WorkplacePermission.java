package com.example.paycheck.global.security.permission;

import com.example.paycheck.domain.employer.entity.Employer;
import com.example.paycheck.domain.employer.repository.EmployerRepository;
import com.example.paycheck.domain.user.entity.User;
import com.example.paycheck.domain.workplace.entity.Workplace;
import com.example.paycheck.domain.workplace.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("workplacePermission")
@RequiredArgsConstructor
public class WorkplacePermission {

    private final WorkplaceRepository workplaceRepository;
    private final EmployerRepository employerRepository;

    public boolean canAccess(Long workplaceId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        Workplace workplace = workplaceRepository.findById(workplaceId).orElse(null);

        if (workplace == null) {
            return false;
        }

        return workplace.getEmployer().getUser().getId().equals(currentUser.getId());
    }

    public boolean isEmployer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        return employerRepository.findByUserId(currentUser.getId()).isPresent();
    }
}
