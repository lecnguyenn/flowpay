package com.flowpay.controller;


import com.flowpay.repository.NotificationRepository;
import com.flowpay.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private static final int MAX_PAGE_SIZE = 100;

    private final NotificationService notificationService;
    // private final Current
}
