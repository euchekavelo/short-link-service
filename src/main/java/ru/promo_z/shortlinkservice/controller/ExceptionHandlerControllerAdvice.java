package ru.promo_z.shortlinkservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.promo_z.shortlinkservice.dto.response.ErrorResponseDto;
import ru.promo_z.shortlinkservice.exception.LimitException;
import ru.promo_z.shortlinkservice.exception.ShortLinkNotFoundException;
import ru.promo_z.shortlinkservice.exception.UserNotFoundException;

import java.io.IOException;
import java.net.URISyntaxException;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler({ShortLinkNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleShortLinkNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler({LimitException.class, URISyntaxException.class, IOException.class})
    public ResponseEntity<ErrorResponseDto> handleReachedLimitException(Exception ex) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(ex.getMessage()));
    }

    private ErrorResponseDto getErrorResponseDto(String message) {
        return ErrorResponseDto.builder()
                .message(message)
                .result(false)
                .build();
    }
}
