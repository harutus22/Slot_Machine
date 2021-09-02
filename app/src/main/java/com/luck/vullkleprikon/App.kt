package com.luck.vullkleprikon

import android.app.Application
import com.appsflyer.AppsFlyerLib
import com.luck.vullkleprikon.utils.ConnectionService
import com.onesignal.OneSignal
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig


class App : Application() {

    private val ONESIGNAL_APP_ID = "0c5527b4-8e2d-42ad-9d27-82d94a1f08a3"
    private val YANDEX_APP_ID = "e252be9f-fb63-438a-8d33-669c8dfff936"


    companion object{
        private lateinit var myApp: App

        @Synchronized
        fun getInstance() = myApp
    }

    override fun onCreate() {
        super.onCreate()
        initOneConfig()
        initYandex()
        initAppsFlyer()
        myApp = this
    }

    private fun initAppsFlyer() {
        AppsFlyerLib.getInstance().init("bSiQz4zRRTErHDbyxPM6fg", null, this)
        AppsFlyerLib.getInstance().start(this)
    }

    private fun initOneConfig() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this)

        OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }

    private fun initYandex(){
        val config: YandexMetricaConfig = YandexMetricaConfig.newConfigBuilder(YANDEX_APP_ID).build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }

    fun setConnectivityManager(listener: ConnectionService.ConnectivityReceiveListener){
        ConnectionService.connMan = listener
    }
}