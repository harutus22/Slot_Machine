package com.luck.vullkleprikon.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.luck.vullkleprikon.R
import com.luck.vullkleprikon.custom_spinner.CountryAdapter
import com.luck.vullkleprikon.custom_spinner.CountryCode
import com.luck.vullkleprikon.utils.CHECK_SMS
import com.luck.vullkleprikon.utils.UUID
import com.luck.vullkleprikon.utils.getSharedString
import kotlinx.android.synthetic.main.activity_sms.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class SmsActivity : AppCompatActivity() {

    companion object{
        private const val SMS_KEY = "uZqxYFuD3C"
    }

    private lateinit var mCountryList: ArrayList<CountryCode>
    private lateinit var code: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)
        initList()

        val adapter = CountryAdapter(this, mCountryList)
        countrySpinner.adapter = adapter
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                code = (parent?.getItemAtPosition(position) as CountryCode).countryCode
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        getSmsBtn.setOnClickListener {
            if (number.text.isNullOrEmpty()){
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
            } else {
                callServer(code + number.text.toString())
            }
        }


    }

    private fun callServer(number: String) {
        val httpAsync = "https://smsbuilder.ru/api/create?phone=$number&key=$SMS_KEY"//&test=true
            .httpGet()
            .timeout(20000)
            .header("Content-Type", "application/json; utf-8")
            .responseString { _, _, result ->
                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        Log.e("Error", "response error ${result.getException()}")
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(this@SmsActivity, "Произошла ошибка, пожалуйста проверьте интернет соидинение", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Result.Success -> {
                        val data = result.get()
                        println(data)
                        Log.e("Succses", "response success ${data}")
                        val jsonObject = JSONObject(data)
                        //fetchResult(data)
                        if (data.contains("code")) {
                            val date: String = jsonObject.getString("code")
                            startActivity(Intent(this, CheckCodeActivity::class.java).putExtra(
                                CHECK_SMS, date))
                            finish()
                        } else {
                            val date= jsonObject.get("error")
                            Toast.makeText(this, date.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        httpAsync.join()


    }

    private fun initList() {
        mCountryList = ArrayList()
        mCountryList.add(CountryCode("+7", R.drawable.russia))
        mCountryList.add(CountryCode("+380", R.drawable.ukraine))
        mCountryList.add(CountryCode("+7", R.drawable.kazakhstan))
        mCountryList.add(CountryCode("+375", R.drawable.belarus))
        mCountryList.add(CountryCode("+994", R.drawable.azerbaijan))
    }
}