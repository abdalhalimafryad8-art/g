package com.my.music.ui.settings

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.my.music.repeat.PlaybackManager

class SubSettingsScreen(
private val context: Context,
private val onBackClicked: () -> Unit
) {

val view: View

init {
val root = LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
background = PlaybackManager.createGlowGradient(Color.parseColor("#12111A"), Color.parseColor("#1C1B22"))
setPadding(40, 40, 40, 40)
}

val topBar = LinearLayout(context).apply {
orientation = LinearLayout.HORIZONTAL
gravity = Gravity.CENTER_VERTICAL
setPadding(0, 0, 0, 40)
}

val backBtn = ImageView(context).apply {
val drawable = context.resources.getDrawable(com.my.music.R.drawable.ic, null)
drawable.setLevel(6)
setImageDrawable(drawable)
setColorFilter(Color.WHITE)
setPadding(16, 16, 16, 16)
background = PlaybackManager.createMaterialM3Drawable(Color.parseColor("#2B2930"), 50f)
layoutParams = LinearLayout.LayoutParams(90, 90)
setOnClickListener { onBackClicked() }
}

val screenTitle = TextView(context).apply {
text = "موزع قنوات ديسيبل"
textSize = 20f
setTextColor(Color.WHITE)
typeface = Typeface.DEFAULT_BOLD
gravity = Gravity.CENTER
layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
setMargins(0, 0, 90, 0)
}
}

topBar.addView(backBtn)
topBar.addView(screenTitle)
root.addView(topBar)

val sectionSpeed = createSectionCard("سرعة المعالجة والتشغيل")
val speedValueText = TextView(context).apply {
text = "السرعة الحالية: ${PlaybackManager.currentSpeed}x"
setTextColor(Color.parseColor("#D0BCFF"))
textSize = 14f
setPadding(0, 0, 0, 20)
}
val speedSeekBar = SeekBar(context).apply {
max = 200
progress = ((PlaybackManager.currentSpeed - 0.5f) * 100).toInt()
setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
val actualSpeed = 0.5f + (progress / 100f)
speedValueText.text = "السرعة الحالية: ${String.format("%.2f", actualSpeed)}x"
PlaybackManager.setSpeed(actualSpeed)
}
override fun onStartTrackingTouch(seekBar: SeekBar?) {}
override fun onStopTrackingTouch(seekBar: SeekBar?) {}
})
}
sectionSpeed.addView(speedValueText)
sectionSpeed.addView(speedSeekBar)
root.addView(sectionSpeed)

val sectionEq = createSectionCard("موزع الترددات الهندسية (Decibel Bands)")

val bands = listOf("60Hz", "230Hz", "910Hz", "4kHz", "14kHz")
for (band in bands) {
val bandLabel = TextView(context).apply {
text = "$band: +0dB"
setTextColor(Color.parseColor("#938F99"))
textSize = 12f
setPadding(0, 12, 0, 8)
}
val bandSeekBar = SeekBar(context).apply {
max = 30
progress = 15
setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
val db = progress - 15
bandLabel.text = "$band: " + (if (db >= 0) "+$db" else "$db") + "dB"
}
override fun onStartTrackingTouch(seekBar: SeekBar?) {}
override fun onStopTrackingTouch(seekBar: SeekBar?) {}
})
}
sectionEq.addView(bandLabel)
sectionEq.addView(bandSeekBar)
}
root.addView(sectionEq)

this.view = root
}

private fun createSectionCard(title: String): LinearLayout {
return LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
setPadding(32, 32, 32, 32)
background = PlaybackManager.createMaterialM3Drawable(Color.parseColor("#1D1B20"), 32f)
val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
params.setMargins(0, 0, 0, 32)
layoutParams = params

val titleText = TextView(context).apply {
text = title
textSize = 16f
setTextColor(Color.WHITE)
typeface = Typeface.DEFAULT_BOLD
setPadding(0, 0, 0, 16)
}
addView(titleText)
}
}
}