import Foundation

// MARK: - Core Types

enum Gender: String, CaseIterable {
    case male = "Male"
    case female = "Female"
}

enum MosCategory {
    case combat
    case combatEnabling

    var displayName: String {
        switch self {
        case .combat: return "Combat MOS"
        case .combatEnabling: return "Combat-Enabling MOS"
        }
    }

    var minimumPerEvent: Int {
        switch self {
        case .combat: return 60
        case .combatEnabling: return 60
        }
    }

    var minimumTotal: Int32 {
        switch self {
        case .combat: return 350
        case .combatEnabling: return 300
        }
    }
}

enum AgeBracket {
    case age17_21, age22_26, age27_31, age32_36, age37_41
    case age42_46, age47_51, age52_56, age57_61, age62Plus

    var displayName: String {
        switch self {
        case .age17_21: return "17-21"
        case .age22_26: return "22-26"
        case .age27_31: return "27-31"
        case .age32_36: return "32-36"
        case .age37_41: return "37-41"
        case .age42_46: return "42-46"
        case .age47_51: return "47-51"
        case .age52_56: return "52-56"
        case .age57_61: return "57-61"
        case .age62Plus: return "62+"
        }
    }

    static func fromAge(_ age: Int32) -> AgeBracket {
        switch age {
        case 17...21: return .age17_21
        case 22...26: return .age22_26
        case 27...31: return .age27_31
        case 32...36: return .age32_36
        case 37...41: return .age37_41
        case 42...46: return .age42_46
        case 47...51: return .age47_51
        case 52...56: return .age52_56
        case 57...61: return .age57_61
        default: return .age62Plus
        }
    }
}

enum AftEvent: CaseIterable {
    case deadlift
    case pushUp
    case sprintDragCarry
    case plank
    case twoMileRun
    case walk25Mile
    case row5k
    case bike12k
    case swim1k

    var displayName: String {
        switch self {
        case .deadlift: return "3-Rep Max Deadlift"
        case .pushUp: return "Hand-Release Push-Up"
        case .sprintDragCarry: return "Sprint-Drag-Carry"
        case .plank: return "Plank"
        case .twoMileRun: return "2-Mile Run"
        case .walk25Mile: return "2.5-Mile Walk"
        case .row5k: return "5000-Meter Row"
        case .bike12k: return "12,000-Meter Bike"
        case .swim1k: return "1000-Meter Swim"
        }
    }

    var isAlternateAerobic: Bool {
        switch self {
        case .walk25Mile, .row5k, .bike12k, .swim1k: return true
        default: return false
        }
    }

    static var alternateAerobicEvents: [AftEvent] {
        [.walk25Mile, .row5k, .bike12k, .swim1k]
    }
}

struct Soldier {
    let age: Int32
    let gender: Gender
    let mosCategory: MosCategory

    var ageBracket: AgeBracket {
        AgeBracket.fromAge(age)
    }
}

// MARK: - Score Entry for Tables

struct ScoreEntry {
    let rawValue: Double
    let points: Int
}

// MARK: - AFT Calculator

class AftCalculator {

    func calculateSingleEvent(
        event: AftEvent,
        rawValue: Double,
        soldier: Soldier
    ) -> (points: Int32, passed: Bool) {
        let isCombatMos = soldier.mosCategory == .combat
        let points = calculatePoints(event: event, rawValue: rawValue, soldier: soldier, isCombatMos: isCombatMos)
        return (Int32(points), points >= 60)
    }

    private func calculatePoints(event: AftEvent, rawValue: Double, soldier: Soldier, isCombatMos: Bool) -> Int {
        let ageBracket = soldier.ageBracket
        let gender = soldier.gender

        // Determine scoring category
        let useFemaleTables = !isCombatMos && gender == .female

        switch event {
        case .deadlift:
            return lookupScore(rawValue: rawValue, table: getDeadliftTable(ageBracket: ageBracket, female: useFemaleTables), higherIsBetter: true)
        case .pushUp:
            return lookupScore(rawValue: rawValue, table: getPushUpTable(ageBracket: ageBracket, female: useFemaleTables), higherIsBetter: true)
        case .sprintDragCarry:
            return lookupScore(rawValue: rawValue, table: getSDCTable(ageBracket: ageBracket, female: useFemaleTables), higherIsBetter: false)
        case .plank:
            return lookupScore(rawValue: rawValue, table: getPlankTable(ageBracket: ageBracket, female: useFemaleTables), higherIsBetter: true)
        case .twoMileRun:
            return lookupScore(rawValue: rawValue, table: getTwoMileRunTable(ageBracket: ageBracket, female: useFemaleTables), higherIsBetter: false)
        case .walk25Mile, .row5k, .bike12k, .swim1k:
            return calculateAlternateAerobicScore(event: event, timeInSeconds: rawValue, ageBracket: ageBracket, gender: gender, isCombatMos: isCombatMos)
        }
    }

    private func lookupScore(rawValue: Double, table: [ScoreEntry], higherIsBetter: Bool) -> Int {
        if table.isEmpty { return 0 }

        // Sort best-to-worst: descending for higher-is-better, ascending for lower-is-better
        let sortedTable = higherIsBetter
            ? table.sorted { $0.rawValue > $1.rawValue }
            : table.sorted { $0.rawValue < $1.rawValue }

        // Step function: find the first (best) threshold the soldier met
        for entry in sortedTable {
            let met = higherIsBetter
                ? rawValue >= entry.rawValue
                : rawValue <= entry.rawValue
            if met { return entry.points }
        }

        return 0
    }

    private func calculateAlternateAerobicScore(event: AftEvent, timeInSeconds: Double, ageBracket: AgeBracket, gender: Gender, isCombatMos: Bool) -> Int {
        // Alternate events are pass/fail at 60 points based on time standards
        let maxTime = getAlternateEventMaxTime(event: event, ageBracket: ageBracket, gender: gender, isCombatMos: isCombatMos)
        return timeInSeconds <= maxTime ? 60 : 0
    }

    private func getAlternateEventMaxTime(event: AftEvent, ageBracket: AgeBracket, gender: Gender, isCombatMos: Bool) -> Double {
        // Simplified max times - in production these would be full tables
        // Times in seconds
        switch event {
        case .walk25Mile: return 34 * 60 // 34:00
        case .row5k: return 25 * 60 // 25:00
        case .bike12k: return 25 * 60 // 25:00
        case .swim1k: return 25 * 60 // 25:00
        default: return 0
        }
    }


    private func t(_ min: Int, _ sec: Int) -> Double { Double(min * 60 + sec) }

    // MARK: - Scoring Tables

    private func getDeadliftTable(ageBracket: AgeBracket, female: Bool) -> [ScoreEntry] {
        if female { return getDeadliftTableFemale(ageBracket: ageBracket) }
        return getDeadliftTableMale(ageBracket: ageBracket)
    }

    private func getDeadliftTableMale(ageBracket: AgeBracket) -> [ScoreEntry] {
        switch ageBracket {
        case .age17_21:
            return [
                ScoreEntry(rawValue: 340, points: 100), ScoreEntry(rawValue: 330, points: 98), ScoreEntry(rawValue: 320, points: 96),
                ScoreEntry(rawValue: 310, points: 94), ScoreEntry(rawValue: 300, points: 92), ScoreEntry(rawValue: 290, points: 89),
                ScoreEntry(rawValue: 280, points: 87), ScoreEntry(rawValue: 270, points: 85), ScoreEntry(rawValue: 260, points: 83),
                ScoreEntry(rawValue: 250, points: 81), ScoreEntry(rawValue: 240, points: 79), ScoreEntry(rawValue: 230, points: 77),
                ScoreEntry(rawValue: 220, points: 75), ScoreEntry(rawValue: 210, points: 73), ScoreEntry(rawValue: 200, points: 70),
                ScoreEntry(rawValue: 190, points: 69), ScoreEntry(rawValue: 180, points: 67), ScoreEntry(rawValue: 170, points: 65),
                ScoreEntry(rawValue: 160, points: 63), ScoreEntry(rawValue: 150, points: 60), ScoreEntry(rawValue: 130, points: 50),
                ScoreEntry(rawValue: 120, points: 40), ScoreEntry(rawValue: 110, points: 30), ScoreEntry(rawValue: 100, points: 20),
                ScoreEntry(rawValue: 90, points: 10), ScoreEntry(rawValue: 80, points: 0)
            ]
        case .age22_26:
            return [
                ScoreEntry(rawValue: 350, points: 100), ScoreEntry(rawValue: 340, points: 99), ScoreEntry(rawValue: 330, points: 97),
                ScoreEntry(rawValue: 320, points: 95), ScoreEntry(rawValue: 310, points: 93), ScoreEntry(rawValue: 300, points: 91),
                ScoreEntry(rawValue: 290, points: 89), ScoreEntry(rawValue: 280, points: 87), ScoreEntry(rawValue: 270, points: 85),
                ScoreEntry(rawValue: 260, points: 83), ScoreEntry(rawValue: 250, points: 81), ScoreEntry(rawValue: 240, points: 79),
                ScoreEntry(rawValue: 230, points: 77), ScoreEntry(rawValue: 220, points: 75), ScoreEntry(rawValue: 210, points: 73),
                ScoreEntry(rawValue: 200, points: 71), ScoreEntry(rawValue: 190, points: 70), ScoreEntry(rawValue: 180, points: 67),
                ScoreEntry(rawValue: 170, points: 65), ScoreEntry(rawValue: 160, points: 63), ScoreEntry(rawValue: 150, points: 60),
                ScoreEntry(rawValue: 130, points: 50), ScoreEntry(rawValue: 120, points: 40), ScoreEntry(rawValue: 110, points: 30),
                ScoreEntry(rawValue: 100, points: 20), ScoreEntry(rawValue: 90, points: 10), ScoreEntry(rawValue: 80, points: 0)
            ]
        case .age27_31:
            return [
                ScoreEntry(rawValue: 350, points: 100), ScoreEntry(rawValue: 340, points: 98), ScoreEntry(rawValue: 330, points: 97),
                ScoreEntry(rawValue: 320, points: 95), ScoreEntry(rawValue: 310, points: 93), ScoreEntry(rawValue: 300, points: 91),
                ScoreEntry(rawValue: 290, points: 89), ScoreEntry(rawValue: 280, points: 87), ScoreEntry(rawValue: 270, points: 85),
                ScoreEntry(rawValue: 260, points: 83), ScoreEntry(rawValue: 250, points: 81), ScoreEntry(rawValue: 240, points: 79),
                ScoreEntry(rawValue: 230, points: 77), ScoreEntry(rawValue: 220, points: 75), ScoreEntry(rawValue: 210, points: 73),
                ScoreEntry(rawValue: 200, points: 71), ScoreEntry(rawValue: 190, points: 70), ScoreEntry(rawValue: 180, points: 67),
                ScoreEntry(rawValue: 170, points: 65), ScoreEntry(rawValue: 160, points: 63), ScoreEntry(rawValue: 150, points: 60),
                ScoreEntry(rawValue: 130, points: 50), ScoreEntry(rawValue: 120, points: 40), ScoreEntry(rawValue: 110, points: 30),
                ScoreEntry(rawValue: 100, points: 20), ScoreEntry(rawValue: 90, points: 10), ScoreEntry(rawValue: 80, points: 0)
            ]
        case .age32_36:
            return [
                ScoreEntry(rawValue: 350, points: 100), ScoreEntry(rawValue: 340, points: 99), ScoreEntry(rawValue: 330, points: 97),
                ScoreEntry(rawValue: 320, points: 95), ScoreEntry(rawValue: 310, points: 93), ScoreEntry(rawValue: 300, points: 91),
                ScoreEntry(rawValue: 290, points: 89), ScoreEntry(rawValue: 280, points: 87), ScoreEntry(rawValue: 270, points: 85),
                ScoreEntry(rawValue: 260, points: 83), ScoreEntry(rawValue: 250, points: 81), ScoreEntry(rawValue: 240, points: 79),
                ScoreEntry(rawValue: 230, points: 77), ScoreEntry(rawValue: 220, points: 75), ScoreEntry(rawValue: 210, points: 73),
                ScoreEntry(rawValue: 200, points: 70), ScoreEntry(rawValue: 190, points: 69), ScoreEntry(rawValue: 180, points: 67),
                ScoreEntry(rawValue: 170, points: 65), ScoreEntry(rawValue: 160, points: 63), ScoreEntry(rawValue: 150, points: 61),
                ScoreEntry(rawValue: 140, points: 60), ScoreEntry(rawValue: 130, points: 50), ScoreEntry(rawValue: 120, points: 40),
                ScoreEntry(rawValue: 110, points: 30), ScoreEntry(rawValue: 100, points: 20), ScoreEntry(rawValue: 90, points: 10),
                ScoreEntry(rawValue: 80, points: 0)
            ]
        case .age37_41:
            return [
                ScoreEntry(rawValue: 350, points: 100), ScoreEntry(rawValue: 340, points: 99), ScoreEntry(rawValue: 330, points: 97),
                ScoreEntry(rawValue: 320, points: 95), ScoreEntry(rawValue: 310, points: 93), ScoreEntry(rawValue: 300, points: 91),
                ScoreEntry(rawValue: 290, points: 89), ScoreEntry(rawValue: 280, points: 87), ScoreEntry(rawValue: 270, points: 85),
                ScoreEntry(rawValue: 260, points: 83), ScoreEntry(rawValue: 250, points: 81), ScoreEntry(rawValue: 240, points: 79),
                ScoreEntry(rawValue: 230, points: 77), ScoreEntry(rawValue: 220, points: 75), ScoreEntry(rawValue: 210, points: 73),
                ScoreEntry(rawValue: 200, points: 70), ScoreEntry(rawValue: 190, points: 69), ScoreEntry(rawValue: 180, points: 67),
                ScoreEntry(rawValue: 170, points: 65), ScoreEntry(rawValue: 160, points: 63), ScoreEntry(rawValue: 150, points: 62),
                ScoreEntry(rawValue: 140, points: 60), ScoreEntry(rawValue: 130, points: 50), ScoreEntry(rawValue: 120, points: 40),
                ScoreEntry(rawValue: 110, points: 30), ScoreEntry(rawValue: 100, points: 20), ScoreEntry(rawValue: 90, points: 10),
                ScoreEntry(rawValue: 80, points: 0)
            ]
        case .age42_46:
            return [
                ScoreEntry(rawValue: 350, points: 100), ScoreEntry(rawValue: 340, points: 99), ScoreEntry(rawValue: 330, points: 97),
                ScoreEntry(rawValue: 320, points: 95), ScoreEntry(rawValue: 310, points: 93), ScoreEntry(rawValue: 300, points: 92),
                ScoreEntry(rawValue: 290, points: 89), ScoreEntry(rawValue: 280, points: 88), ScoreEntry(rawValue: 270, points: 86),
                ScoreEntry(rawValue: 260, points: 84), ScoreEntry(rawValue: 250, points: 82), ScoreEntry(rawValue: 240, points: 79),
                ScoreEntry(rawValue: 230, points: 76), ScoreEntry(rawValue: 220, points: 76), ScoreEntry(rawValue: 210, points: 73),
                ScoreEntry(rawValue: 200, points: 70), ScoreEntry(rawValue: 190, points: 69), ScoreEntry(rawValue: 180, points: 67),
                ScoreEntry(rawValue: 170, points: 66), ScoreEntry(rawValue: 160, points: 64), ScoreEntry(rawValue: 150, points: 62),
                ScoreEntry(rawValue: 140, points: 60), ScoreEntry(rawValue: 130, points: 50), ScoreEntry(rawValue: 120, points: 40),
                ScoreEntry(rawValue: 110, points: 30), ScoreEntry(rawValue: 100, points: 20), ScoreEntry(rawValue: 90, points: 10),
                ScoreEntry(rawValue: 80, points: 0)
            ]
        case .age47_51:
            return [
                ScoreEntry(rawValue: 340, points: 100), ScoreEntry(rawValue: 330, points: 99), ScoreEntry(rawValue: 320, points: 97),
                ScoreEntry(rawValue: 310, points: 95), ScoreEntry(rawValue: 300, points: 93), ScoreEntry(rawValue: 290, points: 91),
                ScoreEntry(rawValue: 280, points: 89), ScoreEntry(rawValue: 270, points: 87), ScoreEntry(rawValue: 260, points: 84),
                ScoreEntry(rawValue: 250, points: 82), ScoreEntry(rawValue: 240, points: 79), ScoreEntry(rawValue: 230, points: 76),
                ScoreEntry(rawValue: 220, points: 74), ScoreEntry(rawValue: 210, points: 73), ScoreEntry(rawValue: 200, points: 70),
                ScoreEntry(rawValue: 190, points: 69), ScoreEntry(rawValue: 180, points: 67), ScoreEntry(rawValue: 170, points: 66),
                ScoreEntry(rawValue: 160, points: 64), ScoreEntry(rawValue: 150, points: 62), ScoreEntry(rawValue: 140, points: 60),
                ScoreEntry(rawValue: 130, points: 50), ScoreEntry(rawValue: 120, points: 40), ScoreEntry(rawValue: 110, points: 30),
                ScoreEntry(rawValue: 100, points: 20), ScoreEntry(rawValue: 90, points: 10), ScoreEntry(rawValue: 80, points: 0)
            ]
        case .age52_56:
            return [
                ScoreEntry(rawValue: 330, points: 100), ScoreEntry(rawValue: 320, points: 99), ScoreEntry(rawValue: 310, points: 97),
                ScoreEntry(rawValue: 300, points: 95), ScoreEntry(rawValue: 290, points: 93), ScoreEntry(rawValue: 280, points: 91),
                ScoreEntry(rawValue: 270, points: 89), ScoreEntry(rawValue: 260, points: 87), ScoreEntry(rawValue: 250, points: 84),
                ScoreEntry(rawValue: 240, points: 82), ScoreEntry(rawValue: 230, points: 80), ScoreEntry(rawValue: 220, points: 76),
                ScoreEntry(rawValue: 210, points: 73), ScoreEntry(rawValue: 200, points: 70), ScoreEntry(rawValue: 190, points: 68),
                ScoreEntry(rawValue: 180, points: 67), ScoreEntry(rawValue: 170, points: 65), ScoreEntry(rawValue: 160, points: 63),
                ScoreEntry(rawValue: 150, points: 61), ScoreEntry(rawValue: 140, points: 60), ScoreEntry(rawValue: 130, points: 50),
                ScoreEntry(rawValue: 120, points: 40), ScoreEntry(rawValue: 110, points: 30), ScoreEntry(rawValue: 100, points: 20),
                ScoreEntry(rawValue: 90, points: 10), ScoreEntry(rawValue: 80, points: 0)
            ]
        case .age57_61:
            return [
                ScoreEntry(rawValue: 250, points: 100), ScoreEntry(rawValue: 240, points: 99), ScoreEntry(rawValue: 230, points: 98),
                ScoreEntry(rawValue: 220, points: 97), ScoreEntry(rawValue: 210, points: 95), ScoreEntry(rawValue: 200, points: 94),
                ScoreEntry(rawValue: 190, points: 93), ScoreEntry(rawValue: 180, points: 91), ScoreEntry(rawValue: 170, points: 89),
                ScoreEntry(rawValue: 160, points: 85), ScoreEntry(rawValue: 150, points: 80), ScoreEntry(rawValue: 140, points: 72),
                ScoreEntry(rawValue: 130, points: 50), ScoreEntry(rawValue: 120, points: 40), ScoreEntry(rawValue: 110, points: 30),
                ScoreEntry(rawValue: 100, points: 20), ScoreEntry(rawValue: 90, points: 10), ScoreEntry(rawValue: 80, points: 0)
            ]
        case .age62Plus:
            return [
                ScoreEntry(rawValue: 230, points: 100), ScoreEntry(rawValue: 220, points: 99), ScoreEntry(rawValue: 210, points: 98),
                ScoreEntry(rawValue: 200, points: 95), ScoreEntry(rawValue: 190, points: 94), ScoreEntry(rawValue: 180, points: 92),
                ScoreEntry(rawValue: 170, points: 89), ScoreEntry(rawValue: 160, points: 82), ScoreEntry(rawValue: 150, points: 75),
                ScoreEntry(rawValue: 140, points: 72), ScoreEntry(rawValue: 130, points: 50), ScoreEntry(rawValue: 120, points: 40),
                ScoreEntry(rawValue: 110, points: 30), ScoreEntry(rawValue: 100, points: 20), ScoreEntry(rawValue: 90, points: 10),
                ScoreEntry(rawValue: 80, points: 0)
            ]
        }
    }

    private func getDeadliftTableFemale(ageBracket: AgeBracket) -> [ScoreEntry] {
        switch ageBracket {
        case .age17_21:
            return [
                ScoreEntry(rawValue: 220, points: 100), ScoreEntry(rawValue: 210, points: 98), ScoreEntry(rawValue: 200, points: 97),
                ScoreEntry(rawValue: 190, points: 94), ScoreEntry(rawValue: 180, points: 91), ScoreEntry(rawValue: 170, points: 88),
                ScoreEntry(rawValue: 160, points: 84), ScoreEntry(rawValue: 150, points: 80), ScoreEntry(rawValue: 140, points: 75),
                ScoreEntry(rawValue: 130, points: 68), ScoreEntry(rawValue: 120, points: 60), ScoreEntry(rawValue: 110, points: 50),
                ScoreEntry(rawValue: 100, points: 40), ScoreEntry(rawValue: 90, points: 30), ScoreEntry(rawValue: 80, points: 20),
                ScoreEntry(rawValue: 70, points: 10), ScoreEntry(rawValue: 60, points: 0)
            ]
        case .age22_26:
            return [
                ScoreEntry(rawValue: 230, points: 100), ScoreEntry(rawValue: 220, points: 99), ScoreEntry(rawValue: 210, points: 98),
                ScoreEntry(rawValue: 200, points: 97), ScoreEntry(rawValue: 190, points: 94), ScoreEntry(rawValue: 180, points: 91),
                ScoreEntry(rawValue: 170, points: 88), ScoreEntry(rawValue: 160, points: 84), ScoreEntry(rawValue: 150, points: 79),
                ScoreEntry(rawValue: 140, points: 75), ScoreEntry(rawValue: 130, points: 68), ScoreEntry(rawValue: 120, points: 60),
                ScoreEntry(rawValue: 110, points: 50), ScoreEntry(rawValue: 100, points: 40), ScoreEntry(rawValue: 90, points: 30),
                ScoreEntry(rawValue: 80, points: 20), ScoreEntry(rawValue: 70, points: 10), ScoreEntry(rawValue: 60, points: 0)
            ]
        case .age27_31:
            return [
                ScoreEntry(rawValue: 240, points: 100), ScoreEntry(rawValue: 230, points: 99), ScoreEntry(rawValue: 220, points: 98),
                ScoreEntry(rawValue: 210, points: 96), ScoreEntry(rawValue: 200, points: 95), ScoreEntry(rawValue: 190, points: 93),
                ScoreEntry(rawValue: 180, points: 91), ScoreEntry(rawValue: 170, points: 86), ScoreEntry(rawValue: 160, points: 82),
                ScoreEntry(rawValue: 150, points: 78), ScoreEntry(rawValue: 140, points: 73), ScoreEntry(rawValue: 130, points: 67),
                ScoreEntry(rawValue: 120, points: 60), ScoreEntry(rawValue: 110, points: 50), ScoreEntry(rawValue: 100, points: 40),
                ScoreEntry(rawValue: 90, points: 30), ScoreEntry(rawValue: 80, points: 20), ScoreEntry(rawValue: 70, points: 10),
                ScoreEntry(rawValue: 60, points: 0)
            ]
        case .age32_36:
            return [
                ScoreEntry(rawValue: 230, points: 100), ScoreEntry(rawValue: 220, points: 99), ScoreEntry(rawValue: 210, points: 97),
                ScoreEntry(rawValue: 200, points: 95), ScoreEntry(rawValue: 190, points: 93), ScoreEntry(rawValue: 180, points: 90),
                ScoreEntry(rawValue: 170, points: 87), ScoreEntry(rawValue: 160, points: 83), ScoreEntry(rawValue: 150, points: 79),
                ScoreEntry(rawValue: 140, points: 74), ScoreEntry(rawValue: 130, points: 68), ScoreEntry(rawValue: 120, points: 60),
                ScoreEntry(rawValue: 110, points: 50), ScoreEntry(rawValue: 100, points: 40), ScoreEntry(rawValue: 90, points: 30),
                ScoreEntry(rawValue: 80, points: 20), ScoreEntry(rawValue: 70, points: 10), ScoreEntry(rawValue: 60, points: 0)
            ]
        case .age37_41:
            return [
                ScoreEntry(rawValue: 220, points: 100), ScoreEntry(rawValue: 210, points: 99), ScoreEntry(rawValue: 200, points: 97),
                ScoreEntry(rawValue: 190, points: 95), ScoreEntry(rawValue: 180, points: 92), ScoreEntry(rawValue: 170, points: 89),
                ScoreEntry(rawValue: 160, points: 85), ScoreEntry(rawValue: 150, points: 80), ScoreEntry(rawValue: 140, points: 75),
                ScoreEntry(rawValue: 130, points: 69), ScoreEntry(rawValue: 120, points: 60), ScoreEntry(rawValue: 110, points: 50),
                ScoreEntry(rawValue: 100, points: 40), ScoreEntry(rawValue: 90, points: 30), ScoreEntry(rawValue: 80, points: 20),
                ScoreEntry(rawValue: 70, points: 10), ScoreEntry(rawValue: 60, points: 0)
            ]
        case .age42_46:
            return [
                ScoreEntry(rawValue: 210, points: 100), ScoreEntry(rawValue: 200, points: 98), ScoreEntry(rawValue: 190, points: 96),
                ScoreEntry(rawValue: 180, points: 92), ScoreEntry(rawValue: 170, points: 88), ScoreEntry(rawValue: 160, points: 84),
                ScoreEntry(rawValue: 150, points: 79), ScoreEntry(rawValue: 140, points: 74), ScoreEntry(rawValue: 130, points: 68),
                ScoreEntry(rawValue: 120, points: 60), ScoreEntry(rawValue: 110, points: 50), ScoreEntry(rawValue: 100, points: 40),
                ScoreEntry(rawValue: 90, points: 30), ScoreEntry(rawValue: 80, points: 20), ScoreEntry(rawValue: 70, points: 10),
                ScoreEntry(rawValue: 60, points: 0)
            ]
        case .age47_51:
            return [
                ScoreEntry(rawValue: 200, points: 100), ScoreEntry(rawValue: 190, points: 98), ScoreEntry(rawValue: 180, points: 93),
                ScoreEntry(rawValue: 170, points: 89), ScoreEntry(rawValue: 160, points: 84), ScoreEntry(rawValue: 150, points: 79),
                ScoreEntry(rawValue: 140, points: 74), ScoreEntry(rawValue: 130, points: 68), ScoreEntry(rawValue: 120, points: 60),
                ScoreEntry(rawValue: 110, points: 50), ScoreEntry(rawValue: 100, points: 40), ScoreEntry(rawValue: 90, points: 30),
                ScoreEntry(rawValue: 80, points: 20), ScoreEntry(rawValue: 70, points: 10), ScoreEntry(rawValue: 60, points: 0)
            ]
        case .age52_56:
            return [
                ScoreEntry(rawValue: 190, points: 100), ScoreEntry(rawValue: 180, points: 98), ScoreEntry(rawValue: 170, points: 95),
                ScoreEntry(rawValue: 160, points: 91), ScoreEntry(rawValue: 150, points: 85), ScoreEntry(rawValue: 140, points: 79),
                ScoreEntry(rawValue: 130, points: 72), ScoreEntry(rawValue: 120, points: 60), ScoreEntry(rawValue: 110, points: 50),
                ScoreEntry(rawValue: 100, points: 40), ScoreEntry(rawValue: 90, points: 30), ScoreEntry(rawValue: 80, points: 20),
                ScoreEntry(rawValue: 70, points: 10), ScoreEntry(rawValue: 60, points: 0)
            ]
        case .age57_61:
            return [
                ScoreEntry(rawValue: 170, points: 100), ScoreEntry(rawValue: 160, points: 99), ScoreEntry(rawValue: 150, points: 90),
                ScoreEntry(rawValue: 140, points: 80), ScoreEntry(rawValue: 130, points: 72), ScoreEntry(rawValue: 120, points: 60),
                ScoreEntry(rawValue: 110, points: 50), ScoreEntry(rawValue: 100, points: 40), ScoreEntry(rawValue: 90, points: 30),
                ScoreEntry(rawValue: 80, points: 20), ScoreEntry(rawValue: 70, points: 10), ScoreEntry(rawValue: 60, points: 0)
            ]
        case .age62Plus:
            return [
                ScoreEntry(rawValue: 170, points: 100), ScoreEntry(rawValue: 160, points: 99), ScoreEntry(rawValue: 150, points: 90),
                ScoreEntry(rawValue: 140, points: 80), ScoreEntry(rawValue: 130, points: 72), ScoreEntry(rawValue: 120, points: 60),
                ScoreEntry(rawValue: 110, points: 50), ScoreEntry(rawValue: 100, points: 40), ScoreEntry(rawValue: 90, points: 30),
                ScoreEntry(rawValue: 80, points: 20), ScoreEntry(rawValue: 70, points: 10), ScoreEntry(rawValue: 60, points: 0)
            ]
        }
    }

    private func getPushUpTable(ageBracket: AgeBracket, female: Bool) -> [ScoreEntry] {
        if female { return getPushUpTableFemale(ageBracket: ageBracket) }
        return getPushUpTableMale(ageBracket: ageBracket)
    }

    private func getPushUpTableMale(ageBracket: AgeBracket) -> [ScoreEntry] {
        switch ageBracket {
        case .age17_21:
            return [
                ScoreEntry(rawValue: 58, points: 100), ScoreEntry(rawValue: 57, points: 99), ScoreEntry(rawValue: 55, points: 98),
                ScoreEntry(rawValue: 54, points: 97), ScoreEntry(rawValue: 53, points: 96), ScoreEntry(rawValue: 52, points: 95),
                ScoreEntry(rawValue: 51, points: 94), ScoreEntry(rawValue: 49, points: 93), ScoreEntry(rawValue: 48, points: 92),
                ScoreEntry(rawValue: 47, points: 91), ScoreEntry(rawValue: 46, points: 90), ScoreEntry(rawValue: 45, points: 89),
                ScoreEntry(rawValue: 44, points: 88), ScoreEntry(rawValue: 43, points: 87), ScoreEntry(rawValue: 42, points: 86),
                ScoreEntry(rawValue: 41, points: 85), ScoreEntry(rawValue: 40, points: 84), ScoreEntry(rawValue: 39, points: 82),
                ScoreEntry(rawValue: 38, points: 81), ScoreEntry(rawValue: 37, points: 80), ScoreEntry(rawValue: 36, points: 79),
                ScoreEntry(rawValue: 35, points: 78), ScoreEntry(rawValue: 34, points: 77), ScoreEntry(rawValue: 33, points: 76),
                ScoreEntry(rawValue: 32, points: 75), ScoreEntry(rawValue: 31, points: 74), ScoreEntry(rawValue: 30, points: 73),
                ScoreEntry(rawValue: 29, points: 72), ScoreEntry(rawValue: 28, points: 70), ScoreEntry(rawValue: 26, points: 69),
                ScoreEntry(rawValue: 25, points: 68), ScoreEntry(rawValue: 24, points: 67), ScoreEntry(rawValue: 23, points: 66),
                ScoreEntry(rawValue: 22, points: 65), ScoreEntry(rawValue: 21, points: 64), ScoreEntry(rawValue: 19, points: 63),
                ScoreEntry(rawValue: 18, points: 62), ScoreEntry(rawValue: 17, points: 61), ScoreEntry(rawValue: 15, points: 60),
                ScoreEntry(rawValue: 9, points: 50), ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30),
                ScoreEntry(rawValue: 6, points: 20), ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age22_26:
            return [
                ScoreEntry(rawValue: 61, points: 100), ScoreEntry(rawValue: 59, points: 99), ScoreEntry(rawValue: 57, points: 98),
                ScoreEntry(rawValue: 56, points: 97), ScoreEntry(rawValue: 55, points: 96), ScoreEntry(rawValue: 53, points: 95),
                ScoreEntry(rawValue: 52, points: 94), ScoreEntry(rawValue: 51, points: 93), ScoreEntry(rawValue: 50, points: 92),
                ScoreEntry(rawValue: 49, points: 91), ScoreEntry(rawValue: 48, points: 90), ScoreEntry(rawValue: 46, points: 89),
                ScoreEntry(rawValue: 45, points: 88), ScoreEntry(rawValue: 44, points: 87), ScoreEntry(rawValue: 43, points: 86),
                ScoreEntry(rawValue: 42, points: 85), ScoreEntry(rawValue: 41, points: 84), ScoreEntry(rawValue: 40, points: 83),
                ScoreEntry(rawValue: 39, points: 82), ScoreEntry(rawValue: 38, points: 81), ScoreEntry(rawValue: 37, points: 80),
                ScoreEntry(rawValue: 36, points: 79), ScoreEntry(rawValue: 35, points: 78), ScoreEntry(rawValue: 34, points: 77),
                ScoreEntry(rawValue: 32, points: 76), ScoreEntry(rawValue: 31, points: 75), ScoreEntry(rawValue: 30, points: 74),
                ScoreEntry(rawValue: 29, points: 73), ScoreEntry(rawValue: 28, points: 72), ScoreEntry(rawValue: 27, points: 71),
                ScoreEntry(rawValue: 26, points: 70), ScoreEntry(rawValue: 25, points: 69), ScoreEntry(rawValue: 24, points: 68),
                ScoreEntry(rawValue: 23, points: 67), ScoreEntry(rawValue: 22, points: 66), ScoreEntry(rawValue: 21, points: 65),
                ScoreEntry(rawValue: 19, points: 64), ScoreEntry(rawValue: 18, points: 63), ScoreEntry(rawValue: 17, points: 62),
                ScoreEntry(rawValue: 15, points: 61), ScoreEntry(rawValue: 14, points: 60), ScoreEntry(rawValue: 9, points: 50),
                ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20),
                ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age27_31:
            return [
                ScoreEntry(rawValue: 62, points: 100), ScoreEntry(rawValue: 60, points: 99), ScoreEntry(rawValue: 58, points: 98),
                ScoreEntry(rawValue: 57, points: 97), ScoreEntry(rawValue: 55, points: 96), ScoreEntry(rawValue: 54, points: 95),
                ScoreEntry(rawValue: 53, points: 94), ScoreEntry(rawValue: 52, points: 93), ScoreEntry(rawValue: 51, points: 92),
                ScoreEntry(rawValue: 49, points: 91), ScoreEntry(rawValue: 48, points: 90), ScoreEntry(rawValue: 47, points: 89),
                ScoreEntry(rawValue: 46, points: 88), ScoreEntry(rawValue: 45, points: 87), ScoreEntry(rawValue: 44, points: 86),
                ScoreEntry(rawValue: 43, points: 85), ScoreEntry(rawValue: 42, points: 84), ScoreEntry(rawValue: 41, points: 83),
                ScoreEntry(rawValue: 39, points: 82), ScoreEntry(rawValue: 38, points: 81), ScoreEntry(rawValue: 37, points: 80),
                ScoreEntry(rawValue: 36, points: 79), ScoreEntry(rawValue: 35, points: 78), ScoreEntry(rawValue: 34, points: 77),
                ScoreEntry(rawValue: 33, points: 76), ScoreEntry(rawValue: 32, points: 75), ScoreEntry(rawValue: 31, points: 74),
                ScoreEntry(rawValue: 30, points: 73), ScoreEntry(rawValue: 29, points: 72), ScoreEntry(rawValue: 28, points: 71),
                ScoreEntry(rawValue: 26, points: 70), ScoreEntry(rawValue: 25, points: 69), ScoreEntry(rawValue: 24, points: 68),
                ScoreEntry(rawValue: 23, points: 67), ScoreEntry(rawValue: 22, points: 66), ScoreEntry(rawValue: 21, points: 65),
                ScoreEntry(rawValue: 20, points: 64), ScoreEntry(rawValue: 18, points: 63), ScoreEntry(rawValue: 17, points: 62),
                ScoreEntry(rawValue: 15, points: 61), ScoreEntry(rawValue: 14, points: 60), ScoreEntry(rawValue: 9, points: 50),
                ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20),
                ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age32_36:
            return [
                ScoreEntry(rawValue: 60, points: 100), ScoreEntry(rawValue: 58, points: 99), ScoreEntry(rawValue: 57, points: 98),
                ScoreEntry(rawValue: 55, points: 97), ScoreEntry(rawValue: 54, points: 96), ScoreEntry(rawValue: 53, points: 95),
                ScoreEntry(rawValue: 52, points: 94), ScoreEntry(rawValue: 51, points: 93), ScoreEntry(rawValue: 49, points: 92),
                ScoreEntry(rawValue: 48, points: 91), ScoreEntry(rawValue: 47, points: 90), ScoreEntry(rawValue: 46, points: 89),
                ScoreEntry(rawValue: 45, points: 88), ScoreEntry(rawValue: 44, points: 87), ScoreEntry(rawValue: 43, points: 86),
                ScoreEntry(rawValue: 42, points: 85), ScoreEntry(rawValue: 41, points: 84), ScoreEntry(rawValue: 40, points: 83),
                ScoreEntry(rawValue: 39, points: 82), ScoreEntry(rawValue: 37, points: 81), ScoreEntry(rawValue: 36, points: 80),
                ScoreEntry(rawValue: 35, points: 79), ScoreEntry(rawValue: 34, points: 78), ScoreEntry(rawValue: 33, points: 77),
                ScoreEntry(rawValue: 32, points: 76), ScoreEntry(rawValue: 31, points: 75), ScoreEntry(rawValue: 30, points: 74),
                ScoreEntry(rawValue: 29, points: 73), ScoreEntry(rawValue: 28, points: 72), ScoreEntry(rawValue: 27, points: 71),
                ScoreEntry(rawValue: 26, points: 70), ScoreEntry(rawValue: 25, points: 69), ScoreEntry(rawValue: 24, points: 68),
                ScoreEntry(rawValue: 22, points: 67), ScoreEntry(rawValue: 21, points: 66), ScoreEntry(rawValue: 20, points: 65),
                ScoreEntry(rawValue: 19, points: 64), ScoreEntry(rawValue: 18, points: 63), ScoreEntry(rawValue: 16, points: 62),
                ScoreEntry(rawValue: 15, points: 61), ScoreEntry(rawValue: 13, points: 60), ScoreEntry(rawValue: 9, points: 50),
                ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20),
                ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age37_41:
            return [
                ScoreEntry(rawValue: 59, points: 100), ScoreEntry(rawValue: 57, points: 99), ScoreEntry(rawValue: 55, points: 98),
                ScoreEntry(rawValue: 54, points: 97), ScoreEntry(rawValue: 53, points: 96), ScoreEntry(rawValue: 51, points: 95),
                ScoreEntry(rawValue: 50, points: 94), ScoreEntry(rawValue: 49, points: 93), ScoreEntry(rawValue: 48, points: 92),
                ScoreEntry(rawValue: 47, points: 91), ScoreEntry(rawValue: 46, points: 90), ScoreEntry(rawValue: 45, points: 89),
                ScoreEntry(rawValue: 44, points: 88), ScoreEntry(rawValue: 42, points: 87), ScoreEntry(rawValue: 41, points: 86),
                ScoreEntry(rawValue: 40, points: 85), ScoreEntry(rawValue: 39, points: 84), ScoreEntry(rawValue: 38, points: 83),
                ScoreEntry(rawValue: 37, points: 82), ScoreEntry(rawValue: 36, points: 81), ScoreEntry(rawValue: 35, points: 80),
                ScoreEntry(rawValue: 34, points: 79), ScoreEntry(rawValue: 33, points: 78), ScoreEntry(rawValue: 32, points: 77),
                ScoreEntry(rawValue: 31, points: 76), ScoreEntry(rawValue: 30, points: 75), ScoreEntry(rawValue: 29, points: 74),
                ScoreEntry(rawValue: 28, points: 73), ScoreEntry(rawValue: 27, points: 72), ScoreEntry(rawValue: 25, points: 71),
                ScoreEntry(rawValue: 24, points: 70), ScoreEntry(rawValue: 23, points: 69), ScoreEntry(rawValue: 22, points: 68),
                ScoreEntry(rawValue: 21, points: 67), ScoreEntry(rawValue: 20, points: 66), ScoreEntry(rawValue: 19, points: 65),
                ScoreEntry(rawValue: 18, points: 64), ScoreEntry(rawValue: 17, points: 63), ScoreEntry(rawValue: 15, points: 62),
                ScoreEntry(rawValue: 14, points: 61), ScoreEntry(rawValue: 12, points: 60), ScoreEntry(rawValue: 9, points: 50),
                ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20),
                ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age42_46:
            return [
                ScoreEntry(rawValue: 57, points: 100), ScoreEntry(rawValue: 55, points: 99), ScoreEntry(rawValue: 53, points: 98),
                ScoreEntry(rawValue: 52, points: 97), ScoreEntry(rawValue: 51, points: 96), ScoreEntry(rawValue: 49, points: 95),
                ScoreEntry(rawValue: 48, points: 94), ScoreEntry(rawValue: 47, points: 93), ScoreEntry(rawValue: 46, points: 92),
                ScoreEntry(rawValue: 45, points: 91), ScoreEntry(rawValue: 44, points: 90), ScoreEntry(rawValue: 43, points: 89),
                ScoreEntry(rawValue: 42, points: 88), ScoreEntry(rawValue: 41, points: 87), ScoreEntry(rawValue: 40, points: 86),
                ScoreEntry(rawValue: 39, points: 85), ScoreEntry(rawValue: 38, points: 84), ScoreEntry(rawValue: 37, points: 83),
                ScoreEntry(rawValue: 36, points: 82), ScoreEntry(rawValue: 35, points: 81), ScoreEntry(rawValue: 34, points: 80),
                ScoreEntry(rawValue: 33, points: 79), ScoreEntry(rawValue: 32, points: 78), ScoreEntry(rawValue: 31, points: 77),
                ScoreEntry(rawValue: 30, points: 76), ScoreEntry(rawValue: 29, points: 75), ScoreEntry(rawValue: 28, points: 74),
                ScoreEntry(rawValue: 26, points: 73), ScoreEntry(rawValue: 25, points: 72), ScoreEntry(rawValue: 24, points: 71),
                ScoreEntry(rawValue: 23, points: 70), ScoreEntry(rawValue: 22, points: 69), ScoreEntry(rawValue: 21, points: 68),
                ScoreEntry(rawValue: 20, points: 67), ScoreEntry(rawValue: 19, points: 66), ScoreEntry(rawValue: 18, points: 65),
                ScoreEntry(rawValue: 17, points: 64), ScoreEntry(rawValue: 16, points: 63), ScoreEntry(rawValue: 15, points: 62),
                ScoreEntry(rawValue: 13, points: 61), ScoreEntry(rawValue: 11, points: 60), ScoreEntry(rawValue: 9, points: 50),
                ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20),
                ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age47_51:
            return [
                ScoreEntry(rawValue: 55, points: 100), ScoreEntry(rawValue: 53, points: 99), ScoreEntry(rawValue: 51, points: 98),
                ScoreEntry(rawValue: 50, points: 97), ScoreEntry(rawValue: 49, points: 96), ScoreEntry(rawValue: 48, points: 95),
                ScoreEntry(rawValue: 46, points: 94), ScoreEntry(rawValue: 45, points: 93), ScoreEntry(rawValue: 44, points: 92),
                ScoreEntry(rawValue: 43, points: 91), ScoreEntry(rawValue: 42, points: 90), ScoreEntry(rawValue: 41, points: 89),
                ScoreEntry(rawValue: 40, points: 88), ScoreEntry(rawValue: 39, points: 87), ScoreEntry(rawValue: 38, points: 86),
                ScoreEntry(rawValue: 37, points: 85), ScoreEntry(rawValue: 36, points: 84), ScoreEntry(rawValue: 35, points: 83),
                ScoreEntry(rawValue: 34, points: 82), ScoreEntry(rawValue: 33, points: 81), ScoreEntry(rawValue: 32, points: 80),
                ScoreEntry(rawValue: 31, points: 79), ScoreEntry(rawValue: 30, points: 78), ScoreEntry(rawValue: 29, points: 77),
                ScoreEntry(rawValue: 28, points: 76), ScoreEntry(rawValue: 27, points: 75), ScoreEntry(rawValue: 26, points: 74),
                ScoreEntry(rawValue: 25, points: 73), ScoreEntry(rawValue: 24, points: 72), ScoreEntry(rawValue: 23, points: 71),
                ScoreEntry(rawValue: 22, points: 70), ScoreEntry(rawValue: 21, points: 69), ScoreEntry(rawValue: 20, points: 68),
                ScoreEntry(rawValue: 19, points: 67), ScoreEntry(rawValue: 18, points: 66), ScoreEntry(rawValue: 17, points: 65),
                ScoreEntry(rawValue: 16, points: 64), ScoreEntry(rawValue: 15, points: 63), ScoreEntry(rawValue: 14, points: 62),
                ScoreEntry(rawValue: 12, points: 61), ScoreEntry(rawValue: 11, points: 60), ScoreEntry(rawValue: 9, points: 50),
                ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20),
                ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age52_56:
            return [
                ScoreEntry(rawValue: 51, points: 100), ScoreEntry(rawValue: 50, points: 99), ScoreEntry(rawValue: 48, points: 98),
                ScoreEntry(rawValue: 47, points: 97), ScoreEntry(rawValue: 46, points: 96), ScoreEntry(rawValue: 45, points: 95),
                ScoreEntry(rawValue: 44, points: 94), ScoreEntry(rawValue: 43, points: 93), ScoreEntry(rawValue: 42, points: 92),
                ScoreEntry(rawValue: 41, points: 91), ScoreEntry(rawValue: 40, points: 90), ScoreEntry(rawValue: 39, points: 89),
                ScoreEntry(rawValue: 38, points: 88), ScoreEntry(rawValue: 37, points: 87), ScoreEntry(rawValue: 36, points: 86),
                ScoreEntry(rawValue: 35, points: 85), ScoreEntry(rawValue: 34, points: 84), ScoreEntry(rawValue: 33, points: 83),
                ScoreEntry(rawValue: 32, points: 82), ScoreEntry(rawValue: 31, points: 81), ScoreEntry(rawValue: 30, points: 80),
                ScoreEntry(rawValue: 29, points: 79), ScoreEntry(rawValue: 28, points: 78), ScoreEntry(rawValue: 27, points: 77),
                ScoreEntry(rawValue: 26, points: 76), ScoreEntry(rawValue: 25, points: 74), ScoreEntry(rawValue: 24, points: 73),
                ScoreEntry(rawValue: 23, points: 72), ScoreEntry(rawValue: 22, points: 71), ScoreEntry(rawValue: 21, points: 70),
                ScoreEntry(rawValue: 20, points: 69), ScoreEntry(rawValue: 19, points: 68), ScoreEntry(rawValue: 18, points: 67),
                ScoreEntry(rawValue: 17, points: 66), ScoreEntry(rawValue: 16, points: 65), ScoreEntry(rawValue: 15, points: 64),
                ScoreEntry(rawValue: 14, points: 63), ScoreEntry(rawValue: 13, points: 62), ScoreEntry(rawValue: 11, points: 61),
                ScoreEntry(rawValue: 10, points: 60), ScoreEntry(rawValue: 9, points: 50), ScoreEntry(rawValue: 8, points: 40),
                ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20), ScoreEntry(rawValue: 5, points: 10),
                ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age57_61:
            return [
                ScoreEntry(rawValue: 46, points: 100), ScoreEntry(rawValue: 43, points: 99), ScoreEntry(rawValue: 40, points: 98),
                ScoreEntry(rawValue: 38, points: 97), ScoreEntry(rawValue: 37, points: 96), ScoreEntry(rawValue: 35, points: 95),
                ScoreEntry(rawValue: 34, points: 94), ScoreEntry(rawValue: 33, points: 93), ScoreEntry(rawValue: 31, points: 92),
                ScoreEntry(rawValue: 30, points: 91), ScoreEntry(rawValue: 29, points: 90), ScoreEntry(rawValue: 26, points: 89),
                ScoreEntry(rawValue: 25, points: 88), ScoreEntry(rawValue: 24, points: 87), ScoreEntry(rawValue: 23, points: 86),
                ScoreEntry(rawValue: 22, points: 84), ScoreEntry(rawValue: 21, points: 83), ScoreEntry(rawValue: 20, points: 82),
                ScoreEntry(rawValue: 19, points: 80), ScoreEntry(rawValue: 18, points: 79), ScoreEntry(rawValue: 17, points: 77),
                ScoreEntry(rawValue: 16, points: 76), ScoreEntry(rawValue: 15, points: 75), ScoreEntry(rawValue: 14, points: 73),
                ScoreEntry(rawValue: 13, points: 72), ScoreEntry(rawValue: 12, points: 68), ScoreEntry(rawValue: 11, points: 65),
                ScoreEntry(rawValue: 10, points: 60), ScoreEntry(rawValue: 9, points: 50), ScoreEntry(rawValue: 8, points: 40),
                ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20), ScoreEntry(rawValue: 5, points: 10),
                ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age62Plus:
            return [
                ScoreEntry(rawValue: 43, points: 100), ScoreEntry(rawValue: 41, points: 99), ScoreEntry(rawValue: 39, points: 98),
                ScoreEntry(rawValue: 37, points: 97), ScoreEntry(rawValue: 35, points: 96), ScoreEntry(rawValue: 34, points: 95),
                ScoreEntry(rawValue: 33, points: 94), ScoreEntry(rawValue: 31, points: 93), ScoreEntry(rawValue: 30, points: 92),
                ScoreEntry(rawValue: 29, points: 91), ScoreEntry(rawValue: 26, points: 90), ScoreEntry(rawValue: 24, points: 89),
                ScoreEntry(rawValue: 23, points: 87), ScoreEntry(rawValue: 22, points: 86), ScoreEntry(rawValue: 21, points: 85),
                ScoreEntry(rawValue: 20, points: 84), ScoreEntry(rawValue: 19, points: 82), ScoreEntry(rawValue: 18, points: 81),
                ScoreEntry(rawValue: 17, points: 80), ScoreEntry(rawValue: 16, points: 79), ScoreEntry(rawValue: 15, points: 77),
                ScoreEntry(rawValue: 14, points: 76), ScoreEntry(rawValue: 13, points: 72), ScoreEntry(rawValue: 12, points: 70),
                ScoreEntry(rawValue: 11, points: 68), ScoreEntry(rawValue: 10, points: 60), ScoreEntry(rawValue: 9, points: 50),
                ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20),
                ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        }
    }

    private func getPushUpTableFemale(ageBracket: AgeBracket) -> [ScoreEntry] {
        switch ageBracket {
        case .age17_21:
            return [
                ScoreEntry(rawValue: 53, points: 100), ScoreEntry(rawValue: 48, points: 99), ScoreEntry(rawValue: 44, points: 98),
                ScoreEntry(rawValue: 42, points: 97), ScoreEntry(rawValue: 40, points: 96), ScoreEntry(rawValue: 38, points: 95),
                ScoreEntry(rawValue: 36, points: 94), ScoreEntry(rawValue: 35, points: 93), ScoreEntry(rawValue: 34, points: 92),
                ScoreEntry(rawValue: 33, points: 91), ScoreEntry(rawValue: 32, points: 90), ScoreEntry(rawValue: 31, points: 89),
                ScoreEntry(rawValue: 30, points: 88), ScoreEntry(rawValue: 29, points: 87), ScoreEntry(rawValue: 28, points: 86),
                ScoreEntry(rawValue: 27, points: 85), ScoreEntry(rawValue: 26, points: 84), ScoreEntry(rawValue: 25, points: 83),
                ScoreEntry(rawValue: 24, points: 81), ScoreEntry(rawValue: 23, points: 80), ScoreEntry(rawValue: 22, points: 79),
                ScoreEntry(rawValue: 21, points: 78), ScoreEntry(rawValue: 20, points: 76), ScoreEntry(rawValue: 19, points: 73),
                ScoreEntry(rawValue: 18, points: 70), ScoreEntry(rawValue: 15, points: 68), ScoreEntry(rawValue: 14, points: 66),
                ScoreEntry(rawValue: 13, points: 64), ScoreEntry(rawValue: 12, points: 62), ScoreEntry(rawValue: 11, points: 60),
                ScoreEntry(rawValue: 9, points: 50), ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30),
                ScoreEntry(rawValue: 6, points: 20), ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age22_26:
            return [
                ScoreEntry(rawValue: 50, points: 100), ScoreEntry(rawValue: 45, points: 99), ScoreEntry(rawValue: 44, points: 98),
                ScoreEntry(rawValue: 42, points: 97), ScoreEntry(rawValue: 40, points: 96), ScoreEntry(rawValue: 39, points: 95),
                ScoreEntry(rawValue: 38, points: 94), ScoreEntry(rawValue: 36, points: 93), ScoreEntry(rawValue: 35, points: 92),
                ScoreEntry(rawValue: 34, points: 91), ScoreEntry(rawValue: 33, points: 90), ScoreEntry(rawValue: 32, points: 89),
                ScoreEntry(rawValue: 31, points: 88), ScoreEntry(rawValue: 30, points: 87), ScoreEntry(rawValue: 29, points: 86),
                ScoreEntry(rawValue: 28, points: 85), ScoreEntry(rawValue: 27, points: 84), ScoreEntry(rawValue: 26, points: 83),
                ScoreEntry(rawValue: 25, points: 82), ScoreEntry(rawValue: 24, points: 81), ScoreEntry(rawValue: 23, points: 80),
                ScoreEntry(rawValue: 22, points: 78), ScoreEntry(rawValue: 21, points: 77), ScoreEntry(rawValue: 20, points: 76),
                ScoreEntry(rawValue: 19, points: 74), ScoreEntry(rawValue: 18, points: 73), ScoreEntry(rawValue: 17, points: 71),
                ScoreEntry(rawValue: 16, points: 70), ScoreEntry(rawValue: 15, points: 68), ScoreEntry(rawValue: 14, points: 66),
                ScoreEntry(rawValue: 13, points: 64), ScoreEntry(rawValue: 12, points: 62), ScoreEntry(rawValue: 11, points: 60),
                ScoreEntry(rawValue: 9, points: 50), ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30),
                ScoreEntry(rawValue: 6, points: 20), ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age27_31:
            return [
                ScoreEntry(rawValue: 48, points: 100), ScoreEntry(rawValue: 45, points: 99), ScoreEntry(rawValue: 43, points: 98),
                ScoreEntry(rawValue: 42, points: 97), ScoreEntry(rawValue: 40, points: 96), ScoreEntry(rawValue: 39, points: 95),
                ScoreEntry(rawValue: 37, points: 94), ScoreEntry(rawValue: 36, points: 93), ScoreEntry(rawValue: 35, points: 92),
                ScoreEntry(rawValue: 34, points: 91), ScoreEntry(rawValue: 33, points: 90), ScoreEntry(rawValue: 32, points: 89),
                ScoreEntry(rawValue: 31, points: 88), ScoreEntry(rawValue: 30, points: 87), ScoreEntry(rawValue: 29, points: 86),
                ScoreEntry(rawValue: 28, points: 85), ScoreEntry(rawValue: 27, points: 84), ScoreEntry(rawValue: 26, points: 83),
                ScoreEntry(rawValue: 25, points: 82), ScoreEntry(rawValue: 24, points: 81), ScoreEntry(rawValue: 23, points: 80),
                ScoreEntry(rawValue: 22, points: 78), ScoreEntry(rawValue: 21, points: 77), ScoreEntry(rawValue: 20, points: 76),
                ScoreEntry(rawValue: 19, points: 74), ScoreEntry(rawValue: 18, points: 73), ScoreEntry(rawValue: 17, points: 71),
                ScoreEntry(rawValue: 16, points: 70), ScoreEntry(rawValue: 15, points: 68), ScoreEntry(rawValue: 14, points: 66),
                ScoreEntry(rawValue: 13, points: 63), ScoreEntry(rawValue: 12, points: 62), ScoreEntry(rawValue: 11, points: 60),
                ScoreEntry(rawValue: 9, points: 50), ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30),
                ScoreEntry(rawValue: 6, points: 20), ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age32_36:
            return [
                ScoreEntry(rawValue: 47, points: 100), ScoreEntry(rawValue: 44, points: 99), ScoreEntry(rawValue: 42, points: 98),
                ScoreEntry(rawValue: 40, points: 97), ScoreEntry(rawValue: 39, points: 96), ScoreEntry(rawValue: 38, points: 95),
                ScoreEntry(rawValue: 36, points: 94), ScoreEntry(rawValue: 35, points: 93), ScoreEntry(rawValue: 34, points: 92),
                ScoreEntry(rawValue: 33, points: 91), ScoreEntry(rawValue: 32, points: 90), ScoreEntry(rawValue: 31, points: 89),
                ScoreEntry(rawValue: 30, points: 88), ScoreEntry(rawValue: 29, points: 87), ScoreEntry(rawValue: 28, points: 86),
                ScoreEntry(rawValue: 27, points: 85), ScoreEntry(rawValue: 26, points: 84), ScoreEntry(rawValue: 25, points: 83),
                ScoreEntry(rawValue: 24, points: 82), ScoreEntry(rawValue: 23, points: 80), ScoreEntry(rawValue: 22, points: 79),
                ScoreEntry(rawValue: 21, points: 78), ScoreEntry(rawValue: 20, points: 76), ScoreEntry(rawValue: 19, points: 74),
                ScoreEntry(rawValue: 18, points: 73), ScoreEntry(rawValue: 17, points: 72), ScoreEntry(rawValue: 16, points: 70),
                ScoreEntry(rawValue: 15, points: 68), ScoreEntry(rawValue: 14, points: 67), ScoreEntry(rawValue: 13, points: 65),
                ScoreEntry(rawValue: 12, points: 62), ScoreEntry(rawValue: 11, points: 60), ScoreEntry(rawValue: 9, points: 50),
                ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20),
                ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age37_41:
            return [
                ScoreEntry(rawValue: 43, points: 100), ScoreEntry(rawValue: 41, points: 99), ScoreEntry(rawValue: 39, points: 98),
                ScoreEntry(rawValue: 38, points: 97), ScoreEntry(rawValue: 37, points: 96), ScoreEntry(rawValue: 35, points: 95),
                ScoreEntry(rawValue: 34, points: 94), ScoreEntry(rawValue: 33, points: 93), ScoreEntry(rawValue: 32, points: 92),
                ScoreEntry(rawValue: 31, points: 91), ScoreEntry(rawValue: 30, points: 90), ScoreEntry(rawValue: 29, points: 89),
                ScoreEntry(rawValue: 28, points: 88), ScoreEntry(rawValue: 27, points: 87), ScoreEntry(rawValue: 26, points: 85),
                ScoreEntry(rawValue: 25, points: 84), ScoreEntry(rawValue: 24, points: 83), ScoreEntry(rawValue: 23, points: 82),
                ScoreEntry(rawValue: 22, points: 80), ScoreEntry(rawValue: 21, points: 79), ScoreEntry(rawValue: 20, points: 78),
                ScoreEntry(rawValue: 19, points: 76), ScoreEntry(rawValue: 18, points: 74), ScoreEntry(rawValue: 17, points: 73),
                ScoreEntry(rawValue: 16, points: 72), ScoreEntry(rawValue: 15, points: 70), ScoreEntry(rawValue: 14, points: 68),
                ScoreEntry(rawValue: 13, points: 66), ScoreEntry(rawValue: 12, points: 64), ScoreEntry(rawValue: 11, points: 61),
                ScoreEntry(rawValue: 10, points: 60), ScoreEntry(rawValue: 9, points: 50), ScoreEntry(rawValue: 8, points: 40),
                ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20), ScoreEntry(rawValue: 5, points: 10),
                ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age42_46:
            return [
                ScoreEntry(rawValue: 40, points: 100), ScoreEntry(rawValue: 38, points: 99), ScoreEntry(rawValue: 37, points: 98),
                ScoreEntry(rawValue: 36, points: 97), ScoreEntry(rawValue: 35, points: 96), ScoreEntry(rawValue: 33, points: 95),
                ScoreEntry(rawValue: 32, points: 94), ScoreEntry(rawValue: 31, points: 93), ScoreEntry(rawValue: 30, points: 92),
                ScoreEntry(rawValue: 29, points: 90), ScoreEntry(rawValue: 28, points: 89), ScoreEntry(rawValue: 27, points: 88),
                ScoreEntry(rawValue: 26, points: 86), ScoreEntry(rawValue: 25, points: 85), ScoreEntry(rawValue: 24, points: 84),
                ScoreEntry(rawValue: 23, points: 83), ScoreEntry(rawValue: 22, points: 82), ScoreEntry(rawValue: 21, points: 80),
                ScoreEntry(rawValue: 20, points: 79), ScoreEntry(rawValue: 19, points: 77), ScoreEntry(rawValue: 18, points: 76),
                ScoreEntry(rawValue: 17, points: 74), ScoreEntry(rawValue: 16, points: 72), ScoreEntry(rawValue: 15, points: 70),
                ScoreEntry(rawValue: 14, points: 68), ScoreEntry(rawValue: 13, points: 66), ScoreEntry(rawValue: 12, points: 64),
                ScoreEntry(rawValue: 11, points: 62), ScoreEntry(rawValue: 10, points: 60), ScoreEntry(rawValue: 9, points: 50),
                ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20),
                ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age47_51:
            return [
                ScoreEntry(rawValue: 38, points: 100), ScoreEntry(rawValue: 37, points: 99), ScoreEntry(rawValue: 35, points: 98),
                ScoreEntry(rawValue: 34, points: 97), ScoreEntry(rawValue: 33, points: 96), ScoreEntry(rawValue: 32, points: 95),
                ScoreEntry(rawValue: 31, points: 94), ScoreEntry(rawValue: 30, points: 93), ScoreEntry(rawValue: 29, points: 92),
                ScoreEntry(rawValue: 28, points: 91), ScoreEntry(rawValue: 27, points: 89), ScoreEntry(rawValue: 26, points: 88),
                ScoreEntry(rawValue: 25, points: 87), ScoreEntry(rawValue: 24, points: 86), ScoreEntry(rawValue: 23, points: 84),
                ScoreEntry(rawValue: 22, points: 83), ScoreEntry(rawValue: 21, points: 81), ScoreEntry(rawValue: 20, points: 80),
                ScoreEntry(rawValue: 19, points: 78), ScoreEntry(rawValue: 18, points: 76), ScoreEntry(rawValue: 17, points: 75),
                ScoreEntry(rawValue: 16, points: 73), ScoreEntry(rawValue: 15, points: 71), ScoreEntry(rawValue: 14, points: 69),
                ScoreEntry(rawValue: 13, points: 67), ScoreEntry(rawValue: 12, points: 64), ScoreEntry(rawValue: 11, points: 62),
                ScoreEntry(rawValue: 10, points: 60), ScoreEntry(rawValue: 9, points: 50), ScoreEntry(rawValue: 8, points: 40),
                ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20), ScoreEntry(rawValue: 5, points: 10),
                ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age52_56:
            return [
                ScoreEntry(rawValue: 36, points: 100), ScoreEntry(rawValue: 34, points: 99), ScoreEntry(rawValue: 33, points: 98),
                ScoreEntry(rawValue: 32, points: 97), ScoreEntry(rawValue: 31, points: 96), ScoreEntry(rawValue: 30, points: 95),
                ScoreEntry(rawValue: 29, points: 94), ScoreEntry(rawValue: 28, points: 93), ScoreEntry(rawValue: 27, points: 92),
                ScoreEntry(rawValue: 26, points: 90), ScoreEntry(rawValue: 25, points: 89), ScoreEntry(rawValue: 24, points: 88),
                ScoreEntry(rawValue: 23, points: 86), ScoreEntry(rawValue: 22, points: 85), ScoreEntry(rawValue: 21, points: 83),
                ScoreEntry(rawValue: 20, points: 82), ScoreEntry(rawValue: 19, points: 80), ScoreEntry(rawValue: 18, points: 78),
                ScoreEntry(rawValue: 17, points: 76), ScoreEntry(rawValue: 16, points: 74), ScoreEntry(rawValue: 15, points: 72),
                ScoreEntry(rawValue: 14, points: 70), ScoreEntry(rawValue: 13, points: 68), ScoreEntry(rawValue: 12, points: 65),
                ScoreEntry(rawValue: 11, points: 62), ScoreEntry(rawValue: 10, points: 60), ScoreEntry(rawValue: 9, points: 50),
                ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30), ScoreEntry(rawValue: 6, points: 20),
                ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age57_61:
            return [
                ScoreEntry(rawValue: 24, points: 100), ScoreEntry(rawValue: 23, points: 99), ScoreEntry(rawValue: 22, points: 98),
                ScoreEntry(rawValue: 21, points: 97), ScoreEntry(rawValue: 20, points: 96), ScoreEntry(rawValue: 19, points: 95),
                ScoreEntry(rawValue: 18, points: 94), ScoreEntry(rawValue: 17, points: 92), ScoreEntry(rawValue: 16, points: 91),
                ScoreEntry(rawValue: 15, points: 90), ScoreEntry(rawValue: 14, points: 89), ScoreEntry(rawValue: 13, points: 84),
                ScoreEntry(rawValue: 12, points: 80), ScoreEntry(rawValue: 11, points: 68), ScoreEntry(rawValue: 10, points: 60),
                ScoreEntry(rawValue: 9, points: 50), ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30),
                ScoreEntry(rawValue: 6, points: 20), ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        case .age62Plus:
            return [
                ScoreEntry(rawValue: 24, points: 100), ScoreEntry(rawValue: 23, points: 99), ScoreEntry(rawValue: 22, points: 98),
                ScoreEntry(rawValue: 21, points: 97), ScoreEntry(rawValue: 20, points: 96), ScoreEntry(rawValue: 19, points: 95),
                ScoreEntry(rawValue: 18, points: 94), ScoreEntry(rawValue: 17, points: 92), ScoreEntry(rawValue: 16, points: 91),
                ScoreEntry(rawValue: 15, points: 90), ScoreEntry(rawValue: 14, points: 89), ScoreEntry(rawValue: 13, points: 86),
                ScoreEntry(rawValue: 12, points: 79), ScoreEntry(rawValue: 11, points: 68), ScoreEntry(rawValue: 10, points: 60),
                ScoreEntry(rawValue: 9, points: 50), ScoreEntry(rawValue: 8, points: 40), ScoreEntry(rawValue: 7, points: 30),
                ScoreEntry(rawValue: 6, points: 20), ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 4, points: 0)
            ]
        }
    }

    private func getSDCTable(ageBracket: AgeBracket, female: Bool) -> [ScoreEntry] {
        if female { return getSDCTableFemale(ageBracket: ageBracket) }
        return getSDCTableMale(ageBracket: ageBracket)
    }

    private func getSDCTableMale(ageBracket: AgeBracket) -> [ScoreEntry] {
        switch ageBracket {
        case .age17_21:
            return [
                ScoreEntry(rawValue: t(1, 29), points: 100), ScoreEntry(rawValue: t(1, 31), points: 99), ScoreEntry(rawValue: t(1, 34), points: 98),
                ScoreEntry(rawValue: t(1, 35), points: 97), ScoreEntry(rawValue: t(1, 36), points: 96), ScoreEntry(rawValue: t(1, 37), points: 95),
                ScoreEntry(rawValue: t(1, 39), points: 94), ScoreEntry(rawValue: t(1, 40), points: 93), ScoreEntry(rawValue: t(1, 41), points: 92),
                ScoreEntry(rawValue: t(1, 42), points: 91), ScoreEntry(rawValue: t(1, 43), points: 90), ScoreEntry(rawValue: t(1, 44), points: 89),
                ScoreEntry(rawValue: t(1, 45), points: 88), ScoreEntry(rawValue: t(1, 46), points: 87), ScoreEntry(rawValue: t(1, 47), points: 86),
                ScoreEntry(rawValue: t(1, 48), points: 85), ScoreEntry(rawValue: t(1, 49), points: 84), ScoreEntry(rawValue: t(1, 50), points: 83),
                ScoreEntry(rawValue: t(1, 51), points: 82), ScoreEntry(rawValue: t(1, 52), points: 81), ScoreEntry(rawValue: t(1, 53), points: 80),
                ScoreEntry(rawValue: t(1, 54), points: 79), ScoreEntry(rawValue: t(1, 55), points: 78), ScoreEntry(rawValue: t(1, 56), points: 77),
                ScoreEntry(rawValue: t(1, 57), points: 76), ScoreEntry(rawValue: t(1, 58), points: 75), ScoreEntry(rawValue: t(1, 59), points: 74),
                ScoreEntry(rawValue: t(2, 0), points: 73), ScoreEntry(rawValue: t(2, 1), points: 72), ScoreEntry(rawValue: t(2, 2), points: 71),
                ScoreEntry(rawValue: t(2, 3), points: 70), ScoreEntry(rawValue: t(2, 4), points: 69), ScoreEntry(rawValue: t(2, 6), points: 68),
                ScoreEntry(rawValue: t(2, 7), points: 67), ScoreEntry(rawValue: t(2, 8), points: 66), ScoreEntry(rawValue: t(2, 11), points: 65),
                ScoreEntry(rawValue: t(2, 13), points: 64), ScoreEntry(rawValue: t(2, 15), points: 63), ScoreEntry(rawValue: t(2, 17), points: 62),
                ScoreEntry(rawValue: t(2, 22), points: 61), ScoreEntry(rawValue: t(2, 28), points: 60), ScoreEntry(rawValue: t(2, 38), points: 50),
                ScoreEntry(rawValue: t(2, 48), points: 40), ScoreEntry(rawValue: t(2, 58), points: 30), ScoreEntry(rawValue: t(3, 8), points: 20),
                ScoreEntry(rawValue: t(3, 18), points: 10), ScoreEntry(rawValue: t(3, 28), points: 0)
            ]
        case .age22_26:
            return [
                ScoreEntry(rawValue: t(1, 30), points: 100), ScoreEntry(rawValue: t(1, 32), points: 99), ScoreEntry(rawValue: t(1, 33), points: 98),
                ScoreEntry(rawValue: t(1, 34), points: 97), ScoreEntry(rawValue: t(1, 36), points: 96), ScoreEntry(rawValue: t(1, 37), points: 95),
                ScoreEntry(rawValue: t(1, 39), points: 94), ScoreEntry(rawValue: t(1, 40), points: 93), ScoreEntry(rawValue: t(1, 41), points: 92),
                ScoreEntry(rawValue: t(1, 42), points: 91), ScoreEntry(rawValue: t(1, 43), points: 90), ScoreEntry(rawValue: t(1, 44), points: 89),
                ScoreEntry(rawValue: t(1, 45), points: 88), ScoreEntry(rawValue: t(1, 46), points: 87), ScoreEntry(rawValue: t(1, 47), points: 86),
                ScoreEntry(rawValue: t(1, 48), points: 85), ScoreEntry(rawValue: t(1, 49), points: 84), ScoreEntry(rawValue: t(1, 50), points: 83),
                ScoreEntry(rawValue: t(1, 51), points: 82), ScoreEntry(rawValue: t(1, 52), points: 81), ScoreEntry(rawValue: t(1, 53), points: 80),
                ScoreEntry(rawValue: t(1, 54), points: 79), ScoreEntry(rawValue: t(1, 55), points: 78), ScoreEntry(rawValue: t(1, 56), points: 77),
                ScoreEntry(rawValue: t(1, 58), points: 76), ScoreEntry(rawValue: t(1, 59), points: 75), ScoreEntry(rawValue: t(2, 0), points: 74),
                ScoreEntry(rawValue: t(2, 1), points: 73), ScoreEntry(rawValue: t(2, 2), points: 72), ScoreEntry(rawValue: t(2, 3), points: 71),
                ScoreEntry(rawValue: t(2, 5), points: 70), ScoreEntry(rawValue: t(2, 7), points: 69), ScoreEntry(rawValue: t(2, 8), points: 68),
                ScoreEntry(rawValue: t(2, 10), points: 67), ScoreEntry(rawValue: t(2, 11), points: 66), ScoreEntry(rawValue: t(2, 14), points: 65),
                ScoreEntry(rawValue: t(2, 16), points: 64), ScoreEntry(rawValue: t(2, 18), points: 63), ScoreEntry(rawValue: t(2, 21), points: 62),
                ScoreEntry(rawValue: t(2, 26), points: 61), ScoreEntry(rawValue: t(2, 31), points: 60), ScoreEntry(rawValue: t(2, 41), points: 50),
                ScoreEntry(rawValue: t(2, 51), points: 40), ScoreEntry(rawValue: t(3, 1), points: 30), ScoreEntry(rawValue: t(3, 11), points: 20),
                ScoreEntry(rawValue: t(3, 21), points: 10), ScoreEntry(rawValue: t(3, 31), points: 0)
            ]
        case .age27_31:
            return [
                ScoreEntry(rawValue: t(1, 30), points: 100), ScoreEntry(rawValue: t(1, 31), points: 99), ScoreEntry(rawValue: t(1, 34), points: 98),
                ScoreEntry(rawValue: t(1, 35), points: 97), ScoreEntry(rawValue: t(1, 37), points: 96), ScoreEntry(rawValue: t(1, 38), points: 95),
                ScoreEntry(rawValue: t(1, 40), points: 94), ScoreEntry(rawValue: t(1, 41), points: 93), ScoreEntry(rawValue: t(1, 42), points: 92),
                ScoreEntry(rawValue: t(1, 43), points: 91), ScoreEntry(rawValue: t(1, 45), points: 90), ScoreEntry(rawValue: t(1, 46), points: 89),
                ScoreEntry(rawValue: t(1, 47), points: 88), ScoreEntry(rawValue: t(1, 48), points: 87), ScoreEntry(rawValue: t(1, 49), points: 86),
                ScoreEntry(rawValue: t(1, 50), points: 85), ScoreEntry(rawValue: t(1, 51), points: 84), ScoreEntry(rawValue: t(1, 52), points: 83),
                ScoreEntry(rawValue: t(1, 53), points: 82), ScoreEntry(rawValue: t(1, 54), points: 81), ScoreEntry(rawValue: t(1, 55), points: 80),
                ScoreEntry(rawValue: t(1, 56), points: 79), ScoreEntry(rawValue: t(1, 57), points: 78), ScoreEntry(rawValue: t(1, 58), points: 77),
                ScoreEntry(rawValue: t(1, 59), points: 76), ScoreEntry(rawValue: t(2, 0), points: 75), ScoreEntry(rawValue: t(2, 1), points: 74),
                ScoreEntry(rawValue: t(2, 2), points: 73), ScoreEntry(rawValue: t(2, 4), points: 72), ScoreEntry(rawValue: t(2, 5), points: 71),
                ScoreEntry(rawValue: t(2, 6), points: 70), ScoreEntry(rawValue: t(2, 8), points: 69), ScoreEntry(rawValue: t(2, 10), points: 68),
                ScoreEntry(rawValue: t(2, 11), points: 67), ScoreEntry(rawValue: t(2, 13), points: 66), ScoreEntry(rawValue: t(2, 15), points: 65),
                ScoreEntry(rawValue: t(2, 17), points: 64), ScoreEntry(rawValue: t(2, 20), points: 63), ScoreEntry(rawValue: t(2, 22), points: 62),
                ScoreEntry(rawValue: t(2, 28), points: 61), ScoreEntry(rawValue: t(2, 32), points: 60), ScoreEntry(rawValue: t(2, 42), points: 50),
                ScoreEntry(rawValue: t(2, 52), points: 40), ScoreEntry(rawValue: t(3, 2), points: 30), ScoreEntry(rawValue: t(3, 12), points: 20),
                ScoreEntry(rawValue: t(3, 22), points: 10), ScoreEntry(rawValue: t(3, 32), points: 0)
            ]
        case .age32_36:
            return [
                ScoreEntry(rawValue: t(1, 33), points: 100), ScoreEntry(rawValue: t(1, 34), points: 99), ScoreEntry(rawValue: t(1, 37), points: 98),
                ScoreEntry(rawValue: t(1, 38), points: 97), ScoreEntry(rawValue: t(1, 40), points: 96), ScoreEntry(rawValue: t(1, 41), points: 95),
                ScoreEntry(rawValue: t(1, 43), points: 94), ScoreEntry(rawValue: t(1, 44), points: 93), ScoreEntry(rawValue: t(1, 45), points: 92),
                ScoreEntry(rawValue: t(1, 46), points: 91), ScoreEntry(rawValue: t(1, 48), points: 90), ScoreEntry(rawValue: t(1, 49), points: 89),
                ScoreEntry(rawValue: t(1, 50), points: 88), ScoreEntry(rawValue: t(1, 51), points: 87), ScoreEntry(rawValue: t(1, 52), points: 86),
                ScoreEntry(rawValue: t(1, 53), points: 85), ScoreEntry(rawValue: t(1, 54), points: 84), ScoreEntry(rawValue: t(1, 55), points: 83),
                ScoreEntry(rawValue: t(1, 56), points: 82), ScoreEntry(rawValue: t(1, 57), points: 81), ScoreEntry(rawValue: t(1, 58), points: 80),
                ScoreEntry(rawValue: t(1, 59), points: 79), ScoreEntry(rawValue: t(2, 0), points: 78), ScoreEntry(rawValue: t(2, 1), points: 77),
                ScoreEntry(rawValue: t(2, 2), points: 76), ScoreEntry(rawValue: t(2, 3), points: 75), ScoreEntry(rawValue: t(2, 4), points: 74),
                ScoreEntry(rawValue: t(2, 5), points: 73), ScoreEntry(rawValue: t(2, 7), points: 72), ScoreEntry(rawValue: t(2, 8), points: 71),
                ScoreEntry(rawValue: t(2, 10), points: 70), ScoreEntry(rawValue: t(2, 11), points: 69), ScoreEntry(rawValue: t(2, 13), points: 68),
                ScoreEntry(rawValue: t(2, 15), points: 67), ScoreEntry(rawValue: t(2, 16), points: 66), ScoreEntry(rawValue: t(2, 19), points: 65),
                ScoreEntry(rawValue: t(2, 21), points: 64), ScoreEntry(rawValue: t(2, 24), points: 63), ScoreEntry(rawValue: t(2, 26), points: 62),
                ScoreEntry(rawValue: t(2, 31), points: 61), ScoreEntry(rawValue: t(2, 36), points: 60), ScoreEntry(rawValue: t(2, 46), points: 50),
                ScoreEntry(rawValue: t(2, 56), points: 40), ScoreEntry(rawValue: t(3, 6), points: 30), ScoreEntry(rawValue: t(3, 16), points: 20),
                ScoreEntry(rawValue: t(3, 26), points: 10), ScoreEntry(rawValue: t(3, 36), points: 0)
            ]
        case .age37_41:
            return [
                ScoreEntry(rawValue: t(1, 36), points: 100), ScoreEntry(rawValue: t(1, 37), points: 99), ScoreEntry(rawValue: t(1, 40), points: 98),
                ScoreEntry(rawValue: t(1, 42), points: 97), ScoreEntry(rawValue: t(1, 43), points: 96), ScoreEntry(rawValue: t(1, 45), points: 95),
                ScoreEntry(rawValue: t(1, 47), points: 94), ScoreEntry(rawValue: t(1, 48), points: 93), ScoreEntry(rawValue: t(1, 49), points: 92),
                ScoreEntry(rawValue: t(1, 50), points: 91), ScoreEntry(rawValue: t(1, 52), points: 90), ScoreEntry(rawValue: t(1, 53), points: 89),
                ScoreEntry(rawValue: t(1, 54), points: 88), ScoreEntry(rawValue: t(1, 55), points: 87), ScoreEntry(rawValue: t(1, 56), points: 86),
                ScoreEntry(rawValue: t(1, 57), points: 85), ScoreEntry(rawValue: t(1, 58), points: 84), ScoreEntry(rawValue: t(1, 59), points: 83),
                ScoreEntry(rawValue: t(2, 0), points: 82), ScoreEntry(rawValue: t(2, 1), points: 81), ScoreEntry(rawValue: t(2, 2), points: 80),
                ScoreEntry(rawValue: t(2, 3), points: 79), ScoreEntry(rawValue: t(2, 4), points: 78), ScoreEntry(rawValue: t(2, 5), points: 77),
                ScoreEntry(rawValue: t(2, 7), points: 76), ScoreEntry(rawValue: t(2, 8), points: 75), ScoreEntry(rawValue: t(2, 9), points: 74),
                ScoreEntry(rawValue: t(2, 10), points: 73), ScoreEntry(rawValue: t(2, 12), points: 72), ScoreEntry(rawValue: t(2, 13), points: 71),
                ScoreEntry(rawValue: t(2, 14), points: 70), ScoreEntry(rawValue: t(2, 16), points: 69), ScoreEntry(rawValue: t(2, 18), points: 68),
                ScoreEntry(rawValue: t(2, 20), points: 67), ScoreEntry(rawValue: t(2, 21), points: 66), ScoreEntry(rawValue: t(2, 24), points: 65),
                ScoreEntry(rawValue: t(2, 26), points: 64), ScoreEntry(rawValue: t(2, 28), points: 63), ScoreEntry(rawValue: t(2, 31), points: 62),
                ScoreEntry(rawValue: t(2, 36), points: 61), ScoreEntry(rawValue: t(2, 41), points: 60), ScoreEntry(rawValue: t(2, 51), points: 50),
                ScoreEntry(rawValue: t(3, 1), points: 40), ScoreEntry(rawValue: t(3, 11), points: 30), ScoreEntry(rawValue: t(3, 21), points: 20),
                ScoreEntry(rawValue: t(3, 31), points: 10), ScoreEntry(rawValue: t(3, 41), points: 0)
            ]
        case .age42_46:
            return [
                ScoreEntry(rawValue: t(1, 40), points: 100), ScoreEntry(rawValue: t(1, 42), points: 99), ScoreEntry(rawValue: t(1, 44), points: 98),
                ScoreEntry(rawValue: t(1, 46), points: 97), ScoreEntry(rawValue: t(1, 48), points: 96), ScoreEntry(rawValue: t(1, 49), points: 95),
                ScoreEntry(rawValue: t(1, 51), points: 94), ScoreEntry(rawValue: t(1, 52), points: 93), ScoreEntry(rawValue: t(1, 53), points: 92),
                ScoreEntry(rawValue: t(1, 54), points: 91), ScoreEntry(rawValue: t(1, 56), points: 90), ScoreEntry(rawValue: t(1, 57), points: 89),
                ScoreEntry(rawValue: t(1, 58), points: 88), ScoreEntry(rawValue: t(1, 59), points: 87), ScoreEntry(rawValue: t(2, 0), points: 86),
                ScoreEntry(rawValue: t(2, 1), points: 85), ScoreEntry(rawValue: t(2, 2), points: 84), ScoreEntry(rawValue: t(2, 4), points: 83),
                ScoreEntry(rawValue: t(2, 5), points: 82), ScoreEntry(rawValue: t(2, 6), points: 81), ScoreEntry(rawValue: t(2, 7), points: 80),
                ScoreEntry(rawValue: t(2, 8), points: 79), ScoreEntry(rawValue: t(2, 9), points: 78), ScoreEntry(rawValue: t(2, 10), points: 77),
                ScoreEntry(rawValue: t(2, 12), points: 76), ScoreEntry(rawValue: t(2, 13), points: 75), ScoreEntry(rawValue: t(2, 14), points: 74),
                ScoreEntry(rawValue: t(2, 15), points: 73), ScoreEntry(rawValue: t(2, 17), points: 72), ScoreEntry(rawValue: t(2, 18), points: 71),
                ScoreEntry(rawValue: t(2, 20), points: 70), ScoreEntry(rawValue: t(2, 22), points: 69), ScoreEntry(rawValue: t(2, 23), points: 68),
                ScoreEntry(rawValue: t(2, 25), points: 67), ScoreEntry(rawValue: t(2, 26), points: 66), ScoreEntry(rawValue: t(2, 29), points: 65),
                ScoreEntry(rawValue: t(2, 31), points: 64), ScoreEntry(rawValue: t(2, 33), points: 63), ScoreEntry(rawValue: t(2, 36), points: 62),
                ScoreEntry(rawValue: t(2, 41), points: 61), ScoreEntry(rawValue: t(2, 45), points: 60), ScoreEntry(rawValue: t(2, 55), points: 50),
                ScoreEntry(rawValue: t(3, 5), points: 40), ScoreEntry(rawValue: t(3, 15), points: 30), ScoreEntry(rawValue: t(3, 25), points: 20),
                ScoreEntry(rawValue: t(3, 35), points: 10), ScoreEntry(rawValue: t(3, 45), points: 0)
            ]
        case .age47_51:
            return [
                ScoreEntry(rawValue: t(1, 45), points: 100), ScoreEntry(rawValue: t(1, 46), points: 99), ScoreEntry(rawValue: t(1, 50), points: 98),
                ScoreEntry(rawValue: t(1, 52), points: 97), ScoreEntry(rawValue: t(1, 54), points: 96), ScoreEntry(rawValue: t(1, 55), points: 95),
                ScoreEntry(rawValue: t(1, 57), points: 94), ScoreEntry(rawValue: t(1, 59), points: 93), ScoreEntry(rawValue: t(2, 0), points: 92),
                ScoreEntry(rawValue: t(2, 1), points: 91), ScoreEntry(rawValue: t(2, 2), points: 90), ScoreEntry(rawValue: t(2, 3), points: 89),
                ScoreEntry(rawValue: t(2, 5), points: 88), ScoreEntry(rawValue: t(2, 6), points: 87), ScoreEntry(rawValue: t(2, 7), points: 86),
                ScoreEntry(rawValue: t(2, 8), points: 85), ScoreEntry(rawValue: t(2, 9), points: 84), ScoreEntry(rawValue: t(2, 10), points: 83),
                ScoreEntry(rawValue: t(2, 12), points: 82), ScoreEntry(rawValue: t(2, 13), points: 81), ScoreEntry(rawValue: t(2, 14), points: 80),
                ScoreEntry(rawValue: t(2, 15), points: 79), ScoreEntry(rawValue: t(2, 16), points: 78), ScoreEntry(rawValue: t(2, 17), points: 77),
                ScoreEntry(rawValue: t(2, 19), points: 76), ScoreEntry(rawValue: t(2, 20), points: 75), ScoreEntry(rawValue: t(2, 21), points: 74),
                ScoreEntry(rawValue: t(2, 23), points: 73), ScoreEntry(rawValue: t(2, 25), points: 72), ScoreEntry(rawValue: t(2, 26), points: 71),
                ScoreEntry(rawValue: t(2, 27), points: 70), ScoreEntry(rawValue: t(2, 29), points: 69), ScoreEntry(rawValue: t(2, 30), points: 68),
                ScoreEntry(rawValue: t(2, 32), points: 67), ScoreEntry(rawValue: t(2, 34), points: 66), ScoreEntry(rawValue: t(2, 37), points: 65),
                ScoreEntry(rawValue: t(2, 39), points: 64), ScoreEntry(rawValue: t(2, 41), points: 63), ScoreEntry(rawValue: t(2, 44), points: 62),
                ScoreEntry(rawValue: t(2, 48), points: 61), ScoreEntry(rawValue: t(2, 53), points: 60), ScoreEntry(rawValue: t(3, 3), points: 50),
                ScoreEntry(rawValue: t(3, 13), points: 40), ScoreEntry(rawValue: t(3, 23), points: 30), ScoreEntry(rawValue: t(3, 33), points: 20),
                ScoreEntry(rawValue: t(3, 43), points: 10), ScoreEntry(rawValue: t(3, 53), points: 0)
            ]
        case .age52_56:
            return [
                ScoreEntry(rawValue: t(1, 52), points: 100), ScoreEntry(rawValue: t(1, 55), points: 99), ScoreEntry(rawValue: t(1, 57), points: 98),
                ScoreEntry(rawValue: t(2, 0), points: 97), ScoreEntry(rawValue: t(2, 1), points: 96), ScoreEntry(rawValue: t(2, 3), points: 95),
                ScoreEntry(rawValue: t(2, 5), points: 94), ScoreEntry(rawValue: t(2, 6), points: 93), ScoreEntry(rawValue: t(2, 7), points: 92),
                ScoreEntry(rawValue: t(2, 9), points: 91), ScoreEntry(rawValue: t(2, 10), points: 90), ScoreEntry(rawValue: t(2, 11), points: 89),
                ScoreEntry(rawValue: t(2, 13), points: 88), ScoreEntry(rawValue: t(2, 14), points: 87), ScoreEntry(rawValue: t(2, 15), points: 86),
                ScoreEntry(rawValue: t(2, 16), points: 85), ScoreEntry(rawValue: t(2, 17), points: 84), ScoreEntry(rawValue: t(2, 19), points: 83),
                ScoreEntry(rawValue: t(2, 20), points: 82), ScoreEntry(rawValue: t(2, 21), points: 81), ScoreEntry(rawValue: t(2, 23), points: 80),
                ScoreEntry(rawValue: t(2, 25), points: 78), ScoreEntry(rawValue: t(2, 26), points: 77), ScoreEntry(rawValue: t(2, 28), points: 76),
                ScoreEntry(rawValue: t(2, 29), points: 75), ScoreEntry(rawValue: t(2, 30), points: 74), ScoreEntry(rawValue: t(2, 31), points: 73),
                ScoreEntry(rawValue: t(2, 32), points: 72), ScoreEntry(rawValue: t(2, 34), points: 71), ScoreEntry(rawValue: t(2, 35), points: 70),
                ScoreEntry(rawValue: t(2, 37), points: 69), ScoreEntry(rawValue: t(2, 38), points: 68), ScoreEntry(rawValue: t(2, 40), points: 67),
                ScoreEntry(rawValue: t(2, 41), points: 66), ScoreEntry(rawValue: t(2, 44), points: 65), ScoreEntry(rawValue: t(2, 46), points: 64),
                ScoreEntry(rawValue: t(2, 48), points: 63), ScoreEntry(rawValue: t(2, 50), points: 62), ScoreEntry(rawValue: t(2, 57), points: 61),
                ScoreEntry(rawValue: t(3, 0), points: 60), ScoreEntry(rawValue: t(3, 10), points: 50), ScoreEntry(rawValue: t(3, 20), points: 40),
                ScoreEntry(rawValue: t(3, 30), points: 30), ScoreEntry(rawValue: t(3, 40), points: 20), ScoreEntry(rawValue: t(3, 50), points: 10),
                ScoreEntry(rawValue: t(4, 0), points: 0)
            ]
        case .age57_61:
            return [
                ScoreEntry(rawValue: t(1, 58), points: 100), ScoreEntry(rawValue: t(2, 2), points: 99), ScoreEntry(rawValue: t(2, 3), points: 98),
                ScoreEntry(rawValue: t(2, 6), points: 97), ScoreEntry(rawValue: t(2, 8), points: 96), ScoreEntry(rawValue: t(2, 9), points: 95),
                ScoreEntry(rawValue: t(2, 11), points: 94), ScoreEntry(rawValue: t(2, 13), points: 93), ScoreEntry(rawValue: t(2, 15), points: 92),
                ScoreEntry(rawValue: t(2, 16), points: 91), ScoreEntry(rawValue: t(2, 17), points: 90), ScoreEntry(rawValue: t(2, 19), points: 89),
                ScoreEntry(rawValue: t(2, 20), points: 88), ScoreEntry(rawValue: t(2, 21), points: 87), ScoreEntry(rawValue: t(2, 22), points: 86),
                ScoreEntry(rawValue: t(2, 23), points: 85), ScoreEntry(rawValue: t(2, 24), points: 84), ScoreEntry(rawValue: t(2, 26), points: 83),
                ScoreEntry(rawValue: t(2, 27), points: 82), ScoreEntry(rawValue: t(2, 28), points: 81), ScoreEntry(rawValue: t(2, 29), points: 80),
                ScoreEntry(rawValue: t(2, 30), points: 79), ScoreEntry(rawValue: t(2, 31), points: 78), ScoreEntry(rawValue: t(2, 33), points: 77),
                ScoreEntry(rawValue: t(2, 35), points: 76), ScoreEntry(rawValue: t(2, 36), points: 75), ScoreEntry(rawValue: t(2, 37), points: 74),
                ScoreEntry(rawValue: t(2, 38), points: 73), ScoreEntry(rawValue: t(2, 40), points: 72), ScoreEntry(rawValue: t(2, 42), points: 71),
                ScoreEntry(rawValue: t(2, 43), points: 70), ScoreEntry(rawValue: t(2, 45), points: 69), ScoreEntry(rawValue: t(2, 47), points: 68),
                ScoreEntry(rawValue: t(2, 48), points: 67), ScoreEntry(rawValue: t(2, 50), points: 66), ScoreEntry(rawValue: t(2, 53), points: 65),
                ScoreEntry(rawValue: t(2, 55), points: 64), ScoreEntry(rawValue: t(2, 57), points: 63), ScoreEntry(rawValue: t(2, 59), points: 62),
                ScoreEntry(rawValue: t(3, 4), points: 61), ScoreEntry(rawValue: t(3, 12), points: 60), ScoreEntry(rawValue: t(3, 22), points: 50),
                ScoreEntry(rawValue: t(3, 32), points: 40), ScoreEntry(rawValue: t(3, 42), points: 30), ScoreEntry(rawValue: t(3, 52), points: 20),
                ScoreEntry(rawValue: t(4, 2), points: 10), ScoreEntry(rawValue: t(4, 12), points: 0)
            ]
        case .age62Plus:
            return [
                ScoreEntry(rawValue: t(2, 9), points: 100), ScoreEntry(rawValue: t(2, 12), points: 99), ScoreEntry(rawValue: t(2, 13), points: 97),
                ScoreEntry(rawValue: t(2, 14), points: 95), ScoreEntry(rawValue: t(2, 15), points: 94), ScoreEntry(rawValue: t(2, 16), points: 93),
                ScoreEntry(rawValue: t(2, 17), points: 89), ScoreEntry(rawValue: t(2, 18), points: 88), ScoreEntry(rawValue: t(2, 19), points: 87),
                ScoreEntry(rawValue: t(2, 20), points: 86), ScoreEntry(rawValue: t(2, 21), points: 85), ScoreEntry(rawValue: t(2, 22), points: 84),
                ScoreEntry(rawValue: t(2, 23), points: 83), ScoreEntry(rawValue: t(2, 24), points: 82), ScoreEntry(rawValue: t(2, 27), points: 81),
                ScoreEntry(rawValue: t(2, 32), points: 80), ScoreEntry(rawValue: t(2, 33), points: 79), ScoreEntry(rawValue: t(2, 35), points: 78),
                ScoreEntry(rawValue: t(2, 36), points: 77), ScoreEntry(rawValue: t(2, 38), points: 76), ScoreEntry(rawValue: t(2, 41), points: 75),
                ScoreEntry(rawValue: t(2, 43), points: 74), ScoreEntry(rawValue: t(2, 44), points: 73), ScoreEntry(rawValue: t(2, 46), points: 72),
                ScoreEntry(rawValue: t(2, 47), points: 71), ScoreEntry(rawValue: t(2, 49), points: 70), ScoreEntry(rawValue: t(2, 52), points: 69),
                ScoreEntry(rawValue: t(2, 56), points: 68), ScoreEntry(rawValue: t(2, 57), points: 67), ScoreEntry(rawValue: t(3, 0), points: 66),
                ScoreEntry(rawValue: t(3, 3), points: 65), ScoreEntry(rawValue: t(3, 9), points: 64), ScoreEntry(rawValue: t(3, 11), points: 63),
                ScoreEntry(rawValue: t(3, 12), points: 62), ScoreEntry(rawValue: t(3, 14), points: 61), ScoreEntry(rawValue: t(3, 16), points: 60),
                ScoreEntry(rawValue: t(3, 26), points: 50), ScoreEntry(rawValue: t(3, 36), points: 40), ScoreEntry(rawValue: t(3, 46), points: 30),
                ScoreEntry(rawValue: t(3, 56), points: 20), ScoreEntry(rawValue: t(4, 6), points: 10), ScoreEntry(rawValue: t(4, 16), points: 0)
            ]
        }
    }

    private func getSDCTableFemale(ageBracket: AgeBracket) -> [ScoreEntry] {
        switch ageBracket {
        case .age17_21:
            return [
                ScoreEntry(rawValue: t(1, 55), points: 100), ScoreEntry(rawValue: t(1, 59), points: 99), ScoreEntry(rawValue: t(2, 2), points: 98),
                ScoreEntry(rawValue: t(2, 5), points: 97), ScoreEntry(rawValue: t(2, 6), points: 96), ScoreEntry(rawValue: t(2, 8), points: 95),
                ScoreEntry(rawValue: t(2, 10), points: 94), ScoreEntry(rawValue: t(2, 12), points: 93), ScoreEntry(rawValue: t(2, 13), points: 92),
                ScoreEntry(rawValue: t(2, 14), points: 91), ScoreEntry(rawValue: t(2, 16), points: 90), ScoreEntry(rawValue: t(2, 17), points: 89),
                ScoreEntry(rawValue: t(2, 18), points: 88), ScoreEntry(rawValue: t(2, 20), points: 87), ScoreEntry(rawValue: t(2, 21), points: 86),
                ScoreEntry(rawValue: t(2, 22), points: 85), ScoreEntry(rawValue: t(2, 23), points: 84), ScoreEntry(rawValue: t(2, 24), points: 83),
                ScoreEntry(rawValue: t(2, 25), points: 82), ScoreEntry(rawValue: t(2, 26), points: 81), ScoreEntry(rawValue: t(2, 28), points: 80),
                ScoreEntry(rawValue: t(2, 29), points: 79), ScoreEntry(rawValue: t(2, 30), points: 78), ScoreEntry(rawValue: t(2, 31), points: 77),
                ScoreEntry(rawValue: t(2, 33), points: 76), ScoreEntry(rawValue: t(2, 34), points: 75), ScoreEntry(rawValue: t(2, 35), points: 74),
                ScoreEntry(rawValue: t(2, 37), points: 73), ScoreEntry(rawValue: t(2, 39), points: 72), ScoreEntry(rawValue: t(2, 40), points: 71),
                ScoreEntry(rawValue: t(2, 41), points: 70), ScoreEntry(rawValue: t(2, 44), points: 69), ScoreEntry(rawValue: t(2, 45), points: 68),
                ScoreEntry(rawValue: t(2, 47), points: 67), ScoreEntry(rawValue: t(2, 49), points: 66), ScoreEntry(rawValue: t(2, 53), points: 65),
                ScoreEntry(rawValue: t(2, 55), points: 64), ScoreEntry(rawValue: t(2, 58), points: 63), ScoreEntry(rawValue: t(3, 0), points: 62),
                ScoreEntry(rawValue: t(3, 8), points: 61), ScoreEntry(rawValue: t(3, 15), points: 60), ScoreEntry(rawValue: t(3, 25), points: 50),
                ScoreEntry(rawValue: t(3, 35), points: 40), ScoreEntry(rawValue: t(3, 45), points: 30), ScoreEntry(rawValue: t(3, 55), points: 20),
                ScoreEntry(rawValue: t(4, 5), points: 10), ScoreEntry(rawValue: t(4, 15), points: 0)
            ]
        case .age22_26:
            return [
                ScoreEntry(rawValue: t(1, 55), points: 100), ScoreEntry(rawValue: t(2, 0), points: 98), ScoreEntry(rawValue: t(2, 5), points: 96),
                ScoreEntry(rawValue: t(2, 10), points: 94), ScoreEntry(rawValue: t(2, 15), points: 92), ScoreEntry(rawValue: t(2, 20), points: 88),
                ScoreEntry(rawValue: t(2, 25), points: 84), ScoreEntry(rawValue: t(2, 30), points: 80), ScoreEntry(rawValue: t(2, 35), points: 76),
                ScoreEntry(rawValue: t(2, 40), points: 72), ScoreEntry(rawValue: t(2, 45), points: 68), ScoreEntry(rawValue: t(2, 50), points: 64),
                ScoreEntry(rawValue: t(3, 0), points: 62), ScoreEntry(rawValue: t(3, 15), points: 60), ScoreEntry(rawValue: t(3, 25), points: 50),
                ScoreEntry(rawValue: t(3, 35), points: 40), ScoreEntry(rawValue: t(3, 45), points: 30), ScoreEntry(rawValue: t(3, 55), points: 20),
                ScoreEntry(rawValue: t(4, 5), points: 10), ScoreEntry(rawValue: t(4, 15), points: 0)
            ]
        case .age27_31:
            return [
                ScoreEntry(rawValue: t(1, 55), points: 100), ScoreEntry(rawValue: t(2, 1), points: 98), ScoreEntry(rawValue: t(2, 6), points: 96),
                ScoreEntry(rawValue: t(2, 12), points: 94), ScoreEntry(rawValue: t(2, 16), points: 92), ScoreEntry(rawValue: t(2, 20), points: 88),
                ScoreEntry(rawValue: t(2, 26), points: 84), ScoreEntry(rawValue: t(2, 31), points: 80), ScoreEntry(rawValue: t(2, 36), points: 76),
                ScoreEntry(rawValue: t(2, 43), points: 70), ScoreEntry(rawValue: t(2, 50), points: 66), ScoreEntry(rawValue: t(3, 0), points: 62),
                ScoreEntry(rawValue: t(3, 15), points: 60), ScoreEntry(rawValue: t(3, 25), points: 50), ScoreEntry(rawValue: t(3, 35), points: 40),
                ScoreEntry(rawValue: t(3, 45), points: 30), ScoreEntry(rawValue: t(3, 55), points: 20), ScoreEntry(rawValue: t(4, 5), points: 10),
                ScoreEntry(rawValue: t(4, 15), points: 0)
            ]
        case .age32_36:
            return [
                ScoreEntry(rawValue: t(1, 59), points: 100), ScoreEntry(rawValue: t(2, 5), points: 98), ScoreEntry(rawValue: t(2, 10), points: 96),
                ScoreEntry(rawValue: t(2, 15), points: 94), ScoreEntry(rawValue: t(2, 20), points: 92), ScoreEntry(rawValue: t(2, 26), points: 88),
                ScoreEntry(rawValue: t(2, 32), points: 84), ScoreEntry(rawValue: t(2, 38), points: 80), ScoreEntry(rawValue: t(2, 45), points: 75),
                ScoreEntry(rawValue: t(2, 52), points: 70), ScoreEntry(rawValue: t(3, 0), points: 66), ScoreEntry(rawValue: t(3, 10), points: 62),
                ScoreEntry(rawValue: t(3, 22), points: 60), ScoreEntry(rawValue: t(3, 32), points: 50), ScoreEntry(rawValue: t(3, 42), points: 40),
                ScoreEntry(rawValue: t(3, 52), points: 30), ScoreEntry(rawValue: t(4, 2), points: 20), ScoreEntry(rawValue: t(4, 12), points: 10),
                ScoreEntry(rawValue: t(4, 22), points: 0)
            ]
        case .age37_41:
            return [
                ScoreEntry(rawValue: t(2, 2), points: 100), ScoreEntry(rawValue: t(2, 10), points: 98), ScoreEntry(rawValue: t(2, 15), points: 96),
                ScoreEntry(rawValue: t(2, 21), points: 94), ScoreEntry(rawValue: t(2, 26), points: 92), ScoreEntry(rawValue: t(2, 32), points: 88),
                ScoreEntry(rawValue: t(2, 38), points: 84), ScoreEntry(rawValue: t(2, 45), points: 80), ScoreEntry(rawValue: t(2, 52), points: 75),
                ScoreEntry(rawValue: t(3, 0), points: 70), ScoreEntry(rawValue: t(3, 8), points: 66), ScoreEntry(rawValue: t(3, 17), points: 62),
                ScoreEntry(rawValue: t(3, 27), points: 60), ScoreEntry(rawValue: t(3, 37), points: 50), ScoreEntry(rawValue: t(3, 47), points: 40),
                ScoreEntry(rawValue: t(3, 57), points: 30), ScoreEntry(rawValue: t(4, 7), points: 20), ScoreEntry(rawValue: t(4, 17), points: 10),
                ScoreEntry(rawValue: t(4, 27), points: 0)
            ]
        case .age42_46:
            return [
                ScoreEntry(rawValue: t(2, 9), points: 100), ScoreEntry(rawValue: t(2, 15), points: 98), ScoreEntry(rawValue: t(2, 20), points: 96),
                ScoreEntry(rawValue: t(2, 27), points: 94), ScoreEntry(rawValue: t(2, 33), points: 92), ScoreEntry(rawValue: t(2, 40), points: 88),
                ScoreEntry(rawValue: t(2, 47), points: 84), ScoreEntry(rawValue: t(2, 55), points: 80), ScoreEntry(rawValue: t(3, 5), points: 75),
                ScoreEntry(rawValue: t(3, 14), points: 70), ScoreEntry(rawValue: t(3, 24), points: 66), ScoreEntry(rawValue: t(3, 35), points: 62),
                ScoreEntry(rawValue: t(3, 42), points: 60), ScoreEntry(rawValue: t(3, 52), points: 50), ScoreEntry(rawValue: t(4, 2), points: 40),
                ScoreEntry(rawValue: t(4, 12), points: 30), ScoreEntry(rawValue: t(4, 22), points: 20), ScoreEntry(rawValue: t(4, 32), points: 10),
                ScoreEntry(rawValue: t(4, 42), points: 0)
            ]
        case .age47_51:
            return [
                ScoreEntry(rawValue: t(2, 11), points: 100), ScoreEntry(rawValue: t(2, 22), points: 98), ScoreEntry(rawValue: t(2, 30), points: 96),
                ScoreEntry(rawValue: t(2, 37), points: 94), ScoreEntry(rawValue: t(2, 44), points: 92), ScoreEntry(rawValue: t(2, 52), points: 88),
                ScoreEntry(rawValue: t(3, 0), points: 84), ScoreEntry(rawValue: t(3, 10), points: 80), ScoreEntry(rawValue: t(3, 21), points: 75),
                ScoreEntry(rawValue: t(3, 32), points: 70), ScoreEntry(rawValue: t(3, 42), points: 66), ScoreEntry(rawValue: t(3, 51), points: 60),
                ScoreEntry(rawValue: t(4, 1), points: 50), ScoreEntry(rawValue: t(4, 11), points: 40), ScoreEntry(rawValue: t(4, 21), points: 30),
                ScoreEntry(rawValue: t(4, 31), points: 20), ScoreEntry(rawValue: t(4, 41), points: 10), ScoreEntry(rawValue: t(4, 51), points: 0)
            ]
        case .age52_56:
            return [
                ScoreEntry(rawValue: t(2, 18), points: 100), ScoreEntry(rawValue: t(2, 28), points: 98), ScoreEntry(rawValue: t(2, 38), points: 96),
                ScoreEntry(rawValue: t(2, 48), points: 94), ScoreEntry(rawValue: t(2, 58), points: 92), ScoreEntry(rawValue: t(3, 10), points: 88),
                ScoreEntry(rawValue: t(3, 22), points: 84), ScoreEntry(rawValue: t(3, 35), points: 80), ScoreEntry(rawValue: t(3, 48), points: 75),
                ScoreEntry(rawValue: t(4, 3), points: 60), ScoreEntry(rawValue: t(4, 13), points: 50), ScoreEntry(rawValue: t(4, 23), points: 40),
                ScoreEntry(rawValue: t(4, 33), points: 30), ScoreEntry(rawValue: t(4, 43), points: 20), ScoreEntry(rawValue: t(4, 53), points: 10),
                ScoreEntry(rawValue: t(5, 3), points: 0)
            ]
        case .age57_61:
            return [
                ScoreEntry(rawValue: t(2, 26), points: 100), ScoreEntry(rawValue: t(2, 34), points: 98), ScoreEntry(rawValue: t(2, 45), points: 96),
                ScoreEntry(rawValue: t(2, 55), points: 94), ScoreEntry(rawValue: t(3, 7), points: 92), ScoreEntry(rawValue: t(3, 21), points: 88),
                ScoreEntry(rawValue: t(3, 36), points: 84), ScoreEntry(rawValue: t(3, 54), points: 80), ScoreEntry(rawValue: t(4, 16), points: 70),
                ScoreEntry(rawValue: t(4, 48), points: 60), ScoreEntry(rawValue: t(4, 58), points: 50), ScoreEntry(rawValue: t(5, 8), points: 40),
                ScoreEntry(rawValue: t(5, 18), points: 30), ScoreEntry(rawValue: t(5, 28), points: 20), ScoreEntry(rawValue: t(5, 38), points: 10),
                ScoreEntry(rawValue: t(5, 48), points: 0)
            ]
        case .age62Plus:
            return [
                ScoreEntry(rawValue: t(2, 26), points: 100), ScoreEntry(rawValue: t(2, 34), points: 98), ScoreEntry(rawValue: t(2, 45), points: 96),
                ScoreEntry(rawValue: t(2, 55), points: 94), ScoreEntry(rawValue: t(3, 7), points: 92), ScoreEntry(rawValue: t(3, 21), points: 88),
                ScoreEntry(rawValue: t(3, 36), points: 84), ScoreEntry(rawValue: t(3, 54), points: 80), ScoreEntry(rawValue: t(4, 16), points: 70),
                ScoreEntry(rawValue: t(4, 48), points: 60), ScoreEntry(rawValue: t(4, 58), points: 50), ScoreEntry(rawValue: t(5, 8), points: 40),
                ScoreEntry(rawValue: t(5, 18), points: 30), ScoreEntry(rawValue: t(5, 28), points: 20), ScoreEntry(rawValue: t(5, 38), points: 10),
                ScoreEntry(rawValue: t(5, 48), points: 0)
            ]
        }
    }

    private func getPlankTable(ageBracket: AgeBracket, female: Bool) -> [ScoreEntry] {
        return getPlankTableByAge(ageBracket: ageBracket)
    }

    private func getPlankTableByAge(ageBracket: AgeBracket) -> [ScoreEntry] {
        switch ageBracket {
        case .age17_21:
            return [
                ScoreEntry(rawValue: t(3, 40), points: 100), ScoreEntry(rawValue: t(3, 37), points: 99), ScoreEntry(rawValue: t(3, 34), points: 98),
                ScoreEntry(rawValue: t(3, 30), points: 97), ScoreEntry(rawValue: t(3, 27), points: 96), ScoreEntry(rawValue: t(3, 24), points: 95),
                ScoreEntry(rawValue: t(3, 21), points: 94), ScoreEntry(rawValue: t(3, 17), points: 93), ScoreEntry(rawValue: t(3, 14), points: 92),
                ScoreEntry(rawValue: t(3, 11), points: 91), ScoreEntry(rawValue: t(3, 8), points: 90), ScoreEntry(rawValue: t(3, 4), points: 89),
                ScoreEntry(rawValue: t(3, 1), points: 88), ScoreEntry(rawValue: t(2, 58), points: 87), ScoreEntry(rawValue: t(2, 55), points: 86),
                ScoreEntry(rawValue: t(2, 51), points: 85), ScoreEntry(rawValue: t(2, 48), points: 84), ScoreEntry(rawValue: t(2, 45), points: 83),
                ScoreEntry(rawValue: t(2, 41), points: 82), ScoreEntry(rawValue: t(2, 38), points: 81), ScoreEntry(rawValue: t(2, 35), points: 80),
                ScoreEntry(rawValue: t(2, 32), points: 79), ScoreEntry(rawValue: t(2, 29), points: 78), ScoreEntry(rawValue: t(2, 25), points: 77),
                ScoreEntry(rawValue: t(2, 22), points: 76), ScoreEntry(rawValue: t(2, 19), points: 75), ScoreEntry(rawValue: t(2, 15), points: 74),
                ScoreEntry(rawValue: t(2, 12), points: 73), ScoreEntry(rawValue: t(2, 9), points: 72), ScoreEntry(rawValue: t(2, 6), points: 71),
                ScoreEntry(rawValue: t(2, 2), points: 70), ScoreEntry(rawValue: t(1, 59), points: 69), ScoreEntry(rawValue: t(1, 56), points: 68),
                ScoreEntry(rawValue: t(1, 53), points: 67), ScoreEntry(rawValue: t(1, 49), points: 66), ScoreEntry(rawValue: t(1, 46), points: 65),
                ScoreEntry(rawValue: t(1, 43), points: 64), ScoreEntry(rawValue: t(1, 40), points: 63), ScoreEntry(rawValue: t(1, 37), points: 62),
                ScoreEntry(rawValue: t(1, 33), points: 61), ScoreEntry(rawValue: t(1, 30), points: 60), ScoreEntry(rawValue: t(1, 25), points: 50),
                ScoreEntry(rawValue: t(1, 20), points: 40), ScoreEntry(rawValue: t(1, 15), points: 30), ScoreEntry(rawValue: t(1, 10), points: 20),
                ScoreEntry(rawValue: t(1, 5), points: 10), ScoreEntry(rawValue: t(1, 0), points: 0)
            ]
        case .age22_26:
            return [
                ScoreEntry(rawValue: t(3, 35), points: 100), ScoreEntry(rawValue: t(3, 32), points: 99), ScoreEntry(rawValue: t(3, 29), points: 98),
                ScoreEntry(rawValue: t(3, 25), points: 97), ScoreEntry(rawValue: t(3, 22), points: 96), ScoreEntry(rawValue: t(3, 19), points: 95),
                ScoreEntry(rawValue: t(3, 16), points: 94), ScoreEntry(rawValue: t(3, 12), points: 93), ScoreEntry(rawValue: t(3, 9), points: 92),
                ScoreEntry(rawValue: t(3, 6), points: 91), ScoreEntry(rawValue: t(3, 3), points: 90), ScoreEntry(rawValue: t(2, 59), points: 89),
                ScoreEntry(rawValue: t(2, 56), points: 88), ScoreEntry(rawValue: t(2, 53), points: 87), ScoreEntry(rawValue: t(2, 50), points: 86),
                ScoreEntry(rawValue: t(2, 46), points: 85), ScoreEntry(rawValue: t(2, 43), points: 84), ScoreEntry(rawValue: t(2, 40), points: 83),
                ScoreEntry(rawValue: t(2, 37), points: 82), ScoreEntry(rawValue: t(2, 33), points: 81), ScoreEntry(rawValue: t(2, 30), points: 80),
                ScoreEntry(rawValue: t(2, 27), points: 79), ScoreEntry(rawValue: t(2, 23), points: 78), ScoreEntry(rawValue: t(2, 20), points: 77),
                ScoreEntry(rawValue: t(2, 17), points: 76), ScoreEntry(rawValue: t(2, 14), points: 75), ScoreEntry(rawValue: t(2, 10), points: 74),
                ScoreEntry(rawValue: t(2, 7), points: 73), ScoreEntry(rawValue: t(2, 4), points: 72), ScoreEntry(rawValue: t(2, 1), points: 71),
                ScoreEntry(rawValue: t(1, 58), points: 70), ScoreEntry(rawValue: t(1, 54), points: 69), ScoreEntry(rawValue: t(1, 51), points: 68),
                ScoreEntry(rawValue: t(1, 48), points: 67), ScoreEntry(rawValue: t(1, 45), points: 66), ScoreEntry(rawValue: t(1, 41), points: 65),
                ScoreEntry(rawValue: t(1, 38), points: 64), ScoreEntry(rawValue: t(1, 35), points: 63), ScoreEntry(rawValue: t(1, 32), points: 62),
                ScoreEntry(rawValue: t(1, 28), points: 61), ScoreEntry(rawValue: t(1, 25), points: 60), ScoreEntry(rawValue: t(1, 20), points: 50),
                ScoreEntry(rawValue: t(1, 15), points: 40), ScoreEntry(rawValue: t(1, 10), points: 30), ScoreEntry(rawValue: t(1, 5), points: 20),
                ScoreEntry(rawValue: t(1, 0), points: 10), ScoreEntry(rawValue: t(0, 55), points: 0)
            ]
        case .age27_31:
            return [
                ScoreEntry(rawValue: t(3, 30), points: 100), ScoreEntry(rawValue: t(3, 27), points: 99), ScoreEntry(rawValue: t(3, 24), points: 98),
                ScoreEntry(rawValue: t(3, 20), points: 97), ScoreEntry(rawValue: t(3, 17), points: 96), ScoreEntry(rawValue: t(3, 14), points: 95),
                ScoreEntry(rawValue: t(3, 11), points: 94), ScoreEntry(rawValue: t(3, 7), points: 93), ScoreEntry(rawValue: t(3, 4), points: 92),
                ScoreEntry(rawValue: t(3, 1), points: 91), ScoreEntry(rawValue: t(2, 58), points: 90), ScoreEntry(rawValue: t(2, 54), points: 89),
                ScoreEntry(rawValue: t(2, 51), points: 88), ScoreEntry(rawValue: t(2, 48), points: 87), ScoreEntry(rawValue: t(2, 45), points: 86),
                ScoreEntry(rawValue: t(2, 41), points: 85), ScoreEntry(rawValue: t(2, 38), points: 84), ScoreEntry(rawValue: t(2, 35), points: 83),
                ScoreEntry(rawValue: t(2, 31), points: 82), ScoreEntry(rawValue: t(2, 28), points: 81), ScoreEntry(rawValue: t(2, 25), points: 80),
                ScoreEntry(rawValue: t(2, 22), points: 79), ScoreEntry(rawValue: t(2, 18), points: 78), ScoreEntry(rawValue: t(2, 15), points: 77),
                ScoreEntry(rawValue: t(2, 12), points: 76), ScoreEntry(rawValue: t(2, 9), points: 75), ScoreEntry(rawValue: t(2, 6), points: 74),
                ScoreEntry(rawValue: t(2, 2), points: 73), ScoreEntry(rawValue: t(1, 59), points: 72), ScoreEntry(rawValue: t(1, 56), points: 71),
                ScoreEntry(rawValue: t(1, 52), points: 70), ScoreEntry(rawValue: t(1, 49), points: 69), ScoreEntry(rawValue: t(1, 46), points: 68),
                ScoreEntry(rawValue: t(1, 43), points: 67), ScoreEntry(rawValue: t(1, 39), points: 66), ScoreEntry(rawValue: t(1, 36), points: 65),
                ScoreEntry(rawValue: t(1, 33), points: 64), ScoreEntry(rawValue: t(1, 30), points: 63), ScoreEntry(rawValue: t(1, 26), points: 62),
                ScoreEntry(rawValue: t(1, 23), points: 61), ScoreEntry(rawValue: t(1, 20), points: 60), ScoreEntry(rawValue: t(1, 15), points: 50),
                ScoreEntry(rawValue: t(1, 10), points: 40), ScoreEntry(rawValue: t(1, 5), points: 30), ScoreEntry(rawValue: t(1, 0), points: 20),
                ScoreEntry(rawValue: t(0, 55), points: 10), ScoreEntry(rawValue: t(0, 50), points: 0)
            ]
        case .age32_36:
            return [
                ScoreEntry(rawValue: t(3, 25), points: 100), ScoreEntry(rawValue: t(3, 22), points: 99), ScoreEntry(rawValue: t(3, 19), points: 98),
                ScoreEntry(rawValue: t(3, 15), points: 97), ScoreEntry(rawValue: t(3, 12), points: 96), ScoreEntry(rawValue: t(3, 9), points: 95),
                ScoreEntry(rawValue: t(3, 6), points: 94), ScoreEntry(rawValue: t(3, 2), points: 93), ScoreEntry(rawValue: t(2, 59), points: 92),
                ScoreEntry(rawValue: t(2, 56), points: 91), ScoreEntry(rawValue: t(2, 53), points: 90), ScoreEntry(rawValue: t(2, 49), points: 89),
                ScoreEntry(rawValue: t(2, 46), points: 88), ScoreEntry(rawValue: t(2, 43), points: 87), ScoreEntry(rawValue: t(2, 40), points: 86),
                ScoreEntry(rawValue: t(2, 36), points: 85), ScoreEntry(rawValue: t(2, 33), points: 84), ScoreEntry(rawValue: t(2, 30), points: 83),
                ScoreEntry(rawValue: t(2, 27), points: 82), ScoreEntry(rawValue: t(2, 23), points: 81), ScoreEntry(rawValue: t(2, 20), points: 80),
                ScoreEntry(rawValue: t(2, 17), points: 79), ScoreEntry(rawValue: t(2, 13), points: 78), ScoreEntry(rawValue: t(2, 10), points: 77),
                ScoreEntry(rawValue: t(2, 7), points: 76), ScoreEntry(rawValue: t(2, 4), points: 75), ScoreEntry(rawValue: t(2, 0), points: 74),
                ScoreEntry(rawValue: t(1, 57), points: 73), ScoreEntry(rawValue: t(1, 54), points: 72), ScoreEntry(rawValue: t(1, 51), points: 71),
                ScoreEntry(rawValue: t(1, 47), points: 70), ScoreEntry(rawValue: t(1, 44), points: 69), ScoreEntry(rawValue: t(1, 41), points: 68),
                ScoreEntry(rawValue: t(1, 38), points: 67), ScoreEntry(rawValue: t(1, 35), points: 66), ScoreEntry(rawValue: t(1, 31), points: 65),
                ScoreEntry(rawValue: t(1, 28), points: 64), ScoreEntry(rawValue: t(1, 25), points: 63), ScoreEntry(rawValue: t(1, 22), points: 62),
                ScoreEntry(rawValue: t(1, 18), points: 61), ScoreEntry(rawValue: t(1, 15), points: 60), ScoreEntry(rawValue: t(1, 10), points: 50),
                ScoreEntry(rawValue: t(1, 5), points: 40), ScoreEntry(rawValue: t(1, 0), points: 30), ScoreEntry(rawValue: t(0, 55), points: 20),
                ScoreEntry(rawValue: t(0, 50), points: 10), ScoreEntry(rawValue: t(0, 45), points: 0)
            ]
        case .age37_41:
            return [
                ScoreEntry(rawValue: t(3, 20), points: 100), ScoreEntry(rawValue: t(3, 17), points: 99), ScoreEntry(rawValue: t(3, 14), points: 98),
                ScoreEntry(rawValue: t(3, 10), points: 97), ScoreEntry(rawValue: t(3, 7), points: 96), ScoreEntry(rawValue: t(3, 4), points: 95),
                ScoreEntry(rawValue: t(3, 1), points: 94), ScoreEntry(rawValue: t(2, 57), points: 93), ScoreEntry(rawValue: t(2, 54), points: 92),
                ScoreEntry(rawValue: t(2, 51), points: 91), ScoreEntry(rawValue: t(2, 47), points: 90), ScoreEntry(rawValue: t(2, 44), points: 89),
                ScoreEntry(rawValue: t(2, 41), points: 88), ScoreEntry(rawValue: t(2, 38), points: 87), ScoreEntry(rawValue: t(2, 35), points: 86),
                ScoreEntry(rawValue: t(2, 31), points: 85), ScoreEntry(rawValue: t(2, 28), points: 84), ScoreEntry(rawValue: t(2, 25), points: 83),
                ScoreEntry(rawValue: t(2, 22), points: 82), ScoreEntry(rawValue: t(2, 18), points: 81), ScoreEntry(rawValue: t(2, 15), points: 80),
                ScoreEntry(rawValue: t(2, 12), points: 79), ScoreEntry(rawValue: t(2, 8), points: 78), ScoreEntry(rawValue: t(2, 5), points: 77),
                ScoreEntry(rawValue: t(2, 2), points: 76), ScoreEntry(rawValue: t(1, 59), points: 75), ScoreEntry(rawValue: t(1, 56), points: 74),
                ScoreEntry(rawValue: t(1, 52), points: 73), ScoreEntry(rawValue: t(1, 49), points: 72), ScoreEntry(rawValue: t(1, 46), points: 71),
                ScoreEntry(rawValue: t(1, 42), points: 70), ScoreEntry(rawValue: t(1, 39), points: 69), ScoreEntry(rawValue: t(1, 36), points: 68),
                ScoreEntry(rawValue: t(1, 33), points: 67), ScoreEntry(rawValue: t(1, 30), points: 66), ScoreEntry(rawValue: t(1, 26), points: 65),
                ScoreEntry(rawValue: t(1, 23), points: 64), ScoreEntry(rawValue: t(1, 20), points: 63), ScoreEntry(rawValue: t(1, 16), points: 62),
                ScoreEntry(rawValue: t(1, 13), points: 61), ScoreEntry(rawValue: t(1, 10), points: 60), ScoreEntry(rawValue: t(1, 5), points: 50),
                ScoreEntry(rawValue: t(1, 0), points: 40), ScoreEntry(rawValue: t(0, 55), points: 30), ScoreEntry(rawValue: t(0, 50), points: 20),
                ScoreEntry(rawValue: t(0, 45), points: 10), ScoreEntry(rawValue: t(0, 40), points: 0)
            ]
        case .age42_46:
            return [
                ScoreEntry(rawValue: t(3, 20), points: 100), ScoreEntry(rawValue: t(3, 17), points: 99), ScoreEntry(rawValue: t(3, 14), points: 98),
                ScoreEntry(rawValue: t(3, 10), points: 97), ScoreEntry(rawValue: t(3, 7), points: 96), ScoreEntry(rawValue: t(3, 4), points: 95),
                ScoreEntry(rawValue: t(3, 1), points: 94), ScoreEntry(rawValue: t(2, 57), points: 93), ScoreEntry(rawValue: t(2, 54), points: 92),
                ScoreEntry(rawValue: t(2, 51), points: 91), ScoreEntry(rawValue: t(2, 47), points: 90), ScoreEntry(rawValue: t(2, 44), points: 89),
                ScoreEntry(rawValue: t(2, 41), points: 88), ScoreEntry(rawValue: t(2, 38), points: 87), ScoreEntry(rawValue: t(2, 35), points: 86),
                ScoreEntry(rawValue: t(2, 31), points: 85), ScoreEntry(rawValue: t(2, 28), points: 84), ScoreEntry(rawValue: t(2, 25), points: 83),
                ScoreEntry(rawValue: t(2, 22), points: 82), ScoreEntry(rawValue: t(2, 18), points: 81), ScoreEntry(rawValue: t(2, 15), points: 80),
                ScoreEntry(rawValue: t(2, 12), points: 79), ScoreEntry(rawValue: t(2, 8), points: 78), ScoreEntry(rawValue: t(2, 5), points: 77),
                ScoreEntry(rawValue: t(2, 2), points: 76), ScoreEntry(rawValue: t(1, 59), points: 75), ScoreEntry(rawValue: t(1, 56), points: 74),
                ScoreEntry(rawValue: t(1, 52), points: 73), ScoreEntry(rawValue: t(1, 49), points: 72), ScoreEntry(rawValue: t(1, 46), points: 71),
                ScoreEntry(rawValue: t(1, 42), points: 70), ScoreEntry(rawValue: t(1, 39), points: 69), ScoreEntry(rawValue: t(1, 36), points: 68),
                ScoreEntry(rawValue: t(1, 33), points: 67), ScoreEntry(rawValue: t(1, 30), points: 66), ScoreEntry(rawValue: t(1, 26), points: 65),
                ScoreEntry(rawValue: t(1, 23), points: 64), ScoreEntry(rawValue: t(1, 20), points: 63), ScoreEntry(rawValue: t(1, 16), points: 62),
                ScoreEntry(rawValue: t(1, 13), points: 61), ScoreEntry(rawValue: t(1, 10), points: 60), ScoreEntry(rawValue: t(1, 5), points: 50),
                ScoreEntry(rawValue: t(1, 0), points: 40), ScoreEntry(rawValue: t(0, 55), points: 30), ScoreEntry(rawValue: t(0, 50), points: 20),
                ScoreEntry(rawValue: t(0, 45), points: 10), ScoreEntry(rawValue: t(0, 40), points: 0)
            ]
        case .age47_51:
            return [
                ScoreEntry(rawValue: t(3, 20), points: 100), ScoreEntry(rawValue: t(3, 17), points: 99), ScoreEntry(rawValue: t(3, 14), points: 98),
                ScoreEntry(rawValue: t(3, 10), points: 97), ScoreEntry(rawValue: t(3, 7), points: 96), ScoreEntry(rawValue: t(3, 4), points: 95),
                ScoreEntry(rawValue: t(3, 1), points: 94), ScoreEntry(rawValue: t(2, 57), points: 93), ScoreEntry(rawValue: t(2, 54), points: 92),
                ScoreEntry(rawValue: t(2, 51), points: 91), ScoreEntry(rawValue: t(2, 47), points: 90), ScoreEntry(rawValue: t(2, 44), points: 89),
                ScoreEntry(rawValue: t(2, 41), points: 88), ScoreEntry(rawValue: t(2, 38), points: 87), ScoreEntry(rawValue: t(2, 35), points: 86),
                ScoreEntry(rawValue: t(2, 31), points: 85), ScoreEntry(rawValue: t(2, 28), points: 84), ScoreEntry(rawValue: t(2, 25), points: 83),
                ScoreEntry(rawValue: t(2, 22), points: 82), ScoreEntry(rawValue: t(2, 18), points: 81), ScoreEntry(rawValue: t(2, 15), points: 80),
                ScoreEntry(rawValue: t(2, 12), points: 79), ScoreEntry(rawValue: t(2, 8), points: 78), ScoreEntry(rawValue: t(2, 5), points: 77),
                ScoreEntry(rawValue: t(2, 2), points: 76), ScoreEntry(rawValue: t(1, 59), points: 75), ScoreEntry(rawValue: t(1, 56), points: 74),
                ScoreEntry(rawValue: t(1, 52), points: 73), ScoreEntry(rawValue: t(1, 49), points: 72), ScoreEntry(rawValue: t(1, 46), points: 71),
                ScoreEntry(rawValue: t(1, 42), points: 70), ScoreEntry(rawValue: t(1, 39), points: 69), ScoreEntry(rawValue: t(1, 36), points: 68),
                ScoreEntry(rawValue: t(1, 33), points: 67), ScoreEntry(rawValue: t(1, 30), points: 66), ScoreEntry(rawValue: t(1, 26), points: 65),
                ScoreEntry(rawValue: t(1, 23), points: 64), ScoreEntry(rawValue: t(1, 20), points: 63), ScoreEntry(rawValue: t(1, 16), points: 62),
                ScoreEntry(rawValue: t(1, 13), points: 61), ScoreEntry(rawValue: t(1, 10), points: 60), ScoreEntry(rawValue: t(1, 5), points: 50),
                ScoreEntry(rawValue: t(1, 0), points: 40), ScoreEntry(rawValue: t(0, 55), points: 30), ScoreEntry(rawValue: t(0, 50), points: 20),
                ScoreEntry(rawValue: t(0, 45), points: 10), ScoreEntry(rawValue: t(0, 40), points: 0)
            ]
        case .age52_56:
            return [
                ScoreEntry(rawValue: t(3, 20), points: 100), ScoreEntry(rawValue: t(3, 17), points: 99), ScoreEntry(rawValue: t(3, 14), points: 98),
                ScoreEntry(rawValue: t(3, 10), points: 97), ScoreEntry(rawValue: t(3, 7), points: 96), ScoreEntry(rawValue: t(3, 4), points: 95),
                ScoreEntry(rawValue: t(3, 1), points: 94), ScoreEntry(rawValue: t(2, 57), points: 93), ScoreEntry(rawValue: t(2, 54), points: 92),
                ScoreEntry(rawValue: t(2, 51), points: 91), ScoreEntry(rawValue: t(2, 47), points: 90), ScoreEntry(rawValue: t(2, 44), points: 89),
                ScoreEntry(rawValue: t(2, 41), points: 88), ScoreEntry(rawValue: t(2, 38), points: 87), ScoreEntry(rawValue: t(2, 35), points: 86),
                ScoreEntry(rawValue: t(2, 31), points: 85), ScoreEntry(rawValue: t(2, 28), points: 84), ScoreEntry(rawValue: t(2, 25), points: 83),
                ScoreEntry(rawValue: t(2, 22), points: 82), ScoreEntry(rawValue: t(2, 18), points: 81), ScoreEntry(rawValue: t(2, 15), points: 80),
                ScoreEntry(rawValue: t(2, 12), points: 79), ScoreEntry(rawValue: t(2, 8), points: 78), ScoreEntry(rawValue: t(2, 5), points: 77),
                ScoreEntry(rawValue: t(2, 2), points: 76), ScoreEntry(rawValue: t(1, 59), points: 75), ScoreEntry(rawValue: t(1, 56), points: 74),
                ScoreEntry(rawValue: t(1, 52), points: 73), ScoreEntry(rawValue: t(1, 49), points: 72), ScoreEntry(rawValue: t(1, 46), points: 71),
                ScoreEntry(rawValue: t(1, 42), points: 70), ScoreEntry(rawValue: t(1, 39), points: 69), ScoreEntry(rawValue: t(1, 36), points: 68),
                ScoreEntry(rawValue: t(1, 33), points: 67), ScoreEntry(rawValue: t(1, 30), points: 66), ScoreEntry(rawValue: t(1, 26), points: 65),
                ScoreEntry(rawValue: t(1, 23), points: 64), ScoreEntry(rawValue: t(1, 20), points: 63), ScoreEntry(rawValue: t(1, 16), points: 62),
                ScoreEntry(rawValue: t(1, 13), points: 61), ScoreEntry(rawValue: t(1, 10), points: 60), ScoreEntry(rawValue: t(1, 5), points: 50),
                ScoreEntry(rawValue: t(1, 0), points: 40), ScoreEntry(rawValue: t(0, 55), points: 30), ScoreEntry(rawValue: t(0, 50), points: 20),
                ScoreEntry(rawValue: t(0, 45), points: 10), ScoreEntry(rawValue: t(0, 40), points: 0)
            ]
        case .age57_61:
            return [
                ScoreEntry(rawValue: t(3, 20), points: 100), ScoreEntry(rawValue: t(3, 17), points: 99), ScoreEntry(rawValue: t(3, 14), points: 98),
                ScoreEntry(rawValue: t(3, 10), points: 97), ScoreEntry(rawValue: t(3, 7), points: 96), ScoreEntry(rawValue: t(3, 4), points: 95),
                ScoreEntry(rawValue: t(3, 1), points: 94), ScoreEntry(rawValue: t(2, 57), points: 93), ScoreEntry(rawValue: t(2, 54), points: 92),
                ScoreEntry(rawValue: t(2, 51), points: 91), ScoreEntry(rawValue: t(2, 47), points: 90), ScoreEntry(rawValue: t(2, 44), points: 89),
                ScoreEntry(rawValue: t(2, 41), points: 88), ScoreEntry(rawValue: t(2, 38), points: 87), ScoreEntry(rawValue: t(2, 35), points: 86),
                ScoreEntry(rawValue: t(2, 31), points: 85), ScoreEntry(rawValue: t(2, 28), points: 84), ScoreEntry(rawValue: t(2, 25), points: 83),
                ScoreEntry(rawValue: t(2, 22), points: 82), ScoreEntry(rawValue: t(2, 18), points: 81), ScoreEntry(rawValue: t(2, 15), points: 80),
                ScoreEntry(rawValue: t(2, 12), points: 79), ScoreEntry(rawValue: t(2, 8), points: 78), ScoreEntry(rawValue: t(2, 5), points: 77),
                ScoreEntry(rawValue: t(2, 2), points: 76), ScoreEntry(rawValue: t(1, 59), points: 75), ScoreEntry(rawValue: t(1, 56), points: 74),
                ScoreEntry(rawValue: t(1, 52), points: 73), ScoreEntry(rawValue: t(1, 49), points: 72), ScoreEntry(rawValue: t(1, 46), points: 71),
                ScoreEntry(rawValue: t(1, 42), points: 70), ScoreEntry(rawValue: t(1, 39), points: 69), ScoreEntry(rawValue: t(1, 36), points: 68),
                ScoreEntry(rawValue: t(1, 33), points: 67), ScoreEntry(rawValue: t(1, 30), points: 66), ScoreEntry(rawValue: t(1, 26), points: 65),
                ScoreEntry(rawValue: t(1, 23), points: 64), ScoreEntry(rawValue: t(1, 20), points: 63), ScoreEntry(rawValue: t(1, 16), points: 62),
                ScoreEntry(rawValue: t(1, 13), points: 61), ScoreEntry(rawValue: t(1, 10), points: 60), ScoreEntry(rawValue: t(1, 5), points: 50),
                ScoreEntry(rawValue: t(1, 0), points: 40), ScoreEntry(rawValue: t(0, 55), points: 30), ScoreEntry(rawValue: t(0, 50), points: 20),
                ScoreEntry(rawValue: t(0, 45), points: 10), ScoreEntry(rawValue: t(0, 40), points: 0)
            ]
        case .age62Plus:
            return [
                ScoreEntry(rawValue: t(3, 20), points: 100), ScoreEntry(rawValue: t(3, 17), points: 99), ScoreEntry(rawValue: t(3, 14), points: 98),
                ScoreEntry(rawValue: t(3, 10), points: 97), ScoreEntry(rawValue: t(3, 7), points: 96), ScoreEntry(rawValue: t(3, 4), points: 95),
                ScoreEntry(rawValue: t(3, 1), points: 94), ScoreEntry(rawValue: t(2, 57), points: 93), ScoreEntry(rawValue: t(2, 54), points: 92),
                ScoreEntry(rawValue: t(2, 51), points: 91), ScoreEntry(rawValue: t(2, 47), points: 90), ScoreEntry(rawValue: t(2, 44), points: 89),
                ScoreEntry(rawValue: t(2, 41), points: 88), ScoreEntry(rawValue: t(2, 38), points: 87), ScoreEntry(rawValue: t(2, 35), points: 86),
                ScoreEntry(rawValue: t(2, 31), points: 85), ScoreEntry(rawValue: t(2, 28), points: 84), ScoreEntry(rawValue: t(2, 25), points: 83),
                ScoreEntry(rawValue: t(2, 22), points: 82), ScoreEntry(rawValue: t(2, 18), points: 81), ScoreEntry(rawValue: t(2, 15), points: 80),
                ScoreEntry(rawValue: t(2, 12), points: 79), ScoreEntry(rawValue: t(2, 8), points: 78), ScoreEntry(rawValue: t(2, 5), points: 77),
                ScoreEntry(rawValue: t(2, 2), points: 76), ScoreEntry(rawValue: t(1, 59), points: 75), ScoreEntry(rawValue: t(1, 56), points: 74),
                ScoreEntry(rawValue: t(1, 52), points: 73), ScoreEntry(rawValue: t(1, 49), points: 72), ScoreEntry(rawValue: t(1, 46), points: 71),
                ScoreEntry(rawValue: t(1, 42), points: 70), ScoreEntry(rawValue: t(1, 39), points: 69), ScoreEntry(rawValue: t(1, 36), points: 68),
                ScoreEntry(rawValue: t(1, 33), points: 67), ScoreEntry(rawValue: t(1, 30), points: 66), ScoreEntry(rawValue: t(1, 26), points: 65),
                ScoreEntry(rawValue: t(1, 23), points: 64), ScoreEntry(rawValue: t(1, 20), points: 63), ScoreEntry(rawValue: t(1, 16), points: 62),
                ScoreEntry(rawValue: t(1, 13), points: 61), ScoreEntry(rawValue: t(1, 10), points: 60), ScoreEntry(rawValue: t(1, 5), points: 50),
                ScoreEntry(rawValue: t(1, 0), points: 40), ScoreEntry(rawValue: t(0, 55), points: 30), ScoreEntry(rawValue: t(0, 50), points: 20),
                ScoreEntry(rawValue: t(0, 45), points: 10), ScoreEntry(rawValue: t(0, 40), points: 0)
            ]
        }
    }

    private func getTwoMileRunTable(ageBracket: AgeBracket, female: Bool) -> [ScoreEntry] {
        if female { return getTwoMileRunTableFemale(ageBracket: ageBracket) }
        return getTwoMileRunTableMale(ageBracket: ageBracket)
    }

    private func getTwoMileRunTableMale(ageBracket: AgeBracket) -> [ScoreEntry] {
        switch ageBracket {
        case .age17_21:
            return [
                ScoreEntry(rawValue: t(13, 22), points: 100), ScoreEntry(rawValue: t(13, 47), points: 99), ScoreEntry(rawValue: t(14, 4), points: 98),
                ScoreEntry(rawValue: t(14, 19), points: 97), ScoreEntry(rawValue: t(14, 32), points: 96), ScoreEntry(rawValue: t(14, 45), points: 95),
                ScoreEntry(rawValue: t(14, 56), points: 94), ScoreEntry(rawValue: t(15, 7), points: 93), ScoreEntry(rawValue: t(15, 18), points: 92),
                ScoreEntry(rawValue: t(15, 29), points: 91), ScoreEntry(rawValue: t(15, 39), points: 90), ScoreEntry(rawValue: t(15, 49), points: 89),
                ScoreEntry(rawValue: t(15, 59), points: 88), ScoreEntry(rawValue: t(16, 9), points: 87), ScoreEntry(rawValue: t(16, 19), points: 86),
                ScoreEntry(rawValue: t(16, 28), points: 85), ScoreEntry(rawValue: t(16, 38), points: 84), ScoreEntry(rawValue: t(16, 48), points: 83),
                ScoreEntry(rawValue: t(16, 57), points: 82), ScoreEntry(rawValue: t(17, 7), points: 81), ScoreEntry(rawValue: t(17, 13), points: 80),
                ScoreEntry(rawValue: t(17, 17), points: 79), ScoreEntry(rawValue: t(17, 25), points: 78), ScoreEntry(rawValue: t(17, 34), points: 77),
                ScoreEntry(rawValue: t(17, 43), points: 76), ScoreEntry(rawValue: t(17, 52), points: 75), ScoreEntry(rawValue: t(18, 0), points: 74),
                ScoreEntry(rawValue: t(18, 9), points: 73), ScoreEntry(rawValue: t(18, 18), points: 72), ScoreEntry(rawValue: t(18, 27), points: 71),
                ScoreEntry(rawValue: t(18, 35), points: 70), ScoreEntry(rawValue: t(18, 45), points: 69), ScoreEntry(rawValue: t(18, 54), points: 68),
                ScoreEntry(rawValue: t(19, 3), points: 67), ScoreEntry(rawValue: t(19, 13), points: 66), ScoreEntry(rawValue: t(19, 23), points: 65),
                ScoreEntry(rawValue: t(19, 33), points: 64), ScoreEntry(rawValue: t(19, 43), points: 63), ScoreEntry(rawValue: t(19, 54), points: 62),
                ScoreEntry(rawValue: t(19, 54), points: 61), ScoreEntry(rawValue: t(19, 57), points: 60), ScoreEntry(rawValue: t(20, 25), points: 50),
                ScoreEntry(rawValue: t(20, 53), points: 40), ScoreEntry(rawValue: t(21, 21), points: 30), ScoreEntry(rawValue: t(21, 49), points: 20),
                ScoreEntry(rawValue: t(22, 17), points: 10), ScoreEntry(rawValue: t(22, 45), points: 0)
            ]
        case .age22_26:
            return [
                ScoreEntry(rawValue: t(13, 25), points: 100), ScoreEntry(rawValue: t(13, 47), points: 99), ScoreEntry(rawValue: t(13, 55), points: 98),
                ScoreEntry(rawValue: t(14, 12), points: 97), ScoreEntry(rawValue: t(14, 27), points: 96), ScoreEntry(rawValue: t(14, 41), points: 95),
                ScoreEntry(rawValue: t(14, 54), points: 94), ScoreEntry(rawValue: t(15, 5), points: 93), ScoreEntry(rawValue: t(15, 17), points: 92),
                ScoreEntry(rawValue: t(15, 28), points: 91), ScoreEntry(rawValue: t(15, 38), points: 90), ScoreEntry(rawValue: t(15, 49), points: 89),
                ScoreEntry(rawValue: t(15, 59), points: 88), ScoreEntry(rawValue: t(16, 9), points: 87), ScoreEntry(rawValue: t(16, 19), points: 86),
                ScoreEntry(rawValue: t(16, 29), points: 85), ScoreEntry(rawValue: t(16, 39), points: 84), ScoreEntry(rawValue: t(16, 49), points: 83),
                ScoreEntry(rawValue: t(16, 59), points: 82), ScoreEntry(rawValue: t(17, 8), points: 81), ScoreEntry(rawValue: t(17, 18), points: 79),
                ScoreEntry(rawValue: t(17, 28), points: 78), ScoreEntry(rawValue: t(17, 37), points: 77), ScoreEntry(rawValue: t(17, 46), points: 76),
                ScoreEntry(rawValue: t(17, 55), points: 75), ScoreEntry(rawValue: t(18, 3), points: 74), ScoreEntry(rawValue: t(18, 12), points: 73),
                ScoreEntry(rawValue: t(18, 21), points: 72), ScoreEntry(rawValue: t(18, 30), points: 70), ScoreEntry(rawValue: t(18, 39), points: 69),
                ScoreEntry(rawValue: t(18, 48), points: 68), ScoreEntry(rawValue: t(18, 57), points: 67), ScoreEntry(rawValue: t(19, 7), points: 66),
                ScoreEntry(rawValue: t(19, 16), points: 65), ScoreEntry(rawValue: t(19, 26), points: 64), ScoreEntry(rawValue: t(19, 36), points: 63),
                ScoreEntry(rawValue: t(19, 45), points: 60), ScoreEntry(rawValue: t(20, 13), points: 50), ScoreEntry(rawValue: t(20, 41), points: 40),
                ScoreEntry(rawValue: t(21, 9), points: 30), ScoreEntry(rawValue: t(21, 37), points: 20), ScoreEntry(rawValue: t(22, 5), points: 10),
                ScoreEntry(rawValue: t(22, 33), points: 0)
            ]
        case .age27_31:
            return [
                ScoreEntry(rawValue: t(13, 25), points: 100), ScoreEntry(rawValue: t(13, 47), points: 99), ScoreEntry(rawValue: t(13, 55), points: 98),
                ScoreEntry(rawValue: t(14, 12), points: 97), ScoreEntry(rawValue: t(14, 27), points: 96), ScoreEntry(rawValue: t(14, 41), points: 95),
                ScoreEntry(rawValue: t(14, 54), points: 94), ScoreEntry(rawValue: t(15, 5), points: 93), ScoreEntry(rawValue: t(15, 17), points: 92),
                ScoreEntry(rawValue: t(15, 28), points: 91), ScoreEntry(rawValue: t(15, 38), points: 90), ScoreEntry(rawValue: t(15, 55), points: 89),
                ScoreEntry(rawValue: t(16, 5), points: 88), ScoreEntry(rawValue: t(16, 14), points: 87), ScoreEntry(rawValue: t(16, 24), points: 86),
                ScoreEntry(rawValue: t(16, 33), points: 85), ScoreEntry(rawValue: t(16, 43), points: 84), ScoreEntry(rawValue: t(16, 52), points: 83),
                ScoreEntry(rawValue: t(17, 2), points: 82), ScoreEntry(rawValue: t(17, 12), points: 81), ScoreEntry(rawValue: t(17, 21), points: 80),
                ScoreEntry(rawValue: t(17, 30), points: 79), ScoreEntry(rawValue: t(17, 38), points: 78), ScoreEntry(rawValue: t(17, 47), points: 77),
                ScoreEntry(rawValue: t(17, 55), points: 76), ScoreEntry(rawValue: t(18, 4), points: 75), ScoreEntry(rawValue: t(18, 13), points: 74),
                ScoreEntry(rawValue: t(18, 21), points: 73), ScoreEntry(rawValue: t(18, 23), points: 70), ScoreEntry(rawValue: t(18, 30), points: 69),
                ScoreEntry(rawValue: t(18, 39), points: 68), ScoreEntry(rawValue: t(18, 57), points: 67), ScoreEntry(rawValue: t(19, 6), points: 66),
                ScoreEntry(rawValue: t(19, 15), points: 65), ScoreEntry(rawValue: t(19, 25), points: 64), ScoreEntry(rawValue: t(19, 35), points: 63),
                ScoreEntry(rawValue: t(19, 45), points: 60), ScoreEntry(rawValue: t(20, 13), points: 50), ScoreEntry(rawValue: t(20, 41), points: 40),
                ScoreEntry(rawValue: t(21, 9), points: 30), ScoreEntry(rawValue: t(21, 37), points: 20), ScoreEntry(rawValue: t(22, 5), points: 10),
                ScoreEntry(rawValue: t(22, 33), points: 0)
            ]
        case .age32_36:
            return [
                ScoreEntry(rawValue: t(13, 42), points: 100), ScoreEntry(rawValue: t(14, 6), points: 99), ScoreEntry(rawValue: t(14, 23), points: 98),
                ScoreEntry(rawValue: t(14, 37), points: 97), ScoreEntry(rawValue: t(14, 49), points: 96), ScoreEntry(rawValue: t(15, 1), points: 95),
                ScoreEntry(rawValue: t(15, 12), points: 94), ScoreEntry(rawValue: t(15, 23), points: 93), ScoreEntry(rawValue: t(15, 33), points: 92),
                ScoreEntry(rawValue: t(15, 43), points: 91), ScoreEntry(rawValue: t(15, 50), points: 90), ScoreEntry(rawValue: t(15, 53), points: 89),
                ScoreEntry(rawValue: t(16, 2), points: 88), ScoreEntry(rawValue: t(16, 12), points: 87), ScoreEntry(rawValue: t(16, 21), points: 86),
                ScoreEntry(rawValue: t(16, 30), points: 85), ScoreEntry(rawValue: t(16, 40), points: 84), ScoreEntry(rawValue: t(16, 49), points: 83),
                ScoreEntry(rawValue: t(16, 58), points: 82), ScoreEntry(rawValue: t(17, 7), points: 81), ScoreEntry(rawValue: t(17, 16), points: 80),
                ScoreEntry(rawValue: t(17, 26), points: 79), ScoreEntry(rawValue: t(17, 34), points: 78), ScoreEntry(rawValue: t(17, 42), points: 77),
                ScoreEntry(rawValue: t(17, 50), points: 76), ScoreEntry(rawValue: t(17, 58), points: 75), ScoreEntry(rawValue: t(18, 7), points: 74),
                ScoreEntry(rawValue: t(18, 15), points: 73), ScoreEntry(rawValue: t(18, 23), points: 72), ScoreEntry(rawValue: t(18, 30), points: 70),
                ScoreEntry(rawValue: t(18, 58), points: 69), ScoreEntry(rawValue: t(19, 6), points: 68), ScoreEntry(rawValue: t(19, 16), points: 67),
                ScoreEntry(rawValue: t(19, 25), points: 66), ScoreEntry(rawValue: t(19, 34), points: 65), ScoreEntry(rawValue: t(19, 44), points: 64),
                ScoreEntry(rawValue: t(19, 55), points: 63), ScoreEntry(rawValue: t(20, 6), points: 62), ScoreEntry(rawValue: t(20, 18), points: 61),
                ScoreEntry(rawValue: t(20, 44), points: 60), ScoreEntry(rawValue: t(21, 12), points: 50), ScoreEntry(rawValue: t(21, 40), points: 40),
                ScoreEntry(rawValue: t(22, 8), points: 30), ScoreEntry(rawValue: t(22, 36), points: 20), ScoreEntry(rawValue: t(23, 4), points: 10),
                ScoreEntry(rawValue: t(23, 32), points: 0)
            ]
        case .age37_41:
            return [
                ScoreEntry(rawValue: t(13, 42), points: 100), ScoreEntry(rawValue: t(14, 16), points: 99), ScoreEntry(rawValue: t(14, 32), points: 98),
                ScoreEntry(rawValue: t(14, 46), points: 97), ScoreEntry(rawValue: t(14, 59), points: 96), ScoreEntry(rawValue: t(15, 10), points: 95),
                ScoreEntry(rawValue: t(15, 21), points: 94), ScoreEntry(rawValue: t(15, 32), points: 93), ScoreEntry(rawValue: t(15, 42), points: 92),
                ScoreEntry(rawValue: t(15, 52), points: 91), ScoreEntry(rawValue: t(16, 1), points: 90), ScoreEntry(rawValue: t(16, 11), points: 89),
                ScoreEntry(rawValue: t(16, 20), points: 88), ScoreEntry(rawValue: t(16, 29), points: 87), ScoreEntry(rawValue: t(16, 39), points: 86),
                ScoreEntry(rawValue: t(16, 48), points: 85), ScoreEntry(rawValue: t(16, 57), points: 84), ScoreEntry(rawValue: t(17, 6), points: 83),
                ScoreEntry(rawValue: t(17, 15), points: 82), ScoreEntry(rawValue: t(17, 24), points: 81), ScoreEntry(rawValue: t(17, 33), points: 80),
                ScoreEntry(rawValue: t(17, 41), points: 79), ScoreEntry(rawValue: t(17, 50), points: 78), ScoreEntry(rawValue: t(17, 58), points: 77),
                ScoreEntry(rawValue: t(18, 6), points: 76), ScoreEntry(rawValue: t(18, 14), points: 75), ScoreEntry(rawValue: t(18, 22), points: 74),
                ScoreEntry(rawValue: t(18, 31), points: 73), ScoreEntry(rawValue: t(18, 35), points: 70), ScoreEntry(rawValue: t(18, 47), points: 69),
                ScoreEntry(rawValue: t(19, 4), points: 68), ScoreEntry(rawValue: t(19, 13), points: 67), ScoreEntry(rawValue: t(19, 22), points: 66),
                ScoreEntry(rawValue: t(19, 31), points: 65), ScoreEntry(rawValue: t(19, 41), points: 64), ScoreEntry(rawValue: t(19, 51), points: 63),
                ScoreEntry(rawValue: t(20, 12), points: 62), ScoreEntry(rawValue: t(20, 24), points: 61), ScoreEntry(rawValue: t(20, 44), points: 60),
                ScoreEntry(rawValue: t(21, 12), points: 50), ScoreEntry(rawValue: t(21, 40), points: 40), ScoreEntry(rawValue: t(22, 8), points: 30),
                ScoreEntry(rawValue: t(22, 36), points: 20), ScoreEntry(rawValue: t(23, 4), points: 10), ScoreEntry(rawValue: t(23, 32), points: 0)
            ]
        case .age42_46:
            return [
                ScoreEntry(rawValue: t(14, 5), points: 100), ScoreEntry(rawValue: t(14, 29), points: 99), ScoreEntry(rawValue: t(14, 45), points: 98),
                ScoreEntry(rawValue: t(14, 59), points: 97), ScoreEntry(rawValue: t(15, 12), points: 96), ScoreEntry(rawValue: t(15, 24), points: 95),
                ScoreEntry(rawValue: t(15, 35), points: 94), ScoreEntry(rawValue: t(15, 45), points: 93), ScoreEntry(rawValue: t(15, 55), points: 92),
                ScoreEntry(rawValue: t(16, 5), points: 91), ScoreEntry(rawValue: t(16, 15), points: 90), ScoreEntry(rawValue: t(16, 24), points: 89),
                ScoreEntry(rawValue: t(16, 33), points: 88), ScoreEntry(rawValue: t(16, 43), points: 87), ScoreEntry(rawValue: t(16, 52), points: 86),
                ScoreEntry(rawValue: t(17, 1), points: 85), ScoreEntry(rawValue: t(17, 10), points: 84), ScoreEntry(rawValue: t(17, 19), points: 83),
                ScoreEntry(rawValue: t(17, 28), points: 82), ScoreEntry(rawValue: t(17, 37), points: 81), ScoreEntry(rawValue: t(17, 47), points: 80),
                ScoreEntry(rawValue: t(17, 56), points: 79), ScoreEntry(rawValue: t(18, 6), points: 78), ScoreEntry(rawValue: t(18, 15), points: 77),
                ScoreEntry(rawValue: t(18, 25), points: 76), ScoreEntry(rawValue: t(18, 35), points: 75), ScoreEntry(rawValue: t(18, 45), points: 74),
                ScoreEntry(rawValue: t(18, 54), points: 73), ScoreEntry(rawValue: t(18, 55), points: 70), ScoreEntry(rawValue: t(19, 15), points: 69),
                ScoreEntry(rawValue: t(19, 36), points: 68), ScoreEntry(rawValue: t(19, 47), points: 67), ScoreEntry(rawValue: t(19, 58), points: 66),
                ScoreEntry(rawValue: t(20, 10), points: 65), ScoreEntry(rawValue: t(20, 37), points: 64), ScoreEntry(rawValue: t(20, 52), points: 63),
                ScoreEntry(rawValue: t(21, 9), points: 62), ScoreEntry(rawValue: t(21, 31), points: 61), ScoreEntry(rawValue: t(22, 4), points: 60),
                ScoreEntry(rawValue: t(22, 32), points: 50), ScoreEntry(rawValue: t(23, 0), points: 40), ScoreEntry(rawValue: t(23, 28), points: 30),
                ScoreEntry(rawValue: t(23, 56), points: 20), ScoreEntry(rawValue: t(24, 24), points: 10), ScoreEntry(rawValue: t(24, 52), points: 0)
            ]
        case .age47_51:
            return [
                ScoreEntry(rawValue: t(14, 30), points: 100), ScoreEntry(rawValue: t(14, 52), points: 99), ScoreEntry(rawValue: t(15, 8), points: 98),
                ScoreEntry(rawValue: t(15, 22), points: 97), ScoreEntry(rawValue: t(15, 35), points: 96), ScoreEntry(rawValue: t(15, 47), points: 95),
                ScoreEntry(rawValue: t(15, 58), points: 94), ScoreEntry(rawValue: t(16, 9), points: 93), ScoreEntry(rawValue: t(16, 19), points: 92),
                ScoreEntry(rawValue: t(16, 29), points: 91), ScoreEntry(rawValue: t(16, 39), points: 90), ScoreEntry(rawValue: t(16, 48), points: 89),
                ScoreEntry(rawValue: t(16, 58), points: 88), ScoreEntry(rawValue: t(17, 7), points: 87), ScoreEntry(rawValue: t(17, 16), points: 86),
                ScoreEntry(rawValue: t(17, 25), points: 85), ScoreEntry(rawValue: t(17, 35), points: 84), ScoreEntry(rawValue: t(17, 44), points: 83),
                ScoreEntry(rawValue: t(17, 53), points: 82), ScoreEntry(rawValue: t(18, 2), points: 81), ScoreEntry(rawValue: t(18, 12), points: 80),
                ScoreEntry(rawValue: t(18, 21), points: 79), ScoreEntry(rawValue: t(18, 31), points: 78), ScoreEntry(rawValue: t(18, 41), points: 77),
                ScoreEntry(rawValue: t(18, 51), points: 76), ScoreEntry(rawValue: t(19, 0), points: 75), ScoreEntry(rawValue: t(19, 10), points: 74),
                ScoreEntry(rawValue: t(19, 20), points: 73), ScoreEntry(rawValue: t(19, 30), points: 70), ScoreEntry(rawValue: t(19, 41), points: 69),
                ScoreEntry(rawValue: t(20, 2), points: 68), ScoreEntry(rawValue: t(20, 13), points: 67), ScoreEntry(rawValue: t(20, 37), points: 66),
                ScoreEntry(rawValue: t(20, 50), points: 65), ScoreEntry(rawValue: t(21, 4), points: 64), ScoreEntry(rawValue: t(21, 19), points: 63),
                ScoreEntry(rawValue: t(21, 37), points: 62), ScoreEntry(rawValue: t(21, 59), points: 61), ScoreEntry(rawValue: t(22, 4), points: 60),
                ScoreEntry(rawValue: t(22, 32), points: 50), ScoreEntry(rawValue: t(23, 0), points: 40), ScoreEntry(rawValue: t(23, 28), points: 30),
                ScoreEntry(rawValue: t(23, 56), points: 20), ScoreEntry(rawValue: t(24, 24), points: 10), ScoreEntry(rawValue: t(24, 52), points: 0)
            ]
        case .age52_56:
            return [
                ScoreEntry(rawValue: t(15, 9), points: 100), ScoreEntry(rawValue: t(15, 38), points: 99), ScoreEntry(rawValue: t(15, 54), points: 98),
                ScoreEntry(rawValue: t(16, 8), points: 97), ScoreEntry(rawValue: t(16, 21), points: 96), ScoreEntry(rawValue: t(16, 33), points: 95),
                ScoreEntry(rawValue: t(16, 44), points: 94), ScoreEntry(rawValue: t(16, 55), points: 93), ScoreEntry(rawValue: t(17, 6), points: 92),
                ScoreEntry(rawValue: t(17, 16), points: 91), ScoreEntry(rawValue: t(17, 26), points: 90), ScoreEntry(rawValue: t(17, 35), points: 89),
                ScoreEntry(rawValue: t(17, 45), points: 88), ScoreEntry(rawValue: t(17, 54), points: 87), ScoreEntry(rawValue: t(18, 4), points: 86),
                ScoreEntry(rawValue: t(18, 13), points: 85), ScoreEntry(rawValue: t(18, 22), points: 84), ScoreEntry(rawValue: t(18, 32), points: 83),
                ScoreEntry(rawValue: t(18, 41), points: 82), ScoreEntry(rawValue: t(18, 51), points: 81), ScoreEntry(rawValue: t(19, 0), points: 80),
                ScoreEntry(rawValue: t(19, 10), points: 79), ScoreEntry(rawValue: t(19, 20), points: 78), ScoreEntry(rawValue: t(19, 30), points: 77),
                ScoreEntry(rawValue: t(19, 39), points: 76), ScoreEntry(rawValue: t(19, 49), points: 75), ScoreEntry(rawValue: t(19, 59), points: 74),
                ScoreEntry(rawValue: t(20, 10), points: 73), ScoreEntry(rawValue: t(20, 20), points: 70), ScoreEntry(rawValue: t(20, 52), points: 69),
                ScoreEntry(rawValue: t(21, 3), points: 68), ScoreEntry(rawValue: t(21, 15), points: 67), ScoreEntry(rawValue: t(21, 27), points: 66),
                ScoreEntry(rawValue: t(21, 40), points: 65), ScoreEntry(rawValue: t(21, 54), points: 64), ScoreEntry(rawValue: t(22, 10), points: 63),
                ScoreEntry(rawValue: t(22, 28), points: 62), ScoreEntry(rawValue: t(22, 50), points: 60), ScoreEntry(rawValue: t(23, 18), points: 50),
                ScoreEntry(rawValue: t(23, 46), points: 40), ScoreEntry(rawValue: t(24, 14), points: 30), ScoreEntry(rawValue: t(24, 42), points: 20),
                ScoreEntry(rawValue: t(25, 10), points: 10), ScoreEntry(rawValue: t(25, 38), points: 0)
            ]
        case .age57_61:
            return [
                ScoreEntry(rawValue: t(15, 28), points: 100), ScoreEntry(rawValue: t(15, 55), points: 99), ScoreEntry(rawValue: t(16, 22), points: 98),
                ScoreEntry(rawValue: t(16, 44), points: 97), ScoreEntry(rawValue: t(16, 58), points: 96), ScoreEntry(rawValue: t(17, 14), points: 95),
                ScoreEntry(rawValue: t(17, 27), points: 94), ScoreEntry(rawValue: t(17, 45), points: 93), ScoreEntry(rawValue: t(17, 57), points: 92),
                ScoreEntry(rawValue: t(18, 7), points: 91), ScoreEntry(rawValue: t(18, 17), points: 90), ScoreEntry(rawValue: t(18, 25), points: 89),
                ScoreEntry(rawValue: t(18, 36), points: 88), ScoreEntry(rawValue: t(18, 45), points: 87), ScoreEntry(rawValue: t(18, 53), points: 86),
                ScoreEntry(rawValue: t(19, 0), points: 85), ScoreEntry(rawValue: t(19, 7), points: 84), ScoreEntry(rawValue: t(19, 17), points: 83),
                ScoreEntry(rawValue: t(19, 27), points: 82), ScoreEntry(rawValue: t(19, 36), points: 81), ScoreEntry(rawValue: t(19, 45), points: 80),
                ScoreEntry(rawValue: t(19, 51), points: 79), ScoreEntry(rawValue: t(19, 59), points: 78), ScoreEntry(rawValue: t(20, 7), points: 77),
                ScoreEntry(rawValue: t(20, 14), points: 76), ScoreEntry(rawValue: t(20, 22), points: 75), ScoreEntry(rawValue: t(20, 31), points: 74),
                ScoreEntry(rawValue: t(20, 41), points: 73), ScoreEntry(rawValue: t(20, 46), points: 72), ScoreEntry(rawValue: t(20, 54), points: 71),
                ScoreEntry(rawValue: t(21, 0), points: 70), ScoreEntry(rawValue: t(21, 1), points: 69), ScoreEntry(rawValue: t(21, 19), points: 68),
                ScoreEntry(rawValue: t(21, 35), points: 67), ScoreEntry(rawValue: t(21, 47), points: 66), ScoreEntry(rawValue: t(22, 3), points: 65),
                ScoreEntry(rawValue: t(22, 21), points: 64), ScoreEntry(rawValue: t(22, 39), points: 63), ScoreEntry(rawValue: t(22, 58), points: 62),
                ScoreEntry(rawValue: t(23, 12), points: 61), ScoreEntry(rawValue: t(23, 36), points: 60), ScoreEntry(rawValue: t(24, 4), points: 50),
                ScoreEntry(rawValue: t(24, 32), points: 40), ScoreEntry(rawValue: t(25, 0), points: 30), ScoreEntry(rawValue: t(25, 28), points: 20),
                ScoreEntry(rawValue: t(25, 56), points: 10), ScoreEntry(rawValue: t(26, 24), points: 0)
            ]
        case .age62Plus:
            return [
                ScoreEntry(rawValue: t(15, 28), points: 100), ScoreEntry(rawValue: t(15, 55), points: 99), ScoreEntry(rawValue: t(16, 22), points: 98),
                ScoreEntry(rawValue: t(16, 44), points: 97), ScoreEntry(rawValue: t(16, 58), points: 96), ScoreEntry(rawValue: t(17, 14), points: 95),
                ScoreEntry(rawValue: t(17, 27), points: 94), ScoreEntry(rawValue: t(17, 45), points: 93), ScoreEntry(rawValue: t(17, 57), points: 92),
                ScoreEntry(rawValue: t(18, 7), points: 91), ScoreEntry(rawValue: t(18, 17), points: 90), ScoreEntry(rawValue: t(18, 25), points: 89),
                ScoreEntry(rawValue: t(18, 36), points: 88), ScoreEntry(rawValue: t(18, 45), points: 87), ScoreEntry(rawValue: t(18, 53), points: 86),
                ScoreEntry(rawValue: t(19, 0), points: 85), ScoreEntry(rawValue: t(19, 7), points: 84), ScoreEntry(rawValue: t(19, 17), points: 83),
                ScoreEntry(rawValue: t(19, 27), points: 82), ScoreEntry(rawValue: t(19, 36), points: 81), ScoreEntry(rawValue: t(19, 45), points: 80),
                ScoreEntry(rawValue: t(19, 51), points: 79), ScoreEntry(rawValue: t(19, 59), points: 78), ScoreEntry(rawValue: t(20, 7), points: 77),
                ScoreEntry(rawValue: t(20, 14), points: 76), ScoreEntry(rawValue: t(20, 22), points: 75), ScoreEntry(rawValue: t(20, 31), points: 74),
                ScoreEntry(rawValue: t(20, 41), points: 73), ScoreEntry(rawValue: t(20, 46), points: 72), ScoreEntry(rawValue: t(20, 54), points: 71),
                ScoreEntry(rawValue: t(21, 0), points: 70), ScoreEntry(rawValue: t(21, 40), points: 60), ScoreEntry(rawValue: t(22, 8), points: 50),
                ScoreEntry(rawValue: t(22, 36), points: 40), ScoreEntry(rawValue: t(23, 4), points: 30), ScoreEntry(rawValue: t(23, 32), points: 20),
                ScoreEntry(rawValue: t(24, 0), points: 10), ScoreEntry(rawValue: t(24, 28), points: 0)
            ]
        }
    }

    private func getTwoMileRunTableFemale(ageBracket: AgeBracket) -> [ScoreEntry] {
        switch ageBracket {
        case .age17_21:
            return [
                ScoreEntry(rawValue: t(16, 0), points: 100), ScoreEntry(rawValue: t(16, 28), points: 99), ScoreEntry(rawValue: t(16, 49), points: 98),
                ScoreEntry(rawValue: t(17, 7), points: 97), ScoreEntry(rawValue: t(17, 14), points: 96), ScoreEntry(rawValue: t(17, 23), points: 95),
                ScoreEntry(rawValue: t(17, 31), points: 94), ScoreEntry(rawValue: t(17, 37), points: 93), ScoreEntry(rawValue: t(17, 44), points: 92),
                ScoreEntry(rawValue: t(17, 50), points: 91), ScoreEntry(rawValue: t(17, 55), points: 90), ScoreEntry(rawValue: t(18, 7), points: 89),
                ScoreEntry(rawValue: t(18, 13), points: 88), ScoreEntry(rawValue: t(18, 24), points: 87), ScoreEntry(rawValue: t(18, 34), points: 86),
                ScoreEntry(rawValue: t(18, 44), points: 85), ScoreEntry(rawValue: t(18, 54), points: 84), ScoreEntry(rawValue: t(19, 3), points: 83),
                ScoreEntry(rawValue: t(19, 12), points: 82), ScoreEntry(rawValue: t(19, 21), points: 81), ScoreEntry(rawValue: t(19, 30), points: 80),
                ScoreEntry(rawValue: t(19, 39), points: 79), ScoreEntry(rawValue: t(19, 47), points: 78), ScoreEntry(rawValue: t(19, 56), points: 77),
                ScoreEntry(rawValue: t(20, 5), points: 76), ScoreEntry(rawValue: t(20, 13), points: 75), ScoreEntry(rawValue: t(20, 24), points: 74),
                ScoreEntry(rawValue: t(20, 35), points: 73), ScoreEntry(rawValue: t(20, 45), points: 72), ScoreEntry(rawValue: t(20, 56), points: 71),
                ScoreEntry(rawValue: t(21, 6), points: 70), ScoreEntry(rawValue: t(21, 17), points: 69), ScoreEntry(rawValue: t(21, 28), points: 68),
                ScoreEntry(rawValue: t(21, 49), points: 67), ScoreEntry(rawValue: t(22, 1), points: 66), ScoreEntry(rawValue: t(22, 12), points: 65),
                ScoreEntry(rawValue: t(22, 25), points: 64), ScoreEntry(rawValue: t(22, 38), points: 63), ScoreEntry(rawValue: t(22, 53), points: 62),
                ScoreEntry(rawValue: t(22, 55), points: 60), ScoreEntry(rawValue: t(23, 24), points: 50), ScoreEntry(rawValue: t(23, 53), points: 40),
                ScoreEntry(rawValue: t(24, 22), points: 30), ScoreEntry(rawValue: t(24, 51), points: 20), ScoreEntry(rawValue: t(25, 20), points: 10),
                ScoreEntry(rawValue: t(25, 50), points: 0)
            ]
        case .age22_26:
            return [
                ScoreEntry(rawValue: t(15, 30), points: 100), ScoreEntry(rawValue: t(15, 44), points: 99), ScoreEntry(rawValue: t(15, 55), points: 98),
                ScoreEntry(rawValue: t(16, 0), points: 97), ScoreEntry(rawValue: t(16, 4), points: 96), ScoreEntry(rawValue: t(16, 27), points: 95),
                ScoreEntry(rawValue: t(16, 46), points: 94), ScoreEntry(rawValue: t(17, 3), points: 93), ScoreEntry(rawValue: t(17, 17), points: 92),
                ScoreEntry(rawValue: t(17, 31), points: 91), ScoreEntry(rawValue: t(17, 44), points: 90), ScoreEntry(rawValue: t(17, 55), points: 89),
                ScoreEntry(rawValue: t(18, 7), points: 88), ScoreEntry(rawValue: t(18, 18), points: 87), ScoreEntry(rawValue: t(18, 28), points: 86),
                ScoreEntry(rawValue: t(18, 38), points: 85), ScoreEntry(rawValue: t(18, 48), points: 84), ScoreEntry(rawValue: t(18, 58), points: 83),
                ScoreEntry(rawValue: t(19, 7), points: 82), ScoreEntry(rawValue: t(19, 16), points: 81), ScoreEntry(rawValue: t(19, 25), points: 80),
                ScoreEntry(rawValue: t(19, 34), points: 79), ScoreEntry(rawValue: t(19, 43), points: 78), ScoreEntry(rawValue: t(19, 52), points: 77),
                ScoreEntry(rawValue: t(20, 1), points: 76), ScoreEntry(rawValue: t(20, 12), points: 75), ScoreEntry(rawValue: t(20, 24), points: 74),
                ScoreEntry(rawValue: t(20, 35), points: 73), ScoreEntry(rawValue: t(20, 46), points: 72), ScoreEntry(rawValue: t(20, 57), points: 71),
                ScoreEntry(rawValue: t(21, 0), points: 70), ScoreEntry(rawValue: t(21, 32), points: 69), ScoreEntry(rawValue: t(21, 40), points: 68),
                ScoreEntry(rawValue: t(21, 49), points: 67), ScoreEntry(rawValue: t(21, 58), points: 66), ScoreEntry(rawValue: t(22, 7), points: 65),
                ScoreEntry(rawValue: t(22, 16), points: 64), ScoreEntry(rawValue: t(22, 26), points: 63), ScoreEntry(rawValue: t(22, 37), points: 62),
                ScoreEntry(rawValue: t(22, 45), points: 60), ScoreEntry(rawValue: t(23, 14), points: 50), ScoreEntry(rawValue: t(23, 43), points: 40),
                ScoreEntry(rawValue: t(24, 12), points: 30), ScoreEntry(rawValue: t(24, 41), points: 20), ScoreEntry(rawValue: t(25, 10), points: 10),
                ScoreEntry(rawValue: t(25, 40), points: 0)
            ]
        case .age27_31:
            return [
                ScoreEntry(rawValue: t(15, 30), points: 100), ScoreEntry(rawValue: t(16, 0), points: 98), ScoreEntry(rawValue: t(16, 30), points: 96),
                ScoreEntry(rawValue: t(17, 0), points: 94), ScoreEntry(rawValue: t(17, 30), points: 92), ScoreEntry(rawValue: t(18, 0), points: 88),
                ScoreEntry(rawValue: t(18, 30), points: 84), ScoreEntry(rawValue: t(19, 0), points: 80), ScoreEntry(rawValue: t(19, 30), points: 76),
                ScoreEntry(rawValue: t(20, 0), points: 72), ScoreEntry(rawValue: t(20, 30), points: 68), ScoreEntry(rawValue: t(21, 0), points: 64),
                ScoreEntry(rawValue: t(21, 30), points: 62), ScoreEntry(rawValue: t(22, 45), points: 60), ScoreEntry(rawValue: t(23, 14), points: 50),
                ScoreEntry(rawValue: t(23, 43), points: 40), ScoreEntry(rawValue: t(24, 12), points: 30), ScoreEntry(rawValue: t(24, 41), points: 20),
                ScoreEntry(rawValue: t(25, 10), points: 10), ScoreEntry(rawValue: t(25, 40), points: 0)
            ]
        case .age32_36:
            return [
                ScoreEntry(rawValue: t(15, 48), points: 100), ScoreEntry(rawValue: t(16, 15), points: 98), ScoreEntry(rawValue: t(16, 45), points: 96),
                ScoreEntry(rawValue: t(17, 15), points: 94), ScoreEntry(rawValue: t(17, 45), points: 92), ScoreEntry(rawValue: t(18, 15), points: 88),
                ScoreEntry(rawValue: t(18, 45), points: 84), ScoreEntry(rawValue: t(19, 15), points: 80), ScoreEntry(rawValue: t(19, 45), points: 76),
                ScoreEntry(rawValue: t(20, 15), points: 72), ScoreEntry(rawValue: t(20, 45), points: 68), ScoreEntry(rawValue: t(21, 15), points: 64),
                ScoreEntry(rawValue: t(21, 45), points: 62), ScoreEntry(rawValue: t(22, 50), points: 60), ScoreEntry(rawValue: t(23, 19), points: 50),
                ScoreEntry(rawValue: t(23, 48), points: 40), ScoreEntry(rawValue: t(24, 17), points: 30), ScoreEntry(rawValue: t(24, 46), points: 20),
                ScoreEntry(rawValue: t(25, 15), points: 10), ScoreEntry(rawValue: t(25, 45), points: 0)
            ]
        case .age37_41:
            return [
                ScoreEntry(rawValue: t(15, 51), points: 100), ScoreEntry(rawValue: t(16, 21), points: 98), ScoreEntry(rawValue: t(16, 51), points: 96),
                ScoreEntry(rawValue: t(17, 21), points: 94), ScoreEntry(rawValue: t(17, 51), points: 92), ScoreEntry(rawValue: t(18, 21), points: 88),
                ScoreEntry(rawValue: t(18, 51), points: 84), ScoreEntry(rawValue: t(19, 21), points: 80), ScoreEntry(rawValue: t(19, 51), points: 76),
                ScoreEntry(rawValue: t(20, 21), points: 72), ScoreEntry(rawValue: t(20, 51), points: 68), ScoreEntry(rawValue: t(21, 21), points: 64),
                ScoreEntry(rawValue: t(21, 51), points: 62), ScoreEntry(rawValue: t(22, 59), points: 60), ScoreEntry(rawValue: t(23, 28), points: 50),
                ScoreEntry(rawValue: t(23, 57), points: 40), ScoreEntry(rawValue: t(24, 26), points: 30), ScoreEntry(rawValue: t(24, 55), points: 20),
                ScoreEntry(rawValue: t(25, 24), points: 10), ScoreEntry(rawValue: t(25, 54), points: 0)
            ]
        case .age42_46:
            return [
                ScoreEntry(rawValue: t(16, 0), points: 100), ScoreEntry(rawValue: t(16, 31), points: 98), ScoreEntry(rawValue: t(17, 2), points: 96),
                ScoreEntry(rawValue: t(17, 33), points: 94), ScoreEntry(rawValue: t(18, 4), points: 92), ScoreEntry(rawValue: t(18, 35), points: 88),
                ScoreEntry(rawValue: t(19, 6), points: 84), ScoreEntry(rawValue: t(19, 37), points: 80), ScoreEntry(rawValue: t(20, 8), points: 76),
                ScoreEntry(rawValue: t(20, 39), points: 72), ScoreEntry(rawValue: t(21, 10), points: 68), ScoreEntry(rawValue: t(21, 41), points: 64),
                ScoreEntry(rawValue: t(22, 12), points: 62), ScoreEntry(rawValue: t(23, 15), points: 60), ScoreEntry(rawValue: t(23, 44), points: 50),
                ScoreEntry(rawValue: t(24, 13), points: 40), ScoreEntry(rawValue: t(24, 42), points: 30), ScoreEntry(rawValue: t(25, 11), points: 20),
                ScoreEntry(rawValue: t(25, 40), points: 10), ScoreEntry(rawValue: t(26, 10), points: 0)
            ]
        case .age47_51:
            return [
                ScoreEntry(rawValue: t(16, 30), points: 100), ScoreEntry(rawValue: t(17, 0), points: 98), ScoreEntry(rawValue: t(17, 30), points: 96),
                ScoreEntry(rawValue: t(18, 0), points: 94), ScoreEntry(rawValue: t(18, 30), points: 92), ScoreEntry(rawValue: t(19, 0), points: 88),
                ScoreEntry(rawValue: t(19, 30), points: 84), ScoreEntry(rawValue: t(20, 0), points: 80), ScoreEntry(rawValue: t(20, 30), points: 76),
                ScoreEntry(rawValue: t(21, 0), points: 72), ScoreEntry(rawValue: t(21, 30), points: 68), ScoreEntry(rawValue: t(22, 0), points: 64),
                ScoreEntry(rawValue: t(22, 30), points: 62), ScoreEntry(rawValue: t(23, 30), points: 60), ScoreEntry(rawValue: t(24, 0), points: 50),
                ScoreEntry(rawValue: t(24, 30), points: 40), ScoreEntry(rawValue: t(25, 0), points: 30), ScoreEntry(rawValue: t(25, 30), points: 20),
                ScoreEntry(rawValue: t(26, 0), points: 10), ScoreEntry(rawValue: t(26, 25), points: 0)
            ]
        case .age52_56:
            return [
                ScoreEntry(rawValue: t(16, 59), points: 100), ScoreEntry(rawValue: t(17, 44), points: 98), ScoreEntry(rawValue: t(18, 5), points: 96),
                ScoreEntry(rawValue: t(18, 22), points: 94), ScoreEntry(rawValue: t(18, 50), points: 92), ScoreEntry(rawValue: t(19, 15), points: 88),
                ScoreEntry(rawValue: t(19, 47), points: 84), ScoreEntry(rawValue: t(20, 16), points: 80), ScoreEntry(rawValue: t(20, 44), points: 76),
                ScoreEntry(rawValue: t(21, 10), points: 72), ScoreEntry(rawValue: t(21, 35), points: 68), ScoreEntry(rawValue: t(22, 7), points: 64),
                ScoreEntry(rawValue: t(22, 38), points: 62), ScoreEntry(rawValue: t(24, 0), points: 60), ScoreEntry(rawValue: t(24, 29), points: 50),
                ScoreEntry(rawValue: t(24, 58), points: 40), ScoreEntry(rawValue: t(25, 27), points: 30), ScoreEntry(rawValue: t(25, 56), points: 20),
                ScoreEntry(rawValue: t(26, 25), points: 10), ScoreEntry(rawValue: t(26, 55), points: 0)
            ]
        case .age57_61:
            return [
                ScoreEntry(rawValue: t(17, 18), points: 100), ScoreEntry(rawValue: t(17, 56), points: 98), ScoreEntry(rawValue: t(18, 25), points: 96),
                ScoreEntry(rawValue: t(18, 46), points: 94), ScoreEntry(rawValue: t(19, 4), points: 92), ScoreEntry(rawValue: t(19, 45), points: 88),
                ScoreEntry(rawValue: t(20, 17), points: 84), ScoreEntry(rawValue: t(20, 44), points: 80), ScoreEntry(rawValue: t(21, 15), points: 76),
                ScoreEntry(rawValue: t(21, 40), points: 72), ScoreEntry(rawValue: t(22, 9), points: 68), ScoreEntry(rawValue: t(22, 43), points: 64),
                ScoreEntry(rawValue: t(23, 22), points: 62), ScoreEntry(rawValue: t(24, 48), points: 60), ScoreEntry(rawValue: t(25, 17), points: 50),
                ScoreEntry(rawValue: t(25, 46), points: 40), ScoreEntry(rawValue: t(26, 15), points: 30), ScoreEntry(rawValue: t(26, 44), points: 20),
                ScoreEntry(rawValue: t(27, 13), points: 10), ScoreEntry(rawValue: t(27, 43), points: 0)
            ]
        case .age62Plus:
            return [
                ScoreEntry(rawValue: t(17, 18), points: 100), ScoreEntry(rawValue: t(17, 56), points: 98), ScoreEntry(rawValue: t(18, 25), points: 96),
                ScoreEntry(rawValue: t(18, 46), points: 94), ScoreEntry(rawValue: t(19, 4), points: 92), ScoreEntry(rawValue: t(19, 45), points: 88),
                ScoreEntry(rawValue: t(20, 17), points: 84), ScoreEntry(rawValue: t(20, 44), points: 80), ScoreEntry(rawValue: t(21, 15), points: 76),
                ScoreEntry(rawValue: t(21, 40), points: 72), ScoreEntry(rawValue: t(22, 9), points: 68), ScoreEntry(rawValue: t(22, 43), points: 64),
                ScoreEntry(rawValue: t(23, 22), points: 62), ScoreEntry(rawValue: t(25, 0), points: 60), ScoreEntry(rawValue: t(25, 29), points: 50),
                ScoreEntry(rawValue: t(25, 58), points: 40), ScoreEntry(rawValue: t(26, 27), points: 30), ScoreEntry(rawValue: t(26, 56), points: 20),
                ScoreEntry(rawValue: t(27, 25), points: 10), ScoreEntry(rawValue: t(27, 55), points: 0)
            ]
        }
    }

}
