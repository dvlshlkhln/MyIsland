# Native Android Dynamic Island App ("MyIsland") for Motorola Edge 60 Pro

Create a native Android application in Kotlin using Jetpack Compose and System Overlay Services (`TYPE_APPLICATION_OVERLAY`) to imitate the iOS Dynamic Island functionality, tailored specifically for the Motorola Edge 60 Pro punch-hole camera display.

## User Review Required

> [!IMPORTANT]
> **Android Permissions Required**: System Overlays (`SYSTEM_ALERT_WINDOW`) and Notification Listener (`BIND_NOTIFICATION_LISTENER_SERVICE`) require manual user approval via Android System Settings. The app includes a step-by-step Onboarding UI to guide you through granting these permissions effortlessly.

> [!NOTE]
> **Motorola Edge 60 Pro Cutout Alignment**: The app features a live visual calibration slider to fine-tune the pill's X/Y offset, width, and height directly over your Motorola Edge 60 Pro's center punch-hole camera.

## Open Questions

None at this time. The default configuration is pre-configured for center punch-hole displays with full customization controls.

## Proposed Changes

### Project Foundation & Build System

#### [NEW] [settings.gradle.kts](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/settings.gradle.kts)
- Configure Gradle plugin repositories and app module includes.

#### [NEW] [build.gradle.kts](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/build.gradle.kts)
- Root build script with Kotlin 1.9+ and Android Gradle Plugin 8.x configuration.

#### [NEW] [app/build.gradle.kts](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/build.gradle.kts)
- App dependencies: Jetpack Compose (Material3, Animations), Lifecycle Service, Coroutines, MediaController, Accompanist/Core KTX.

---

### Android Manifest & Core Application Structure

#### [NEW] [app/src/main/AndroidManifest.xml](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/AndroidManifest.xml)
- Declare permissions: `SYSTEM_ALERT_WINDOW`, `BIND_NOTIFICATION_LISTENER_SERVICE`, `POST_NOTIFICATIONS`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`.
- Register `MainActivity`, `IslandOverlayService`, `IslandNotificationListenerService`, and broadcast receivers.

---

### Services & Data Logic (Kotlin Core)

#### [NEW] [app/src/main/java/com/myisland/dynamic/data/IslandModels.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/data/IslandModels.kt)
- Define `IslandState` (Idle, CompactPill, ExpandedCard, ToastBanner), `MediaState` (track title, artist, album art, duration, position, isPlaying), `NotificationItem` (app icon, title, text, timestamp), and `IslandConfig` (position, size, features enabled).

#### [NEW] [app/src/main/java/com/myisland/dynamic/data/PreferencesManager.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/data/PreferencesManager.kt)
- Persistent settings storage using `SharedPreferences` / `DataStore` for cutout offset (X, Y, Width, Height), animation speeds, and enabled modules.

#### [NEW] [app/src/main/java/com/myisland/dynamic/service/IslandOverlayService.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/IslandOverlayService.kt)
- Foreground Service managing the `WindowManager` overlay view.
- Embeds Compose `ComposeView` with `TYPE_APPLICATION_OVERLAY`.
- Handles interactive window layout updates (touch passthrough when collapsed, intercept touch when expanded).

#### [NEW] [app/src/main/java/com/myisland/dynamic/service/IslandNotificationListenerService.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/IslandNotificationListenerService.kt)
- Binds to Android system notifications to stream real-time incoming alerts, active timers, incoming calls, and messaging notifications to the Dynamic Island.

#### [NEW] [app/src/main/java/com/myisland/dynamic/service/MediaSessionManager.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/MediaSessionManager.kt)
- Listens to active Android `MediaSessionController` (Spotify, YouTube Music, Apple Music, etc.) to capture album artwork, title, playback state, and provide play/pause/skip actions.

#### [NEW] [app/src/main/java/com/myisland/dynamic/service/SystemEventReceiver.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/SystemEventReceiver.kt)
- Receives broadcast events for charging connected/disconnected, battery low, volume/ringer state changes, and bluetooth connection.

---

### Jetpack Compose UI (Overlay & Settings App)

#### [NEW] [app/src/main/java/com/myisland/dynamic/ui/overlay/DynamicIslandView.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/ui/overlay/DynamicIslandView.kt)
- Main Compose overlay surface with fluid spring animation transitions between Compact Pill, Expanded Card, and Quick Toast states.

#### [NEW] [app/src/main/java/com/myisland/dynamic/ui/overlay/CompactPillContent.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/ui/overlay/CompactPillContent.kt)
- Mini status elements (music visualizer, animated battery indicator, app icon badges, timer indicator).

#### [NEW] [app/src/main/java/com/myisland/dynamic/ui/overlay/ExpandedCardContent.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/ui/overlay/ExpandedCardContent.kt)
- Detailed rich cards: Full Media Player (Album Art, Seek Bar, Controls), Detailed Notification Card with quick actions, Charging Status banner.

#### [NEW] [app/src/main/java/com/myisland/dynamic/ui/settings/MainActivity.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/ui/settings/MainActivity.kt)
- Main setup application entry point written in Jetpack Compose with Material3 dark theme.

#### [NEW] [app/src/main/java/com/myisland/dynamic/ui/settings/SettingsScreens.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/ui/settings/SettingsScreens.kt)
- Tabbed settings screens:
  1. **Dashboard & Service Toggle**: Quick Start/Stop toggle with service status indicator.
  2. **Camera Cutout Calibration**: Sliders for X Position, Y Offset, Width, Height, Corner Radius with live on-screen overlay preview.
  3. **Permission Setup Wizard**: Direct buttons to enable Overlay permission & Notification Listener.
  4. **Feature Toggles**: Customize active widgets (Music, Charging, Notifications, Gestures).

---

## Verification Plan

### Manual Verification
1. Open the project in Android Studio or build APK.
2. Launch **MyIsland** app on Motorola Edge 60 Pro (or emulator).
3. Grant **Display Over Apps** and **Notification Access** permissions via the onboarding screen.
4. Toggle the service **ON**.
5. Adjust position sliders to align the pill surrounding the Motorola Edge 60 Pro punch-hole camera.
6. Test features:
   - Play music in Spotify / YouTube Music -> observe Dynamic Island compact music visualizer & tap to expand player controls.
   - Plug in charger -> observe dynamic charging animation & battery percentage toast.
   - Send test notification -> observe floating notification bubble expanding from camera cutout.
