# SWP391 Job Portal - Setup Guide

## Environment Variables Setup

### 1. Database Configuration

```bash
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

### 2. Google OAuth2 Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing project
3. Enable Google+ API
4. Create OAuth2 credentials
5. Set redirect URI: `http://localhost:8080/login/oauth2/code/google`

```bash
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GOOGLE_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google
```

### 3. Gmail SMTP Setup

1. Enable 2-Factor Authentication on your Gmail
2. Generate App Password: [https://myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)

```bash
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_16_digit_app_password
```

## How to Run

### Option 1: Using Environment Variables

```bash
# Windows
set GOOGLE_CLIENT_ID=your_client_id
set GOOGLE_CLIENT_SECRET=your_client_secret
set MAIL_USERNAME=your_email@gmail.com
set MAIL_PASSWORD=your_app_password

./gradlew bootRun
```


### Option 2: Using .env file

1. Copy `env.example` to `.env`
2. Fill in your actual credentials in `.env`
3. Install dotenv plugin for your IDE
4. Run the application

### Option 3: Using application-local.properties

1. Create `src/main/resources/application-local.properties`
2. Add your credentials:

```properties
spring.datasource.username=root
spring.datasource.password=your_password
spring.security.oauth2.client.registration.google.client-id=your_client_id
spring.security.oauth2.client.registration.google.client-secret=your_client_secret
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

3. Run with profile: `./gradlew bootRun --args='--spring.profiles.active=local'`

## Security Notes

- Never commit actual credentials to Git
- Use different credentials for development and production
- Regularly rotate your API keys and passwords
- Use strong, unique passwords
