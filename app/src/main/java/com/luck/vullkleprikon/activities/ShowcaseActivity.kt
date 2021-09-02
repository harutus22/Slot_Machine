package com.luck.vullkleprikon.activities

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.PreferenceManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.luck.vullkleprikon.R
import com.luck.vullkleprikon.retrofit.ApiClientMagicChecker
import com.luck.vullkleprikon.retrofit.ResultModel
import com.luck.vullkleprikon.showcase.OnShowcaseClicked
import com.luck.vullkleprikon.showcase.ShowcaseAdapter
import com.luck.vullkleprikon.showcase.ShowcaseModel
import com.luck.vullkleprikon.utils.HOME_CLICK
import com.luck.vullkleprikon.utils.LAST_URL
import com.luck.vullkleprikon.utils.UUID
import com.luck.vullkleprikon.utils.getSharedString
import kotlinx.android.synthetic.main.activity_showcase.*
import org.json.JSONArray
import org.json.JSONObject

class ShowcaseActivity : AppCompatActivity(), OnShowcaseClicked {
    private lateinit var arrayShow: ArrayList<ShowcaseModel>
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showcase)

        val url = PreferenceManager.getDefaultSharedPreferences(this).getString(LAST_URL, "")

        if(url.isNullOrEmpty() || intent.getBooleanExtra(HOME_CLICK, false)) {
            mediaPlayer = MediaPlayer.create(this, R.raw.showcase_music)
            call()
        } else {
            startActivity(Intent(this, WebActivity::class.java))
            finish()
        }
    }

    private fun call() {
        progress.visibility = View.VISIBLE
        val httpAsync = "http://luckleprikon.space/content/products.html"
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
                            val jObject = JSONObject(data)
                            val array = jObject.getJSONArray("list_items")
                            initList(jsonArray = array)
                            showcaseRecycler.adapter = ShowcaseAdapter(arrayShow, this)
                            progress.visibility = View.GONE
                            mediaPlayer?.start()
                        }
                    }
                }

        httpAsync.join()

    }

    private fun initList(jsonArray: JSONArray){
        arrayShow = ArrayList<ShowcaseModel>()
        arrayShow.apply {
            add(ShowcaseModel(image = R.drawable.vulkan))
            add(ShowcaseModel(image = R.drawable.vulkan_platinum))
            add(ShowcaseModel(image = R.drawable.pin_up))
            add(ShowcaseModel(image = R.drawable.maxbet))
            add(ShowcaseModel(image = R.drawable.joycasino))
            add(ShowcaseModel(image = R.drawable.telegram))
            add(ShowcaseModel(image = R.drawable.azino))
            add(ShowcaseModel(image = R.drawable.slottica))
            add(ShowcaseModel(image = R.drawable.watssup))
        }
        for (i in 0 until  jsonArray.length()){
            val jsonObj = jsonArray.getJSONObject(i)
            arrayShow[i].icon = jsonObj.getString("image")
            arrayShow[i].url = jsonObj.getString("url")
        }
    }

    override fun onShowcaseClick(url: String) {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
            .putString(LAST_URL, url + "?hash=${getSharedString(UUID, this)}&app=$packageName").apply()
        startActivity(Intent(this, WebActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }


}