import React, { useState } from 'react';
import { Container, Form, Button, Card, Alert } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Login() {
    const [formData, setFormData] = useState({
        usernameOrEmail: '',
        password: '',
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
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

        const result = await login(formData);

        if (result.success) {
            navigate('/');
        } else {
            setError(result.error);
        }

        setLoading(false);
    };

    return (
        <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
            <Card style={{ width: '400px' }} className="shadow">
                <Card.Body>
                    <h2 className="text-center mb-4">POSTIT</h2>
                    <h5 className="text-center mb-4 text-muted">Login to your account</h5>

                    {error && <Alert variant="danger">{error}</Alert>}

                    <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-3">
                            <Form.Label>Username or Email</Form.Label>
                            <Form.Control
                                type="text"
                                name="usernameOrEmail"
                                value={formData.usernameOrEmail}
                                onChange={handleChange}
                                required
                                placeholder="Enter username or email"
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
                                placeholder="Enter password"
                            />
                        </Form.Group>

                        <Button variant="primary" type="submit" className="w-100" disabled={loading}>
                            {loading ? 'Logging in...' : 'Login'}
                        </Button>
                    </Form>

                    <div className="text-center mt-3">
                        <p>
                            Don't have an account? <Link to="/register">Register here</Link>
                        </p>
                    </div>
                </Card.Body>
            </Card>
        </Container>
    );
}

export default Login;
