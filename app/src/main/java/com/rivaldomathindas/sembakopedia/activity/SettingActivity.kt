package com.rivaldomathindas.sembakopedia.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.utils.AlarmReceiver
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    companion object {
        const val ALARM_PREFERENCES = "alarm_preferences"
        private const val SWITCH_PREFERENCES = "switch_preferences"
    }

    private lateinit var alarmReceiver: AlarmReceiver
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setSupportActionBar(toolbarSetting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.setting)

        alarmReceiver = AlarmReceiver()
        mSharedPreferences = getSharedPreferences(ALARM_PREFERENCES, Context.MODE_PRIVATE)

        setSwitch()
        swDailyReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                alarmReceiver.setDailyReminder(this)
            } else {
                alarmReceiver.cancelAlarm(this)
            }
            saveSettings(isChecked)
        }

        tvChangeLanguage.setOnClickListener {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }
    }

    private fun setSwitch() {
        swDailyReminder.isChecked = mSharedPreferences.getBoolean(SWITCH_PREFERENCES, false)
    }

    private fun saveSettings(value: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(SWITCH_PREFERENCES, value)
        editor.apply()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}