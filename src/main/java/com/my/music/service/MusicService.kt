package com.my.music.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.IBinder
import com.my.music.MainActivity
import com.my.music.repeat.PlaybackManager
import com.my.music.repeat.Track

class MusicService : Service(), PlaybackManager.PlaybackListener {

private val CHANNEL_ID = "AURA_MUSIC_M3"
private val NOTIFICATION_ID = 9999

private var mediaSession: MediaSession? = null

override fun onCreate() {
super.onCreate()

mediaSession = MediaSession(this, "AuraMusicM3Session").apply {
isActive = true
}

createNotificationChannel()
PlaybackManager.registerListener(this)
}

override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
val action = intent?.action
if (action != null) {
when (action) {
"ACTION_PLAY_PAUSE" -> PlaybackManager.togglePlayPause(this)
"ACTION_NEXT" -> PlaybackManager.next(this)
"ACTION_PREV" -> PlaybackManager.prev(this)
"ACTION_STOP" -> {
PlaybackManager.stopPlayback()
stopSelf()
}
}
}
updateNotification()
return START_NOT_STICKY
}

// بناء وتحديث الإشعار وربط البيانات الحية بمحرك النظام الرسمي
private fun updateNotification() {
val track = PlaybackManager.getCurrentTrack()
val playPauseText = if (PlaybackManager.isPlaying) "إيقاف مؤقت" else "تشغيل"

// 1. حقن وتحديث حالة التشغيل (PlaybackState) لأندرويد 13/14 لإجبار النظام على إظهار الشريط
val stateBuilder = PlaybackState.Builder()
.setActions(
PlaybackState.ACTION_PLAY or
PlaybackState.ACTION_PAUSE or
PlaybackState.ACTION_PLAY_PAUSE or
PlaybackState.ACTION_SKIP_TO_NEXT or
PlaybackState.ACTION_SKIP_TO_PREVIOUS or
PlaybackState.ACTION_STOP
)
.setState(
if (PlaybackManager.isPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED,
PlaybackManager.getCurrentPosition().toLong(),
PlaybackManager.currentSpeed
)
mediaSession?.setPlaybackState(stateBuilder.build())

// 2. حقن وتحديث بيانات الملف (MediaMetadata) لإظهار العنوان وتوليد الخلفية الضبابية الملونة
val metadataBuilder = MediaMetadata.Builder()
.putString(MediaMetadata.METADATA_KEY_TITLE, track.title)
.putString(MediaMetadata.METADATA_KEY_ARTIST, track.artist)
.putLong(MediaMetadata.METADATA_KEY_DURATION, PlaybackManager.getDuration().toLong())
mediaSession?.setMetadata(metadataBuilder.build())

val notificationIntent = Intent(this, MainActivity::class.java)
val pendingIntent = PendingIntent.getActivity(
this, 0, notificationIntent,
PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

val playPauseIntent = Intent(this, MusicService::class.java).apply { action = "ACTION_PLAY_PAUSE" }
val pPlayPause = PendingIntent.getService(this, 1, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

val nextIntent = Intent(this, MusicService::class.java).apply { action = "ACTION_NEXT" }
val pNext = PendingIntent.getService(this, 2, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

val prevIntent = Intent(this, MusicService::class.java).apply { action = "ACTION_PREV" }
val pPrev = PendingIntent.getService(this, 3, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

val stopIntent = Intent(this, MusicService::class.java).apply { action = "ACTION_STOP" }
val pStop = PendingIntent.getService(this, 4, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
Notification.Builder(this, CHANNEL_ID)
} else {
Notification.Builder(this)
}

val notification = builder
.setContentTitle(track.title)
.setContentText(track.artist)
.setSmallIcon(android.R.drawable.ic_media_play)
.setContentIntent(pendingIntent)
.setOngoing(PlaybackManager.isPlaying)
.setVisibility(Notification.VISIBILITY_PUBLIC)
.apply {
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
addAction(Notification.Action.Builder(
android.R.drawable.ic_media_previous, "السابق", pPrev).build())
addAction(Notification.Action.Builder(
if (PlaybackManager.isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play, playPauseText, pPlayPause).build())
addAction(Notification.Action.Builder(
android.R.drawable.ic_media_next, "التالي", pNext).build())
addAction(Notification.Action.Builder(
android.R.drawable.ic_menu_close_clear_cancel, "إغلاق", pStop).build())
} else {
@Suppress("DEPRECATION")
addAction(android.R.drawable.ic_media_previous, "السابق", pPrev)
@Suppress("DEPRECATION")
addAction(if (PlaybackManager.isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play, playPauseText, pPlayPause)
@Suppress("DEPRECATION")
addAction(android.R.drawable.ic_media_next, "التالي", pNext)
@Suppress("DEPRECATION")
addAction(android.R.drawable.ic_menu_close_clear_cancel, "إغلاق", pStop)
}
}
.setStyle(Notification.MediaStyle()
.setMediaSession(mediaSession?.sessionToken)
.setShowActionsInCompactView(0, 1, 2))
.build()

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
} else {
startForeground(NOTIFICATION_ID, notification)
}
}

private fun createNotificationChannel() {
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
val channel = NotificationChannel(
CHANNEL_ID,
"Aura Music M3 Playback",
NotificationManager.IMPORTANCE_LOW
).apply {
description = "التحكم في صوتيات ماتريال 3 بالخلفية"
setShowBadge(false)
}
val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
manager.createNotificationChannel(channel)
}
}

override fun onDestroy() {
PlaybackManager.stopPlayback()
mediaSession?.release()
PlaybackManager.unregisterListener(this)
super.onDestroy()
}

override fun onBind(intent: Intent?): IBinder? = null
override fun onStateChanged() = updateNotification()
override fun onTrackChanged(track: Track) = updateNotification()
override fun onProgressUpdate(currentMs: Int, totalMs: Int) {}
}