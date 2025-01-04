package ru.promo_z.shortlinkservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.promo_z.shortlinkservice.dto.request.LinkRequestDto;
import ru.promo_z.shortlinkservice.dto.request.ShortLinkLimitRequestDto;
import ru.promo_z.shortlinkservice.dto.request.ShortLinkRequestDto;
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

    @PostMapping
    public ResponseEntity<ShortLinkResponseDto> createNewShortLinkForUser(@PathVariable UUID userId,
                                                                          @RequestBody LinkRequestDto linkRequestDto)
            throws UserNotFoundException {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shortLinkService.createNewShortLinkForUser(userId, linkRequestDto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteShortLinkByShortLink(@PathVariable UUID userId,
                                                           @RequestBody ShortLinkRequestDto shortLinkRequestDto)
            throws ShortLinkNotFoundException {

        shortLinkService.deleteUsersShortLink(userId, shortLinkRequestDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<ShortLinkResponseDto> changeShortLinkLimitByShortLink(
            @PathVariable UUID userId, @RequestBody ShortLinkLimitRequestDto shortLinkLimitRequestDto)
            throws ShortLinkNotFoundException, LimitException {

        return ResponseEntity.ok(shortLinkService.changeLimitUsersShortLink(userId, shortLinkLimitRequestDto));
    }
}
