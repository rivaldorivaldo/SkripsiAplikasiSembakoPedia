package com.rivaldomathindas.sembakopedia.activity

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import android.view.MenuItem
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.fragment.OrdersPartsFragment
import com.rivaldomathindas.sembakopedia.base.BaseActivity
import com.rivaldomathindas.sembakopedia.utils.AppUtils
import com.rivaldomathindas.sembakopedia.utils.PagerAdapter
import kotlinx.android.synthetic.main.activity_orders.*

class OrdersActivity : BaseActivity(), TabLayout.OnTabSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        initViews()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Orders"

        setupViewPager()
        setupTabs()
    }

    private fun setupViewPager() {
        val adapter = PagerAdapter(supportFragmentManager, this)
        val product = OrdersPartsFragment()

        adapter.addAllFrags(product)
        adapter.addAllTitles(getString(R.string.incoming_orders))

        viewpager.adapter = adapter
        viewpager.offscreenPageLimit = 1
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))

    }

    private fun setupTabs() {
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.animateEnterLeft(this)
    }

}
