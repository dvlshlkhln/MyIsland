# MyIsland - Native Dynamic Island for Motorola Edge 60 Pro

**MyIsland** is a native Android application built in Kotlin with Jetpack Compose that brings the iOS Dynamic Island experience to the **Motorola Edge 60 Pro**, framed seamlessly around its center punch-hole camera cutout.

---

## ✨ Features

- 🏝️ **Native System Overlay (`TYPE_APPLICATION_OVERLAY`)**: Floats smoothly on top of all apps, status bars, and home screens.
- 🎵 **Media Player Interception**: Integrates with Android `MediaSessionController` for Spotify, YouTube Music, Apple Music, VLC, etc., featuring animated audio visualizer bars and expandable playback controls.
- ⚡ **Charging Alerts**: Real-time animated battery status and charging indicators.
- 🔔 **Notification Popups**: Streamlined notification previews for incoming messages and system alerts.
- 🎯 **Motorola Cutout Calibration**: Built-in setup screen with live sliders for Y-offset, width, height, and corner rounding.

---

## 🚀 Getting Started

1. Clone this repository:
   ```bash
   git clone https://github.com/DevalShalkhlan/MyIsland.git
   ```
2. Open the project in **Android Studio**.
3. Connect your **Motorola Edge 60 Pro** handset via USB / Wireless ADB.
4. Build and run the app.
5. In the app:
   - Grant **Display Over Other Apps** and **Notification Access**.
   - Toggle **Dynamic Island Service** to **ON**.
   - Fine-tune positioning sliders to align with your camera punch hole.

---

## 🛠️ Built With

- **Kotlin**
- **Jetpack Compose** & **Material 3**
- **Android WindowManager** & **Lifecycle Service**
- **MediaSessionController** & **NotificationListenerService**
