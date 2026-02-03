import Foundation
import PDFKit
import UIKit

struct Form705Data {
    let soldierName: String
    let graderName: String
    let unitLocation: String
    let mos: String
    let payGrade: String
    let score: SwiftAftScore
    let testDate: Date

    init(
        soldierName: String,
        graderName: String,
        unitLocation: String,
        mos: String,
        payGrade: String,
        score: SwiftAftScore,
        testDate: Date = Date()
    ) {
        self.soldierName = soldierName
        self.graderName = graderName
        self.unitLocation = unitLocation
        self.mos = mos
        self.payGrade = payGrade
        self.score = score
        self.testDate = testDate
    }
}

class Form705Generator {

    // Cache for preloaded template
    private static var cachedTemplateURL: URL?
    private static var cachedDocument: PDFDocument?

    /// Preload the PDF template to avoid first-use delays
    static func preloadTemplate() {
        DispatchQueue.global(qos: .userInitiated).async {
            if cachedTemplateURL == nil {
                cachedTemplateURL = Bundle.main.url(forResource: "da_form_705", withExtension: "pdf")
                if let url = cachedTemplateURL {
                    // Load the document once to warm up PDFKit
                    let _ = PDFDocument(url: url)
                    print("Form705: Template preloaded")
                }
            }
        }
    }

    static func generateForm705(data: Form705Data) -> Result<URL, Error> {
        // Use cached URL if available, otherwise search bundle
        var templateURL: URL? = cachedTemplateURL

        if templateURL == nil {
            // Debug: List all PDF files in bundle
            print("Form705: Searching for PDF in bundle...")
            if let bundlePath = Bundle.main.resourcePath {
                print("Form705: Bundle path: \(bundlePath)")
                let fileManager = FileManager.default
                if let contents = try? fileManager.contentsOfDirectory(atPath: bundlePath) {
                    let pdfFiles = contents.filter { $0.hasSuffix(".pdf") }
                    print("Form705: PDF files in bundle: \(pdfFiles)")
                    if pdfFiles.isEmpty {
                        print("Form705: No PDF files found in bundle!")
                        // List all files for debugging
                        print("Form705: All bundle files: \(contents.prefix(30))...")
                    }
                }
                // Also check for Resources subfolder
                let resourcesPath = bundlePath + "/Resources"
                if let resourceContents = try? fileManager.contentsOfDirectory(atPath: resourcesPath) {
                    let resourcePdfs = resourceContents.filter { $0.hasSuffix(".pdf") }
                    print("Form705: PDF files in Resources subfolder: \(resourcePdfs)")
                }
            }

            // Get the template PDF from bundle - try multiple locations
            templateURL = Bundle.main.url(forResource: "da_form_705", withExtension: "pdf")

            // If not found, try with subdirectory
            if templateURL == nil {
                templateURL = Bundle.main.url(forResource: "da_form_705", withExtension: "pdf", subdirectory: "Resources")
                if templateURL != nil {
                    print("Form705: Found template in Resources subdirectory")
                }
            }

            // Cache for next time
            cachedTemplateURL = templateURL
        } else {
            print("Form705: Using cached template URL")
        }

        guard let templateURL = templateURL else {
            print("Form705: Template not found in bundle - PDF must be added to Xcode target")
            return .failure(Form705Error.templateNotFound)
        }

        guard let document = PDFDocument(url: templateURL) else {
            print("Form705: Cannot load PDF document")
            return .failure(Form705Error.cannotLoadTemplate)
        }

        guard let page = document.page(at: 0) else {
            print("Form705: Cannot access page 0")
            return .failure(Form705Error.cannotAccessPage)
        }

        // Get annotations (form fields)
        let annotations = page.annotations
        print("Form705: Found \(annotations.count) annotations")

        // Date formatter for the form
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyyMMdd"
        let formattedDate = dateFormatter.string(from: data.testDate)

        var filledFields = 0

        // Fill form fields
        for annotation in annotations {
            guard let fieldName = annotation.fieldName else { continue }

            // Header fields
            if fieldName.contains("Name[0]") && !fieldName.contains("OIC") {
                annotation.widgetStringValue = data.soldierName
                filledFields += 1
            }
            else if fieldName.contains("Unit_Location[0]") {
                annotation.widgetStringValue = data.unitLocation
                filledFields += 1
            }
            // Standard checkboxes
            else if fieldName.contains("Check_Standard_Combat[0]") {
                if data.score.isCombatMos {
                    annotation.buttonWidgetState = .onState
                    filledFields += 1
                }
            }
            else if fieldName.contains("Check_Standard_General[0]") {
                if !data.score.isCombatMos {
                    annotation.buttonWidgetState = .onState
                    filledFields += 1
                }
            }
            // Gender checkboxes
            else if fieldName.contains("Male[0]") {
                if data.score.soldierGender == "Male" {
                    annotation.buttonWidgetState = .onState
                    filledFields += 1
                }
            }
            else if fieldName.contains("Female[0]") {
                if data.score.soldierGender == "Female" {
                    annotation.buttonWidgetState = .onState
                    filledFields += 1
                }
            }
            // Test One fields
            else if fieldName.contains("Test_One_Date[0]") {
                annotation.widgetStringValue = formattedDate
                filledFields += 1
            }
            else if fieldName.contains("Test_One_MOS[0]") {
                annotation.widgetStringValue = data.mos
                filledFields += 1
            }
            else if fieldName.contains("Test_One_Rank_Grade[0]") {
                annotation.widgetStringValue = data.payGrade
                filledFields += 1
            }
            else if fieldName.contains("Test_One_Age[0]") {
                annotation.widgetStringValue = "\(data.score.soldierAge)"
                filledFields += 1
            }
            // Event scores
            else if fieldName.contains("Test_One_First_Attempt[0]") {
                if let deadlift = data.score.eventScores.first(where: { $0.eventName.contains("Deadlift") }) {
                    annotation.widgetStringValue = "\(Int(deadlift.rawValue))"
                    filledFields += 1
                }
            }
            else if fieldName.contains("Test_One_Points1[0]") {
                if let deadlift = data.score.eventScores.first(where: { $0.eventName.contains("Deadlift") }) {
                    annotation.widgetStringValue = "\(deadlift.points)"
                    filledFields += 1
                }
            }
            else if fieldName.contains("Test_One_Repetitions[0]") {
                if let pushUp = data.score.eventScores.first(where: { $0.eventName.contains("Push") }) {
                    annotation.widgetStringValue = "\(Int(pushUp.rawValue))"
                    filledFields += 1
                }
            }
            else if fieldName.contains("Test_One_Points3[0]") {
                if let pushUp = data.score.eventScores.first(where: { $0.eventName.contains("Push") }) {
                    annotation.widgetStringValue = "\(pushUp.points)"
                    filledFields += 1
                }
            }
            else if fieldName.contains("Test_One_Time1[0]") {
                if let sdc = data.score.eventScores.first(where: { $0.eventName.contains("Sprint") }) {
                    annotation.widgetStringValue = formatTime(seconds: sdc.rawValue)
                    filledFields += 1
                }
            }
            else if fieldName.contains("Test_One_Points4[0]") {
                if let sdc = data.score.eventScores.first(where: { $0.eventName.contains("Sprint") }) {
                    annotation.widgetStringValue = "\(sdc.points)"
                    filledFields += 1
                }
            }
            else if fieldName.contains("Test_One_Time2[0]") {
                if let plank = data.score.eventScores.first(where: { $0.eventName.contains("Plank") }) {
                    annotation.widgetStringValue = formatTime(seconds: plank.rawValue)
                    filledFields += 1
                }
            }
            else if fieldName.contains("Test_One_Points5[0]") {
                if let plank = data.score.eventScores.first(where: { $0.eventName.contains("Plank") }) {
                    annotation.widgetStringValue = "\(plank.points)"
                    filledFields += 1
                }
            }
            else if fieldName.contains("Test_One_Time3[0]") {
                if let run = data.score.eventScores.first(where: { $0.eventName.contains("2 Mile") }) {
                    annotation.widgetStringValue = formatTime(seconds: run.rawValue)
                    filledFields += 1
                }
            }
            else if fieldName.contains("Test_One_Points6[0]") {
                if let run = data.score.eventScores.first(where: { $0.eventName.contains("2 Mile") }) {
                    annotation.widgetStringValue = "\(run.points)"
                    filledFields += 1
                }
            }
            // Alternate aerobic event fields
            else if fieldName.contains("Test_One_Row_Swim_Bike_Walk[0]") {
                if let alternate = findAlternateAerobicEvent(in: data.score.eventScores) {
                    annotation.widgetStringValue = alternate.eventName
                    filledFields += 1
                }
            }
            else if fieldName.contains("Test_One_Time4[0]") {
                if let alternate = findAlternateAerobicEvent(in: data.score.eventScores) {
                    annotation.widgetStringValue = formatTime(seconds: alternate.rawValue)
                    filledFields += 1
                }
            }
            else if fieldName.contains("Test_One_Points7[0]") {
                if let alternate = findAlternateAerobicEvent(in: data.score.eventScores) {
                    annotation.widgetStringValue = "\(alternate.points)"
                    filledFields += 1
                }
            }
            // Totals
            else if fieldName.contains("Test_One_Total_Points[0]") {
                annotation.widgetStringValue = "\(data.score.totalPoints)"
                filledFields += 1
            }
            // GO / NO GO checkboxes for alternate aerobic event row
            // Only check if an alternate event was used
            else if fieldName.contains("Test_One_Go[0]") && !fieldName.contains("NoGo") && !fieldName.contains("Final") && !fieldName.contains("Weight") && !fieldName.contains("Body_Fat") {
                if let altEvent = findAlternateAerobicEvent(in: data.score.eventScores) {
                    if altEvent.points >= 60 {
                        annotation.buttonWidgetState = .onState
                        filledFields += 1
                    }
                }
            }
            else if fieldName.contains("Test_One_NoGo[0]") && !fieldName.contains("Final") && !fieldName.contains("Weight") && !fieldName.contains("Body_Fat") {
                if let altEvent = findAlternateAerobicEvent(in: data.score.eventScores) {
                    if altEvent.points < 60 {
                        annotation.buttonWidgetState = .onState
                        filledFields += 1
                    }
                }
            }
            // Final GO / NO GO checkboxes for overall test result
            else if fieldName.contains("Test_One_Final_Go[0]") {
                if data.score.passed {
                    annotation.buttonWidgetState = .onState
                    filledFields += 1
                }
            }
            else if fieldName.contains("Test_One_Final_NoGo[0]") {
                if !data.score.passed {
                    annotation.buttonWidgetState = .onState
                    filledFields += 1
                }
            }
            // Grader info
            else if fieldName.contains("OIC_NCOIC_Name_Test_One[0]") {
                annotation.widgetStringValue = data.graderName
                filledFields += 1
            }
            else if fieldName.contains("OIC_NCOIC_Date_Test_One[0]") {
                annotation.widgetStringValue = formattedDate
                filledFields += 1
            }
        }

        print("Form705: Filled \(filledFields) fields")

        // Generate output filename
        let outputURL = getOutputURL(data: data)

        // Remove existing file if present
        try? FileManager.default.removeItem(at: outputURL)

        // Use document.write(to:) to preserve form field editability
        // The "structure tree" warning can be ignored - it doesn't prevent saving
        if document.write(to: outputURL) {
            print("Form705: PDF written using document.write to \(outputURL.path)")

            // Small delay to ensure file system sync
            Thread.sleep(forTimeInterval: 0.1)

            // Verify file was created and has content
            if FileManager.default.fileExists(atPath: outputURL.path) {
                let fileSize = (try? FileManager.default.attributesOfItem(atPath: outputURL.path)[.size] as? Int) ?? 0
                print("Form705: File size: \(fileSize) bytes")
                if fileSize > 1000 {  // Ensure file has meaningful content
                    return .success(outputURL)
                } else {
                    print("Form705: File too small, might be corrupted")
                }
            }
        }

        // Fallback: try dataRepresentation
        print("Form705: document.write failed, trying dataRepresentation...")
        if let pdfData = document.dataRepresentation() {
            do {
                try pdfData.write(to: outputURL)
                print("Form705: PDF written using dataRepresentation to \(outputURL.path)")

                if FileManager.default.fileExists(atPath: outputURL.path) {
                    let fileSize = (try? FileManager.default.attributesOfItem(atPath: outputURL.path)[.size] as? Int) ?? 0
                    print("Form705: File size: \(fileSize) bytes")
                    return .success(outputURL)
                }
            } catch {
                print("Form705: dataRepresentation write failed: \(error)")
            }
        }

        print("Form705: All save methods failed")
        return .failure(Form705Error.cannotSavePDF)
    }

    private static func getOutputURL(data: Form705Data) -> URL {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyyMMdd"
        let fileDate = dateFormatter.string(from: data.testDate)

        let nameParts = data.soldierName.split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces) }
        let lastName = nameParts.first?.uppercased().replacingOccurrences(of: " ", with: "_") ?? "UNKNOWN"
        let firstInitial = nameParts.count > 1 ? String(nameParts[1].prefix(1)).uppercased() : "X"

        let fileName = "DA_Form_705_\(lastName)_\(firstInitial)_\(fileDate).pdf"

        let documentsURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        return documentsURL.appendingPathComponent(fileName)
    }

    private static func formatTime(seconds: Double) -> String {
        let totalSeconds = Int(seconds)
        let minutes = totalSeconds / 60
        let secs = totalSeconds % 60
        return "\(minutes):\(String(format: "%02d", secs))"
    }

    private static func findAlternateAerobicEvent(in eventScores: [SwiftEventScore]) -> SwiftEventScore? {
        return eventScores.first { score in
            score.eventName.contains("Walk") ||
            score.eventName.contains("Row") ||
            score.eventName.contains("Bike") ||
            score.eventName.contains("Swim")
        }
    }
}

enum Form705Error: LocalizedError {
    case templateNotFound
    case cannotLoadTemplate
    case cannotAccessPage
    case cannotSavePDF

    var errorDescription: String? {
        switch self {
        case .templateNotFound:
            return "DA Form 705 template not found. In Xcode: 1) Select da_form_705.pdf in Project Navigator, 2) In File Inspector, check 'Target Membership: iosApp', 3) Clean build (Cmd+Shift+K) and rebuild."
        case .cannotLoadTemplate:
            return "Cannot load DA Form 705 template - the PDF file may be corrupted"
        case .cannotAccessPage:
            return "Cannot access PDF page - template may be invalid"
        case .cannotSavePDF:
            return "Cannot save filled PDF - check device storage"
        }
    }
}
