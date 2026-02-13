import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import CreatePost from '../components/CreatePost';
import PostCard from '../components/PostCard';
import FollowButton from '../components/FollowButton';
import { postAPI, userAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';

function Home() {
    const { user: currentUser } = useAuth();
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [suggestedUsers, setSuggestedUsers] = useState([]);
    const [loadingSuggestions, setLoadingSuggestions] = useState(true);

    const loadFeed = async (pageNum = 0) => {
        try {
            const response = await postAPI.getFeed(pageNum, 20);
            const newPosts = response.data.content;

            if (pageNum === 0) {
                setPosts(newPosts);
            } else {
                setPosts((prev) => [...prev, ...newPosts]);
            }

            setHasMore(!response.data.last);
            setPage(pageNum);
        } catch (error) {
            console.error('Error loading feed:', error);
        } finally {
            setLoading(false);
        }
    };

    const loadSuggestedUsers = async () => {
        try {
            setLoadingSuggestions(true);
            // Fetch current user profile to get suggestions
            // For now, we'll just show a message
            // In a production app, you'd have an endpoint for suggested users
            setSuggestedUsers([]);
        } catch (error) {
            console.error('Error loading suggestions:', error);
        } finally {
            setLoadingSuggestions(false);
        }
    };

    useEffect(() => {
        loadFeed(0);
        loadSuggestedUsers();
    }, []);

    const handlePostCreated = (newPost) => {
        setPosts((prev) => [newPost, ...prev]);
    };

    const handlePostDeleted = (postId) => {
        setPosts((prev) => prev.filter((post) => post.id !== postId));
    };

    const handlePostUpdated = (updatedPost) => {
        setPosts((prev) =>
            prev.map((post) => (post.id === updatedPost.id ? updatedPost : post))
        );
    };

    return (
        <Container className="py-4">
            <Row className="g-4">
                {/* Main Feed - Center */}
                <Col lg={6} md={8} xs={12}>
                    <CreatePost onPostCreated={handlePostCreated} />

                    {loading ? (
                        <div className="text-center my-5">
                            <Spinner animation="border" variant="primary" role="status">
                                <span className="visually-hidden">Loading...</span>
                            </Spinner>
                        </div>
                    ) : (
                        <>
                            {posts.length === 0 ? (
                                <Card className="text-center my-5">
                                    <Card.Body className="text-muted">
                                        <p>No posts yet. Follow some users or create your first post!</p>
                                    </Card.Body>
                                </Card>
                            ) : (
                                posts.map((post) => (
                                    <PostCard
                                        key={post.id}
                                        post={post}
                                        onDelete={handlePostDeleted}
                                        onUpdate={handlePostUpdated}
                                    />
                                ))
                            )}

                            {hasMore && posts.length > 0 && (
                                <div className="text-center my-4">
                                    <Button
                                        variant="outline-primary"
                                        onClick={() => loadFeed(page + 1)}
                                    >
                                        Load More
                                    </Button>
                                </div>
                            )}
                        </>
                    )}
                </Col>

                {/* Sidebar - Right (Hidden on mobile) */}
                <Col lg={4} md={4} className="d-none d-lg-block">
                    <Card className="sticky-top" style={{ top: '80px' }}>
                        <Card.Header className="bg-light">
                            <h5 className="mb-0">💡 Tips</h5>
                        </Card.Header>
                        <Card.Body>
                            <div className="mb-4">
                                <h6 className="fw-bold mb-2">📝 Create Posts</h6>
                                <p className="small text-muted">Share your thoughts with your followers</p>
                            </div>
                            <div className="mb-4">
                                <h6 className="fw-bold mb-2">👥 Find People</h6>
                                <p className="small text-muted">Use the search bar to find users and follow them</p>
                            </div>
                            <div className="mb-4">
                                <h6 className="fw-bold mb-2">❤️ Engage</h6>
                                <p className="small text-muted">Like and comment on posts to show your support</p>
                            </div>
                            <div>
                                <h6 className="fw-bold mb-2">🔔 Stay Updated</h6>
                                <p className="small text-muted">Check notifications for likes and comments</p>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
}

export default Home;
