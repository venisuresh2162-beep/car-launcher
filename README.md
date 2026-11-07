# Car Launcher (Android 10 head unit, 1024x600)

A custom home screen for an Allwinner K2501 head unit. Big tiles for Android Auto and CarPlay,
plus Maps, Music, Radio and Camera. It does not replace the phone projection: it opens whichever
app on your unit already handles Android Auto / CarPlay (often Zlink or a vendor app).

## Build
1. Install Android Studio on your PC and open this folder (`car-launcher`).
2. Let it sync Gradle (first sync downloads dependencies; needs internet).
3. Build > Build APK(s). The APK is in `app/build/outputs/apk/debug/app-debug.apk`.

## Install on the unit
- Copy the APK to a USB drive, plug it into the unit, open it from the unit's file manager
  and allow "install unknown apps" if asked; or
- With USB debugging on: `adb connect <unit-ip>:5555` then `adb install -r app-debug.apk`.

## First run
1. Press Home. Android asks which launcher to use: pick **Car Launcher** (choose "Just once" the
   first time, so you can easily go back to the stock one if needed).
2. Tap the **Android Auto** tile. It has no app assigned yet, so a picker opens: choose the app
   on your unit that connects to Android Auto / CarPlay.
3. Same for the other tiles. **Long-press** any tile later to change its app.

## If something goes wrong
- Keep the stock launcher installed. Home > pick the stock one to switch back.
- If the unit forgets the default launcher after ignition off, tell me and we will add a workaround.
- Reverse camera uses the unit's own app and should still pop up as before. Test it right away.

## Settings
`HIDE_BARS` in `MainActivity.kt` hides the system bars (off by default, because some units
need them for Home/Back).
