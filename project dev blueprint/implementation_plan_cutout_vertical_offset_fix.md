# Fix Vertical Offset Display Cutout Alignment for Motorola Edge 60 Pro

## Background & Problem
The user reported that while vertical offset adjustment is working, `y = 0` positions the overlay **below the status bar** instead of at the top edge of the screen where the physical pinhole camera is located on the Motorola Edge 60 Pro.

### Root Cause
In `IslandOverlayService.kt`, the system overlay window is created with `WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN`. However, on Android 9+ (API 28+), Android's WindowManager prevents overlay windows from extending into display cutouts by default. Because `layoutInDisplayCutoutMode` was not set on `layoutParams`, Android automatically inset the window below the status bar cutout boundary. Consequently, `y = 0` was aligned with the bottom of the status bar inset rather than the physical top edge of the display screen (where the camera cutout resides).

---

## User Review Required
> [!IMPORTANT]
> Enabling `LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS` / `SHORT_EDGES` allows `y = 0` to mean 0dp from the physical top edge of the screen.
> Presets and default `yOffsetDp` values are updated so `10dp` perfectly centers the island over the Motorola Edge 60 Pro punch-hole camera.

---

## Proposed Changes

### Overlay Window Layout

#### [MODIFY] [IslandOverlayService.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/IslandOverlayService.kt)
- Configure `layoutInDisplayCutoutMode` on `layoutParams` during initial setup in `setupOverlayView()`:
  - `LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS` for Android 11+ (API 30+)
  - `LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES` for Android 9 & 10 (API 28-29)
- Ensure `layoutInDisplayCutoutMode` is preserved when `updateWindowLayout()` updates the window parameters.

---

### Device Presets & Models

#### [MODIFY] [DevicePresets.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/data/DevicePresets.kt)
- Adjust preset `yOffsetDp` values to reflect absolute screen top indexing:
  - `MOTOROLA_EDGE_60_PRO`: `yOffsetDp = 10`
  - `MOTOROLA_EDGE_50_ULTRA`: `yOffsetDp = 10`
  - `SAMSUNG_S20_FE`: `yOffsetDp = 8`
  - `SAMSUNG_S23_PLUS`: `yOffsetDp = 8`
  - `GENERIC_PUNCH_HOLE`: `yOffsetDp = 8`

#### [MODIFY] [IslandModels.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/data/IslandModels.kt)
- Update default `yOffsetDp` in `IslandConfig` from 34 to 10.

#### [MODIFY] [PreferencesManager.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/data/PreferencesManager.kt)
- Update default fallback value for `KEY_Y_OFFSET` from 34 to 10.

---

### Documentation

#### [MODIFY] [README.md](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/README.md)
- Update README to document absolute pinhole cutout positioning and updated Motorola Edge 60 Pro preset offsets.

---

## Verification Plan

### Automated Tests / Build Verification
- Run `./gradlew assembleDebug` (or `gradlew.bat assembleDebug`) to verify clean Android build compilation with no errors.

### Manual Verification
- Deploy APK to target device / emulator or test service layout rendering.
- Verify `y = 0` positions the top of the island overlay at the physical top edge of the screen (0dp from top), extending through the status bar / cutout region.
- Verify Motorola Edge 60 Pro preset (`yOffsetDp = 10`) aligns directly on top of the pinhole camera.
