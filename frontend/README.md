# POSTIT Frontend

React-based frontend for the POSTIT social media application.

## Features

- User authentication (Login/Register)
- Create and view posts
- Like and comment on posts
- Follow/unfollow users
- User profiles
- Personalized feed

## Prerequisites

- Node.js 18+
- npm or yarn

## Installation

```bash
npm install
```

## Environment Variables

Create a `.env` file in the root directory:

```env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_GOOGLE_CLIENT_ID=your-google-client-id
```

## Running Locally

```bash
# Development mode
npm start

# Build for production
npm run build
```

The app will open at [http://localhost:3000](http://localhost:3000)

## Running with Docker

```bash
# Build the image
docker build -t postit-frontend .

# Run the container
docker run -p 80:80 postit-frontend
```

## Project Structure

```
src/
├── components/         # Reusable components
│   ├── Navbar.jsx
│   ├── CreatePost.jsx
│   ├── PostCard.jsx
│   ├── CommentSection.jsx
│   └── FollowButton.jsx
├── context/           # React context
│   └── AuthContext.js
├── pages/             # Page components
│   ├── Login.jsx
│   ├── Register.jsx
│   ├── Home.jsx
│   └── Profile.jsx
├── services/          # API services
│   └── api.js
├── App.js             # Main app component
└── index.js           # Entry point
```

## Available Scripts

- `npm start` - Run development server
- `npm run build` - Build for production
- `npm test` - Run tests
- `npm run eject` - Eject from Create React App

## Technologies Used

- React 18
- React Router DOM
- Bootstrap 5
- React Bootstrap
- Axios
- React Icons

## API Integration

The frontend communicates with the Spring Boot backend via REST API. All API calls are handled through the `api.js` service which includes:

- JWT token management
- Automatic token refresh
- Request/response interceptors
- Error handling

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

MIT
