package com.rivaldomathindas.sembakopedia.adapter

import android.graphics.Color
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.callbacks.TypeCallback
import com.rivaldomathindas.sembakopedia.databinding.ItemTypeStatisticsBinding
import com.rivaldomathindas.sembakopedia.model.DetailProduct
import com.rivaldomathindas.sembakopedia.model.Product
import com.rivaldomathindas.sembakopedia.model.Type
import com.rivaldomathindas.sembakopedia.utils.*
import kotlinx.android.synthetic.main.fragment_stats_weekly.*
import kotlinx.android.synthetic.main.item_type_statistics.view.*

class TypeAdapter(private val callback: TypeCallback) :
    RecyclerView.Adapter<TypeAdapter.TypeHolder>() {

    private val types = mutableListOf<Type>()
    private val products = mutableListOf<Product>()

    fun addType(type: ArrayList<Type>) {
        types.addAll(type)
        notifyDataSetChanged()
    }

    fun addProduct(product: Product) {
        products.add(product)
        notifyItemInserted(products.size - 1)
    }

    fun clearProducts() {
        products.clear()
        notifyDataSetChanged()
    }

    fun updateProduct(updatedProduct: Product) {
        for ((index, product) in products.withIndex()) {
            if (updatedProduct.id == product.id) {
                products[index] = updatedProduct
                notifyItemChanged(index, updatedProduct)
            }
        }
    }

    fun removeProduct(removedProduct: Product) {
        var indexToRemove: Int = -1

        for ((index, product) in products.withIndex()) {
            if (removedProduct.id == product.id) {
                indexToRemove = index
            }
        }

        products.removeAt(indexToRemove)
        notifyItemRemoved(indexToRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeHolder {
        return TypeHolder(parent.inflate(R.layout.item_type_statistics), callback)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: TypeHolder, position: Int) {
        holder.bind(types[position], products as ArrayList<Product>)
    }

    override fun getItemCount() = types.size

    class TypeHolder(private val binding: ItemTypeStatisticsBinding, callback: TypeCallback) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.callback = callback
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(type: Type, products: ArrayList<Product>) {

            itemView.typeStatisticsChart.setDrawBarShadow(false)
            itemView.typeStatisticsChart.setDrawValueAboveBar(true)
            itemView.typeStatisticsChart.description.isEnabled = false
            itemView.typeStatisticsChart.setDrawGridBackground(false)
            itemView.typeStatisticsChart.setPinchZoom(true)
            itemView.typeStatisticsChart.setMaxVisibleValueCount(7)

            // mapping product
            val groupsTypeProduct = products.groupBy { it.type == type.name }
            val listTypeProduct = groupsTypeProduct[true]?.toList()

            // mpChart Data
            val yValues = mutableListOf<BarEntry>()
            val xValues = mutableListOf<String>()
            var indexLabel = 0

            listTypeProduct?.groupBy { product ->
                product.time?.let { it -> TimeFormatter().getDayWithMonth(it) }
            }?.forEach { key, value ->

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
            itemView.typeStatisticsChart.data = data

            // remove legend
            val rightAxis: YAxis =  itemView.typeStatisticsChart.axisRight
            rightAxis.isEnabled = false
            val legend =  itemView.typeStatisticsChart.legend
            legend.isEnabled = false

            // add cubic chart
            val xAxisFormatter = DayFormatter(itemView.typeStatisticsChart)
            val xAxis = itemView.typeStatisticsChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            xAxis.labelCount = xValues.size
            xAxis.valueFormatter = IndexAxisValueFormatter(xValues)

            val custom = MyAxisValueFormatter()
            val leftAxis: YAxis = itemView.typeStatisticsChart.axisLeft
            leftAxis.setLabelCount(5, false)
            leftAxis.valueFormatter = custom
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            leftAxis.spaceTop = 15f

            // other view data
            binding.totalProduct = listTypeProduct?.size ?: 0
            binding.type = type
            binding.detailProduct = listTypeProduct?.let { mappingDetailProduct(type, it) }
        }

        private fun mappingDetailProduct(type: Type, products: List<Product>): DetailProduct{
            return DetailProduct(type, products)
        }

    }
}