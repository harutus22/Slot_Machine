package com.luck.vullkleprikon.showcase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.luck.vullkleprikon.R

class ShowcaseAdapter(private val list: ArrayList<ShowcaseModel>, val onShowcaseClicked: OnShowcaseClicked): RecyclerView.Adapter<ShowcaseAdapter.ShowcaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowcaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.showcase_item, parent, false)
        return ShowcaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowcaseViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    inner class ShowcaseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val image = itemView.findViewById<ImageView>(R.id.image)
        private val action = itemView.findViewById<AppCompatButton>(R.id.action)

        fun bind(model: ShowcaseModel){
            image.load(model.icon)
            action.setOnClickListener {
                onShowcaseClicked.onShowcaseClick(model.url)
            }
        }
    }
}