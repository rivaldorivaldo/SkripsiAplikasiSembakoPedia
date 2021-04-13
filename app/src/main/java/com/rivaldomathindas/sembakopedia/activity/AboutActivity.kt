package com.rivaldomathindas.sembakopedia.activity

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.rivaldomathindas.sembakopedia.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        initViews()
    }

    fun initViews(){
        setSupportActionBar(toolbarAbout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.about)

        val appVersion = getString(R.string.version)+ ":" + getAppVersion(this)
        app_version.text = appVersion
    }

    //ambil versi aplikasi
    private fun getAppVersion(context: Context): String{
        var version = ""
        try{
            val info = context.packageManager.getPackageInfo(context.packageName,0)
            version = info.versionName
        } catch (e:PackageManager.NameNotFoundException){
            e.printStackTrace()
        }
        return version
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}