package com.example.paycheck.global.security.permission;

import com.example.paycheck.domain.payment.entity.Payment;
import com.example.paycheck.domain.payment.repository.PaymentRepository;
import com.example.paycheck.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("paymentPermission")
@RequiredArgsConstructor
public class PaymentPermission {

    private final PaymentRepository paymentRepository;
    private final WorkplacePermission workplacePermission;

    public boolean canAccess(Long paymentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        Payment payment = paymentRepository.findById(paymentId).orElse(null);

        if (payment == null) {
            return false;
        }

        return payment.getSalary().getContract().getWorkplace().getEmployer().getUser().getId().equals(currentUser.getId());
    }

    public boolean canAccessWorkplacePayments(Long workplaceId) {
        return workplacePermission.canAccess(workplaceId);
    }
}
