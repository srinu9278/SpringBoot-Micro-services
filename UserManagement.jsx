import React, { useState, useEffect } from 'react';
import { useUserApi } from './useUserApi';
import './UserManagement.css';

const UserManagement = () => {
    const {
        users,
        loading,
        error,
        currentUser,
        fetchAllUsers,
        fetchUserById,
        registerUser,
        updateUser,
        deleteUser,
        clearError,
        clearCurrentUser,
    } = useUserApi();

    // Form states
    const [showRegisterForm, setShowRegisterForm] = useState(false);
    const [showUpdateForm, setShowUpdateForm] = useState(false);
    const [selectedUserId, setSelectedUserId] = useState(null);
    const [searchId, setSearchId] = useState('');

    // Form data
    const [registerForm, setRegisterForm] = useState({
        userName: '',
        password: '',
        role: '0',
        active: true
    });

    const [updateForm, setUpdateForm] = useState({
        id: '',
        name: '',
        password: '',
        role: '0'
    });

    // Load users on component mount
    useEffect(() => {
        fetchAllUsers();
    }, [fetchAllUsers]);

    // Handle register form submission
    const handleRegisterSubmit = async (e) => {
        e.preventDefault();
        try {
            await registerUser(registerForm);
            setRegisterForm({ userName: '', password: '', role: '0', active: true });
            setShowRegisterForm(false);
            alert('User registered successfully!');
        } catch (err) {
            alert(`Registration failed: ${err.message}`);
        }
    };

    // Handle update form submission
    const handleUpdateSubmit = async (e) => {
        e.preventDefault();
        try {
            await updateUser(updateForm);
            setUpdateForm({ id: '', name: '', password: '', role: '0' });
            setShowUpdateForm(false);
            setSelectedUserId(null);
            alert('User updated successfully!');
        } catch (err) {
            alert(`Update failed: ${err.message}`);
        }
    };

    // Handle user deletion
    const handleDeleteUser = async (id) => {
        if (window.confirm('Are you sure you want to delete this user?')) {
            try {
                await deleteUser(id);
                alert('User deleted successfully!');
            } catch (err) {
                alert(`Deletion failed: ${err.message}`);
            }
        }
    };

    // Handle user search
    const handleSearchUser = async () => {
        if (!searchId) {
            alert('Please enter a user ID');
            return;
        }
        try {
            await fetchUserById(parseInt(searchId));
        } catch (err) {
            alert(`Search failed: ${err.message}`);
        }
    };

    // Get role name
    const getRoleName = (role) => {
        const roles = { '0': 'Admin', '1': 'Manager', '2': 'User' };
        return roles[role] || 'Unknown';
    };

    return (
        <div className="user-management">
            <h1>Hotel Management - User Management</h1>
            
            {/* Error Display */}
            {error && (
                <div className="error-message">
                    <span>{error}</span>
                    <button onClick={clearError}>×</button>
                </div>
            )}

            {/* Loading Indicator */}
            {loading && <div className="loading">Loading...</div>}

            {/* Action Buttons */}
            <div className="action-buttons">
                <button 
                    onClick={() => setShowRegisterForm(!showRegisterForm)}
                    className="btn btn-primary"
                >
                    {showRegisterForm ? 'Cancel Registration' : 'Register New User'}
                </button>
                <button 
                    onClick={() => setShowUpdateForm(!showUpdateForm)}
                    className="btn btn-secondary"
                >
                    {showUpdateForm ? 'Cancel Update' : 'Update User'}
                </button>
            </div>

            {/* Register User Form */}
            {showRegisterForm && (
                <div className="form-container">
                    <h3>Register New User</h3>
                    <form onSubmit={handleRegisterSubmit}>
                        <div className="form-group">
                            <label>Username:</label>
                            <input
                                type="text"
                                value={registerForm.userName}
                                onChange={(e) => setRegisterForm({...registerForm, userName: e.target.value})}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Password:</label>
                            <input
                                type="password"
                                value={registerForm.password}
                                onChange={(e) => setRegisterForm({...registerForm, password: e.target.value})}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Role:</label>
                            <select
                                value={registerForm.role}
                                onChange={(e) => setRegisterForm({...registerForm, role: e.target.value})}
                            >
                                <option value="0">Admin</option>
                                <option value="1">Manager</option>
                                <option value="2">User</option>
                            </select>
                        </div>
                        <div className="form-group">
                            <label>
                                <input
                                    type="checkbox"
                                    checked={registerForm.active}
                                    onChange={(e) => setRegisterForm({...registerForm, active: e.target.checked})}
                                />
                                Active
                            </label>
                        </div>
                        <button type="submit" className="btn btn-primary">Register User</button>
                    </form>
                </div>
            )}

            {/* Update User Form */}
            {showUpdateForm && (
                <div className="form-container">
                    <h3>Update User</h3>
                    <form onSubmit={handleUpdateSubmit}>
                        <div className="form-group">
                            <label>User ID:</label>
                            <input
                                type="number"
                                value={updateForm.id}
                                onChange={(e) => setUpdateForm({...updateForm, id: e.target.value})}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>New Username:</label>
                            <input
                                type="text"
                                value={updateForm.name}
                                onChange={(e) => setUpdateForm({...updateForm, name: e.target.value})}
                            />
                        </div>
                        <div className="form-group">
                            <label>New Password:</label>
                            <input
                                type="password"
                                value={updateForm.password}
                                onChange={(e) => setUpdateForm({...updateForm, password: e.target.value})}
                            />
                        </div>
                        <div className="form-group">
                            <label>New Role:</label>
                            <select
                                value={updateForm.role}
                                onChange={(e) => setUpdateForm({...updateForm, role: e.target.value})}
                            >
                                <option value="0">Admin</option>
                                <option value="1">Manager</option>
                                <option value="2">User</option>
                            </select>
                        </div>
                        <button type="submit" className="btn btn-secondary">Update User</button>
                    </form>
                </div>
            )}

            {/* Search User */}
            <div className="search-section">
                <h3>Search User by ID</h3>
                <div className="search-form">
                    <input
                        type="number"
                        placeholder="Enter User ID"
                        value={searchId}
                        onChange={(e) => setSearchId(e.target.value)}
                    />
                    <button onClick={handleSearchUser} className="btn btn-info">Search</button>
                    {currentUser && (
                        <button onClick={clearCurrentUser} className="btn btn-warning">Clear</button>
                    )}
                </div>
                
                {currentUser && (
                    <div className="user-details">
                        <h4>User Details</h4>
                        <p><strong>Name:</strong> {currentUser.name}</p>
                        <p><strong>Role:</strong> {getRoleName(currentUser.role)}</p>
                        <p><strong>Status:</strong> {currentUser.state ? 'Active' : 'Inactive'}</p>
                    </div>
                )}
            </div>

            {/* Users List */}
            <div className="users-list">
                <h3>All Users ({users.length})</h3>
                <div className="users-grid">
                    {users.map((user) => (
                        <div key={user.id} className="user-card">
                            <h4>{user.userName}</h4>
                            <p><strong>ID:</strong> {user.id}</p>
                            <p><strong>Role:</strong> {getRoleName(user.role)}</p>
                            <p><strong>Status:</strong> {user.active ? 'Active' : 'Inactive'}</p>
                            <div className="user-actions">
                                <button 
                                    onClick={() => {
                                        setUpdateForm({...updateForm, id: user.id.toString()});
                                        setShowUpdateForm(true);
                                    }}
                                    className="btn btn-small btn-secondary"
                                >
                                    Edit
                                </button>
                                <button 
                                    onClick={() => handleDeleteUser(user.id)}
                                    className="btn btn-small btn-danger"
                                >
                                    Delete
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
                {users.length === 0 && !loading && (
                    <p className="no-users">No users found</p>
                )}
            </div>
        </div>
    );
};

export default UserManagement; 