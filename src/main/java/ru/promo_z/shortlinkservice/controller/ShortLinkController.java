package ru.promo_z.shortlinkservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.promo_z.shortlinkservice.dto.request.LinkRequestDto;
import ru.promo_z.shortlinkservice.dto.request.TransitionRequestDto;
import ru.promo_z.shortlinkservice.dto.response.ShortLinkResponseDto;
import ru.promo_z.shortlinkservice.exception.ShortLinkNotFoundException;
import ru.promo_z.shortlinkservice.service.ShortLinkService;

import java.io.IOException;

@RestController
@RequestMapping("/short-links")
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    @Autowired
    public ShortLinkController(ShortLinkService shortLinkService) {
        this.shortLinkService = shortLinkService;
    }

    @PostMapping
    public ResponseEntity<ShortLinkResponseDto> createShortLink(@RequestBody LinkRequestDto linkRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shortLinkService.createShortLink(linkRequestDto));
    }

    @PostMapping("/transition")
    public void transitToShortLink(HttpServletResponse httpServletResponse,
                                   @RequestBody TransitionRequestDto transitionRequestDto)
            throws ShortLinkNotFoundException, IOException {

        ShortLinkResponseDto shortLinkResponseDto = shortLinkService.transitionToShortLink(transitionRequestDto);
        httpServletResponse.sendRedirect(shortLinkResponseDto.getLink());
    }
}
