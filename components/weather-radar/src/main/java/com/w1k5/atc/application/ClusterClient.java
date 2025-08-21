package com.w1k5.atc.application;

import io.aeron.cluster.client.AeronCluster;
import io.aeron.cluster.client.EgressListener;
import io.aeron.cluster.codecs.EventCode;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

import java.util.List;

public class ClusterClient implements EgressListener {

    public static int calculatePort(int nodeId, int offset) {
        return 9000 + nodeId * 100 + offset;
    }

    public static String ingressEndpoints(List<String> hostnames) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < hostnames.size(); ++i) {
            sb.append(i).append('=');
            sb.append((String)hostnames.get(i)).append(':').append(calculatePort(i, 2));
            sb.append(',');
        }

        sb.setLength(sb.length() - 1);
        return sb.toString();
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


    @Override
    public void onSessionEvent(long correlationId, long clusterSessionId, long leadershipTermId, int leaderMemberId,
                               EventCode code, String detail) {
        System.out.println("[ClusterClient] Session Event: correlationId=" + correlationId
                + ", clusterSessionId=" + clusterSessionId
                + ", leadershipTermId=" + leadershipTermId
                + ", leaderMemberId=" + leaderMemberId
                + ", code=" + code
                + ", detail=" + detail);
    }

    @Override
    public void onMessage(long clusterSessionId, long timestamp, DirectBuffer buffer, int offset, int length, Header header) {
        System.out.println("[ClusterClient] Received message: sessionId=" + clusterSessionId
                + ", timestamp=" + timestamp
                + ", messageLength=" + length);
    }

    @Override
    public void onNewLeader(long clusterSessionId, long leadershipTermId, int leaderMemberId, String ingressEndpoints) {
        System.out.println("[ClusterClient] New Leader elected: clusterSessionId=" + clusterSessionId
                + ", leadershipTermId=" + leadershipTermId
                + ", leaderMemberId=" + leaderMemberId
                + ", ingressEndpoints=" + ingressEndpoints);
    }

    public static void main(String[] args) {
        // Setup Aeron Media Driver
        System.out.println("[ClusterClient] Starting Media Driver...");
        MediaDriver mediaDriver = MediaDriver.launchEmbedded(new MediaDriver.Context()
                .threadingMode(ThreadingMode.SHARED)
                .dirDeleteOnStart(true)
                .dirDeleteOnShutdown(true));
        System.out.println("[ClusterClient] Media Driver started with directory: " + mediaDriver.aeronDirectoryName());

        try (AeronCluster cluster = AeronCluster.connect(
                new AeronCluster.Context()
                        .aeronDirectoryName(mediaDriver.aeronDirectoryName())
                        .egressChannel("aeron:udp?endpoint=localhost:0")
                        .ingressChannel("aeron:udp?endpoint=localhost:9002")
                        .egressListener(new ClusterClient()))) {
            System.out.println("[ClusterClient] Successfully connected to the cluster.");

            // Simulate some cluster interaction
            for (int i = 0; i < 5; i++) {
                System.out.println("[ClusterClient] Sent message: Test message " + i);
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            System.err.println("[ClusterClient] Error during cluster interaction: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("[ClusterClient] Shutting down Media Driver...");
            mediaDriver.close();
            System.out.println("[ClusterClient] Media Driver shut down.");
        }
    }
}