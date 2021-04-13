package com.rivaldomathindas.sembakopedia.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.callbacks.CategoryCallback
import com.rivaldomathindas.sembakopedia.databinding.ItemCategoryBinding
import com.rivaldomathindas.sembakopedia.model.ProductCategory
import com.rivaldomathindas.sembakopedia.utils.inflate

class CategoryAdapter(private val callback: CategoryCallback) : RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    private val productCategories = mutableListOf<ProductCategory>()

    fun addCategory(productCategory: ArrayList<ProductCategory>) {
        productCategories.addAll(productCategory)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        return CategoryHolder(parent.inflate(R.layout.item_category), callback)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.bind(productCategories[position])
    }

    override fun getItemCount() = productCategories.size

    class CategoryHolder(private val binding: ItemCategoryBinding, callback: CategoryCallback) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.callback = callback
        }

        fun bind(productCategory: ProductCategory) {
            binding.category = productCategory
        }

    }
}