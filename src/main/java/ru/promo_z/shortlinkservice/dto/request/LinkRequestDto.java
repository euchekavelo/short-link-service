package ru.promo_z.shortlinkservice.dto.request;

import lombok.Data;

@Data
public class LinkRequestDto {

    private String link;
    private Long expirationIntervalSeconds;
    private Integer hopLimit;
}
