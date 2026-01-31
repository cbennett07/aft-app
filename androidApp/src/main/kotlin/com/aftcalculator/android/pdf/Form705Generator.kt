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
            android.util.Log.d("Form705", "Loading PDF from assets...")

            val inputStream = context.assets.open("da_form_705.pdf")
            val document = PDDocument.load(inputStream)
            android.util.Log.d("Form705", "PDF loaded, pages: ${document.numberOfPages}")

            val acroForm = document.documentCatalog.acroForm

            if (acroForm != null) {
                android.util.Log.d("Form705", "Starting field collection...")

                // Build a map of all fields by their fully qualified name
                val fieldMap = mutableMapOf<String, PDField>()
                val visited = mutableSetOf<String>()
                collectAllFields(acroForm.fields, fieldMap, visited, 0)

                android.util.Log.d("Form705", "Found ${fieldMap.size} total fields")

                android.util.Log.d("Form705", "Starting form fill...")
                fillForm(fieldMap, data)
                android.util.Log.d("Form705", "Form fill complete")
            } else {
                android.util.Log.w("Form705", "No AcroForm found")
            }

            // Save to cache directory
            val outputFile = File(context.cacheDir, "DA_Form_705_${System.currentTimeMillis()}.pdf")
            android.util.Log.d("Form705", "Saving PDF to: ${outputFile.absolutePath}")

            document.save(outputFile)
            android.util.Log.d("Form705", "PDF saved, closing document...")

            document.close()
            inputStream.close()

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

    private fun fillForm(fieldMap: Map<String, PDField>, data: Form705Data) {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val formattedDate = dateFormat.format(data.testDate)

        android.util.Log.d("Form705", "Filling header fields...")

        // Header fields (Page 1)
        setField(fieldMap, "form1[0].Page1[0].Name[0]", data.soldierName)
        setField(fieldMap, "form1[0].Page1[0].Unit_Location[0]", data.unitLocation)

        android.util.Log.d("Form705", "Filling Test One section...")

        // Test One section
        setField(fieldMap, "form1[0].Page1[0].Test_One_Date[0]", formattedDate)
        setField(fieldMap, "form1[0].Page1[0].Test_One_MOS[0]", data.mos)
        setField(fieldMap, "form1[0].Page1[0].Test_One_Rank_Grade[0]", data.payGrade)
        setField(fieldMap, "form1[0].Page1[0].Test_One_Age[0]", data.score.soldier.age.toString())

        android.util.Log.d("Form705", "Filling event scores...")

        // Fill event scores (text fields only - skip checkboxes for now)
        data.score.eventScores.forEach { eventScore ->
            fillEventScore(fieldMap, eventScore.event, eventScore.rawValue, eventScore.points)
        }

        android.util.Log.d("Form705", "Filling totals...")

        // Total points
        setField(fieldMap, "form1[0].Page1[0].Test_One_Total_Points[0]", data.score.totalPoints.toString())

        // Grader info
        setField(fieldMap, "form1[0].Page1[0].OIC_NCOIC_Name_Test_One[0]", data.graderName)
        setField(fieldMap, "form1[0].Page1[0].OIC_NCOIC_Date_Test_One[0]", formattedDate)

        android.util.Log.d("Form705", "Form fill function complete")
    }

    private fun fillEventScore(fieldMap: Map<String, PDField>, event: AftEvent, rawValue: Double, points: Int) {
        val prefix = "form1[0].Page1[0]."

        when (event) {
            AftEvent.DEADLIFT -> {
                setField(fieldMap, "${prefix}Test_One_First_Attempt[0]", rawValue.toInt().toString())
                // Skip checkbox: setCheckbox(fieldMap, "${prefix}Test_One_First_Attempt_Check[0]", true)
                setField(fieldMap, "${prefix}Test_One_Points1[0]", points.toString())
            }
            AftEvent.PUSH_UP -> {
                setField(fieldMap, "${prefix}Test_One_Repetitions[0]", rawValue.toInt().toString())
                setField(fieldMap, "${prefix}Test_One_Points3[0]", points.toString())
            }
            AftEvent.SPRINT_DRAG_CARRY -> {
                setField(fieldMap, "${prefix}Test_One_Time1[0]", AftCalculator.formatTime(rawValue))
                setField(fieldMap, "${prefix}Test_One_Points4[0]", points.toString())
            }
            AftEvent.PLANK -> {
                setField(fieldMap, "${prefix}Test_One_Time2[0]", AftCalculator.formatTime(rawValue))
                setField(fieldMap, "${prefix}Test_One_Points5[0]", points.toString())
            }
            AftEvent.TWO_MILE_RUN -> {
                setField(fieldMap, "${prefix}Test_One_Time3[0]", AftCalculator.formatTime(rawValue))
                setField(fieldMap, "${prefix}Test_One_Points6[0]", points.toString())
            }
        }
    }

    private fun setField(fieldMap: Map<String, PDField>, fieldName: String, value: String) {
        val field = fieldMap[fieldName]
        if (field != null) {
            try {
                field.setValue(value)
                android.util.Log.d("Form705", "✓ Set '$fieldName' = '$value'")
            } catch (e: Exception) {
                android.util.Log.e("Form705", "✗ Error setting '$fieldName': ${e.message}")
            }
        } else {
            android.util.Log.w("Form705", "✗ Field not found: $fieldName")
        }
    }

    private fun setCheckbox(fieldMap: Map<String, PDField>, fieldName: String, checked: Boolean) {
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
            } catch (e: Exception) {
                android.util.Log.e("Form705", "✗ Error setting checkbox '$fieldName': ${e.message}")
            }
        } else {
            android.util.Log.w("Form705", "✗ Checkbox not found: $fieldName")
        }
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
