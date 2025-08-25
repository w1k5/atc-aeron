import React, { useState } from 'react';
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
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from '@mui/material';
import {
  Refresh as RefreshIcon,
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  Speed as SpeedIcon,
} from '@mui/icons-material';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';

const PerformanceMetrics = () => {
  const [timeRange, setTimeRange] = useState('1h');
  const [selectedMetric, setSelectedMetric] = useState('messages');

  const [metrics, setMetrics] = useState({
    messageRate: 1250,
    avgLatency: 12.5,
    throughput: 98.7,
    errorRate: 0.3,
    activeConnections: 156,
  });

  const [performanceData, setPerformanceData] = useState([
    { time: '00:00', messages: 1200, latency: 12, errors: 2, connections: 150 },
    { time: '00:05', messages: 1350, latency: 11, errors: 1, connections: 155 },
    { time: '00:10', messages: 1280, latency: 13, errors: 3, connections: 152 },
    { time: '00:15', messages: 1420, latency: 10, errors: 1, connections: 158 },
    { time: '00:20', messages: 1380, latency: 12, errors: 2, connections: 156 },
    { time: '00:25', messages: 1450, latency: 11, errors: 1, connections: 160 },
  ]);

  const [componentMetrics, setComponentMetrics] = useState([
    { name: 'Conflict Detection', messages: 450, latency: 8.5, errors: 0, health: 95 },
    { name: 'Aircraft Tracking', messages: 320, latency: 15.2, errors: 2, health: 88 },
    { name: 'Weather Service', messages: 180, latency: 25.1, errors: 1, health: 92 },
    { name: 'Message Broker', messages: 300, latency: 5.3, errors: 3, health: 73 },
  ]);

  const handleRefresh = () => {
    // Simulate data refresh
    setMetrics(prev => ({
      ...prev,
      messageRate: Math.floor(Math.random() * 500) + 1000,
      avgLatency: (Math.random() * 10 + 8).toFixed(1),
      throughput: (Math.random() * 10 + 90).toFixed(1),
      errorRate: (Math.random() * 1).toFixed(1),
      activeConnections: Math.floor(Math.random() * 50) + 130,
    }));
  };

  const getTrendIcon = (current, previous) => {
    if (current > previous) return <TrendingUpIcon color="success" />;
    if (current < previous) return <TrendingDownIcon color="error" />;
    return null;
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Performance Metrics
        </Typography>
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>Time Range</InputLabel>
            <Select
              value={timeRange}
              label="Time Range"
              onChange={(e) => setTimeRange(e.target.value)}
            >
              <MenuItem value="15m">Last 15 min</MenuItem>
              <MenuItem value="1h">Last hour</MenuItem>
              <MenuItem value="6h">Last 6 hours</MenuItem>
              <MenuItem value="24h">Last 24 hours</MenuItem>
            </Select>
          </FormControl>
          <Tooltip title="Refresh Data">
            <IconButton onClick={handleRefresh} color="primary">
              <RefreshIcon />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      {/* Key Performance Indicators */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <SpeedIcon color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Message Rate</Typography>
              </Box>
              <Typography variant="h4" gutterBottom>
                {metrics.messageRate.toLocaleString()}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                messages/sec
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Avg Latency
              </Typography>
              <Typography variant="h4" gutterBottom>
                {metrics.avgLatency}ms
              </Typography>
              <Typography variant="body2" color="text.secondary">
                response time
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Throughput
              </Typography>
              <Typography variant="h4" gutterBottom>
                {metrics.throughput}%
              </Typography>
              <Typography variant="body2" color="text.secondary">
                system efficiency
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Error Rate
              </Typography>
              <Typography variant="h4" gutterBottom>
                {metrics.errorRate}%
              </Typography>
              <Typography variant="body2" color="text.secondary">
                failure rate
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        {/* Performance Charts */}
        <Grid item xs={12} lg={8}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6">
                  Performance Trends
                </Typography>
                <FormControl size="small" sx={{ minWidth: 120 }}>
                  <Select
                    value={selectedMetric}
                    onChange={(e) => setSelectedMetric(e.target.value)}
                  >
                    <MenuItem value="messages">Messages/sec</MenuItem>
                    <MenuItem value="latency">Latency (ms)</MenuItem>
                    <MenuItem value="errors">Errors</MenuItem>
                    <MenuItem value="connections">Connections</MenuItem>
                  </Select>
                </FormControl>
              </Box>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={performanceData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="time" />
                  <YAxis />
                  <RechartsTooltip />
                  <Line type="monotone" dataKey={selectedMetric} stroke="#2196f3" />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* Component Performance */}
        <Grid item xs={12} lg={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Component Performance
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={componentMetrics}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" angle={-45} textAnchor="end" height={80} />
                  <YAxis />
                  <RechartsTooltip />
                  <Bar dataKey="health" fill="#4caf50" />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* Detailed Component Metrics */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Component Metrics
              </Typography>
              <TableContainer component={Paper} variant="outlined">
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Component</TableCell>
                      <TableCell align="right">Messages/sec</TableCell>
                      <TableCell align="right">Latency (ms)</TableCell>
                      <TableCell align="right">Errors</TableCell>
                      <TableCell align="right">Health (%)</TableCell>
                      <TableCell align="right">Status</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {componentMetrics.map((component, index) => (
                      <TableRow key={index}>
                        <TableCell>
                          <Typography variant="body2" fontWeight="medium">
                            {component.name}
                          </Typography>
                        </TableCell>
                        <TableCell align="right">
                          {component.messages.toLocaleString()}
                        </TableCell>
                        <TableCell align="right">
                          {component.latency}
                        </TableCell>
                        <TableCell align="right">
                          <Chip
                            label={component.errors}
                            color={component.errors === 0 ? 'success' : 'error'}
                            size="small"
                          />
                        </TableCell>
                        <TableCell align="right">
                          <Chip
                            label={`${component.health}%`}
                            color={component.health >= 90 ? 'success' : component.health >= 70 ? 'warning' : 'error'}
                            size="small"
                          />
                        </TableCell>
                        <TableCell align="right">
                          <Chip
                            label={component.health >= 70 ? 'Healthy' : 'Warning'}
                            color={component.health >= 70 ? 'success' : 'warning'}
                            size="small"
                            variant="outlined"
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
      </Grid>
    </Box>
  );
};

export default PerformanceMetrics;
