import React, { useEffect, useRef } from 'react';
import { Box, Typography, Paper, Chip } from '@mui/material';
import { Flight as FlightIcon } from '@mui/icons-material';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

// Fix for default marker icons in Leaflet
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
});

const AircraftMap = ({ aircraft, conflicts, onAircraftSelect, sectors = [] }) => {
  const mapRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const markersRef = useRef([]);
  const sectorLayersRef = useRef([]);
  const conflictZonesRef = useRef([]);

  // Sample sector data - in real app this would come from backend
  const defaultSectors = [
    {
      id: 1,
      name: 'N90',
      bounds: [[40.5, -74.5], [41.0, -73.5]],
      maxAircraft: 20,
      maxComplexity: 100,
      currentAircraft: aircraft.length,
      currentComplexity: aircraft.reduce((sum, ac) => sum + (ac.complexity || 5), 0),
    }
  ];

  const sectorsToUse = sectors.length > 0 ? sectors : defaultSectors;

  useEffect(() => {
    if (!mapRef.current || mapInstanceRef.current) return;

    // Initialize map centered on NYC area
    const map = L.map(mapRef.current).setView([40.7128, -74.0060], 10);
    mapInstanceRef.current = map;

    // Add OpenStreetMap tiles
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      maxZoom: 18,
    }).addTo(map);

    // Cleanup function
    return () => {
      if (mapInstanceRef.current) {
        mapInstanceRef.current.remove();
        mapInstanceRef.current = null;
      }
    };
  }, []);

  // Add sector boundaries
  useEffect(() => {
    if (!mapInstanceRef.current) return;

    // Clear existing sector layers
    sectorLayersRef.current.forEach(layer => layer.remove());
    sectorLayersRef.current = [];

    sectorsToUse.forEach(sector => {
      const sectorLayer = L.rectangle(sector.bounds, {
        color: getSectorColor(sector),
        weight: 2,
        fillColor: getSectorColor(sector),
        fillOpacity: 0.1,
      }).addTo(mapInstanceRef.current);

      // Add sector label
      const center = [
        (sector.bounds[0][0] + sector.bounds[1][0]) / 2,
        (sector.bounds[0][1] + sector.bounds[1][1]) / 2
      ];
      
      const label = L.divIcon({
        className: 'sector-label',
        html: `
          <div style="
            background: ${getSectorColor(sector)};
            color: white;
            padding: 4px 8px;
            border-radius: 4px;
            font-weight: bold;
            font-size: 12px;
            border: 2px solid white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.3);
          ">
            ${sector.name}
          </div>
        `,
        iconSize: [60, 30],
        iconAnchor: [30, 15],
      });

      L.marker(center, { icon: label }).addTo(mapInstanceRef.current);
      sectorLayersRef.current.push(sectorLayer);
    });
  }, [sectorsToUse]);

  // Add conflict zones
  useEffect(() => {
    if (!mapInstanceRef.current) return;

    // Clear existing conflict zones
    conflictZonesRef.current.forEach(zone => zone.remove());
    conflictZonesRef.current = [];

    conflicts.forEach(conflict => {
      const aircraft1 = aircraft.find(a => a.id === conflict.aircraft1);
      const aircraft2 = aircraft.find(a => a.id === conflict.aircraft2);
      
      if (aircraft1 && aircraft2) {
        const center = [
          (aircraft1.lat + aircraft2.lat) / 2,
          (aircraft1.lon + aircraft2.lon) / 2
        ];
        
        // Create conflict zone circle
        const conflictZone = L.circle(center, {
          radius: 5000, // 5km radius
          color: getConflictColor(conflict.severity),
          weight: 3,
          fillColor: getConflictColor(conflict.severity),
          fillOpacity: 0.2,
        }).addTo(mapInstanceRef.current);

        // Add conflict label
        const label = L.divIcon({
          className: 'conflict-label',
          html: `
            <div style="
              background: ${getConflictColor(conflict.severity)};
              color: white;
              padding: 4px 8px;
              border-radius: 4px;
              font-weight: bold;
              font-size: 10px;
              border: 2px solid white;
              box-shadow: 0 2px 4px rgba(0,0,0,0.3);
            ">
              ${conflict.severity.toUpperCase()}
            </div>
          `,
          iconSize: [50, 25],
          iconAnchor: [25, 12],
        });

        L.marker(center, { icon: label }).addTo(mapInstanceRef.current);
        conflictZonesRef.current.push(conflictZone);
      }
    });
  }, [conflicts, aircraft]);

  useEffect(() => {
    if (!mapInstanceRef.current) return;

    // Clear existing markers
    markersRef.current.forEach(marker => marker.remove());
    markersRef.current = [];

    // Add aircraft markers
    aircraft.forEach((ac) => {
      // Create custom aircraft icon with complexity indicator
      const aircraftIcon = L.divIcon({
        className: 'aircraft-marker',
        html: `
          <div style="
            background: ${ac.status === 'conflict' ? '#f44336' : '#1976d2'};
            color: white;
            border-radius: 50%;
            width: 24px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
            font-weight: bold;
            border: 2px solid white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.3);
            transform: rotate(${ac.heading}deg);
            position: relative;
          ">
            ✈
            ${ac.complexity ? `
              <div style="
                position: absolute;
                top: -8px;
                right: -8px;
                background: ${getComplexityColor(ac.complexity)};
                color: white;
                border-radius: 50%;
                width: 16px;
                height: 16px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 8px;
                font-weight: bold;
                border: 1px solid white;
                transform: rotate(-${ac.heading}deg);
              ">
                ${Math.round(ac.complexity)}
              </div>
            ` : ''}
          </div>
        `,
        iconSize: [24, 24],
        iconAnchor: [12, 12],
      });

      // Create marker
      const marker = L.marker([ac.lat, ac.lon], { icon: aircraftIcon })
        .addTo(mapInstanceRef.current)
        .bindPopup(`
          <div style="min-width: 250px;">
            <h4 style="margin: 0 0 8px 0; color: #1976d2;">${ac.callsign}</h4>
            <p style="margin: 4px 0;"><strong>Type:</strong> ${ac.type}</p>
            <p style="margin: 4px 0;"><strong>Altitude:</strong> ${ac.altitude.toLocaleString()} ft</p>
            <p style="margin: 4px 0;"><strong>Speed:</strong> ${ac.speed} kts</p>
            <p style="margin: 4px 0;"><strong>Heading:</strong> ${ac.heading}°</p>
            <p style="margin: 4px 0;"><strong>Status:</strong> 
              <span style="color: ${ac.status === 'conflict' ? '#f44336' : '#4caf50'};">
                ${ac.status}
              </span>
            </p>
            ${ac.complexity ? `<p style="margin: 4px 0;"><strong>Complexity:</strong> 
              <span style="color: ${getComplexityColor(ac.complexity)};">
                ${ac.complexity.toFixed(1)}/10
              </span>
            </p>` : ''}
            ${ac.sector ? `<p style="margin: 4px 0;"><strong>Sector:</strong> ${ac.sector}</p>` : ''}
          </div>
        `);

      // Add click handler
      marker.on('click', () => {
        onAircraftSelect(ac.id);
      });

      markersRef.current.push(marker);
    });

    // Fit map to show all aircraft if there are any
    if (aircraft.length > 0) {
      const group = new L.featureGroup(markersRef.current);
      mapInstanceRef.current.fitBounds(group.getBounds().pad(0.1));
    }
  }, [aircraft, onAircraftSelect]);

  // Helper functions
  const getSectorColor = (sector) => {
    const utilization = sector.currentAircraft / sector.maxAircraft;
    if (utilization > 0.9) return '#f44336'; // Red - near capacity
    if (utilization > 0.7) return '#ff9800'; // Orange - high utilization
    if (utilization > 0.5) return '#ffc107'; // Yellow - moderate
    return '#4caf50'; // Green - low utilization
  };

  const getConflictColor = (severity) => {
    switch (severity.toLowerCase()) {
      case 'critical': return '#d32f2f';
      case 'high': return '#f44336';
      case 'medium': return '#ff9800';
      case 'low': return '#ffc107';
      default: return '#f44336';
    }
  };

  const getComplexityColor = (complexity) => {
    if (complexity >= 8) return '#d32f2f'; // Red - very complex
    if (complexity >= 6) return '#f44336'; // Red - complex
    if (complexity >= 4) return '#ff9800'; // Orange - moderate
    if (complexity >= 2) return '#ffc107'; // Yellow - simple
    return '#4caf50'; // Green - very simple
  };

  // Add CSS for the enhanced markers
  useEffect(() => {
    const style = document.createElement('style');
    style.textContent = `
      .aircraft-marker {
        background: transparent !important;
        border: none !important;
      }
      .leaflet-popup-content {
        margin: 8px 12px;
      }
      .leaflet-popup-content h4 {
        margin: 0 0 8px 0;
        color: #1976d2;
      }
      .leaflet-popup-content p {
        margin: 4px 0;
      }
      .sector-label, .conflict-label {
        background: transparent !important;
        border: none !important;
      }
    `;
    document.head.appendChild(style);

    return () => {
      document.head.removeChild(style);
    };
  }, []);

  return (
    <Paper
      variant="outlined"
      sx={{
        height: '100%',
        width: '100%',
        position: 'relative',
        overflow: 'hidden',
      }}
    >
      <div
        ref={mapRef}
        style={{
          height: '100%',
          width: '100%',
          minHeight: '400px',
        }}
      />
      
      {/* Enhanced map overlay info */}
      <Box
        sx={{
          position: 'absolute',
          top: 10,
          right: 10,
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          padding: 1.5,
          borderRadius: 1,
          zIndex: 1000,
          minWidth: 200,
        }}
      >
        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 1 }}>
          <strong>System Status</strong>
        </Typography>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
            <Typography variant="caption">Aircraft:</Typography>
            <Chip 
              label={aircraft.length} 
              size="small" 
              color={aircraft.length > 15 ? 'warning' : 'success'}
            />
          </Box>
          <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
            <Typography variant="caption">Conflicts:</Typography>
            <Chip 
              label={conflicts.length} 
              size="small" 
              color={conflicts.length > 0 ? 'error' : 'success'}
            />
          </Box>
          <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
            <Typography variant="caption">Sectors:</Typography>
            <Chip 
              label={sectorsToUse.length} 
              size="small" 
              color="primary"
            />
          </Box>
        </Box>
      </Box>

      {/* Legend */}
      <Box
        sx={{
          position: 'absolute',
          bottom: 10,
          left: 10,
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          padding: 1.5,
          borderRadius: 1,
          zIndex: 1000,
          minWidth: 150,
        }}
      >
        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 1 }}>
          <strong>Legend</strong>
        </Typography>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: '#1976d2' }} />
            <Typography variant="caption">Normal Aircraft</Typography>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: '#f44336' }} />
            <Typography variant="caption">Conflict Aircraft</Typography>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: '#ff9800' }} />
            <Typography variant="caption">Complexity Score</Typography>
          </Box>
        </Box>
      </Box>
    </Paper>
  );
};

export default AircraftMap;
