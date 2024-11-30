package com.w1k5.atc.engine;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;

public class AeronClusterManager implements AutoCloseable {
    final private Aeron aeron;
    final private Publication publication;
    final private Subscription subscription;

    public AeronClusterManager() {
        try {
            Aeron.Context context = new Aeron.Context();
            aeron = Aeron.connect(context);
            publication = aeron.addPublication("aeron:udp?endpoint=localhost:40123", 10);
            subscription = aeron.addSubscription("aeron:udp?endpoint=localhost:40124", 10);
        } catch (Exception e) {
            // Log error or rethrow exception
            throw new RuntimeException("Failed to initialize AeronClusterManager", e);
        }
    }

    @Override
    public void close() {
        if (publication != null) {
            publication.close();
        }
        if (subscription != null) {
            subscription.close();
        }
        if (aeron != null) {
            aeron.close();
        }
    }
}