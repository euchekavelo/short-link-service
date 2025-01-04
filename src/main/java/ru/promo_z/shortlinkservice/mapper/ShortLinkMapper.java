package ru.promo_z.shortlinkservice.mapper;

import org.mapstruct.*;
import ru.promo_z.shortlinkservice.dto.request.LinkRequestDto;
import ru.promo_z.shortlinkservice.dto.response.ShortLinkResponseDto;
import ru.promo_z.shortlinkservice.model.ShortLink;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ShortLinkMapper {

    @Mapping(target = "userId", source = "shortLink.user.id")
    public abstract ShortLinkResponseDto shortLinkToShortLinkResponseDto(ShortLink shortLink);

    public abstract ShortLink linkRequestDtoToShortLink(LinkRequestDto linkRequestDto);

    @AfterMapping
    protected void updateLink(@MappingTarget ShortLink postDtoResponse) {
        String currentLink = postDtoResponse.getLink();
        if (!(currentLink.startsWith("http://") || currentLink.startsWith("https://"))) {
            postDtoResponse.setLink("http://" + currentLink);
        }
    }
}
