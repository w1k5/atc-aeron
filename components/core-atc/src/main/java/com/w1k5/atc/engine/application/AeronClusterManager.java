package com.w1k5.atc.engine.application;

import io.aeron.*;
import io.aeron.cluster.ClusteredMediaDriver;
import io.aeron.cluster.ConsensusModule;
import io.aeron.cluster.client.AeronCluster;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import org.agrona.CloseHelper;
import org.agrona.concurrent.ShutdownSignalBarrier;

import java.io.File;
import java.util.List;

import static io.aeron.samples.cluster.ClusterConfig.clusterMembers;

public class AeronClusterManager implements AutoCloseable {
    private final Aeron aeron;
    private final AeronCluster aeronCluster;
    private final ClusteredMediaDriver clusteredMediaDriver;
    private final ShutdownSignalBarrier barrier;

    public AeronClusterManager() {
        // Use centralized configuration
        final String aeronDir = CommonContext.getAeronDirectoryName();
        final int memberId = 1;

        barrier = new ShutdownSignalBarrier();
        File clusterDir = new File(aeronDir);
        if (clusterDir.exists()) {
            clusterDir.delete();  // Ensure there is no stale Mark file
        }

        clusteredMediaDriver = ClusteredMediaDriver.launch(
                new MediaDriver.Context()
                        .aeronDirectoryName(aeronDir)
                        .threadingMode(ThreadingMode.SHARED)
                        .warnIfDirectoryExists(false),
                new io.aeron.archive.Archive.Context()
                        .aeronDirectoryName(aeronDir)
                        .controlChannel("aeron:udp?endpoint=localhost:8010")
                        .controlStreamId(100)
                        .replicationChannel("aeron:udp?endpoint=localhost:8020") // Set replication channel
                        .archiveDir(new File(aeronDir, "archive"))
                        .threadingMode(io.aeron.archive.ArchiveThreadingMode.SHARED),
                new ConsensusModule.Context()
                        .errorHandler(Throwable::printStackTrace)
                        .clusterMembers(clusterMembers(List.of("localhost"), List.of("localhost"), 2000))
                        .clusterDir(new File(aeronDir, "cluster"))
                        .ingressChannel("aeron:udp?endpoint=localhost:1103")
                        .replicationChannel("aeron:udp?endpoint=localhost:1104")
        );

        aeron = Aeron.connect(new Aeron.Context().aeronDirectoryName(aeronDir));

        aeronCluster = AeronCluster.connect(new AeronCluster.Context()
                .aeronDirectoryName(aeronDir)
                .ingressChannel("aeron:udp?endpoint=localhost:1103")
                .egressChannel("aeron:udp?endpoint=localhost:9020")
        );

        System.out.println("Clustered Media Driver and Aeron Cluster initialized.");
    }

    public Publication createPublication(String channel, int streamId) {
        return aeron.addPublication(channel, streamId);
    }

    public Subscription createSubscription(String channel, int streamId) {
        return aeron.addSubscription(channel, streamId);
    }

    @Override
    public void close() {
        CloseHelper.closeAll(aeronCluster, aeron, clusteredMediaDriver);
        System.out.println("Clustered Media Driver shut down.");
    }

    public static void main(String[] args) {
        try (AeronClusterManager manager = new AeronClusterManager()) {
            // Example of publication and subscription setup
            //Publication publication = manager.createPublication("aeron:udp?endpoint=localhost:9030", 10);
           // Subscription subscription = manager.createSubscription("aeron:udp?endpoint=localhost:9030", 10);

            // Barrier for clean shutdown
            System.out.println("Clustered Media Driver running...");
            manager.barrier.await();
        }
    }
}