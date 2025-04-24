package com.plus.service.study;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

class InstantStudy {
    private final ZoneId zoneId = ZoneId.of("Asia/Seoul");
    @Test
    void instantZoneIdTest() {
        Instant expiresAt = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24)
                .toInstant()
                .atZone(zoneId)
                .toInstant();
        System.out.println("expiresAt = " + expiresAt);
    }
    @Test
    void instantToLocalDateTimeTest() {
        Instant expiresAt = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24)
                .toInstant()
                .atZone(zoneId)
                .toInstant();
        LocalDateTime dateTime = LocalDateTime.ofInstant(expiresAt, zoneId);
        System.out.println("dateTime = " + dateTime);
    }
}
