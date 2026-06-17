package com.my.music.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import com.my.music.repeat.PlaybackManager
import com.my.music.ui.settings.AppThemeManager
import kotlin.math.sin

class SplashScreen(
context: Context,
private val onAnimationFinished: () -> Unit
) {

val view: View

init {
val root = FrameLayout(context).apply {
layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
// استخدام تدرج الثيم النشط المختار فوراً
background = PlaybackManager.createGlowGradient(AppThemeManager.bgStart, AppThemeManager.bgEnd)
}

// الأيقونة الحركية النيونية فائقة الفخامة المرسومة على الكانفاس
val animatedLogo = AnimatedAuraLogo(context).apply {
val size = (180 * context.resources.displayMetrics.density).toInt()
layoutParams = FrameLayout.LayoutParams(size, size).apply {
gravity = android.view.Gravity.CENTER
}
}
root.addView(animatedLogo)

// مؤقت الحركة ثم الانتقال التلقائي للواجهة الرئيسية بسلاسة
android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
animatedLogo.stopAnimation()
onAnimationFinished()
}, 2600) // زمن عرض الأنيميشن بدقة

this.view = root
}

// تصميم الأيقونة النيونية المتحركة برمجياً بالكامل
class AnimatedAuraLogo(context: Context) : View(context) {
private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
private var animFraction = 0f
private var pulseAnimator: ValueAnimator? = null
private val density = context.resources.displayMetrics.density

init {
startAnimation()
}

private fun startAnimation() {
pulseAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
duration = 1300
repeatCount = ValueAnimator.INFINITE
repeatMode = ValueAnimator.REVERSE
interpolator = AccelerateDecelerateInterpolator()
addUpdateListener {
animFraction = it.animatedValue as Float
invalidate()
}
start()
}
}

fun stopAnimation() {
pulseAnimator?.cancel()
}

override fun onDraw(canvas: Canvas) {
super.onDraw(canvas)
val cx = width / 2f
val cy = height / 2f
val baseRadius = width * 0.22f

// 1. الدائرة النيونية المتوهجة الخارجية (Outer Pulse Ring)
paint.style = Paint.Style.STROKE
paint.strokeWidth = 3f * density
paint.color = AppThemeManager.accent
paint.alpha = ((1f - animFraction) * 160).toInt() // تبهيت تدريجي عند الاتساع
canvas.drawCircle(cx, cy, baseRadius + (animFraction * 40 * density), paint)

// 2. الهالات الدائرية المتموجة الوسطى (Soundwave Ripple)
paint.alpha = 45
paint.strokeWidth = 2f * density
for (i in 1..3) {
val offset = sin(animFraction * Math.PI + i).toFloat() * 12 * density
canvas.drawCircle(cx, cy, baseRadius + offset, paint)
}

// 3. النواة النيونية المضيئة في المركز (Glowing Neon Core)
paint.style = Paint.Style.FILL
paint.color = AppThemeManager.accent
paint.alpha = 255
canvas.drawCircle(cx, cy, baseRadius - (5 * density), paint)

// 4. رسم النوتة الموسيقية المصغرة داخل النواة
paint.color = Color.BLACK
paint.style = Paint.Style.FILL
val noteRadius = 5 * density

// رأس النوتة اليمنى
canvas.drawCircle(cx + (4 * density), cy + (8 * density), noteRadius, paint)
// رأس النوتة اليسرى
canvas.drawCircle(cx - (10 * density), cy + (12 * density), noteRadius, paint)

// العواميد الرأسية للنوتة
paint.strokeWidth = 2.5f * density
paint.style = Paint.Style.STROKE
canvas.drawLine(cx + (8 * density), cy - (14 * density), cx + (8 * density), cy + (8 * density), paint)
canvas.drawLine(cx - (6 * density), cy - (10 * density), cx - (6 * density), cy + (12 * density), paint)

// الجسر الرابط العلوي المنحني
canvas.drawLine(cx - (6 * density), cy - (10 * density), cx + (8 * density), cy - (14 * density), paint)
}
}
}