package com.rivaldomathindas.sembakopedia.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.callbacks.ProductCallback
import com.rivaldomathindas.sembakopedia.databinding.ItemProductBinding
import com.rivaldomathindas.sembakopedia.model.Product
import com.rivaldomathindas.sembakopedia.utils.inflate

class ProductsAdapter(private val callback: ProductCallback) : RecyclerView.Adapter<ProductsAdapter.ProductHolder>() {
    private val products = mutableListOf<Product>()

    fun addProduct(product: Product) {
        products.add(product)
        notifyItemInserted(products.size - 1)
    }

    fun clearProducts() {
        products.clear()
        notifyDataSetChanged()
    }

    fun updateProduct(updatedProduct: Product) {
        for ((index, product) in products.withIndex()) {
            if (updatedProduct.id == product.id) {
                products[index] = updatedProduct
                notifyItemChanged(index, updatedProduct)
            }
        }
    }

    fun removeProduct(removedProduct: Product) {
        var indexToRemove: Int = -1

        for ((index, product) in products.withIndex()) {
            if (removedProduct.id == product.id) {
                indexToRemove = index
            }
        }

        products.removeAt(indexToRemove)
        notifyItemRemoved(indexToRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        return ProductHolder(parent.inflate(R.layout.item_product), callback)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.bind(products[position])
    }

    class ProductHolder(private val binding: ItemProductBinding, callback: ProductCallback) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.callback = callback
        }

        fun bind(product: Product) {
            binding.product = product
        }

    }

}