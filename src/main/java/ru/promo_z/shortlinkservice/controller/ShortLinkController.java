package ru.promo_z.shortlinkservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.promo_z.shortlinkservice.dto.request.LinkRequestDto;
import ru.promo_z.shortlinkservice.dto.request.TransitionRequestDto;
import ru.promo_z.shortlinkservice.dto.response.ErrorResponseDto;
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

    @Operation(summary = "Создать короткую ссылку для нового пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ShortLinkResponseDto.class))
            })
    })
    @PostMapping
    public ResponseEntity<ShortLinkResponseDto> createShortLink(@RequestBody LinkRequestDto linkRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shortLinkService.createShortLink(linkRequestDto));
    }

    @Operation(summary = "Выполнить переход по короткой ссылке.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            })
    })
    @PostMapping("/transition")
    public void transitToShortLink(HttpServletResponse httpServletResponse,
                                   @RequestBody TransitionRequestDto transitionRequestDto)
            throws ShortLinkNotFoundException, IOException {

        ShortLinkResponseDto shortLinkResponseDto = shortLinkService.transitionToShortLink(transitionRequestDto);
        httpServletResponse.sendRedirect(shortLinkResponseDto.getLink());
    }
}
