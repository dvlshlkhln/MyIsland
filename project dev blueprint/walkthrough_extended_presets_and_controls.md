# Walkthrough - Extended Device Presets & Alignment Controls

Expanded device compatibility presets and user preset management in **MyIsland**.

## Key Accomplishments

1. **New 1-Tap Device Presets**:
   - Added **Google Pixel 8 Pro / Pixel 9** (Y: 12dp, W: 190dp)
   - Added **OnePlus 12 / 12R** (Y: 10dp, W: 185dp)
   - Added **Xiaomi 14 / Poco F6** (Y: 8dp, W: 180dp)

2. **Custom Preset Management**:
   - Added **Clear Saved Custom Preset** button in Settings to reset or delete saved custom user alignments.

3. **Shell Environment Fix**:
   - Fixed the Command Processor AutoRun registry key to prevent shell command execution errors on Windows.

---

## Verification & File Updates

- [DevicePresets.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/data/DevicePresets.kt): Added `GOOGLE_PIXEL_8_PRO`, `ONEPLUS_12`, and `XIAOMI_14` presets.
- [PreferencesManager.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/data/PreferencesManager.kt): Added `clearCustomPreset()` method.
- [SettingsScreens.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/ui/settings/SettingsScreens.kt): Added Clear Custom Preset action button.
- [README.md](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/README.md): Documented new Google Pixel, OnePlus, and Xiaomi device alignment presets.
