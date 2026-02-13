import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Spinner } from 'react-bootstrap';
import { useSearchParams, Link } from 'react-router-dom';
import { userAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import FollowButton from '../components/FollowButton';

function Search() {
    const [searchParams] = useSearchParams();
    const { user: currentUser } = useAuth();
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(true);
    const query = searchParams.get('q');

    useEffect(() => {
        if (query) {
            performSearch();
        }
    }, [query]);

    const performSearch = async () => {
        try {
            setLoading(true);
            // Search by username - fetch user and check if username contains query
            const response = await userAPI.getUserByUsername(query);
            setResults([response.data]);
        } catch (error) {
            console.log('User not found, showing no results');
            setResults([]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="py-4">
            <Row className="justify-content-center">
                <Col md={8} lg={6}>
                    <h3 className="mb-4">Search Results for "{query}"</h3>

                    {loading ? (
                        <div className="text-center">
                            <Spinner animation="border" variant="primary" />
                        </div>
                    ) : results.length === 0 ? (
                        <Card>
                            <Card.Body className="text-center text-muted">
                                <p>No users found matching "{query}"</p>
                            </Card.Body>
                        </Card>
                    ) : (
                        results.map((profile) => (
                            <Card key={profile.id} className="mb-3 shadow-sm">
                                <Card.Body>
                                    <div className="d-flex justify-content-between align-items-start">
                                        <Link
                                            to={`/profile/${profile.username}`}
                                            style={{ textDecoration: 'none', color: 'inherit' }}
                                        >
                                            <div>
                                                <h5 className="mb-1">{profile.displayName || profile.username}</h5>
                                                <p className="text-muted mb-2">@{profile.username}</p>
                                                {profile.bio && <p className="mb-2">{profile.bio}</p>}
                                                <div className="text-muted small">
                                                    <span className="me-3">
                                                        <strong>{profile.followersCount}</strong> followers
                                                    </span>
                                                    <span>
                                                        <strong>{profile.followingCount}</strong> following
                                                    </span>
                                                </div>
                                            </div>
                                        </Link>
                                        {currentUser?.username !== profile.username && (
                                            <FollowButton
                                                userId={profile.id}
                                                initialFollowing={profile.followedByCurrentUser}
                                            />
                                        )}
                                    </div>
                                </Card.Body>
                            </Card>
                        ))
                    )}
                </Col>
            </Row>
        </Container>
    );
}

export default Search;

