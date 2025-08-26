# ATC Aeron Admin UI

A modern, real-time dashboard for monitoring and controlling the ATC Aeron air traffic control system.

## Features

### 🎯 **Real-time Monitoring**
- Live aircraft position tracking
- Conflict detection alerts
- System health monitoring
- Performance metrics dashboard

### 📊 **Data Visualization**
- Interactive charts and graphs
- Real-time performance trends
- System resource usage
- Message throughput analytics

### ⚙️ **System Control**
- Configuration management
- Performance testing tools
- Alert system configuration
- Service status monitoring

### 🔧 **Technical Features**
- Responsive Material-UI design
- WebSocket real-time updates
- REST API integration
- Dark theme optimized for operations

## Pages

### 1. **Dashboard** (`/`)
- System overview with key metrics
- Real-time performance charts
- Active alerts and notifications
- System health status

### 2. **Aircraft Monitor** (`/aircraft`)
- Interactive aircraft map view
- Real-time position tracking
- Conflict visualization
- Aircraft details and search

### 3. **System Health** (`/health`)
- Service status monitoring
- Resource usage tracking
- Performance trends
- System information

### 4. **Performance Metrics** (`/performance`)
- Detailed performance analysis
- Component health monitoring
- Throughput and latency metrics
- Historical data trends

### 5. **Configuration** (`/config`)
- System settings management
- Performance testing tools
- Alert configuration
- Test result analysis

## Getting Started

### Prerequisites
- Node.js 16+ 
- npm or yarn
- ATC Aeron backend running on `localhost:8080`

### Installation

1. **Install dependencies:**
   ```bash
   cd ui/admin-ui
   npm install
   ```

2. **Start the development server:**
   ```bash
   npm start
   ```

3. **Open your browser:**
   Navigate to `http://localhost:3000`

### Building for Production

```bash
npm run build
```

The build artifacts will be stored in the `build/` directory.

## Technology Stack

- **Frontend Framework:** React 18
- **UI Library:** Material-UI (MUI) v5
- **Charts:** Recharts
- **Routing:** React Router v6
- **HTTP Client:** Axios
- **Real-time:** WebSocket (STOMP)
- **Styling:** Emotion (CSS-in-JS)
- **Icons:** Material Icons

## Architecture

### Component Structure
```
src/
├── components/          # Reusable UI components
│   ├── Layout.js       # Main navigation and layout
│   ├── MetricCard.js   # Metric display cards
│   ├── StatusCard.js   # Status and health cards
│   ├── AlertPanel.js   # Alert notifications
│   ├── AircraftMap.js  # Aircraft position map
│   └── ConflictTable.js # Conflict display table
├── pages/              # Main application pages
│   ├── Dashboard.js    # Main overview page
│   ├── AircraftMonitor.js # Aircraft tracking
│   ├── SystemHealth.js # System monitoring
│   ├── PerformanceMetrics.js # Performance analysis
│   └── Configuration.js # System configuration
├── App.js              # Main application component
└── index.js            # Application entry point
```

### Data Flow
1. **Real-time Updates:** WebSocket connections for live data
2. **REST API:** Configuration and control operations
3. **State Management:** React hooks for local state
4. **Component Updates:** Automatic re-rendering on data changes

## API Integration

### WebSocket Topics
- `/topic/aircraft/positions` - Aircraft position updates
- `/topic/admin/stats` - System statistics
- `/topic/admin/conflicts` - Conflict detection alerts
- `/topic/test` - Test message streaming

### REST Endpoints
- `GET /api/admin/health` - System health status
- `GET /api/admin/metrics/streaming` - Performance metrics
- `POST /api/admin/test/stream` - Performance testing
- `GET /api/admin/system/info` - System information

## Configuration

### Environment Variables
- `REACT_APP_API_URL` - Backend API URL (default: `http://localhost:8080`)
- `REACT_APP_WS_URL` - WebSocket URL (default: `ws://localhost:8080/ws/admin`)

### Performance Settings
- Message rate limits
- Batch processing sizes
- Timeout configurations
- Retry policies

## Development

### Code Style
- ESLint configuration included
- Prettier formatting
- Component-based architecture
- Consistent naming conventions

### Testing
```bash
npm test          # Run test suite
npm run test:watch # Watch mode for development
```

### Adding New Features
1. Create new components in `src/components/`
2. Add new pages in `src/pages/`
3. Update routing in `App.js`
4. Add navigation items in `Layout.js`

## Deployment

### Docker (Recommended)
```bash
docker build -t atc-admin-ui .
docker run -p 3000:3000 atc-admin-ui
```

### Static Hosting
```bash
npm run build
# Deploy build/ directory to your hosting service
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is part of the ATC Aeron system. See the main project license for details.

## Support

For issues and questions:
- Check the main project documentation
- Review existing issues
- Create a new issue with detailed information

---

**Built with ❤️ for air traffic control operations**




