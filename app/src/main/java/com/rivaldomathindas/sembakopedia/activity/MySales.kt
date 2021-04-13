package com.rivaldomathindas.sembakopedia.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.rivaldomathindas.sembakopedia.fragment.MySalesProductFragment
import com.google.android.material.tabs.TabLayout
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.ionicons_typeface_library.Ionicons
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.base.BaseActivity
import com.rivaldomathindas.sembakopedia.utils.AppUtils
import com.rivaldomathindas.sembakopedia.utils.PagerAdapter
import kotlinx.android.synthetic.main.activity_my_sales.*

class MySales : BaseActivity(), TabLayout.OnTabSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_sales)

        setupViewPager()
        initFab()
    }

    //pengaturan view pager
    private fun setupViewPager() {
        val adapter = PagerAdapter(supportFragmentManager, this)
        val product = MySalesProductFragment()

        adapter.addAllFrags(product)
        adapter.addAllTitles(getString(R.string.my_sales))

        viewpager.adapter = adapter
        viewpager.offscreenPageLimit = 1
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))

        tabs.setupWithViewPager(viewpager)
        tabs.addOnTabSelectedListener(this)
    }

    //pengaturan FAB
    private fun initFab() {
        fabPart.setImageDrawable(IconicsDrawable(this).icon(Ionicons.Icon.ion_plus).color(Color.WHITE).sizeDp(22))
        fabPart.setOnClickListener {
            startActivity(Intent(this, AddPartActivity::class.java))
            AppUtils.animateEnterRight(this)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        viewpager.setCurrentItem(tab!!.position, true)
    }

    //memodifikasi tombol kembali
    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.animateEnterLeft(this)
    }
}
