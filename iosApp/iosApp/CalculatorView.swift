import SwiftUI

// Swift-native types
struct SwiftEventScore {
    let eventName: String
    let rawValue: Double
    let points: Int32
    let passed: Bool
    let isTimeBased: Bool
}

struct SwiftAftScore: Identifiable {
    let id = UUID()
    let mosCategoryName: String
    let mosCategoryMinimum: Int32
    let isCombatMos: Bool
    let soldierAge: Int32
    let soldierGender: String
    let soldierAgeBracketName: String
    let eventScores: [SwiftEventScore]
    let totalPoints: Int32
    let passed: Bool
    let failureReasons: [String]
}

struct CalculatorView: View {
    let gender: Gender
    let age: Int32
    let mosCategory: MosCategory
    let onDismiss: () -> Void

    @State private var deadliftLbs: String = ""
    @State private var pushUpReps: String = ""
    @State private var sdcTime: String = ""
    @State private var plankTime: String = ""
    @State private var runTime: String = ""
    @State private var calculatedScore: SwiftAftScore?

    // Exempt toggles
    @State private var deadliftExempt: Bool = false
    @State private var pushUpExempt: Bool = false
    @State private var sdcExempt: Bool = false
    @State private var plankExempt: Bool = false
    @State private var runExempt: Bool = false

    // Alternate aerobic event state
    @State private var useAlternateAerobic: Bool = false
    @State private var selectedAlternateEvent: AftEvent = .walk25Mile
    @State private var alternateAerobicTime: String = ""

    private var soldier: Soldier {
        Soldier(age: age, gender: gender, mosCategory: mosCategory)
    }

    private let calculator = AftCalculator()

    // Live score calculations (return nil if exempt)
    private var deadliftPoints: Int32? {
        guard !deadliftExempt else { return nil }
        guard let value = Double(deadliftLbs), value > 0 else { return nil }
        return calculator.calculateSingleEvent(event: .deadlift, rawValue: value, soldier: soldier).points
    }

    private var pushUpPoints: Int32? {
        guard !pushUpExempt else { return nil }
        guard let value = Double(pushUpReps), value > 0 else { return nil }
        return calculator.calculateSingleEvent(event: .pushUp, rawValue: value, soldier: soldier).points
    }

    private var sdcPoints: Int32? {
        guard !sdcExempt else { return nil }
        guard !sdcTime.isEmpty else { return nil }
        let parts = sdcTime.split(separator: ":")
        guard parts.count == 2, let mins = Int(parts[0]), let secs = Int(parts[1]) else { return nil }
        let seconds = Double(mins * 60 + secs)
        return calculator.calculateSingleEvent(event: .sprintDragCarry, rawValue: seconds, soldier: soldier).points
    }

    private var plankPoints: Int32? {
        guard !plankExempt else { return nil }
        guard !plankTime.isEmpty else { return nil }
        let parts = plankTime.split(separator: ":")
        guard parts.count == 2, let mins = Int(parts[0]), let secs = Int(parts[1]) else { return nil }
        let seconds = Double(mins * 60 + secs)
        return calculator.calculateSingleEvent(event: .plank, rawValue: seconds, soldier: soldier).points
    }

    private var runPoints: Int32? {
        guard !runExempt else { return nil }
        guard !useAlternateAerobic else { return nil }
        guard !runTime.isEmpty else { return nil }
        let parts = runTime.split(separator: ":")
        guard parts.count == 2, let mins = Int(parts[0]), let secs = Int(parts[1]) else { return nil }
        let seconds = Double(mins * 60 + secs)
        return calculator.calculateSingleEvent(event: .twoMileRun, rawValue: seconds, soldier: soldier).points
    }

    private var alternateAerobicPoints: Int32? {
        guard !runExempt else { return nil }
        guard useAlternateAerobic else { return nil }
        guard !alternateAerobicTime.isEmpty else { return nil }
        let parts = alternateAerobicTime.split(separator: ":")
        guard parts.count == 2, let mins = Int(parts[0]), let secs = Int(parts[1]) else { return nil }
        let seconds = Double(mins * 60 + secs)
        return calculator.calculateSingleEvent(event: selectedAlternateEvent, rawValue: seconds, soldier: soldier).points
    }

    // Use either run or alternate aerobic score
    private var aerobicPoints: Int32? {
        guard !runExempt else { return nil }
        return useAlternateAerobic ? alternateAerobicPoints : runPoints
    }

    private var exemptCount: Int {
        [deadliftExempt, pushUpExempt, sdcExempt, plankExempt, runExempt].filter { $0 }.count
    }

    private var exemptPoints: Int32 {
        Int32(exemptCount) * 60
    }

    private var runningTotal: Int32 {
        let scored = [deadliftPoints, pushUpPoints, sdcPoints, plankPoints, aerobicPoints]
            .compactMap { $0 }
            .reduce(0, +)
        return scored + exemptPoints
    }

    private var eventCount: Int {
        [deadliftPoints, pushUpPoints, sdcPoints, plankPoints, aerobicPoints]
            .compactMap { $0 }
            .count
    }

    private var isOnTrack: Bool {
        let scores = [deadliftPoints, pushUpPoints, sdcPoints, plankPoints, aerobicPoints].compactMap { $0 }
        guard !scores.isEmpty || exemptCount > 0 else { return true }
        let anyFailing = scores.contains { $0 < 60 }
        // Minimum stays at full value; exempt events contribute 60 pts each
        return !anyFailing && runningTotal >= mosCategory.minimumTotal
    }

    // Minimum passing values (60 points)
    private var minDeadlift: String {
        // Find minimum weight for 60 points
        for weight in stride(from: 140, through: 340, by: 10) {
            let pts = calculator.calculateSingleEvent(event: .deadlift, rawValue: Double(weight), soldier: soldier).points
            if pts >= 60 { return "\(weight)" }
        }
        return "140"
    }

    private var minPushUps: String {
        for reps in 10...60 {
            let pts = calculator.calculateSingleEvent(event: .pushUp, rawValue: Double(reps), soldier: soldier).points
            if pts >= 60 { return "\(reps)" }
        }
        return "10"
    }

    private var maxSDCTime: String {
        // SDC: lower time is better, find max time for 60 points
        for secs in stride(from: 180, through: 93, by: -1) {
            let pts = calculator.calculateSingleEvent(event: .sprintDragCarry, rawValue: Double(secs), soldier: soldier).points
            if pts >= 60 {
                let mins = secs / 60
                let sec = secs % 60
                return "\(mins):\(String(format: "%02d", sec))"
            }
        }
        return "3:00"
    }

    private var minPlankTime: String {
        for secs in 90...240 {
            let pts = calculator.calculateSingleEvent(event: .plank, rawValue: Double(secs), soldier: soldier).points
            if pts >= 60 {
                let mins = secs / 60
                let sec = secs % 60
                return "\(mins):\(String(format: "%02d", sec))"
            }
        }
        return "1:30"
    }

    private var maxRunTime: String {
        // Run: lower time is better, find max time for 60 points
        for secs in stride(from: 1260, through: 810, by: -1) {
            let pts = calculator.calculateSingleEvent(event: .twoMileRun, rawValue: Double(secs), soldier: soldier).points
            if pts >= 60 {
                let mins = secs / 60
                let sec = secs % 60
                return "\(mins):\(String(format: "%02d", sec))"
            }
        }
        return "21:00"
    }

    private var maxAlternateAerobicTime: String {
        // Alternate aerobic: pass/fail at 60 points, find max passing time
        // Search from high to low time to find the max passing time
        for secs in stride(from: 3600, through: 600, by: -1) {
            let pts = calculator.calculateSingleEvent(event: selectedAlternateEvent, rawValue: Double(secs), soldier: soldier).points
            if pts >= 60 {
                let mins = secs / 60
                let sec = secs % 60
                return "\(mins):\(String(format: "%02d", sec))"
            }
        }
        return "60:00"
    }

    var body: some View {
        ZStack {
            LinearGradient(
                colors: [Color.armyBlack, Color.armyDarkGray],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 0) {
                // Header
                HStack {
                    Button(action: onDismiss) {
                        Image(systemName: "chevron.left")
                            .foregroundColor(.armyGold)
                            .font(.system(size: 20, weight: .semibold))
                    }

                    Spacer()

                    VStack(alignment: .trailing, spacing: 2) {
                        Text("ENTER EVENT SCORES")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.white)
                        Text("\(mosCategory.displayName) • \(gender == .male ? "Male" : "Female") • Age \(age)")
                            .font(.system(size: 12))
                            .foregroundColor(.white.opacity(0.6))
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(Color.armyBlack)

                ScrollView {
                    VStack(spacing: 16) {
                        // Running Total Card
                        if eventCount > 0 || exemptCount > 0 {
                            HStack {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("RUNNING TOTAL")
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundColor(.armyGold)
                                    Text("\(eventCount + exemptCount) of 5 events (\(exemptCount) exempt)")
                                        .font(.system(size: 11))
                                        .foregroundColor(.white.opacity(0.5))
                                }
                                Spacer()
                                Text("\(runningTotal)")
                                    .font(.system(size: 32, weight: .bold))
                                    .foregroundColor(isOnTrack ? .passGreen : .failRed)
                            }
                            .padding()
                            .background(Color.white.opacity(0.05))
                            .cornerRadius(12)
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(isOnTrack ? Color.passGreen.opacity(0.5) : Color.failRed.opacity(0.5), lineWidth: 1)
                            )
                        }

                        // Event Input Cards with live scoring
                        LiveEventInputCard(
                            title: "3-REP MAX DEADLIFT",
                            value: $deadliftLbs,
                            hint: "Min \(minDeadlift) lbs to pass",
                            points: deadliftPoints,
                            keyboardType: .numberPad,
                            isExempt: $deadliftExempt
                        )

                        LiveEventInputCard(
                            title: "HAND-RELEASE PUSH-UP",
                            value: $pushUpReps,
                            hint: "Min \(minPushUps) reps to pass",
                            points: pushUpPoints,
                            keyboardType: .numberPad,
                            isExempt: $pushUpExempt
                        )

                        LiveEventInputCard(
                            title: "SPRINT-DRAG-CARRY",
                            value: $sdcTime,
                            hint: "Max \(maxSDCTime) to pass",
                            points: sdcPoints,
                            keyboardType: .numbersAndPunctuation,
                            isTimeInput: true,
                            isExempt: $sdcExempt
                        )

                        LiveEventInputCard(
                            title: "PLANK",
                            value: $plankTime,
                            hint: "Min \(minPlankTime) to pass",
                            points: plankPoints,
                            keyboardType: .numbersAndPunctuation,
                            isTimeInput: true,
                            isExempt: $plankExempt
                        )

                        // Aerobic Event: Either 2-Mile Run or Alternate
                        if runExempt {
                            // Show exempt card for aerobic event
                            LiveEventInputCard(
                                title: "AEROBIC EVENT",
                                value: .constant(""),
                                hint: "",
                                points: nil,
                                isExempt: $runExempt
                            )
                        } else if useAlternateAerobic {
                            AlternateAerobicCard(
                                selectedEvent: $selectedAlternateEvent,
                                timeValue: $alternateAerobicTime,
                                maxPassingTime: maxAlternateAerobicTime,
                                points: alternateAerobicPoints,
                                onSwitchToStandard: { useAlternateAerobic = false }
                            )
                        } else {
                            LiveEventInputCard(
                                title: "2-MILE RUN",
                                value: $runTime,
                                hint: "Max \(maxRunTime) to pass",
                                points: runPoints,
                                keyboardType: .numbersAndPunctuation,
                                isTimeInput: true,
                                showAlternateOption: true,
                                onSwitchToAlternate: { useAlternateAerobic = true },
                                isExempt: $runExempt
                            )
                        }

                        Button(action: calculateScore) {
                            Text("VIEW RESULTS")
                                .font(.system(size: 16, weight: .bold))
                                .tracking(1)
                                .foregroundColor(.armyBlack)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 16)
                                .background(Color.armyGold)
                                .cornerRadius(8)
                        }
                        .padding(.top, 8)

                        Spacer(minLength: 40)
                    }
                    .padding(16)
                }
            }
        }
        .fullScreenCover(item: $calculatedScore) { score in
            ResultsContent(
                score: score,
                onEditScores: { calculatedScore = nil },
                onStartOver: {
                    calculatedScore = nil
                    onDismiss()
                }
            )
        }
        // Clear input fields when events become exempt
        .onChange(of: deadliftExempt) { _, isExempt in
            if isExempt { deadliftLbs = "" }
        }
        .onChange(of: pushUpExempt) { _, isExempt in
            if isExempt { pushUpReps = "" }
        }
        .onChange(of: sdcExempt) { _, isExempt in
            if isExempt { sdcTime = "" }
        }
        .onChange(of: plankExempt) { _, isExempt in
            if isExempt { plankTime = "" }
        }
        .onChange(of: runExempt) { _, isExempt in
            if isExempt {
                runTime = ""
                alternateAerobicTime = ""
            }
        }
    }

    private func calculateScore() {
        var swiftScores: [SwiftEventScore] = []
        var failureReasons: [String] = []
        var exemptCount = 0

        // Count exempt events
        if deadliftExempt { exemptCount += 1 }
        if pushUpExempt { exemptCount += 1 }
        if sdcExempt { exemptCount += 1 }
        if plankExempt { exemptCount += 1 }
        if runExempt { exemptCount += 1 }

        // Only include non-exempt events
        if !deadliftExempt, let value = Double(deadliftLbs), value > 0 {
            let result = calculator.calculateSingleEvent(event: .deadlift, rawValue: value, soldier: soldier)
            swiftScores.append(SwiftEventScore(eventName: "3 Rep Max Deadlift", rawValue: value, points: result.points, passed: result.passed, isTimeBased: false))
            if result.points < 60 { failureReasons.append("Deadlift: \(result.points) points (min 60)") }
        }

        if !pushUpExempt, let value = Double(pushUpReps), value > 0 {
            let result = calculator.calculateSingleEvent(event: .pushUp, rawValue: value, soldier: soldier)
            swiftScores.append(SwiftEventScore(eventName: "Hand Release Push-Up", rawValue: value, points: result.points, passed: result.passed, isTimeBased: false))
            if result.points < 60 { failureReasons.append("Push-Up: \(result.points) points (min 60)") }
        }

        if !sdcExempt, !sdcTime.isEmpty {
            let parts = sdcTime.split(separator: ":")
            if parts.count == 2, let mins = Int(parts[0]), let secs = Int(parts[1]) {
                let seconds = Double(mins * 60 + secs)
                let result = calculator.calculateSingleEvent(event: .sprintDragCarry, rawValue: seconds, soldier: soldier)
                swiftScores.append(SwiftEventScore(eventName: "Sprint-Drag-Carry", rawValue: seconds, points: result.points, passed: result.passed, isTimeBased: true))
                if result.points < 60 { failureReasons.append("SDC: \(result.points) points (min 60)") }
            }
        }

        if !plankExempt, !plankTime.isEmpty {
            let parts = plankTime.split(separator: ":")
            if parts.count == 2, let mins = Int(parts[0]), let secs = Int(parts[1]) {
                let seconds = Double(mins * 60 + secs)
                let result = calculator.calculateSingleEvent(event: .plank, rawValue: seconds, soldier: soldier)
                swiftScores.append(SwiftEventScore(eventName: "Plank", rawValue: seconds, points: result.points, passed: result.passed, isTimeBased: true))
                if result.points < 60 { failureReasons.append("Plank: \(result.points) points (min 60)") }
            }
        }

        // Aerobic event: either 2-mile run or alternate (only if not exempt)
        if !runExempt {
            if useAlternateAerobic {
                if !alternateAerobicTime.isEmpty {
                    let parts = alternateAerobicTime.split(separator: ":")
                    if parts.count == 2, let mins = Int(parts[0]), let secs = Int(parts[1]) {
                        let seconds = Double(mins * 60 + secs)
                        let result = calculator.calculateSingleEvent(event: selectedAlternateEvent, rawValue: seconds, soldier: soldier)
                        let eventName = selectedAlternateEvent.displayName
                        swiftScores.append(SwiftEventScore(eventName: eventName, rawValue: seconds, points: result.points, passed: result.passed, isTimeBased: true))
                        if result.points < 60 { failureReasons.append("\(eventName): \(result.points) points (min 60)") }
                    }
                }
            } else {
                if !runTime.isEmpty {
                    let parts = runTime.split(separator: ":")
                    if parts.count == 2, let mins = Int(parts[0]), let secs = Int(parts[1]) {
                        let seconds = Double(mins * 60 + secs)
                        let result = calculator.calculateSingleEvent(event: .twoMileRun, rawValue: seconds, soldier: soldier)
                        swiftScores.append(SwiftEventScore(eventName: "2 Mile Run", rawValue: seconds, points: result.points, passed: result.passed, isTimeBased: true))
                        if result.points < 60 { failureReasons.append("2MR: \(result.points) points (min 60)") }
                    }
                }
            }
        }

        guard !swiftScores.isEmpty || exemptCount > 0 else { return }

        // Exempt events receive 60 points each; minimum stays at full value
        let exemptPts = Int32(exemptCount) * 60
        let totalPoints = swiftScores.reduce(0) { $0 + $1.points } + exemptPts
        let allEventsPassed = swiftScores.allSatisfy { $0.points >= 60 }
        let minimumTotal = mosCategory.minimumTotal
        let totalPassed = totalPoints >= minimumTotal

        if !totalPassed {
            failureReasons.append("Total: \(totalPoints) (min \(minimumTotal))")
        }

        calculatedScore = SwiftAftScore(
            mosCategoryName: mosCategory.displayName,
            mosCategoryMinimum: mosCategory.minimumTotal,
            isCombatMos: mosCategory == .combat,
            soldierAge: age,
            soldierGender: gender == .male ? "Male" : "Female",
            soldierAgeBracketName: soldier.ageBracket.displayName,
            eventScores: swiftScores,
            totalPoints: totalPoints,
            passed: allEventsPassed && totalPassed,
            failureReasons: failureReasons
        )
    }
}

// MARK: - Results View

struct ResultsContent: View {
    let score: SwiftAftScore
    let onEditScores: () -> Void
    let onStartOver: () -> Void

    // Form 705 dialog state
    @State private var showForm705Dialog = false
    @State private var soldierName = ""
    @State private var unitLocation = ""
    @State private var mos = ""
    @State private var payGrade = "E-4"
    @State private var graderName = ""
    @State private var isGenerating = false
    @State private var showShareSheet = false
    @State private var generatedPDFURL: URL?
    @State private var pendingShareURL: URL?  // URL waiting to be shared after dialog dismisses
    @State private var errorMessage: String?
    @State private var showError = false

    private let payGrades = [
        "E-1", "E-2", "E-3", "E-4", "E-5", "E-6", "E-7", "E-8", "E-9",
        "W-1", "W-2", "W-3", "W-4", "W-5",
        "O-1", "O-2", "O-3", "O-4", "O-5", "O-6", "O-7", "O-8", "O-9", "O-10"
    ]

    private var passedColor: Color { score.passed ? .passGreen : .failRed }

    var body: some View {
        ZStack {
            LinearGradient(
                colors: [Color.armyBlack, Color.armyDarkGray],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 0) {
                HStack {
                    Button(action: onEditScores) {
                        Image(systemName: "xmark")
                            .foregroundColor(.armyGold)
                            .font(.system(size: 18, weight: .semibold))
                    }

                    Spacer()

                    VStack(alignment: .trailing, spacing: 2) {
                        Text("AFT RESULTS")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.white)
                        Text("\(score.mosCategoryName) • \(score.soldierGender) • Age \(score.soldierAge)")
                            .font(.system(size: 12))
                            .foregroundColor(.white.opacity(0.6))
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(Color.armyBlack)

                ScrollView {
                    VStack(spacing: 24) {
                        // Main Score Card
                        VStack(spacing: 16) {
                            Text(score.passed ? "PASS" : "FAIL")
                                .font(.system(size: 24, weight: .black))
                                .tracking(4)
                                .foregroundColor(passedColor)

                            Text("\(score.totalPoints)")
                                .font(.system(size: 72, weight: .bold))
                                .foregroundColor(.white)

                            Text("TOTAL POINTS")
                                .font(.system(size: 12, weight: .bold))
                                .tracking(2)
                                .foregroundColor(.white.opacity(0.6))

                            let exemptEvents = 5 - Int32(score.eventScores.count)
                            Text(exemptEvents > 0
                                ? "Minimum required: \(score.mosCategoryMinimum) (\(exemptEvents) exempt @ 60 pts)"
                                : "Minimum required: \(score.mosCategoryMinimum)")
                                .font(.system(size: 14))
                                .foregroundColor(.white.opacity(0.5))
                        }
                        .padding(24)
                        .frame(maxWidth: .infinity)
                        .background(passedColor.opacity(0.1))
                        .cornerRadius(16)
                        .overlay(
                            RoundedRectangle(cornerRadius: 16)
                                .stroke(passedColor.opacity(0.3), lineWidth: 2)
                        )

                        // Event Scores
                        VStack(spacing: 12) {
                            ForEach(score.eventScores, id: \.eventName) { eventScore in
                                EventScoreRow(eventScore: eventScore)
                            }
                        }

                        // Failure Reasons
                        if !score.failureReasons.isEmpty {
                            VStack(alignment: .leading, spacing: 8) {
                                Text("FAILURE REASONS")
                                    .font(.system(size: 12, weight: .bold))
                                    .tracking(1)
                                    .foregroundColor(.failRed)

                                ForEach(score.failureReasons, id: \.self) { reason in
                                    HStack(alignment: .top, spacing: 8) {
                                        Image(systemName: "exclamationmark.triangle.fill")
                                            .foregroundColor(.failRed)
                                            .font(.system(size: 12))
                                        Text(reason)
                                            .font(.system(size: 14))
                                            .foregroundColor(.white.opacity(0.8))
                                    }
                                }
                            }
                            .padding()
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .background(Color.failRed.opacity(0.1))
                            .cornerRadius(12)
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(Color.failRed.opacity(0.3), lineWidth: 1)
                            )
                        }

                        // Generate Form 705 Button
                        Button(action: { showForm705Dialog = true }) {
                            Text("GENERATE DA FORM 705")
                                .font(.system(size: 14, weight: .bold))
                                .tracking(0.5)
                                .foregroundColor(.armyGold)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 14)
                                .background(Color.white.opacity(0.1))
                                .cornerRadius(8)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 8)
                                        .stroke(Color.armyGold.opacity(0.5), lineWidth: 1)
                                )
                        }

                        // Buttons
                        HStack(spacing: 12) {
                            Button(action: onEditScores) {
                                Text("EDIT SCORES")
                                    .font(.system(size: 14, weight: .bold))
                                    .tracking(0.5)
                                    .foregroundColor(.white)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 14)
                                    .background(Color.white.opacity(0.1))
                                    .cornerRadius(8)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 8)
                                            .stroke(Color.white.opacity(0.3), lineWidth: 1)
                                    )
                            }

                            Button(action: onStartOver) {
                                Text("START OVER")
                                    .font(.system(size: 14, weight: .bold))
                                    .tracking(0.5)
                                    .foregroundColor(.armyBlack)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 14)
                                    .background(Color.armyGold)
                                    .cornerRadius(8)
                            }
                        }

                        // Scoring Information
                        VStack(alignment: .leading, spacing: 12) {
                            Text("SCORING INFORMATION")
                                .font(.system(size: 12, weight: .bold))
                                .tracking(1)
                                .foregroundColor(.armyGold)

                            InfoRow(label: "Category", value: score.mosCategoryName)
                            InfoRow(label: "Minimum per event", value: "60 points")
                            InfoRow(label: "Events taken", value: "\(score.eventScores.count)")

                            InfoRow(label: "Minimum total", value: "\(score.mosCategoryMinimum) points")

                            InfoRow(label: "Gender", value: score.soldierGender)
                            InfoRow(label: "Age bracket", value: score.soldierAgeBracketName)
                            InfoRow(label: "Scoring type", value: score.isCombatMos ? "Gender-neutral" : "Gender and age-normed")
                        }
                        .padding()
                        .background(Color.white.opacity(0.05))
                        .cornerRadius(12)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Color.white.opacity(0.1), lineWidth: 1)
                        )

                        Text("Based on official HQDA EXORD 218-25 scoring tables")
                            .font(.system(size: 11))
                            .foregroundColor(.white.opacity(0.4))

                        Spacer(minLength: 40)
                    }
                    .padding(16)
                }
            }
        }
        .sheet(isPresented: $showForm705Dialog) {
            Form705DialogView(
                soldierName: $soldierName,
                unitLocation: $unitLocation,
                mos: $mos,
                payGrade: $payGrade,
                graderName: $graderName,
                payGrades: payGrades,
                isGenerating: $isGenerating,
                onCancel: { showForm705Dialog = false },
                onGenerate: { generateForm705() }
            )
            .presentationDetents([.large])
            .presentationDragIndicator(.visible)
        }
        .sheet(isPresented: $showShareSheet, onDismiss: { generatedPDFURL = nil }) {
            if let url = generatedPDFURL, FileManager.default.fileExists(atPath: url.path) {
                ShareSheet(activityItems: [url])
            } else {
                VStack(spacing: 16) {
                    Image(systemName: "exclamationmark.triangle")
                        .font(.system(size: 48))
                        .foregroundColor(.orange)
                    Text("PDF Not Available")
                        .font(.headline)
                    Text("The PDF file could not be loaded.")
                        .font(.caption)
                        .multilineTextAlignment(.center)
                        .foregroundColor(.secondary)
                    Button("Close") {
                        showShareSheet = false
                    }
                    .buttonStyle(.borderedProminent)
                }
                .padding()
            }
        }
        .alert("Error", isPresented: $showError) {
            Button("OK", role: .cancel) {}
        } message: {
            Text(errorMessage ?? "Unknown error")
        }
        .onAppear {
            // Preload PDF template to avoid first-use initialization delays
            Form705Generator.preloadTemplate()
        }
        .onChange(of: showForm705Dialog) { _, isShowing in
            // When dialog dismisses and we have a pending URL, show share sheet
            if !isShowing, let url = pendingShareURL {
                print("Form705Dialog: Dialog dismissed, showing share sheet for \(url.path)")
                pendingShareURL = nil  // Clear pending
                generatedPDFURL = url  // Ensure URL is set
                showShareSheet = true
            }
        }
    }

    private func generateForm705() {
        isGenerating = true

        print("Form705Dialog: Starting generation...")
        print("Form705Dialog: Score has \(score.eventScores.count) events")
        print("Form705Dialog: Soldier: \(soldierName), MOS: \(mos)")

        let formData = Form705Data(
            soldierName: soldierName,
            graderName: graderName,
            unitLocation: unitLocation,
            mos: mos,
            payGrade: payGrade,
            score: score
        )

        // Try generation with automatic retry on failure
        attemptPDFGeneration(formData: formData, attempt: 1)
    }

    private func attemptPDFGeneration(formData: Form705Data, attempt: Int) {
        print("Form705Dialog: Attempt \(attempt)...")

        let result = Form705Generator.generateForm705(data: formData)

        switch result {
        case .success(let url):
            print("Form705Dialog: SUCCESS - PDF at \(url.path)")
            // Verify the file exists and has content
            Thread.sleep(forTimeInterval: 0.1) // Brief pause for file system
            if FileManager.default.fileExists(atPath: url.path),
               let attrs = try? FileManager.default.attributesOfItem(atPath: url.path),
               let size = attrs[.size] as? Int, size > 1000 {
                print("Form705Dialog: File verified - size: \(size) bytes")
                isGenerating = false
                // Store URL for sharing after dialog dismisses (reactive pattern)
                pendingShareURL = url
                generatedPDFURL = url
                // Dismiss dialog - onChange handler will show share sheet
                showForm705Dialog = false
            } else if attempt < 2 {
                // Retry once
                print("Form705Dialog: File verification failed, retrying...")
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                    self.attemptPDFGeneration(formData: formData, attempt: attempt + 1)
                }
            } else {
                print("Form705Dialog: ERROR - File missing or too small after retry")
                isGenerating = false
                errorMessage = "PDF generation failed - please try again"
                showError = true
            }
        case .failure(let error):
            if attempt < 2 {
                // Retry once
                print("Form705Dialog: Generation failed, retrying...")
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                    self.attemptPDFGeneration(formData: formData, attempt: attempt + 1)
                }
            } else {
                print("Form705Dialog: FAILURE after retry - \(error.localizedDescription)")
                isGenerating = false
                errorMessage = error.localizedDescription
                showError = true
            }
        }
    }
}

// MARK: - Form 705 Dialog View

struct Form705DialogView: View {
    @Binding var soldierName: String
    @Binding var unitLocation: String
    @Binding var mos: String
    @Binding var payGrade: String
    @Binding var graderName: String
    let payGrades: [String]
    @Binding var isGenerating: Bool
    let onCancel: () -> Void
    let onGenerate: () -> Void

    var body: some View {
        ZStack {
            Color.armyDarkGray.ignoresSafeArea()

            VStack(spacing: 0) {
                // Header
                HStack {
                    Text("GENERATE DA FORM 705")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.armyGold)
                    Spacer()
                    Button(action: onCancel) {
                        Image(systemName: "xmark")
                            .foregroundColor(.white.opacity(0.6))
                    }
                }
                .padding()
                .background(Color.armyBlack)

                ScrollView {
                    VStack(spacing: 16) {
                        // Soldier Name
                        FormTextField(
                            label: "Soldier Name",
                            placeholder: "Last, First, MI",
                            text: $soldierName
                        )

                        // Unit/Location
                        FormTextField(
                            label: "Unit/Location",
                            placeholder: "",
                            text: $unitLocation
                        )

                        // MOS and Pay Grade row
                        HStack(spacing: 12) {
                            FormTextField(
                                label: "MOS",
                                placeholder: "11B",
                                text: $mos
                            )

                            VStack(alignment: .leading, spacing: 8) {
                                Text("Pay Grade")
                                    .font(.system(size: 12, weight: .medium))
                                    .foregroundColor(.white.opacity(0.6))

                                Menu {
                                    ForEach(payGrades, id: \.self) { grade in
                                        Button(grade) {
                                            payGrade = grade
                                        }
                                    }
                                } label: {
                                    HStack {
                                        Text(payGrade)
                                            .foregroundColor(.white)
                                        Spacer()
                                        Image(systemName: "chevron.down")
                                            .foregroundColor(.armyGold)
                                    }
                                    .padding()
                                    .background(Color.white.opacity(0.1))
                                    .cornerRadius(8)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 8)
                                            .stroke(Color.white.opacity(0.3), lineWidth: 1)
                                    )
                                }
                            }
                        }

                        // Grader Name
                        FormTextField(
                            label: "Grader Name",
                            placeholder: "Last, First, MI",
                            text: $graderName
                        )

                        Text("Exempt events will be left blank. No signatures will be applied.")
                            .font(.system(size: 12))
                            .foregroundColor(.white.opacity(0.5))
                            .frame(maxWidth: .infinity, alignment: .leading)

                        // Buttons
                        HStack(spacing: 12) {
                            Button(action: onCancel) {
                                Text("CANCEL")
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.white)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 14)
                                    .background(Color.white.opacity(0.1))
                                    .cornerRadius(8)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 8)
                                            .stroke(Color.white.opacity(0.3), lineWidth: 1)
                                    )
                            }
                            .disabled(isGenerating)

                            Button(action: {
                                // Dismiss keyboard to commit text field values
                                UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                                // Small delay to ensure bindings are updated
                                DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                                    onGenerate()
                                }
                            }) {
                                if isGenerating {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle(tint: .armyBlack))
                                } else {
                                    Text("GENERATE")
                                        .font(.system(size: 14, weight: .bold))
                                }
                            }
                            .foregroundColor(.armyBlack)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                            .background(soldierName.isEmpty ? Color.armyGold.opacity(0.5) : Color.armyGold)
                            .cornerRadius(8)
                            .disabled(soldierName.isEmpty || isGenerating)
                        }
                        .padding(.top, 8)
                    }
                    .padding(16)
                }
            }
        }
    }
}

struct FormTextField: View {
    let label: String
    let placeholder: String
    @Binding var text: String

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(label)
                .font(.system(size: 12, weight: .medium))
                .foregroundColor(.white.opacity(0.6))

            TextField("", text: $text, prompt: Text(placeholder).foregroundColor(.white.opacity(0.3)))
                .foregroundColor(.white)
                .padding()
                .background(Color.white.opacity(0.1))
                .cornerRadius(8)
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(Color.white.opacity(0.3), lineWidth: 1)
                )
        }
    }
}

// MARK: - Share Sheet

struct ShareSheet: UIViewControllerRepresentable {
    let activityItems: [Any]

    func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: activityItems, applicationActivities: nil)
    }

    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}

// MARK: - Supporting Views

struct EventScoreRow: View {
    let eventScore: SwiftEventScore

    private var scoreColor: Color {
        if eventScore.points < 60 { return .failRed }
        if eventScore.points < 66 { return .warningAmber }
        return .passGreen
    }

    private var formattedRawValue: String {
        if eventScore.isTimeBased {
            let totalSeconds = Int(eventScore.rawValue)
            let mins = totalSeconds / 60
            let secs = totalSeconds % 60
            return "\(mins):\(String(format: "%02d", secs))"
        } else if eventScore.eventName.contains("Deadlift") {
            return "\(Int(eventScore.rawValue)) lbs"
        } else {
            return "\(Int(eventScore.rawValue)) reps"
        }
    }

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(eventScore.eventName.uppercased())
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(.white)
                Text(formattedRawValue)
                    .font(.system(size: 14))
                    .foregroundColor(.white.opacity(0.5))
            }
            Spacer()
            Text("\(eventScore.points)")
                .font(.system(size: 24, weight: .bold))
                .foregroundColor(scoreColor)
                .frame(width: 56)
                .padding(.vertical, 8)
                .background(scoreColor.opacity(0.2))
                .cornerRadius(8)
        }
        .padding()
        .background(Color.white.opacity(0.05))
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color.white.opacity(0.1), lineWidth: 1)
        )
    }
}

struct InfoRow: View {
    let label: String
    let value: String

    var body: some View {
        HStack {
            Text(label)
                .font(.system(size: 13))
                .foregroundColor(.white.opacity(0.6))
            Spacer()
            Text(value)
                .font(.system(size: 13, weight: .medium))
                .foregroundColor(.white)
        }
    }
}

// MARK: - Alternate Aerobic Card

struct AlternateAerobicCard: View {
    @Binding var selectedEvent: AftEvent
    @Binding var timeValue: String
    let maxPassingTime: String
    let points: Int32?
    let onSwitchToStandard: () -> Void

    private var selectedEventName: String {
        switch selectedEvent {
        case .walk25Mile: return "2.5-Mile Walk"
        case .row5k: return "5000-Meter Row"
        case .bike12k: return "12,000-Meter Bike"
        case .swim1k: return "1000-Meter Swim"
        default: return "2.5-Mile Walk"
        }
    }

    private var scoreColor: Color {
        guard let pts = points else { return .white.opacity(0.4) }
        if pts < 60 { return .failRed }
        return .passGreen
    }

    private var isPassing: Bool {
        guard let pts = points else { return false }
        return pts >= 60
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Header with switch back option
            HStack {
                Text("ALTERNATE AEROBIC EVENT")
                    .font(.system(size: 12, weight: .bold))
                    .tracking(1)
                    .foregroundColor(.armyGold)

                Spacer()

                Button(action: onSwitchToStandard) {
                    Text("Use 2-Mile Run")
                        .font(.system(size: 11, weight: .medium))
                        .foregroundColor(.armyGold)
                }
            }

            // Event selector
            Menu {
                Button("2.5-Mile Walk") { selectedEvent = .walk25Mile }
                Button("5000-Meter Row") { selectedEvent = .row5k }
                Button("12,000-Meter Bike") { selectedEvent = .bike12k }
                Button("1000-Meter Swim") { selectedEvent = .swim1k }
            } label: {
                HStack {
                    Text(selectedEventName)
                        .foregroundColor(.white)
                    Spacer()
                    Image(systemName: "chevron.down")
                        .foregroundColor(.armyGold)
                }
                .padding()
                .background(Color.white.opacity(0.1))
                .cornerRadius(8)
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(Color.white.opacity(0.3), lineWidth: 1)
                )
            }

            HStack(spacing: 16) {
                VStack(alignment: .leading, spacing: 4) {
                    TextField("", text: $timeValue)
                        .keyboardType(.numbersAndPunctuation)
                        .font(.system(size: 18, weight: .medium))
                        .foregroundColor(.white)
                        .padding()
                        .background(Color.white.opacity(0.1))
                        .cornerRadius(8)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.armyGold.opacity(0.5), lineWidth: 1)
                        )
                        .onChange(of: timeValue) { oldValue, newValue in
                            timeValue = formatTimeInput(newValue)
                        }

                    Text("Max \(maxPassingTime) to pass")
                        .font(.system(size: 11))
                        .foregroundColor(.white.opacity(0.5))

                    // Pass/Fail indicator
                    if points != nil {
                        Text(isPassing ? "PASS - Time meets standard" : "FAIL - Exceeds max time")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(isPassing ? .passGreen : .failRed)
                            .padding(.top, 4)
                    }
                }

                VStack(spacing: 4) {
                    Text(points != nil ? "\(points!)" : "--")
                        .font(.system(size: 28, weight: .bold))
                        .foregroundColor(scoreColor)
                        .frame(width: 64, height: 48)
                        .background(scoreColor.opacity(0.2))
                        .cornerRadius(8)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(scoreColor, lineWidth: 1)
                        )

                    Text("POINTS")
                        .font(.system(size: 10, weight: .medium))
                        .foregroundColor(.white.opacity(0.5))
                }
            }
        }
        .padding()
        .background(Color.white.opacity(0.05))
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color.white.opacity(0.1), lineWidth: 1)
        )
    }

    private func formatTimeInput(_ input: String) -> String {
        let digits = input.filter { $0.isNumber }
        let limited = String(digits.prefix(4))

        switch limited.count {
        case 0, 1, 2, 3:
            return limited
        case 4:
            let seconds = Int(limited.suffix(2)) ?? 0
            if seconds <= 59 {
                return "\(limited.prefix(2)):\(limited.suffix(2))"
            }
            return limited
        default:
            return String(limited.prefix(4))
        }
    }
}

// MARK: - Live Event Input Card

struct LiveEventInputCard: View {
    let title: String
    @Binding var value: String
    let hint: String
    let points: Int32?
    var keyboardType: UIKeyboardType = .numberPad
    var isTimeInput: Bool = false
    var showAlternateOption: Bool = false
    var onSwitchToAlternate: (() -> Void)? = nil
    @Binding var isExempt: Bool

    private var scoreColor: Color {
        if isExempt { return .white.opacity(0.3) }
        guard let pts = points else { return .white.opacity(0.4) }
        if pts < 60 { return .failRed }
        if pts < 66 { return .warningAmber }
        return .passGreen
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Header with title and exempt toggle
            HStack {
                Text(title)
                    .font(.system(size: 12, weight: .bold))
                    .tracking(1)
                    .foregroundColor(isExempt ? .white.opacity(0.5) : .armyGold)

                Spacer()

                HStack(spacing: 8) {
                    Text("Exempt")
                        .font(.system(size: 11))
                        .foregroundColor(.white.opacity(0.5))
                    Toggle("", isOn: $isExempt)
                        .labelsHidden()
                        .scaleEffect(0.8)
                        .tint(.armyGold)
                }
            }

            // Alternate option link (only for 2-mile run, when not exempt)
            if showAlternateOption && !isExempt, let onSwitch = onSwitchToAlternate {
                Button(action: onSwitch) {
                    Text("Use Alternate Aerobic Event")
                        .font(.system(size: 11, weight: .medium))
                        .foregroundColor(.armyGold)
                }
            }

            HStack(spacing: 16) {
                VStack(alignment: .leading, spacing: 4) {
                    TextField("", text: $value)
                        .keyboardType(keyboardType)
                        .font(.system(size: 18, weight: .medium))
                        .foregroundColor(isExempt ? .white.opacity(0.5) : .white)
                        .disabled(isExempt)
                        .padding()
                        .background(Color.white.opacity(isExempt ? 0.05 : 0.1))
                        .cornerRadius(8)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(isExempt ? Color.white.opacity(0.1) : Color.armyGold.opacity(0.5), lineWidth: 1)
                        )
                        .onChange(of: value) { oldValue, newValue in
                            if isTimeInput {
                                value = formatTimeInput(newValue)
                            }
                        }

                    Text(isExempt ? "Profile Exempt" : hint)
                        .font(.system(size: 11))
                        .foregroundColor(.white.opacity(0.5))
                }

                VStack(spacing: 4) {
                    Text(isExempt ? "60" : (points != nil ? "\(points!)" : "--"))
                        .font(.system(size: 28, weight: .bold))
                        .foregroundColor(scoreColor)
                        .frame(width: 64, height: 48)
                        .background(scoreColor.opacity(0.2))
                        .cornerRadius(8)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(scoreColor, lineWidth: 1)
                        )

                    Text("POINTS")
                        .font(.system(size: 10, weight: .medium))
                        .foregroundColor(.white.opacity(0.5))
                }
            }
        }
        .padding()
        .background(Color.white.opacity(0.05))
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color.white.opacity(0.1), lineWidth: 1)
        )
    }

    private func formatTimeInput(_ input: String) -> String {
        let digits = input.filter { $0.isNumber }
        let limited = String(digits.prefix(4))

        switch limited.count {
        case 0, 1, 2:
            return limited
        case 3:
            let seconds = Int(limited.suffix(2)) ?? 0
            if seconds <= 59 {
                return "\(limited.prefix(1)):\(limited.suffix(2))"
            }
            return limited
        case 4:
            let seconds = Int(limited.suffix(2)) ?? 0
            if seconds <= 59 {
                return "\(limited.prefix(2)):\(limited.suffix(2))"
            }
            return limited
        default:
            return String(limited.prefix(4))
        }
    }
}

#Preview {
    CalculatorView(gender: .male, age: 25, mosCategory: .combat, onDismiss: {})
}
