#!/bin/bash

echo "🚀 Starting ATC Aeron Admin UI..."
echo "📁 Working directory: $(pwd)"

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 16+ first."
    exit 1
fi

# Check Node.js version
NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
if [ "$NODE_VERSION" -lt 16 ]; then
    echo "❌ Node.js version 16+ is required. Current version: $(node -v)"
    exit 1
fi

echo "✅ Node.js version: $(node -v)"

# Check if dependencies are installed
if [ ! -d "node_modules" ]; then
    echo "📦 Installing dependencies..."
    npm install
    if [ $? -ne 0 ]; then
        echo "❌ Failed to install dependencies"
        exit 1
    fi
    echo "✅ Dependencies installed successfully"
else
    echo "✅ Dependencies already installed"
fi

# Check if backend is running
echo "🔍 Checking backend connection..."
if curl -s http://localhost:8080/api/admin/health > /dev/null; then
    echo "✅ Backend is running on localhost:8080"
else
    echo "⚠️  Warning: Backend not responding on localhost:8080"
    echo "   Make sure the ATC Aeron backend is running before using the UI"
fi

echo ""
echo "🌐 Starting development server..."
echo "   The UI will be available at: http://localhost:3000"
echo "   Press Ctrl+C to stop the server"
echo ""

# Start the development server
npm start
