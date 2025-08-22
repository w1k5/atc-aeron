package com.w1k5.atc.admin;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.mockito.Mockito;

/**
 * Test configuration for admin gateway tests
 */
@TestConfiguration
public class TestConfig {

    /**
     * Mock SimpMessagingTemplate for testing
     */
    @Bean
    @Primary
    public SimpMessagingTemplate mockMessagingTemplate() {
        return Mockito.mock(SimpMessagingTemplate.class);
    }
}
