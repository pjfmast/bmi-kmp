package com.example.demo

import androidx.compose.ui.graphics.Color
import com.example.demo.resources.*
import kotlin.math.abs
import kotlin.math.round
import org.jetbrains.compose.resources.StringResource

// Boundary constants for input validation
const val MIN_HEIGHT_CM = 100.0
const val MAX_HEIGHT_CM = 300.0
const val MIN_WEIGHT_KG = 10.0
const val MAX_WEIGHT_KG = 500.0

/**
 * Enumeration of BMI status categories with corresponding multiplatform string resources and colors.
 */
enum class BmiCategory(
    val categoryRes: StringResource,
    val descriptionRes: StringResource,
    val legendLabelRes: StringResource,
    val rangeRes: StringResource,
    val indicatorColor: Color,
    val containerColor: Color,
    val contentColor: Color
) {
    UNDERWEIGHT(
        categoryRes = Res.string.category_underweight,
        descriptionRes = Res.string.category_underweight_desc,
        legendLabelRes = Res.string.category_underweight,
        rangeRes = Res.string.category_underweight_range,
        indicatorColor = Color(0xFF0288D1),
        containerColor = Color(0xFFE1F5FE),
        contentColor = Color(0xFF01579B)
    ),
    NORMAL(
        categoryRes = Res.string.category_normal,
        descriptionRes = Res.string.category_normal_desc,
        legendLabelRes = Res.string.category_normal_legend,
        rangeRes = Res.string.category_normal_range,
        indicatorColor = Color(0xFF2E7D32),
        containerColor = Color(0xFFE8F5E9),
        contentColor = Color(0xFF1B5E20)
    ),
    OVERWEIGHT(
        categoryRes = Res.string.category_overweight,
        descriptionRes = Res.string.category_overweight_desc,
        legendLabelRes = Res.string.category_overweight,
        rangeRes = Res.string.category_overweight_range,
        indicatorColor = Color(0xFFF57C00),
        containerColor = Color(0xFFFFF3E0),
        contentColor = Color(0xFFE65100)
    ),
    OBESE(
        categoryRes = Res.string.category_obese,
        descriptionRes = Res.string.category_obese_desc,
        legendLabelRes = Res.string.category_obese_legend,
        rangeRes = Res.string.category_obese_range,
        indicatorColor = Color(0xFFD32F2F),
        containerColor = Color(0xFFFFEBEE),
        contentColor = Color(0xFFB71C1C)
    )
}

/**
 * Data class representing BMI status categories with corresponding colors and descriptions.
 */
data class BmiCategoryInfo(
    val category: BmiCategory,
    val indicatorColor: Color = category.indicatorColor,
    val containerColor: Color = category.containerColor,
    val contentColor: Color = category.contentColor
)

/**
 * Pure calculation function for BMI with boundary checks.
 * Returns null if height or weight is null or outside realistic physiological bounds.
 */
fun calculateBmi(heightCm: Double?, weightKg: Double?): Double? {
    if (heightCm == null || weightKg == null) return null
    if (heightCm !in MIN_HEIGHT_CM..MAX_HEIGHT_CM || weightKg !in MIN_WEIGHT_KG..MAX_WEIGHT_KG) {
        return null
    }
    val heightInMeters = heightCm / 100.0
    return weightKg / (heightInMeters * heightInMeters)
}

/**
 * Formats a BMI value to one decimal place in a platform-independent way.
 */
fun formatBmi(bmi: Double): String {
    val tenths = round(bmi * 10.0).toLong()
    return "${tenths / 10}.${abs(tenths % 10)}"
}

/**
 * Determines the BMI status and color scheme based on the BMI value.
 */
fun getBmiCategoryInfo(bmi: Double): BmiCategoryInfo {
    val category = when {
        bmi < 18.5 -> BmiCategory.UNDERWEIGHT
        bmi < 25.0 -> BmiCategory.NORMAL
        bmi < 30.0 -> BmiCategory.OVERWEIGHT
        else -> BmiCategory.OBESE
    }
    return BmiCategoryInfo(category = category)
}
