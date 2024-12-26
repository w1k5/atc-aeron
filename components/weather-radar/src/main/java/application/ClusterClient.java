package application;

import io.aeron.cluster.client.AeronCluster;
import io.aeron.cluster.client.EgressListener;
import io.aeron.cluster.codecs.EventCode;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import io.aeron.logbuffer.Header;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import io.aeron.samples.cluster.tutorial.BasicAuctionClusterClient;
import org.agrona.DirectBuffer;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.BackoffIdleStrategy;
import org.agrona.concurrent.IdleStrategy;

public class ClusterClient implements EgressListener {
    private final MutableDirectBuffer actionBidBuffer = new ExpandableArrayBuffer();
    private final IdleStrategy idleStrategy = new BackoffIdleStrategy();
    private final int numOfMessages;
    private final int intervalMs;
    private long correlationId = ThreadLocalRandom.current().nextLong();

    public ClusterClient(int numOfMessages, int intervalMs) {
        this.numOfMessages = numOfMessages;
        this.intervalMs = intervalMs;
    }

    public void onMessage(long clusterSessionId, long timestamp, DirectBuffer buffer,
                          int offset, int length, Header header) {
        long correlationId = buffer.getLong(offset);
        long customerId = buffer.getLong(offset + 8);
        long currentPrice = buffer.getLong(offset + 16);
        boolean bidSucceed = 0 != buffer.getByte(offset + 24);
        this.printOutput("SessionMessage(" + clusterSessionId + ", " + correlationId + ","
                + customerId + ", " + currentPrice + ", " + bidSucceed + ")");
    }

    public void onSessionEvent(long correlationId, long clusterSessionId, long leadershipTermId,
                               int leaderMemberId, EventCode code, String detail) {
        this.printOutput("SessionEvent(" + correlationId + ", " + leadershipTermId + ", " +
                leaderMemberId + ", " + code + ", " + detail + ")");
    }

    public void onNewLeader(long clusterSessionId, long leadershipTermId,
                            int leaderMemberId, String ingressEndpoints) {
        this.printOutput("NewLeader(" + clusterSessionId + ", " + leadershipTermId + ", " + leaderMemberId + ")");
    }

    protected void sendRadarMessages(AeronCluster aeronCluster) {
        long keepAliveDeadlineMs = 0L;
        long nextMessageDeadlineMs = System.currentTimeMillis() + (long) ThreadLocalRandom.current().nextInt(1000);

        for (int messagesLeftToSend = this.numOfMessages; !Thread.currentThread().isInterrupted();
             this.idleStrategy.idle(aeronCluster.pollEgress())) {
            long currentTimeMs = System.currentTimeMillis();

            if (nextMessageDeadlineMs <= currentTimeMs && messagesLeftToSend > 0) {
                // Generate radar message data
                long timestamp = System.currentTimeMillis();
                double latitude = ThreadLocalRandom.current().nextDouble(-90.0, 90.0);
                double longitude = ThreadLocalRandom.current().nextDouble(-180.0, 180.0);
                double altitude = ThreadLocalRandom.current().nextDouble(0, 40000); // Altitude in feet
                double speed = ThreadLocalRandom.current().nextDouble(0, 1200); // Speed in knots

                // Pack the data into the buffer
                long correlationId = sendRadarMessage(aeronCluster, timestamp, latitude, longitude, altitude, speed);
                nextMessageDeadlineMs = currentTimeMs + (long) ThreadLocalRandom.current().nextInt(this.intervalMs);
                keepAliveDeadlineMs = currentTimeMs + 1000L;
                --messagesLeftToSend;

                this.printOutput("Sent Radar Message(" + correlationId + ", timestamp=" + timestamp + ", lat=" + latitude + ", lon=" + longitude + ", alt=" + altitude + ", speed=" + speed + ") messagesRemaining=" + messagesLeftToSend);
            } else if (keepAliveDeadlineMs <= currentTimeMs) {
                if (messagesLeftToSend <= 0) {
                    break;
                }

                aeronCluster.sendKeepAlive();
                keepAliveDeadlineMs = currentTimeMs + 1000L;
            }
        }
    }

    private long sendRadarMessage(AeronCluster aeronCluster, long timestamp, double latitude, double longitude,
                                  double altitude, double speed) {
        long correlationId = this.correlationId++;

        // Pack radar message into the buffer
        this.actionBidBuffer.putLong(0, correlationId);
        this.actionBidBuffer.putLong(8, timestamp);
        this.actionBidBuffer.putDouble(16, latitude);
        this.actionBidBuffer.putDouble(24, longitude);
        this.actionBidBuffer.putDouble(32, altitude);
        this.actionBidBuffer.putDouble(40, speed);

        this.idleStrategy.reset();

        while (aeronCluster.offer(this.actionBidBuffer, 0, 48) < 0L) {
            this.idleStrategy.idle(aeronCluster.pollEgress());
        }

        return correlationId;
    }

    public static String ingressEndpoints(List<String> hostnames) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < hostnames.size(); ++i) {
            sb.append(i).append('=');
            sb.append(hostnames.get(i)).append(':').append(calculatePort(i, 2));
            sb.append(',');
        }

        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static int calculatePort(int nodeId, int offset) {
        return 9000 + nodeId * 100 + offset;
    }

    private void printOutput(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
        int numOfBids = Integer.parseInt(System.getProperty("aeron.cluster.tutorial.numOfMessages", "10"));
        int bidIntervalMs = Integer.parseInt(System.getProperty("aeron.cluster.tutorial.intervalMs", "100"));
        String[] hostnames = System.getProperty("aeron.cluster.tutorial.hostnames",
                "localhost,localhost,localhost").split(",");
        String ingressEndpoints = ingressEndpoints(Arrays.asList(hostnames));
        ClusterClient client = new ClusterClient(numOfBids, bidIntervalMs);
        MediaDriver mediaDriver = MediaDriver.launchEmbedded((new MediaDriver.Context())
                .threadingMode(ThreadingMode.SHARED)
                .dirDeleteOnStart(true)
                .dirDeleteOnShutdown(true));
        Throwable var8 = null;

        try {
            AeronCluster aeronCluster = AeronCluster.connect(
                    (new AeronCluster.Context())
                            .egressListener(client)
                            .ingressChannel("aeron:udp?endpoint=localhost:9000")
                            .egressChannel("aeron:udp?endpoint=localhost:9000")
                            .aeronDirectoryName(mediaDriver.aeronDirectoryName())
                            .ingressEndpoints(ingressEndpoints));
            Throwable var10 = null;

            try {
                client.sendRadarMessages(aeronCluster);
            } catch (Throwable var33) {
                var10 = var33;
                throw var33;
            } finally {
                if (aeronCluster != null) {
                    if (var10 != null) {
                        try {
                            aeronCluster.close();
                        } catch (Throwable var32) {
                            var10.addSuppressed(var32);
                        }
                    } else {
                        aeronCluster.close();
                    }
                }

            }
        } catch (Throwable var35) {
            var8 = var35;
            throw var35;
        } finally {
            if (mediaDriver != null) {
                if (var8 != null) {
                    try {
                        mediaDriver.close();
                    } catch (Throwable var31) {
                        var8.addSuppressed(var31);
                    }
                } else {
                    mediaDriver.close();
                }
            }

        }

    }
}