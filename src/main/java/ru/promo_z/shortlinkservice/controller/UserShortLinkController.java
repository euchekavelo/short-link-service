package ru.promo_z.shortlinkservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.promo_z.shortlinkservice.dto.request.LinkRequestDto;
import ru.promo_z.shortlinkservice.dto.request.ShortLinkLimitRequestDto;
import ru.promo_z.shortlinkservice.dto.request.ShortLinkRequestDto;
import ru.promo_z.shortlinkservice.dto.response.ErrorResponseDto;
import ru.promo_z.shortlinkservice.dto.response.ShortLinkResponseDto;
import ru.promo_z.shortlinkservice.exception.LimitException;
import ru.promo_z.shortlinkservice.exception.ShortLinkNotFoundException;
import ru.promo_z.shortlinkservice.exception.UserNotFoundException;
import ru.promo_z.shortlinkservice.service.ShortLinkService;

import java.util.UUID;

@RestController
@RequestMapping("/users/{userId}/short-links")
public class UserShortLinkController {

    private final ShortLinkService shortLinkService;

    @Autowired
    public UserShortLinkController(ShortLinkService shortLinkService) {
        this.shortLinkService = shortLinkService;
    }

    @Operation(summary = "Создать/пересоздать короткую ссылку для существующего пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ShortLinkResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @PostMapping
    public ResponseEntity<ShortLinkResponseDto> createNewShortLinkForUser(@PathVariable UUID userId,
                                                                          @RequestBody LinkRequestDto linkRequestDto)
            throws UserNotFoundException {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shortLinkService.createNewShortLinkForUser(userId, linkRequestDto));
    }

    @Operation(summary = "Удалить короткую ссылку пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteShortLinkByShortLink(@PathVariable UUID userId,
                                                           @RequestBody ShortLinkRequestDto shortLinkRequestDto)
            throws ShortLinkNotFoundException {

        shortLinkService.deleteUsersShortLink(userId, shortLinkRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Изменить лимит переходов по короткой ссылке для пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ShortLinkResponseDto.class))
            }),
            @ApiResponse(responseCode = "400", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @PutMapping
    public ResponseEntity<ShortLinkResponseDto> changeShortLinkLimitByShortLink(
            @PathVariable UUID userId, @RequestBody ShortLinkLimitRequestDto shortLinkLimitRequestDto)
            throws ShortLinkNotFoundException, LimitException {

        return ResponseEntity.ok(shortLinkService.changeLimitUsersShortLink(userId, shortLinkLimitRequestDto));
    }
}
