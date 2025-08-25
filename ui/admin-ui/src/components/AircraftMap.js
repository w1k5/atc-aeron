import React from 'react';
import { Box, Typography, Paper } from '@mui/material';
import { Flight as FlightIcon } from '@mui/icons-material';

const AircraftMap = ({ aircraft, conflicts, onAircraftSelect }) => {
  // Placeholder map component - we'll implement actual map later
  return (
    <Paper
      variant="outlined"
      sx={{
        height: '100%',
        width: '100%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: 'background.default',
        position: 'relative',
        overflow: 'hidden',
      }}
    >
      <Box sx={{ textAlign: 'center' }}>
        <FlightIcon sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
        <Typography variant="h6" color="text.secondary">
          Aircraft Map View
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {aircraft.length} aircraft tracked • {conflicts.length} active conflicts
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
          Map integration coming soon...
        </Typography>
      </Box>
      
      {/* Aircraft position indicators */}
      {aircraft.map((ac, index) => (
        <Box
          key={ac.id}
          onClick={() => onAircraftSelect(ac.id)}
          sx={{
            position: 'absolute',
            left: `${20 + (index * 15)}%`,
            top: `${30 + (index * 10)}%`,
            cursor: 'pointer',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            '&:hover': {
              transform: 'scale(1.1)',
            },
          }}
        >
          <FlightIcon
            sx={{
              fontSize: 24,
              color: ac.status === 'conflict' ? 'error.main' : 'primary.main',
              transform: `rotate(${ac.heading}deg)`,
            }}
          />
          <Typography variant="caption" sx={{ mt: 0.5, fontSize: '0.7rem' }}>
            {ac.callsign}
          </Typography>
        </Box>
      ))}
    </Paper>
  );
};

export default AircraftMap;
