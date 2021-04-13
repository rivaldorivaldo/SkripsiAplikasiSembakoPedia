@file:Suppress("DEPRECATION")

package com.rivaldomathindas.sembakopedia.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

import java.text.DecimalFormat

@Suppress("DEPRECATION")
class MyAxisValueFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        return "Rp " + value.toInt()
    }
}
