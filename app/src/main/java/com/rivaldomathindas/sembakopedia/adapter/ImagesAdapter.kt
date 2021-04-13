package com.rivaldomathindas.sembakopedia.adapter

import android.net.Uri

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.databinding.ItemImageBinding
import com.rivaldomathindas.sembakopedia.utils.inflate

class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ImageHolder>() {
    private val images = mutableListOf<Uri>()

    fun addImages(images: MutableList<Uri>) {
        this.images.clear()
        this.images.addAll(images)
        notifyDataSetChanged()
    }

    fun addImage(image: Uri) {
        this.images.add(image)
        notifyItemInserted(images.size -1 )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(parent.inflate(R.layout.item_image))
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(images[position])
    }

    class ImageHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(image: Uri) {
            binding.uri = image
        }

    }

}