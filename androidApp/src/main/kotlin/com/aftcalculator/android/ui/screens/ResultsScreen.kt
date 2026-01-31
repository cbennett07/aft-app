package com.aftcalculator.android.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.aftcalculator.android.pdf.Form705Data
import com.aftcalculator.android.pdf.Form705Generator
import com.aftcalculator.android.ui.components.ScoreResultCard
import com.aftcalculator.android.ui.theme.ArmyGold
import com.aftcalculator.android.ui.theme.ArmyBlack
import com.aftcalculator.android.ui.theme.ArmyDarkGray
import com.aftcalculator.models.AftScore
import com.aftcalculator.models.MosCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    score: AftScore,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Dialog state
    var showForm705Dialog by remember { mutableStateOf(false) }
    var soldierName by remember { mutableStateOf("") }
    var unitLocation by remember { mutableStateOf("") }
    var mos by remember { mutableStateOf("") }
    var payGrade by remember { mutableStateOf("") }
    var graderName by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ArmyBlack,
                        ArmyDarkGray
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Custom Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArmyBlack)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ArmyGold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AFT RESULTS",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
                IconButton(onClick = onReset) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Start Over",
                        tint = ArmyGold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ScoreResultCard(score = score)

                Spacer(modifier = Modifier.height(24.dp))

                // Generate DA Form 705 Button
                Button(
                    onClick = { showForm705Dialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.1f),
                        contentColor = ArmyGold
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(
                            listOf(ArmyGold.copy(alpha = 0.5f), ArmyGold.copy(alpha = 0.5f))
                        )
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "GENERATE DA FORM 705",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.3f))
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "EDIT SCORES",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Button(
                        onClick = onReset,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArmyGold,
                            contentColor = ArmyBlack
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "START OVER",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Info section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "SCORING INFORMATION",
                            style = MaterialTheme.typography.labelMedium,
                            color = ArmyGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val fullMinimum = score.soldier.mosCategory.minimumTotal
                        val minimumTotal = if (score.eventScores.size < 5) {
                            (fullMinimum * score.eventScores.size) / 5
                        } else {
                            fullMinimum
                        }

                        InfoRow(label = "Category", value = score.soldier.mosCategory.displayName)
                        InfoRow(label = "Minimum per event", value = "60 points")
                        InfoRow(label = "Events taken", value = "${score.eventScores.size}")
                        InfoRow(label = "Minimum total", value = "$minimumTotal points")
                        InfoRow(label = "Age bracket", value = score.soldier.ageBracket.displayName)
                        InfoRow(
                            label = "Scoring type",
                            value = if (score.soldier.mosCategory == MosCategory.COMBAT) {
                                "Sex-neutral"
                            } else {
                                "Sex and age-normed"
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Based on official HQDA EXORD 218-25 scoring tables",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // DA Form 705 Dialog
    if (showForm705Dialog) {
        Dialog(onDismissRequest = { if (!isGenerating) showForm705Dialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ArmyDarkGray
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Text(
                        text = "GENERATE DA FORM 705",
                        style = MaterialTheme.typography.titleMedium,
                        color = ArmyGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = soldierName,
                        onValueChange = { soldierName = it },
                        label = { Text("Soldier Name (Last, First, MI)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ArmyGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = ArmyGold,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                            cursorColor = ArmyGold
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = unitLocation,
                        onValueChange = { unitLocation = it },
                        label = { Text("Unit/Location") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ArmyGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = ArmyGold,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                            cursorColor = ArmyGold
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = mos,
                            onValueChange = { mos = it },
                            label = { Text("MOS") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ArmyGold,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = ArmyGold,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                                cursorColor = ArmyGold
                            )
                        )

                        OutlinedTextField(
                            value = payGrade,
                            onValueChange = { payGrade = it },
                            label = { Text("Pay Grade") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ArmyGold,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = ArmyGold,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                                cursorColor = ArmyGold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = graderName,
                        onValueChange = { graderName = it },
                        label = { Text("Grader Name (Last, First, MI)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ArmyGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = ArmyGold,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                            cursorColor = ArmyGold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Exempt events will be left blank. No signatures will be applied.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showForm705Dialog = false },
                            modifier = Modifier.weight(1f),
                            enabled = !isGenerating,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(
                                    listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.3f))
                                )
                            )
                        ) {
                            Text("CANCEL")
                        }

                        Button(
                            onClick = {
                                isGenerating = true
                                scope.launch {
                                    try {
                                        val formData = Form705Data(
                                            soldierName = soldierName,
                                            graderName = graderName,
                                            unitLocation = unitLocation,
                                            mos = mos,
                                            payGrade = payGrade,
                                            score = score
                                        )

                                        android.util.Log.d("Form705UI", "Starting PDF generation...")

                                        val result = Form705Generator.generateForm705(context, formData)

                                        isGenerating = false

                                        result.onSuccess { pdfFile ->
                                            android.util.Log.d("Form705UI", "PDF generated: ${pdfFile.absolutePath}, exists: ${pdfFile.exists()}, size: ${pdfFile.length()}")
                                            showForm705Dialog = false

                                            // Share the PDF
                                            try {
                                                val uri = FileProvider.getUriForFile(
                                                    context,
                                                    "${context.packageName}.fileprovider",
                                                    pdfFile
                                                )
                                                android.util.Log.d("Form705UI", "FileProvider URI: $uri")

                                                // Try to open/view the PDF first
                                                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                                                    setDataAndType(uri, "application/pdf")
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }

                                                // Check if there's an app to view PDFs
                                                if (viewIntent.resolveActivity(context.packageManager) != null) {
                                                    context.startActivity(viewIntent)
                                                } else {
                                                    // Fallback to share chooser
                                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                        type = "application/pdf"
                                                        putExtra(Intent.EXTRA_STREAM, uri)
                                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                    }
                                                    context.startActivity(
                                                        Intent.createChooser(shareIntent, "Share DA Form 705")
                                                    )
                                                }
                                            } catch (e: Exception) {
                                                android.util.Log.e("Form705UI", "Error sharing PDF", e)
                                                Toast.makeText(
                                                    context,
                                                    "Error sharing PDF: ${e.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }.onFailure { error ->
                                            android.util.Log.e("Form705UI", "PDF generation failed", error)
                                            Toast.makeText(
                                                context,
                                                "Error generating PDF: ${error.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        isGenerating = false
                                        android.util.Log.e("Form705UI", "Unexpected error", e)
                                        Toast.makeText(
                                            context,
                                            "Unexpected error: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isGenerating && soldierName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ArmyGold,
                                contentColor = ArmyBlack
                            )
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = ArmyBlack,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("GENERATE")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}
