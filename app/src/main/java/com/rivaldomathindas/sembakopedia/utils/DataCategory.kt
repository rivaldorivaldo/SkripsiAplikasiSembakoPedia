package com.rivaldomathindas.sembakopedia.utils

import android.content.res.Resources
import com.google.gson.Gson
import com.rivaldomathindas.sembakopedia.model.ProductCategory
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


object DataCategory {

    lateinit var file : InputStream

    private fun loadJSONFromAssets(resources: Resources) : String? {
        val json: String?
        val charset= Charsets.UTF_8
        json = try {
            val language = Locale.getDefault().displayLanguage
            if (language=="Indonesia") {
                file = resources.assets.open("kategori.json")
            }else{
                file = resources.assets.open("category.json")
            }
            val inputStream = file
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun getCategory(res: Resources): ArrayList<ProductCategory> {
        val data = loadJSONFromAssets(res)
        val gson = Gson()
        val response = gson.fromJson(data, Array<ProductCategory>::class.java)
        val productCategory = ArrayList<ProductCategory>()
        for (i in response){
            productCategory.add(i)
        }

        return productCategory
    }
}