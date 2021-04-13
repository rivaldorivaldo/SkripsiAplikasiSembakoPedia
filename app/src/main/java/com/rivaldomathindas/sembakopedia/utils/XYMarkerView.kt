package com.rivaldomathindas.sembakopedia.utils


import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.rivaldomathindas.sembakopedia.R
import java.text.DecimalFormat


@SuppressLint("ViewConstructor")
class XYMarkerView(
    context: Context?,
    private val xAxisValueFormatter: IAxisValueFormatter
) :
    MarkerView(context, R.layout.custom_marker_view) {
    private val tvContent: TextView
    private val format: DecimalFormat

    override fun refreshContent(
        e: Entry,
        highlight: Highlight
    ) {
        tvContent.text = String.format(
            "Hari ke-%s, Harga: %s",
            xAxisValueFormatter.getFormattedValue(e.x, null),
            format.format(e.y.toDouble())
        )
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }

    init {
        tvContent = findViewById(R.id.tvContent)
        format = DecimalFormat("###")
    }
}
