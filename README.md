# Dress Den - Android E-commerce App

A full-featured Android e-commerce application built with Kotlin and Java, implementing modern Android development practices and advanced functionalities.

## Architecture & Technologies

### Core Technologies
- Kotlin & Java mixed codebase
- MVVM Architecture
- Android Architecture Components
- Dependency Injection with Hilt
- Firebase Authentication & Messaging
- Room Database
- Retrofit for API communication
- WorkManager for background tasks
- Google Maps integration

### Key Features
1. **Authentication & Security**
   - Firebase Authentication
   - Secure API communication
   - Token management

2. **Location & Maps**
   - Real-time location tracking
   - Store locator
   - Delivery tracking

3. **Media Handling**
   - Camera integration
   - Image processing
   - Gallery management

4. **Sensors & Hardware**
   - Accelerometer
   - Gyroscope
   - Proximity sensor
   - Device orientation

5. **Background Processing**
   - Order synchronization
   - Product updates
   - Inventory management

6. **Notifications**
   - Push notifications (Firebase)
   - Order status updates
   - Promotional messages
   - Chat messages

7. **Telephony**
   - Phone calls
   - SMS integration
   - Contact management

## Project Structure

```
app/
├── build.gradle           # App level build configuration
├── src/
│   └── main/
│       ├── java/com/dressden/app/
│       │   ├── data/           # Data layer
│       │   │   ├── api/       # API interfaces and implementations
│       │   │   ├── local/     # Local database and preferences
│       │   │   ├── models/    # Data models
│       │   │   └── repository/# Repositories
│       │   ├── di/            # Dependency injection
│       │   ├── services/      # Background services
│       │   ├── ui/            # UI layer
│       │   │   ├── activities/# Activities
│       │   │   ├── adapters/  # RecyclerView adapters
│       │   │   ├── fragments/ # Fragments
│       │   │   └── viewmodels/# ViewModels
│       │   └── utils/         # Utilities
│       │       ├── animations/# Animation utilities
│       │       ├── location/  # Location utilities
│       │       ├── media/     # Media utilities
│       │       ├── permissions/# Permission handling
│       │       ├── sensors/   # Sensor utilities
│       │       ├── telephony/ # Phone and SMS utilities
│       │       └── workers/   # Background workers
│       └── res/              # Resources
```

## Setup Instructions

1. **Prerequisites**
   - Android Studio Arctic Fox or later
   - JDK 11 or later
   - Android SDK 31 or later

2. **Firebase Setup**
   - Create a Firebase project
   - Add Android app to Firebase project
   - Download `google-services.json`
   - Place in app/ directory

3. **API Configuration**
   - Update `Constants.kt` with your API endpoints
   - Configure API authentication in `AuthInterceptor.kt`

4. **Maps Setup**
   - Get Google Maps API key
   - Add to `AndroidManifest.xml`

5. **Build & Run**
   ```bash
   # Clone repository
   git clone https://github.com/your-repo/dress-den.git

   # Open in Android Studio
   # Sync project with Gradle files
   # Run on device or emulator
   ```

## Architecture Components

### Managers
- `LocationManager`: Handles location services and tracking
- `MediaManager`: Manages camera and media operations
- `NotificationManager`: Handles all notifications
- `PermissionManager`: Manages runtime permissions
- `SensorManager`: Controls device sensors
- `TelephonyManager`: Handles calls and SMS
- `WorkerManager`: Manages background tasks

### Background Processing
- `OrderSyncWorker`: Synchronizes orders with backend
- `FirebaseMessagingService`: Handles push notifications

### Base Components
- `BaseActivity`: Common functionality for activities
- `DressDenApplication`: Application initialization

## Contributing

1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
