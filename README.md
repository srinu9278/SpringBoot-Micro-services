# Hotel Management System

## Environment Setup

### Required Environment Variables

Create a `.env` file in the root directory with the following variables:

```bash
# Slack Configuration
SLACK_BOT_TOKEN=your-slack-bot-token-here
SLACK_DEFAULT_CHANNEL=#hotel-general
SLACK_BOOKINGS_CHANNEL=#hotel-bookings
SLACK_PAYMENTS_CHANNEL=#hotel-payments
SLACK_ROOMS_CHANNEL=#hotel-rooms
SLACK_GUESTS_CHANNEL=#hotel-guests
SLACK_MAINTENANCE_CHANNEL=#hotel-maintenance
SLACK_ALERTS_CHANNEL=#hotel-alerts
SLACK_GENERAL_CHANNEL=#hotel-general

# Database Configuration
MONGODB_URI=mongodb://localhost:27017/notification_db
```

### Getting Slack Bot Token

1. Go to [Slack API](https://api.slack.com/apps)
2. Create a new app or select existing app
3. Go to "OAuth & Permissions"
4. Copy the "Bot User OAuth Token" (starts with `xoxb-`)
5. Set it as `SLACK_BOT_TOKEN` in your `.env` file

### Running the Services

1. **Set environment variables** (create `.env` file)
2. **Start services in order:**
   - Eureka Server (port 8761)
   - Config Service (port 8888)
   - Notification Service (port 9006)
   - Other services

### Security Notes

- Never commit `.env` files to Git
- All secrets are stored in environment variables
- The `.env` file is gitignored for security
