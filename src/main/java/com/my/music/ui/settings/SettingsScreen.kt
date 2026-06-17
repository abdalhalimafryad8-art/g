package com.my.music.ui.settings

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.my.music.repeat.PlaybackManager
import com.my.music.repeat.Track
import com.my.music.ui.M3Effects

class SettingsScreen(
private val context: Context,
private val onBackClicked: () -> Unit,
private val onAdvanceSettingsClicked: () -> Unit,
private val onAboutClicked: () -> Unit
) {

val view: View
private val density = context.resources.displayMetrics.density
private val themeCardsList = ArrayList<MaterialCardView>()

init {
val root = LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
background = PlaybackManager.createGlowGradient(AppThemeManager.bgStart, AppThemeManager.bgEnd)
setPadding((24 * density).toInt(), (40 * density).toInt(), (24 * density).toInt(), (24 * density).toInt())
}

val topBar = LinearLayout(context).apply {
orientation = LinearLayout.HORIZONTAL
gravity = Gravity.CENTER_VERTICAL
setPadding(0, 0, 0, (32 * density).toInt())
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
text = AppLocaleManager.getString("settings_title")
textSize = 20f
setTextColor(Color.WHITE)
typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
gravity = Gravity.CENTER
layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
setMargins(0, 0, (38 * density).toInt(), 0)
}
}

topBar.addView(backBtn)
topBar.addView(screenTitle)
root.addView(topBar)

val mainScroll = ScrollView(context).apply {
layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
}

val scrollContainer = LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
}

// 1. قسم كروت الثيمات الستة الماتريالية الفخمة مع تمدد بؤري فيزيائي (Carousel)
val sectionTheme = createSectionCard(AppLocaleManager.getString("theme_section"))

val themeLayout = LinearLayout(context).apply {
orientation = LinearLayout.HORIZONTAL
setPadding(0, (12 * density).toInt(), 0, (12 * density).toInt())
}

themeCardsList.clear()
val themes = AppThemeManager.themes

for ((index, theme) in themes.withIndex()) {
val isSelected = AppThemeManager.themeName == theme.name

val themeCard = MaterialCardView(context).apply {
radius = 20 * density
cardElevation = 0f
strokeWidth = if (isSelected) 3 else 0
setStrokeColor(ColorStateList.valueOf(AppThemeManager.accent))
setCardBackgroundColor(ColorStateList.valueOf(AppThemeManager.cardBg))

// تمدد الكارد المحدد ديناميكياً برمجياً لمحاكاة الـ Carousel
val initialWeight = if (isSelected) 1.9f else 0.65f
val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, initialWeight).apply {
setMargins((6 * density).toInt(), 0, (6 * density).toInt(), 0)
}
layoutParams = params

val cardContent = LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
gravity = Gravity.CENTER_HORIZONTAL
setPadding((12 * density).toInt(), (18 * density).toInt(), (12 * density).toInt(), (18 * density).toInt())
}

val previewDot = View(context).apply {
background = PlaybackManager.createMaterialM3Drawable(theme.accent, 50f)
layoutParams = LinearLayout.LayoutParams((20 * density).toInt(), (20 * density).toInt()).apply {
setMargins(0, 0, 0, (10 * density).toInt())
}
}

val themeLabel = TextView(context).apply {
text = theme.name
textSize = 10f
setTextColor(Color.WHITE)
gravity = Gravity.CENTER
maxLines = 1
}

cardContent.addView(previewDot)
cardContent.addView(themeLabel)
addView(cardContent)
}

themeCardsList.add(themeCard)
themeLayout.addView(themeCard)
}

// تطبيق دالة تمدد وتقلص كروت الثيمات الماتريالية عند النقر (Focus carousel movement)
for (i in 0 until themeCardsList.size) {
val card = themeCardsList[i]
val theme = themes[i]
card.setOnClickListener {
M3Effects.animateM3CarouselWeights(themeCardsList, i)
root.postDelayed({
AppThemeManager.applyTheme(context, theme.name)
(context as? Activity)?.recreate() // تحديث فوري لكامل أجزاء التطبيق
}, 350)
}
}

sectionTheme.addView(themeLayout)
scrollContainer.addView(sectionTheme)

// 2. قسم اللغات الماتريالي التفاعلي (M3 Languages Hub)
val sectionLang = createSectionCard(AppLocaleManager.getString("lang_section"))
val langBtn = createSettingButton("English / العربية") {
showLanguageSelectionDialog()
}
sectionLang.addView(langBtn)
scrollContainer.addView(sectionLang)

// 3. قسم المطور ورابط التيليجرام الماتريالي المطور (About Developer Integration)
val sectionAbout = createSectionCard(AppLocaleManager.getString("about_section"))
val aboutBtn = createSettingButton("عن المطور..") {
onAboutClicked()
}
sectionAbout.addView(aboutBtn)
scrollContainer.addView(sectionAbout)

mainScroll.addView(scrollContainer)
root.addView(mainScroll)

this.view = root
}

private fun showLanguageSelectionDialog() {
val dialog = Dialog(context).apply {
requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
}

val dialogCard = MaterialCardView(context).apply {
radius = 28 * density
strokeWidth = 2
setStrokeColor(ColorStateList.valueOf(AppThemeManager.accent))
setCardBackgroundColor(ColorStateList.valueOf(AppThemeManager.cardBg))
val params = LinearLayout.LayoutParams((280 * density).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
layoutParams = params
}

val layout = LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
gravity = Gravity.CENTER_HORIZONTAL
setPadding((24 * density).toInt(), (24 * density).toInt(), (24 * density).toInt(), (24 * density).toInt())
}

val title = TextView(context).apply {
text = AppLocaleManager.getString("dialog_title")
textSize = 18f
setTextColor(Color.WHITE)
typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
gravity = Gravity.CENTER
setPadding(0, 0, 0, (12 * density).toInt())
}

val desc = TextView(context).apply {
text = AppLocaleManager.getString("dialog_desc")
textSize = 13f
setTextColor(Color.parseColor("#938F99"))
gravity = Gravity.CENTER
setPadding(0, 0, 0, (24 * density).toInt())
}

val btnAr = Button(context).apply {
text = "العربية (Arabic)"
setTextColor(Color.BLACK)
background = PlaybackManager.createMaterialM3Drawable(AppThemeManager.accent, 20 * density)
val btnParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
setMargins(0, 0, 0, (10 * density).toInt())
}
layoutParams = btnParams
setOnClickListener {
AppLocaleManager.setLanguage(context, "ar")
dialog.dismiss()
(context as? Activity)?.recreate()
}
}

val btnEn = Button(context).apply {
text = "English (الإنجليزية)"
setTextColor(Color.BLACK)
background = PlaybackManager.createMaterialM3Drawable(AppThemeManager.accent, 20 * density)
layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
setOnClickListener {
AppLocaleManager.setLanguage(context, "en")
dialog.dismiss()
(context as? Activity)?.recreate()
}
}

layout.addView(title)
layout.addView(desc)
layout.addView(btnAr)
layout.addView(btnEn)
dialogCard.addView(layout)

dialog.setContentView(dialogCard)
dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
dialog.show()
}

private fun createSectionCard(title: String): LinearLayout {
return LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
background = PlaybackManager.createMaterialM3Drawable(AppThemeManager.cardBg, 24 * density)
val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
setMargins(0, 0, 0, (24 * density).toInt())
}
layoutParams = params

val titleText = TextView(context).apply {
text = title
textSize = 15f
setTextColor(Color.WHITE)
typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
setPadding(0, 0, 0, (12 * density).toInt())
}
addView(titleText)
}
}

private fun createSettingButton(text: String, onClick: () -> Unit): Button {
return Button(context).apply {
this.text = text
setTextColor(Color.WHITE)
textSize = 13f
setPadding((24 * density).toInt(), (14 * density).toInt(), (24 * density).toInt(), (14 * density).toInt())
background = PlaybackManager.createMaterialM3Drawable(Color.parseColor("#2B2930"), 20f * density)
layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
setOnClickListener { onClick() }
}
}
}