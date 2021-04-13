package com.rivaldomathindas.sembakopedia.activity

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.fragment.MyPurchasesProductFragment
import com.rivaldomathindas.sembakopedia.base.BaseActivity
import com.rivaldomathindas.sembakopedia.utils.AppUtils
import com.rivaldomathindas.sembakopedia.utils.PagerAdapter
import kotlinx.android.synthetic.main.activity_my_orders.*

class MyPurchases : BaseActivity(), TabLayout.OnTabSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = PagerAdapter(supportFragmentManager, this)
        val product = MyPurchasesProductFragment()

        adapter.addAllFrags(product)
        adapter.addAllTitles(getString(R.string.my_purchases))

        viewpager.adapter = adapter
        viewpager.offscreenPageLimit = 1
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))

        tabs.setupWithViewPager(viewpager)
        tabs.addOnTabSelectedListener(this)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        viewpager.setCurrentItem(tab!!.position, true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.animateEnterLeft(this)
    }
}
