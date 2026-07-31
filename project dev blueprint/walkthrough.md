# MyIsland - Native Dynamic Island (Phase 2 Completed)

Phase 2 of **MyIsland** is fully implemented and pushed to GitHub! This phase brings live phone call management, active countdown timers, and Bluetooth audio connection banners into the dynamic island.

---

## 🌟 Phase 2 Features Added

1. **Live Phone Call Controller ([CallSessionManager.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/CallSessionManager.kt))**:
   - Intercepts incoming (`CALL_STATE_RINGING`) and active (`CALL_STATE_OFFHOOK`) phone calls.
   - Shows caller contact avatar, live elapsed call timer, and interactive **Mute Microphone** & **End Call** controls in expanded island card.

2. **Active Countdown Timer ([TimerSessionManager.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/TimerSessionManager.kt))**:
   - Streams active clock timers & stopwatches to the island pill.
   - Shows live `MM:SS` countdown timer and **+1 Min** extension button.

3. **Bluetooth Headphone Banners ([BluetoothEventReceiver.kt](file:///c:/Users/Deval%20Shalkhlan/Desktop/reactApp/MyIsland/app/src/main/java/com/myisland/dynamic/service/BluetoothEventReceiver.kt))**:
   - Displays connection banner when Bluetooth headphones / ear-buds (Galaxy Buds, Moto Buds, AirPods, Sony) are paired or disconnected.

4. **Multi-Device Presets (Phase 1 Retained)**:
   - Motorola Edge 60 Pro, Samsung Galaxy S20 FE, Samsung Galaxy S23 Plus, Motorola Edge 50 Ultra.

---

## 🌐 GitHub Repository Status

- Repository: **[https://github.com/dvlshlkhln/MyIsland](https://github.com/dvlshlkhln/MyIsland)**
- All Phase 1 and Phase 2 implementations are pushed to `main`.
