import React, { createContext, useState, useContext, useEffect, useMemo } from 'react';
import { authAPI, userAPI } from '../services/api';

const AuthContext = createContext(null);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Check if user is logged in
        const token = localStorage.getItem('accessToken');
        const storedUser = localStorage.getItem('user');

        if (token && storedUser) {
            try {
                setUser(JSON.parse(storedUser));
            } catch (e) {
                console.error('Failed to parse stored user:', e);
                logout();
                setLoading(false);
                return;
            }

            // Verify token is still valid by making an API call
            userAPI.getCurrentUser()
                .then(response => {
                    setUser(response.data);
                    localStorage.setItem('user', JSON.stringify(response.data));
                    setLoading(false);
                })
                .catch((error) => {
                    console.error('Token verification failed:', error);
                    // Only logout if it's a 401/403, not other errors
                    if (error.response?.status === 401 || error.response?.status === 403) {
                        logout();
                    } else {
                        // For other errors, keep user logged in (network issues, etc.)
                        setLoading(false);
                    }
                });
        } else {
            setLoading(false);
        }
    }, []);

    const login = async (credentials) => {
        try {
            const response = await authAPI.login(credentials);
            const { accessToken, refreshToken, user } = response.data;

            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', refreshToken);
            localStorage.setItem('user', JSON.stringify(user));

            setUser(user);
            return { success: true };
        } catch (error) {
            console.error('Login failed:', error);
            // Extract error message from various possible response formats
            let errorMessage = 'Login failed';
            if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            } else if (error.response?.data?.error) {
                errorMessage = error.response.data.error;
            } else if (error.message) {
                errorMessage = error.message;
            }
            return {
                success: false,
                error: errorMessage,
            };
        }
    };

    const register = async (userData) => {
        try {
            const response = await authAPI.register(userData);
            const { accessToken, refreshToken, user } = response.data;

            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', refreshToken);
            localStorage.setItem('user', JSON.stringify(user));

            setUser(user);
            return { success: true };
        } catch (error) {
            console.error('Registration failed:', error);
            if (error.response) {
                console.error('Response data:', error.response.data);
                console.error('Response status:', error.response.status);
            }
            // Extract error message from various possible response formats
            let errorMessage = 'Registration failed';
            if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            } else if (error.response?.data?.error) {
                errorMessage = error.response.data.error;
            } else if (error.message) {
                errorMessage = error.message;
            }
            return {
                success: false,
                error: errorMessage,
            };
        }
    };

    const logout = async () => {
        try {
            await authAPI.logout();
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('user');
            setUser(null);
        }
    };

    const value = useMemo(() => ({
        user,
        login,
        register,
        logout,
        loading,
        isAuthenticated: !!user,
    }), [user, loading]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
