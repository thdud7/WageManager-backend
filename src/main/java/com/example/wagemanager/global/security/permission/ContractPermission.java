package com.example.wagemanager.global.security.permission;

import com.example.wagemanager.domain.contract.entity.WorkerContract;
import com.example.wagemanager.domain.contract.repository.WorkerContractRepository;
import com.example.wagemanager.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("contractPermission")
@RequiredArgsConstructor
public class ContractPermission {

    private final WorkerContractRepository contractRepository;

    public boolean canAccessAsEmployer(Long contractId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        WorkerContract contract = contractRepository.findById(contractId).orElse(null);

        if (contract == null) {
            return false;
        }

        return contract.getWorkplace().getEmployer().getUser().getId().equals(currentUser.getId());
    }

    public boolean canAccessAsWorker(Long contractId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        WorkerContract contract = contractRepository.findById(contractId).orElse(null);

        if (contract == null) {
            return false;
        }

        return contract.getWorker().getUser().getId().equals(currentUser.getId());
    }
}
