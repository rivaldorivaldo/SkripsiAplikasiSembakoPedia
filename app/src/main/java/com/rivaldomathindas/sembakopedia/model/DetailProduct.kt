package com.rivaldomathindas.sembakopedia.model

import java.io.Serializable

data class DetailProduct (
    val type: Type,
    val product: List<Product>
) : Serializable