package com.my.music.ui

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.my.music.repeat.PlaybackManager
import com.my.music.repeat.Track
import com.my.music.ui.settings.AppThemeManager
import com.my.music.ui.settings.AppLocaleManager
import kotlin.math.sin

class VideoScreen(
private val context: Context,
private val onBackClicked: () -> Unit
) : PlaybackManager.PlaybackListener {

val view: View
private val titleView: TextView
private val artistView: TextView
private val playPauseBtn: ImageView
private val backBtn: ImageView
private val visualizerView: AuraVisualizerView
private val progressSeekBar: SeekBar
private val progressText: TextView
private val durationText: TextView
private val cdSpinner: CDSpinnerView

private val density = context.resources.displayMetrics.density

init {
applySystemUiTheme()

val root = LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
// تطبيق خلفية تدرج السمة الديناميكية
background = PlaybackManager.createGlowGradient(AppThemeManager.bgStart, AppThemeManager.bgEnd)
setPadding((24 * density).toInt(), (40 * density).toInt(), (24 * density).toInt(), (24 * density).toInt())
}

val topBar = LinearLayout(context).apply {
orientation = LinearLayout.HORIZONTAL
gravity = Gravity.CENTER_VERTICAL
}

backBtn = ImageView(context).apply {
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
text = AppLocaleManager.getString("now_playing") // استخدام الترجمة الديناميكية الفورية
textSize = 15f
setTextColor(Color.parseColor("#938F99"))
typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
gravity = Gravity.CENTER
layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
setMargins(0, 0, (38 * density).toInt(), 0)
}
}

topBar.addView(backBtn)
topBar.addView(screenTitle)
root.addView(topBar)

cdSpinner = CDSpinnerView(context).apply {
layoutParams = LinearLayout.LayoutParams((240 * density).toInt(), (240 * density).toInt()).apply {
gravity = Gravity.CENTER
setMargins(0, (40 * density).toInt(), 0, (40 * density).toInt())
}
}
root.addView(cdSpinner)

titleView = TextView(context).apply {
textSize = 20f
setTextColor(Color.WHITE)
typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
gravity = Gravity.CENTER
setPadding(0, (12 * density).toInt(), 0, (4 * density).toInt())
maxLines = 1
ellipsize = TextUtils.TruncateAt.END
}

artistView = TextView(context).apply {
textSize = 13f
setTextColor(AppThemeManager.accent) // استخدام لكنة السمة النشطة فوراً
gravity = Gravity.CENTER
setPadding(0, 0, 0, (24 * density).toInt())
}

root.addView(titleView)
root.addView(artistView)

visualizerView = AuraVisualizerView(context).apply {
layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (60 * density).toInt()).apply {
setMargins(0, 0, 0, (32 * density).toInt())
}
}
root.addView(visualizerView)

progressSeekBar = SeekBar(context).apply {
// صبغ وتلوين شريط التقدم بلكنة السمة النشطة ديناميكياً
progressTintList = ColorStateList.valueOf(AppThemeManager.accent)
thumbTintList = ColorStateList.valueOf(AppThemeManager.accent)
progressBackgroundTintList = ColorStateList.valueOf(Color.parseColor("#35343A"))
layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
if (fromUser) {
PlaybackManager.seekTo(progress)
}
}
override fun onStartTrackingTouch(seekBar: SeekBar?) {}
override fun onStopTrackingTouch(seekBar: SeekBar?) {}
})
}
root.addView(progressSeekBar)

val timeLayout = LinearLayout(context).apply {
orientation = LinearLayout.HORIZONTAL
setPadding((8 * density).toInt(), (6 * density).toInt(), (8 * density).toInt(), (40 * density).toInt())
}

progressText = TextView(context).apply {
text = "00:00"
textSize = 11f
setTextColor(Color.parseColor("#938F99"))
typeface = Typeface.MONOSPACE
layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
}

durationText = TextView(context).apply {
text = "00:00"
textSize = 11f
setTextColor(Color.parseColor("#938F99"))
typeface = Typeface.MONOSPACE
gravity = Gravity.END
layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
}

timeLayout.addView(progressText)
timeLayout.addView(durationText)
root.addView(timeLayout)

val controlsLayout = LinearLayout(context).apply {
orientation = LinearLayout.HORIZONTAL
gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
}

val prevBtn = ImageView(context).apply {
val drawable = context.resources.getDrawable(com.my.music.R.drawable.ic, null)
drawable.setLevel(3)
setImageDrawable(drawable)
setColorFilter(Color.WHITE)
setPadding((12 * density).toInt(), (12 * density).toInt(), (12 * density).toInt(), (12 * density).toInt())
background = PlaybackManager.createMaterialM3Drawable(AppThemeManager.cardBg, 50f)
layoutParams = LinearLayout.LayoutParams((44 * density).toInt(), (44 * density).toInt()).apply {
setMargins(0, 0, (24 * density).toInt(), 0)
}
setOnClickListener { PlaybackManager.prev(context) }
}

playPauseBtn = ImageView(context).apply {
val drawable = context.resources.getDrawable(com.my.music.R.drawable.ic, null)
drawable.setLevel(0)
setImageDrawable(drawable)
setColorFilter(Color.BLACK)
setPadding((14 * density).toInt(), (14 * density).toInt(), (14 * density).toInt(), (14 * density).toInt())
background = PlaybackManager.createMaterialM3Drawable(AppThemeManager.accent, 16 * density)
layoutParams = LinearLayout.LayoutParams((56 * density).toInt(), (56 * density).toInt())
setOnClickListener { PlaybackManager.togglePlayPause(context) }
}

val nextBtn = ImageView(context).apply {
val drawable = context.resources.getDrawable(com.my.music.R.drawable.ic, null)
drawable.setLevel(2)
setImageDrawable(drawable)
setColorFilter(Color.WHITE)
setPadding((12 * density).toInt(), (12 * density).toInt(), (12 * density).toInt(), (12 * density).toInt())
background = PlaybackManager.createMaterialM3Drawable(AppThemeManager.cardBg, 50f)
layoutParams = LinearLayout.LayoutParams((44 * density).toInt(), (44 * density).toInt()).apply {
setMargins((24 * density).toInt(), 0, 0, 0)
}
setOnClickListener { PlaybackManager.next(context) }
}

controlsLayout.addView(prevBtn)
controlsLayout.addView(playPauseBtn)
controlsLayout.addView(nextBtn)
root.addView(controlsLayout)

this.view = root
PlaybackManager.registerListener(this)
updateUI(PlaybackManager.getCurrentTrack())
}

private fun applySystemUiTheme() {
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
val window = (context as? Activity)?.window
window?.statusBarColor = AppThemeManager.bgStart
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
val decorView = window?.decorView
var flags = decorView?.systemUiVisibility ?: 0
flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
decorView?.systemUiVisibility = flags
}
}
}

private fun updateUI(track: Track) {
titleView.text = track.title
artistView.text = track.artist
onStateChanged()
}

override fun onStateChanged() {
playPauseBtn.drawable?.let {
it.setLevel(if (PlaybackManager.isPlaying) 1 else 0)
}
if (PlaybackManager.isPlaying) {
cdSpinner.startSpinning()
} else {
cdSpinner.stopSpinning()
}
}

override fun onTrackChanged(track: Track) {
updateUI(track)
}

override fun onProgressUpdate(currentMs: Int, totalMs: Int) {
progressSeekBar.max = totalMs
progressSeekBar.progress = currentMs

val curSecs = currentMs / 1000
val curMins = curSecs / 60
progressText.text = String.format("%02d:%02d", curMins % 60, curSecs % 60)

val totSecs = totalMs / 1000
val totMins = totSecs / 60
durationText.text = String.format("%02d:%02d", totMins % 60, totSecs % 60)
}

fun destroy() {
PlaybackManager.unregisterListener(this)
cdSpinner.stopSpinning()
}

class CDSpinnerView(context: Context) : View(context) {
private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
private var angle = 0f
private var animator: ValueAnimator? = null

override fun onDraw(canvas: Canvas) {
super.onDraw(canvas)
val cx = width / 2f
val cy = height / 2f
val radius = width.coerceAtMost(height) / 2f

canvas.save()
canvas.rotate(angle, cx, cy)

val cdDrawable = PlaybackManager.createGlowGradient(Color.parseColor("#1C1A22"), AppThemeManager.bgStart).apply {
shape = GradientDrawable.OVAL
setBounds((cx - radius).toInt(), (cy - radius).toInt(), (cx + radius).toInt(), (cy + radius).toInt())
}
cdDrawable.draw(canvas)

paint.style = Paint.Style.STROKE
paint.strokeWidth = 1.5f

paint.color = Color.parseColor("#15FFFFFF")
canvas.drawCircle(cx, cy, radius * 0.90f, paint)
canvas.drawCircle(cx, cy, radius * 0.80f, paint)

paint.color = Color.parseColor("#0DFFFFFF")
canvas.drawCircle(cx, cy, radius * 0.70f, paint)
canvas.drawCircle(cx, cy, radius * 0.60f, paint)
canvas.drawCircle(cx, cy, radius * 0.50f, paint)

// صبغ قلب الفينيل بلكنة المظهر النشط المختار ديناميكياً
paint.style = Paint.Style.FILL
paint.color = Color.parseColor("#2B2930")
canvas.drawCircle(cx, cy, radius * 0.22f, paint)

paint.color = AppThemeManager.accent // النواة تتغير بتغير الثيم
canvas.drawCircle(cx, cy, radius * 0.12f, paint)

paint.color = AppThemeManager.bgStart
canvas.drawCircle(cx, cy, radius * 0.04f, paint)

canvas.restore()
}

fun startSpinning() {
if (animator != null && animator!!.isRunning) return
animator = ValueAnimator.ofFloat(angle, angle + 360f).apply {
duration = 10000
repeatCount = ValueAnimator.INFINITE
interpolator = LinearInterpolator()
addUpdateListener {
angle = it.animatedValue as Float
invalidate()
}
start()
}
}

fun stopSpinning() {
animator?.cancel()
}
}

class AuraVisualizerView(context: Context) : View(context) {
private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
color = AppThemeManager.accent // لون ذبذبات المصور يتغير مع الثيم النشط
strokeWidth = 4f
strokeCap = Paint.Cap.ROUND
}
private var phase = 0f

override fun onDraw(canvas: Canvas) {
super.onDraw(canvas)
val w = width.toFloat()
val h = height.toFloat()
val midY = h / 2f
val points = 36
val spacing = w / points

paint.color = AppThemeManager.accent // تحديث فوري للألوان

phase += if (PlaybackManager.isPlaying) 0.12f else 0.02f

for (i in 0 until points) {
val x = i * spacing + spacing / 2
val multiplier = if (PlaybackManager.isPlaying) (18 + sin(i.toDouble() * 0.35 + phase) * 14).toFloat() else 3f
val barHeight = multiplier * PlaybackManager.currentSpeed
canvas.drawLine(x, midY - barHeight, x, midY + barHeight, paint)
}
invalidate()
}
}
}