import api from './api';

const analyticsService = {
  // Get bid success rate over time
  getBidSuccessRate: async (companyId, months = 6) => {
    try {
      const response = await api.get(`/analytics/bids/success-rate/${companyId}?months=${months}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get bid volume over time
  getBidVolume: async (companyId, months = 6) => {
    try {
      const response = await api.get(`/analytics/bids/volume/${companyId}?months=${months}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get bid status distribution
  getBidDistribution: async (companyId) => {
    try {
      const response = await api.get(`/analytics/bids/distribution/${companyId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get overall bid statistics
  getBidStatistics: async (companyId) => {
    try {
      const response = await api.get(`/analytics/bids/statistics/${companyId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get performance metrics
  getPerformanceMetrics: async (companyId) => {
    try {
      const response = await api.get(`/analytics/bids/performance/${companyId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get bid trends data by category
  getBidTrends: async (companyId, timeframe = 'month') => {
    try {
      const response = await api.get(`/analytics/bids/trends/${companyId}?timeframe=${timeframe}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Get project timeline data by project type
  getProjectTimelines: async (projectType) => {
    try {
      const response = await api.get(`/analytics/bids/projects/timelines?type=${projectType}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },
};

export default analyticsService;