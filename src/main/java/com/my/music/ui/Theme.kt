package com.my.music.ui

import android.content.Context
import android.graphics.Color

data class AppTheme(
val name: String,
val bgStart: Int,
val bgEnd: Int,
val accent: Int,
val cardBg: Int
)

object AppThemeManager {
var themeName = "غسق الأثير"
var bgStart = Color.parseColor("#0F0D13")
var bgEnd = Color.parseColor("#141218")
var accent = Color.parseColor("#D0BCFF")
var cardBg = Color.parseColor("#1D1B20")

// قاعدة بيانات الـ 6 ثيمات الماتريالية الفخمة مع إدراج البني والبرونز الدافئ
val themes = listOf(
AppTheme("غسق الأثير", Color.parseColor("#0F0D13"), Color.parseColor("#141218"), Color.parseColor("#D0BCFF"), Color.parseColor("#1D1B20")),
AppTheme("أوقيانوس نيون", Color.parseColor("#050B14"), Color.parseColor("#091220"), Color.parseColor("#00E5FF"), Color.parseColor("#121A2D")),
AppTheme("الزمرد النيوني", Color.parseColor("#04140B"), Color.parseColor("#092014"), Color.parseColor("#2ECC71"), Color.parseColor("#112C1E")),
AppTheme("الياقوت الملكي", Color.parseColor("#05081A"), Color.parseColor("#0A0F2E"), Color.parseColor("#4D7CFF"), Color.parseColor("#131B41")),
AppTheme("البني الدافئ", Color.parseColor("#150E0C"), Color.parseColor("#201511"), Color.parseColor("#CD7F32"), Color.parseColor("#2A1C18")),
AppTheme("العقيق الداكن", Color.parseColor("#14050D"), Color.parseColor("#200A14"), Color.parseColor("#FF4081"), Color.parseColor("#2D121E"))
)

fun applyTheme(context: Context, name: String) {
val matchedTheme = themes.find { it.name == name } ?: themes[0]
themeName = matchedTheme.name
bgStart = matchedTheme.bgStart
bgEnd = matchedTheme.bgEnd
accent = matchedTheme.accent
cardBg = matchedTheme.cardBg

val prefs = context.getSharedPreferences("AuraM3Prefs", Context.MODE_PRIVATE)
prefs.edit().putString("active_app_theme", name).apply()
}

fun loadTheme(context: Context) {
val prefs = context.getSharedPreferences("AuraM3Prefs", Context.MODE_PRIVATE)
val savedTheme = prefs.getString("active_app_theme", "غسق الأثير") ?: "غسق الأثير"
applyTheme(context, savedTheme)
}
}