# AFT Calculator App

## Project Overview
Army Fitness Test (AFT) calculator for Android and iOS. Android built with Kotlin and Jetpack Compose, iOS built with SwiftUI. Shared scoring logic via Kotlin Multiplatform.

## AFT Events (Official as of June 2025)
1. **3-Rep Max Deadlift** - 140-340 lbs range
2. **Hand-Release Push-Up** - 10-60+ reps
3. **Sprint-Drag-Carry** - 1:33-3:00+ time
4. **Plank** - 1:30-4:00+ time
5. **2-Mile Run** - 13:30-21:00+ time

## Scoring Rules
- 0-100 points per event
- **Combat MOSs**: Minimum 60 per event, 350 total required (sex-neutral for all, age-normed)
- **Combat-enabling MOSs**: Minimum 60 per event, 300 total required (sex and age-normed)
- Males always use Male scoring tables regardless of MOS
- Females use Male tables for Combat MOS, Female tables for Combat-enabling MOS
- Age brackets: 17-21, 22-26, 27-31, 32-36, 37-41, 42-46, 47-51, 52-56, 57-61, 62+
- Pro-rated minimums when events are exempt (e.g., 4 events = 280 min for Combat)

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM with ViewModels
- **Build**: Gradle with Kotlin DSL
- **Minimum SDK**: API 26 (Android 8.0)
- **Target SDK**: API 34 (Android 14)
- **PDF Generation**: PDFBox-Android 2.0.27.0

## Project Structure
```
aft-app/
├── shared/                           # Kotlin Multiplatform shared code
│   └── src/
│       ├── commonMain/kotlin/com/aftcalculator/
│       │   ├── AftCalculator.kt      # Main calculator logic
│       │   ├── models/
│       │   │   ├── AftEvent.kt
│       │   │   ├── AftScore.kt
│       │   │   ├── Soldier.kt
│       │   │   ├── Gender.kt
│       │   │   ├── MosCategory.kt
│       │   │   └── AgeBracket.kt
│       │   └── scoring/
│       │       ├── OfficialScoreTables.kt   # Table selection logic
│       │       ├── DeadliftTables.kt
│       │       ├── PushUpTables.kt
│       │       ├── TimedEventTables.kt      # SDC, Plank
│       │       └── TwoMileRunTables.kt
│       ├── commonTest/               # Shared unit tests
│       ├── androidMain/
│       └── iosMain/
├── androidApp/
│   └── src/main/
│       ├── kotlin/com/aftcalculator/android/
│       │   ├── MainActivity.kt
│       │   ├── ui/
│       │   │   ├── screens/
│       │   │   │   ├── HomeScreen.kt        # Gender, age, MOS selection
│       │   │   │   ├── CalculatorScreen.kt  # Event input with live scoring
│       │   │   │   └── ResultsScreen.kt     # Results + Form 705 generation
│       │   │   ├── components/
│       │   │   │   ├── EventInputCard.kt    # Input card with exempt toggle
│       │   │   │   └── ScoreCard.kt         # Score display with colors
│       │   │   └── theme/
│       │   │       └── Theme.kt             # GoArmy-inspired dark theme
│       │   ├── viewmodels/
│       │   │   └── CalculatorViewModel.kt   # State management
│       │   └── pdf/
│       │       └── Form705Generator.kt      # DA Form 705 PDF filling
│       ├── res/
│       │   ├── xml/file_paths.xml           # FileProvider config
│       │   └── values/
│       └── assets/
│           └── da_form_705.pdf              # Blank DA Form 705 (XFA-stripped)
├── iosApp/                          # iOS SwiftUI app
│   ├── iosApp.xcodeproj/
│   ├── iosApp/                      # SwiftUI source files
│   └── fastlane/                    # Fastlane config
├── docs/
│   ├── ARN43863-DA_FORM_705-TEST-107-WEB-8.pdf  # Original form
│   └── HQDA_EXORD_218-25__CC__Annex_B_-_Scoring_Tables__Final_.pdf
└── .github/workflows/
    ├── android.yml                  # Android CI/CD
    └── ios.yml                      # iOS CI/CD
```

## Key Features Implemented
- **Live scoring**: Scores update as user types
- **Color indicators**: Red (<60 fail), Yellow (60-65 warning), Green (66+ pass)
- **Profile exemptions**: Toggle per event for medical profiles
- **Running total**: Shows current total, turns red if any event fails or below minimum
- **Placeholder text**: Shows minimum passing raw score based on age/gender/MOS
- **DA Form 705 generation**: Fills official Army fitness test form (IN PROGRESS)

## DA Form 705 PDF Generation
- Uses PDFBox-Android to fill the official DA Form 705
- Original PDF has XFA forms - stripped using `pdftk drop_xfa`
- PDF decrypted using `qpdf --decrypt`
- Fields filled: Name, Unit/Location, MOS, Pay Grade, Age, Date, event scores, totals, grader info

### PDF Field Names (hierarchical XFA-style)
```
form1[0].Page1[0].Name[0]
form1[0].Page1[0].Unit_Location[0]
form1[0].Page1[0].Test_One_Date[0]
form1[0].Page1[0].Test_One_MOS[0]
form1[0].Page1[0].Test_One_Rank_Grade[0]
form1[0].Page1[0].Test_One_Age[0]
form1[0].Page1[0].Test_One_First_Attempt[0]     # Deadlift weight
form1[0].Page1[0].Test_One_Points1[0]           # Deadlift points
form1[0].Page1[0].Test_One_Repetitions[0]       # Push-ups
form1[0].Page1[0].Test_One_Points3[0]           # Push-up points
form1[0].Page1[0].Test_One_Time1[0]             # SDC time
form1[0].Page1[0].Test_One_Points4[0]           # SDC points
form1[0].Page1[0].Test_One_Time2[0]             # Plank time
form1[0].Page1[0].Test_One_Points5[0]           # Plank points
form1[0].Page1[0].Test_One_Time3[0]             # 2MR time
form1[0].Page1[0].Test_One_Points6[0]           # 2MR points
form1[0].Page1[0].Test_One_Total_Points[0]
form1[0].Page1[0].OIC_NCOIC_Name_Test_One[0]
```

### PDF Freeze Fix (Resolved)
The freeze was caused by PDFBox trying to regenerate appearances for all fields in the complex XFA-derived form. Fixed by:
1. Running generation on `Dispatchers.IO` background thread
2. Only updating appearances for filled fields (not calling `acroForm.refreshAppearances()`)
3. Adding cycle detection and depth limiting when collecting form fields
4. Using file-based PDF loading instead of stream

## Commands
```bash
# Build debug APK
./gradlew :androidApp:assembleDebug

# Clean build
./gradlew :androidApp:clean :androidApp:assembleDebug

# Run shared module tests
./gradlew :shared:test

# Build release APK
./gradlew :androidApp:assembleRelease

# Install on connected device
./gradlew :androidApp:installDebug

# Strip XFA from PDF (requires pdftk)
pdftk input.pdf output output.pdf drop_xfa

# Decrypt PDF (requires qpdf)
qpdf --decrypt input.pdf output.pdf

# List PDF form fields
qpdf --json input.pdf | python3 -c "import json,sys; [print(f['fullname']) for f in json.load(sys.stdin)['acroform']['fields']]"
```

## Development Status
- [x] Environment setup
- [x] KMP project structure
- [x] Scoring logic with official tables
- [x] Unit tests for scoring
- [x] UI screens with Jetpack Compose
- [x] GoArmy dark theme
- [x] Live scoring with color indicators
- [x] Profile exemption toggles
- [x] CI/CD with GitHub Actions (Android + iOS)
- [x] Form 705 dialog with soldier/grader info
- [x] Form 705 PDF generation
- [x] iOS App Store deployment (TestFlight CI/CD configured)
- [ ] Local persistence for score history
- [ ] Play Store deployment (waiting on API key)

## iOS App Store Deployment
iOS deployment is fully configured with automated CI/CD.

### iOS Tech Stack
- **UI**: SwiftUI
- **Bundle ID**: `com.aftcalculator.AFTCalc`
- **Team ID**: `RBYZJ6KX4D`
- **Deployment**: Fastlane + GitHub Actions

### iOS Project Structure
```
iosApp/
├── iosApp.xcodeproj/          # Xcode project
├── iosApp/                    # SwiftUI source
│   ├── iosAppApp.swift        # App entry point
│   ├── HomeView.swift         # Gender/age/MOS selection
│   ├── CalculatorView.swift   # Event scoring calculator
│   ├── ResultsView.swift      # Results display
│   ├── Form705Generator.swift # DA Form 705 PDF generation
│   ├── AftTypes.swift         # Domain models
│   ├── Theme.swift            # GoArmy dark theme
│   └── Assets.xcassets/       # App icons
├── fastlane/
│   ├── Appfile                # App identifier config
│   └── Fastfile               # Build/deploy lanes
└── Gemfile                    # Fastlane dependencies
```

### GitHub Actions Workflow
- **File**: `.github/workflows/ios.yml`
- **Trigger**: Push to `master` branch
- **Jobs**:
  - `build`: Builds debug app on iOS Simulator
  - `deploy-testflight`: Archives and uploads to TestFlight

### Required GitHub Secrets
```
APP_STORE_CONNECT_API_KEY_ID     # App Store Connect API Key ID
APP_STORE_CONNECT_API_ISSUER_ID  # App Store Connect Issuer ID
APP_STORE_CONNECT_API_KEY        # Base64-encoded .p8 key
IOS_CERTIFICATE_BASE64           # Base64-encoded .p12 distribution cert
IOS_CERTIFICATE_PASSWORD         # Password for .p12
IOS_PROVISIONING_PROFILE_BASE64  # Base64-encoded .mobileprovision
KEYCHAIN_PASSWORD                # Temp keychain password (any string)
```

### Fastlane Lanes
```bash
# Build release IPA
cd iosApp && bundle exec fastlane build

# Deploy to TestFlight
cd iosApp && bundle exec fastlane beta

# Deploy to App Store
cd iosApp && bundle exec fastlane release
```

## Sources
- [Army Fitness Test Official](https://www.army.mil/aft/)
- [HQDA EXORD 218-25 Scoring Tables](docs/)
