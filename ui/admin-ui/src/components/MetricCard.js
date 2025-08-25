import React from 'react';
import { Card, CardContent, Typography, Box, Chip } from '@mui/material';

const MetricCard = ({ title, value, icon, color = 'primary', trend, trendColor = 'success' }) => {
  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
          <Box
            sx={{
              backgroundColor: `${color}.main`,
              borderRadius: '50%',
              p: 1,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            {React.cloneElement(icon, { sx: { color: 'white', fontSize: 24 } })}
          </Box>
          {trend && (
            <Chip
              label={trend}
              size="small"
              color={trendColor}
              variant="outlined"
            />
          )}
        </Box>
        
        <Typography variant="h4" component="div" gutterBottom>
          {value}
        </Typography>
        
        <Typography variant="body2" color="text.secondary">
          {title}
        </Typography>
      </CardContent>
    </Card>
  );
};

export default MetricCard;
