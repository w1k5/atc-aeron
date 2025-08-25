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
} from '@mui/material';
import {
  Flight as FlightIcon,
  Warning as WarningIcon,
  Search as SearchIcon,
  Refresh as RefreshIcon,
  LocationOn as LocationIcon,
} from '@mui/icons-material';
import AircraftMap from '../components/AircraftMap';
import ConflictTable from '../components/ConflictTable';

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
  ]);

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedAircraft, setSelectedAircraft] = useState(null);

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
    })));
  };

  const filteredAircraft = aircraft.filter(a =>
    a.callsign.toLowerCase().includes(searchTerm.toLowerCase()) ||
    a.type.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Aircraft Monitor
        </Typography>
        <Tooltip title="Refresh Data">
          <IconButton onClick={handleRefresh} color="primary">
            <RefreshIcon />
          </IconButton>
        </Tooltip>
      </Box>

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
    </Box>
  );
};

export default AircraftMonitor;
