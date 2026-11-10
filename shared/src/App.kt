package com.example.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.demo.icons.AppIcons
import com.example.demo.icons.Height
import com.example.demo.icons.MonitorWeight
import com.example.demo.resources.*
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.jetbrains.compose.resources.stringResource

/**
 * Reusable, validated number input field with leading icon and range feedback.
 */
@Composable
fun EditNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    minValue: Double,
    maxValue: Double,
    unit: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val numericValue = value.replace(',', '.').toDoubleOrNull()
    val isOutOfRange = numericValue != null && (numericValue !in minValue..maxValue)

    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            // Allow only digits and at most one decimal separator (dot or comma)
            val normalized = input.replace(',', '.')
            val validChars = input.all { it.isDigit() || it == '.' || it == ',' }
            val singleSeparator = normalized.count { it == '.' } <= 1
            if (validChars && singleSeparator) {
                onValueChange(input)
            }
        },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon,
        singleLine = true,
        isError = isOutOfRange,
        supportingText = {
            if (isOutOfRange) {
                val minFormatted = if (minValue % 1.0 == 0.0) minValue.toInt().toString() else minValue.toString()
                val maxFormatted = if (maxValue % 1.0 == 0.0) maxValue.toInt().toString() else maxValue.toString()
                Text(
                    text = stringResource(Res.string.value_out_of_range, minFormatted, maxFormatted, unit),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = imeAction
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun BmiScreen(
    modifier: Modifier = Modifier
) {
    // State management for inputs using rememberSaveable to preserve state on configuration change
    var lengthInput by rememberSaveable { mutableStateOf("") }
    var weightInput by rememberSaveable { mutableStateOf("") }

    // Parse and calculate BMI reactively via pure calculation function
    val length = lengthInput.replace(',', '.').toDoubleOrNull()
    val weight = weightInput.replace(',', '.').toDoubleOrNull()

    val bmiResult = remember(length, weight) {
        calculateBmi(length, weight)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            PlatformBanner()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Text(
                text = stringResource(Res.string.screen_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            )

            // Reusable Input: Length in cm
            EditNumberField(
                value = lengthInput,
                onValueChange = { lengthInput = it },
                label = stringResource(Res.string.length_label, MIN_HEIGHT_CM.toInt(), MAX_HEIGHT_CM.toInt()),
                placeholder = stringResource(Res.string.length_placeholder),
                minValue = MIN_HEIGHT_CM,
                maxValue = MAX_HEIGHT_CM,
                unit = stringResource(Res.string.unit_cm),
                imeAction = ImeAction.Next,
                leadingIcon = {
                    Icon(
                        imageVector = AppIcons.Height,
                        contentDescription = stringResource(Res.string.length_icon_content_description)
                    )
                }
            )

            // Reusable Input: Weight in kg
            EditNumberField(
                value = weightInput,
                onValueChange = { weightInput = it },
                label = stringResource(Res.string.weight_label, MIN_WEIGHT_KG.toInt(), MAX_WEIGHT_KG.toInt()),
                placeholder = stringResource(Res.string.weight_placeholder),
                minValue = MIN_WEIGHT_KG,
                maxValue = MAX_WEIGHT_KG,
                unit = stringResource(Res.string.unit_kg),
                imeAction = ImeAction.Done,
                leadingIcon = {
                    Icon(
                        imageVector = AppIcons.MonitorWeight,
                        contentDescription = stringResource(Res.string.weight_icon_content_description)
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // BMI Output Section (conditional rendering between result card and placeholder)
            BmiResultSection(bmi = bmiResult)

            // Legend for BMI color scheme indication
            BmiLegendCard(modifier = Modifier.padding(top = 8.dp))
        }
    }
}

/**
 * Small bottom banner displaying the current platform using expect/actual [getPlatform].
 */
@Composable
fun PlatformBanner(
    modifier: Modifier = Modifier,
    platformName: String = remember { getPlatform().name }
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        tonalElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(Res.string.running_on_platform, platformName),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}

/**
 * Helper Composable demonstrating conditional rendering based on state:
 * Displays either the active BMI calculation result card or an instructional placeholder card.
 */
@Composable
fun BmiResultSection(
    bmi: Double?,
    modifier: Modifier = Modifier
) {
    if (bmi != null) {
        val categoryInfo = getBmiCategoryInfo(bmi)
        BmiResultCard(bmi = bmi, categoryInfo = categoryInfo, modifier = modifier)
    } else {
        BmiPlaceholderCard(modifier = modifier)
    }
}

/**
 * Helper Composable for displaying calculated BMI, status badge, and descriptive guidance.
 */
@Composable
fun BmiResultCard(
    bmi: Double,
    categoryInfo: BmiCategoryInfo,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = categoryInfo.containerColor
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.your_bmi),
                style = MaterialTheme.typography.titleMedium,
                color = categoryInfo.contentColor
            )

            Text(
                text = formatBmi(bmi),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = categoryInfo.indicatorColor
            )

            // Status Badge
            Surface(
                shape = RoundedCornerShape(50),
                color = categoryInfo.indicatorColor,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(categoryInfo.category.categoryRes),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Text(
                text = stringResource(categoryInfo.category.descriptionRes),
                style = MaterialTheme.typography.bodyMedium,
                color = categoryInfo.contentColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Helper Composable displaying helpful placeholder text when inputs are incomplete or invalid.
 */
@Composable
fun BmiPlaceholderCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.placeholder_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(Res.string.placeholder_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Helper Composable displaying the reference color-coded legend for all BMI categories.
 */
@Composable
fun BmiLegendCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(Res.string.legend_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            BmiCategory.entries.forEach { category ->
                BmiLegendItem(
                    color = category.indicatorColor,
                    label = stringResource(category.legendLabelRes),
                    range = stringResource(category.rangeRes)
                )
            }
        }
    }
}

@Composable
fun BmiLegendItem(
    color: Color,
    label: String,
    range: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = range,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
@Preview
@DevelopmentEntryPoint
fun App() {
    MaterialTheme {
        BmiScreen()
    }
}

@Composable
@Preview
fun BmiResultCardPreview() {
    MaterialTheme {
        val bmi = 22.5
        BmiResultCard(
            bmi = bmi,
            categoryInfo = getBmiCategoryInfo(bmi),
            modifier = Modifier.padding(16.dp)
        )
    }
}
