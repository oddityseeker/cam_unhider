# Cam Unlocker

LSPosed module that exposes **all cameras present on the device** — including physical/logical sub-cameras that Android deliberately hides from `CameraManager.getCameraIdList()` — to any app that asks for the camera ID list.

## The problem

On many devices (especially multi-camera phones running custom ROMs), `getCameraIdList()` only returns the "public" logical camera IDs — usually just `0` (rear) and `1` (front). Any additional physical sensors (ultra-wide, telephoto, macro, mono, IR, etc.) that are grouped under a logical camera are hidden from this list by design in the Android framework, even though the camera itself works perfectly fine and can be opened directly if you already know its ID.

This means stock camera apps, GCam ports, Open Camera, and most third-party camera apps never even see these sensors exist — not because the ROM is broken, but because of how Android's multi-camera API filtering works.

## How it works

Cam Unlocker hooks `CameraManager.getCameraIdList()`. On every call, it:

1. Takes the original (filtered) list returned by the system.
2. Probes camera IDs `0` through `32` by calling `getCameraCharacteristics(id)` for each one not already in the list.
3. If a given ID resolves without throwing an exception, the camera physically exists — its ID is added to the result.
4. Returns the extended list to the calling app.

No HAL patching, no vendor partition edits, no rebuilding the ROM — this works entirely at the framework API level via Xposed/LSPosed.

## Requirements

- Root + LSPosed (or EdXposed) installed
- A camera app that simply enumerates `getCameraIdList()` and lets you pick a camera to open (e.g. Open Camera)

## Installation

1. Download the APK from Releases or build it yourself (see below).
2. Install it like a normal app.
3. Enable the module in **LSPosed Manager**.
4. Set the **Scope** to the camera app(s) you want to see the extra IDs (e.g. Open Camera, stock Camera).
5. Force-stop or restart the target app(s).

## Building from source

`git clone [<repo-url>](https://github.com/oddityseeker/cam_unhider)`
`cd cam-unlocker`
Open in Android Studio and build normally (`Build → Build APK(s)`), or `./gradlew assembleDebug` in terminal.

## Configuration

`MAX_PROBE_ID` in `HookMain.java` controls the probing range (default `32`, covers all known devices). `TARGET_PACKAGES` can be used to restrict the hook to specific packages directly in code, in addition to the LSPosed Scope setting.

## Limitations & disclaimer

- Made with AI
- Tested on **OnePlus 8 Pro (MatrixX ROM)**. Behavior on other devices/ROMs is untested and may vary.
- An unlocked ID appearing in the list does **not** guarantee it can be opened as an independent stream. Some IDs are *physical* cameras belonging to a logical multi-camera and may require being opened via `OutputConfiguration.setPhysicalCameraId()` within a logical camera session rather than as a standalone device. If an app crashes on open after unlocking an ID, this is the likely reason.
- Probing 32 IDs runs on every `getCameraIdList()` call; the overhead is negligible in practice but you can lower `MAX_PROBE_ID` if needed.
- Use at your own risk. This project is provided as-is, with no warranty and under MIT license.
