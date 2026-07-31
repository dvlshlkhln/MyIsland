# MyIsland - Native Dynamic Island (Motorola Edge 60 Pro Calibration Fixed)

We have resolved the Y-Offset alignment issue for **Motorola Edge 60 Pro**.

---

## 🛠️ Motorola Edge 60 Pro Fixes Applied

1. **Real-time SharedPreferences Sync**:
   - Fixed SharedPreferences name mismatch (`"myisland_preferences"` in [PreferencesManager.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/data/PreferencesManager.kt) vs `"myisland_prefs"` in [IslandOverlayService.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/IslandOverlayService.kt)).
   - Slider movements in Settings now immediately update `layoutParams.y` and `layoutParams.x` in real time on screen.

2. **Edge 60 Pro Preset Calibration**:
   - Updated Motorola Edge 60 Pro default Y-Offset to `34dp` in [DevicePresets.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/data/DevicePresets.kt) so the island frames the punch-hole camera lens.
   - Extended vertical slider range to `0dp – 150dp` in [SettingsScreens.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/ui/settings/SettingsScreens.kt).

---

## 🌐 GitHub Repository Status

- Repository: **[https://github.com/dvlshlkhln/MyIsland](https://github.com/dvlshlkhln/MyIsland)**
- All calibration fixes committed to `working1` and merged into `main`.
