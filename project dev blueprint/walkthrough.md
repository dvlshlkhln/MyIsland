# MyIsland - Native Android Dynamic Island for Motorola Edge 60 Pro

We have successfully built a complete native Android application in **Kotlin** with **Jetpack Compose** that reproduces the iOS Dynamic Island experience around the center punch-hole camera of your **Motorola Edge 60 Pro**.

---

## 🛠️ Project Structure Created

The codebase is located in [MyIsland](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland):

```
MyIsland/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        └── java/com/myisland/dynamic/
            ├── data/
            │   ├── IslandModels.kt        # Data classes (MediaState, NotificationItem, ChargingState, IslandConfig)
            │   └── PreferencesManager.kt  # Persistent settings storage for cutout alignment & features
            ├── service/
            │   ├── IslandOverlayService.kt              # Floating System Overlay window with ComposeView
            │   ├── IslandNotificationListenerService.kt # Listens for incoming system notifications & apps
            │   ├── MediaSessionManager.kt               # Intercepts active playback (Spotify, YT Music, etc.)
            │   └── SystemEventReceiver.kt               # Battery, charging, and ringer state broadcasts
            ├── ui/
            │   ├── theme/                 # Dark theme, colors, typography
            │   ├── overlay/
            │   │   ├── DynamicIslandView.kt  # Physics-based spring animated container
            │   │   ├── CompactPillContent.kt # Mini pill view (Music visualizer, battery %, app icon)
            │   │   └── ExpandedCardContent.kt# Rich player, notification preview, charging card
            │   └── settings/
            │       ├── MainActivity.kt        # Dashboard launcher activity
            │       └── SettingsScreens.kt     # Jetpack Compose calibration & settings screens
            └── utils/
                └── PermissionUtils.kt     # System Alert Window & Notification listener permission helpers
```

---

## 🌟 Key Features Implemented

1. **Overlay Surface (`TYPE_APPLICATION_OVERLAY`)**:
   - Floats directly on top of all apps, status bar, and home screen using Android's native `WindowManager`.
   - Native Compose UI with physics-driven spring animations (`animateContentSize`, `Spring.DampingRatioLowBouncy`).

2. **Motorola Edge 60 Pro Punch-Hole Calibration**:
   - Live calibration sliders in the app to adjust:
     - **Vertical Offset (Y-Axis)**: Fine-tune distance from top of the screen to align with the camera cutout.
     - **Pill Width & Height**: Customize compact state size.
     - **Corner Rounding**: Match device corner radii.

3. **Active Media Controls**:
   - Connects to Android `MediaSessionController` to intercept playback from Spotify, YouTube Music, Apple Music, VLC, etc.
   - Shows live mini-audio visualizer bars in compact mode.
   - Expands on tap to reveal full media player controls (Album artwork, Track title, Artist, seek bar, Play/Pause, Next, Previous).

4. **Charging & Notification Alerts**:
   - Displays animated charging indicator with live battery % when plugged into TurboPower / USB-C charger.
   - Intercepts incoming messages/alerts (WhatsApp, Telegram, System alerts) and pops out smoothly from the punch-hole.

---

## 🚀 How to Run on Motorola Edge 60 Pro

1. Open **Android Studio**.
2. Select **Open Project** and navigate to:
   `c:\Users\Deval Shalkhlan\Desktop\reactApp\MyIsland`
3. Connect your Motorola Edge 60 Pro via USB (or wireless ADB).
4. Click **Run App** (or press `Shift + F10`).
5. In the **MyIsland** dashboard on your phone:
   - Tap **Grant** for **Display Over Other Apps**.
   - Tap **Grant** for **Notification & Media Access**.
   - Toggle the **Dynamic Island Service** to **ON**.
   - Use the **Position & Cutout Alignment** sliders to perfectly center the pill around your camera cutout.
