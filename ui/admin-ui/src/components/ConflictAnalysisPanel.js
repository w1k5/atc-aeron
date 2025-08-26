import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Chip,
  Grid,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Tooltip,
  Collapse,
  LinearProgress,
  Button,
} from '@mui/material';
import {
  Warning as WarningIcon,
  Error as ErrorIcon,
  Info as InfoIcon,
  ExpandMore as ExpandMoreIcon,
  ExpandLess as ExpandLessIcon,
  Refresh as RefreshIcon,
  Timeline as TimelineIcon,
  Speed as SpeedIcon,
  Height as HeightIcon,
} from '@mui/icons-material';

const ConflictAnalysisPanel = ({ conflicts = [], aircraft = [], onRefresh }) => {
  const [expandedConflict, setExpandedConflict] = useState(null);

  // Sample conflict data with enhanced information - in real app this would come from backend
  const defaultConflicts = [
    {
      id: 1,
      aircraft1: 'AA123',
      aircraft2: 'DL456',
      type: 'separation',
      severity: 'high',
      distance: 2.1,
      altitude: 1000,
      timeToConflict: 45,
      urgency: 'urgent',
      predictedEvolution: [
        { time: 0, distance: 2.1, altitude: 1000, risk: 'high' },
        { time: 15, distance: 1.8, altitude: 800, risk: 'critical' },
        { time: 30, distance: 1.2, altitude: 600, risk: 'critical' },
        { time: 45, distance: 0.8, altitude: 400, risk: 'critical' },
      ],
      resolutionSuggestions: [
        { type: 'altitude', action: 'Climb AA123 to FL360', priority: 'high' },
        { type: 'speed', action: 'Reduce DL456 speed to 380 kts', priority: 'medium' },
        { type: 'heading', action: 'Turn AA123 15° right', priority: 'low' },
      ],
      sector: 'N90',
      controller: 'CTR-01',
      lastUpdate: new Date(),
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
      urgency: 'high',
      predictedEvolution: [
        { time: 0, distance: 3.5, altitude: 2000, risk: 'medium' },
        { time: 30, distance: 3.2, altitude: 1800, risk: 'medium' },
        { time: 60, distance: 2.8, altitude: 1600, risk: 'high' },
        { time: 90, distance: 2.3, altitude: 1400, risk: 'high' },
        { time: 120, distance: 1.9, altitude: 1200, risk: 'critical' },
      ],
      resolutionSuggestions: [
        { type: 'altitude', action: 'Descend SW101 to FL280', priority: 'medium' },
        { type: 'speed', action: 'Increase FR303 speed to 400 kts', priority: 'low' },
      ],
      sector: 'N90',
      controller: 'CTR-02',
      lastUpdate: new Date(),
    },
  ];

  const conflictsToUse = conflicts.length > 0 ? conflicts : defaultConflicts;

  const getUrgencyColor = (urgency) => {
    if (!urgency) return 'info';
    switch (urgency) {
      case 'immediate': return 'error';
      case 'urgent': return 'error';
      case 'high': return 'warning';
      case 'normal': return 'info';
      default: return 'info';
    }
  };

  const getUrgencyIcon = (urgency) => {
    if (!urgency) return <InfoIcon />;
    switch (urgency) {
      case 'immediate': return <ErrorIcon />;
      case 'urgent': return <ErrorIcon />;
      case 'high': return <WarningIcon />;
      case 'normal': return <InfoIcon />;
      default: return <InfoIcon />;
    }
  };

  const getSeverityColor = (severity) => {
    if (!severity) return 'info';
    switch (severity.toLowerCase()) {
      case 'critical': return 'error';
      case 'high': return 'error';
      case 'medium': return 'warning';
      case 'low': return 'info';
      default: return 'info';
    }
  };

  const getPriorityColor = (priority) => {
    if (!priority) return 'info';
    switch (priority) {
      case 'high': return 'error';
      case 'medium': return 'warning';
      case 'low': return 'info';
      default: return 'info';
    }
  };

  const getRiskColor = (risk) => {
    if (!risk) return '#757575';
    switch (risk) {
      case 'critical': return '#d32f2f';
      case 'high': return '#f44336';
      case 'medium': return '#ff9800';
      case 'low': return '#4caf50';
      default: return '#757575';
    }
  };

  const getAircraftInfo = (aircraftId) => {
    return aircraft.find(a => a.id === aircraftId) || { callsign: aircraftId, type: 'Unknown' };
  };

  const toggleConflictExpansion = (conflictId) => {
    setExpandedConflict(expandedConflict === conflictId ? null : conflictId);
  };

  const getOverallSystemStatus = () => {
    const immediateConflicts = conflictsToUse.filter(c => c.urgency === 'immediate').length;
    const urgentConflicts = conflictsToUse.filter(c => c.urgency === 'urgent').length;
    
    if (immediateConflicts > 0) return { status: 'error', message: `${immediateConflicts} immediate conflicts require attention` };
    if (urgentConflicts > 0) return { status: 'warning', message: `${urgentConflicts} urgent conflicts detected` };
    if (conflictsToUse.length > 0) return { status: 'info', message: `${conflictsToUse.length} conflicts being monitored` };
    return { status: 'success', message: 'No active conflicts detected' };
  };

  const overallStatus = getOverallSystemStatus();

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h6" component="h3">
            Conflict Analysis & Resolution
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
          icon={getUrgencyIcon(overallStatus.status === 'error' ? 'immediate' : 'normal')}
          sx={{ mb: 2 }}
        >
          {overallStatus.message}
        </Alert>

        {/* Conflicts Table */}
        <TableContainer component={Paper} variant="outlined">
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Conflict</TableCell>
                <TableCell>Severity</TableCell>
                <TableCell>Urgency</TableCell>
                <TableCell>Time to Conflict</TableCell>
                <TableCell>Distance</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {conflictsToUse.map((conflict) => {
                const ac1 = getAircraftInfo(conflict.aircraft1);
                const ac2 = getAircraftInfo(conflict.aircraft2);
                
                return (
                  <React.Fragment key={conflict.id}>
                    <TableRow hover>
                      <TableCell>
                        <Box>
                          <Typography variant="body2" fontWeight="bold">
                            {ac1.callsign} ↔ {ac2.callsign}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            {ac1?.type || 'Unknown'} vs {ac2?.type || 'Unknown'} • Sector {conflict.sector || 'Unknown'}
                          </Typography>
                        </Box>
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={(conflict.severity || 'unknown').toUpperCase()}
                          color={getSeverityColor(conflict.severity)}
                          size="small"
                        />
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={(conflict.urgency || 'normal').toUpperCase()}
                          color={getUrgencyColor(conflict.urgency)}
                          size="small"
                          icon={getUrgencyIcon(conflict.urgency)}
                        />
                      </TableCell>
                      <TableCell>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <Typography variant="body2">
                            {conflict.timeToConflict || 0}s
                          </Typography>
                          <LinearProgress
                            variant="determinate"
                            value={Math.min(100, ((conflict.timeToConflict || 0) / 300) * 100)}
                            color={getUrgencyColor(conflict.urgency)}
                            sx={{ width: 40, height: 4, borderRadius: 2 }}
                          />
                        </Box>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body2">
                          {(conflict.distance || 0).toFixed(1)} NM
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <IconButton
                          size="small"
                          onClick={() => toggleConflictExpansion(conflict.id)}
                        >
                          {expandedConflict === conflict.id ? <ExpandLessIcon /> : <ExpandMoreIcon />}
                        </IconButton>
                      </TableCell>
                    </TableRow>
                    
                    {/* Expanded Conflict Details */}
                    <TableRow>
                      <TableCell colSpan={6} sx={{ p: 0 }}>
                        <Collapse in={expandedConflict === conflict.id} timeout="auto" unmountOnExit>
                          <Box sx={{ p: 2, bgcolor: 'background.default' }}>
                            <Grid container spacing={2}>
                              {/* Conflict Evolution Timeline */}
                              <Grid item xs={12} md={6}>
                                <Typography variant="subtitle2" gutterBottom>
                                  <TimelineIcon sx={{ mr: 1, fontSize: 16 }} />
                                  Conflict Evolution
                                </Typography>
                                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                                  {(conflict.predictedEvolution || []).map((evolution, index) => (
                                    <Box key={index} sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                      <Box
                                        sx={{
                                          width: 12,
                                          height: 12,
                                          borderRadius: '50%',
                                          bgcolor: getRiskColor(evolution.risk),
                                        }}
                                      />
                                      <Typography variant="caption">
                                        T+{evolution.time}s: {evolution.distance.toFixed(1)}NM, {evolution.altitude}ft
                                      </Typography>
                                      <Chip
                                        label={evolution.risk}
                                        size="small"
                                        color={evolution.risk === 'critical' ? 'error' : 'warning'}
                                      />
                                    </Box>
                                  ))}
                                </Box>
                              </Grid>

                              {/* Resolution Suggestions */}
                              <Grid item xs={12} md={6}>
                                <Typography variant="subtitle2" gutterBottom>
                                  <SpeedIcon sx={{ mr: 1, fontSize: 16 }} />
                                  Resolution Suggestions
                                </Typography>
                                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                                  {(conflict.resolutionSuggestions || []).map((suggestion, index) => (
                                    <Box key={index} sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                      <Chip
                                        label={(suggestion.type || 'unknown').toUpperCase()}
                                        size="small"
                                        color={getPriorityColor(suggestion.priority)}
                                        variant="outlined"
                                      />
                                      <Typography variant="body2" sx={{ flex: 1 }}>
                                        {suggestion.action}
                                      </Typography>
                                      <Chip
                                        label={suggestion.priority}
                                        size="small"
                                        color={getPriorityColor(suggestion.priority)}
                                      />
                                    </Box>
                                  ))}
                                </Box>
                              </Grid>

                              {/* Controller Information */}
                              <Grid item xs={12}>
                                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                  <Typography variant="caption" color="text.secondary">
                                    Controller: {conflict.controller || 'Unassigned'} • Last Update: {conflict.lastUpdate ? conflict.lastUpdate.toLocaleTimeString() : 'Unknown'}
                                  </Typography>
                                  <Button
                                    variant="outlined"
                                    size="small"
                                    startIcon={<HeightIcon />}
                                  >
                                    Apply Resolution
                                  </Button>
                                </Box>
                              </Grid>
                            </Grid>
                          </Box>
                        </Collapse>
                      </TableCell>
                    </TableRow>
                  </React.Fragment>
                );
              })}
            </TableBody>
          </Table>
        </TableContainer>

        {/* Summary Statistics */}
        <Box sx={{ mt: 2, p: 2, bgcolor: 'background.default', borderRadius: 1 }}>
          <Typography variant="subtitle2" gutterBottom>
            Conflict Summary
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={6} md={3}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h6" color="error.main">
                  {conflictsToUse.filter(c => c.urgency === 'immediate').length}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Immediate
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={6} md={3}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h6" color="warning.main">
                  {conflictsToUse.filter(c => c.urgency === 'urgent').length}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Urgent
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={6} md={3}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h6" color="info.main">
                  {conflictsToUse.filter(c => c.urgency === 'high').length}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  High Priority
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={6} md={3}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h6" color="primary">
                  {conflictsToUse.length}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Total Conflicts
                </Typography>
              </Box>
            </Grid>
          </Grid>
        </Box>
      </CardContent>
    </Card>
  );
};

export default ConflictAnalysisPanel;

