package com.w1k5.atc.engine.application;

import io.aeron.ExclusivePublication;
import io.aeron.Image;
import io.aeron.cluster.codecs.CloseReason;
import io.aeron.cluster.service.ClientSession;
import io.aeron.cluster.service.Cluster;
import io.aeron.cluster.service.ClusteredService;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class MyClusteredService implements ClusteredService, AutoCloseable {

    @Override
    public void onStart(Cluster cluster, Image image) {
        System.out.println("Clustered service started.");
    }

    @Override
    public void onSessionOpen(ClientSession clientSession, long sessionId) {
        System.out.println("Session opened: " + sessionId);
    }

    @Override
    public void onSessionClose(ClientSession clientSession, long sessionId, CloseReason closeReason) {
        System.out.println("Session closed: " + sessionId + ", Reason: " + closeReason);
    }

    @Override
    public void onSessionMessage(ClientSession clientSession, long sessionId, DirectBuffer directBuffer, int offset, int length, Header header) {
        byte[] message = new byte[length];
        directBuffer.getBytes(offset, message);
        System.out.println("Received message: " + new String(message));
    }

    @Override
    public void onTimerEvent(long time, long epochTime) {
        System.out.println("Timer event at: " + epochTime);
    }

    @Override
    public void onTakeSnapshot(ExclusivePublication exclusivePublication) {
        System.out.println("Taking snapshot...");
        // Example: Write current state to publication
    }

    @Override
    public void onRoleChange(Cluster.Role role) {
        System.out.println("Role changed to: " + role);
    }

    @Override
    public void onTerminate(Cluster cluster) {
        close();
        System.out.println("Terminating clustered service.");
    }

    @Override
    public void close() {
        // TODO: CLOSE EVERYTHING!
    }

    public void onClusterTick(Object o) {
        // TODO: IMPLEMENT LOGIC!
    }
}
