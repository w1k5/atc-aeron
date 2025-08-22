# ATC Aeron Development Plan

## Current State Analysis

The project currently has a basic Aeron cluster infrastructure with:
- **Core ATC component** (`components/core-atc/`) with basic domain models
- **Weather Radar component** (`components/weather-radar/`) 
- Basic Aeron cluster setup with `ClusteredServiceNode` and `MyClusteredService`
- Simple domain models: `AircraftState`, `Sector`, `SectorBalancer`

## Realistic Development Roadmap

### Phase 1: Foundation & Core Infrastructure (Weeks 1-2)

#### 1.1 Complete Aeron Cluster Setup
- [ ] **Fix `ClusteredServiceNode`** - Remove tutorial imports, integrate with `MyClusteredService`
- [ ] **Implement proper cluster lifecycle** - Start/stop, role changes, session management
- [ ] **Add cluster configuration** - Environment-based config, proper port management
- [ ] **Create cluster startup scripts** - Single node and multi-node deployment

#### 1.2 Message Codecs & Wire Protocol
- [ ] **Add SBE dependency** to `core-atc/build.gradle`
- [ ] **Create SBE schemas** for ingress/egress messages
- [ ] **Generate message codecs** using SBE
- [ ] **Implement message adapters** between SBE and domain objects

#### 1.3 Basic Domain Model Completion
- [ ] **Enhance `AircraftState`** - Add proper 4D state, covariance, timestamps
- [ ] **Create `Flight` entity** - Composite of state + intent + performance
- [ ] **Add `FlightIntent`** - Route segments, SID/STAR, constraints
- [ ] **Implement `Sector` management** - Boundaries, capacity, workload

### Phase 2: Core ATC Engine (Weeks 3-4)

#### 2.1 State Management & Persistence
- [ ] **Create `EngineState`** - Central state container for all flights/sectors
- [ ] **Implement snapshotting** - `SnapshotWriter`/`SnapshotReader` for persistence
- [ ] **Add deterministic event loop** - `DeterministicDispatcher` for message processing
- [ ] **Integrate with `MyClusteredService`** - Replace basic logging with real processing

#### 2.2 Basic Conflict Detection
- [ ] **Implement `ConflictProbe`** - Simple 3NM/1000ft separation checking
- [ ] **Add trajectory prediction** - Basic straight-line + wind modeling
- [ ] **Create conflict resolution** - Simple speed/heading/altitude adjustments
- [ ] **Add safety buffer policy** - Dynamic separation minima based on conditions

#### 2.3 Advisory Generation
- [ ] **Create `AdvisoryFormatter`** - Convert engine decisions to ATC clearances
- [ ] **Implement `EgressPublisher`** - Send advisories via cluster egress
- [ ] **Add advisory types** - Speed, heading, altitude, runway assignments

### Phase 2.5: Web Admin UI & Real-time Monitoring (Weeks 4-5)

#### 2.5.1 Web Infrastructure Setup
- [ ] **Create `web-admin` component** - New Gradle module for web interface
- [ ] **Add Spring Boot Web** - REST API endpoints and WebSocket support
- [ ] **Setup WebSocket streaming** - Real-time aircraft position updates
- [ ] **Add CORS configuration** - Allow browser access from different origins

#### 2.5.2 Real-time Aircraft Visualization
- [ ] **Implement WebSocket endpoints** - `/ws/aircraft-positions` for live updates
- [ ] **Create aircraft position streaming** - Real-time lat/lon/altitude updates
- [ ] **Add trajectory prediction display** - Show predicted flight paths
- [ ] **Implement sector boundary visualization** - Display airspace sectors

#### 2.5.3 Message Flow Monitoring
- [ ] **Create message monitoring endpoints** - `/api/messages/ingress`, `/api/messages/egress`
- [ ] **Add message statistics dashboard** - Message counts, latency, throughput
- [ ] **Implement conflict visualization** - Show detected conflicts on map
- [ ] **Add advisory display** - Show generated advisories in real-time

#### 2.5.4 Interactive Admin Controls
- [ ] **Create aircraft injection interface** - Add synthetic aircraft for testing
- [ ] **Add conflict resolution controls** - Manual override of automated decisions
- [ ] **Implement replay controls** - Play/pause/fast-forward through captured data
- [ ] **Add system health monitoring** - Cluster status, performance metrics

### Phase 3: External Integration (Weeks 5-6)

#### 3.1 Data Ingestion
- [ ] **Create `IngressPublisher`** - Client-side cluster ingress wrapper
- [ ] **Implement `AdsBAdapter`** - Parse ADS-B feeds to `TrackUpdate` messages
- [ ] **Add `WeatherAdapter`** - METAR/TAF parsing for weather constraints
- [ ] **Create `AirportOpsAdapter`** - Runway configuration and NOTAM handling

#### 3.2 Client Applications
- [ ] **Build `OpsConsoleClient`** - Controller interface for advisories
- [ ] **Add `ShadowModeRecorder`** - Capture live vs. automated decisions
- [ ] **Create `ReplayTool`** - Replay captured data for testing/analysis

#### 3.3 Weather Integration
- [ ] **Enhance weather-radar component** - Real-time weather data processing
- [ ] **Add weather routing** - Avoid convective/icing/turbulence areas
- [ ] **Implement weather constraints** - Wind, visibility, runway conditions

### Phase 4: Advanced Features (Weeks 7-8)

#### 4.1 Optimization & Efficiency
- [ ] **Add `MpcOptimizer`** - Model predictive control for efficiency
- [ ] **Implement sector balancing** - Dynamic workload distribution
- [ ] **Add flow control** - GDP/AFP capacity management
- [ ] **Create dependency tracking** - Impact analysis for changes

#### 4.2 Performance & Monitoring
- [ ] **Add `HealthMonitor`** - Engine performance metrics
- [ ] **Implement latency profiling** - End-to-end timing analysis
- [ ] **Add load testing** - Synthetic traffic generation
- [ ] **Create performance benchmarks** - P50/P99 latency targets

#### 4.3 Testing & Validation
- [ ] **Add comprehensive unit tests** - All engine components
- [ ] **Create integration tests** - End-to-end message flow
- [ ] **Add performance tests** - Latency and throughput validation
- [ ] **Implement regression testing** - Automated replay validation

## Immediate Next Steps (This Week)

### 1. Fix Current Issues
- [ ] **Remove tutorial imports** from `ClusteredServiceNode`
- [ ] **Integrate `MyClusteredService`** with cluster startup
- [ ] **Add proper error handling** and logging

### 2. Add Missing Dependencies
- [ ] **Add SBE dependency** to `core-atc/build.gradle`
- [ ] **Add Agrona collections** for performance
- [ ] **Add testing dependencies** (JUnit, Mockito)

### 3. Create Basic Message Flow
- [ ] **Design simple SBE schema** for track updates
- [ ] **Implement basic message handling** in `MyClusteredService`
- [ ] **Add simple advisory generation** (speed/heading changes)

### 4. Plan Web UI Architecture
- [ ] **Design WebSocket message format** for aircraft positions
- [ ] **Plan REST API structure** for monitoring endpoints
- [ ] **Choose frontend technology** (React, Vue, or vanilla JS)
- [ ] **Design real-time visualization** approach

## Technical Architecture Notes

### Current Package Structure (to be enhanced)
```
com.w1k5.atc.engine
├─ application/          // Aeron cluster service + lifecycle
├─ domain/              // Core ATC entities (AircraftState, Sector, etc.)
├─ messaging/            // SBE codecs + message handling (NEW)
├─ engine/              // Core ATC algorithms (NEW)
├─ persistence/          // Snapshotting + state management (NEW)
└─ client/              // External client interfaces (NEW)

components/
├─ core-atc/            // Core ATC engine
├─ weather-radar/        // Weather processing
└─ web-admin/           // Web interface (NEW)
```

### Web Admin UI Architecture
- **Spring Boot Web** - REST API and WebSocket server
- **WebSocket streaming** - Real-time aircraft position updates
- **Interactive map visualization** - Aircraft positions, trajectories, sectors
- **Message monitoring** - Ingress/egress message flow display
- **Admin controls** - System management and testing interfaces

### Aeron Cluster Integration
- **Single deterministic thread** for all ATC processing
- **Event-driven architecture** with bounded work per cycle
- **Snapshot-based persistence** for fault tolerance
- **Session management** for multiple controller clients
- **Real-time streaming** to web clients via WebSocket

### Performance Targets
- **Latency**: <100ms end-to-end for conflict resolution
- **Throughput**: 1000+ aircraft updates per second
- **Web UI updates**: <50ms for position updates
- **Determinism**: Same inputs always produce same outputs
- **Fault tolerance**: Automatic failover with state recovery

## Success Metrics

### Week 2
- [ ] Aeron cluster starts successfully
- [ ] Basic message flow works (ingress → processing → egress)
- [ ] Simple conflict detection operational

### Week 4
- [ ] Full ATC engine operational
- [ ] Advisory generation working
- [ ] Basic client interface functional

### Week 5
- [ ] Web admin UI accessible via browser
- [ ] Real-time aircraft visualization working
- [ ] Message flow monitoring operational

### Week 6
- [ ] External data ingestion working
- [ ] Weather integration operational
- [ ] Performance targets met

### Week 8
- [ ] Advanced optimization features working
- [ ] Comprehensive testing complete
- [ ] Production-ready deployment

## Risk Mitigation

### Technical Risks
- **Aeron complexity**: Start with single-node, add clustering incrementally
- **Performance tuning**: Profile early, optimize critical paths first
- **State management**: Use simple snapshots initially, enhance later
- **Web UI performance**: Use efficient WebSocket streaming, avoid polling

### Timeline Risks
- **Scope creep**: Focus on core ATC engine first, add features incrementally
- **Integration complexity**: Build and test components independently first
- **Performance issues**: Set up monitoring early, identify bottlenecks quickly
- **UI complexity**: Start with basic visualization, enhance incrementally

## Resources & Dependencies

### Required Skills
- **Java 17+** - Modern Java features for performance
- **Aeron** - High-performance messaging framework
- **SBE** - Binary message encoding
- **Spring Boot** - Web framework for admin UI
- **WebSocket** - Real-time communication
- **Frontend development** - HTML/CSS/JavaScript for visualization
- **ATC domain knowledge** - Aviation procedures and constraints

### External Dependencies
- **Aeron 1.40.0** - Already added
- **SBE** - To be added for message encoding
- **Agrona** - To be added for performance utilities
- **Spring Boot Web** - For web admin interface
- **Weather data sources** - METAR/TAF feeds
- **ADS-B data** - Aircraft position feeds

### Web UI Technology Stack
- **Backend**: Spring Boot with WebSocket support
- **Frontend**: Modern JavaScript (ES6+) with WebSocket API
- **Visualization**: Canvas API or SVG for aircraft movement
- **Styling**: CSS Grid/Flexbox for responsive layout
- **Build**: Gradle for backend, npm/yarn for frontend assets

This plan provides a realistic roadmap that builds on your existing code while creating a production-ready ATC system with a comprehensive web admin interface. Each phase delivers working functionality that can be tested and validated before moving to the next phase. 