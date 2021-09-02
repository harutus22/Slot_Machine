package com.luck.vullkleprikon.image_view_scrolling

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.luck.vullkleprikon.R
import kotlinx.android.synthetic.main.image_view_scrolling.view.*

class ImageViewScrolling : FrameLayout {
    internal lateinit var eventEnd: IEventEnd
    internal var lastResult = 0
    internal var oldValue = 0
    companion object{
        private const val ANIMATION_DURATION = 150
    }

    val value: Int
        get() = Integer.parseInt(nextImage.tag.toString())

    fun setEventEnd(eventEnd: IEventEnd){
        this.eventEnd = eventEnd
    }

    constructor(context: Context):super(context){
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet):super(context, attrs){
        init(context)
    }

    private fun init(context: Context){
        LayoutInflater.from(context).inflate(R.layout.image_view_scrolling, this)

        nextImage.translationY = height.toFloat()
    }

    fun setValueRandom(image: Int, num_rotate: Int){
        currentImage.animate().translationY((-height).toFloat())
            .setDuration(ANIMATION_DURATION.toLong()).start()

        nextImage.translationY = nextImage.height.toFloat()

        nextImage.animate().translationY(0f).setDuration(ANIMATION_DURATION.toLong())
            .setListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    setImage(currentImage, oldValue%4)
                    currentImage.translationY = 0f
                    if (oldValue != num_rotate) { //if still have rotate
                        setValueRandom(image, num_rotate)
                        oldValue++
                    } else {
                        lastResult = 0
                        oldValue = 0
                        setImage(nextImage, image)
                        eventEnd.eventEnd(image%4, num_rotate) //because we have 4 images
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }

            }).start()


    }

    private fun setImage(image: ImageView?, value: Int){
        if (value == Util.podkova)
            image!!.setImageResource(R.drawable.slot_one)
        else if (value == Util.gold)
            image!!.setImageResource(R.drawable.slot_two)
        else if (value == Util.hat)
            image!!.setImageResource(R.drawable.slot_three)
        else if (value == Util.leprichaun)
            image!!.setImageResource(R.drawable.slot_four)

        image!!.tag = value
        lastResult = value
    }
}