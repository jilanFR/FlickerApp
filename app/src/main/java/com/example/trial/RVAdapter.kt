package com.example.trial

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trial.Constants.TAG_RV_ADAPTER
import com.example.trial.databinding.ItemRawDesignBinding

class RVAdapter(var context: Context,
                var photoList: ArrayList<PhotoItems>,
                var selectedImg :ImgRowClickListener
                )

    : RecyclerView.Adapter<RVAdapter.FlickImageViewHolder>() {

    class FlickImageViewHolder(var binding: ItemRawDesignBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickImageViewHolder {

        // Called by the layout manager when it needs a new view
        return FlickImageViewHolder(ItemRawDesignBinding
            .inflate(LayoutInflater.
            from(
                parent.context),
                parent,
                false)
        )

        Log.d(TAG_RV_ADAPTER, ".onCreateViewHolder new view requested")
    }

    override fun onBindViewHolder(holder: FlickImageViewHolder, position: Int) {

        // called by the layout manager when it wants new data in an existing view
        var photo = photoList[position]
        var  serverId= photo.server
        var secret = photo.secrete
        var  id = photo.id
        var title=photo.title


        val photoUrl= "https://live.staticflickr.com/$serverId/${id}_${secret}.jpg"

        holder.binding.apply {

            // set image title
            holder.binding.textView.text = title

            // make image display
            Glide.with(context)
                .load(photoUrl)
                .into(photoIv)


            photoIv.setOnClickListener{
                selectedImg.onClick(photoUrl)
            }
        }

    }


    override fun getItemCount(): Int {
        return  photoList.size-11
    }

    fun loadNewData(newPhotos:ArrayList<PhotoItems>){
        photoList = newPhotos
        notifyDataSetChanged()
    }

    interface ImgRowClickListener{
        fun onClick(img : String)
    }

}

