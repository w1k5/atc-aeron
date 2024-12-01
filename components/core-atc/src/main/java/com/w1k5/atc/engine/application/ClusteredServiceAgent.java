package com.w1k5.atc.engine.application;

import org.agrona.concurrent.Agent;

public class ClusteredServiceAgent implements Agent, AutoCloseable {

    private final MyClusteredService clusteredService;
    private final AeronClusterManager aeronClusterManager;

    public ClusteredServiceAgent(MyClusteredService clusteredService, AeronClusterManager aeronClusterManager) {
        this.clusteredService = clusteredService;
        this.aeronClusterManager = aeronClusterManager;
    }

    @Override
    public int doWork() throws Exception {
        // Perform your service-specific work here
        clusteredService.onClusterTick(null);
        return 1;  // Return number of work items processed
    }

    @Override
    public String roleName() {
        return "ClusteredServiceAgent";
    }

    @Override
    public void close() {
        clusteredService.close();
        aeronClusterManager.close();  // Ensures AeronClusterManager is also cleaned up
    }
}
