package com.flowpay.dto.response;

import com.flowpay.enums.NotificationStatus;
import com.flowpay.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long transactionId,
        NotificationType type,
        String title,
        String message,
        NotificationStatus status,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {
}
