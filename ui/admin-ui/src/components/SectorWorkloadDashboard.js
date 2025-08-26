import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  LinearProgress,
  Chip,
  Grid,
  Alert,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  Warning as WarningIcon,
  CheckCircle as CheckCircleIcon,
  Error as ErrorIcon,
  Info as InfoIcon,
  Refresh as RefreshIcon,
} from '@mui/icons-material';

const SectorWorkloadDashboard = ({ sectors = [], onRefresh }) => {
  // Sample sector data - in real app this would come from backend
  const defaultSectors = [
    {
      id: 1,
      name: 'N90',
      maxAircraft: 20,
      maxComplexity: 100,
      currentAircraft: 6,
      currentComplexity: 42,
      utilization: 0.75,
      complexityUtilization: 0.42,
      status: 'normal',
      lastUpdate: new Date(),
    },
    {
      id: 2,
      name: 'N91',
      maxAircraft: 18,
      maxComplexity: 90,
      currentAircraft: 12,
      currentComplexity: 78,
      utilization: 0.67,
      complexityUtilization: 0.87,
      status: 'warning',
      lastUpdate: new Date(),
    },
    {
      id: 3,
      name: 'N92',
      maxAircraft: 22,
      maxComplexity: 110,
      currentAircraft: 8,
      currentComplexity: 35,
      utilization: 0.36,
      complexityUtilization: 0.32,
      status: 'normal',
      lastUpdate: new Date(),
    },
  ];

  const sectorsToUse = sectors.length > 0 ? sectors : defaultSectors;

  const getStatusColor = (status) => {
    switch (status) {
      case 'critical': return 'error';
      case 'warning': return 'warning';
      case 'normal': return 'success';
      default: return 'info';
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'critical': return <ErrorIcon />;
      case 'warning': return <WarningIcon />;
      case 'normal': return <CheckCircleIcon />;
      default: return <InfoIcon />;
    }
  };

  const getUtilizationColor = (utilization) => {
    if (utilization > 0.9) return 'error';
    if (utilization > 0.7) return 'warning';
    if (utilization > 0.5) return 'info';
    return 'success';
  };

  const getComplexityColor = (complexity) => {
    if (complexity >= 8) return 'error';
    if (complexity >= 6) return 'warning';
    if (complexity >= 4) return 'info';
    return 'success';
  };

  const getOverallSystemStatus = () => {
    const criticalSectors = sectorsToUse.filter(s => s.status === 'critical').length;
    const warningSectors = sectorsToUse.filter(s => s.status === 'warning').length;
    
    if (criticalSectors > 0) return { status: 'critical', message: `${criticalSectors} sectors at critical capacity` };
    if (warningSectors > 0) return { status: 'warning', message: `${warningSectors} sectors approaching capacity` };
    return { status: 'normal', message: 'All sectors operating normally' };
  };

  const overallStatus = getOverallSystemStatus();

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h6" component="h3">
            Sector Workload Dashboard
          </Typography>
          <Tooltip title="Refresh Data">
            <IconButton onClick={onRefresh} size="small">
              <RefreshIcon />
            </IconButton>
          </Tooltip>
        </Box>

        {/* Overall System Status */}
        <Alert 
          severity={overallStatus.status} 
          icon={getStatusIcon(overallStatus.status)}
          sx={{ mb: 2 }}
        >
          {overallStatus.message}
        </Alert>

        {/* Sector Grid */}
        <Grid container spacing={2}>
          {sectorsToUse.map((sector) => (
            <Grid item xs={12} md={4} key={sector.id}>
              <Card variant="outlined" sx={{ height: '100%' }}>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                    <Typography variant="h6" component="h4">
                      Sector {sector.name}
                    </Typography>
                    <Chip
                      label={sector.status.toUpperCase()}
                      color={getStatusColor(sector.status)}
                      size="small"
                      icon={getStatusIcon(sector.status)}
                    />
                  </Box>

                  {/* Aircraft Capacity */}
                  <Box sx={{ mb: 2 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                      <Typography variant="body2" color="text.secondary">
                        Aircraft Capacity
                      </Typography>
                      <Typography variant="body2">
                        {sector.currentAircraft}/{sector.maxAircraft}
                      </Typography>
                    </Box>
                    <LinearProgress
                      variant="determinate"
                      value={sector.utilization * 100}
                      color={getUtilizationColor(sector.utilization)}
                      sx={{ height: 8, borderRadius: 4 }}
                    />
                    <Typography variant="caption" color="text.secondary">
                      {Math.round(sector.utilization * 100)}% utilized
                    </Typography>
                  </Box>

                  {/* Complexity Score */}
                  <Box sx={{ mb: 2 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                      <Typography variant="body2" color="text.secondary">
                        Complexity Score
                      </Typography>
                      <Typography variant="body2">
                        {sector.currentComplexity.toFixed(1)}/{sector.maxComplexity}
                      </Typography>
                    </Box>
                    <LinearProgress
                      variant="determinate"
                      value={sector.complexityUtilization * 100}
                      color={getComplexityColor(sector.complexityUtilization * 10)}
                      sx={{ height: 8, borderRadius: 4 }}
                    />
                    <Typography variant="caption" color="text.secondary">
                      {Math.round(sector.complexityUtilization * 100)}% of complexity capacity
                    </Typography>
                  </Box>

                  {/* Sector Details */}
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Typography variant="caption" color="text.secondary">
                      Last Update: {sector.lastUpdate.toLocaleTimeString()}
                    </Typography>
                    <Chip
                      label={`${sector.currentAircraft} active`}
                      size="small"
                      variant="outlined"
                    />
                  </Box>

                  {/* Warnings */}
                  {sector.utilization > 0.8 && (
                    <Alert severity="warning" sx={{ mt: 1 }} size="small">
                      Approaching aircraft capacity limit
                    </Alert>
                  )}
                  {sector.complexityUtilization > 0.8 && (
                    <Alert severity="warning" sx={{ mt: 1 }} size="small">
                      High complexity workload
                    </Alert>
                  )}
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>

        {/* Summary Statistics */}
        <Box sx={{ mt: 3, p: 2, bgcolor: 'background.default', borderRadius: 1 }}>
          <Typography variant="subtitle2" gutterBottom>
            System Summary
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={6} md={3}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h6" color="primary">
                  {sectorsToUse.length}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Active Sectors
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={6} md={3}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h6" color="primary">
                  {sectorsToUse.reduce((sum, s) => sum + s.currentAircraft, 0)}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Total Aircraft
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={6} md={3}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h6" color="warning.main">
                  {sectorsToUse.filter(s => s.status === 'warning').length}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Warning Sectors
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={6} md={3}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h6" color="error.main">
                  {sectorsToUse.filter(s => s.status === 'critical').length}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Critical Sectors
                </Typography>
              </Box>
            </Grid>
          </Grid>
        </Box>
      </CardContent>
    </Card>
  );
};

export default SectorWorkloadDashboard;


