package com.luck.vullkleprikon.custom_text_view

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.luck.vullkleprikon.R

class GradientTextView: androidx.appcompat.widget.AppCompatTextView {

    private var primaryColor: Int = 0
    private var secondaryColor: Int = 0

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int):
            super(context, attrs, defStyleAttr)

    fun setColors(primaryColor: Int, secondaryColor: Int){
        this.primaryColor = primaryColor
        this.secondaryColor = secondaryColor
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (changed){
            paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
            ContextCompat.getColor(context, if (primaryColor == 0) R.color.yellow else primaryColor),
            ContextCompat.getColor(context, if (secondaryColor == 0) R.color.orange else secondaryColor),
            Shader.TileMode.CLAMP)
        }
    }

}