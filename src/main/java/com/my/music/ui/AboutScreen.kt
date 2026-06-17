package com.my.music.ui

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.my.music.repeat.PlaybackManager
import com.my.music.ui.settings.AppThemeManager
import com.my.music.ui.settings.AppLocaleManager

class AboutScreen(
private val context: Context,
private val onBackClicked: () -> Unit
) {

val view: View
private val density = context.resources.displayMetrics.density

init {
val root = LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
background = PlaybackManager.createGlowGradient(AppThemeManager.bgStart, AppThemeManager.bgEnd)
setPadding((24 * density).toInt(), (40 * density).toInt(), (24 * density).toInt(), (24 * density).toInt())
}

// الهيدر المتناسق
val topBar = LinearLayout(context).apply {
orientation = LinearLayout.HORIZONTAL
gravity = Gravity.CENTER_VERTICAL
setPadding(0, 0, 0, (40 * density).toInt())
}

val backBtn = ImageView(context).apply {
val drawable = context.resources.getDrawable(com.my.music.R.drawable.ic, null)
drawable.setLevel(6)
setImageDrawable(drawable)
setColorFilter(Color.WHITE)
setPadding((10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt())
background = PlaybackManager.createMaterialM3Drawable(AppThemeManager.cardBg, 18 * density)
layoutParams = LinearLayout.LayoutParams((38 * density).toInt(), (38 * density).toInt())
setOnClickListener { onBackClicked() }
}

val screenTitle = TextView(context).apply {
text = AppLocaleManager.getString("about_section")
textSize = 20f
setTextColor(Color.WHITE)
typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
gravity = Gravity.CENTER
layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
setMargins(0, 0, (38 * density).toInt(), 0)
}
}

topBar.addView(backBtn)
topBar.addView(screenTitle)
root.addView(topBar)

// بطاقة المطور الفاخرة (Material 3 Dev Card)
val devCard = MaterialCardView(context).apply {
radius = 28 * density
cardElevation = 4 * density
strokeWidth = 2
setStrokeColor(ColorStateList.valueOf(AppThemeManager.accent))
setCardBackgroundColor(ColorStateList.valueOf(AppThemeManager.cardBg))
val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
setMargins(0, (20 * density).toInt(), 0, 0)
}
layoutParams = params
}

val cardLayout = LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
gravity = Gravity.CENTER_HORIZONTAL
setPadding((24 * density).toInt(), (40 * density).toInt(), (24 * density).toInt(), (40 * density).toInt())
}

// لوجو المطور الحركي المستدير
val avatar = ImageView(context).apply {
val drawable = context.resources.getDrawable(com.my.music.R.drawable.ic, null)
drawable.setLevel(10) // Profile Icon
setImageDrawable(drawable)
setColorFilter(Color.WHITE)
setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
background = PlaybackManager.createMaterialM3Drawable(AppThemeManager.accent, 50 * density)
layoutParams = LinearLayout.LayoutParams((84 * density).toInt(), (84 * density).toInt()).apply {
setMargins(0, 0, 0, (24 * density).toInt())
}
}

val devName = TextView(context).apply {
text = AppLocaleManager.getString("developer_title")
textSize = 20f
setTextColor(Color.WHITE)
typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
gravity = Gravity.CENTER
setPadding(0, 0, 0, (12 * density).toInt())
}

val devBio = TextView(context).apply {
text = AppLocaleManager.getString("about_desc")
textSize = 14f
setTextColor(Color.parseColor("#938F99"))
gravity = Gravity.CENTER
setPadding(0, 0, 0, (32 * density).toInt())
setLineSpacing(0f, 1.3f)
}

// أيقونة ورابط التيليجرام البرونزي النبيل المانع للتجمد والانهيار كلياً
val telegramLayout = LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
gravity = Gravity.CENTER_HORIZONTAL
setOnClickListener {
try {
// دمج حساب المطور عبد الحليم بشكل آمن
val telegramUrl = "https://t.me/Abdalhalim101001"
val intent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl)).apply {
// إضافة علم البدء المنفصل لتفادي تجميد واجهة التطبيق تماماً
addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
context.startActivity(intent)
} catch (e: Exception) {
// حماية استباقية ضد الانهيار في حال عدم استجابة الهاتف
e.printStackTrace()
}
}
}

val tgIcon = ImageView(context).apply {
val drawable = context.resources.getDrawable(com.my.music.R.drawable.telegram, null)
setImageDrawable(drawable)
layoutParams = LinearLayout.LayoutParams((64 * density).toInt(), (64 * density).toInt()).apply {
setMargins(0, 0, 0, (12 * density).toInt())
}
}

val contactText = TextView(context).apply {
text = AppLocaleManager.getString("contact_me")
textSize = 13f
setTextColor(AppThemeManager.accent)
typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
gravity = Gravity.CENTER
}

telegramLayout.addView(tgIcon)
telegramLayout.addView(contactText)

cardLayout.addView(avatar)
cardLayout.addView(devName)
cardLayout.addView(devBio)
cardLayout.addView(telegramLayout)

devCard.addView(cardLayout)
root.addView(devCard)

this.view = root
}
}