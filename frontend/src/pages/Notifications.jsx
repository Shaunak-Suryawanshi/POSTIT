import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Spinner, Button, Badge } from 'react-bootstrap';
import { useAuth } from '../context/AuthContext';
import { postAPI } from '../services/api';

function Notifications() {
    const { user: currentUser } = useAuth();
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);

    useEffect(() => {
        loadNotifications();
    }, []);

    const loadNotifications = async (pageNum = 0) => {
        try {
            setLoading(true);
            const response = await postAPI.getNotifications(pageNum, 20);
            if (pageNum === 0) {
                setNotifications(response.data.content);
            } else {
                setNotifications((prev) => [...prev, ...response.data.content]);
            }
            setPage(pageNum);
        } catch (error) {
            console.error('Error loading notifications:', error);
        } finally {
            setLoading(false);
        }
    };

    const markAsRead = async (notificationId) => {
        try {
            await postAPI.markNotificationAsRead(notificationId);
            setNotifications((prev) =>
                prev.map((notif) =>
                    notif.id === notificationId ? { ...notif, read: true } : notif
                )
            );
        } catch (error) {
            console.error('Error marking notification as read:', error);
        }
    };

    return (
        <Container className="py-4">
            <Row className="justify-content-center">
                <Col md={8} lg={6}>
                    <div className="d-flex justify-content-between align-items-center mb-4">
                        <h3>Notifications</h3>
                        <Button
                            variant="outline-primary"
                            size="sm"
                            onClick={() => loadNotifications(0)}
                        >
                            Refresh
                        </Button>
                    </div>

                    {loading ? (
                        <div className="text-center">
                            <Spinner animation="border" variant="primary" />
                        </div>
                    ) : notifications.length === 0 ? (
                        <Card>
                            <Card.Body className="text-center text-muted">
                                <p>No notifications yet</p>
                            </Card.Body>
                        </Card>
                    ) : (
                        <>
                            {notifications.map((notification) => (
                                <Card
                                    key={notification.id}
                                    className={`mb-3 ${!notification.read ? 'border-primary' : ''}`}
                                    onClick={() => !notification.read && markAsRead(notification.id)}
                                    style={{ cursor: 'pointer' }}
                                >
                                    <Card.Body>
                                        <div className="d-flex justify-content-between align-items-start">
                                            <div className="flex-grow-1">
                                                <div className="d-flex align-items-center gap-2">
                                                    <strong>{notification.senderUsername}</strong>
                                                    {notification.type === 'LIKE' && (
                                                        <Badge bg="danger">❤️ Liked</Badge>
                                                    )}
                                                    {notification.type === 'COMMENT' && (
                                                        <Badge bg="info">💬 Commented</Badge>
                                                    )}
                                                </div>
                                                <p className="mb-0 mt-2">{notification.message}</p>
                                                <small className="text-muted">
                                                    {new Date(notification.createdAt).toLocaleDateString()}
                                                </small>
                                            </div>
                                            {!notification.read && (
                                                <span className="badge bg-primary rounded-circle ms-2">●</span>
                                            )}
                                        </div>
                                    </Card.Body>
                                </Card>
                            ))}
                        </>
                    )}
                </Col>
            </Row>
        </Container>
    );
}

export default Notifications;

