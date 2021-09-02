package com.luck.vullkleprikon.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.Nullable
import com.luck.vullkleprikon.R
import kotlinx.android.synthetic.main.combination_view.view.*

class CombinationView: FrameLayout {

    private var image: Int = 0

    constructor(context: Context):super(context){
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet):super(context, attrs){
        init(context, attrs)
    }

    private fun init(context: Context, @Nullable set: AttributeSet?){
        image = R.drawable.mini_slot_one
        LayoutInflater.from(context).inflate(R.layout.combination_view, this)
        if (set == null)
            return
        val ra = context.obtainStyledAttributes(set, R.styleable.CombinationView)
        val image = ra.getResourceId(R.styleable.CombinationView_src, R.drawable.mini_slot_one)
        image1.setImageResource(image)
        image2.setImageResource(image)
        image3.setImageResource(image)

        ra.recycle()
    }
}