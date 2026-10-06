# GameBoost - Android Gaming Performance & 120Hz Optimizer

**GameBoost** is an open-source Android gaming utility designed to optimize supported Android devices for competitive mobile gaming—specifically tuned for:
* **PUBG Mobile (Global / VN / TW)** (`com.tencent.ig`, `com.vng.pubgmobile`, `com.rechild.tencent.ig`)
* **BGMI (Battlegrounds Mobile India)** (`com.pubg.imobile`)
* **PUBG Mobile KR (Korean / Japanese PUBG)** (`com.pubg.krmobile`)

---

## 🛡️ Core Philosophy: Legitimate & Verifiable Optimization

Unlike deceptive "RAM boosters" or prohibited game hacks, **GameBoost operates strictly within legitimate Android system boundaries**:
* **No Root Exploits**: Elevated operations are authorized transparently through **Shizuku**.
* **No APK / OBB Modification**: Game binaries and file systems are never altered.
* **No Memory Injection / Cheats**: Anti-cheat systems are never tampered with or bypassed.
* **No Network Manipulation**: Game network packets and traffic are untouched.
* **No Game Client Modification**: Assets, configs, and memory space remain intact.
* **No Fake FPS Claims**: Transparently distinguishes between **Display Refresh Rate (Hz)**, **Game Render Rate (FPS)**, and **Android Performance States**.
* **100% Reversible**: Automatically captures baseline system and display configurations before applying any changes, allowing full restoration.

---

## 🚀 Key Features

### 1. Shizuku-Powered System Control
* Detects whether Shizuku is installed, running, and authorized.
* Requests user permission once and checks the actual Shizuku service and binder authorization state dynamically across app launches.
* Does not assume specific dialog button text (such as "Allow All the Time")—the live Shizuku binder state is always the single source of truth.
* Gracefully detects permission revocation or stopped services without crashing.
* Protected by a strict regex security allowlist (`CommandAllowlist`) preventing arbitrary command execution or shell injection.

### 2. Verified Display Optimization
* Queries hardware display capabilities and supported display modes via `DisplayManager`.
* **OEM-Aware**: Acknowledges that settings such as `peak_refresh_rate`, `min_refresh_rate`, and `user_refresh_rate` do not work identically across all Android OEMs (e.g., Xiaomi MIUI/HyperOS, Samsung One UI, Realme UI, OnePlus OxygenOS, etc.).
* Applies the safest supported mechanism across relevant system namespaces (`system`, `global`, `secure`).
* **Hardware Verification**: Queries the active display rate after execution rather than assuming success from command exit codes alone.
* If a device or firmware refuses the requested refresh rate, GameBoost honestly reports `Not Supported` or `Device/system refused` with clear diagnostic reasons.

### 3. Performance Profiles
* **Balanced**: Requests the highest appropriate refresh rate while keeping standard system animations and normal battery behavior.
* **Performance**: Targets 120Hz display (if supported), applies reduced UI animation latency (0.5x), and checks for the Android 12+ Game Mode framework.
* **Extreme (Safe)**: Targets maximum supported refresh rate, zero animation latency (0.0x), and safe gaming-focused configurations using allowlisted commands.

### 4. Android Game Mode Detection
* Checks whether the device supports the official Android Game Mode framework (`cmd game mode performance <package>`).
* Checks Android version (API 31+) and command availability prior to execution.
* If unsupported by the device, firmware, or game package, GameBoost reports `Game Mode API: Not supported` without aborting the optimization session.

### 5. Reversible Session Backup & Auto-Restore
* Stores baseline values prior to modifying display or animation settings.
* Provides **[ RESTORE DEFAULT ]** to restore original settings.
* If restoration is only partially successful due to system restrictions, displays exact per-item diagnostics.

### 6. Live Hardware Telemetry
* Efficient polling (2.5-second interval) for battery percentage, battery temperature (°C), RAM usage, and active refresh rate.
* Truthful FPS representation: displays `FPS: Not available` when rendering rates cannot be legitimately measured.

---

## 🔍 Understanding 120Hz vs 120 FPS

* **120Hz (Display Refresh Rate)**: The physical display panel refreshes up to 120 times per second.
* **120 FPS (Game Frame Rate)**: The game graphics engine renders 120 individual frames per second.
* **Important Distinction**: A 120Hz display configuration does **not** guarantee that PUBG or BGMI will render at 120 FPS. In-game rendering rates depend on the game's internal graphics settings, GPU performance, and thermal management. GameBoost configures supported system display rates and performance states, but never modifies game binaries or injects into game processes.

---

## 📱 Technology Stack

* **Language**: Kotlin 2.1+ (Java 17 target)
* **UI**: Jetpack Compose with Material 3 Dark Gaming Cyber Theme
* **Architecture**: Clean Architecture (MVVM, Repositories, Use Cases)
* **Asynchrony**: Kotlin Coroutines & `StateFlow`
* **Persistence**: Jetpack DataStore Preferences
* **Privilege Layer**: Official Shizuku API (`dev.rikka.shizuku:api:13.1.5` & `provider:13.1.5`)
* **Navigation**: Jetpack Navigation 3 (`androidx.navigation3`)

---

## 🛠️ Build and Setup Instructions

### Prerequisites
* Android Studio (Ladybug or newer) or Command Line Tools
* Android SDK Platform 35 / 36 (Build-Tools 35.0.0+)
* Java Development Kit (JDK 17)

### 1. Build the APK
Open a terminal in the project root:
```bash
# Windows
.\gradlew.bat assembleDebug

# Linux / macOS
./gradlew assembleDebug
```
The compiled debug APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

### 2. Configure Shizuku on Your Device
1. Install **Shizuku** from Google Play or GitHub ([RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku)).
2. Start the Shizuku service on your device:
   * **Android 11+**: Open Shizuku -> Start via **Wireless Debugging** (pair in Developer Options).
   * **Android 8.0 - 10**: Connect phone to PC with USB debugging enabled and run:
     ```bash
     adb shell sh /sdcard/Android/data/moe.shizuku.privileged.api/start.sh
     ```
3. Verify Shizuku shows "Shizuku is running".

### 3. Setup GameBoost
1. Install and launch **GameBoost**.
2. Tap **[ GET STARTED ]** to scan hardware and display capabilities.
3. Tap **[ SET UP SHIZUKU ]** -> **[ GRANT PERMISSION ]** and authorize GameBoost in the prompt.
4. GameBoost checks the live authorization state via Shizuku APIs. Once granted, future launches recognize the permission automatically.

### 4. Optimize PUBG / BGMI
1. From the Dashboard, select your installed game profile (PUBG Mobile Global, VN, TW, BGMI, or PUBG KR).
2. Choose your target Refresh Rate (e.g., **120Hz** or **Highest Available**). Only options physically supported by your panel are shown.
3. Select your desired profile (**Balanced**, **Performance**, or **Extreme**).
4. Tap **[ APPLY NOW ]**.
5. GameBoost applies the settings, verifies the resulting display rate, and reports the verified outcome.
6. When done, tap **[ RESTORE DEFAULT ]** to restore baseline settings.

---

## 🧪 Testing & Verification

Unit test suites cover:
* **Shizuku Lifecycle**: Detection of not installed, not running, permission required, authorized, and revocation states.
* **Security Allowlist**: Strict regex validation of display, animation, and game mode commands; rejection of arbitrary commands and shell injection attempts (`;`, `&&`, `|`, backticks).
* **Display Capability Detection**: 60Hz, 90Hz, 120Hz, and 144Hz panel detection, available target filtering, and unsupported mode rejection.
* **Optimization Session & Rollback**: Session state machine, baseline capture, verified application, limitation handling, and rollback diagnostics.
* **Game Registry**: Variant resolution across all 5 regional PUBG and BGMI packages.

To run the unit tests:
```bash
.\gradlew.bat test
```
All 31 unit tests pass in the local build environment.

---

## 📄 License & Third-Party Attribution

Distributed under the Apache License 2.0. See [`LICENSE`](file:///d:/projects/app/LICENSE) for details.

* GameBoost is an independent open-source utility and is **not** affiliated with, endorsed by, or sponsored by Tencent, KRAFTON, Level Infinite, or VNG Corporation.
* Shizuku is developed and copyrighted by Rikka and contributors under the Apache 2.0 License.
#   g a m e - b o o s t  
 