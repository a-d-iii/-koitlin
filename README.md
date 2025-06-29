# Basic Android App

This repository contains a minimal Android application written in Kotlin using Jetpack Compose. It displays a simple "Hello Android!" message.

## Building

The Gradle wrapper scripts (`gradlew` and `gradlew.bat`) are included without the
`gradle-wrapper.jar` binary. When you run `./gradlew` for the first time, the
wrapper will download the required jar automatically. Ensure the Android SDK is
available on your system and then execute:

```bash
./gradlew assembleDebug
```

During the build the Inter font is copied from
`vit-student-app/font/Inter-VariableFont_opsz,wght.ttf` into
`app/src/main/res/font`. The font file itself is not tracked in git to keep the
repository lightweight.

The project now uses the Gradle plugin DSL and a modern plugin management
configuration. This removes deprecation warnings that appeared with newer
versions of Gradle.

You can also open the project directory directly in Android Studio and run the
app on an emulator from there.

## Permissions

The app requires network access to fetch data. The `INTERNET` permission is
declared in `app/src/main/AndroidManifest.xml`. If you encounter crashes related
to network requests, verify that this permission is included when merging
manifests in your build process.
