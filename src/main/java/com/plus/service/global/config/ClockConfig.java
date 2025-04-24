package com.plus.service.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfig {
    @Bean
    public Clock clock(ZoneId zoneId) {
        return Clock.system(zoneId);
    }
    @Bean
    public ZoneId zoneId() {
        return ZoneId.systemDefault();
    }
}
