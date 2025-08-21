#!/bin/bash

# ATC Aeron Cluster Stop Script

if [ -f cluster.pid ]; then
    PID=$(cat cluster.pid)
    echo "Stopping ATC Aeron Cluster (PID: $PID)..."
    
    # Try graceful shutdown first
    kill $PID
    
    # Wait a bit for graceful shutdown
    sleep 2
    
    # Check if still running
    if kill -0 $PID 2>/dev/null; then
        echo "Cluster still running, forcing shutdown..."
        kill -9 $PID
    fi
    
    # Remove PID file
    rm -f cluster.pid
    echo "Cluster stopped."
else
    echo "No cluster PID file found. Cluster may not be running."
fi 