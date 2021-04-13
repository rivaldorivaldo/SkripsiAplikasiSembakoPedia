package com.rivaldomathindas.sembakopedia.callbacks

import android.view.View
import com.rivaldomathindas.sembakopedia.model.ProductCategory

interface CategoryCallback {
    fun onClick(v: View, productCategory: ProductCategory)
}