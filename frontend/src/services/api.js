import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        const response = await axios.post(`${API_URL}/auth/refresh`, {
          refreshToken,
        });

        const { accessToken } = response.data;
        localStorage.setItem('accessToken', accessToken);

        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  logout: () => api.post('/auth/logout'),
};

// User API
export const userAPI = {
  getCurrentUser: () => api.get('/users/me'),
  getUserByUsername: (username) => api.get(`/users/${username}`),
};

// Post API
export const postAPI = {
  createPost: (data) => api.post('/posts', data),
  getFeed: (page = 0, size = 20) => api.get(`/posts/feed?page=${page}&size=${size}`),
  getUserPosts: (userId, page = 0, size = 20) => api.get(`/posts/user/${userId}?page=${page}&size=${size}`),
  getPost: (postId) => api.get(`/posts/${postId}`),
  deletePost: (postId) => api.delete(`/posts/${postId}`),
  likePost: (postId) => api.post(`/posts/${postId}/like`),
  unlikePost: (postId) => api.delete(`/posts/${postId}/like`),
  addComment: (postId, data) => api.post(`/posts/${postId}/comments`, data),
  getComments: (postId, page = 0, size = 20) => api.get(`/posts/${postId}/comments?page=${page}&size=${size}`),
  deleteComment: (commentId) => api.delete(`/posts/comments/${commentId}`),
  // Notification API
  getNotifications: (page = 0, size = 20) => api.get(`/notifications?page=${page}&size=${size}`),
  getUnreadNotifications: (page = 0, size = 20) => api.get(`/notifications/unread?page=${page}&size=${size}`),
  getUnreadCount: () => api.get('/notifications/unread-count'),
  markNotificationAsRead: (notificationId) => api.put(`/notifications/${notificationId}/read`),
  markAllNotificationsAsRead: () => api.put('/notifications/read-all'),
};

// Follow API
export const followAPI = {
  followUser: (userId) => api.post(`/users/${userId}/follow`),
  unfollowUser: (userId) => api.delete(`/users/${userId}/follow`),
  getFollowers: (userId) => api.get(`/users/${userId}/followers`),
  getFollowing: (userId) => api.get(`/users/${userId}/following`),
  getFollowStatus: (userId) => api.get(`/users/${userId}/follow-status`),
};

export default api;
