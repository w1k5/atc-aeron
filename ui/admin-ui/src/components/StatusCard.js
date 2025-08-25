import React from 'react';
import { Card, CardContent, Typography, Box, Chip, List, ListItem, ListItemText } from '@mui/material';
import { CheckCircle as CheckIcon, Error as ErrorIcon, Warning as WarningIcon } from '@mui/icons-material';

const StatusCard = ({ title, status, details = [] }) => {
  const getStatusIcon = (status) => {
    switch (status) {
      case 'online':
      case 'healthy':
        return <CheckIcon color="success" />;
      case 'warning':
        return <WarningIcon color="warning" />;
      case 'offline':
      case 'error':
        return <ErrorIcon color="error" />;
      default:
        return <CheckIcon color="success" />;
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'online':
      case 'healthy':
        return 'success';
      case 'warning':
        return 'warning';
      case 'offline':
      case 'error':
        return 'error';
      default:
        return 'success';
    }
  };

  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          {getStatusIcon(status)}
          <Typography variant="h6" sx={{ ml: 1 }}>
            {title}
          </Typography>
        </Box>
        
        <Box sx={{ mb: 2 }}>
          <Chip
            label={status}
            color={getStatusColor(status)}
            variant="outlined"
            size="small"
          />
        </Box>
        
        <List dense>
          {details.map((detail, index) => (
            <ListItem key={index} sx={{ px: 0 }}>
              <ListItemText
                primary={detail.label}
                secondary={
                  <Chip
                    label={detail.status}
                    color={detail.color || 'success'}
                    size="small"
                    variant="outlined"
                  />
                }
              />
            </ListItem>
          ))}
        </List>
      </CardContent>
    </Card>
  );
};

export default StatusCard;
