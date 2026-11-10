package com.example.demo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BmiTest {

    @Test
    fun testBmiCalculation_validInputs() {
        // Standard normal BMI calculation: 180cm, 75kg -> 75 / (1.8 * 1.8) = 23.148...
        val bmi = calculateBmi(180.0, 75.0)
        assertNotNull(bmi)
        assertEquals(23.15, bmi, 0.01)

        // Decimal height and weight: 175.5cm, 68.2kg
        val bmiDecimal = calculateBmi(175.5, 68.2)
        assertNotNull(bmiDecimal)
        assertEquals(22.14, bmiDecimal, 0.01)
    }

    @Test
    fun testBmiCalculation_boundaryLimits() {
        // Lower boundaries: 100cm, 10kg -> 10 / (1.0 * 1.0) = 10.0
        val lowerBmi = calculateBmi(100.0, 10.0)
        assertNotNull(lowerBmi)
        assertEquals(10.0, lowerBmi, 0.001)

        // Upper boundaries: 300cm, 500kg -> 500 / (3.0 * 3.0) = 55.555...
        val upperBmi = calculateBmi(300.0, 500.0)
        assertNotNull(upperBmi)
        assertEquals(55.56, upperBmi, 0.01)
    }

    @Test
    fun testBmiCalculation_outOfRangeAndInvalidInputs() {
        // Height below minimum (< 100 cm)
        assertNull(calculateBmi(99.9, 70.0))
        assertNull(calculateBmi(0.0, 70.0))
        assertNull(calculateBmi(-175.0, 70.0))

        // Height above maximum (> 300 cm)
        assertNull(calculateBmi(300.1, 70.0))

        // Weight below minimum (< 10 kg)
        assertNull(calculateBmi(175.0, 9.9))
        assertNull(calculateBmi(175.0, 0.0))
        assertNull(calculateBmi(175.0, -5.0))

        // Weight above maximum (> 500 kg)
        assertNull(calculateBmi(175.0, 500.1))

        // Null inputs
        assertNull(calculateBmi(null, 70.0))
        assertNull(calculateBmi(175.0, null))
        assertNull(calculateBmi(null, null))
    }

    @Test
    fun testBmiCategoryInfo_classification() {
        // Underweight: < 18.5
        val under = getBmiCategoryInfo(17.5)
        assertEquals(BmiCategory.UNDERWEIGHT, under.category)

        // Normal / Good BMI: 18.5 - 24.9
        val normalMin = getBmiCategoryInfo(18.5)
        assertEquals(BmiCategory.NORMAL, normalMin.category)
        val normal = getBmiCategoryInfo(22.0)
        assertEquals(BmiCategory.NORMAL, normal.category)
        val normalMax = getBmiCategoryInfo(24.9)
        assertEquals(BmiCategory.NORMAL, normalMax.category)

        // Overweight: 25.0 - 29.9
        val overMin = getBmiCategoryInfo(25.0)
        assertEquals(BmiCategory.OVERWEIGHT, overMin.category)
        val over = getBmiCategoryInfo(27.5)
        assertEquals(BmiCategory.OVERWEIGHT, over.category)

        // Obese: >= 30.0
        val obeseMin = getBmiCategoryInfo(30.0)
        assertEquals(BmiCategory.OBESE, obeseMin.category)
        val obese = getBmiCategoryInfo(35.0)
        assertEquals(BmiCategory.OBESE, obese.category)
    }

    @Test
    fun testFormatBmi() {
        assertEquals("23.1", formatBmi(23.148))
        assertEquals("20.0", formatBmi(20.0))
        assertEquals("19.0", formatBmi(18.96))
    }
}
