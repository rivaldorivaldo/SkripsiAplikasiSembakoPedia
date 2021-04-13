package com.rivaldomathindas.sembakopedia.callbacks

import android.view.View
import com.rivaldomathindas.sembakopedia.model.Product

interface ProductCallback {

    fun onClick(v: View, product: Product)

}