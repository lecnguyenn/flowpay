package com.flowpay.mapper;

import com.flowpay.dto.response.NotificationResponse;
import com.flowpay.entity.NotificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface NotificationMapper {
    NotificationResponse toResponse(NotificationEntity entity);
}
