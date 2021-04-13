package com.rivaldomathindas.sembakopedia.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.tabs.TabLayout
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.fragment.StatsAllFragment
import com.rivaldomathindas.sembakopedia.fragment.StatsMonthlyFragment
import com.rivaldomathindas.sembakopedia.fragment.StatsWeeklyFragment
import com.rivaldomathindas.sembakopedia.model.DetailProduct
import com.rivaldomathindas.sembakopedia.utils.*
import kotlinx.android.synthetic.main.activity_detail_statistics.*

class DetailStatisticsActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {
    private lateinit var detailProduct: DetailProduct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_statistics)

        initViews()
        setupViewPager()
        setupPredictions()
    }

    //pengaturan view
    private fun initViews() {
        detailProduct = intent.getSerializableExtra(K.TYPE_PRODUCT) as DetailProduct

        setSupportActionBar(toolbarDetailStatistics)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = detailProduct.type.name

        tvStatistics.text = getString(R.string.statistic, detailProduct.type.name)
        tvPrediction.text = getString(R.string.prediction, detailProduct.type.name)
    }

    //chart prediksi harga
    private fun setupPredictions() {

        //setup tampilan chart
        predictionChart.setDrawBarShadow(false)
        predictionChart.setDrawValueAboveBar(true)
        predictionChart.description.isEnabled = false
        predictionChart.setPinchZoom(true)
        predictionChart.setDrawGridBackground(false)
        predictionChart.setMaxVisibleValueCount(7)

        //setup dan deklarasi data
        val predictionPrice = mutableListOf<BarEntry>()
        val predictionDate = mutableListOf<String>()

        val alpha = 0.9f
        val historicalDailyData = detailProduct.product.groupBy { product ->
            product.time?.let {  TimeFormatter().getNormalSecondYear(it) }
        }
        var forecastingPrice: Float
        var forecastForTommorow : Float
        val tommorowPrice : Float
        val lastKey = historicalDailyData.keys.first()
        var indexDate = 0

        fun actualPriceInDay(n : Int) : Float {
            val listProduct = historicalDailyData.values.elementAt(n)
            val prices = arrayListOf<Float>()

            listProduct.forEach { product ->
                product.price?.toFloat()?.let { prices.add(it) }
            }

            return if (listProduct.size > 1) {
                prices.sum() / listProduct.size
            } else {
                prices[0]
            }
        }

        forecastingPrice = actualPriceInDay(historicalDailyData.size-1)

        for (i in historicalDailyData.size-1 downTo 0){
            forecastingPrice += (alpha * (actualPriceInDay(i) - forecastingPrice))
        }

        forecastForTommorow = actualPriceInDay(0)

        tommorowPrice = forecastingPrice + (alpha * (forecastForTommorow - forecastingPrice))
        val textTomorrowPrediciton = getString(R.string.tommorrow_prediction) + " " + tommorowPrice
        tvTommorrowPrediction.text = textTomorrowPrediciton

        for(i in 1..30){
            lastKey?.let { TimeFormatter().getNextDay(it, indexDate) }?.let { predictionDate.add(it) }
            predictionPrice.add(BarEntry(indexDate.toFloat(), forecastForTommorow))

            forecastForTommorow = forecastingPrice + (alpha * (forecastForTommorow - forecastingPrice))

            indexDate +=1
        }

        //memasukan data kedalam chart
        val barDataSets = BarDataSet(predictionPrice, "")
        barDataSets.color = R.color.colorPrimary
        val dataSets = arrayListOf<IBarDataSet>()
        dataSets.add(barDataSets)
        val data = BarData(dataSets)
        data.setValueTextSize(10f)
        data.barWidth = 0.9f
        predictionChart.data = data

        //tampilan axis x
        val xAxisFormatter = DayFormatter(predictionChart)
        val xAxis: XAxis = predictionChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = predictionDate.size
        xAxis.valueFormatter = IndexAxisValueFormatter(predictionDate)

        //tampilan axis y
        val custom = MyAxisValueFormatter()
        val leftAxis: YAxis = predictionChart.axisLeft
        leftAxis.setLabelCount(7, false)
        leftAxis.valueFormatter = custom
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.spaceTop = 15f

        //remove rightaxis dan legend
        val rightAxis: YAxis = predictionChart.axisRight
        rightAxis.isEnabled = false
        val legend = predictionChart.legend
        legend.isEnabled = false

        //marker pada chart
        val mv = XYMarkerView(this, xAxisFormatter)
        mv.chartView = predictionChart
        predictionChart.marker = mv
    }

    //fragment chart statistik harga
    private fun setupViewPager() {
        val adapter = PagerAdapter(supportFragmentManager, this)
        val weeklyFragment = StatsWeeklyFragment.newInstance(detailProduct)
        val monthlyFragment = StatsMonthlyFragment.newInstance(detailProduct)
        val allFragment = StatsAllFragment.newInstance(detailProduct)

        adapter.addFragment(weeklyFragment)
        adapter.addFragment(monthlyFragment)
        adapter.addFragment(allFragment)

        vp_statistics_detail.adapter = adapter
        vp_statistics_detail.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_statistics))
        tab_statistics.addOnTabSelectedListener(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.animateEnterLeft(this)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab != null)
            vp_statistics_detail.currentItem = tab.position
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun onTabReselected(tab: TabLayout.Tab?) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}