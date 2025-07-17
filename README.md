// API Usage Examples (for testing)
/*
1. Register regular user (VISITOR):
POST /api/auth/signup
{
    "username": "testuser",
    "email": "test@example.com", 
    "password": "password123"
}

2. Login with username or email:
POST /api/auth/signin
{
    "identifier": "testuser", // or "test@example.com"
    "password": "password123"
}

3. Refresh JWT token:
POST /api/auth/refreshtoken
{
    "refreshToken": "your_refresh_token_here"
}

4. Logout user (invalidates refresh token):
POST /api/auth/signout
Headers:
Authorization: Bearer <your_jwt_token>

5. Register organizer using invite token:
POST /api/auth/signup
{
    "username": "organizer123",
    "email": "org@example.com", 
    "password": "password123",
    "inviteToken": "valid_invite_token_here"
}
*/
