package com.luck.vullkleprikon.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.luck.vullkleprikon.App

class ConnectionService : BroadcastReceiver() {

    companion object{
        var connMan: ConnectivityReceiveListener? = null

        fun isConnected(): Boolean{
            val cm = App.getInstance().
            applicationContext.
            getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            val net = cm.activeNetworkInfo
            return net != null && net.isConnectedOrConnecting
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetworkInfo

        val isConnected = net != null && net.isConnectedOrConnecting

        if (connMan != null){
            connMan!!.onNetworkConnection(isConnected)
        }
    }

    interface ConnectivityReceiveListener{
        fun onNetworkConnection(isConnected: Boolean)
    }
}