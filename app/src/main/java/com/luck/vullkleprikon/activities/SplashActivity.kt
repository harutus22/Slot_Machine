package com.luck.vullkleprikon.activities

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.PreferenceManager
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.luck.vullkleprikon.retrofit.ApiClientMagicChecker
import com.luck.vullkleprikon.retrofit.ResultModel
import com.luck.vullkleprikon.showcase.ShowcaseAdapter
import com.luck.vullkleprikon.utils.*
import kotlinx.android.synthetic.main.activity_showcase.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import com.appsflyer.AFInAppEventType
import com.appsflyer.AFInAppEventType.COMPLETE_REGISTRATION

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventCheckCall()
        if (getSharedString(UUID, this) == ""){
            val id = java.util.UUID.randomUUID().toString()
            setString(UUID, id, this)
        }
        if (!getSharedBoolean(PASS_TO_SHOW_CASE, this))
        {
            if (checkInternetConnection()) {
                startActivity(Intent(this, ShowcaseActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        } else {
            if (getSharedBoolean(IS_MODERATOR, this)){
                call()
            } else {
                if (!getSharedBoolean(AUTORISATION, this)){
                    startActivity(Intent(this, SmsActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finish()
            }
        }
    }

    private fun checkInternetConnection() = ConnectionService.isConnected()

    private fun call(){
        val a = ApiClientMagicChecker.create().getResult()
        a.enqueue(object : Callback<ResultModel> {
            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                val a = response.body()
                if(a?.content == "1"){
                    Log.d("RESULT", "PASSED")
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                } else {
                    Log.d("RESULT", "NOT PASSED CAUSE :" + a!!.content)
                    setSharedBoolean(IS_MODERATOR, false, this@SplashActivity)
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }

            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                setSharedBoolean(IS_MODERATOR, false, this@SplashActivity)
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

        })
    }

    private fun eventCheckCall() {
        val httpAsync = "https://smsbuilder.ru/s2s/get?hash=${getSharedString(UUID, this)}&app=$packageName"
            .httpGet()
            .timeout(20000)
            .header("Content-Type", "application/json; utf-8")
            .responseString { _, _, result ->
                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        Log.e("Error", "response error ${result.getException()}")
                    }
                    is Result.Success -> {
                        val data = result.get()
                        println(data)
                        Log.e("Succses", "response success ${data}")
//                        val jObject = JSONObject(data)
//                        val response = jObject.get("response")
                        if (data == "1"){
                            val eventValues = HashMap<String, Any>()
                            eventValues[COMPLETE_REGISTRATION] = "PP_REGISTRATION"
                            AppsFlyerLib.getInstance().logEvent(this, "PP_REGISTRATION", eventValues)
                        } else if (data == "2"){
                            val eventValues = HashMap<String, Any>()
                            eventValues[AFInAppEventType.PURCHASE] = "PP_DEPOSIT"
                            AppsFlyerLib.getInstance().logEvent(this, AFInAppEventType.PURCHASE, eventValues)
                        }
                    }
                }
            }

        httpAsync.join()

    }
}