# Dress Den

A full-featured Android e-commerce application built with Kotlin and Java, implementing modern Android development practices.

## Features

- User Authentication (Firebase)
- Product Management
- Shopping Cart
- Order Processing
- Real-time Updates
- Sensor Integration
- Background Processing
- Local Notifications
- Network State Monitoring
- Custom Animations

## Tech Stack

- Kotlin & Java (Mixed codebase)
- MVVM Architecture
- Firebase Authentication
- Room Database
- Retrofit for API communication
- Dagger Hilt for dependency injection
- WorkManager for background tasks
- Android Architecture Components
- Material Design
- Coroutines for asynchronous operations

## Project Structure

```
app/
├── src/
│   └── main/
│       ├── java/com/dressden/app/
│       │   ├── data/           # Data layer (repositories, models, data sources)
│       │   ├── di/            # Dependency injection modules
│       │   ├── ui/            # UI layer (activities, fragments, viewmodels)
│       │   └── utils/         # Utility classes
│       └── res/               # Android resources
```

## Setup Instructions

1. Clone the repository:
```bash
git clone https://github.com/lahiru31/Dressden.git
```

2. Open the project in Android Studio

3. Firebase Setup:
   - Create a new Firebase project in the Firebase Console
   - Add your Android app to the Firebase project
   - Download `google-services.json` and place it in the app directory
   - Enable Authentication in Firebase Console

4. Build and run the project

## Development Environment

- Android Studio Hedgehog | 2023.1.1
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Kotlin version: 1.9.20
- Gradle version: 8.2.0

## Architecture

The app follows MVVM (Model-View-ViewModel) architecture pattern and Clean Architecture principles:

- **UI Layer**: Activities, Fragments, ViewModels
- **Domain Layer**: Use Cases, Models
- **Data Layer**: Repositories, Data Sources (Local & Remote)

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
