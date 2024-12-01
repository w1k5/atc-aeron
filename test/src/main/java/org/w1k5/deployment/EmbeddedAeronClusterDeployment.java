package org.w1k5.deployment;

import com.w1k5.atc.engine.application.AeronClusterManager;
import com.w1k5.atc.engine.application.ClusteredServiceAgent;
import com.w1k5.atc.engine.application.ConfigManager;
import com.w1k5.atc.engine.application.MyClusteredService;
import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class EmbeddedAeronClusterDeployment implements AutoCloseable {

    private static final String DEFAULT_AERON_DIR = System.getProperty("user.home") + "/.aeron";
    private MediaDriver mediaDriver;
    private Aeron aeron;

    // Add references to your application classes
    private AeronClusterManager aeronClusterManager;
    private ClusteredServiceAgent clusteredServiceAgent;
    private ConfigManager configManager;

    public EmbeddedAeronClusterDeployment() {
        // Initialize the Aeron driver and the Aeron connection
        initializeAeronDirectory();
        setupAeronDriver();

        // Initialize your application components
        initializeApplicationComponents();
    }

    private void initializeAeronDirectory() {
        // Set the default Aeron directory if not provided by the user
        String aeronDir = System.getProperty("aeron.dir", DEFAULT_AERON_DIR);

        // Create the directory if it doesn't exist
        File dir = new File(aeronDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Failed to create Aeron directory");
            }
        }

        // Log the directory being used
        System.setProperty("aeron.dir", aeronDir);
    }

    private void setupAeronDriver() {
        // Launch the embedded Aeron MediaDriver
        try {
            MediaDriver.Context context = new MediaDriver.Context()
                    .aeronDirectoryName(System.getProperty("aeron.dir"));

            mediaDriver = MediaDriver.launchEmbedded(context);

            // Connect to Aeron
            aeron = Aeron.connect(new Aeron.Context());

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Aeron driver and connection", e);
        }
    }

    private void initializeApplicationComponents() {
        try {
            // Initialize components from the other module
            configManager = new ConfigManager();
            aeronClusterManager = new AeronClusterManager(aeron, configManager);

            // Optionally initialize the service (e.g., MyClusteredService)
            MyClusteredService myClusteredService = new MyClusteredService();
            clusteredServiceAgent = new ClusteredServiceAgent(myClusteredService, aeronClusterManager);

            // Make sure everything is set up
            configManager.loadConfig("application.properties");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize application components", e);
        }
    }

    public Aeron getAeron() {
        return aeron;
    }

    @Override
    public void close() {
        // Cleanup resources
        if (aeron != null) {
            aeron.close();
        }

        if (mediaDriver != null) {
            mediaDriver.close();
        }

        // Clean up the Aeron directory after use
        cleanUpAeronDirectory();
    }

    private void cleanUpAeronDirectory() {
        String aeronDir = System.getProperty("aeron.dir", DEFAULT_AERON_DIR);
        try {
            Files.walk(Paths.get(aeronDir))
                    .map(Path::toFile)
                    .forEach(File::delete);
            System.out.println("Aeron directory cleaned up: " + aeronDir);
        } catch (IOException e) {
            System.err.println("Failed to clean up Aeron directory: " + e.getMessage());
        }
    }

    // Utility method to simulate a test cycle (could be replaced by your actual logic)
    public void runTestCycle() {
        // Placeholder for running your application's logic
        try {
            // Simulate Aeron cluster management
            aeronClusterManager.startCluster();
            clusteredServiceAgent.startService();
            TimeUnit.SECONDS.sleep(2);  // Simulate some processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Error during the test cycle: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Use try-with-resources to ensure cleanup
        try (EmbeddedAeronClusterDeployment manager = new EmbeddedAeronClusterDeployment()) {
            // Run your application/test
            manager.runTestCycle();
            // Access Aeron instance if needed
            Aeron aeron = manager.getAeron();
            // Your test or application logic goes here
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}