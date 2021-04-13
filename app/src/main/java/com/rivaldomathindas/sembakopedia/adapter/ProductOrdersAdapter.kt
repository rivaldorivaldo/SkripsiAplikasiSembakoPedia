package com.rivaldomathindas.sembakopedia.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.rivaldomathindas.sembakopedia.R
import com.google.firebase.auth.FirebaseAuth
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.ionicons_typeface_library.Ionicons
import com.rivaldomathindas.sembakopedia.databinding.ItemOrderBinding
import com.rivaldomathindas.sembakopedia.model.ProductOrder
import com.rivaldomathindas.sembakopedia.utils.AppUtils.setDrawable
import com.rivaldomathindas.sembakopedia.utils.inflate
import com.rivaldomathindas.sembakopedia.utils.setDrawable

class ProductOrdersAdapter(private val context: Context) : RecyclerView.Adapter<ProductOrdersAdapter.ProductOrderHolder>() {
    private val products = mutableListOf<ProductOrder>()

    fun addProductOrder(product: ProductOrder) {
        products.add(product)
        notifyItemInserted(products.size - 1)
    }

    fun addOrders(products: MutableList<ProductOrder>) {
        this.products.addAll(products)
        notifyDataSetChanged()
    }

    fun clearOrders() {
        products.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductOrderHolder {
        return ProductOrderHolder(parent.inflate(R.layout.item_order), context)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductOrderHolder, position: Int) {
        holder.bind(products[position])
    }

    class ProductOrderHolder(private val binding: ItemOrderBinding, context: Context) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.user.setDrawable(setDrawable(context, Ionicons.Icon.ion_person, R.color.secondaryText, 14))
            binding.desc.setDrawable(setDrawable(context, FontAwesome.Icon.faw_shopping_basket, R.color.secondaryText, 14))
        }

        fun bind(product: ProductOrder) {
            binding.data = product
            binding.isMine = (product.sellerId == FirebaseAuth.getInstance().currentUser?.uid)
        }

    }

}