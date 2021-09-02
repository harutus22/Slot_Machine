package com.luck.vullkleprikon.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.luck.vullkleprikon.R
import com.luck.vullkleprikon.utils.SCORE
import com.luck.vullkleprikon.utils.setScore

import kotlinx.android.synthetic.main.activity_end.*

class EndActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)

        tryAgain.setOnClickListener{
            setScore(SCORE, 1000, this)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}