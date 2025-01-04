package ru.promo_z.shortlinkservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorResponseDto {

    private String message;
    private boolean result;
}
