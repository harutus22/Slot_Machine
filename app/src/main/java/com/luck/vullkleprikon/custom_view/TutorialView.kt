package com.luck.vullkleprikon.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Nullable
import com.luck.vullkleprikon.R
import com.luck.vullkleprikon.utils.OnTutorialViewClicked

class TutorialView: View {

    private lateinit var paint: Paint
    private lateinit var vectorPath : Path
    private var startX = 0f
    private var startY = 0f
    private var viewWidth = 0f
    private var viewHeight = 0f
    private var screenHeight = 0
    private var screenWidth = 0
    private lateinit var onTutorialViewClicked: OnTutorialViewClicked

    constructor(context: Context, attrs: AttributeSet?
                ):super(context, attrs){
        init(context, attrs)
    }

    private fun init(context: Context, @Nullable set: AttributeSet?){
        val ra = context.obtainStyledAttributes(set, R.styleable.TutorialView)
        val color = ra.getResourceId(R.styleable.TutorialView_color, R.color.tutorial)
        ra.recycle()

        paint = Paint()
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint.isDither = true
        paint.color = resources.getColor(color)
        vectorPath = Path()
    }

    fun getScreen(screenHeight: Int, screenWidth: Int, onTutorialViewClicked: OnTutorialViewClicked){
        this.screenHeight = screenHeight
        this.screenWidth = screenWidth
        this.onTutorialViewClicked = onTutorialViewClicked
    }

    fun getCoordinates(viewXStart: Float, viewXEnd: Float,
                       viewYStart: Float, viewYEnd: Float,
    ){
        this.startX = viewXStart
        this.startY = viewYStart
        this.viewWidth = viewXEnd
        this.viewHeight = viewYEnd

        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        
        val emptySpace = viewHeight / 4

        vectorPath.moveTo(0f, 0f)
        vectorPath.lineTo(startX - emptySpace, 0f)
        vectorPath.lineTo(startX - emptySpace, screenHeight.toFloat())
        vectorPath.lineTo(0f, screenHeight.toFloat())
        vectorPath.moveTo(startX - emptySpace, 0f)
        vectorPath.lineTo(startX - emptySpace, startY - viewHeight - emptySpace)
        vectorPath.lineTo(screenWidth.toFloat(), startY - viewHeight - emptySpace)
        vectorPath.lineTo(screenWidth.toFloat(), 0f)
        vectorPath.moveTo(startX.toFloat() + viewWidth + emptySpace, startY.toFloat() - emptySpace - viewHeight)
        vectorPath.lineTo(startX.toFloat() + viewWidth + emptySpace, screenHeight.toFloat())
        vectorPath.lineTo(screenWidth.toFloat(), screenHeight.toFloat())
        vectorPath.lineTo(screenWidth.toFloat(), startY.toFloat() - emptySpace - viewHeight)
        vectorPath.moveTo(startX.toFloat() - emptySpace, startY.toFloat() + emptySpace + viewHeight)
        vectorPath.lineTo(startX.toFloat() - emptySpace, screenHeight.toFloat())
        vectorPath.lineTo(startX.toFloat() + viewWidth + emptySpace, screenHeight.toFloat() + viewHeight)
        vectorPath.lineTo(startX.toFloat() + viewWidth + emptySpace, startY.toFloat() + emptySpace + viewHeight)
        vectorPath.close()

        canvas!!.drawPath(vectorPath, paint)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if(event!!.action == MotionEvent.ACTION_DOWN) {
            onTutorialViewClicked.onTutorialViewClick()
        }
        return super.dispatchTouchEvent(event)
    }
}