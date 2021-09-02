package com.luck.vullkleprikon.custom_spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.luck.vullkleprikon.R

class CountryAdapter(context: Context, countryList: ArrayList<CountryCode>) :
    ArrayAdapter<CountryCode>(context, 0, countryList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initRepresentView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup?): View{
        var view: View? = convertView
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        }
        val image = view?.findViewById<ImageView>(R.id.flag_image)
        val text = view?.findViewById<TextView>(R.id.country_code)

        val countryItem = getItem(position)
        if (countryItem != null) {
            image?.setImageResource(countryItem.countryFlag)
            text?.text = countryItem.countryCode
        }
        return view!!
    }

    private fun initRepresentView(position: Int, convertView: View?, parent: ViewGroup?): View{
        var view: View? = convertView
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.spinner_item_drop_down, parent, false)
        }
        val image = view?.findViewById<ImageView>(R.id.flag_image)
        val text = view?.findViewById<TextView>(R.id.country_code)

        val countryItem = getItem(position)
        if (countryItem != null) {
            image?.setImageResource(countryItem.countryFlag)
            text?.text = countryItem.countryCode
        }
        return view!!
    }

}