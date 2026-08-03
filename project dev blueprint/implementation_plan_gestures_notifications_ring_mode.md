# Implementation Plan - Gesture Fixes, System Notification Filtering, Media Recovery & Camera Ring Mode

Fix all touch gestures, filter system overlay notifications, restore active music session detection, and implement the customizable Camera Ring collapsed mode.

## User Review Required

> [!IMPORTANT]
> - **Gestures**: Replaces fragmented pointer inputs with a single unified pointer input block in `DynamicIslandView.kt` so single tap, double tap, long press, swipe left, and swipe right all work reliably.
> - **Notification Filtering**: Ignores system packages (`android`, `com.android.systemui`), "displaying over other apps" alerts, and non-navigation Google Maps system overlays.
> - **Media Detection**: Selects active playing media session (`STATE_PLAYING` or non-blank track title) across all system media controllers.
> - **Camera Ring Collapsed Mode**: Adds `IslandCollapseStyle` (`PILL` vs `CAMERA_RING`) with customizable Ring Diameter (36dp–72dp) and Ring Thickness (2dp–8dp).

## Proposed Changes

### Gesture Engine
#### [MODIFY] [DynamicIslandView.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/ui/overlay/DynamicIslandView.kt)
- Combine gesture detectors into a unified pointer input handling tap, double-tap, long-press, swipe-left (dismiss item/notification), and swipe-right (skip music track).
- Support Camera Ring collapsed rendering mode around camera cutout.

---

### Notification & System Alert Filter Engine
#### [MODIFY] [IslandNotificationListenerService.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/IslandNotificationListenerService.kt)
- Ignore system alerts containing "displaying over other apps", system UI overlay notifications, and non-navigation Maps background alerts.
- Filter out system packages (`android`, `com.android.systemui`, `com.google.android.systemui`).

---

### Media Session Controller Engine
#### [MODIFY] [MediaSessionManager.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/MediaSessionManager.kt)
- Iterate through active media sessions to prioritize the playing music controller (`STATE_PLAYING` or active metadata).

---

### Data Models & Settings UI
#### [MODIFY] [IslandModels.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/data/IslandModels.kt)
- Add `IslandCollapseStyle` enum (`PILL`, `CAMERA_RING`), `cameraRingDiameterDp`, and `cameraRingThicknessDp` to `IslandConfig`.

#### [MODIFY] [PreferencesManager.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/data/PreferencesManager.kt)
- Save and load `collapseStyle`, `cameraRingDiameterDp`, and `cameraRingThicknessDp`.

#### [MODIFY] [SettingsScreens.kt](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/ui/settings/SettingsScreens.kt)
- Add Collapse Style selection card (`Classic Pill` vs `Camera Ring Accent`) with ring diameter and thickness sliders.

---

### Documentation & Blueprint Preservation
#### [MODIFY] [README.md](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/README.md)
- Update features and gesture documentation.
#### [NEW] [project dev blueprint/implementation_plan_gestures_notifications_ring_mode.md](file:///c:/Users/Deval/Shalkhlan/Desktop/reactApp/MyIsland/project%20dev%20blueprint/implementation_plan_gestures_notifications_ring_mode.md)
- Save feature blueprint copy.

## Verification Plan

### Automated Verification
- Code compilation check.

### Manual Verification
- Test double tap, long press, swipe left, and swipe right gestures.
- Verify system overlay notifications ("displaying over other apps") are ignored.
- Test active Spotify / YouTube Music playback detection.
- Test switching between Classic Pill and Camera Ring collapsed modes.
