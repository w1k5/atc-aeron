# Immediate TODO - This Week

## Priority 1: Fix Current Issues

### Fix MyClusteredService
- [ ] Implement real message processing instead of just logging

## Priority 2: Add Missing Dependencies

### Update weather-radar/build.gradle
- [ ] Add SBE dependency for message compatibility
- [ ] Add Agrona utilities for performance

### Create web-admin component
- [ ] Create `components/admin-gateway/` directory structure
- [ ] Add `components/admin-gateway/build.gradle` with Spring Boot dependencies
- [ ] Add Spring Boot Web, WebSocket, and Aeron client dependencies
- [ ] Create `ui/admin/` directory for React frontend
- [ ] Add `ui/admin/package.json` with React, TypeScript, and Vite dependencies

## Priority 3: Create Basic Message Flow

### Design Simple SBE Schema
- [ ] Create `src/main/resources/ingress-schema.xml` with basic TrackUpdate message
- [ ] Create `src/main/resources/egress-schema.xml` with basic Advisory message
- [ ] Generate SBE codecs using Gradle task

### Implement Basic Message Handling
- [ ] Create `TrackUpdate` domain object
- [ ] Create `Advisory` domain object
- [ ] Implement SBE codec adapters
- [ ] Add message routing in `MyClusteredService`

### Add Simple Advisory Generation
- [ ] Create basic conflict detection (3NM horizontal, 1000ft vertical)
- [ ] Generate simple speed/heading advisories
- [ ] Send advisories via cluster egress

## Priority 4: Plan Web Admin UI

### Design WebSocket Architecture
- [ ] Design WebSocket message format for aircraft positions
- [ ] Plan REST API structure for monitoring endpoints
- [ ] Create React hooks for WebSocket management
- [ ] Design React component architecture for real-time updates

### Create Basic Web Infrastructure
- [ ] Create `WebSocketController` for real-time updates
- [ ] Create `MessageMonitoringController` for REST endpoints
- [ ] Setup React development environment with Vite
- [ ] Add CORS configuration for browser access

## Priority 5: Basic Testing

### Create Test Infrastructure
- [ ] Add test for `AircraftState` distance calculation
- [ ] Add test for basic conflict detection
- [ ] Add test for message encoding/decoding
- [ ] Add integration test for cluster startup
- [ ] Add test for WebSocket message handling

## Files to Create/Modify

### New Files
- `components/core-atc/src/main/resources/ingress-schema.xml`
- `components/core-atc/src/main/resources/egress-schema.xml`
- `components/core-atc/src/main/java/com/w1k5/atc/engine/messaging/`
- `components/core-atc/src/main/java/com/w1k5/atc/engine/engine/`
- `components/core-atc/src/test/java/com/w1k5/atc/engine/engine/ConflictProbeTest.java`
- `components/admin-gateway/build.gradle`
- `components/admin-gateway/src/main/java/com/w1k5/atc/admin/AdminGatewayApplication.java`
- `components/admin-gateway/src/main/java/com/w1k5/atc/admin/controller/AdminController.java`
- `components/admin-gateway/src/main/java/com/w1k5/atc/admin/service/MessageMonitoringService.java`
- `components/admin-gateway/src/main/java/com/w1k5/atc/admin/service/AeronClientService.java`
- `ui/admin/package.json`
- `ui/admin/src/App.tsx`
- `ui/admin/src/components/AircraftMap.tsx`
- `ui/admin/src/components/MessageDashboard.tsx`
- `ui/admin/src/components/AdminControls.tsx`
- `ui/admin/src/hooks/useWebSocket.ts`
- `ui/admin/src/types/index.ts`
- `ui/admin/vite.config.ts`
- `ui/admin/tsconfig.json`

### Files to Modify
- `components/core-atc/build.gradle` - Add dependencies
- `components/core-atc/src/main/java/com/w1k5/atc/engine/application/ClusteredServiceNode.java` - Fix imports
- `components/core-atc/src/main/java/com/w1k5/atc/engine/application/MyClusteredService.java` - Fix method signatures
- `components/core-atc/src/main/java/com/w1k5/atc/engine/domain/AircraftState.java` - Enhance with timestamps
- `settings.gradle` - Add admin-gateway module

## Success Criteria for This Week

- [ ] Aeron cluster starts without errors
- [ ] Basic message flow works (ingress → processing → egress)
- [ ] Simple conflict detection operational
- [ ] At least one test passing
- [ ] Can generate and send basic advisories
- [ ] Admin gateway component created with basic structure
- [ ] UI admin directory created with frontend setup
- [ ] WebSocket architecture planned and documented

## Time Estimates

- **Day 1-2**: Fix current issues, add dependencies, create web-admin component
- **Day 3-4**: Create SBE schemas and basic message handling
- **Day 5**: Add simple conflict detection and advisory generation
- **Weekend**: Basic testing, web UI planning, and cleanup

## Blockers & Dependencies

- **SBE codec generation** - Need to set up Gradle task
- **Aeron cluster configuration** - May need to adjust port settings
- **Domain model design** - Need to decide on exact data structures
- **Web UI technology choice** - Need to decide on frontend framework
- **WebSocket message format** - Need to design efficient binary format

## Notes

- Start simple - get basic message flow working before adding complexity
- Focus on single-node cluster first, multi-node can come later
- Use existing `AircraftState` as foundation, enhance incrementally
- Keep performance in mind but prioritize correctness first
- Web UI should be lightweight and responsive - avoid heavy frameworks initially
- Plan for real-time updates but start with basic polling if needed 