import React, { useState } from 'react';
import { Card, Form, Button, Alert } from 'react-bootstrap';
import { postAPI } from '../services/api';

function CreatePost({ onPostCreated }) {
    const [content, setContent] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!content.trim()) {
            setError('Post content cannot be empty');
            return;
        }

        if (content.length > 280) {
            setError('Post content cannot exceed 280 characters');
            return;
        }

        setLoading(true);

        try {
            const response = await postAPI.createPost({ content });
            onPostCreated(response.data);
            setContent('');
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create post');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card className="mb-4">
            <Card.Body>
                <Form onSubmit={handleSubmit}>
                    {error && <Alert variant="danger" dismissible onClose={() => setError('')}>{error}</Alert>}

                    <Form.Group className="mb-3">
                        <Form.Control
                            as="textarea"
                            rows={3}
                            placeholder="What's on your mind?"
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                            maxLength={280}
                        />
                        <Form.Text className="text-muted">
                            {content.length}/280 characters
                        </Form.Text>
                    </Form.Group>

                    <Button variant="primary" type="submit" disabled={loading || !content.trim()}>
                        {loading ? 'Posting...' : 'Post'}
                    </Button>
                </Form>
            </Card.Body>
        </Card>
    );
}

export default CreatePost;
