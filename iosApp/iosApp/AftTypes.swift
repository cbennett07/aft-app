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

    // MARK: - Scoring Tables

    private func getDeadliftTable(ageBracket: AgeBracket, female: Bool) -> [ScoreEntry] {
        if female {
            return getDeadliftFemaleTable(ageBracket: ageBracket)
        }
        return getDeadliftMaleTable(ageBracket: ageBracket)
    }

    private func getDeadliftMaleTable(ageBracket: AgeBracket) -> [ScoreEntry] {
        switch ageBracket {
        case .age17_21:
            return [
                ScoreEntry(rawValue: 340, points: 100), ScoreEntry(rawValue: 330, points: 98),
                ScoreEntry(rawValue: 320, points: 96), ScoreEntry(rawValue: 310, points: 94),
                ScoreEntry(rawValue: 300, points: 92), ScoreEntry(rawValue: 290, points: 89),
                ScoreEntry(rawValue: 280, points: 87), ScoreEntry(rawValue: 270, points: 85),
                ScoreEntry(rawValue: 260, points: 83), ScoreEntry(rawValue: 250, points: 81),
                ScoreEntry(rawValue: 240, points: 79), ScoreEntry(rawValue: 230, points: 77),
                ScoreEntry(rawValue: 220, points: 75), ScoreEntry(rawValue: 210, points: 73),
                ScoreEntry(rawValue: 200, points: 70), ScoreEntry(rawValue: 190, points: 69),
                ScoreEntry(rawValue: 180, points: 67), ScoreEntry(rawValue: 170, points: 65),
                ScoreEntry(rawValue: 160, points: 63), ScoreEntry(rawValue: 150, points: 60),
                ScoreEntry(rawValue: 130, points: 50), ScoreEntry(rawValue: 120, points: 40),
                ScoreEntry(rawValue: 110, points: 30), ScoreEntry(rawValue: 100, points: 20),
                ScoreEntry(rawValue: 90, points: 10), ScoreEntry(rawValue: 80, points: 0)
            ]
        case .age22_26, .age27_31, .age32_36, .age37_41, .age42_46:
            return [
                ScoreEntry(rawValue: 350, points: 100), ScoreEntry(rawValue: 340, points: 99),
                ScoreEntry(rawValue: 330, points: 97), ScoreEntry(rawValue: 320, points: 95),
                ScoreEntry(rawValue: 310, points: 93), ScoreEntry(rawValue: 300, points: 91),
                ScoreEntry(rawValue: 290, points: 89), ScoreEntry(rawValue: 280, points: 87),
                ScoreEntry(rawValue: 270, points: 85), ScoreEntry(rawValue: 260, points: 83),
                ScoreEntry(rawValue: 250, points: 81), ScoreEntry(rawValue: 240, points: 79),
                ScoreEntry(rawValue: 230, points: 77), ScoreEntry(rawValue: 220, points: 75),
                ScoreEntry(rawValue: 210, points: 73), ScoreEntry(rawValue: 200, points: 70),
                ScoreEntry(rawValue: 190, points: 69), ScoreEntry(rawValue: 180, points: 67),
                ScoreEntry(rawValue: 170, points: 65), ScoreEntry(rawValue: 160, points: 63),
                ScoreEntry(rawValue: 150, points: 60), ScoreEntry(rawValue: 140, points: 60),
                ScoreEntry(rawValue: 130, points: 50), ScoreEntry(rawValue: 120, points: 40),
                ScoreEntry(rawValue: 110, points: 30), ScoreEntry(rawValue: 100, points: 20),
                ScoreEntry(rawValue: 90, points: 10), ScoreEntry(rawValue: 80, points: 0)
            ]
        case .age47_51, .age52_56:
            return [
                ScoreEntry(rawValue: 340, points: 100), ScoreEntry(rawValue: 330, points: 99),
                ScoreEntry(rawValue: 320, points: 97), ScoreEntry(rawValue: 310, points: 95),
                ScoreEntry(rawValue: 300, points: 93), ScoreEntry(rawValue: 290, points: 91),
                ScoreEntry(rawValue: 280, points: 89), ScoreEntry(rawValue: 270, points: 87),
                ScoreEntry(rawValue: 260, points: 84), ScoreEntry(rawValue: 250, points: 82),
                ScoreEntry(rawValue: 240, points: 79), ScoreEntry(rawValue: 230, points: 76),
                ScoreEntry(rawValue: 220, points: 74), ScoreEntry(rawValue: 210, points: 73),
                ScoreEntry(rawValue: 200, points: 70), ScoreEntry(rawValue: 190, points: 69),
                ScoreEntry(rawValue: 180, points: 67), ScoreEntry(rawValue: 170, points: 66),
                ScoreEntry(rawValue: 160, points: 64), ScoreEntry(rawValue: 150, points: 62),
                ScoreEntry(rawValue: 140, points: 60), ScoreEntry(rawValue: 130, points: 50),
                ScoreEntry(rawValue: 120, points: 40), ScoreEntry(rawValue: 110, points: 30),
                ScoreEntry(rawValue: 100, points: 20), ScoreEntry(rawValue: 90, points: 10),
                ScoreEntry(rawValue: 80, points: 0)
            ]
        case .age57_61:
            return [
                ScoreEntry(rawValue: 250, points: 100), ScoreEntry(rawValue: 240, points: 99),
                ScoreEntry(rawValue: 230, points: 98), ScoreEntry(rawValue: 220, points: 97),
                ScoreEntry(rawValue: 210, points: 95), ScoreEntry(rawValue: 200, points: 94),
                ScoreEntry(rawValue: 190, points: 93), ScoreEntry(rawValue: 180, points: 91),
                ScoreEntry(rawValue: 170, points: 89), ScoreEntry(rawValue: 160, points: 85),
                ScoreEntry(rawValue: 150, points: 80), ScoreEntry(rawValue: 140, points: 72),
                ScoreEntry(rawValue: 130, points: 50), ScoreEntry(rawValue: 120, points: 40),
                ScoreEntry(rawValue: 110, points: 30), ScoreEntry(rawValue: 100, points: 20),
                ScoreEntry(rawValue: 90, points: 10), ScoreEntry(rawValue: 80, points: 0)
            ]
        case .age62Plus:
            return [
                ScoreEntry(rawValue: 230, points: 100), ScoreEntry(rawValue: 220, points: 99),
                ScoreEntry(rawValue: 210, points: 98), ScoreEntry(rawValue: 200, points: 95),
                ScoreEntry(rawValue: 190, points: 94), ScoreEntry(rawValue: 180, points: 92),
                ScoreEntry(rawValue: 170, points: 89), ScoreEntry(rawValue: 160, points: 82),
                ScoreEntry(rawValue: 150, points: 75), ScoreEntry(rawValue: 140, points: 72),
                ScoreEntry(rawValue: 130, points: 50), ScoreEntry(rawValue: 120, points: 40),
                ScoreEntry(rawValue: 110, points: 30), ScoreEntry(rawValue: 100, points: 20),
                ScoreEntry(rawValue: 90, points: 10), ScoreEntry(rawValue: 80, points: 0)
            ]
        }
    }

    private func getDeadliftFemaleTable(ageBracket: AgeBracket) -> [ScoreEntry] {
        switch ageBracket {
        case .age17_21:
            return [
                ScoreEntry(rawValue: 220, points: 100), ScoreEntry(rawValue: 210, points: 98),
                ScoreEntry(rawValue: 200, points: 97), ScoreEntry(rawValue: 190, points: 94),
                ScoreEntry(rawValue: 180, points: 91), ScoreEntry(rawValue: 170, points: 88),
                ScoreEntry(rawValue: 160, points: 84), ScoreEntry(rawValue: 150, points: 80),
                ScoreEntry(rawValue: 140, points: 75), ScoreEntry(rawValue: 130, points: 68),
                ScoreEntry(rawValue: 120, points: 60), ScoreEntry(rawValue: 110, points: 50),
                ScoreEntry(rawValue: 100, points: 40), ScoreEntry(rawValue: 90, points: 30),
                ScoreEntry(rawValue: 80, points: 20), ScoreEntry(rawValue: 70, points: 10),
                ScoreEntry(rawValue: 60, points: 0)
            ]
        default:
            return [
                ScoreEntry(rawValue: 230, points: 100), ScoreEntry(rawValue: 220, points: 99),
                ScoreEntry(rawValue: 210, points: 98), ScoreEntry(rawValue: 200, points: 97),
                ScoreEntry(rawValue: 190, points: 94), ScoreEntry(rawValue: 180, points: 91),
                ScoreEntry(rawValue: 170, points: 88), ScoreEntry(rawValue: 160, points: 84),
                ScoreEntry(rawValue: 150, points: 79), ScoreEntry(rawValue: 140, points: 75),
                ScoreEntry(rawValue: 130, points: 68), ScoreEntry(rawValue: 120, points: 60),
                ScoreEntry(rawValue: 110, points: 50), ScoreEntry(rawValue: 100, points: 40),
                ScoreEntry(rawValue: 90, points: 30), ScoreEntry(rawValue: 80, points: 20),
                ScoreEntry(rawValue: 70, points: 10), ScoreEntry(rawValue: 60, points: 0)
            ]
        }
    }

    private func getPushUpTable(ageBracket: AgeBracket, female: Bool) -> [ScoreEntry] {
        if female {
            return [
                ScoreEntry(rawValue: 50, points: 100), ScoreEntry(rawValue: 45, points: 94),
                ScoreEntry(rawValue: 40, points: 88), ScoreEntry(rawValue: 35, points: 81),
                ScoreEntry(rawValue: 30, points: 75), ScoreEntry(rawValue: 25, points: 68),
                ScoreEntry(rawValue: 20, points: 62), ScoreEntry(rawValue: 17, points: 60),
                ScoreEntry(rawValue: 15, points: 50), ScoreEntry(rawValue: 10, points: 30),
                ScoreEntry(rawValue: 5, points: 10), ScoreEntry(rawValue: 0, points: 0)
            ]
        }
        return [
            ScoreEntry(rawValue: 60, points: 100), ScoreEntry(rawValue: 55, points: 95),
            ScoreEntry(rawValue: 50, points: 90), ScoreEntry(rawValue: 45, points: 84),
            ScoreEntry(rawValue: 40, points: 78), ScoreEntry(rawValue: 35, points: 72),
            ScoreEntry(rawValue: 30, points: 66), ScoreEntry(rawValue: 27, points: 60),
            ScoreEntry(rawValue: 25, points: 55), ScoreEntry(rawValue: 20, points: 44),
            ScoreEntry(rawValue: 15, points: 33), ScoreEntry(rawValue: 10, points: 22),
            ScoreEntry(rawValue: 5, points: 11), ScoreEntry(rawValue: 0, points: 0)
        ]
    }

    private func getSDCTable(ageBracket: AgeBracket, female: Bool) -> [ScoreEntry] {
        // Times in seconds, lower is better
        if female {
            return [
                ScoreEntry(rawValue: 120, points: 100), ScoreEntry(rawValue: 130, points: 95),
                ScoreEntry(rawValue: 140, points: 90), ScoreEntry(rawValue: 150, points: 85),
                ScoreEntry(rawValue: 160, points: 80), ScoreEntry(rawValue: 170, points: 75),
                ScoreEntry(rawValue: 180, points: 70), ScoreEntry(rawValue: 190, points: 65),
                ScoreEntry(rawValue: 200, points: 60), ScoreEntry(rawValue: 210, points: 50),
                ScoreEntry(rawValue: 220, points: 40), ScoreEntry(rawValue: 240, points: 30),
                ScoreEntry(rawValue: 260, points: 20), ScoreEntry(rawValue: 280, points: 10),
                ScoreEntry(rawValue: 300, points: 0)
            ]
        }
        return [
            ScoreEntry(rawValue: 93, points: 100), ScoreEntry(rawValue: 100, points: 96),
            ScoreEntry(rawValue: 110, points: 90), ScoreEntry(rawValue: 120, points: 84),
            ScoreEntry(rawValue: 130, points: 78), ScoreEntry(rawValue: 140, points: 72),
            ScoreEntry(rawValue: 150, points: 66), ScoreEntry(rawValue: 155, points: 60),
            ScoreEntry(rawValue: 165, points: 54), ScoreEntry(rawValue: 175, points: 48),
            ScoreEntry(rawValue: 185, points: 42), ScoreEntry(rawValue: 195, points: 36),
            ScoreEntry(rawValue: 210, points: 30), ScoreEntry(rawValue: 230, points: 20),
            ScoreEntry(rawValue: 250, points: 10), ScoreEntry(rawValue: 270, points: 0)
        ]
    }

    private func getPlankTable(ageBracket: AgeBracket, female: Bool) -> [ScoreEntry] {
        // Times in seconds, higher is better
        return [
            ScoreEntry(rawValue: 240, points: 100), ScoreEntry(rawValue: 230, points: 97),
            ScoreEntry(rawValue: 220, points: 94), ScoreEntry(rawValue: 210, points: 91),
            ScoreEntry(rawValue: 200, points: 88), ScoreEntry(rawValue: 190, points: 85),
            ScoreEntry(rawValue: 180, points: 82), ScoreEntry(rawValue: 170, points: 78),
            ScoreEntry(rawValue: 160, points: 74), ScoreEntry(rawValue: 150, points: 70),
            ScoreEntry(rawValue: 140, points: 66), ScoreEntry(rawValue: 130, points: 62),
            ScoreEntry(rawValue: 120, points: 60), ScoreEntry(rawValue: 110, points: 52),
            ScoreEntry(rawValue: 100, points: 45), ScoreEntry(rawValue: 90, points: 37),
            ScoreEntry(rawValue: 80, points: 30), ScoreEntry(rawValue: 70, points: 22),
            ScoreEntry(rawValue: 60, points: 15), ScoreEntry(rawValue: 50, points: 7),
            ScoreEntry(rawValue: 40, points: 0)
        ]
    }

    private func getTwoMileRunTable(ageBracket: AgeBracket, female: Bool) -> [ScoreEntry] {
        // Times in seconds, lower is better
        if female {
            return [
                ScoreEntry(rawValue: 930, points: 100), ScoreEntry(rawValue: 960, points: 97),
                ScoreEntry(rawValue: 990, points: 94), ScoreEntry(rawValue: 1020, points: 91),
                ScoreEntry(rawValue: 1050, points: 88), ScoreEntry(rawValue: 1080, points: 85),
                ScoreEntry(rawValue: 1110, points: 82), ScoreEntry(rawValue: 1140, points: 78),
                ScoreEntry(rawValue: 1170, points: 74), ScoreEntry(rawValue: 1200, points: 70),
                ScoreEntry(rawValue: 1230, points: 66), ScoreEntry(rawValue: 1260, points: 62),
                ScoreEntry(rawValue: 1278, points: 60), ScoreEntry(rawValue: 1320, points: 50),
                ScoreEntry(rawValue: 1380, points: 40), ScoreEntry(rawValue: 1440, points: 30),
                ScoreEntry(rawValue: 1500, points: 20), ScoreEntry(rawValue: 1560, points: 10),
                ScoreEntry(rawValue: 1620, points: 0)
            ]
        }
        return [
            ScoreEntry(rawValue: 810, points: 100), ScoreEntry(rawValue: 840, points: 97),
            ScoreEntry(rawValue: 870, points: 94), ScoreEntry(rawValue: 900, points: 91),
            ScoreEntry(rawValue: 930, points: 88), ScoreEntry(rawValue: 960, points: 85),
            ScoreEntry(rawValue: 990, points: 82), ScoreEntry(rawValue: 1020, points: 78),
            ScoreEntry(rawValue: 1050, points: 74), ScoreEntry(rawValue: 1080, points: 70),
            ScoreEntry(rawValue: 1110, points: 66), ScoreEntry(rawValue: 1140, points: 62),
            ScoreEntry(rawValue: 1158, points: 60), ScoreEntry(rawValue: 1200, points: 50),
            ScoreEntry(rawValue: 1260, points: 40), ScoreEntry(rawValue: 1320, points: 30),
            ScoreEntry(rawValue: 1380, points: 20), ScoreEntry(rawValue: 1440, points: 10),
            ScoreEntry(rawValue: 1500, points: 0)
        ]
    }
}
