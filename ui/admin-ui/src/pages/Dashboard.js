import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Chip,
  LinearProgress,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  Flight as FlightIcon,
  Warning as WarningIcon,
  Speed as SpeedIcon,
  Memory as MemoryIcon,
  Refresh as RefreshIcon,
} from '@mui/icons-material';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer } from 'recharts';
import StatusCard from '../components/StatusCard';
import MetricCard from '../components/MetricCard';
import AlertPanel from '../components/AlertPanel';

const Dashboard = () => {
  const [systemStatus, setSystemStatus] = useState({
    aircraftCount: 42,
    activeConflicts: 3,
    systemLoad: 67,
    memoryUsage: 78,
    messageRate: 1250,
  });

  const [performanceData, setPerformanceData] = useState([
    { time: '00:00', messages: 1200, conflicts: 2, load: 65 },
    { time: '00:05', messages: 1350, conflicts: 3, load: 68 },
    { time: '00:10', messages: 1280, conflicts: 1, load: 66 },
    { time: '00:15', messages: 1420, conflicts: 4, load: 70 },
    { time: '00:20', messages: 1380, conflicts: 2, load: 69 },
    { time: '00:25', messages: 1450, conflicts: 3, load: 71 },
  ]);

  const [alerts, setAlerts] = useState([
    { id: 1, type: 'warning', message: 'Aircraft AA123 approaching minimum separation', timestamp: new Date() },
    { id: 2, type: 'error', message: 'Sector 3 workload exceeds threshold', timestamp: new Date() },
    { id: 3, type: 'info', message: 'Weather radar data updated', timestamp: new Date() },
  ]);

  const handleRefresh = () => {
    // Simulate data refresh
    setSystemStatus(prev => ({
      ...prev,
      aircraftCount: Math.floor(Math.random() * 50) + 30,
      activeConflicts: Math.floor(Math.random() * 5) + 1,
      systemLoad: Math.floor(Math.random() * 30) + 50,
    }));
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          System Overview
        </Typography>
        <Tooltip title="Refresh Data">
          <IconButton onClick={handleRefresh} color="primary">
            <RefreshIcon />
          </IconButton>
        </Tooltip>
      </Box>

      {/* Key Metrics Row */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Active Aircraft"
            value={systemStatus.aircraftCount}
            icon={<FlightIcon />}
            color="primary"
            trend="+5%"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Active Conflicts"
            value={systemStatus.activeConflicts}
            icon={<WarningIcon />}
            color="error"
            trend="-2"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="System Load"
            value={`${systemStatus.systemLoad}%`}
            icon={<SpeedIcon />}
            color="warning"
            trend="+3%"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Memory Usage"
            value={`${systemStatus.memoryUsage}%`}
            icon={<MemoryIcon />}
            color="info"
            trend="+1%"
          />
        </Grid>
      </Grid>

      {/* Main Content Grid */}
      <Grid container spacing={3}>
        {/* Performance Chart */}
        <Grid item xs={12} lg={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                System Performance Trends
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={performanceData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="time" />
                  <YAxis />
                  <RechartsTooltip />
                  <Line type="monotone" dataKey="messages" stroke="#2196f3" name="Messages/sec" />
                  <Line type="monotone" dataKey="load" stroke="#ff9800" name="System Load %" />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* Alerts Panel */}
        <Grid item xs={12} lg={4}>
          <AlertPanel alerts={alerts} />
        </Grid>

        {/* System Status */}
        <Grid item xs={12} md={6}>
          <StatusCard
            title="System Health"
            status="healthy"
            details={[
              { label: 'Core Engine', status: 'online', color: 'success' },
              { label: 'Conflict Detection', status: 'online', color: 'success' },
              { label: 'Weather Service', status: 'online', color: 'success' },
              { label: 'Database', status: 'online', color: 'success' },
            ]}
          />
        </Grid>

        {/* Message Throughput */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Message Throughput
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Typography variant="h4" component="span" sx={{ mr: 1 }}>
                  {systemStatus.messageRate}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  msg/sec
                </Typography>
              </Box>
              <LinearProgress 
                variant="determinate" 
                value={(systemStatus.messageRate / 2000) * 100} 
                sx={{ height: 8, borderRadius: 4 }}
              />
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                Target: 2000 msg/sec
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;
