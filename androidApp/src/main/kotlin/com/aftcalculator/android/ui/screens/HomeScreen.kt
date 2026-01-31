package com.aftcalculator.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aftcalculator.android.ui.theme.ArmyGold
import com.aftcalculator.android.ui.theme.ArmyBlack
import com.aftcalculator.android.ui.theme.ArmyDarkGray
import com.aftcalculator.android.viewmodels.CalculatorUiState
import com.aftcalculator.models.Gender
import com.aftcalculator.models.MosCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: CalculatorUiState,
    onAgeChange: (Int) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onMosCategoryChange: (MosCategory) -> Unit,
    onStartCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Title with gold accent
            Text(
                text = "ARMY",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = ArmyGold,
                letterSpacing = 8.sp
            )

            Text(
                text = "FITNESS TEST",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 4.sp
            )

            Text(
                text = "CALCULATOR",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Effective date badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(ArmyGold.copy(alpha = 0.2f))
                    .border(1.dp, ArmyGold, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "EFFECTIVE JUNE 2025",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArmyGold,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Soldier Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.05f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "SOLDIER INFORMATION",
                        style = MaterialTheme.typography.titleSmall,
                        color = ArmyGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Age
                    Text(
                        text = "AGE: ${uiState.age}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Slider(
                        value = uiState.age.toFloat(),
                        onValueChange = { onAgeChange(it.toInt()) },
                        valueRange = 17f..70f,
                        steps = 52,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = ArmyGold,
                            activeTrackColor = ArmyGold,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gender
                    Text(
                        text = "GENDER",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Gender.entries.forEach { gender ->
                            SelectionButton(
                                text = gender.name,
                                selected = uiState.gender == gender,
                                onClick = { onGenderChange(gender) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // MOS Category
                    Text(
                        text = "MOS CATEGORY",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    MosCategory.entries.forEach { category ->
                        MosCategoryCard(
                            category = category,
                            selected = uiState.mosCategory == category,
                            onClick = { onMosCategoryChange(category) }
                        )
                        if (category != MosCategory.entries.last()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Start Button
            Button(
                onClick = onStartCalculator,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ArmyGold,
                    contentColor = ArmyBlack
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "ENTER EVENT SCORES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Based on official HQDA EXORD 218-25 scoring tables",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SelectionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) ArmyGold else Color.Transparent
    val contentColor = if (selected) ArmyBlack else Color.White
    val borderColor = if (selected) ArmyGold else Color.White.copy(alpha = 0.3f)

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.linearGradient(listOf(borderColor, borderColor))
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun MosCategoryCard(
    category: MosCategory,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) ArmyGold.copy(alpha = 0.15f) else Color.Transparent
    val borderColor = if (selected) ArmyGold else Color.White.copy(alpha = 0.2f)

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = category.displayName.uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selected) ArmyGold else Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Min: ${category.minimumPerEvent}/event • ${category.minimumTotal} total",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ArmyGold),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = ArmyBlack,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
