import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Box } from '@mui/material';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import AircraftMonitor from './pages/AircraftMonitor';
import SystemHealth from './pages/SystemHealth';
import PerformanceMetrics from './pages/PerformanceMetrics';
import Configuration from './pages/Configuration';

function App() {
  const [mode, setMode] = useState(() => {
    // Check localStorage for saved preference, default to dark
    const saved = localStorage.getItem('theme-mode');
    return saved || 'dark';
  });

  // Create theme function
  const createAppTheme = (mode) => createTheme({
    palette: {
      mode,
      primary: {
        main: '#2196f3',
      },
      secondary: {
        main: '#f50057',
      },
      background: {
        default: mode === 'dark' ? '#0a0a0a' : '#f5f5f5',
        paper: mode === 'dark' ? '#1e1e1e' : '#ffffff',
      },
      text: {
        primary: mode === 'dark' ? '#ffffff' : '#000000',
        secondary: mode === 'dark' ? '#b0b0b0' : '#666666',
      },
      divider: mode === 'dark' ? '#333' : '#e0e0e0',
    },
    typography: {
      fontFamily: 'Roboto, Arial, sans-serif',
      h4: {
        fontWeight: 600,
      },
      h6: {
        fontWeight: 500,
      },
    },
    components: {
      MuiCard: {
        styleOverrides: {
          root: {
            backgroundColor: mode === 'dark' ? '#1e1e1e' : '#ffffff',
            border: `1px solid ${mode === 'dark' ? '#333' : '#e0e0e0'}`,
          },
        },
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            backgroundColor: mode === 'dark' ? '#1e1e1e' : '#ffffff',
          },
        },
      },
      MuiAppBar: {
        styleOverrides: {
          root: {
            backgroundColor: mode === 'dark' ? '#1e1e1e' : '#ffffff',
            color: mode === 'dark' ? '#ffffff' : '#000000',
            borderBottom: `1px solid ${mode === 'dark' ? '#333' : '#e0e0e0'}`,
          },
        },
      },
      MuiDrawer: {
        styleOverrides: {
          paper: {
            backgroundColor: mode === 'dark' ? '#1e1e1e' : '#ffffff',
            borderRight: `1px solid ${mode === 'dark' ? '#333' : '#e0e0e0'}`,
          },
        },
      },
    },
  });

  const theme = createAppTheme(mode);

  const toggleTheme = () => {
    const newMode = mode === 'dark' ? 'light' : 'dark';
    setMode(newMode);
    localStorage.setItem('theme-mode', newMode);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Box sx={{ display: 'flex', height: '100vh' }}>
          <Layout onThemeToggle={toggleTheme} currentMode={mode}>
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/aircraft" element={<AircraftMonitor />} />
              <Route path="/health" element={<SystemHealth />} />
              <Route path="/performance" element={<PerformanceMetrics />} />
              <Route path="/config" element={<Configuration />} />
            </Routes>
          </Layout>
        </Box>
      </Router>
    </ThemeProvider>
  );
}

export default App;
