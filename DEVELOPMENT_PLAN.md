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
- **Create performance benchmarks** - P50/P99 latency targets

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
```

### Aeron Cluster Integration
- **Single deterministic thread** for all ATC processing
- **Event-driven architecture** with bounded work per cycle
- **Snapshot-based persistence** for fault tolerance
- **Session management** for multiple controller clients

### Performance Targets
- **Latency**: <100ms end-to-end for conflict resolution
- **Throughput**: 1000+ aircraft updates per second
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

### Timeline Risks
- **Scope creep**: Focus on core ATC engine first, add features incrementally
- **Integration complexity**: Build and test components independently first
- **Performance issues**: Set up monitoring early, identify bottlenecks quickly

## Resources & Dependencies

### Required Skills
- **Java 17+** - Modern Java features for performance
- **Aeron** - High-performance messaging framework
- **SBE** - Binary message encoding
- **ATC domain knowledge** - Aviation procedures and constraints

### External Dependencies
- **Aeron 1.40.0** - Already added
- **SBE** - To be added for message encoding
- **Agrona** - To be added for performance utilities
- **Weather data sources** - METAR/TAF feeds
- **ADS-B data** - Aircraft position feeds

This plan provides a realistic roadmap that builds on your existing code while creating a production-ready ATC system. Each phase delivers working functionality that can be tested and validated before moving to the next phase. 