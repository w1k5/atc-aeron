package com.w1k5.atc.admin.controller;

import com.w1k5.atc.admin.service.MessageStreamingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Admin REST Controller
 * 
 * Provides endpoints for monitoring and controlling the admin gateway
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*") // Allow all origins for development
@Slf4j
public class AdminController {

    private final MessageStreamingService streamingService;

    @Autowired
    public AdminController(MessageStreamingService streamingService) {
        this.streamingService = streamingService;
    }

    /**
     * Get system health and status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Admin Gateway");
        health.put("timestamp", System.currentTimeMillis());
        health.put("version", "0.0.1-SNAPSHOT");
        
        return ResponseEntity.ok(health);
    }

    /**
     * Get streaming performance metrics
     */
    @GetMapping("/metrics/streaming")
    public ResponseEntity<MessageStreamingService.StreamingMetrics> getStreamingMetrics() {
        return ResponseEntity.ok(streamingService.getMetrics());
    }

    /**
     * Send test messages for performance testing
     */
    @PostMapping("/test/stream")
    public ResponseEntity<Map<String, Object>> testStreaming(
            @RequestParam(defaultValue = "100") int messageCount,
            @RequestParam(defaultValue = "1000") int messageSize) {
        
        long startTime = System.currentTimeMillis();
        List<Object> testMessages = new ArrayList<>();
        
        // Generate test messages
        for (int i = 0; i < messageCount; i++) {
            String message = String.format("Test message %d: %s", i, 
                "x".repeat(Math.max(0, messageSize - 20)));
            testMessages.add(message);
        }
        
        // Stream the messages
        streamingService.sendBatchMessages("/topic/test", testMessages);
        
        long duration = System.currentTimeMillis() - startTime;
        
        Map<String, Object> result = new HashMap<>();
        result.put("messageCount", messageCount);
        result.put("messageSize", messageSize);
        result.put("duration", duration);
        
        // Calculate throughput, handling zero message count
        long throughput = (messageCount > 0 && duration > 0) ? (messageCount * 1000L) / duration : 0L;
        result.put("throughput", throughput);
        result.put("status", "success");
        
        log.info("Test streaming completed: {} messages in {}ms ({} msg/sec)", 
                messageCount, duration, throughput);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Send aircraft position updates (for testing)
     */
    @PostMapping("/aircraft/positions")
    public ResponseEntity<Map<String, Object>> sendAircraftPositions(
            @RequestBody List<Object> positions) {
        
        long startTime = System.currentTimeMillis();
        streamingService.streamAircraftPositions(positions);
        long duration = System.currentTimeMillis() - startTime;
        
        Map<String, Object> result = new HashMap<>();
        result.put("positionsSent", positions.size());
        result.put("duration", duration);
        result.put("status", "success");
        
        return ResponseEntity.ok(result);
    }

    /**
     * Get available WebSocket topics
     */
    @GetMapping("/topics")
    public ResponseEntity<Map<String, Object>> getAvailableTopics() {
        Map<String, Object> topics = new HashMap<>();
        topics.put("aircraft", "/topic/aircraft/positions");
        topics.put("stats", "/topic/admin/stats");
        topics.put("conflicts", "/topic/admin/conflicts");
        topics.put("test", "/topic/test");
        
        return ResponseEntity.ok(topics);
    }

    /**
     * Get system information
     */
    @GetMapping("/system/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Runtime runtime = Runtime.getRuntime();
        
        Map<String, Object> info = new HashMap<>();
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("javaVendor", System.getProperty("java.vendor"));
        info.put("osName", System.getProperty("os.name"));
        info.put("osVersion", System.getProperty("os.version"));
        info.put("availableProcessors", runtime.availableProcessors());
        info.put("totalMemory", runtime.totalMemory());
        info.put("freeMemory", runtime.freeMemory());
        info.put("maxMemory", runtime.maxMemory());
        
        return ResponseEntity.ok(info);
    }
}
