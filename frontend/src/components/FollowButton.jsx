import React, { useState } from 'react';
import { Button } from 'react-bootstrap';
import { followAPI } from '../services/api';

function FollowButton({ userId, initialFollowing, onFollowChange }) {
    const [following, setFollowing] = useState(initialFollowing);
    const [loading, setLoading] = useState(false);

    const handleFollow = async () => {
        setLoading(true);
        try {
            if (following) {
                await followAPI.unfollowUser(userId);
                setFollowing(false);
                onFollowChange && onFollowChange(false);
            } else {
                await followAPI.followUser(userId);
                setFollowing(true);
                onFollowChange && onFollowChange(true);
            }
        } catch (error) {
            console.error('Error toggling follow:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Button
            variant={following ? 'outline-primary' : 'primary'}
            size="sm"
            onClick={handleFollow}
            disabled={loading}
        >
            {loading ? 'Loading...' : following ? 'Following' : 'Follow'}
        </Button>
    );
}

export default FollowButton;
