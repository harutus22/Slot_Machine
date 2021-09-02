package com.luck.vullkleprikon.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luck.vullkleprikon.R

class PopUpRecyclerAdapter(val onMenuItemClicked: OnMenuItemClicked): RecyclerView.Adapter<PopUpRecyclerAdapter.PopUpViewHolder>() {

    private val array = ArrayList<MoneyModel>().apply {
        add(MoneyModel("5", 5))
        add(MoneyModel("25", 25))
        add(MoneyModel("50", 50))
        add(MoneyModel("100", 100))
    }

    inner class PopUpViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val text: TextView = view.findViewById(R.id.text)

        fun bind(money: MoneyModel){
            itemView.setOnClickListener{
                onMenuItemClicked.onItemClick(money.money)
            }
            text.text = money.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopUpViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pop_up_layout_item, parent, false)
        return PopUpViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopUpViewHolder, position: Int) {
        holder.bind(array[position])
    }

    override fun getItemCount() = array.size
}