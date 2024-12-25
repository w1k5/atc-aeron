package com.w1k5.atc.engine.application;

import io.aeron.CommonContext;
import io.aeron.ExclusivePublication;
import io.aeron.Image;
import io.aeron.archive.client.AeronArchive;
import io.aeron.cluster.codecs.CloseReason;
import io.aeron.cluster.service.ClientSession;
import io.aeron.cluster.service.Cluster;
import io.aeron.cluster.service.ClusteredService;
import io.aeron.cluster.service.ClusteredServiceContainer;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MyClusteredService implements ClusteredService, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(MyClusteredService.class);
    final ClusteredServiceContainer.Context clusteredServiceContext;

    public MyClusteredService(final AeronArchive.Context archiveContext) {
        clusteredServiceContext = new ClusteredServiceContainer.Context()
                        .aeronDirectoryName(CommonContext.getAeronDirectoryName())
                        .archiveContext(archiveContext)
                        .clusterDir(new File(CommonContext.getAeronDirectoryName() + "/cluster"))
                        .clusteredService(this)
                        .errorHandler(Throwable::printStackTrace);
    }

    @Override
    public void onStart(Cluster cluster, Image image) {
        log.info("Clustered service started.");
    }

    @Override
    public void onSessionOpen(ClientSession clientSession, long sessionId) {
        log.info("Session opened: {}", sessionId);
    }

    @Override
    public void onSessionClose(ClientSession clientSession, long sessionId, CloseReason closeReason) {
        log.info("Session closed: {}, Reason: {}", sessionId, closeReason);
    }

    @Override
    public void onSessionMessage(ClientSession clientSession, long sessionId, DirectBuffer directBuffer, int offset, int length, Header header) {
        byte[] message = new byte[length];
        directBuffer.getBytes(offset, message);
        log.info("Received message: {}", new String(message));
    }

    @Override
    public void onTimerEvent(long time, long epochTime) {
        log.info("Timer event at: {}", epochTime);
    }

    @Override
    public void onTakeSnapshot(ExclusivePublication exclusivePublication) {
        log.info("Taking snapshot...");
        // Example: Write current state to publication
    }

    @Override
    public void onRoleChange(Cluster.Role role) {
        log.info("Role changed to: {}", role);
    }

    @Override
    public void onTerminate(Cluster cluster) {
        close();
        log.info("Terminating clustered service.");
    }

    @Override
    public void close() {
        if (clusteredServiceContext != null) {
            clusteredServiceContext.close();
        }
    }
}
