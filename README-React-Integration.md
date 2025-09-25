# Hotel Management - React Frontend Integration

This document explains how to integrate the React.js frontend with your Java Spring Boot user service APIs.

## Files Created

1. **`api-service.js`** - API service class for making HTTP requests to your Java backend
2. **`useUserApi.js`** - Custom React hook for managing user state and API operations
3. **`UserManagement.jsx`** - Complete React component with all CRUD operations
4. **`UserManagement.css`** - Styling for the user management component
5. **`App.jsx`** - Main App component
6. **`App.css`** - Basic App styling

## Setup Instructions

### 1. Copy Files to Your React Project

Copy all the created files to your React project's `src` directory:

```bash
# Copy these files to your React project's src folder
api-service.js
useUserApi.js
UserManagement.jsx
UserManagement.css
App.jsx
App.css
```

### 2. Update Your Main App Component

Replace your existing `App.jsx` with the provided one, or import the `UserManagement` component:

```jsx
import React from 'react';
import UserManagement from './UserManagement';
import './App.css';

function App() {
  return (
    <div className="App">
      <UserManagement />
    </div>
  );
}

export default App;
```

### 3. Configure API Base URL

Update the `API_BASE_URL` in `api-service.js` to match your Java backend URL:

```javascript
const API_BASE_URL = 'http://localhost:8080/api/v2/user';
```

### 4. Handle CORS (Backend Configuration)

Make sure your Java Spring Boot application allows CORS requests from your React app. Add this to your Java controller or create a CORS configuration:

```java
@CrossOrigin(origins = "http://localhost:3000") // Add this to your UserController
@RestController
@RequestMapping("/api/v2/user")
public class UserController {
    // ... your existing code
}
```

Or create a global CORS configuration:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*");
    }
}
```

## API Endpoints Used

The React frontend integrates with these Java backend endpoints:

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v2/user/register` | Register new user |
| `GET` | `/api/v2/user/getAll` | Get all users |
| `GET` | `/api/v2/user/{id}` | Get user by ID |
| `PATCH` | `/api/v2/user/update` | Update user |
| `DELETE` | `/api/v2/user/{id}` | Delete user |

## Features Included

### 1. **User Registration**
- Form with username, password, role selection, and active status
- Validation and error handling
- Automatic refresh of user list after successful registration

### 2. **User Listing**
- Display all users in a responsive grid layout
- Show user ID, username, role, and active status
- Card-based design with hover effects

### 3. **User Search**
- Search users by ID
- Display detailed user information
- Clear search results

### 4. **User Update**
- Update user information (name, password, role)
- Pre-fill form with existing user data
- Validation and error handling

### 5. **User Deletion**
- Delete users with confirmation dialog
- Automatic refresh of user list after deletion

### 6. **Error Handling**
- Display error messages with dismiss functionality
- Loading states for all operations
- User-friendly error messages

### 7. **Responsive Design**
- Mobile-friendly layout
- Responsive grid for user cards
- Adaptive forms and buttons

## Usage Examples

### Using the API Service Directly

```javascript
import userApiService from './api-service';

// Register a new user
const newUser = {
  userName: 'john_doe',
  password: 'password123',
  role: '1', // 0=Admin, 1=Manager, 2=User
  active: true
};

try {
  const result = await userApiService.registerUser(newUser);
  console.log('User registered:', result);
} catch (error) {
  console.error('Registration failed:', error.message);
}
```

### Using the Custom Hook

```javascript
import { useUserApi } from './useUserApi';

function MyComponent() {
  const { users, loading, error, fetchAllUsers, registerUser } = useUserApi();

  useEffect(() => {
    fetchAllUsers();
  }, [fetchAllUsers]);

  const handleRegister = async (userData) => {
    try {
      await registerUser(userData);
      alert('User registered successfully!');
    } catch (error) {
      alert(`Registration failed: ${error.message}`);
    }
  };

  return (
    <div>
      {loading && <p>Loading...</p>}
      {error && <p>Error: {error}</p>}
      {users.map(user => (
        <div key={user.id}>{user.userName}</div>
      ))}
    </div>
  );
}
```

## Role Mapping

The frontend maps role numbers to display names:

- `0` → Admin
- `1` → Manager  
- `2` → User

## Error Handling

The integration includes comprehensive error handling:

- Network errors
- API validation errors
- User-friendly error messages
- Loading states
- Error dismissal functionality

## Styling

The component uses modern CSS with:

- Responsive design
- Hover effects
- Card-based layout
- Color-coded buttons
- Form validation styling
- Mobile-friendly design

## Testing the Integration

1. Start your Java Spring Boot application
2. Start your React development server
3. Navigate to the user management page
4. Test all CRUD operations:
   - Register a new user
   - View all users
   - Search for a specific user
   - Update user information
   - Delete a user

## Troubleshooting

### Common Issues

1. **CORS Errors**: Make sure your Java backend allows CORS from your React app
2. **API URL Mismatch**: Verify the `API_BASE_URL` in `api-service.js`
3. **Port Conflicts**: Ensure your Java app runs on port 8080 and React on port 3000
4. **Network Errors**: Check if your Java backend is running and accessible

### Debug Tips

- Check browser developer tools for network requests
- Verify API responses in the Network tab
- Check console for JavaScript errors
- Ensure all required fields are filled in forms

## Next Steps

1. Add authentication and authorization
2. Implement pagination for large user lists
3. Add sorting and filtering capabilities
4. Implement real-time updates using WebSocket
5. Add more user management features (password reset, account activation, etc.) 