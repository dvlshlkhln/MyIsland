# Ultra-Smooth iOS Dynamic Island Feature Plan

Plan to upgrade **MyIsland** to match iOS 18 Dynamic Island appearance with physics-based spring morphing and Dual-Pill multi-session splitting.

## User Review Required

> [!IMPORTANT]
> Please review the proposed visual and animation smoothness enhancements below!

---

## 🎨 Proposed Enhancements

### 1. Organic Fluid Spring Morphing & Scale Physics
- **Animation System**: Upgrade `animateContentSize` and width/height interpolators to custom spring specifications:
  - `stiffness = Spring.StiffnessMediumLow`
  - `dampingRatio = Spring.DampingRatioMediumBouncy`
- **Effect**: Expanding and collapsing feels fluid, bouncy, and organic—just like iOS.

### 2. Dual-Pill Split Architecture (Dual Dynamic Island Sub-Pills)
- **Multi-Session Support**: When two background activities are active simultaneously (e.g., Spotify Music + Active Countdown Timer):
  - **Left Main Pill**: Displays Music Album Cover & Visualizer
  - **Right Detached Sub-Pill**: Displays Live Timer Badge (e.g., `04:12`)
- Tapping either pill expands its respective session card!

### 3. Hardware Camera Lens Blend Rim
- Render an ultra-subtle glossy inner border and camera cutout mask so the physical camera lens melts seamlessly into the OLED black island pixels.

### 4. 4-Bar Sine Wave Frequency Audio Visualizer
- Upgrade music visualizer to a 4-bar sine-wave physics animation with dynamic height phase shifts according to active media playback.

---

## Verification Plan

### Manual Verification
1. Play music + start timer simultaneously -> verify dual-pill splitting.
2. Expand and collapse island -> verify fluid spring bouncy animation.
3. Observe visualizer bars during playback.
