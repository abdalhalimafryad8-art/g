package com.my.music.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.my.music.repeat.PlaybackManager
import com.my.music.repeat.Track
import com.my.music.ui.settings.AppThemeManager
import com.my.music.ui.settings.AppLocaleManager

class MainScreen(
private val context: Context,
private val onTrackSelected: (Track) -> Unit,
private val onSettingsClicked: () -> Unit
) : PlaybackManager.PlaybackListener {

val view: View
private val tracksContainer: LinearLayout
private val miniPlayer: MaterialCardView
private val miniTitle: TextView
private val miniArtist: TextView
private val miniPlayPause: ImageView
private var allTracks = ArrayList<Track>()

private val density = context.resources.displayMetrics.density

init {
val root = LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
background = PlaybackManager.createGlowGradient(AppThemeManager.bgStart, AppThemeManager.bgEnd)
}

val header = LinearLayout(context).apply {
orientation = LinearLayout.HORIZONTAL
gravity = Gravity.CENTER_VERTICAL
setPadding((24 * density).toInt(), (40 * density).toInt(), (24 * density).toInt(), (16 * density).toInt())
}

val titleView = TextView(context).apply {
text = AppLocaleManager.getString("app_name")
textSize = 20f
setTextColor(Color.WHITE)
typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
}

val settingsBtn = ImageView(context).apply {
val drawable = context.resources.getDrawable(com.my.music.R.drawable.ic, null)
drawable.setLevel(5)
setImageDrawable(drawable)
setColorFilter(AppThemeManager.accent)
setPadding((10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt())
background = PlaybackManager.createMaterialM3Drawable(AppThemeManager.cardBg, 18 * density)
layoutParams = LinearLayout.LayoutParams((38 * density).toInt(), (38 * density).toInt())
setOnClickListener { onSettingsClicked() }
}

header.addView(titleView)
header.addView(settingsBtn)
root.addView(header)

val searchContainer = MaterialCardView(context).apply {
radius = 24 * density
cardElevation = 0f
strokeWidth = 0
setCardBackgroundColor(ColorStateList.valueOf(AppThemeManager.cardBg))
val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (46 * density).toInt()).apply {
setMargins((24 * density).toInt(), 0, (24 * density).toInt(), (20 * density).toInt())
}
layoutParams = params
}

val searchLayout = LinearLayout(context).apply {
orientation = LinearLayout.HORIZONTAL
gravity = Gravity.CENTER_VERTICAL
setPadding((16 * density).toInt(), 0, (16 * density).toInt(), 0)
}

val searchIcon = ImageView(context).apply {
val drawable = context.resources.getDrawable(com.my.music.R.drawable.ic, null)
drawable.setLevel(9)
setImageDrawable(drawable)
setColorFilter(Color.parseColor("#938F99"))
layoutParams = LinearLayout.LayoutParams((18 * density).toInt(), (18 * density).toInt())
}

val searchEditText = EditText(context).apply {
hint = AppLocaleManager.getString("search_hint")
setHintTextColor(Color.parseColor("#938F99"))
setTextColor(Color.WHITE)
textSize = 14f
background = null
setPadding((12 * density).toInt(), 0, (12 * density).toInt(), 0)
layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
addTextChangedListener(object : TextWatcher {
override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
filterTracks(s.toString())
}
override fun afterTextChanged(s: Editable?) {}
})
}

searchLayout.addView(searchIcon)
searchLayout.addView(searchEditText)
searchContainer.addView(searchLayout)
root.addView(searchContainer)

val scrollView = ScrollView(context).apply {
layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
}

tracksContainer = LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
setPadding((24 * density).toInt(), (8 * density).toInt(), (24 * density).toInt(), (24 * density).toInt())
}

scrollView.addView(tracksContainer)
root.addView(scrollView)

// الشريط السفلي العائم - المنفذ الوحيد المعتمد لفتح مشغل القرص التفاعلي
miniPlayer = MaterialCardView(context).apply {
radius = 20 * density
cardElevation = 6 * density
strokeWidth = 1
setStrokeColor(ColorStateList.valueOf(Color.parseColor("#2D2B32")))
setCardBackgroundColor(ColorStateList.valueOf(AppThemeManager.cardBg))
val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (64 * density).toInt()).apply {
setMargins(16, 8, 16, 16)
}
layoutParams = params
visibility = View.GONE
setOnClickListener {
// فتح شاشة القرص الدوار عند الضغط هنا فقط
onTrackSelected(PlaybackManager.getCurrentTrack())
}
}

val innerLayout = LinearLayout(context).apply {
orientation = LinearLayout.HORIZONTAL
gravity = Gravity.CENTER_VERTICAL
setPadding((16 * density).toInt(), 0, (16 * density).toInt(), 0)
}

val miniArtwork = ImageView(context).apply {
val drawable = context.resources.getDrawable(com.my.music.R.drawable.ic, null)
drawable.setLevel(4)
setImageDrawable(drawable)
setColorFilter(Color.WHITE)
setPadding((10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt())
background = PlaybackManager.createMaterialM3Drawable(AppThemeManager.accent, 12 * density)
layoutParams = LinearLayout.LayoutParams((40 * density).toInt(), (40 * density).toInt())
}

val textLayout = LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
setMargins((16 * density).toInt(), 0, (16 * density).toInt(), 0)
}
}

miniTitle = TextView(context).apply {
text = "..."
textSize = 14f
setTextColor(Color.WHITE)
typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
maxLines = 1
ellipsize = TextUtils.TruncateAt.END
}

miniArtist = TextView(context).apply {
text = "..."
textSize = 11f
setTextColor(Color.parseColor("#938F99"))
maxLines = 1
ellipsize = TextUtils.TruncateAt.END
}

textLayout.addView(miniTitle)
textLayout.addView(miniArtist)

miniPlayPause = ImageView(context).apply {
val drawable = context.resources.getDrawable(com.my.music.R.drawable.ic, null)
drawable.setLevel(0)
setImageDrawable(drawable)
setColorFilter(Color.BLACK)
setPadding((8 * density).toInt(), (8 * density).toInt(), (8 * density).toInt(), (8 * density).toInt())
background = PlaybackManager.createMaterialM3Drawable(AppThemeManager.accent, 50f)
layoutParams = LinearLayout.LayoutParams((34 * density).toInt(), (34 * density).toInt())
setOnClickListener {
PlaybackManager.togglePlayPause(context)
}
}

innerLayout.addView(miniArtwork)
innerLayout.addView(textLayout)
innerLayout.addView(miniPlayPause)
miniPlayer.addView(innerLayout)

root.addView(miniPlayer)
this.view = root

refreshTrackList()
PlaybackManager.registerListener(this)
}

fun refreshTrackList() {
allTracks.clear()
allTracks.addAll(PlaybackManager.playlist)
renderTracks(allTracks)
}

private fun renderTracks(tracksList: List<Track>) {
tracksContainer.removeAllViews()
for ((index, track) in tracksList.withIndex()) {

val itemCard = MaterialCardView(context).apply {
radius = 16 * density
cardElevation = 0f
setCardBackgroundColor(ColorStateList.valueOf(AppThemeManager.cardBg))
val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (64 * density).toInt()).apply {
setMargins(0, 0, 0, (6 * density).toInt())
}
layoutParams = params
setOnClickListener {
val originalIndex = PlaybackManager.playlist.indexOf(track)
if (originalIndex != -1) {
// تشغيل الموسيقى بالخلفية فوراً دون مغادرة الصفحة
PlaybackManager.playTrack(context, originalIndex)
}
}
}

val itemLayout = LinearLayout(context).apply {
orientation = LinearLayout.HORIZONTAL
gravity = Gravity.CENTER_VERTICAL
setPadding((12 * density).toInt(), 0, (12 * density).toInt(), 0)
}

val trackThumbnail = ImageView(context).apply {
val drawable = context.resources.getDrawable(com.my.music.R.drawable.ic, null)
drawable.setLevel(4)
setImageDrawable(drawable)
setColorFilter(AppThemeManager.accent)
setPadding((10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt())
background = PlaybackManager.createGlowGradient(
track.startColor,
AppThemeManager.cardBg
).apply {
cornerRadius = 12 * density
}
layoutParams = LinearLayout.LayoutParams((42 * density).toInt(), (42 * density).toInt())
}

val textWrapper = LinearLayout(context).apply {
orientation = LinearLayout.VERTICAL
gravity = Gravity.CENTER_VERTICAL
layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
setMargins((16 * density).toInt(), 0, (16 * density).toInt(), 0)
}
}

val trackName = TextView(context).apply {
text = track.title
textSize = 14f
setTextColor(Color.WHITE)
typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
maxLines = 1
ellipsize = TextUtils.TruncateAt.END
textDirection = View.TEXT_DIRECTION_LOCALE
}

val artistName = TextView(context).apply {
text = track.artist
textSize = 11f
setTextColor(Color.parseColor("#938F99"))
maxLines = 1
ellipsize = TextUtils.TruncateAt.END
textDirection = View.TEXT_DIRECTION_LOCALE
}

textWrapper.addView(trackName)
textWrapper.addView(artistName)

val durationView = TextView(context).apply {
text = track.durationText
textSize = 11f
setTextColor(Color.parseColor("#938F99"))
typeface = Typeface.MONOSPACE
gravity = Gravity.END
}

itemLayout.addView(trackThumbnail)
itemLayout.addView(textWrapper)
itemLayout.addView(durationView)
itemCard.addView(itemLayout)

tracksContainer.addView(itemCard)
}
}

private fun filterTracks(query: String) {
if (query.isEmpty()) {
renderTracks(allTracks)
} else {
val filtered = allTracks.filter {
it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
}
renderTracks(filtered)
}
}

override fun onStateChanged() {
miniPlayPause.drawable?.let {
it.setLevel(if (PlaybackManager.isPlaying) 1 else 0)
}
}

override fun onTrackChanged(track: Track) {
miniPlayer.visibility = View.VISIBLE
miniTitle.text = track.title
miniArtist.text = track.artist
onStateChanged()
}

override fun onProgressUpdate(currentMs: Int, totalMs: Int) {}

fun destroy() {
PlaybackManager.unregisterListener(this)
}
}