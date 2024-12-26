package com.w1k5.atc.engine.application;//

import io.aeron.ChannelUriStringBuilder;
import io.aeron.CommonContext;
import io.aeron.archive.Archive;
import io.aeron.archive.ArchiveThreadingMode;
import io.aeron.archive.client.AeronArchive;
import io.aeron.cluster.ClusteredMediaDriver;
import io.aeron.cluster.ConsensusModule;
import io.aeron.cluster.service.ClusteredServiceContainer;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.MinMulticastFlowControlSupplier;
import io.aeron.driver.ThreadingMode;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.agrona.ErrorHandler;
import org.agrona.concurrent.NoOpLock;
import org.agrona.concurrent.ShutdownSignalBarrier;

public class ClusteredServiceNode {
    private static ErrorHandler errorHandler(String context) {
        return (throwable) -> {
            System.err.println(context);
            throwable.printStackTrace(System.err);
        };
    }

    public static int calculatePort(int nodeId, int offset) {
        return 9000 + nodeId * 100 + offset;
    }

    private static String udpChannel(int nodeId, String hostname, int portOffset) {
        int port = calculatePort(nodeId, portOffset);
        return (new ChannelUriStringBuilder()).media("udp").termLength(65536).endpoint(hostname + ":" + port).build();
    }

    private static String logControlChannel(int nodeId, String hostname, int portOffset) {
        int port = calculatePort(nodeId, portOffset);
        return (new ChannelUriStringBuilder()).media("udp").termLength(65536).controlMode("manual").controlEndpoint(hostname + ":" + port).build();
    }

    private static String logReplicationChannel(String hostname) {
        return (new ChannelUriStringBuilder()).media("udp").endpoint(hostname + ":0").build();
    }

    private static String clusterMembers(List<String> hostnames) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < hostnames.size(); ++i) {
            sb.append(i);
            sb.append(',').append((String) hostnames.get(i)).append(':').append(calculatePort(i, 2));
            sb.append(',').append((String) hostnames.get(i)).append(':').append(calculatePort(i, 3));
            sb.append(',').append((String) hostnames.get(i)).append(':').append(calculatePort(i, 4));
            sb.append(',').append((String) hostnames.get(i)).append(':').append(calculatePort(i, 5));
            sb.append(',').append((String) hostnames.get(i)).append(':').append(calculatePort(i, 1));
            sb.append('|');
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        int nodeId = Integer.parseInt(System.getProperty("aeron.cluster.tutorial.nodeId", "0"));
        String[] hostnames = System.getProperty("aeron.cluster.tutorial.hostnames",
                "localhost,localhost,localhost").split(",");
        String hostname = hostnames[nodeId];
        File baseDir = new File(CommonContext.getAeronDirectoryName(), "node" + nodeId);
        String aeronDirName = CommonContext.getAeronDirectoryName() + "-" + nodeId + "-driver";
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        MediaDriver.Context var10000 = (new MediaDriver.Context())
                .aeronDirectoryName(aeronDirName)
                .threadingMode(ThreadingMode.SHARED)
                .termBufferSparseFile(true)
                .multicastFlowControlSupplier(new MinMulticastFlowControlSupplier());
        MediaDriver.Context mediaDriverContext = var10000
                .terminationHook(barrier::signal)
                .errorHandler(errorHandler("Media Driver"));
        AeronArchive.Context replicationArchiveContext = (new AeronArchive.Context())
                .controlResponseChannel("aeron:udp?endpoint=" + hostname + ":0");
        Archive.Context archiveContext = (new Archive.Context())
                .aeronDirectoryName(aeronDirName)
                .archiveDir(new File(baseDir, "archive"))
                .controlChannel(udpChannel(nodeId, hostname, 1))
                .archiveClientContext(replicationArchiveContext)
                .replicationChannel(logReplicationChannel(hostname))
                .localControlChannel("aeron:ipc?term-length=64k")
                .recordingEventsEnabled(false)
                .threadingMode(ArchiveThreadingMode.SHARED);
        AeronArchive.Context aeronArchiveContext = (new AeronArchive.Context())
                .lock(NoOpLock.INSTANCE)
                .controlRequestChannel(archiveContext.localControlChannel())
                .controlResponseChannel(archiveContext.localControlChannel())
                .aeronDirectoryName(aeronDirName);
        ConsensusModule.Context consensusModuleContext = (new ConsensusModule.Context())
                .errorHandler(errorHandler("Consensus Module"))
                .clusterMemberId(nodeId)
                .clusterMembers(clusterMembers(Arrays.asList(hostnames)))
                .clusterDir(new File(baseDir, "cluster"))
                .ingressChannel("aeron:udp?term-length=64k")
                .logChannel(logControlChannel(nodeId, hostname, 6))
                .replicationChannel(logReplicationChannel(hostname))
                .archiveContext(aeronArchiveContext.clone());
        ClusteredServiceContainer.Context clusteredServiceContext =
                (new ClusteredServiceContainer.Context())
                        .aeronDirectoryName(aeronDirName)
                        .archiveContext(aeronArchiveContext.clone())
                        .clusterDir(new File(baseDir, "cluster"))
                        .clusteredService(new MyClusteredService())
                        .errorHandler(errorHandler("Clustered Service"));
        ClusteredMediaDriver clusteredMediaDriver =
                ClusteredMediaDriver.launch(mediaDriverContext, archiveContext, consensusModuleContext);
        Throwable var14 = null;

        try {
            ClusteredServiceContainer container =
                    ClusteredServiceContainer.launch(clusteredServiceContext);
            Throwable var16 = null;

            try {
                System.out.println("[" + nodeId + "] Started Cluster Node on " + hostname + "...");
                barrier.await();
                System.out.println("[" + nodeId + "] Exiting");
            } catch (Throwable var39) {
                var16 = var39;
                throw var39;
            } finally {
                if (var16 != null) {
                    try {
                        container.close();
                    } catch (Throwable var38) {
                        var16.addSuppressed(var38);
                    }
                } else {
                    container.close();
                }

            }
        } catch (Throwable var41) {
            var14 = var41;
            throw var41;
        } finally {
            if (var14 != null) {
                try {
                    clusteredMediaDriver.close();
                } catch (Throwable var37) {
                    var14.addSuppressed(var37);
                }
            } else {
                clusteredMediaDriver.close();
            }

        }

    }
}
