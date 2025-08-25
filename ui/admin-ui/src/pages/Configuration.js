import React, { useState } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  TextField,
  Button,
  Switch,
  FormControlLabel,
  Slider,
  Divider,
  Alert,
  IconButton,
  Tooltip,
  Chip,
} from '@mui/material';
import {
  Save as SaveIcon,
  Refresh as RefreshIcon,
  PlayArrow as PlayIcon,
  Stop as StopIcon,
  Settings as SettingsIcon,
  Science as TestIcon,
} from '@mui/icons-material';

const Configuration = () => {
  const [config, setConfig] = useState({
    conflictDetection: {
      enabled: true,
      minSeparation: 5.0,
      altitudeThreshold: 1000,
      updateFrequency: 1000,
    },
    performance: {
      maxMessageRate: 2000,
      batchSize: 100,
      timeout: 5000,
      retryAttempts: 3,
    },
    alerts: {
      enabled: true,
      severity: 'medium',
      notificationSound: true,
      autoAcknowledge: false,
    },
  });

  const [testConfig, setTestConfig] = useState({
    messageCount: 100,
    messageSize: 1000,
    interval: 100,
    running: false,
  });

  const [saved, setSaved] = useState(false);
  const [testResults, setTestResults] = useState(null);

  const handleConfigChange = (section, key, value) => {
    setConfig(prev => ({
      ...prev,
      [section]: {
        ...prev[section],
        [key]: value,
      },
    }));
    setSaved(false);
  };

  const handleSave = () => {
    // Simulate saving configuration
    setTimeout(() => {
      setSaved(true);
      setTimeout(() => setSaved(false), 3000);
    }, 1000);
  };

  const handleTestStart = () => {
    setTestConfig(prev => ({ ...prev, running: true }));
    // Simulate test running
    setTimeout(() => {
      setTestResults({
        messagesSent: testConfig.messageCount,
        duration: Math.floor(Math.random() * 5000) + 1000,
        throughput: Math.floor(Math.random() * 1000) + 500,
        errors: Math.floor(Math.random() * 5),
      });
      setTestConfig(prev => ({ ...prev, running: false }));
    }, 3000);
  };

  const handleTestStop = () => {
    setTestConfig(prev => ({ ...prev, running: false }));
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          System Configuration
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="contained"
            startIcon={<SaveIcon />}
            onClick={handleSave}
            disabled={saved}
          >
            {saved ? 'Saved!' : 'Save Configuration'}
          </Button>
          <Tooltip title="Reset to Defaults">
            <IconButton onClick={() => window.location.reload()}>
              <RefreshIcon />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      {saved && (
        <Alert severity="success" sx={{ mb: 3 }}>
          Configuration saved successfully!
        </Alert>
      )}

      <Grid container spacing={3}>
        {/* Conflict Detection Settings */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <SettingsIcon sx={{ mr: 1 }} />
                <Typography variant="h6">
                  Conflict Detection
                </Typography>
              </Box>
              
              <FormControlLabel
                control={
                  <Switch
                    checked={config.conflictDetection.enabled}
                    onChange={(e) => handleConfigChange('conflictDetection', 'enabled', e.target.checked)}
                  />
                }
                label="Enable Conflict Detection"
                sx={{ mb: 2 }}
              />
              
              <TextField
                fullWidth
                label="Minimum Separation (nm)"
                type="number"
                value={config.conflictDetection.minSeparation}
                onChange={(e) => handleConfigChange('conflictDetection', 'minSeparation', parseFloat(e.target.value))}
                sx={{ mb: 2 }}
                disabled={!config.conflictDetection.enabled}
              />
              
              <TextField
                fullWidth
                label="Altitude Threshold (ft)"
                type="number"
                value={config.conflictDetection.altitudeThreshold}
                onChange={(e) => handleConfigChange('conflictDetection', 'altitudeThreshold', parseInt(e.target.value))}
                sx={{ mb: 2 }}
                disabled={!config.conflictDetection.enabled}
              />
              
              <TextField
                fullWidth
                label="Update Frequency (ms)"
                type="number"
                value={config.conflictDetection.updateFrequency}
                onChange={(e) => handleConfigChange('conflictDetection', 'updateFrequency', parseInt(e.target.value))}
                disabled={!config.conflictDetection.enabled}
              />
            </CardContent>
          </Card>
        </Grid>

        {/* Performance Settings */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Performance Settings
              </Typography>
              
              <TextField
                fullWidth
                label="Max Message Rate (msg/sec)"
                type="number"
                value={config.performance.maxMessageRate}
                onChange={(e) => handleConfigChange('performance', 'maxMessageRate', parseInt(e.target.value))}
                sx={{ mb: 2 }}
              />
              
              <TextField
                fullWidth
                label="Batch Size"
                type="number"
                value={config.performance.batchSize}
                onChange={(e) => handleConfigChange('performance', 'batchSize', parseInt(e.target.value))}
                sx={{ mb: 2 }}
              />
              
              <TextField
                fullWidth
                label="Timeout (ms)"
                type="number"
                value={config.performance.timeout}
                onChange={(e) => handleConfigChange('performance', 'timeout', parseInt(e.target.value))}
                sx={{ mb: 2 }}
              />
              
              <TextField
                fullWidth
                label="Retry Attempts"
                type="number"
                value={config.performance.retryAttempts}
                onChange={(e) => handleConfigChange('performance', 'retryAttempts', parseInt(e.target.value))}
              />
            </CardContent>
          </Card>
        </Grid>

        {/* Alert Settings */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Alert Configuration
              </Typography>
              
              <FormControlLabel
                control={
                  <Switch
                    checked={config.alerts.enabled}
                    onChange={(e) => handleConfigChange('alerts', 'enabled', e.target.checked)}
                  />
                }
                label="Enable Alerts"
                sx={{ mb: 2 }}
              />
              
              <TextField
                fullWidth
                select
                label="Alert Severity"
                value={config.alerts.severity}
                onChange={(e) => handleConfigChange('alerts', 'severity', e.target.value)}
                sx={{ mb: 2 }}
                disabled={!config.alerts.enabled}
              >
                <option value="low">Low</option>
                <option value="medium">Medium</option>
                <option value="high">High</option>
                <option value="critical">Critical</option>
              </TextField>
              
              <FormControlLabel
                control={
                  <Switch
                    checked={config.alerts.notificationSound}
                    onChange={(e) => handleConfigChange('alerts', 'notificationSound', e.target.checked)}
                    disabled={!config.alerts.enabled}
                  />
                }
                label="Notification Sound"
                sx={{ mb: 2 }}
              />
              
              <FormControlLabel
                control={
                  <Switch
                    checked={config.alerts.autoAcknowledge}
                    onChange={(e) => handleConfigChange('alerts', 'autoAcknowledge', e.target.checked)}
                    disabled={!config.alerts.enabled}
                  />
                }
                label="Auto-acknowledge Alerts"
              />
            </CardContent>
          </Card>
        </Grid>

        {/* Test Controls */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <TestIcon sx={{ mr: 1 }} />
                <Typography variant="h6">
                  Performance Testing
                </Typography>
              </Box>
              
              <TextField
                fullWidth
                label="Message Count"
                type="number"
                value={testConfig.messageCount}
                onChange={(e) => setTestConfig(prev => ({ ...prev, messageCount: parseInt(e.target.value) }))}
                sx={{ mb: 2 }}
                disabled={testConfig.running}
              />
              
              <TextField
                fullWidth
                label="Message Size (bytes)"
                type="number"
                value={testConfig.messageSize}
                onChange={(e) => setTestConfig(prev => ({ ...prev, messageSize: parseInt(e.target.value) }))}
                sx={{ mb: 2 }}
                disabled={testConfig.running}
              />
              
              <TextField
                fullWidth
                label="Interval (ms)"
                type="number"
                value={testConfig.interval}
                onChange={(e) => setTestConfig(prev => ({ ...prev, interval: parseInt(e.target.value) }))}
                sx={{ mb: 2 }}
                disabled={testConfig.running}
              />
              
              <Box sx={{ display: 'flex', gap: 1 }}>
                <Button
                  variant="contained"
                  color="success"
                  startIcon={<PlayIcon />}
                  onClick={handleTestStart}
                  disabled={testConfig.running}
                  fullWidth
                >
                  Start Test
                </Button>
                <Button
                  variant="contained"
                  color="error"
                  startIcon={<StopIcon />}
                  onClick={handleTestStop}
                  disabled={!testConfig.running}
                  fullWidth
                >
                  Stop Test
                </Button>
              </Box>
              
              {testConfig.running && (
                <Box sx={{ mt: 2, textAlign: 'center' }}>
                  <Chip label="Test Running..." color="info" />
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Test Results */}
        {testResults && (
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Test Results
                </Typography>
                <Grid container spacing={2}>
                  <Grid item xs={6} md={3}>
                    <Typography variant="body2" color="text.secondary">
                      Messages Sent
                    </Typography>
                    <Typography variant="h6">
                      {testResults.messagesSent.toLocaleString()}
                    </Typography>
                  </Grid>
                  <Grid item xs={6} md={3}>
                    <Typography variant="body2" color="text.secondary">
                      Duration
                    </Typography>
                    <Typography variant="h6">
                      {testResults.duration}ms
                    </Typography>
                  </Grid>
                  <Grid item xs={6} md={3}>
                    <Typography variant="body2" color="text.secondary">
                      Throughput
                    </Typography>
                    <Typography variant="h6">
                      {testResults.throughput} msg/sec
                    </Typography>
                  </Grid>
                  <Grid item xs={6} md={3}>
                    <Typography variant="body2" color="text.secondary">
                      Errors
                    </Typography>
                    <Typography variant="h6" color={testResults.errors > 0 ? 'error' : 'success'}>
                      {testResults.errors}
                    </Typography>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>
        )}
      </Grid>
    </Box>
  );
};

export default Configuration;
