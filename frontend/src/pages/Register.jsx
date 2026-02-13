import React, { useState } from 'react';
import { Container, Form, Button, Card, Alert } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Register() {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        displayName: '',
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { register } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        // Client-side validation
        if (formData.username.length < 3 || formData.username.length > 20) {
            setError('Username must be between 3 and 20 characters');
            setLoading(false);
            return;
        }

        if (formData.password.length < 6) {
            setError('Password must be at least 6 characters');
            setLoading(false);
            return;
        }

        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            setError('Please enter a valid email address');
            setLoading(false);
            return;
        }

        console.log('Sending registration request with data:', {
            username: formData.username,
            email: formData.email,
            displayName: formData.displayName,
            passwordLength: formData.password.length,
        });

        const result = await register(formData);

        if (result.success) {
            console.log('Registration successful, navigating to home');
            navigate('/');
        } else {
            console.error('Registration error:', result.error);
            setError(result.error);
        }

        setLoading(false);
    };

    return (
        <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
            <Card style={{ width: '400px' }} className="shadow">
                <Card.Body>
                    <h2 className="text-center mb-4">POSTIT</h2>
                    <h5 className="text-center mb-4 text-muted">Create your account</h5>

                    {error && <Alert variant="danger">{error}</Alert>}

                    <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-3">
                            <Form.Label>Username</Form.Label>
                            <Form.Control
                                type="text"
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                required
                                placeholder="Choose a username"
                                minLength={3}
                                maxLength={20}
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Email</Form.Label>
                            <Form.Control
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                required
                                placeholder="Enter your email"
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Display Name</Form.Label>
                            <Form.Control
                                type="text"
                                name="displayName"
                                value={formData.displayName}
                                onChange={handleChange}
                                placeholder="Your display name (optional)"
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Password</Form.Label>
                            <Form.Control
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                placeholder="Choose a password"
                                minLength={6}
                            />
                        </Form.Group>

                        <Button variant="primary" type="submit" className="w-100" disabled={loading}>
                            {loading ? 'Creating account...' : 'Register'}
                        </Button>
                    </Form>

                    <div className="text-center mt-3">
                        <p>
                            Already have an account? <Link to="/login">Login here</Link>
                        </p>
                    </div>
                </Card.Body>
            </Card>
        </Container>
    );
}

export default Register;
