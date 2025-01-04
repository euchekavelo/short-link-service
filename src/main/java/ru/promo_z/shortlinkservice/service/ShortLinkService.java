package ru.promo_z.shortlinkservice.service;

import ru.promo_z.shortlinkservice.dto.request.LinkRequestDto;
import ru.promo_z.shortlinkservice.dto.request.ShortLinkLimitRequestDto;
import ru.promo_z.shortlinkservice.dto.request.ShortLinkRequestDto;
import ru.promo_z.shortlinkservice.dto.request.TransitionRequestDto;
import ru.promo_z.shortlinkservice.dto.response.ShortLinkResponseDto;
import ru.promo_z.shortlinkservice.exception.LimitException;
import ru.promo_z.shortlinkservice.exception.ShortLinkNotFoundException;
import ru.promo_z.shortlinkservice.exception.UserNotFoundException;

import java.util.UUID;

public interface ShortLinkService {

    ShortLinkResponseDto createShortLink(LinkRequestDto linkRequestDto);

    void removeExpiredShortLinks();

    ShortLinkResponseDto transitionToShortLink(TransitionRequestDto transitionRequestDto)
            throws ShortLinkNotFoundException;

    ShortLinkResponseDto createNewShortLinkForUser(UUID userId, LinkRequestDto linkRequestDto) 
            throws UserNotFoundException;

    void deleteUsersShortLink(UUID userId, ShortLinkRequestDto shortLinkRequestDto) throws ShortLinkNotFoundException;

    ShortLinkResponseDto changeLimitUsersShortLink(UUID userId, ShortLinkLimitRequestDto shortLinkLimitRequestDto)
            throws ShortLinkNotFoundException, LimitException;
}
