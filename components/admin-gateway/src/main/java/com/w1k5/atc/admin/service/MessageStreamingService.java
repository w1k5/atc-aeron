package com.w1k5.atc.admin.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;

/**
 * High-performance message streaming service
 * 
 * Optimized for sending many messages quickly over WebSocket streams
 */
@Service
@Slf4j
public class MessageStreamingService {

    private final SimpMessagingTemplate messagingTemplate;
    private final AtomicLong messageCounter = new AtomicLong(0);
    private final AtomicLong bytesSent = new AtomicLong(0);

    public MessageStreamingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send a single message to a specific topic
     */
    public void sendMessage(String topic, Object message) {
        try {
            messagingTemplate.convertAndSend(topic, message);
            messageCounter.incrementAndGet();
            if (message instanceof String) {
                bytesSent.addAndGet(((String) message).getBytes().length);
            }
        } catch (Exception e) {
            log.error("Failed to send message to topic {}: {}", topic, e.getMessage());
        }
    }

    /**
     * Send multiple messages in batch for high throughput
     */
    @Async
    public void sendBatchMessages(String topic, List<Object> messages) {
        long startTime = System.currentTimeMillis();
        int successCount = 0;
        
        for (Object message : messages) {
            try {
                messagingTemplate.convertAndSend(topic, message);
                successCount++;
                messageCounter.incrementAndGet();
            } catch (Exception e) {
                log.error("Failed to send batch message: {}", e.getMessage());
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        // Avoid division by zero
        if (duration > 0) {
            long throughput = (successCount * 1000L) / duration;
            log.info("Batch sent {} messages in {}ms ({} msg/sec)", 
                    successCount, duration, throughput);
        } else {
            log.info("Batch sent {} messages in <1ms", successCount);
        }
    }

    /**
     * Send aircraft position updates at high frequency
     */
    @Async
    public void streamAircraftPositions(List<Object> positions) {
        sendBatchMessages("/topic/aircraft/positions", positions);
    }

    /**
     * Send message flow statistics
     */
    @Async
    public void streamMessageStats(Object stats) {
        sendMessage("/topic/admin/stats", stats);
    }

    /**
     * Send conflict detection alerts
     */
    @Async
    public void streamConflictAlerts(Object alert) {
        sendMessage("/topic/admin/conflicts", alert);
    }

    /**
     * Get streaming performance metrics
     */
    public StreamingMetrics getMetrics() {
        return new StreamingMetrics(
            messageCounter.get(),
            bytesSent.get(),
            System.currentTimeMillis()
        );
    }

    /**
     * Performance metrics for message streaming
     */
    public static class StreamingMetrics {
        public final long totalMessages;
        public final long totalBytes;
        public final long timestamp;

        public StreamingMetrics(long totalMessages, long totalBytes, long timestamp) {
            this.totalMessages = totalMessages;
            this.totalBytes = totalBytes;
            this.timestamp = timestamp;
        }
    }

    /**
     * Scheduled task to log performance metrics
     */
    @Scheduled(fixedRate = 10000) // Every 10 seconds
    public void logPerformanceMetrics() {
        StreamingMetrics metrics = getMetrics();
        log.info("Streaming Performance: {} messages, {} bytes sent", 
                metrics.totalMessages, metrics.totalBytes);
    }
}
