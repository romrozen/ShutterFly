## ShutterFly2

Small Compose-first Android app demonstrating a canvas where users long-press thumbnails in a bottom carousel and drag them onto a square canvas. Images can be moved and pinch-zoomed on the canvas.

### Tech stack
- Kotlin + Jetpack Compose (Material 3)
- MVVM (Clean-ish): `CanvasViewModel` exposes `StateFlow<CanvasUiState>` and consumes `CanvasUiEvent`
- Hilt for DI (`ShutterFlyApp`, `CanvasModule`)
- Unit tests (JUnit) for `CanvasViewModel`

### Structure (key files)
- `app/src/main/java/com/roman/shutter/feature/canvas/`
  - `ui/` — UI and state models
    - `CanvasScreen.kt` (screen wiring and bounds)
    - `CanvasStage.kt` (placed images + gestures)
    - `CanvasCarousel.kt` (thumbnails + long-press drag)
    - `CanvasOverlay.kt` (drag preview overlay)
    - `CanvasWalkthrough.kt` (one-time hint overlay)
    - `CanvasModels.kt` (UiState/UiEvent and models)
  - `data/` — data and DI
    - `CanvasRepository.kt` + `DefaultCanvasRepository`
    - `di/CanvasModule.kt`

### Build & run
1. Open in Android Studio (Ladybug+ recommended) and sync.
2. Run on a device (minSdk 24, targetSdk 36).
3. Or CLI:
   - Windows: `gradlew.bat assembleDebug`
   - Unix: `./gradlew assembleDebug`

### Previews
- Previews avoid Hilt by using a stateless variant.
  - `CanvasScreenStateless(uiState = sampleCanvasUiState())`

### Interactions
- Long-press a thumbnail to start a drag; drop on the canvas to place.
- Pinch/zoom and drag placed images within the canvas (center is clamped to bounds).
- One-time hint overlay explains the long-press gesture (dismissable).

### Haptics
- Strong haptic feedback on long-press using Compose haptics + `Vibrator` fallback.
- Requires a physical device with system haptics enabled.

### Tests
- `app/src/test/java/.../CanvasViewModelTest.kt`
  - Covers initial state, bounds set, drag+drop, transforms/clamping, z-index bump, walkthrough dismissal.

### Notes
- DI: `CanvasViewModel` requests `CanvasRepository`; bound via `CanvasModule`.
- Accessibility: key surfaces include `semantics { contentDescription = ... }` for testing.


