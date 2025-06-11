# Weather Forecast App ☀️🌧️

This is a simple Android weather application that fetches real-time weather data from the OpenWeatherMap API and displays current weather conditions based on the city entered by the user.

## Features

- 🌆 Enter any city to get real-time weather data
- 🌡️ Shows temperature, humidity, and wind speed
- 🌄 Sunrise and 🌇 sunset time display
- 🌤️ Displays weather condition with icon and background color
- 📅 Shows today's date in Turkish format
- 🎨 Smooth card animation after weather info loads

## Technologies Used

- **Language:** Java
- **Framework:** Android SDK
- **API:** OpenWeatherMap (https://openweathermap.org/)
- **UI Elements:** ConstraintLayout, CardView, ImageView, Toast, Animations

## Getting Started

1. Clone or download this repository.
2. Open the project in **Android Studio**.
3. Replace the `API_KEY` variable in `MainActivity.java` with your own key from [OpenWeatherMap](https://home.openweathermap.org/api_keys).
4. Run the app on your device or emulator.

## API Setup

You need an API key from [OpenWeatherMap](https://openweathermap.org/):
- Sign up and generate a key
- Add it here in `MainActivity.java`:

```java
private final String API_KEY = "YOUR_API_KEY_HERE";

👨‍💻 Developed by Gamze Yağmurcu
