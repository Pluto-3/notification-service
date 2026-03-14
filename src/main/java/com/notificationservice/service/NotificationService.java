package com.notificationservice.service;

import com.notificationservice.dto.NotificationRequest;
import com.notificationservice.dto.NotificationResponse;
import com.notificationservice.enums.NotificationStatus;
import com.notificationservice.enums.NotificationType;
import com.notificationservice.model.Notification;
import com.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final EmailService emailService;

    public NotificationResponse send(NotificationRequest request) {
        Notification notification = Notification.builder()
                .type(request.getType())
                .recipient(request.getRecipient())
                .subject(request.getSubject())
                .message(request.getMessage())
                .templateName(request.getTemplateName())
                .status(NotificationStatus.PENDING)
                .build();

        notification = repository.save(notification);
        notification = dispatch(notification, request);
        repository.save(notification);

        return NotificationResponse.from(notification);
    }

    public NotificationResponse getById(Long id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        return NotificationResponse.from(notification);
    }

    public List<NotificationResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    private Notification dispatch(Notification notification, NotificationRequest request) {
        try {
            if (notification.getType() == NotificationType.EMAIL) {
                sendEmail(request);
            } else {
                throw new UnsupportedOperationException(
                        "Type " + notification.getType() + " not yet supported."
                );
            }
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());

        } catch (Exception e) {
            log.error("Failed to send | id={} to={} | error={}",
                    notification.getId(), notification.getRecipient(), e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
        }
        return notification;
    }

    private void sendEmail(NotificationRequest request) throws Exception {
        boolean useTemplate = request.getTemplateName() != null && !request.getTemplateName().isBlank();
        if (useTemplate) {
            emailService.sendWithTemplate(
                    request.getRecipient(),
                    request.getSubject(),
                    request.getTemplateName(),
                    request.getData()
            );
        } else {
            emailService.sendPlain(
                    request.getRecipient(),
                    request.getSubject(),
                    request.getMessage()
            );
        }
    }
}