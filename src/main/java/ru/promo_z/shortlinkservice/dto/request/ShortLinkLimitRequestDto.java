package ru.promo_z.shortlinkservice.dto.request;

import lombok.Data;

@Data
public class ShortLinkLimitRequestDto {

    private String shortLink;
    private Integer hopLimit;
}
