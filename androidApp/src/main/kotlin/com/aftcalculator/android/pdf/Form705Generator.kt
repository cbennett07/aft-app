package com.aftcalculator.android.pdf

import android.content.Context
import com.aftcalculator.AftCalculator
import com.aftcalculator.models.AftEvent
import com.aftcalculator.models.AftScore
import com.aftcalculator.models.Gender
import com.aftcalculator.models.MosCategory
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDCheckBox
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDField
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDNonTerminalField
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDTerminalField
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class Form705Data(
    val soldierName: String,
    val graderName: String,
    val unitLocation: String,
    val mos: String,
    val payGrade: String,
    val score: AftScore,
    val testDate: Date = Date()
)

object Form705Generator {

    private var initialized = false

    fun initialize(context: Context) {
        if (!initialized) {
            PDFBoxResourceLoader.init(context)
            initialized = true
        }
    }

    suspend fun generateForm705(
        context: Context,
        data: Form705Data
    ): Result<File> = withContext(Dispatchers.IO) {
        initialize(context)

        try {
            android.util.Log.d("Form705", "Copying PDF from assets to cache...")

            // Copy the PDF to cache first - loading from file is more reliable
            // Always overwrite to ensure we have the latest template
            val templateFile = File(context.cacheDir, "da_form_705_template.pdf")
            context.assets.open("da_form_705.pdf").use { input ->
                templateFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            android.util.Log.d("Form705", "Loading PDF from file...")
            val document = PDDocument.load(templateFile)
            android.util.Log.d("Form705", "PDF loaded, pages: ${document.numberOfPages}")

            val acroForm = document.documentCatalog.acroForm

            if (acroForm != null) {
                android.util.Log.d("Form705", "Configuring AcroForm...")

                // Build a map of all fields by their fully qualified name
                val fieldMap = mutableMapOf<String, PDField>()
                val visited = mutableSetOf<String>()
                collectAllFields(acroForm.fields, fieldMap, visited, 0)

                android.util.Log.d("Form705", "Found ${fieldMap.size} total fields")

                android.util.Log.d("Form705", "Starting form fill...")
                val filledFields = fillForm(fieldMap, data)
                android.util.Log.d("Form705", "Form fill complete, filled ${filledFields.size} fields")

                // Generate appearances only for fields we filled (fast)
                // This keeps the rest of the form editable for graders
                android.util.Log.d("Form705", "Generating appearances for filled fields...")
                filledFields.forEach { field ->
                    try {
                        if (field is PDTerminalField) {
                            field.widgets.forEach { widget ->
                                // Force appearance generation for this widget
                                widget.getCOSObject().setNeedToBeUpdated(true)
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("Form705", "Could not update appearance for ${field.fullyQualifiedName}")
                    }
                }
                android.util.Log.d("Form705", "Appearances generated")
            } else {
                android.util.Log.w("Form705", "No AcroForm found")
            }

            // Save to cache directory with soldier name and date
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
            val fileDate = dateFormat.format(data.testDate)

            // Parse soldier name (expected format: "Last, First, MI" or "Last, First")
            val nameParts = data.soldierName.split(",").map { it.trim() }
            val lastName = nameParts.getOrNull(0)?.uppercase()?.replace(" ", "_") ?: "UNKNOWN"
            val firstInitial = nameParts.getOrNull(1)?.firstOrNull()?.uppercaseChar() ?: 'X'

            val outputFile = File(context.cacheDir, "DA_Form_705_${lastName}_${firstInitial}_${fileDate}.pdf")
            android.util.Log.d("Form705", "Saving PDF to: ${outputFile.absolutePath}")

            // Use OutputStream for more control
            outputFile.outputStream().use { out ->
                document.save(out)
            }
            android.util.Log.d("Form705", "PDF saved, closing document...")

            document.close()

            android.util.Log.d("Form705", "PDF complete, size: ${outputFile.length()} bytes")

            Result.success(outputFile)
        } catch (e: Exception) {
            android.util.Log.e("Form705", "Error generating PDF", e)
            Result.failure(e)
        }
    }

    private fun collectAllFields(
        fields: List<PDField>?,
        fieldMap: MutableMap<String, PDField>,
        visited: MutableSet<String> = mutableSetOf(),
        depth: Int = 0
    ) {
        // Prevent infinite recursion
        if (depth > 10 || fields == null) return

        fields.forEach { field ->
            val name = field.fullyQualifiedName

            // Skip if already visited (prevents circular references)
            if (name in visited) return@forEach
            visited.add(name)

            fieldMap[name] = field

            // If it's a non-terminal field, recurse into children (with depth limit)
            if (field is PDNonTerminalField && depth < 10) {
                collectAllFields(field.children, fieldMap, visited, depth + 1)
            }
        }
    }

    private fun fillForm(fieldMap: Map<String, PDField>, data: Form705Data): List<PDField> {
        val filledFields = mutableListOf<PDField>()
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val formattedDate = dateFormat.format(data.testDate)

        android.util.Log.d("Form705", "Filling header fields...")

        // Header fields (Page 1)
        setField(fieldMap, "form1[0].Page1[0].Name[0]", data.soldierName)?.let { filledFields.add(it) }
        setField(fieldMap, "form1[0].Page1[0].Unit_Location[0]", data.unitLocation)?.let { filledFields.add(it) }

        // Check Combat or General standard based on MOS category
        android.util.Log.d("Form705", "Setting standard checkbox for ${data.score.soldier.mosCategory}...")
        when (data.score.soldier.mosCategory) {
            MosCategory.COMBAT -> {
                setCheckbox(fieldMap, "form1[0].Page1[0].Check_Standard_Combat[0]", true)?.let { filledFields.add(it) }
            }
            MosCategory.COMBAT_ENABLING -> {
                setCheckbox(fieldMap, "form1[0].Page1[0].Check_Standard_General[0]", true)?.let { filledFields.add(it) }
            }
        }

        // Set gender checkbox
        android.util.Log.d("Form705", "Setting gender checkbox for ${data.score.soldier.gender}...")
        when (data.score.soldier.gender) {
            Gender.MALE -> {
                setCheckbox(fieldMap, "form1[0].Page1[0].Male[0]", true)?.let { filledFields.add(it) }
            }
            Gender.FEMALE -> {
                setCheckbox(fieldMap, "form1[0].Page1[0].Female[0]", true)?.let { filledFields.add(it) }
            }
        }

        android.util.Log.d("Form705", "Filling Test One section...")

        // Test One section
        setField(fieldMap, "form1[0].Page1[0].Test_One_Date[0]", formattedDate)?.let { filledFields.add(it) }
        setField(fieldMap, "form1[0].Page1[0].Test_One_MOS[0]", data.mos)?.let { filledFields.add(it) }
        setField(fieldMap, "form1[0].Page1[0].Test_One_Rank_Grade[0]", data.payGrade)?.let { filledFields.add(it) }
        setField(fieldMap, "form1[0].Page1[0].Test_One_Age[0]", data.score.soldier.age.toString())?.let { filledFields.add(it) }

        android.util.Log.d("Form705", "Filling event scores...")

        // Fill event scores (text fields only - skip checkboxes for now)
        data.score.eventScores.forEach { eventScore ->
            filledFields.addAll(fillEventScore(fieldMap, eventScore.event, eventScore.rawValue, eventScore.points))
        }

        android.util.Log.d("Form705", "Filling totals...")

        // Total points
        setField(fieldMap, "form1[0].Page1[0].Test_One_Total_Points[0]", data.score.totalPoints.toString())?.let { filledFields.add(it) }

        // Grader info
        setField(fieldMap, "form1[0].Page1[0].OIC_NCOIC_Name_Test_One[0]", data.graderName)?.let { filledFields.add(it) }
        setField(fieldMap, "form1[0].Page1[0].OIC_NCOIC_Date_Test_One[0]", formattedDate)?.let { filledFields.add(it) }

        android.util.Log.d("Form705", "Form fill function complete")
        return filledFields
    }

    private fun fillEventScore(fieldMap: Map<String, PDField>, event: AftEvent, rawValue: Double, points: Int): List<PDField> {
        val filledFields = mutableListOf<PDField>()
        val prefix = "form1[0].Page1[0]."

        when (event) {
            AftEvent.DEADLIFT -> {
                setField(fieldMap, "${prefix}Test_One_First_Attempt[0]", rawValue.toInt().toString())?.let { filledFields.add(it) }
                // Skip checkbox: setCheckbox(fieldMap, "${prefix}Test_One_First_Attempt_Check[0]", true)
                setField(fieldMap, "${prefix}Test_One_Points1[0]", points.toString())?.let { filledFields.add(it) }
            }
            AftEvent.PUSH_UP -> {
                setField(fieldMap, "${prefix}Test_One_Repetitions[0]", rawValue.toInt().toString())?.let { filledFields.add(it) }
                setField(fieldMap, "${prefix}Test_One_Points3[0]", points.toString())?.let { filledFields.add(it) }
            }
            AftEvent.SPRINT_DRAG_CARRY -> {
                setField(fieldMap, "${prefix}Test_One_Time1[0]", AftCalculator.formatTime(rawValue))?.let { filledFields.add(it) }
                setField(fieldMap, "${prefix}Test_One_Points4[0]", points.toString())?.let { filledFields.add(it) }
            }
            AftEvent.PLANK -> {
                setField(fieldMap, "${prefix}Test_One_Time2[0]", AftCalculator.formatTime(rawValue))?.let { filledFields.add(it) }
                setField(fieldMap, "${prefix}Test_One_Points5[0]", points.toString())?.let { filledFields.add(it) }
            }
            AftEvent.TWO_MILE_RUN -> {
                setField(fieldMap, "${prefix}Test_One_Time3[0]", AftCalculator.formatTime(rawValue))?.let { filledFields.add(it) }
                setField(fieldMap, "${prefix}Test_One_Points6[0]", points.toString())?.let { filledFields.add(it) }
            }
            // Alternate aerobic events use Row_Swim_Bike_Walk field
            AftEvent.WALK_2_5_MILE, AftEvent.ROW_5K, AftEvent.BIKE_12K, AftEvent.SWIM_1K -> {
                // Fill the alternate event description and time
                val eventDesc = when (event) {
                    AftEvent.WALK_2_5_MILE -> "2.5 MI WALK"
                    AftEvent.ROW_5K -> "5K ROW"
                    AftEvent.BIKE_12K -> "12K BIKE"
                    AftEvent.SWIM_1K -> "1K SWIM"
                    else -> event.displayName
                }
                setField(fieldMap, "${prefix}Test_One_Row_Swim_Bike_Walk[0]", eventDesc)?.let { filledFields.add(it) }
                if (rawValue > 0) {
                    setField(fieldMap, "${prefix}Test_One_Time4[0]", AftCalculator.formatTime(rawValue))?.let { filledFields.add(it) }
                }
                setField(fieldMap, "${prefix}Test_One_Points7[0]", points.toString())?.let { filledFields.add(it) }
            }
        }
        return filledFields
    }

    private fun setField(fieldMap: Map<String, PDField>, fieldName: String, value: String): PDField? {
        val field = fieldMap[fieldName]
        if (field != null) {
            try {
                field.setValue(value)
                android.util.Log.d("Form705", "✓ Set '$fieldName' = '$value'")
                return field
            } catch (e: Exception) {
                android.util.Log.e("Form705", "✗ Error setting '$fieldName': ${e.message}")
            }
        } else {
            android.util.Log.w("Form705", "✗ Field not found: $fieldName")
        }
        return null
    }

    private fun setCheckbox(fieldMap: Map<String, PDField>, fieldName: String, checked: Boolean): PDField? {
        val field = fieldMap[fieldName]
        if (field != null) {
            try {
                if (field is PDCheckBox) {
                    if (checked) field.check() else field.unCheck()
                    android.util.Log.d("Form705", "✓ Checkbox '$fieldName' = $checked")
                } else {
                    // For radio buttons or other button types, try setting value
                    val onValue = getOnValue(field)
                    field.setValue(if (checked) onValue else "Off")
                    android.util.Log.d("Form705", "✓ Button '$fieldName' = ${if (checked) onValue else "Off"}")
                }
                return field
            } catch (e: Exception) {
                android.util.Log.e("Form705", "✗ Error setting checkbox '$fieldName': ${e.message}")
            }
        } else {
            android.util.Log.w("Form705", "✗ Checkbox not found: $fieldName")
        }
        return null
    }

    private fun getOnValue(field: PDField): String {
        // Try to get the "on" value for a checkbox/radio button
        return try {
            if (field is PDTerminalField) {
                val widgets = field.widgets
                if (widgets.isNotEmpty()) {
                    val appearance = widgets[0].appearance
                    val normalAppearance = appearance?.normalAppearance
                    if (normalAppearance != null && !normalAppearance.isSubDictionary) {
                        // Get available appearance states
                        val dict = normalAppearance
                        // Return first non-Off value
                        return "Yes" // Default fallback
                    }
                }
            }
            "Yes"
        } catch (e: Exception) {
            "Yes"
        }
    }

    fun getFormFieldNames(context: Context): List<String> {
        initialize(context)

        return try {
            val inputStream = context.assets.open("da_form_705.pdf")
            val document = PDDocument.load(inputStream)
            val acroForm = document.documentCatalog.acroForm

            val fieldMap = mutableMapOf<String, PDField>()
            val visited = mutableSetOf<String>()
            collectAllFields(acroForm?.fields, fieldMap, visited, 0)

            document.close()
            inputStream.close()

            fieldMap.keys.sorted()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
