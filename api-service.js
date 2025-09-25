// API Service for User Management
// Base URL for your Java Spring Boot API
const API_BASE_URL = 'http://localhost:8080/api/v2/user';

// API Service class for user operations
class UserApiService {
    constructor() {
        this.baseURL = API_BASE_URL;
    }

    // Helper method for making HTTP requests
    async makeRequest(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
            },
        };

        const config = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...options.headers,
            },
        };

        try {
            const response = await fetch(url, config);
            
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('API Request failed:', error);
            throw error;
        }
    }

    // Register a new user
    async registerUser(userData) {
        const payload = {
            userName: userData.userName,
            password: userData.password,
            role: userData.role, // 0, 1, or 2
            active: userData.active || true
        };

        return this.makeRequest('/register', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
    }

    // Get user by ID
    async getUserById(id) {
        return this.makeRequest(`/${id}`, {
            method: 'GET'
        });
    }

    // Get all users
    async getAllUsers() {
        return this.makeRequest('/getAll', {
            method: 'GET'
        });
    }

    // Update user
    async updateUser(updateData) {
        const payload = {
            id: updateData.id,
            name: updateData.name,
            password: updateData.password,
            role: updateData.role
        };

        return this.makeRequest('/update', {
            method: 'PATCH',
            body: JSON.stringify(payload)
        });
    }

    // Delete user by ID
    async deleteUser(id) {
        return this.makeRequest(`/${id}`, {
            method: 'DELETE'
        });
    }
}

// Create and export a singleton instance
const userApiService = new UserApiService();
export default userApiService; 