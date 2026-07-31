# MyIsland - Native Dynamic Island (Touch Freeze Fix & Swipe Dismiss Added)

We have resolved the UI touch blocking / freezing issue and added the requested **Swipe Left Gesture & Dismiss Animation**.

---

## 🛠️ Fixes & Enhancements

1. **Touch-Passthrough Window Architecture (UI Freeze Resolution)**:
   - **Root Cause**: Previously, `WindowManager.LayoutParams` set the root `ComposeView` window size to `MATCH_PARENT` x `MATCH_PARENT`, causing an invisible full-screen transparent view to intercept taps intended for underlying apps.
   - **Fix**: Implemented `updateWindowLayout(mode, isScreenOn)` in [IslandOverlayService.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/IslandOverlayService.kt). The window size is dynamically resized to strictly match ONLY the island pill dimensions (`compactWidthDp` x `compactHeightDp + yOffsetDp`) with `WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL`. Taps anywhere else on the screen pass through 100% unimpeded!

2. **Swipe Left Dismiss Gesture & Animation**:
   - Added horizontal drag gesture detection (`detectHorizontalDragGestures`) in [DynamicIslandView.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/ui/overlay/DynamicIslandView.kt).
   - Swiping left smoothly shifts the pill to the left with an alpha fade-out animation and heavy haptic feedback, dismissing the current alert/notification.

---

## 🌐 GitHub Repository Status

- Repository: **[https://github.com/dvlshlkhln/MyIsland](https://github.com/dvlshlkhln/MyIsland)**
- Changes committed to `working1` and merged into `main`.
