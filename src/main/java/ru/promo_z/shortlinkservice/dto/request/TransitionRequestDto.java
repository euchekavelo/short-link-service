package ru.promo_z.shortlinkservice.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class TransitionRequestDto {

    private UUID userId;
    private String shortLink;
}
