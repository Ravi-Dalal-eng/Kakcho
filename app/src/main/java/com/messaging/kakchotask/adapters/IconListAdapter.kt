package com.messaging.kakchotask.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.messaging.kakchotask.R
import com.messaging.kakchotask.model.Icon
import com.messaging.kakchotask.utils.BASE_URL
import com.messaging.kakchotask.utils.*
import com.messaging.kakchotask.utils.RetrofitHelper.Companion.askForPermission
import com.messaging.kakchotask.utils.RetrofitHelper.Companion.downloadImage
import com.messaging.kakchotask.utils.RetrofitHelper.Companion.isPermissionGranted

class IconListAdapter(var list: List<Icon>)
    : RecyclerView.Adapter<IconListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun submitList(list: List<Icon>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) {
            val item = list[position]
          val image:ImageView=itemView.findViewById(R.id.image)
            val image_paid:ImageView=itemView.findViewById(R.id.image_paid)
            val download_btn:ImageView=itemView.findViewById(R.id.download_btn)
            val price: TextView =itemView.findViewById(R.id.price)
                //  icon_name.text = item.type Name isn't coming from the API
            with(itemView){
                if (item.rasterSizes.size > 6)
                    Glide.with(context).load(item.rasterSizes[6].formats[0].previewUrl)
                        .placeholder(R.drawable.ic_loading).into(image)
                   // For testing purposes
                // taking 128 px image only otherwise using index directly like 6 is a bad practice
                else
                    Glide.with(context).load(item.rasterSizes[0].formats[0].previewUrl)
                        .placeholder(R.drawable.ic_loading).into(image)

                if (item.isPremium) { // Show Price
                    image_paid.visibility = View.VISIBLE
                    download_btn.visibility = View.GONE
                    price.visibility = View.VISIBLE

                    if (item.prices.isNotEmpty())
                        price.text = "${item.prices[0].currency} ${item.prices[0].price}"

                } else { // Show Download Button

                    image_paid.visibility = View.INVISIBLE
                    download_btn.visibility = View.VISIBLE
                    price.visibility = View.INVISIBLE

                    download_btn.setOnClickListener {
                        if (!isPermissionGranted(context)) {
                            askForPermission(context as Activity)
                        } else {
                            val filePath = item.rasterSizes[0].formats[0].downloadUrl
                            val downloadUrl = getDownloadUrl("$BASE_URL$filePath")
                            downloadImage(context, downloadUrl)
                        }
                    }
                }
            }
        }

        private fun getDownloadUrl(baseUrl: String) =
            "$baseUrl?$CLIENT_ID=${MY_ID}&$CLIENT_SECRET=${MY_SECRET}"
    }
}