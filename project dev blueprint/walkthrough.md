# MyIsland - Native Dynamic Island for Motorola & Samsung Devices

We have successfully implemented **Phase 1** of the **MyIsland** roadmap, adding instant 1-tap device calibration presets for **Samsung Galaxy S20 FE** and **Samsung Galaxy S23 Plus** alongside Motorola Edge series smartphones, as well as tactile haptic feedback.

---

## 📱 Device Presets Included

1. **Motorola Edge 60 Pro** (Default: Y = 12dp, Compact Width = 190dp, Height = 38dp, Radius = 24dp)
2. **Samsung Galaxy S20 FE** (Y = 16dp, Compact Width = 180dp, Height = 36dp, Radius = 22dp)
3. **Samsung Galaxy S23 Plus** (Y = 14dp, Compact Width = 175dp, Height = 35dp, Radius = 24dp)
4. **Motorola Edge 50 Ultra / 40 Pro** (Y = 12dp, Compact Width = 185dp, Height = 36dp, Radius = 24dp)
5. **Generic Center Punch-Hole** (Y = 14dp, Compact Width = 180dp, Height = 36dp, Radius = 22dp)

---

## 🛠️ Codebase Additions (Phase 1)

- [DevicePresets.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/data/DevicePresets.kt): Added device preset data models and preset configurations.
- [HapticManager.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/utils/HapticManager.kt): Integrated Android `Vibrator` / `VibrationEffect` engine for tactile clicks on tap, expansion, and media controls.
- [SettingsScreens.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/ui/settings/SettingsScreens.kt): Added 1-tap Device Alignment Dropdown card in the dashboard.
- [DynamicIslandView.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/ui/overlay/DynamicIslandView.kt): Connected haptics engine to island state transitions.

---

## 🌐 GitHub Repository Status

- Repository: **[https://github.com/dvlshlkhln/MyIsland](https://github.com/dvlshlkhln/MyIsland)**
- Branch `main` is up-to-date with all Phase 1 commits.
