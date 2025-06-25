# Random QR Android App

This repository contains a minimal Kotlin Android application using Jetpack Compose. It has four root pages (Alpha, Beta, Gamma, Delta) accessible via a bottom navigation bar.

## Building the app

1. Install the Android SDK and ensure `ANDROID_HOME` is set.
2. Run the Gradle wrapper to build the debug APK. The wrapper will download its
   required JAR on first use if it is not present:

```bash
./gradlew assembleDebug
```

The resulting APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

## Sharing via QR Code

A small Python script is provided to host the APK on a local HTTP server and display a QR code. Ensure your mobile device is on the same network as this machine.

Install the Python dependency:

```bash
pip install qrcode
```

Run the script:

```bash
python3 serve_apk.py
```

Scan the displayed QR code with your mobile device to download the APK directly without a USB cable.
