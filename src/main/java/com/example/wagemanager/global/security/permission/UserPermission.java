package com.example.wagemanager.global.security.permission;

import com.example.wagemanager.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userPermission")
@RequiredArgsConstructor
public class UserPermission {

    public boolean canAccess(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        return currentUser.getId().equals(userId);
    }

    public boolean canAccessByKakaoId(String kakaoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        return currentUser.getKakaoId().equals(kakaoId);
    }
}
