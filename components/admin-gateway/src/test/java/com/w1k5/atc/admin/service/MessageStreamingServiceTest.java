package com.w1k5.atc.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test for MessageStreamingService
 */
@ExtendWith(MockitoExtension.class)
class MessageStreamingServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private MessageStreamingService streamingService;

    @BeforeEach
    void setUp() {
        streamingService = new MessageStreamingService(messagingTemplate);
    }

    @Test
    void testSendMessage() {
        // Given
        String topic = "/topic/test";
        String message = "Test message";

        // When
        streamingService.sendMessage(topic, message);

        // Then
        verify(messagingTemplate).convertAndSend(topic, message);
    }

    @Test
    void testSendMessageWithError() {
        // Given
        String topic = "/topic/test";
        String message = "Test message";
        doThrow(new RuntimeException("Test error")).when(messagingTemplate).convertAndSend(eq(topic), any(Object.class));

        // When & Then - Should not throw exception, just log error
        assertDoesNotThrow(() -> streamingService.sendMessage(topic, message));
    }

    @Test
    void testSendBatchMessages() throws InterruptedException {
        // Given
        String topic = "/topic/batch";
        List<Object> messages = Arrays.asList("Message 1", "Message 2", "Message 3");

        // When
        streamingService.sendBatchMessages(topic, messages);
        
        // Wait a bit for async processing
        Thread.sleep(100);

        // Then
        verify(messagingTemplate, times(3)).convertAndSend(eq(topic), any(Object.class));
    }

    @Test
    void testSendBatchMessagesWithErrors() throws InterruptedException {
        // Given
        String topic = "/topic/batch";
        List<Object> messages = Arrays.asList("Message 1", "Message 2", "Message 3");
        
        // Make second message fail
        doThrow(new RuntimeException("Test error"))
            .when(messagingTemplate).convertAndSend(eq(topic), eq("Message 2"));

        // When & Then - Should continue processing other messages
        assertDoesNotThrow(() -> streamingService.sendBatchMessages(topic, messages));
        
        Thread.sleep(100);
        verify(messagingTemplate, times(3)).convertAndSend(eq(topic), any(Object.class));
    }

    @Test
    void testStreamAircraftPositions() throws InterruptedException {
        // Given
        List<Object> positions = Arrays.asList("Pos1", "Pos2", "Pos3");

        // When
        streamingService.streamAircraftPositions(positions);
        
        Thread.sleep(100);

        // Then
        verify(messagingTemplate, times(3)).convertAndSend(eq("/topic/aircraft/positions"), any(Object.class));
    }

    @Test
    void testStreamMessageStats() {
        // Given
        Object stats = "Test stats";

        // When
        streamingService.streamMessageStats(stats);

        // Then
        verify(messagingTemplate).convertAndSend("/topic/admin/stats", stats);
    }

    @Test
    void testStreamConflictAlerts() {
        // Given
        Object alert = "Test conflict alert";

        // When
        streamingService.streamConflictAlerts(alert);

        // Then
        verify(messagingTemplate).convertAndSend("/topic/admin/conflicts", alert);
    }

    @Test
    void testGetMetrics() {
        // When
        MessageStreamingService.StreamingMetrics metrics = streamingService.getMetrics();

        // Then
        assertNotNull(metrics);
        assertEquals(0, metrics.totalMessages);
        assertEquals(0, metrics.totalBytes);
        assertTrue(metrics.timestamp > 0);
    }

    @Test
    void testMessageCounterIncrements() {
        // Given
        String topic = "/topic/test";
        String message = "Test message";

        // When
        streamingService.sendMessage(topic, message);
        MessageStreamingService.StreamingMetrics metrics = streamingService.getMetrics();

        // Then
        assertEquals(1, metrics.totalMessages);
        assertTrue(metrics.totalBytes > 0);
    }

    @Test
    void testBytesSentCalculation() {
        // Given
        String topic = "/topic/test";
        String message = "Hello World"; // 11 bytes

        // When
        streamingService.sendMessage(topic, message);
        MessageStreamingService.StreamingMetrics metrics = streamingService.getMetrics();

        // Then
        assertEquals(1, metrics.totalMessages);
        assertEquals(11, metrics.totalBytes);
    }

    @Test
    void testBatchMessagePerformance() throws InterruptedException {
        // Given
        String topic = "/topic/performance";
        List<Object> messages = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            messages.add("Message " + i);
        }

        // When
        long startTime = System.currentTimeMillis();
        streamingService.sendBatchMessages(topic, messages);
        Thread.sleep(200); // Wait for async processing
        long endTime = System.currentTimeMillis();

        // Then
        verify(messagingTemplate, times(100)).convertAndSend(eq(topic), any(Object.class));
        
        // Ensure we have a reasonable duration for performance testing
        long duration = endTime - startTime;
        assertTrue(duration >= 0, "Duration should be non-negative");
        assertTrue(duration < 1000, "Should complete quickly (under 1 second)");
    }
}
