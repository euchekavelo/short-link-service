package ru.promo_z.shortlinkservice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ShortLinkResponseDto {

    private UUID id;
    private UUID userId;
    private String shortLink;
    private String link;
    private int hopCounter;
    private int hopLimit;
    private LocalDateTime expirationDate;
    private LocalDateTime creationDate;
}
