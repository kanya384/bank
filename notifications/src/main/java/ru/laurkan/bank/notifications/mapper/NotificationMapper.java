package ru.laurkan.bank.notifications.mapper;

import org.mapstruct.*;
import ru.laurkan.bank.notifications.dto.CreateEmailNotificationRequestDTO;
import ru.laurkan.bank.notifications.dto.EmailNotificationResponseDTO;
import ru.laurkan.bank.notifications.model.EmailNotification;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class NotificationMapper {
    public abstract EmailNotificationResponseDTO map(EmailNotification notification);

    public abstract EmailNotification map(CreateEmailNotificationRequestDTO notification);

    @AfterMapping
    protected void setSentToFalse(@MappingTarget EmailNotification emailNotification) {
        if (emailNotification.getSent() == null) {
            emailNotification.setSent(false);
        }
    }

}
