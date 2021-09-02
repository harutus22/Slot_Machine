package com.luck.vullkleprikon.pop_up

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.luck.vullkleprikon.R
import com.luck.vullkleprikon.recycler_view.OnMenuItemClicked
import com.luck.vullkleprikon.recycler_view.PopUpRecyclerAdapter


class CustomPopUp (private val onMenuItemClicked: OnMenuItemClicked){
    private var recycleAdapter: PopUpRecyclerAdapter = PopUpRecyclerAdapter(onMenuItemClicked)


    fun showPopUpMenu(view: View){
        val inflater = LayoutInflater.from(view.context)
        val popUpView = inflater.inflate(R.layout.pop_up_layout, null, false)
        val width = RelativeLayout.LayoutParams.WRAP_CONTENT
        val height = RelativeLayout.LayoutParams.WRAP_CONTENT
        val focusable = true

        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val x = 0
        val y = location[1]

        //Create a window with our parameters

        //Create a window with our parameters
        val popupWindow = PopupWindow(popUpView, width, height, focusable)
        popupWindow.showAtLocation(view, Gravity.START, x, y)

        //Initialize the elements of our window, install the handler
        val recycler = popUpView.findViewById<RecyclerView>(R.id.recycler)
        recycler.setHasFixedSize(true)
        recycler.adapter = recycleAdapter
        popupWindow.setOnDismissListener {
            onMenuItemClicked.onPopUpClose()
        }
    }
}