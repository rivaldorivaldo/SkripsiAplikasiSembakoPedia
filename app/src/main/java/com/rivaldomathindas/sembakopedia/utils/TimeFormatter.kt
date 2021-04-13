package com.rivaldomathindas.sembakopedia.utils

import android.text.format.DateUtils
import android.util.Log
import com.kizitonwose.time.hours
import com.kizitonwose.time.seconds
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TimeFormatter {

    private var timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private var weekFormat = SimpleDateFormat("EEE, h:mm a", Locale.getDefault())
    private var fullFormat = SimpleDateFormat("EEE, MMM d, yyyy h:mm a", Locale.getDefault())
    private var detailFormat = SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault())
    private var simpleYearFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
    private var normalYearFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var secondYearFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var reportTime = SimpleDateFormat("MMM dd, yyy", Locale.getDefault())
    private var saveFormat = SimpleDateFormat("yyyyMMdd-mmss", Locale.getDefault())
    private var dayWithMonthFormat = SimpleDateFormat("d/MM", Locale.getDefault())
    private var dateOnly = SimpleDateFormat("d", Locale.getDefault())
    private var monthOnly = SimpleDateFormat("M", Locale.getDefault())
    private var yearOnly = SimpleDateFormat("yyyy", Locale.getDefault())

    fun getTimeStamp(time: Long): String {
        val currentTime = System.currentTimeMillis()
        val timeDifference = getTimeDifference(currentTime, time)

        return when {
            timeDifference <= 10.seconds.inMilliseconds.longValue -> "Just now"
            timeDifference <= 24.hours.inMilliseconds.longValue -> DateUtils.getRelativeTimeSpanString(
                time,
                currentTime,
                0
            ).toString()
            timeDifference <= 168.hours.inMilliseconds.longValue -> getTimeWeek(time)
            else -> if (isThisYear(time)) getDetailDate(time) else getFullFormat(time)
        }
    }

    fun getChatTimeStamp(time: Long): String {
        val currentTime = System.currentTimeMillis()
        val timeDifference = getTimeDifference(currentTime, time)
        val formattedTime: String

        formattedTime = when {
            timeDifference <= 12.hours.inMilliseconds.longValue -> getTime(time)
            isYesterday(time) -> "Yesterday"
            else -> getNormalYear(time)
        }

        Log.d(javaClass.simpleName, "${isYesterday(time)}")

        return formattedTime
    }

    private fun getTimeDifference(currentTime: Long, postTime: Long) = currentTime - postTime

    fun getTime(millis: Long): String {
        return timeFormat.format(millis)
    }

     fun getTimeWeek(millis: Long): String {
        return weekFormat.format(millis)
    }

    fun getFullFormat(millis: Long): String {
        return fullFormat.format(millis)
    }

    fun getDetailDate(millis: Long): String {
        return detailFormat.format(millis)
    }

    fun getSimpleYear(millis: Long): String {
        return simpleYearFormat.format(millis)
    }

    fun getNormalYear(millis: Long): String {
        return normalYearFormat.format(millis)
    }

    fun getNormalYear(date: Calendar): String {
        return normalYearFormat.format(date.time)
    }

    fun getSaveFormat(millis: Long): String {
        return saveFormat.format(millis)
    }

    fun getReportTime(millis: Long): String {
        return reportTime.format(millis)
    }

    fun getNormalSecondYear(millis: Long): String {
        return secondYearFormat.format(millis)
    }

    fun getNextDay(dateOfYear: String, addDay: Int): String {
        val calendar = Calendar.getInstance()
        val inputDate = secondYearFormat.parse(dateOfYear)
        try {
            if (inputDate != null)
                calendar.time = inputDate
        }catch (e: ParseException){
            e.printStackTrace()
        }

        calendar.add(Calendar.DATE, addDay)
        val resultDate = Date(calendar.timeInMillis)
        return dayWithMonthFormat.format(resultDate)
    }

    fun getDayWithMonth(millis: Long): String {
        return dayWithMonthFormat.format(millis)
    }

    fun isThisWeek(millis: Long): String {
        var date = ""
        if (getWeekFromTimeStamp(millis) == getCurrentWeekOfMonth()){
            date = dayWithMonthFormat.format(millis)
        }

        return date
    }

    fun isThisMont(millis: Long) : String {
        var date = ""
        if (getMonthFromTimeStamp(millis) == getCurrentMonthOfYear()){
            date = dayWithMonthFormat.format(millis)
        }

        return date
    }

    private fun getCurrentMonthOfYear(): Int{
        val currentDate = Date()
        return monthOnly.format(currentDate).toInt()
    }

    private fun getMonthFromTimeStamp(millis: Long): Int{
        return monthOnly.format(millis).toInt()
    }

    private fun getCurrentWeekOfMonth(): Int {
        val currentDate = Date()
        val date = dateOnly.format(currentDate).toInt()
        val month = monthOnly.format(currentDate).toInt()
        val year = yearOnly.format(currentDate).toInt()
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, date)
        calendar.minimalDaysInFirstWeek = 1
        return calendar.get(Calendar.WEEK_OF_MONTH)
    }

    private fun getWeekFromTimeStamp(millis: Long): Int {
        val date = dateOnly.format(millis).toInt()
        val month = monthOnly.format(millis).toInt()
        val year = yearOnly.format(millis).toInt()
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, date)
        calendar.minimalDaysInFirstWeek = 1
        return calendar.get(Calendar.WEEK_OF_MONTH)
    }

    private fun isThisYear(millis: Long): Boolean {
        val cal = Calendar.getInstance()
        cal.time = Date(millis)

        return cal.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
    }

    private fun isToday(millis: Long): Boolean {
        return DateUtils.isToday(millis)
    }

    private fun isYesterday(millis: Long): Boolean {
        return DateUtils.isToday(millis + DateUtils.DAY_IN_MILLIS)
    }

}