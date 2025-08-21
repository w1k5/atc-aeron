#!/bin/bash

# ATC Aeron Cluster Startup Script
# This script starts a single-node Aeron cluster for development

echo "Starting ATC Aeron Cluster..."

# Set system properties for cluster configuration
export JAVA_OPTS="-Daeron.cluster.tutorial.nodeId=0 -Daeron.cluster.tutorial.hostnames=localhost"

# Create logs directory if it doesn't exist
mkdir -p logs

# Start the cluster
echo "Launching cluster node 0..."
java $JAVA_OPTS -cp "components/core-atc/build/classes/java/main:components/core-atc/build/resources/main" \
     com.w1k5.atc.engine.application.ClusteredServiceNode > logs/cluster.log 2>&1 &

# Save the PID
echo $! > cluster.pid

echo "Cluster started with PID $(cat cluster.pid)"
echo "Logs are being written to logs/cluster.log"
echo "To stop the cluster, run: kill \$(cat cluster.pid)"
echo ""
echo "Cluster endpoints:"
echo "  Ingress: localhost:8000"
echo "  Egress:  localhost:8001"
echo ""
echo "Press Ctrl+C to stop monitoring (cluster will continue running)"
echo ""

# Monitor the logs
tail -f logs/cluster.log 