package com.rivaldomathindas.sembakopedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.activity.TypeStatisticsActivity
import com.rivaldomathindas.sembakopedia.adapter.CategoryAdapter
import com.rivaldomathindas.sembakopedia.callbacks.CategoryCallback
import com.rivaldomathindas.sembakopedia.model.ProductCategory
import com.rivaldomathindas.sembakopedia.utils.AppUtils
import com.rivaldomathindas.sembakopedia.utils.DataCategory
import com.rivaldomathindas.sembakopedia.utils.K
import kotlinx.android.synthetic.main.fragment_statistics.*

class StatisticsFragment : Fragment(), CategoryCallback {
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
   
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        loadCategories()
    }

    private fun loadCategories() {
        val data = DataCategory.getCategory(resources)
        categoryAdapter.addCategory(data)
    }

    private fun initViews() {
        rvStatistics.setHasFixedSize(true)
        rvStatistics.layoutManager = GridLayoutManager(activity, 3)
        rvStatistics.itemAnimator = DefaultItemAnimator()
        (rvStatistics.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false

        categoryAdapter = CategoryAdapter(this)
        rvStatistics.adapter = categoryAdapter
    }

    override fun onClick(v: View, productCategory: ProductCategory) {
        val i = Intent(activity, TypeStatisticsActivity::class.java)
        i.putExtra(K.CATEGORY_PRODUCT, productCategory)
        startActivity(i)
        AppUtils.animateFadein(requireActivity())
    }
}
