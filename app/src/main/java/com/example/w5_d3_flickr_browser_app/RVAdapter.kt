package com.example.w5_d3_flickr_browser_app

import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.w5_d3_flickr_browser_app.databinding.ItemRowBinding

class RVAdapter(val activity: MainActivity, private val photos: ArrayList<Image>):
    RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: ItemRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val photo = photos[position]

        holder.binding.apply {
            tvImageText.text = photo.title
            Glide.with(activity).load(photo.link).into(ivThumbnail)
            llItemRow.setOnClickListener { activity.openImg(photo.link) }
        }
    }

    override fun getItemCount() = photos.size
}