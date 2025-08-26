import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  IconButton,
  Tooltip,
  TextField,
  InputAdornment,
  Tabs,
  Tab,
  Alert,
} from '@mui/material';
import {
  Flight as FlightIcon,
  Warning as WarningIcon,
  Search as SearchIcon,
  Refresh as RefreshIcon,
  LocationOn as LocationIcon,
  Speed as SpeedIcon,
  Height as HeightIcon,
  Timeline as TimelineIcon,
} from '@mui/icons-material';
import AircraftMap from '../components/AircraftMap';
import ConflictTable from '../components/ConflictTable';
import SectorWorkloadDashboard from '../components/SectorWorkloadDashboard';
import ConflictAnalysisPanel from '../components/ConflictAnalysisPanel';
import PerformanceMetricsDashboard from '../components/PerformanceMetricsDashboard';
import FlightIntentPanel from '../components/FlightIntentPanel';

const AircraftMonitor = () => {
  const [aircraft, setAircraft] = useState([
    {
      id: 'AA123',
      callsign: 'AA123',
      type: 'B737',
      altitude: 35000,
      speed: 450,
      heading: 270,
      lat: 40.7128,
      lon: -74.0060,
      status: 'normal',
      sector: 'N90',
      complexity: 6.2,
    },
    {
      id: 'DL456',
      callsign: 'DL456',
      type: 'A320',
      altitude: 32000,
      speed: 420,
      heading: 180,
      lat: 40.7589,
      lon: -73.9851,
      status: 'conflict',
      sector: 'N90',
      complexity: 8.1,
    },
    {
      id: 'UA789',
      callsign: 'UA789',
      type: 'B777',
      altitude: 38000,
      speed: 480,
      heading: 90,
      lat: 40.7505,
      lon: -73.9934,
      status: 'normal',
      sector: 'N90',
      complexity: 4.8,
    },
    {
      id: 'SW101',
      callsign: 'SW101',
      type: 'B737',
      altitude: 28000,
      speed: 380,
      heading: 45,
      lat: 40.6413,
      lon: -73.7781,
      status: 'normal',
      sector: 'N90',
      complexity: 5.5,
    },
    {
      id: 'JB202',
      callsign: 'JB202',
      type: 'A321',
      altitude: 31000,
      speed: 410,
      heading: 135,
      lat: 40.6895,
      lon: -74.1745,
      status: 'normal',
      sector: 'N90',
      complexity: 3.9,
    },
    {
      id: 'FR303',
      callsign: 'FR303',
      type: 'B737',
      altitude: 26000,
      speed: 360,
      heading: 315,
      lat: 40.7769,
      lon: -73.8740,
      status: 'conflict',
      sector: 'N90',
      complexity: 7.3,
    },
  ]);

  const [conflicts, setConflicts] = useState([
    {
      id: 1,
      aircraft1: 'AA123',
      aircraft2: 'DL456',
      type: 'separation',
      severity: 'high',
      distance: 2.1,
      altitude: 1000,
      timeToConflict: 45,
    },
    {
      id: 2,
      aircraft1: 'SW101',
      aircraft2: 'FR303',
      type: 'separation',
      severity: 'medium',
      distance: 3.5,
      altitude: 2000,
      timeToConflict: 120,
    },
  ]);

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedAircraft, setSelectedAircraft] = useState(null);
  const [activeTab, setActiveTab] = useState(0);

  const handleAircraftSelect = (aircraftId) => {
    const selected = aircraft.find(a => a.id === aircraftId);
    setSelectedAircraft(selected);
  };

  const handleRefresh = () => {
    // Simulate data refresh
    setAircraft(prev => prev.map(a => ({
      ...a,
      altitude: a.altitude + Math.floor(Math.random() * 1000) - 500,
      speed: a.speed + Math.floor(Math.random() * 100) - 50,
      complexity: Math.max(1, Math.min(10, a.complexity + (Math.random() - 0.5) * 2)),
    })));
  };

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  const filteredAircraft = aircraft.filter(a =>
    a.callsign.toLowerCase().includes(searchTerm.toLowerCase()) ||
    a.type.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Aircraft Monitor & ATC Decision Dashboard
        </Typography>
        <Tooltip title="Refresh Data">
          <IconButton onClick={handleRefresh} color="primary">
            <RefreshIcon />
          </IconButton>
        </Tooltip>
      </Box>

      {/* System Status Overview */}
      <Alert severity="info" sx={{ mb: 3 }}>
        <Typography variant="body2">
          <strong>System Status:</strong> {aircraft.length} aircraft tracked • {conflicts.length} active conflicts • 
          Sector N90 operating at {Math.round((aircraft.length / 20) * 100)}% capacity
        </Typography>
      </Alert>

      {/* Main Dashboard Tabs */}
      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={activeTab} onChange={handleTabChange} aria-label="ATC dashboard tabs">
          <Tab label="Real-time Monitor" icon={<FlightIcon />} iconPosition="start" />
          <Tab label="Sector Workload" icon={<LocationIcon />} iconPosition="start" />
          <Tab label="Conflict Analysis" icon={<WarningIcon />} iconPosition="start" />
          <Tab label="Performance Metrics" icon={<SpeedIcon />} iconPosition="start" />
          <Tab label="Flight Intent" icon={<TimelineIcon />} iconPosition="start" />
        </Tabs>
      </Box>

      {/* Tab Content */}
      {activeTab === 0 && (
        <Grid container spacing={3}>
          {/* Aircraft Map */}
          <Grid item xs={12} lg={8}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Real-time Aircraft Positions
                </Typography>
                <Box sx={{ height: 500, width: '100%' }}>
                  <AircraftMap
                    aircraft={filteredAircraft}
                    conflicts={conflicts}
                    onAircraftSelect={handleAircraftSelect}
                  />
                </Box>
              </CardContent>
            </Card>
          </Grid>

          {/* Aircraft List */}
          <Grid item xs={12} lg={4}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Aircraft List
                </Typography>
                
                <TextField
                  fullWidth
                  size="small"
                  placeholder="Search aircraft..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <SearchIcon />
                      </InputAdornment>
                    ),
                  }}
                  sx={{ mb: 2 }}
                />

                <TableContainer component={Paper} variant="outlined">
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Callsign</TableCell>
                        <TableCell>Type</TableCell>
                        <TableCell>Altitude</TableCell>
                        <TableCell>Complexity</TableCell>
                        <TableCell>Status</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {filteredAircraft.map((aircraft) => (
                        <TableRow
                          key={aircraft.id}
                          hover
                          onClick={() => handleAircraftSelect(aircraft.id)}
                          sx={{ cursor: 'pointer' }}
                        >
                          <TableCell>
                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                              <FlightIcon sx={{ mr: 1, fontSize: 16 }} />
                              {aircraft.callsign}
                            </Box>
                          </TableCell>
                          <TableCell>{aircraft.type}</TableCell>
                          <TableCell>{aircraft.altitude.toLocaleString()}</TableCell>
                          <TableCell>
                            <Chip
                              label={aircraft.complexity?.toFixed(1) || 'N/A'}
                              size="small"
                              color={aircraft.complexity >= 7 ? 'warning' : 'success'}
                              variant="outlined"
                            />
                          </TableCell>
                          <TableCell>
                            <Chip
                              label={aircraft.status}
                              color={aircraft.status === 'conflict' ? 'error' : 'success'}
                              size="small"
                            />
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </CardContent>
            </Card>
          </Grid>

          {/* Selected Aircraft Details */}
          {selectedAircraft && (
            <Grid item xs={12} md={6}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Aircraft Details: {selectedAircraft.callsign}
                  </Typography>
                  <Grid container spacing={2}>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Aircraft Type
                      </Typography>
                      <Typography variant="body1">
                        {selectedAircraft.type}
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Sector
                      </Typography>
                      <Typography variant="body1">
                        {selectedAircraft.sector}
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Altitude
                      </Typography>
                      <Typography variant="body1">
                        {selectedAircraft.altitude.toLocaleString()} ft
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Speed
                      </Typography>
                      <Typography variant="body1">
                        {selectedAircraft.speed} kts
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Heading
                      </Typography>
                      <Typography variant="body1">
                        {selectedAircraft.heading}°
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Complexity Score
                      </Typography>
                      <Typography variant="body1">
                        <Chip
                          label={`${selectedAircraft.complexity?.toFixed(1) || 'N/A'}/10`}
                          size="small"
                          color={selectedAircraft.complexity >= 7 ? 'warning' : 'success'}
                        />
                      </Typography>
                    </Grid>
                    <Grid item xs={12}>
                      <Typography variant="body2" color="text.secondary">
                        Coordinates
                      </Typography>
                      <Typography variant="body1" sx={{ fontSize: '0.875rem' }}>
                        {selectedAircraft.lat.toFixed(4)}, {selectedAircraft.lon.toFixed(4)}
                      </Typography>
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            </Grid>
          )}

          {/* Conflicts Panel */}
          <Grid item xs={12} md={6}>
            <ConflictTable conflicts={conflicts} />
          </Grid>
        </Grid>
      )}

      {activeTab === 1 && (
        <SectorWorkloadDashboard onRefresh={handleRefresh} />
      )}

      {activeTab === 2 && (
        <ConflictAnalysisPanel conflicts={conflicts} aircraft={aircraft} onRefresh={handleRefresh} />
      )}

      {activeTab === 3 && (
        <PerformanceMetricsDashboard onRefresh={handleRefresh} />
      )}

      {activeTab === 4 && (
        <FlightIntentPanel aircraft={aircraft} onRefresh={handleRefresh} />
      )}
    </Box>
  );
};

export default AircraftMonitor;


