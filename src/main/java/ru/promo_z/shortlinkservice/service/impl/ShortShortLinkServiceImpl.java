package ru.promo_z.shortlinkservice.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.promo_z.shortlinkservice.dto.request.LinkRequestDto;
import ru.promo_z.shortlinkservice.dto.request.ShortLinkLimitRequestDto;
import ru.promo_z.shortlinkservice.dto.request.ShortLinkRequestDto;
import ru.promo_z.shortlinkservice.dto.request.TransitionRequestDto;
import ru.promo_z.shortlinkservice.dto.response.ShortLinkResponseDto;
import ru.promo_z.shortlinkservice.exception.LimitException;
import ru.promo_z.shortlinkservice.exception.ShortLinkNotFoundException;
import ru.promo_z.shortlinkservice.exception.UserNotFoundException;
import ru.promo_z.shortlinkservice.mapper.ShortLinkMapper;
import ru.promo_z.shortlinkservice.model.ShortLink;
import ru.promo_z.shortlinkservice.model.User;
import ru.promo_z.shortlinkservice.repository.ShortLinkRepository;
import ru.promo_z.shortlinkservice.repository.UserRepository;
import ru.promo_z.shortlinkservice.service.ShortLinkService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShortShortLinkServiceImpl implements ShortLinkService {

    @Value("${hop.limit}")
    private int hopLimit;

    @Value("${expiration.interval.seconds}")
    private long expirationInterval;

    private static final Logger LOGGER = LoggerFactory.getLogger(ShortShortLinkServiceImpl.class);
    private final UserRepository userRepository;
    private final ShortLinkRepository shortLinkRepository;
    private final ShortLinkMapper shortLinkMapper;

    @Autowired
    public ShortShortLinkServiceImpl(UserRepository userRepository, ShortLinkRepository shortLinkRepository,
                                     ShortLinkMapper shortLinkMapper) {

        this.userRepository = userRepository;
        this.shortLinkRepository = shortLinkRepository;
        this.shortLinkMapper = shortLinkMapper;
    }

    @Transactional
    @Retryable(retryFor = SQLException.class, maxAttempts = Integer.MAX_VALUE)
    @Override
    public ShortLinkResponseDto createShortLink(LinkRequestDto linkRequestDto) {
        User savedUser = userRepository.save(new User());
        ShortLink newShortLink = generateUniqueShortLinkForUser(linkRequestDto, savedUser);
        ShortLink savedShortLink = shortLinkRepository.save(newShortLink);

        return shortLinkMapper.shortLinkToShortLinkResponseDto(savedShortLink);
    }

    @Transactional
    @Retryable(retryFor = SQLException.class, maxAttempts = Integer.MAX_VALUE)
    @Scheduled(cron = "${remove.interval.cron}")
    @Override
    public void removeExpiredShortLinks() {
        LOGGER.info("Removed {} expired or reached limit short links according to schedule.",
                shortLinkRepository.removeIrrelevantShortLinks(LocalDateTime.now()));
    }

    @Transactional
    @Retryable(retryFor = SQLException.class, maxAttempts = Integer.MAX_VALUE)
    @Override
    public ShortLinkResponseDto transitionToShortLink(TransitionRequestDto transitionRequestDto)
            throws ShortLinkNotFoundException {

        Optional<ShortLink> optionalShortLink =
                shortLinkRepository.findActualShortLinkForTransit(LocalDateTime.now(),
                        transitionRequestDto.getUserId(), transitionRequestDto.getShortLink());

        if (optionalShortLink.isEmpty()) {
            throw new ShortLinkNotFoundException("The specified short link is missing or does not " +
                    "meet the conditions for the transition.");
        }

        ShortLink existingShortLink = optionalShortLink.get();
        existingShortLink.setHopCounter(existingShortLink.getHopCounter() + 1);
        ShortLink updatedShortLink = shortLinkRepository.save(existingShortLink);

        return shortLinkMapper.shortLinkToShortLinkResponseDto(updatedShortLink);
    }

    @Transactional
    @Retryable(retryFor = SQLException.class, maxAttempts = Integer.MAX_VALUE)
    @Override
    public ShortLinkResponseDto createNewShortLinkForUser(UUID userId, LinkRequestDto linkRequestDto)
            throws UserNotFoundException {

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("The user with the specified ID was not found.");
        }

        String currentLink = linkRequestDto.getLink();
        if (!(currentLink.startsWith("http://") || currentLink.startsWith("https://"))) {
            currentLink = "http://" + currentLink;
        }
        shortLinkRepository.deleteByUserIdAndLink(userId, currentLink);

        ShortLink newShortLink = generateUniqueShortLinkForUser(linkRequestDto, optionalUser.get());
        ShortLink savedShortLink = shortLinkRepository.save(newShortLink);

        return shortLinkMapper.shortLinkToShortLinkResponseDto(savedShortLink);
    }

    @Transactional
    @Retryable(retryFor = SQLException.class, maxAttempts = Integer.MAX_VALUE)
    @Override
    public void deleteUsersShortLink(UUID userId, ShortLinkRequestDto shortLinkRequestDto)
            throws ShortLinkNotFoundException {

        Optional<ShortLink> optionalShortLink = shortLinkRepository.findByUserIdAndShortLink(userId,
                shortLinkRequestDto.getShortLink());
        if (optionalShortLink.isEmpty()) {
            throw new ShortLinkNotFoundException("The specified short link did not exist or was deleted.");
        }

        shortLinkRepository.delete(optionalShortLink.get());
    }

    @Transactional
    @Retryable(retryFor = SQLException.class, maxAttempts = Integer.MAX_VALUE)
    @Override
    public ShortLinkResponseDto changeLimitUsersShortLink(UUID userId,
                                                          ShortLinkLimitRequestDto shortLinkLimitRequestDto)
            throws ShortLinkNotFoundException, LimitException {

        if (shortLinkLimitRequestDto.getHopLimit() <= hopLimit) {
            throw new LimitException("Invalid value specified. You must specify a limit value greater " +
                    "than the default (default is " + hopLimit + ").");
        }

        Optional<ShortLink> optionalShortLink = shortLinkRepository.findByUserIdAndShortLink(userId,
                shortLinkLimitRequestDto.getShortLink());
        if (optionalShortLink.isEmpty()) {
            throw new ShortLinkNotFoundException("The specified short link did not exist or was deleted.");
        }

        ShortLink existingShortLink = optionalShortLink.get();
        existingShortLink.setHopLimit(shortLinkLimitRequestDto.getHopLimit());
        ShortLink updatedShortLink = shortLinkRepository.save(existingShortLink);

        return shortLinkMapper.shortLinkToShortLinkResponseDto(updatedShortLink);
    }

    private ShortLink generateUniqueShortLinkForUser(LinkRequestDto linkRequestDto, User user) {
        String shortLink;
        Optional<ShortLink> optionalShortLink;
        do {
            shortLink = generateShortLink();
            optionalShortLink = shortLinkRepository.findByShortLinkAndLink(shortLink, linkRequestDto.getLink());
        } while (optionalShortLink.isPresent());

        LocalDateTime currentTime = LocalDateTime.now();
        ShortLink newShortLink = shortLinkMapper.linkRequestDtoToShortLink(linkRequestDto);
        newShortLink.setUser(user);
        newShortLink.setShortLink(shortLink);
        newShortLink.setCreationDate(currentTime);

        long currentExpirationInterval = expirationInterval;
        if (linkRequestDto.getExpirationIntervalSeconds() != null) {
            currentExpirationInterval = Math.min(currentExpirationInterval, linkRequestDto.getExpirationIntervalSeconds());
        }
        newShortLink.setExpirationDate(currentTime.plusSeconds(currentExpirationInterval));

        int currentHopLimit = hopLimit;
        if (linkRequestDto.getHopLimit() != null) {
            currentHopLimit = Math.max(currentHopLimit, linkRequestDto.getHopLimit());
        }
        newShortLink.setHopLimit(currentHopLimit);

        return newShortLink;
    }

    private String generateShortLink() {
        return "clck.ru/" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 3);
    }
}
