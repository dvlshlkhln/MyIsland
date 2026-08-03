# MyIsland - Native Dynamic Island for Motorola & Samsung Devices

[![Android CI - Auto Build APK](https://github.com/dvlshlkhln/MyIsland/actions/workflows/android-build.yml/badge.svg)](https://github.com/dvlshlkhln/MyIsland/actions/workflows/android-build.yml)

**MyIsland** is a native Android application built in **Kotlin** with **Jetpack Compose** that brings the iOS Dynamic Island experience to Android smartphones, framed seamlessly around center punch-hole camera cutouts.

---

## 📥 Automated APK Builds

Every commit pushed or merged into the `main` branch automatically triggers GitHub Actions CI to compile and publish the latest downloadable APK.

- Go to **[GitHub Actions Artifacts](https://github.com/dvlshlkhln/MyIsland/actions)** to download the latest compiled `MyIsland-debug.apk`.

---

## ✨ Features

- 🤖 **Zero-Calibration Display Cutout Auto-Detection**: Uses native Android `DisplayCutout` API (`WindowInsets`) to automatically detect physical camera cutout bounds & status bar inset, auto-adjusting Y-offset and pill width instantly.
- 🖼️ **Real-Time Aesthetic Render Preview**: Interactive live preview box inside Settings showing instant visual changes to island size, themes, visualizers, and rounded corners.
- 🔍 **Smart Notification Whitelist & Search Manager**: Live app title search, category filter tabs (All, Allowed, Blocked), and 1-tap "Allow All" / "Block All" controls for all installed applications.
- 🖐️ **Interactive Gesture Cheat-Sheet Guide**: In-app visual onboarding breakdown of single-tap, double-tap, long-press, swipe-left, and swipe-right gesture controls.
- ⚡ **Configurable Auto-Collapse Delay & Haptic Engine**: Customizable auto-collapse timer (2s, 4s, 6s, 8s, 10s) and 4-tier haptic vibration intensity levels (Off, Light, Medium, Heavy).
- 📁 **Live Download & File Transfer Progress**: Intercepts ongoing downloads (Chrome, Telegram, system downloads) with live percentage rings, transfer speeds (`2.4 MB/s`), and expanded progress bars.
- 🎙️ **Voice Recorder & Screen Recording Pill**: Active recording pill featuring a pulsing red dot, duration timer (`00:12`), and quick action buttons.
- 🎛️ **Touch Gesture Controls on Compact Pill**: Swipe left/right for track navigation, horizontal drag for volume/seek adjustments, double tap to toggle playback, and single tap to expand.
- 🌐 **Personal Hotspot & Data Speed Monitor Banner**: Real-time connected client count badge and network throughput speed monitoring.
- 🔊 **In-Island Audio Output Device Switcher**: Native inline route picker to switch audio output between Phone Speaker, Bluetooth Earphones, and Wired Headphones.
- 🎨 **Island Aesthetic Themes & Visualizer Styles**: 4 dynamic aesthetic themes (Midnight OLED, Cyberpunk Neon, Sunset Gold, Glassmorphism) and 3 audio spectrum styles (4-Bar Equalizer, Waveform, Pulsating Ring).
- 📱 **Interactive Drag Calibration & App Whitelist Manager**: Touch-drag positioning calibration directly around your camera cutout with live DP coordinates, plus a granular app notification whitelist/blacklist manager in Settings.
- 🗺️ **Live Google Maps & Waze Turn-by-Turn Navigation**: Intercepts navigation directions, rendering real-time direction arrows (Turn Left, Turn Right, U-Turn), distance countdown (`150m`), and upcoming street names directly in the island pill.
- ✨ **Ambient Accent Aura Glow**: Glassmorphic glowing shadow aura around the island container dynamically tinted by album artwork or app branding color.
- 👯 **Dual-Pill Split Sub-Pill Architecture (iOS Multi-Session)**: When music and a countdown timer run simultaneously, the island splits organically into a main left pill and a secondary detached right sub-pill.
- 🌊 **4-Bar Sine Wave Audio Visualizer**: Dynamic equalizer bars with phase-shifted height physics matching active playback rhythm.
- 💨 **Fluid Bouncy Spring Physics**: Morphing animations spec tuned to `Spring.StiffnessMediumLow` & `Spring.DampingRatioMediumBouncy` for authentic iOS fluid motion.
- 🔦 **Physical Camera Flashlight Toggle (Quick Action)**: Tapping "Torch" in the long-press radial action menu turns your physical camera LED flashlight on/off via `CameraManager`.
- 🔊 **Audio Output & App Mute Control**: Tapping "Audio Out" opens System Sound settings, while "Mute App" instantly dismisses the active notification.
- 📐 **Live Y-Offset & X-Offset Position Calibration**: Adjusting Y-Offset or X-Offset sliders in Settings updates the floating overlay position in REAL TIME on screen (`layoutParams.y`). Built with `layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS` so `y = 0` sits directly at the top edge of the display canvas over physical camera cutouts.
- 👈 **Swipe Left Gesture Notification Deletion**: Swiping left on an island notification item clears it from `IslandNotificationListenerService` and collapses the island with a slide-out animation and haptic vibration.
- 🔊 **iOS-Style Top Volume & Ringer Island HUD**: Top-mounted progress slider popping directly from your punch-hole camera on side volume button presses.
- 💬 **In-Island Quick Reply**: Type and send replies directly inside the expanded notification card for WhatsApp, Telegram, and SMS.
- 🔒 **Lockscreen & Always-On Display (AOD) Support**: Stays active on your lockscreen (`FLAG_SHOW_WHEN_LOCKED`) for ongoing music, timers, and caller alerts.
- 🎯 **Touch-Passthrough Window Architecture (UI Freeze Fix)**: Dynamically adjusts `WindowManager` overlay dimensions to strictly cover the island pill area with `FLAG_NOT_TOUCH_MODAL`, allowing taps anywhere else on the screen to pass through 100% unimpeded.
- 🎨 **Dynamic Palette Accent Engine**: Real-time album cover artwork & app icon color extraction via Android `Palette` API. Automatically tints progress bars, play buttons, visualizer bars, and floating island glowing shadows to match active media.
- 🔋 **Smart Power Saver & Zero Standby Battery Drain**: Automatically suspends Compose rendering pipelines and sets overlay visibility to `View.GONE` when the display turns off (`ACTION_SCREEN_OFF`), ensuring 0.0% standby battery drain.
- 🏝️ **Native System Overlay (`TYPE_APPLICATION_OVERLAY`)**: Floats smoothly on top of all apps, status bars, and lock screens with physics-based spring animations.
- 📱 **1-Tap Device Calibration Presets**:
  - **Motorola Edge 60 Pro** (Default, Y: 10dp)
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
