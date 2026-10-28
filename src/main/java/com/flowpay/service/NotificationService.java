package com.flowpay.service;

import com.flowpay.exception.AppException;
import com.flowpay.exception.ErrorCode;
import com.flowpay.mapper.NotificationMapper;
import com.flowpay.dto.response.NotificationResponse;
import com.flowpay.dto.response.UnreadNotificationCountResponse;
import com.flowpay.entity.NotificationEntity;
import com.flowpay.enums.NotificationStatus;
import com.flowpay.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;


    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UnreadNotificationCountResponse countUnRead(Long userId) {
        long unreadCount = notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);

        return  new UnreadNotificationCountResponse(unreadCount);
    }

    public NotificationResponse markAsRead(Long notificationId, Long userId) {
        NotificationEntity notification =
                notificationRepository.findByIdAndUserId(notificationId, userId).orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if(notification.getStatus() == NotificationStatus.UNREAD){
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
        }

        return notificationMapper.toResponse(notification);
    }
}
