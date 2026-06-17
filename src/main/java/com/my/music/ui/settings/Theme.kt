package com.my.music.ui.settings

import android.content.Context
import android.graphics.Color

data class AppTheme(
val name: String,
val bgStart: Int,
val bgEnd: Int,
val accent: Int,
val cardBg: Int
)

// مدير السمات والألوان الستة الماتريالية الفخمة للتطبيق كلياً
object AppThemeManager {
var themeName = "غسق الأثير"
var bgStart = Color.parseColor("#0F0D13")
var bgEnd = Color.parseColor("#141218")
var accent = Color.parseColor("#D0BCFF")
var cardBg = Color.parseColor("#1D1B20")

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

// مدير الترجمة واللغات التلقائية واليدوية للتطبيق كلياً
object AppLocaleManager {
var currentLanguage = "en"

fun init(context: Context) {
val prefs = context.getSharedPreferences("AuraM3Prefs", Context.MODE_PRIVATE)
val sysLang = java.util.Locale.getDefault().language
val defaultLang = if (sysLang == "ar") "ar" else "en"
currentLanguage = prefs.getString("app_lang", defaultLang) ?: defaultLang
}

fun setLanguage(context: Context, lang: String) {
currentLanguage = lang
val prefs = context.getSharedPreferences("AuraM3Prefs", Context.MODE_PRIVATE)
prefs.edit().putString("app_lang", lang).apply()
}

fun getString(key: String): String {
val arMap = mapOf(
"app_name" to "الموسيقى المحلية",
"search_hint" to "ابحث في مكتبتك...",
"settings_title" to "الإعدادات الذكية",
"theme_section" to "سمة وتلوين التطبيق (Theme)",
"about_section" to "عن المطور",
"lang_section" to "لغة التطبيق (Languages)",
"developer_title" to "مطور النظام والمحرك",
"contact_me" to "اضغط هنا للتواصل مع المطور على تيليجرام",
"dialog_title" to "تحديد لغة التطبيق",
"dialog_desc" to "اختر لغتك المفضلة لتطبيقها فوراً",
"now_playing" to "شاشة التشغيل",
"about_desc" to "مطور برمجيات أندرويد قيادي، صممت هذا المحرك لتجربة موسيقية مذهلة خالية من الانهيارات كلياً."
)

val enMap = mapOf(
"app_name" to "Local Music",
"search_hint" to "Search your library...",
"settings_title" to "Smart Settings",
"theme_section" to "App Theme & Color",
"about_section" to "About Developer",
"lang_section" to "App Language",
"developer_title" to "System & Engine Developer",
"contact_me" to "Tap here to contact developer on Telegram",
"dialog_title" to "Select App Language",
"dialog_desc" to "Choose your preferred language to apply instantly",
"now_playing" to "Now Playing",
"about_desc" to "Lead Android developer, I built this engine to deliver a gorgeous, flawless, and completely crash-free audio experience."
)

return if (currentLanguage == "ar") {
arMap[key] ?: key
} else {
enMap[key] ?: key
}
}
}