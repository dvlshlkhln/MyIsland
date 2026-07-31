# MyIsland - Native Dynamic Island for Motorola & Samsung Devices

[![Android CI - Auto Build APK](https://github.com/dvlshlkhln/MyIsland/actions/workflows/android-build.yml/badge.svg)](https://github.com/dvlshlkhln/MyIsland/actions/workflows/android-build.yml)

**MyIsland** is a native Android application built in **Kotlin** with **Jetpack Compose** that brings the iOS Dynamic Island experience to Android smartphones, framed seamlessly around center punch-hole camera cutouts.

---

## 📥 Automated APK Builds

Every commit pushed or merged into the `main` branch automatically triggers GitHub Actions CI to compile and publish the latest downloadable APK.

- Go to **[GitHub Actions Artifacts](https://github.com/dvlshlkhln/MyIsland/actions)** to download the latest compiled `MyIsland-debug.apk`.

---

## ✨ Features

- 👯 **Dual-Pill Split Sub-Pill Architecture (iOS Multi-Session)**: When music and a countdown timer run simultaneously, the island splits organically into a main left pill and a secondary detached right sub-pill.
- 🌊 **4-Bar Sine Wave Audio Visualizer**: Dynamic equalizer bars with phase-shifted height physics matching active playback rhythm.
- 💨 **Fluid Bouncy Spring Physics**: Morphing animations spec tuned to `Spring.StiffnessMediumLow` & `Spring.DampingRatioMediumBouncy` for authentic iOS fluid motion.
- 🔦 **Physical Camera Flashlight Toggle (Quick Action)**: Tapping "Torch" in the long-press radial action menu turns your physical camera LED flashlight on/off via `CameraManager`.
- 🔊 **Audio Output & App Mute Control**: Tapping "Audio Out" opens System Sound settings, while "Mute App" instantly dismisses the active notification.
- 📐 **Live Y-Offset & X-Offset Position Calibration**: Adjusting Y-Offset or X-Offset sliders in Settings updates the floating overlay position in REAL TIME on screen (`layoutParams.y`).
- 👈 **Swipe Left Gesture Notification Deletion**: Swiping left on an island notification item clears it from `IslandNotificationListenerService` and collapses the island with a slide-out animation and haptic vibration.
- 🔊 **iOS-Style Top Volume & Ringer Island HUD**: Top-mounted progress slider popping directly from your punch-hole camera on side volume button presses.
- 💬 **In-Island Quick Reply**: Type and send replies directly inside the expanded notification card for WhatsApp, Telegram, and SMS.
- 🔒 **Lockscreen & Always-On Display (AOD) Support**: Stays active on your lockscreen (`FLAG_SHOW_WHEN_LOCKED`) for ongoing music, timers, and caller alerts.
- 🎯 **Touch-Passthrough Window Architecture (UI Freeze Fix)**: Dynamically adjusts `WindowManager` overlay dimensions to strictly cover the island pill area with `FLAG_NOT_TOUCH_MODAL`, allowing taps anywhere else on the screen to pass through 100% unimpeded.
- 🎨 **Dynamic Palette Accent Engine**: Real-time album cover artwork & app icon color extraction via Android `Palette` API. Automatically tints progress bars, play buttons, visualizer bars, and floating island glowing shadows to match active media.
- 🔋 **Smart Power Saver & Zero Standby Battery Drain**: Automatically suspends Compose rendering pipelines and sets overlay visibility to `View.GONE` when the display turns off (`ACTION_SCREEN_OFF`), ensuring 0.0% standby battery drain.
- 🏝️ **Native System Overlay (`TYPE_APPLICATION_OVERLAY`)**: Floats smoothly on top of all apps, status bars, and lock screens with physics-based spring animations.
- 📱 **1-Tap Device Calibration Presets**:
  - **Motorola Edge 60 Pro** (Default)
  - **Samsung Galaxy S20 FE**
  - **Samsung Galaxy S23 Plus**
  - **Motorola Edge 50 Ultra / 40 Pro**
  - **Generic Center Punch-Hole**
- 📞 **Live Phone Call Manager**: Displays incoming & active phone calls with caller name/avatar, live call timer, and interactive **Mute** & **End Call** controls.
- ⏱️ **Active Countdown Timers**: Streams system clock countdown timers & stopwatches with live `MM:SS` display and **+1 Min** quick extension button.
- 🎧 **Bluetooth Audio Accessory Banners**: Animated popups when Bluetooth headphones (Galaxy Buds, Moto Buds, AirPods, Sony) connect or disconnect.
- 🎵 **Media Player Interception**: Integrates with Android `MediaSessionController` for Spotify, YouTube Music, Apple Music, VLC, etc., featuring live mini audio visualizers and expandable playback controls.
- ⚡ **Charging Alerts**: Real-time animated battery status and charging indicators.
- 📳 **Tactile Haptic Feedback Engine**: Haptic vibrations for taps, expansions, and playback control presses.

---

## 🚀 Getting Started

1. Clone this repository:
   ```bash
   git clone https://github.com/dvlshlkhln/MyIsland.git
   ```
2. Open the project in **Android Studio**.
3. Connect your Android handset (Motorola Edge 60 Pro, Samsung S20 FE, Samsung S23 Plus, etc.) via USB / Wireless ADB.
4. Build and run the app.
5. In the app:
   - Grant **Display Over Other Apps** and **Notification Access**.
   - Select your device model from the **Device Alignment Presets** dropdown (or fine-tune manual position sliders).
   - Toggle **Dynamic Island Service** to **ON**.

---

## 🛠️ Built With

- **Kotlin** & **Jetpack Compose** (Material 3)
- **GitHub Actions CI/CD** (Automated APK Compiler)
- **Android WindowManager** & **Lifecycle Service**
- **Android Palette API (`androidx.palette:palette-ktx`)** (Dynamic Accent Engine)
- **ScreenStateReceiver** & **PowerManager** (Smart Render Throttling)
- **MediaSessionController** & **NotificationListenerService**
- **TelephonyManager / TelecomManager** & **Bluetooth Broadcast Receivers**
