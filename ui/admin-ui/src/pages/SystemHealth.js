import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  LinearProgress,
  Chip,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  CheckCircle as CheckIcon,
  Error as ErrorIcon,
  Warning as WarningIcon,
  Refresh as RefreshIcon,
  Memory as MemoryIcon,
  Storage as StorageIcon,
  Speed as SpeedIcon,
  NetworkCheck as NetworkIcon,
} from '@mui/icons-material';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer } from 'recharts';

const SystemHealth = () => {
  const [systemMetrics, setSystemMetrics] = useState({
    cpu: 67,
    memory: 78,
    disk: 45,
    network: 82,
    uptime: '3d 14h 23m',
  });

  const [services, setServices] = useState([
    { name: 'Core ATC Engine', status: 'online', health: 95, lastCheck: '2s ago' },
    { name: 'Conflict Detection', status: 'online', health: 88, lastCheck: '1s ago' },
    { name: 'Weather Service', status: 'online', health: 92, lastCheck: '5s ago' },
    { name: 'Database Cluster', status: 'online', health: 97, lastCheck: '1s ago' },
    { name: 'Message Broker', status: 'warning', health: 73, lastCheck: '3s ago' },
    { name: 'Admin Gateway', status: 'online', health: 100, lastCheck: '1s ago' },
  ]);

  const [performanceData, setPerformanceData] = useState([
    { time: '00:00', cpu: 65, memory: 75, disk: 45 },
    { time: '00:05', cpu: 68, memory: 77, disk: 45 },
    { time: '00:10', cpu: 70, memory: 78, disk: 46 },
    { time: '00:15', cpu: 72, memory: 79, disk: 46 },
    { time: '00:20', cpu: 69, memory: 77, disk: 45 },
    { time: '00:25', cpu: 67, memory: 76, disk: 45 },
  ]);

  const handleRefresh = () => {
    // Simulate data refresh
    setSystemMetrics(prev => ({
      ...prev,
      cpu: Math.floor(Math.random() * 30) + 50,
      memory: Math.floor(Math.random() * 20) + 70,
      disk: Math.floor(Math.random() * 20) + 40,
      network: Math.floor(Math.random() * 20) + 75,
    }));
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'online':
        return 'success';
      case 'warning':
        return 'warning';
      case 'offline':
        return 'error';
      default:
        return 'default';
    }
  };

  const getHealthColor = (health) => {
    if (health >= 90) return 'success';
    if (health >= 70) return 'warning';
    return 'error';
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          System Health
        </Typography>
        <Tooltip title="Refresh Data">
          <IconButton onClick={handleRefresh} color="primary">
            <RefreshIcon />
          </IconButton>
        </Tooltip>
      </Box>

      {/* System Overview Cards */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <SpeedIcon color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">CPU Usage</Typography>
              </Box>
              <Typography variant="h4" gutterBottom>
                {systemMetrics.cpu}%
              </Typography>
              <LinearProgress
                variant="determinate"
                value={systemMetrics.cpu}
                color={systemMetrics.cpu > 80 ? 'error' : systemMetrics.cpu > 60 ? 'warning' : 'success'}
                sx={{ height: 8, borderRadius: 4 }}
              />
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <MemoryIcon color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Memory</Typography>
              </Box>
              <Typography variant="h4" gutterBottom>
                {systemMetrics.memory}%
              </Typography>
              <LinearProgress
                variant="determinate"
                value={systemMetrics.memory}
                color={systemMetrics.memory > 85 ? 'error' : systemMetrics.memory > 70 ? 'warning' : 'success'}
                sx={{ height: 8, borderRadius: 4 }}
              />
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <StorageIcon color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Disk</Typography>
              </Box>
              <Typography variant="h4" gutterBottom>
                {systemMetrics.disk}%
              </Typography>
              <LinearProgress
                variant="determinate"
                value={systemMetrics.disk}
                color={systemMetrics.disk > 80 ? 'error' : systemMetrics.disk > 60 ? 'warning' : 'success'}
                sx={{ height: 8, borderRadius: 4 }}
              />
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <NetworkIcon color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Network</Typography>
              </Box>
              <Typography variant="h4" gutterBottom>
                {systemMetrics.network}%
              </Typography>
              <LinearProgress
                variant="determinate"
                value={systemMetrics.network}
                color={systemMetrics.network > 80 ? 'error' : systemMetrics.network > 60 ? 'warning' : 'success'}
                sx={{ height: 8, borderRadius: 4 }}
              />
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        {/* Performance Chart */}
        <Grid item xs={12} lg={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Resource Usage Trends
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={performanceData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="time" />
                  <YAxis />
                  <RechartsTooltip />
                  <Line type="monotone" dataKey="cpu" stroke="#2196f3" name="CPU %" />
                  <Line type="monotone" dataKey="memory" stroke="#ff9800" name="Memory %" />
                  <Line type="monotone" dataKey="disk" stroke="#4caf50" name="Disk %" />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* System Uptime */}
        <Grid item xs={12} lg={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                System Information
              </Typography>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  Uptime
                </Typography>
                <Typography variant="h5">
                  {systemMetrics.uptime}
                </Typography>
              </Box>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  Last Restart
                </Typography>
                <Typography variant="body1">
                  3 days ago
                </Typography>
              </Box>
              <Box>
                <Typography variant="body2" color="text.secondary">
                  Version
                </Typography>
                <Typography variant="body1">
                  v0.1.0-SNAPSHOT
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Service Status */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Service Status
              </Typography>
              <List>
                {services.map((service, index) => (
                  <ListItem key={index} divider={index < services.length - 1}>
                    <ListItemIcon>
                      {service.status === 'online' ? (
                        <CheckIcon color="success" />
                      ) : service.status === 'warning' ? (
                        <WarningIcon color="warning" />
                      ) : (
                        <ErrorIcon color="error" />
                      )}
                    </ListItemIcon>
                    <ListItemText
                      primary={service.name}
                      secondary={`Last check: ${service.lastCheck}`}
                    />
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Chip
                        label={service.status}
                        color={getStatusColor(service.status)}
                        size="small"
                        variant="outlined"
                      />
                      <Chip
                        label={`${service.health}%`}
                        color={getHealthColor(service.health)}
                        size="small"
                      />
                    </Box>
                  </ListItem>
                ))}
              </List>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default SystemHealth;




