package com.w1k5.atc.engine.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClusteredServiceNodeTest {

    @Test
    void testCalculatePort() {
        // Test port calculation for different node IDs and offsets
        assertEquals(9000, ClusteredServiceNode.calculatePort(0, 0));
        assertEquals(9100, ClusteredServiceNode.calculatePort(1, 0));
        assertEquals(9001, ClusteredServiceNode.calculatePort(0, 1));
        assertEquals(9101, ClusteredServiceNode.calculatePort(1, 1));
    }

    @Test
    void testClusterMembers() {
        // Test cluster members string generation
        String members = ClusteredServiceNode.clusterMembers(java.util.Arrays.asList("localhost", "127.0.0.1"));
        
        assertNotNull(members);
        assertTrue(members.contains("localhost"));
        assertTrue(members.contains("127.0.0.1"));
        // The clusterMembers method uses offsets 1-5, not 0
        assertTrue(members.contains("9001")); // Node 0, offset 1
        assertTrue(members.contains("9101")); // Node 1, offset 1
    }

    @Test
    void testMyClusteredServiceInstantiation() {
        // Test that our clustered service can be instantiated
        MyClusteredService service = new MyClusteredService();
        assertNotNull(service);
        
        // Test that it implements the required interface
        assertTrue(service instanceof io.aeron.cluster.service.ClusteredService);
    }

    @Test
    void testMyClusteredServiceLifecycle() {
        MyClusteredService service = new MyClusteredService();
        
        // Test that service can be closed without errors
        assertDoesNotThrow(() -> service.close());
    }
} 