# MyIsland - Native Dynamic Island (Quick Actions, Y-Offset Calibration & Swipe Delete Fixed)

We have resolved all 3 issues reported:

---

## 🛠️ Fixes & Enhancements

1. **Functional Quick Action Buttons (Torch, Audio Out, Mute App)**:
   - **Torch / Flashlight**: Created [CameraTorchManager.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/utils/CameraTorchManager.kt) to toggle physical camera LED flashlight on/off via `CameraManager`.
   - **Audio Out**: Launches system Sound Settings (`Settings.ACTION_SOUND_SETTINGS`).
   - **Mute App & Close**: Dismisses active menu and clears current notification.

2. **Real-time Y-Offset Slider Calibration**:
   - Added `SharedPreferences.OnSharedPreferenceChangeListener` in [IslandOverlayService.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/IslandOverlayService.kt) to update `layoutParams.y = (islandConfig.yOffsetDp * density).toInt()` in real time when moving sliders in Settings.

3. **Swipe Left Notification Deletion**:
   - Added `clearLatestNotification()` in [IslandNotificationListenerService.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/IslandNotificationListenerService.kt). Swiping left on an island notification item deletes it from memory and collapses the island.

---

## 🌐 GitHub Repository Status

- Repository: **[https://github.com/dvlshlkhln/MyIsland](https://github.com/dvlshlkhln/MyIsland)**
- All fixes committed to `working1` and merged into `main`.
