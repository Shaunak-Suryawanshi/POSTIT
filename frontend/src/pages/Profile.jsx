import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Tabs, Tab } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import { userAPI, postAPI, followAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import PostCard from '../components/PostCard';
import FollowButton from '../components/FollowButton';

function Profile() {
    const { username } = useParams();
    const { user: currentUser } = useAuth();
    const [profile, setProfile] = useState(null);
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState('posts');

    useEffect(() => {
        loadProfile();
    }, [username]);

    const loadProfile = async () => {
        try {
            const profileResponse = await userAPI.getUserByUsername(username);
            setProfile(profileResponse.data);

            const postsResponse = await postAPI.getUserPosts(profileResponse.data.id, 0, 20);
            setPosts(postsResponse.data.content);
        } catch (error) {
            console.error('Error loading profile:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleFollowChange = (isFollowing) => {
        setProfile((prev) => ({
            ...prev,
            followedByCurrentUser: isFollowing,
            followersCount: isFollowing ? prev.followersCount + 1 : prev.followersCount - 1,
        }));
    };

    if (loading) {
        return (
            <Container className="text-center my-5">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </Container>
        );
    }

    if (!profile) {
        return (
            <Container className="text-center my-5">
                <h3>User not found</h3>
            </Container>
        );
    }

    const isOwnProfile = currentUser?.username === profile.username;

    return (
        <Container className="py-4">
            <Row className="justify-content-center">
                <Col md={8} lg={6}>
                    <Card className="mb-4">
                        <Card.Body>
                            <div className="d-flex justify-content-between align-items-start">
                                <div>
                                    <h3>{profile.displayName || profile.username}</h3>
                                    <p className="text-muted">@{profile.username}</p>
                                    {profile.bio && <p>{profile.bio}</p>}
                                </div>
                                {!isOwnProfile && (
                                    <FollowButton
                                        userId={profile.id}
                                        initialFollowing={profile.followedByCurrentUser}
                                        onFollowChange={handleFollowChange}
                                    />
                                )}
                            </div>

                            <div className="d-flex gap-4 mt-3">
                                <div>
                                    <strong>{profile.postsCount}</strong> Posts
                                </div>
                                <div>
                                    <strong>{profile.followersCount}</strong> Followers
                                </div>
                                <div>
                                    <strong>{profile.followingCount}</strong> Following
                                </div>
                            </div>
                        </Card.Body>
                    </Card>

                    <Tabs activeKey={activeTab} onSelect={(k) => setActiveTab(k)} className="mb-3">
                        <Tab eventKey="posts" title="Posts">
                            {posts.length === 0 ? (
                                <div className="text-center my-5 text-muted">
                                    <p>No posts yet</p>
                                </div>
                            ) : (
                                posts.map((post) => (
                                    <PostCard
                                        key={post.id}
                                        post={post}
                                        onDelete={(id) => setPosts((prev) => prev.filter((p) => p.id !== id))}
                                        onUpdate={(updated) =>
                                            setPosts((prev) => prev.map((p) => (p.id === updated.id ? updated : p)))
                                        }
                                    />
                                ))
                            )}
                        </Tab>
                    </Tabs>
                </Col>
            </Row>
        </Container>
    );
}

export default Profile;
