package com.notificationservice.dto;

import com.notificationservice.enums.NotificationStatus;
import com.notificationservice.enums.NotificationType;
import com.notificationservice.model.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private Long id;
    private NotificationType type;
    private String recipient;
    private String subject;
    private NotificationStatus status;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .recipient(n.getRecipient())
                .subject(n.getSubject())
                .status(n.getStatus())
                .errorMessage(n.getErrorMessage())
                .createdAt(n.getCreatedAt())
                .sentAt(n.getSentAt())
                .build();
    }
}
