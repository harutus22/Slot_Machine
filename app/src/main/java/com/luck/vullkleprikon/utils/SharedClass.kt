package com.luck.vullkleprikon.utils

import android.content.Context
import androidx.preference.PreferenceManager

fun setSharedBoolean(text: String, isPlaying: Boolean, context: Context) {
    val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
    editor.putBoolean(text, isPlaying)
    editor.apply()
}

fun setScore(text: String, score: Int, context: Context) {
    val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
    editor.putInt(text, score)
    editor.apply()
}

fun setString(text: String, value: String, context: Context) {
    val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
    editor.putString(text, value)
    editor.apply()
}

fun getScore(context: Context) = PreferenceManager.getDefaultSharedPreferences(context).getInt(SCORE, 1000)
fun getSharedBoolean(text: String, context: Context) = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(text, true)
fun getSharedString(text: String, context: Context) = PreferenceManager.getDefaultSharedPreferences(context).getString(text, "")
