import React, { useState } from 'react';
import { Navbar as BSNavbar, Container, Nav, Button, Form, FormControl } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FaHome, FaUser, FaSignOutAlt, FaSearch, FaBell } from 'react-icons/fa';

function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');

    const handleLogout = async () => {
        await logout();
        navigate('/login');
    };

    const handleSearch = (e) => {
        e.preventDefault();
        if (searchQuery.trim()) {
            navigate(`/search?q=${encodeURIComponent(searchQuery)}`);
            setSearchQuery('');
        }
    };

    return (
        <BSNavbar bg="dark" variant="dark" expand="lg" sticky="top" className="shadow-sm">
            <Container>
                <BSNavbar.Brand as={Link} to="/" className="fw-bold fs-5">
                    <FaHome className="me-2" />POSTIT
                </BSNavbar.Brand>
                <BSNavbar.Toggle aria-controls="basic-navbar-nav" />
                <BSNavbar.Collapse id="basic-navbar-nav">
                    {/* Search Bar - Center */}
                    <Form className="d-flex mx-auto my-2 my-lg-0" onSubmit={handleSearch}>
                        <FormControl
                            type="text"
                            placeholder="Search users..."
                            className="me-2"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            style={{ maxWidth: '300px' }}
                        />
                        <Button variant="outline-light" type="submit" className="d-flex align-items-center">
                            <FaSearch />
                        </Button>
                    </Form>

                    {/* Navigation Links - Right */}
                    <Nav className="ms-auto align-items-center">
                        <Nav.Link as={Link} to="/" className="d-flex align-items-center">
                            <FaHome className="me-1" /> Home
                        </Nav.Link>
                        <Nav.Link as={Link} to="/notifications" className="d-flex align-items-center">
                            <FaBell className="me-1" /> Notifications
                        </Nav.Link>
                        <Nav.Link as={Link} to={`/profile/${user?.username}`} className="d-flex align-items-center">
                            <FaUser className="me-1" /> Profile
                        </Nav.Link>
                        <Button
                            variant="outline-light"
                            size="sm"
                            onClick={handleLogout}
                            className="ms-2 d-flex align-items-center"
                        >
                            <FaSignOutAlt className="me-1" /> Logout
                        </Button>
                    </Nav>
                </BSNavbar.Collapse>
            </Container>
        </BSNavbar>
    );
}

export default Navbar;
