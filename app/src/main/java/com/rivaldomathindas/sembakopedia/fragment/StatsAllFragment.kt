package com.rivaldomathindas.sembakopedia.fragment

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.model.DetailProduct
import com.rivaldomathindas.sembakopedia.utils.DayFormatter
import com.rivaldomathindas.sembakopedia.utils.K.STATISTIC_PRODUCT
import com.rivaldomathindas.sembakopedia.utils.MyAxisValueFormatter
import com.rivaldomathindas.sembakopedia.utils.TimeFormatter
import com.rivaldomathindas.sembakopedia.utils.XYMarkerView
import kotlinx.android.synthetic.main.fragment_stats_all.*
import kotlinx.android.synthetic.main.fragment_stats_weekly.*

class StatsAllFragment : Fragment() {
    private var paramStats: DetailProduct? = null

    companion object {
        @JvmStatic
        fun newInstance(paramStats: DetailProduct) =
            StatsAllFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(STATISTIC_PRODUCT, paramStats)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramStats = it.getSerializable(STATISTIC_PRODUCT) as DetailProduct
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats_all, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allChart.setDrawBarShadow(false)
        allChart.setDrawValueAboveBar(true)
        allChart.description.isEnabled = false
        allChart.setPinchZoom(true)
        allChart.setDrawGridBackground(false)
        allChart.setMaxVisibleValueCount(7)

        // mpChart Data
        val yValues = mutableListOf<BarEntry>()
        val xValues = mutableListOf<String>()
        var indexLabel = 0

        val dataStatistics = paramStats?.product?.groupBy { product ->
            product.time?.let { it -> TimeFormatter().getDayWithMonth(it) } }

        dataStatistics?.forEach{ key, value ->
            // list data price
            val priceData = arrayListOf<Float>()
            value.forEach { product ->
                product.price?.toFloat()?.let { priceData.add(it) }
            }

            // average data price if size > 1
            val avgPrice = if (value.size > 1) {
                priceData.sum() / value.size
            } else {
                priceData[0]
            }

            // add y value
            yValues.add(BarEntry(indexLabel.toFloat(), avgPrice))
            // add label with date and indexLabel
            key?.let { xValues.add(it) }
            indexLabel += 1


        }

        // add and custom line dataset
        val barDataSets = BarDataSet(yValues, "")
        barDataSets.color = R.color.colorPrimary
        val dataSets = arrayListOf<IBarDataSet>()
        dataSets.add(barDataSets)
        val data = BarData(dataSets)
        data.setValueTextSize(10f)
        data.barWidth = 0.9f
        allChart.data = data

        //remove rightaxis dan legend
        val rightAxis: YAxis = allChart.axisRight
        rightAxis.isEnabled = false
        val legend = allChart.legend
        legend.isEnabled = false

        // add cubic chart
        val xAxisFormatter = DayFormatter(allChart)
        val xAxis = allChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = xValues.size
        xAxis.valueFormatter = IndexAxisValueFormatter(xValues)

        val custom = MyAxisValueFormatter()
        val leftAxis: YAxis = allChart.axisLeft
        leftAxis.setLabelCount(5, false)
        leftAxis.valueFormatter = custom
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.spaceTop = 15f

        //marker pada chart
        val mv = XYMarkerView(activity, xAxisFormatter)
        mv.chartView = allChart
        allChart.marker = mv
    }
}