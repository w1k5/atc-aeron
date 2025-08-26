import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Tooltip,
  LinearProgress,
  Button,
  Alert,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Divider,
} from '@mui/material';
import {
  Flight as FlightIcon,
  LocationOn as LocationIcon,
  Speed as SpeedIcon,
  Height as HeightIcon,
  Timeline as TimelineIcon,
  Route as RouteIcon,
  Warning as WarningIcon,
  CheckCircle as CheckCircleIcon,
  Refresh as RefreshIcon,
  PlayArrow as PlayIcon,
  Pause as PauseIcon,
} from '@mui/icons-material';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer, Area, AreaChart } from 'recharts';

const FlightIntentPanel = ({ aircraft = [], onRefresh }) => {
  const [selectedFlight, setSelectedFlight] = useState(null);
  const [trajectoryPrediction, setTrajectoryPrediction] = useState([]);

  // Sample flight intent data - in real app this would come from backend
  const flightIntents = [
    {
      id: 'AA123',
      callsign: 'AA123',
      type: 'B737',
      phase: 'departure',
      sid: 'KENNEDY1',
      star: null,
      waypoints: [
        { name: 'JFK', lat: 40.6413, lon: -73.7781, altitude: 0, speed: 0, type: 'departure' },
        { name: 'KENNEDY1', lat: 40.6500, lon: -73.7500, altitude: 3000, speed: 250, type: 'waypoint' },
        { name: 'BETTE', lat: 40.8000, lon: -73.6000, altitude: 15000, speed: 320, type: 'waypoint' },
        { name: 'JFK', lat: 40.6413, lon: -73.7781, altitude: 35000, speed: 450, type: 'cruise' },
      ],
      constraints: {
        maxSpeed: 450,
        maxAltitude: 41000,
        climbRate: 2500,
        descentRate: 2000,
      },
      estimatedTime: 180,
      fuel: 85,
      status: 'on_track',
    },
    {
      id: 'DL456',
      callsign: 'DL456',
      type: 'A320',
      phase: 'arrival',
      sid: null,
      star: 'KENNEDY2',
      waypoints: [
        { name: 'ATL', lat: 33.6407, lon: -84.4277, altitude: 35000, speed: 480, type: 'cruise' },
        { name: 'KENNEDY2', lat: 40.7000, lon: -73.8000, altitude: 25000, speed: 320, type: 'waypoint' },
        { name: 'JFK', lat: 40.6413, lon: -73.7781, altitude: 0, speed: 0, type: 'arrival' },
      ],
      constraints: {
        maxSpeed: 480,
        maxAltitude: 39000,
        climbRate: 2000,
        descentRate: 1800,
      },
      estimatedTime: 45,
      fuel: 25,
      status: 'delayed',
    },
    {
      id: 'UA789',
      callsign: 'UA789',
      type: 'B777',
      phase: 'cruise',
      sid: 'KENNEDY3',
      star: 'KENNEDY4',
      waypoints: [
        { name: 'JFK', lat: 40.6413, lon: -73.7781, altitude: 0, speed: 0, type: 'departure' },
        { name: 'KENNEDY3', lat: 40.7500, lon: -73.7000, altitude: 5000, speed: 300, type: 'waypoint' },
        { name: 'LONDON', lat: 51.4700, lon: -0.4543, altitude: 38000, speed: 480, type: 'cruise' },
      ],
      constraints: {
        maxSpeed: 480,
        maxAltitude: 43000,
        climbRate: 3000,
        descentRate: 2500,
      },
      estimatedTime: 420,
      fuel: 95,
      status: 'on_track',
    },
  ];

  const getPhaseColor = (phase) => {
    switch (phase) {
      case 'departure': return 'primary';
      case 'arrival': return 'warning';
      case 'cruise': return 'success';
      default: return 'default';
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'on_track': return 'success';
      case 'delayed': return 'warning';
      case 'diverted': return 'error';
      default: return 'info';
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'on_track': return <CheckCircleIcon />;
      case 'delayed': return <WarningIcon />;
      case 'diverted': return <FlightIcon />;
      default: return <FlightIcon />;
    }
  };

  const handleFlightSelect = (flightId) => {
    const flight = flightIntents.find(f => f.id === flightId);
    setSelectedFlight(flight);
    
    // Generate trajectory prediction
    if (flight) {
      const prediction = flight.waypoints.map((wp, index) => ({
        time: index * 15, // 15 minutes between waypoints
        altitude: wp.altitude,
        speed: wp.speed,
        distance: index * 50, // 50 NM between waypoints
        waypoint: wp.name,
      }));
      setTrajectoryPrediction(prediction);
    }
  };

  const getConstraintViolations = (flight) => {
    const violations = [];
    const currentState = aircraft.find(a => a.id === flight.id);
    
    if (currentState) {
      if (currentState.speed > flight.constraints.maxSpeed) {
        violations.push(`Speed ${currentState.speed} kts exceeds limit ${flight.constraints.maxSpeed} kts`);
      }
      if (currentState.altitude > flight.constraints.maxAltitude) {
        violations.push(`Altitude ${currentState.altitude} ft exceeds limit ${flight.constraints.maxAltitude} ft`);
      }
    }
    
    return violations;
  };

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h6" component="h3">
            Flight Intent & Trajectory Planning
          </Typography>
          <Tooltip title="Refresh Data">
            <IconButton onClick={onRefresh} size="small">
              <RefreshIcon />
            </IconButton>
          </Tooltip>
        </Box>

        <Grid container spacing={3}>
          {/* Flight Intent Table */}
          <Grid item xs={12} md={6}>
            <Card variant="outlined">
              <CardContent>
                <Typography variant="subtitle2" gutterBottom>
                  <RouteIcon sx={{ mr: 1, fontSize: 16 }} />
                  Active Flight Plans
                </Typography>
                
                <TableContainer component={Paper} variant="outlined">
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Callsign</TableCell>
                        <TableCell>Phase</TableCell>
                        <TableCell>Route</TableCell>
                        <TableCell>Status</TableCell>
                        <TableCell>Actions</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {flightIntents.map((flight) => {
                        const currentState = aircraft.find(a => a.id === flight.id);
                        const violations = getConstraintViolations(flight);
                        
                        return (
                          <TableRow 
                            key={flight.id} 
                            hover 
                            onClick={() => handleFlightSelect(flight.id)}
                            sx={{ cursor: 'pointer' }}
                          >
                            <TableCell>
                              <Box>
                                <Typography variant="body2" fontWeight="bold">
                                  {flight.callsign}
                                </Typography>
                                <Typography variant="caption" color="text.secondary">
                                  {flight.type}
                                </Typography>
                              </Box>
                            </TableCell>
                            <TableCell>
                              <Chip
                                label={flight.phase.toUpperCase()}
                                color={getPhaseColor(flight.phase)}
                                size="small"
                              />
                            </TableCell>
                            <TableCell>
                              <Box>
                                <Typography variant="caption">
                                  {flight.sid || 'N/A'} → {flight.star || 'N/A'}
                                </Typography>
                                <Typography variant="caption" display="block" color="text.secondary">
                                  {flight.waypoints.length} waypoints
                                </Typography>
                              </Box>
                            </TableCell>
                            <TableCell>
                              <Chip
                                label={flight.status.replace('_', ' ').toUpperCase()}
                                color={getStatusColor(flight.status)}
                                size="small"
                                icon={getStatusIcon(flight.status)}
                              />
                            </TableCell>
                            <TableCell>
                              <IconButton size="small">
                                <PlayIcon />
                              </IconButton>
                            </TableCell>
                          </TableRow>
                        );
                      })}
                    </TableBody>
                  </Table>
                </TableContainer>
              </CardContent>
            </Card>
          </Grid>

          {/* Selected Flight Details */}
          {selectedFlight && (
            <Grid item xs={12} md={6}>
              <Card variant="outlined">
                <CardContent>
                  <Typography variant="subtitle2" gutterBottom>
                    Flight Plan: {selectedFlight.callsign}
                  </Typography>
                  
                  <Grid container spacing={2}>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Aircraft Type
                      </Typography>
                      <Typography variant="body1">
                        {selectedFlight.type}
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Flight Phase
                      </Typography>
                      <Chip
                        label={selectedFlight.phase.toUpperCase()}
                        color={getPhaseColor(selectedFlight.phase)}
                        size="small"
                      />
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        SID Procedure
                      </Typography>
                      <Typography variant="body1">
                        {selectedFlight.sid || 'N/A'}
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        STAR Procedure
                      </Typography>
                      <Typography variant="body1">
                        {selectedFlight.star || 'N/A'}
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Estimated Time
                      </Typography>
                      <Typography variant="body1">
                        {selectedFlight.estimatedTime} min
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Fuel Status
                      </Typography>
                      <LinearProgress
                        variant="determinate"
                        value={selectedFlight.fuel}
                        color={selectedFlight.fuel < 30 ? 'error' : 'success'}
                        sx={{ height: 8, borderRadius: 4 }}
                      />
                      <Typography variant="caption">
                        {selectedFlight.fuel}%
                      </Typography>
                    </Grid>
                  </Grid>

                  {/* Constraint Violations */}
                  {getConstraintViolations(selectedFlight).length > 0 && (
                    <Alert severity="warning" sx={{ mt: 2 }}>
                      <Typography variant="body2">
                        <strong>Constraint Violations:</strong>
                      </Typography>
                      {getConstraintViolations(selectedFlight).map((violation, index) => (
                        <Typography key={index} variant="caption" display="block">
                          • {violation}
                        </Typography>
                      ))}
                    </Alert>
                  )}

                  {/* Waypoints */}
                  <Box sx={{ mt: 2 }}>
                    <Typography variant="subtitle2" gutterBottom>
                      <LocationIcon sx={{ mr: 1, fontSize: 16 }} />
                      Waypoints
                    </Typography>
                    <List dense>
                      {selectedFlight.waypoints.map((waypoint, index) => (
                        <React.Fragment key={index}>
                          <ListItem>
                            <ListItemIcon>
                              <LocationIcon color="primary" />
                            </ListItemIcon>
                            <ListItemText
                              primary={waypoint.name}
                              secondary={`${waypoint.altitude.toLocaleString()} ft • ${waypoint.speed} kts • ${waypoint.type}`}
                            />
                            <Chip
                              label={`${waypoint.lat.toFixed(2)}, ${waypoint.lon.toFixed(2)}`}
                              size="small"
                              variant="outlined"
                            />
                          </ListItem>
                          {index < selectedFlight.waypoints.length - 1 && <Divider />}
                        </React.Fragment>
                      ))}
                    </List>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          )}
        </Grid>

        {/* Trajectory Prediction Chart */}
        {selectedFlight && trajectoryPrediction.length > 0 && (
          <Card variant="outlined" sx={{ mt: 3 }}>
            <CardContent>
              <Typography variant="subtitle2" gutterBottom>
                <TimelineIcon sx={{ mr: 1, fontSize: 16 }} />
                Trajectory Prediction
              </Typography>
              
              <Grid container spacing={2}>
                <Grid item xs={12} md={6}>
                  <Typography variant="body2" gutterBottom>
                    Altitude & Speed Profile
                  </Typography>
                  <ResponsiveContainer width="100%" height={200}>
                    <AreaChart data={trajectoryPrediction}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="time" />
                      <YAxis yAxisId="left" />
                      <YAxis yAxisId="right" orientation="right" />
                      <RechartsTooltip />
                      <Area 
                        yAxisId="left"
                        type="monotone" 
                        dataKey="altitude" 
                        stroke="#1976d2" 
                        fill="#1976d2" 
                        fillOpacity={0.3}
                        name="Altitude (ft)"
                      />
                      <Area 
                        yAxisId="right"
                        type="monotone" 
                        dataKey="speed" 
                        stroke="#4caf50" 
                        fill="#4caf50" 
                        fillOpacity={0.3}
                        name="Speed (kts)"
                      />
                    </AreaChart>
                  </ResponsiveContainer>
                </Grid>

                <Grid item xs={12} md={6}>
                  <Typography variant="body2" gutterBottom>
                    Distance Progress
                  </Typography>
                  <ResponsiveContainer width="100%" height={200}>
                    <LineChart data={trajectoryPrediction}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="time" />
                      <YAxis />
                      <RechartsTooltip />
                      <Line 
                        type="monotone" 
                        dataKey="distance" 
                        stroke="#ff9800" 
                        strokeWidth={2}
                        dot={{ fill: '#ff9800' }}
                        name="Distance (NM)"
                      />
                    </LineChart>
                  </ResponsiveContainer>
                </Grid>
              </Grid>

              {/* Flight Progress */}
              <Box sx={{ mt: 2, p: 2, bgcolor: 'background.default', borderRadius: 1 }}>
                <Typography variant="subtitle2" gutterBottom>
                  Flight Progress
                </Typography>
                <Grid container spacing={2}>
                  <Grid item xs={6} md={3}>
                    <Box sx={{ textAlign: 'center' }}>
                      <Typography variant="h6" color="primary">
                        {selectedFlight.waypoints.length}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Total Waypoints
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={6} md={3}>
                    <Box sx={{ textAlign: 'center' }}>
                      <Typography variant="h6" color="success.main">
                        {selectedFlight.estimatedTime}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Est. Time (min)
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={6} md={3}>
                    <Box sx={{ textAlign: 'center' }}>
                      <Typography variant="h6" color="info.main">
                        {selectedFlight.constraints.maxAltitude.toLocaleString()}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Max Altitude (ft)
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={6} md={3}>
                    <Box sx={{ textAlign: 'center' }}>
                      <Typography variant="h6" color="warning.main">
                        {selectedFlight.constraints.maxSpeed}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Max Speed (kts)
                      </Typography>
                    </Box>
                  </Grid>
                </Grid>
              </Box>
            </CardContent>
          </Card>
        )}
      </CardContent>
    </Card>
  );
};

export default FlightIntentPanel;


