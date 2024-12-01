package com.w1k5.atc.engine.application;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;

public class AeronClusterManager implements AutoCloseable {
    private final Aeron aeron;
    private final ConfigManager configManager;

    private final Publication weatherPublication;
    private final Subscription weatherSubscription;
    private final Publication radarPublication;
    private final Subscription radarSubscription;
    private final Publication adsbPublication;
    private final Subscription adsbSubscription;

    public AeronClusterManager(Aeron aeron, ConfigManager configManager) {
        this.aeron = aeron;
        this.configManager = configManager;

        this.weatherPublication = createPublication(AdapterType.WEATHER);
        this.weatherSubscription = createSubscription(AdapterType.WEATHER);
        this.radarPublication = createPublication(AdapterType.RADAR);
        this.radarSubscription = createSubscription(AdapterType.RADAR);
        this.adsbPublication = createPublication(AdapterType.ADSB);
        this.adsbSubscription = createSubscription(AdapterType.ADSB);
    }

    private Publication createPublication(AdapterType adapterType) {
        String endpoint = configManager.getProperty(adapterType.getConfigPrefix() + ".publication.endpoint");
        int streamId = configManager.getIntProperty(adapterType.getConfigPrefix() + ".streamId");
        return aeron.addPublication(endpoint, streamId);
    }

    private Subscription createSubscription(AdapterType adapterType) {
        String endpoint = configManager.getProperty(adapterType.getConfigPrefix() + ".subscription.endpoint");
        int streamId = configManager.getIntProperty(adapterType.getConfigPrefix() + ".streamId");
        return aeron.addSubscription(endpoint, streamId);
    }

    public void receiveMessages(AdapterType adapterType) {
        Subscription subscription = getSubscriptionForAdapter(adapterType);
        while (true) {
            subscription.poll((buffer, offset, length, header) -> {
                byte[] bytes = new byte[length];
                buffer.wrap(bytes, offset, length);
                String message = new String(bytes);
                System.out.println("Received message: " + message);
            }, 1);
        }
    }

    private Subscription getSubscriptionForAdapter(AdapterType adapterType) {
        return switch (adapterType) {
            case WEATHER -> weatherSubscription;
            case RADAR -> radarSubscription;
            case ADSB -> adsbSubscription;
            default -> throw new IllegalArgumentException("Unknown adapter: " + adapterType);
        };
    }

    @Override
    public void close() {
        if (weatherPublication != null) weatherPublication.close();
        if (weatherSubscription != null) weatherSubscription.close();
        if (radarPublication != null) radarPublication.close();
        if (radarSubscription != null) radarSubscription.close();
        if (adsbPublication != null) adsbPublication.close();
        if (adsbSubscription != null) adsbSubscription.close();
        if (aeron != null) aeron.close();
    }
}