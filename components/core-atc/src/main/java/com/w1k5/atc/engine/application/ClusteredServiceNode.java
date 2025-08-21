package com.w1k5.atc.engine.application;

import io.aeron.*;
import io.aeron.archive.*;
import io.aeron.archive.client.AeronArchive;
import io.aeron.cluster.*;
import io.aeron.driver.*;
import io.aeron.samples.cluster.tutorial.BasicAuctionClusterClient;
import org.agrona.concurrent.*;
import org.agrona.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ClusteredServiceNode {
    private static ErrorHandler errorHandler(String context) {
        return (throwable) -> {
            System.err.println("[ERROR] " + context);
            throwable.printStackTrace(System.err);
        };
    }

    public static int calculatePort(int nodeId, int offset) {
        return 9000 + nodeId * 100 + offset;
    }

    private static String udpChannel(int nodeId, String hostname, int portOffset) {
        int port = calculatePort(nodeId, portOffset);
        String channel = new ChannelUriStringBuilder()
                .media("udp")
                .termLength(65536)
                .endpoint(hostname + ":" + port)
                .build();
        System.out.println("[ClusteredServiceNode] UDP Channel: " + channel);
        return channel;
    }

    private static String logControlChannel(int nodeId, String hostname, int portOffset) {
        int port = calculatePort(nodeId, portOffset);
        String channel = new ChannelUriStringBuilder()
                .media("udp")
                .termLength(65536)
                .controlMode("manual")
                .controlEndpoint(hostname + ":" + port)
                .build();
        System.out.println("[ClusteredServiceNode] Log Control Channel: " + channel);
        return channel;
    }

    private static String logReplicationChannel(String hostname) {
        return new ChannelUriStringBuilder()
                .media("udp")
                .endpoint(hostname + ":0")
                .build();
    }

    public static String clusterMembers(List<String> hostnames) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hostnames.size(); ++i) {
            sb.append(i)
                    .append(',').append(hostnames.get(i)).append(':').append(calculatePort(i, 2))
                    .append(',').append(hostnames.get(i)).append(':').append(calculatePort(i, 3))
                    .append(',').append(hostnames.get(i)).append(':').append(calculatePort(i, 4))
                    .append(',').append(hostnames.get(i)).append(':').append(calculatePort(i, 5))
                    .append(',').append(hostnames.get(i)).append(':').append(calculatePort(i, 1))
                    .append('|');
        }
        String members = sb.toString();
        System.out.println("[ClusteredServiceNode] Cluster Members: " + members);
        return members;
    }

    public static void main(String[] args) {
        int nodeId = Integer.parseInt(System.getProperty("aeron.cluster.tutorial.nodeId", "0"));
        String[] hostnames = System.getProperty("aeron.cluster.tutorial.hostnames", "localhost").split(",");
        String hostname = hostnames[nodeId];

        System.out.println("[ClusteredServiceNode] Starting node " + nodeId + " at " + hostname);

        File baseDir = new File(CommonContext.getAeronDirectoryName(), "node" + nodeId);
        String aeronDirName = CommonContext.getAeronDirectoryName() + "-" + nodeId + "-driver";
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();

        MediaDriver.Context mediaDriverContext = new MediaDriver.Context()
                .aeronDirectoryName(aeronDirName)
                .threadingMode(ThreadingMode.SHARED)
                .termBufferSparseFile(true)
                .dirDeleteOnShutdown(true)
                .dirDeleteOnStart(true)
                .errorHandler(errorHandler("Media Driver"));

        AeronArchive.Context replicationArchiveContext = new AeronArchive.Context()
                .controlResponseChannel("aeron:udp?endpoint=" + hostname + ":0");

        Archive.Context archiveContext = new Archive.Context()
                .deleteArchiveOnStart(true)
                .aeronDirectoryName(aeronDirName)
                .archiveDir(new File(baseDir, "archive"))
                .controlChannel(udpChannel(nodeId, hostname, 1))
                .archiveClientContext(replicationArchiveContext)
                .replicationChannel(logReplicationChannel(hostname))
                .localControlChannel("aeron:ipc?term-length=64k");

        ConsensusModule.Context consensusModuleContext = new ConsensusModule.Context()
                .clusterMemberId(nodeId)
                .deleteDirOnStart(true)
                .replicationChannel(logReplicationChannel(hostname))
                .clusterMembers(clusterMembers(Arrays.asList(hostnames)))
                .ingressChannel("aeron:udp?endpoint=localhost:8000")
                .egressChannel("aeron:udp?endpoint=localhost:8001");

        System.out.println("[ClusteredServiceNode] Node initialized. Awaiting connections");

        try (ClusteredMediaDriver clusteredMediaDriver =
                     ClusteredMediaDriver.launch(mediaDriverContext, archiveContext, consensusModuleContext)) {
            barrier.await();
            System.out.println("[ClusteredServiceNode] Node shutting down.");
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to start ClusteredMediaDriver: " + e.getMessage());
        }
    }
}