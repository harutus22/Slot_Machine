package com.luck.vullkleprikon.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.luck.vullkleprikon.R
import com.luck.vullkleprikon.utils.CHECK_SMS
import com.luck.vullkleprikon.utils.PASS_TO_SHOW_CASE
import com.luck.vullkleprikon.utils.setSharedBoolean
import kotlinx.android.synthetic.main.activity_check_code.*

class CheckCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_code)

        val code = intent.getStringExtra(CHECK_SMS)
        checkBtn.setOnClickListener {
            when {
                number.text.isNullOrEmpty() -> {
                    Toast.makeText(this, "Пожалуйста введите номер", Toast.LENGTH_SHORT).show()
                }
                number.text.toString() != code -> {
                    Toast.makeText(this, "Проверочный код не совпадает", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    setSharedBoolean(PASS_TO_SHOW_CASE, false, this)
                    startActivity(Intent(this, ShowcaseActivity::class.java))
                    finish()
                }
            }

        }
    }
}