package com.my.music.repeat

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import java.util.concurrent.CopyOnWriteArrayList

data class Track(
val id: Int,
val title: String,
val artist: String,
val url: String,
val durationText: String,
val lyrics: List<String>,
val startColor: Int,
val endColor: Int
)

object PlaybackManager {
private var mediaPlayer: MediaPlayer? = null
val playlist = ArrayList<Track>()
var currentTrackIndex = 0
var isPlaying = false
var currentSpeed = 1.0f
var sleepTimerRemainingSeconds = 0

private val listeners = CopyOnWriteArrayList<PlaybackListener>()
private val handler = Handler(Looper.getMainLooper())
private var sleepRunnable: Runnable? = null

interface PlaybackListener {
fun onStateChanged()
fun onTrackChanged(track: Track)
fun onProgressUpdate(currentMs: Int, totalMs: Int)
}

init {
setupProgressPoller()
}

fun scanLocalMusic(context: Context) {
playlist.clear()
val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
val projection = arrayOf(
MediaStore.Audio.Media._ID,
MediaStore.Audio.Media.TITLE,
MediaStore.Audio.Media.ARTIST,
MediaStore.Audio.Media.DURATION,
MediaStore.Audio.Media.DATA
)
val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

try {
context.contentResolver.query(uri, projection, selection, null, null)?.use { cursor ->
val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
val durCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

while (cursor.moveToNext()) {
val id = cursor.getInt(idCol)
val title = cursor.getString(titleCol) ?: "مقطع غير معروف"
val artist = cursor.getString(artistCol) ?: "فنان غير معروف"
val durationMs = cursor.getInt(durCol)
val path = cursor.getString(dataCol) ?: ""

val secs = durationMs / 1000
val mins = secs / 60
val durText = String.format("%02d:%02d", mins % 60, secs % 60)

playlist.add(
Track(
id = id,
title = title,
artist = artist,
url = path,
durationText = durText,
lyrics = listOf("كلمات متزامنة للملف المحلي..", "استمتع بالصوت العالي.."),
startColor = Color.parseColor("#4F378B"),
endColor = Color.parseColor("#1C1B2F")
)
)
}
}
} catch (e: Exception) {
e.printStackTrace()
}

if (playlist.isEmpty()) {
playlist.add(
Track(
id = -1,
title = "موجة النيون الافتراضية",
artist = "مكتبة أورا المدمجة",
url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
durationText = "06:12",
lyrics = listOf("نغمة نيون هادئة تحلق في الأفق..", "ألحان الفضاء ترحب بك.."),
startColor = Color.parseColor("#6750A4"),
endColor = Color.parseColor("#21005D")
)
)
}
notifyState()
}

fun registerListener(listener: PlaybackListener) {
if (!listeners.contains(listener)) {
listeners.add(listener)
if (playlist.isNotEmpty() && currentTrackIndex < playlist.size) {
listener.onTrackChanged(getCurrentTrack())
}
}
}

fun unregisterListener(listener: PlaybackListener) {
listeners.remove(listener)
}

fun getCurrentTrack(): Track {
return if (playlist.isNotEmpty() && currentTrackIndex < playlist.size) {
playlist[currentTrackIndex]
} else {
Track(-1, "لا يوجد مسار", "فنان", "", "00:00", emptyList(), Color.BLACK, Color.BLACK)
}
}

fun playTrack(context: Context, index: Int) {
if (playlist.isEmpty()) return
currentTrackIndex = (index + playlist.size) % playlist.size
val track = getCurrentTrack()

try {
mediaPlayer?.release()
mediaPlayer = MediaPlayer().apply {
setDataSource(track.url)
prepareAsync()
setOnPreparedListener { mp ->
mp.playbackParams = mp.playbackParams.setSpeed(currentSpeed)
mp.start()
PlaybackManager.isPlaying = true
notifyState()
notifyTrack(track)
}
setOnCompletionListener {
next(context)
}
}
} catch (e: Exception) {
e.printStackTrace()
}
}

fun togglePlayPause(context: Context) {
val mp = mediaPlayer
if (mp != null) {
if (mp.isPlaying) {
mp.pause()
PlaybackManager.isPlaying = false
} else {
mp.start()
PlaybackManager.isPlaying = true
}
notifyState()
} else {
playTrack(context, currentTrackIndex)
}
}

// إغلاق مطلق فوري وحاد لتحرير بطاقة الصوت فوراً بنسبة 100% دون أي تأخير
fun stopPlayback() {
try {
mediaPlayer?.let {
if (it.isPlaying) {
it.pause()
}
it.stop()
it.release()
}
mediaPlayer = null
PlaybackManager.isPlaying = false
notifyState()
} catch (e: Exception) {
e.printStackTrace()
}
}

fun next(context: Context) {
playTrack(context, currentTrackIndex + 1)
}

fun prev(context: Context) {
playTrack(context, currentTrackIndex - 1)
}

fun seekTo(progressMs: Int) {
mediaPlayer?.seekTo(progressMs)
}

fun getDuration(): Int = mediaPlayer?.duration ?: 0
fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

fun setSpeed(speed: Float) {
currentSpeed = speed
mediaPlayer?.let {
if (it.isPlaying) {
try {
it.playbackParams = it.playbackParams.setSpeed(speed)
} catch (e: Exception) {
e.printStackTrace()
}
}
}
notifyState()
}

fun setSleepTimer(minutes: Int) {
sleepRunnable?.let { handler.removeCallbacks(it) }
if (minutes == 0) {
sleepTimerRemainingSeconds = 0
notifyState()
return
}
sleepTimerRemainingSeconds = minutes * 60
val runnable = object : Runnable {
override fun run() {
if (sleepTimerRemainingSeconds > 0) {
sleepTimerRemainingSeconds--
if (sleepTimerRemainingSeconds == 0) {
stopPlayback()
} else {
handler.postDelayed(this, 1000)
}
notifyState()
}
}
}
sleepRunnable = runnable
handler.post(runnable)
}

private fun setupProgressPoller() {
handler.post(object : Runnable {
override fun run() {
val mp = mediaPlayer
if (mp != null && PlaybackManager.isPlaying) {
val current = mp.currentPosition
val total = mp.duration
for (l in listeners) {
l.onProgressUpdate(current, total)
}
}
handler.postDelayed(this, 1000)
}
})
}

private fun notifyState() {
for (l in listeners) l.onStateChanged()
}

private fun notifyTrack(track: Track) {
for (l in listeners) l.onTrackChanged(track)
}

fun createGlowGradient(startColor: Int, endColor: Int): GradientDrawable {
return GradientDrawable(
GradientDrawable.Orientation.TOP_BOTTOM,
intArrayOf(startColor, endColor)
).apply {
cornerRadius = 0f
gradientType = GradientDrawable.LINEAR_GRADIENT
}
}

fun createMaterialM3Drawable(backgroundColor: Int, cornerRadiusDp: Float): MaterialShapeDrawable {
val shapeAppearanceModel = ShapeAppearanceModel.builder()
.setAllCorners(CornerFamily.ROUNDED, cornerRadiusDp)
.build()
return MaterialShapeDrawable(shapeAppearanceModel).apply {
fillColor = ColorStateList.valueOf(backgroundColor)
}
}
}