package com.rivaldomathindas.sembakopedia.callbacks

import android.view.View
import com.rivaldomathindas.sembakopedia.model.DetailProduct

interface TypeCallback {
    fun onClick(v: View, detailProduct: DetailProduct)
}