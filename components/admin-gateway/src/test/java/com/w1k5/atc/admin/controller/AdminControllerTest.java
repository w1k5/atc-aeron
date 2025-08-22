package com.w1k5.atc.admin.controller;

import com.w1k5.atc.admin.service.MessageStreamingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test for AdminController
 */
@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private MessageStreamingService streamingService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private AdminController adminController;

    @BeforeEach
    void setUp() {
        adminController = new AdminController(streamingService);
    }

    @Test
    void testGetHealth() {
        // When
        ResponseEntity<Map<String, Object>> response = adminController.getHealth();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> health = response.getBody();
        assertEquals("UP", health.get("status"));
        assertEquals("Admin Gateway", health.get("service"));
        assertNotNull(health.get("timestamp"));
        assertEquals("0.0.1-SNAPSHOT", health.get("version"));
    }

    @Test
    void testGetStreamingMetrics() {
        // Given
        MessageStreamingService.StreamingMetrics metrics = 
            new MessageStreamingService.StreamingMetrics(100, 1024, System.currentTimeMillis());
        when(streamingService.getMetrics()).thenReturn(metrics);

        // When
        ResponseEntity<MessageStreamingService.StreamingMetrics> response = 
            adminController.getStreamingMetrics();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100, response.getBody().totalMessages);
        assertEquals(1024, response.getBody().totalBytes);
    }

    @Test
    void testTestStreaming() {
        // Given
        int messageCount = 50;
        int messageSize = 500;

        // When
        ResponseEntity<Map<String, Object>> response = 
            adminController.testStreaming(messageCount, messageSize);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> result = response.getBody();
        assertEquals(messageCount, result.get("messageCount"));
        assertEquals(messageSize, result.get("messageSize"));
        assertEquals("success", result.get("status"));
        assertNotNull(result.get("duration"));
        assertNotNull(result.get("throughput"));
        
        // Verify streaming service was called
        verify(streamingService).sendBatchMessages(eq("/topic/test"), anyList());
    }

    @Test
    void testTestStreamingWithDefaultValues() {
        // When
        ResponseEntity<Map<String, Object>> response = adminController.testStreaming(0, 0);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> result = response.getBody();
        assertEquals(0, result.get("messageCount"));
        assertEquals(0, result.get("messageSize"));
        assertEquals("success", result.get("status"));
        
        // For zero messages, duration should be very small and throughput should be 0
        assertNotNull(result.get("duration"));
        assertEquals(0L, result.get("throughput"));
    }

    @Test
    void testSendAircraftPositions() {
        // Given
        List<Object> positions = Arrays.asList("Pos1", "Pos2", "Pos3");

        // When
        ResponseEntity<Map<String, Object>> response = 
            adminController.sendAircraftPositions(positions);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> result = response.getBody();
        assertEquals(3, result.get("positionsSent"));
        assertEquals("success", result.get("status"));
        assertNotNull(result.get("duration"));
        
        // Verify streaming service was called
        verify(streamingService).streamAircraftPositions(positions);
    }

    @Test
    void testGetAvailableTopics() {
        // When
        ResponseEntity<Map<String, Object>> response = adminController.getAvailableTopics();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> topics = response.getBody();
        assertEquals("/topic/aircraft/positions", topics.get("aircraft"));
        assertEquals("/topic/admin/stats", topics.get("stats"));
        assertEquals("/topic/admin/conflicts", topics.get("conflicts"));
        assertEquals("/topic/test", topics.get("test"));
    }

    @Test
    void testGetSystemInfo() {
        // When
        ResponseEntity<Map<String, Object>> response = adminController.getSystemInfo();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> info = response.getBody();
        assertNotNull(info.get("javaVersion"));
        assertNotNull(info.get("javaVendor"));
        assertNotNull(info.get("osName"));
        assertNotNull(info.get("osVersion"));
        assertNotNull(info.get("availableProcessors"));
        assertNotNull(info.get("totalMemory"));
        assertNotNull(info.get("freeMemory"));
        assertNotNull(info.get("maxMemory"));
        
        // Verify numeric values are positive
        assertTrue((Integer) info.get("availableProcessors") > 0);
        assertTrue((Long) info.get("totalMemory") > 0);
        assertTrue((Long) info.get("maxMemory") > 0);
    }

    @Test
    void testTestStreamingPerformance() {
        // Given
        int messageCount = 1000;
        int messageSize = 100;

        // When
        ResponseEntity<Map<String, Object>> response = 
            adminController.testStreaming(messageCount, messageSize);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> result = response.getBody();
        assertEquals(messageCount, result.get("messageCount"));
        assertEquals(messageSize, result.get("messageSize"));
        assertEquals("success", result.get("status"));
        
        // Performance should be reasonable
        Long throughput = (Long) result.get("throughput");
        assertTrue(throughput > 0, "Throughput should be positive");
        assertTrue(throughput > 100, "Should handle at least 100 msg/sec");
    }

    @Test
    void testTestStreamingEdgeCases() {
        // Test with very small messages
        ResponseEntity<Map<String, Object>> smallResponse = 
            adminController.testStreaming(10, 1);
        assertEquals(HttpStatus.OK, smallResponse.getStatusCode());
        
        // Test with large message count
        ResponseEntity<Map<String, Object>> largeResponse = 
            adminController.testStreaming(10000, 10);
        assertEquals(HttpStatus.OK, largeResponse.getStatusCode());
        
        // Test with zero messages - should handle gracefully
        ResponseEntity<Map<String, Object>> zeroResponse = 
            adminController.testStreaming(0, 100);
        assertEquals(HttpStatus.OK, zeroResponse.getStatusCode());
        
        Map<String, Object> zeroResult = zeroResponse.getBody();
        assertEquals(0, zeroResult.get("messageCount"));
        assertEquals(0L, zeroResult.get("throughput"));
    }
}
