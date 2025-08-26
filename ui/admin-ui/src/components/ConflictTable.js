import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Box,
} from '@mui/material';
import { Warning as WarningIcon } from '@mui/icons-material';

const ConflictTable = ({ conflicts = [] }) => {
  const getSeverityColor = (severity) => {
    switch (severity) {
      case 'high':
        return 'error';
      case 'medium':
        return 'warning';
      case 'low':
        return 'info';
      default:
        return 'default';
    }
  };

  const getConflictTypeIcon = (type) => {
    switch (type) {
      case 'separation':
        return <WarningIcon color="error" />;
      case 'altitude':
        return <WarningIcon color="warning" />;
      case 'speed':
        return <WarningIcon color="info" />;
      default:
        return <WarningIcon />;
    }
  };

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <WarningIcon sx={{ mr: 1 }} />
          <Typography variant="h6">
            Active Conflicts
          </Typography>
          {conflicts.length > 0 && (
            <Chip
              label={conflicts.length}
              color="error"
              size="small"
              sx={{ ml: 'auto' }}
            />
          )}
        </Box>

        {conflicts.length === 0 ? (
          <Typography variant="body2" color="text.secondary" sx={{ textAlign: 'center', py: 2 }}>
            No active conflicts
          </Typography>
        ) : (
          <TableContainer component={Paper} variant="outlined">
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Aircraft</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Distance</TableCell>
                  <TableCell>Time</TableCell>
                  <TableCell>Severity</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {conflicts.map((conflict) => (
                  <TableRow key={conflict.id} hover>
                    <TableCell>
                      <Box>
                        <Typography variant="body2">
                          {conflict.aircraft1} ↔ {conflict.aircraft2}
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        {getConflictTypeIcon(conflict.type)}
                        <Typography variant="body2" sx={{ ml: 0.5 }}>
                          {conflict.type}
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {conflict.distance} nm
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {conflict.timeToConflict}s
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={conflict.severity}
                        color={getSeverityColor(conflict.severity)}
                        size="small"
                      />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </CardContent>
    </Card>
  );
};

export default ConflictTable;




