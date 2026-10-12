# Smart Macro Studio — GitHub Actions build

Android/Kotlin starter project designed to build from a phone using GitHub Actions. No Android Studio required on your device.

## Build from a phone
1. Download this ZIP and extract it on your phone (or upload the ZIP to GitHub and use the repository's web upload workflow if available).
2. Create a GitHub repository.
3. Upload the *contents* of this folder to the repository, keeping `.github/workflows/android.yml` in place.
4. Open the repository's **Actions** tab and enable Actions if GitHub asks.
5. Run **Build Android APK** with **Run workflow**.
6. When the run succeeds, open its summary and download the `SmartMacroStudio-debug-apk` artifact. Extract the artifact ZIP to get the APK.

## What is included
- Kotlin + Jetpack Compose dashboard
- Floating edge handle and a floating macro panel
- Add/edit circular touch-target appearance (name, size, opacity) in the app UI
- Step editor models for click, long press, slide, delay and multi-press
- Accessibility service gesture dispatcher scaffold
- App-profile list UI
- GitHub Actions workflow to compile and upload the debug APK artifact

## Important limitations
This is an MVP starter, not a full Panda Touch Pro replacement:
- Macro steps and app profiles are currently prototype UI/state, not fully persisted or connected end-to-end.
- The floating panel is functional UI, but the circular touch-target editor is not yet a full draggable per-target overlay.
- Gesture dispatch methods exist in the accessibility service, but a complete macro runner, reliable foreground-app profile matching, and touch-target-to-runner integration still need implementation and device testing.
- Android requires users to explicitly grant overlay permission and enable the Accessibility service in Settings.
- Some games may block overlays/gestures or disallow automation. Only use automation where permitted.
- App/game support differs across Android versions and manufacturers.

## Package size
The APK size is a build output, not a source ZIP setting. The workflow produces a debug APK; its size can vary by Android Gradle Plugin, dependencies and build configuration. No minimum size is artificially enforced.

## Security
No signing key is included. The debug APK is not a Play Store release. Do not upload passwords, keystores, or tokens into the repository.
