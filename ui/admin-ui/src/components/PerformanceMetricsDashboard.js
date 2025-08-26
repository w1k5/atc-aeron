import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  LinearProgress,
  Chip,
  Alert,
  IconButton,
  Tooltip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from '@mui/material';
import {
  Refresh as RefreshIcon,
  Speed as SpeedIcon,
  Timer as TimerIcon,
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  CheckCircle as CheckCircleIcon,
  Error as ErrorIcon,
  Warning as WarningIcon,
} from '@mui/icons-material';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer, Area, AreaChart } from 'recharts';

const PerformanceMetricsDashboard = ({ onRefresh }) => {
  const [metrics, setMetrics] = useState({
    systemHealth: 'excellent',
    responseTime: 45,
    throughput: 1250,
    conflictResolutionRate: 94.2,
    sectorBalancingEfficiency: 87.5,
    decisionAccuracy: 96.8,
    lastUpdate: new Date(),
  });

  const [performanceHistory, setPerformanceHistory] = useState([]);
  const [systemAlerts, setSystemAlerts] = useState([]);

  // Sample performance data - in real app this would come from backend
  const samplePerformanceData = [
    { time: '00:00', responseTime: 45, throughput: 1250, conflicts: 3, accuracy: 96.8 },
    { time: '00:05', responseTime: 42, throughput: 1280, conflicts: 2, accuracy: 97.1 },
    { time: '00:10', responseTime: 48, throughput: 1220, conflicts: 4, accuracy: 96.5 },
    { time: '00:15', responseTime: 51, throughput: 1190, conflicts: 5, accuracy: 96.2 },
    { time: '00:20', responseTime: 44, throughput: 1260, conflicts: 3, accuracy: 96.9 },
    { time: '00:25', responseTime: 47, throughput: 1230, conflicts: 4, accuracy: 96.6 },
    { time: '00:30', responseTime: 43, throughput: 1270, conflicts: 2, accuracy: 97.0 },
  ];

  const sampleSystemAlerts = [
    {
      id: 1,
      type: 'warning',
      message: 'Sector N91 approaching capacity threshold',
      timestamp: new Date(Date.now() - 5 * 60 * 1000),
      priority: 'medium',
    },
    {
      id: 2,
      type: 'info',
      message: 'Conflict resolution rate improved by 2.3%',
      timestamp: new Date(Date.now() - 15 * 60 * 1000),
      priority: 'low',
    },
    {
      id: 3,
      type: 'success',
      message: 'All sectors operating within optimal parameters',
      timestamp: new Date(Date.now() - 30 * 60 * 1000),
      priority: 'low',
    },
  ];

  useEffect(() => {
    setPerformanceHistory(samplePerformanceData);
    setSystemAlerts(sampleSystemAlerts);
  }, []);

  const getHealthColor = (health) => {
    switch (health) {
      case 'excellent': return 'success';
      case 'good': return 'success';
      case 'fair': return 'warning';
      case 'poor': return 'error';
      default: return 'info';
    }
  };

  const getHealthIcon = (health) => {
    switch (health) {
      case 'excellent': return <CheckCircleIcon />;
      case 'good': return <CheckCircleIcon />;
      case 'fair': return <WarningIcon />;
      case 'poor': return <ErrorIcon />;
      default: return <WarningIcon />;
    }
  };

  const getPerformanceColor = (value, threshold) => {
    if (value >= threshold * 0.9) return 'success';
    if (value >= threshold * 0.7) return 'warning';
    return 'error';
  };

  const getAlertColor = (type) => {
    switch (type) {
      case 'error': return 'error';
      case 'warning': return 'warning';
      case 'info': return 'info';
      case 'success': return 'success';
      default: return 'info';
    }
  };

  const getAlertIcon = (type) => {
    switch (type) {
      case 'error': return <ErrorIcon />;
      case 'warning': return <WarningIcon />;
      case 'info': return <CheckCircleIcon />;
      case 'success': return <CheckCircleIcon />;
      default: return <CheckCircleIcon />;
    }
  };

  const formatTime = (date) => {
    return date.toLocaleTimeString();
  };

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h6" component="h3">
            Performance Metrics Dashboard
          </Typography>
          <Tooltip title="Refresh Metrics">
            <IconButton onClick={onRefresh} size="small">
              <RefreshIcon />
            </IconButton>
          </Tooltip>
        </Box>

        {/* Overall System Health */}
        <Alert 
          severity={getHealthColor(metrics.systemHealth)} 
          icon={getHealthIcon(metrics.systemHealth)}
          sx={{ mb: 2 }}
        >
          System Health: {metrics.systemHealth.toUpperCase()} • Last Update: {formatTime(metrics.lastUpdate)}
        </Alert>

        {/* Key Performance Indicators */}
        <Grid container spacing={2} sx={{ mb: 3 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Card variant="outlined">
              <CardContent sx={{ textAlign: 'center' }}>
                <SpeedIcon sx={{ fontSize: 40, color: 'primary.main', mb: 1 }} />
                <Typography variant="h4" color="primary">
                  {metrics.responseTime}ms
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Response Time
                </Typography>
                <LinearProgress
                  variant="determinate"
                  value={Math.min(100, (100 - metrics.responseTime) / 100 * 100)}
                  color={getPerformanceColor(metrics.responseTime, 50)}
                  sx={{ mt: 1 }}
                />
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card variant="outlined">
              <CardContent sx={{ textAlign: 'center' }}>
                <TrendingUpIcon sx={{ fontSize: 40, color: 'success.main', mb: 1 }} />
                <Typography variant="h4" color="success.main">
                  {metrics.throughput}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Aircraft/min
                </Typography>
                <LinearProgress
                  variant="determinate"
                  value={Math.min(100, (metrics.throughput / 1500) * 100)}
                  color={getPerformanceColor(metrics.throughput, 1200)}
                  sx={{ mt: 1 }}
                />
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card variant="outlined">
              <CardContent sx={{ textAlign: 'center' }}>
                <CheckCircleIcon sx={{ fontSize: 40, color: 'success.main', mb: 1 }} />
                <Typography variant="h4" color="success.main">
                  {metrics.conflictResolutionRate}%
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Conflict Resolution
                </Typography>
                <LinearProgress
                  variant="determinate"
                  value={metrics.conflictResolutionRate}
                  color={getPerformanceColor(metrics.conflictResolutionRate, 90)}
                  sx={{ mt: 1 }}
                />
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card variant="outlined">
              <CardContent sx={{ textAlign: 'center' }}>
                <TimerIcon sx={{ fontSize: 40, color: 'info.main', mb: 1 }} />
                <Typography variant="h4" color="info.main">
                  {metrics.decisionAccuracy}%
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Decision Accuracy
                </Typography>
                <LinearProgress
                  variant="determinate"
                  value={metrics.decisionAccuracy}
                  color={getPerformanceColor(metrics.decisionAccuracy, 95)}
                  sx={{ mt: 1 }}
                />
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Performance Charts */}
        <Grid container spacing={2} sx={{ mb: 3 }}>
          <Grid item xs={12} md={6}>
            <Card variant="outlined">
              <CardContent>
                <Typography variant="subtitle2" gutterBottom>
                  Response Time Trend
                </Typography>
                <ResponsiveContainer width="100%" height={200}>
                  <LineChart data={performanceHistory}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="time" />
                    <YAxis />
                    <RechartsTooltip />
                    <Line 
                      type="monotone" 
                      dataKey="responseTime" 
                      stroke="#1976d2" 
                      strokeWidth={2}
                      dot={{ fill: '#1976d2' }}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={6}>
            <Card variant="outlined">
              <CardContent>
                <Typography variant="subtitle2" gutterBottom>
                  Throughput & Conflicts
                </Typography>
                <ResponsiveContainer width="100%" height={200}>
                  <AreaChart data={performanceHistory}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="time" />
                    <YAxis yAxisId="left" />
                    <YAxis yAxisId="right" orientation="right" />
                    <RechartsTooltip />
                    <Area 
                      yAxisId="left"
                      type="monotone" 
                      dataKey="throughput" 
                      stroke="#4caf50" 
                      fill="#4caf50" 
                      fillOpacity={0.3}
                    />
                    <Area 
                      yAxisId="right"
                      type="monotone" 
                      dataKey="conflicts" 
                      stroke="#f44336" 
                      fill="#f44336" 
                      fillOpacity={0.3}
                    />
                  </AreaChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* System Alerts */}
        <Card variant="outlined" sx={{ mb: 2 }}>
          <CardContent>
            <Typography variant="subtitle2" gutterBottom>
              System Alerts & Notifications
            </Typography>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Type</TableCell>
                    <TableCell>Message</TableCell>
                    <TableCell>Priority</TableCell>
                    <TableCell>Timestamp</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {systemAlerts.map((alert) => (
                    <TableRow key={alert.id} hover>
                      <TableCell>
                        <Chip
                          label={alert.type.toUpperCase()}
                          color={getAlertColor(alert.type)}
                          size="small"
                          icon={getAlertIcon(alert.type)}
                        />
                      </TableCell>
                      <TableCell>
                        <Typography variant="body2">
                          {alert.message}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={alert.priority}
                          size="small"
                          variant="outlined"
                          color={alert.priority === 'high' ? 'error' : 'default'}
                        />
                      </TableCell>
                      <TableCell>
                        <Typography variant="caption" color="text.secondary">
                          {formatTime(alert.timestamp)}
                        </Typography>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </CardContent>
        </Card>

        {/* Performance Summary */}
        <Box sx={{ p: 2, bgcolor: 'background.default', borderRadius: 1 }}>
          <Typography variant="subtitle2" gutterBottom>
            Performance Summary
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={6} md={3}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h6" color="success.main">
                  {performanceHistory.length > 0 ? performanceHistory[performanceHistory.length - 1].accuracy : 0}%
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Current Accuracy
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={6} md={3}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h6" color="primary">
                  {performanceHistory.length > 0 ? performanceHistory[performanceHistory.length - 1].throughput : 0}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Current Throughput
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={6} md={3}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h6" color="warning.main">
                  {performanceHistory.length > 0 ? performanceHistory[performanceHistory.length - 1].conflicts : 0}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Active Conflicts
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={6} md={3}>
              <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h6" color="info.main">
                  {systemAlerts.filter(a => a.type === 'warning' || a.type === 'error').length}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  Active Alerts
                </Typography>
              </Box>
            </Grid>
          </Grid>
        </Box>
      </CardContent>
    </Card>
  );
};

export default PerformanceMetricsDashboard;
