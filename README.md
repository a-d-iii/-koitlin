# Random QR Android App

This repository contains a minimal Kotlin Android application using Jetpack Compose. It has four root pages (Alpha, Beta, Gamma, Delta) accessible via a bottom navigation bar.

## Building the app

1. Install the Android SDK and ensure `ANDROID_HOME` is set.
2. If the Gradle wrapper JAR is missing, generate it with a local Gradle installation:

```bash
gradle wrapper
```

   Then run the wrapper to build the debug APK. It will download the Gradle distribution automatically:

```bash
./gradlew assembleDebug
```

The resulting APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

## Sharing via QR Code

 
A small Python script is provided to display a QR code that lets you install the APK wirelessly.
By default it hosts the APK on a local HTTP server. If your phone is not on the same network,
pass `--upload` to have the script upload the APK to a temporary hosting service and generate
a QR code for that public link.
 

Install the Python dependency:

```bash
pip install qrcode
```

 
Run the script for local network sharing:
 

```bash
python3 serve_apk.py
```

 
If your phone is on a different network, run:

```bash
python3 serve_apk.py --upload
```

The script will print a public URL and QR code that you can scan to download the APK without a USB cable.
 
