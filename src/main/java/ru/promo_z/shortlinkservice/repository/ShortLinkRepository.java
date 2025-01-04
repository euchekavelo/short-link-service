package ru.promo_z.shortlinkservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.promo_z.shortlinkservice.model.ShortLink;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShortLinkRepository extends JpaRepository<ShortLink, UUID> {

    Optional<ShortLink> findByShortLinkAndLink(String shortLink, String link);

    Optional<ShortLink> findByUserIdAndShortLink(UUID userId, String shortLink);

    @Query("SELECT sl FROM ShortLink sl WHERE sl.expirationDate > :dateTime AND sl.hopCounter < sl.hopLimit " +
            "AND sl.user.id = :userId AND sl.shortLink = :shortLink")
    Optional<ShortLink> findActualShortLinkForTransit(@Param("dateTime") LocalDateTime dateTime,
                                                      @Param("userId") UUID userId,
                                                      @Param("shortLink") String shortLink);

    void deleteByUserIdAndLink(UUID userId, String link);

    @Modifying
    @Query("DELETE FROM ShortLink sl WHERE sl.expirationDate <= :dateTime OR sl.hopCounter = sl.hopLimit")
    int removeIrrelevantShortLinks(@Param("dateTime") LocalDateTime dateTime);
}
