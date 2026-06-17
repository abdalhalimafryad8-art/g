package com.my.music.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout

object M3Effects {
private val decelerate = DecelerateInterpolator()
private val overshoot = OvershootInterpolator(1.15f)

// تأثير الدوران والاتساع الماتريالي الفخم عند فتح الإعدادات أو المطور (radial scale reveal)
fun rotateAndScaleIn(view: View, durationMs: Long = 400) {
view.alpha = 0f
view.scaleX = 0.82f
view.scaleY = 0.82f
view.rotation = -6f // زاوية ميلان خفيفة جداً أثناء الدوران والاتساع

view.animate()
.alpha(1f)
.scaleX(1f)
.scaleY(1f)
.rotation(0f)
.setDuration(durationMs)
.setInterpolator(overshoot)
.start()
}

// تأثير الطلوع الفخم العائم للمشغل (Luxury Bottom Sheet Slide-Up)
fun slideUp(view: View, screenHeight: Float, durationMs: Long = 450, onEnd: () -> Unit = {}) {
view.translationY = screenHeight
view.alpha = 0.9f

view.animate()
.translationY(0f)
.alpha(1f)
.setDuration(durationMs)
.setInterpolator(decelerate)
.setListener(object : AnimatorListenerAdapter() {
override fun onAnimationEnd(animation: Animator) {
onEnd()
}
})
.start()
}

// تأثير الانزلاق لأسفل عند تنحية أو إخلاق واجهة القرص (Slide-Down Dismiss)
fun slideDown(view: View, screenHeight: Float, durationMs: Long = 400, onEnd: () -> Unit = {}) {
view.animate()
.translationY(screenHeight)
.alpha(0.9f)
.setDuration(durationMs)
.setInterpolator(decelerate)
.setListener(object : AnimatorListenerAdapter() {
override fun onAnimationEnd(animation: Animator) {
onEnd()
}
})
.start()
}

// محاكاة تأثير الـ GIF برمجياً: تمدد حجم الكرت المختار وتقلص وزحمة الكروت الأخرى (Focal Carousel Animation)
fun animateM3CarouselWeights(cards: List<View>, selectedIndex: Int) {
for ((index, card) in cards.withIndex()) {
val params = card.layoutParams as LinearLayout.LayoutParams
val startWeight = params.weight

// كرت التحديد يتسع وزنه برياضيات مطاطية، بينما تتقلص الكروت الأخرى وتزاح جانباً (زحمة)
val endWeight = if (index == selectedIndex) 1.9f else 0.65f

val animator = ValueAnimator.ofFloat(startWeight, endWeight).apply {
duration = 380
interpolator = decelerate
addUpdateListener {
val lp = card.layoutParams as LinearLayout.LayoutParams
lp.weight = it.animatedValue as Float
card.layoutParams = lp
}
}
animator.start()
}
}
}