package com.example.paycheck.domain.notification.event;

import com.example.paycheck.domain.notification.enums.NotificationActionType;
import com.example.paycheck.domain.notification.enums.NotificationType;
import com.example.paycheck.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;

@Getter
@Builder
@AllArgsConstructor
public class NotificationEvent {
    @NonNull
    private final User user;
    @NonNull
    private final NotificationType type;
    @NonNull
    private final String title;
    @NonNull
    private final NotificationActionType actionType;
    @Nullable
    private final String actionData;
}
