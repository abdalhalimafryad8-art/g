package com.my.music

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.my.music.repeat.PlaybackManager
import com.my.music.ui.MainScreen
import com.my.music.ui.SplashScreen
import com.my.music.ui.VideoScreen
import com.my.music.ui.AboutScreen
import com.my.music.ui.M3Effects
import com.my.music.ui.settings.AppThemeManager
import com.my.music.ui.settings.AppLocaleManager
import com.my.music.ui.settings.SettingsScreen
import com.my.music.ui.settings.SubSettingsScreen
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : AppCompatActivity() {

private lateinit var mainContainer: FrameLayout
private val PERMISSION_REQUEST_CODE = 1000
private var currentMainScreen: MainScreen? = null
private var activePlayerView: View? = null

override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)

setupCrashHandler()

AppLocaleManager.init(this)
AppThemeManager.loadTheme(this)
applyLaunchSystemTheme()

mainContainer = FrameLayout(this).apply {
layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
background = PlaybackManager.createGlowGradient(AppThemeManager.bgStart, AppThemeManager.bgEnd)
}
setContentView(mainContainer)

requestDynamicPermissions()
startMusicBackgroundService()

if (savedInstanceState == null) {
navigateToSplash()
} else {
navigateToSettings()
}
}

private fun applyLaunchSystemTheme() {
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
window.statusBarColor = AppThemeManager.bgStart
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
val decorView = window.decorView
var flags = decorView.systemUiVisibility
flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
decorView.systemUiVisibility = flags
}
}

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
splashScreen.setOnExitAnimationListener { splashScreenView ->
splashScreenView.remove()
}
}
}

private fun startMusicBackgroundService() {
try {
val serviceClass = Class.forName("com.my.music.service.MusicService")
val serviceIntent = Intent(this, serviceClass)
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
startForegroundService(serviceIntent)
} else {
startService(serviceIntent)
}
} catch (e: ClassNotFoundException) {
e.printStackTrace()
} catch (e: Exception) {
e.printStackTrace()
}
}

private fun requestDynamicPermissions() {
val permissions = mutableListOf<String>()
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
permissions.add(Manifest.permission.POST_NOTIFICATIONS)
} else {
permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
}

val missingPermissions = permissions.filter {
ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
}

if (missingPermissions.isNotEmpty()) {
ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
} else {
PlaybackManager.scanLocalMusic(this)
}
}

override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
super.onRequestPermissionsResult(requestCode, permissions, grantResults)
if (requestCode == PERMISSION_REQUEST_CODE) {
if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
PlaybackManager.scanLocalMusic(this)
currentMainScreen?.refreshTrackList()
}
}
}

private fun setScreen(screenView: View) {
mainContainer.removeAllViews()
mainContainer.addView(screenView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
}

private fun navigateToSplash() {
val splashScreen = SplashScreen(this) {
navigateToMain()
}
setScreen(splashScreen.view)
}

// الانتقال للرئيسية مع حقن التأثير الدوار للتناظر الحركي عند الخروج من الإعدادات
private fun navigateToMain() {
val mainScreen = MainScreen(this,
onTrackSelected = { _ ->
navigateToPlayer()
},
onSettingsClicked = {
navigateToSettings()
}
)
currentMainScreen = mainScreen
setScreen(mainScreen.view)

// تأثير ماتريال 3 الدوار المتزامن لمراعاة الدخول والرجوع بشكل متطابق
M3Effects.rotateAndScaleIn(mainScreen.view)
}

private fun navigateToPlayer() {
val screenHeight = resources.displayMetrics.heightPixels.toFloat()

val videoScreen = VideoScreen(this) {
activePlayerView?.let { player ->
M3Effects.slideDown(player, screenHeight) {
navigateToMain()
}
}
}

activePlayerView = videoScreen.view
setScreen(videoScreen.view)
M3Effects.slideUp(videoScreen.view, screenHeight)
}

private fun navigateToSettings() {
val settingsScreen = SettingsScreen(this,
onBackClicked = {
navigateToMain()
},
onAdvanceSettingsClicked = {
navigateToSubSettings()
},
onAboutClicked = {
navigateToAbout()
}
)
setScreen(settingsScreen.view)
M3Effects.rotateAndScaleIn(settingsScreen.view)
}

private fun navigateToAbout() {
val aboutScreen = AboutScreen(this) {
navigateToSettings()
}
setScreen(aboutScreen.view)
M3Effects.rotateAndScaleIn(aboutScreen.view)
}

private fun navigateToSubSettings() {
val subSettingsScreen = SubSettingsScreen(this) {
navigateToSettings()
}
setScreen(subSettingsScreen.view)
}

private fun setupCrashHandler() {
Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
runOnUiThread {
try {
val sw = StringWriter()
val pw = PrintWriter(sw)
throwable.printStackTrace(pw)
val stackTraceString = sw.toString()

val crashRoot = ScrollView(this).apply {
layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
setBackgroundColor(Color.BLACK)
setPadding(30, 50, 30, 50)
}

val container = LinearLayout(this).apply {
orientation = LinearLayout.VERTICAL
}

val terminalHeader = TextView(this).apply {
text = "== CRASH REPORT TERMINAL v1.0.0 =="
setTextColor(Color.parseColor("#00FF00"))
textSize = 18f
typeface = Typeface.MONOSPACE
setPadding(0, 0, 0, 30)
}

val exceptionMsg = TextView(this).apply {
text = "Exception: ${throwable.localizedMessage}\n"
setTextColor(Color.WHITE)
textSize = 14f
typeface = Typeface.MONOSPACE
setPadding(0, 0, 0, 20)
}

val traceView = TextView(this).apply {
text = stackTraceString
setTextColor(Color.parseColor("#00FF00"))
textSize = 11f
typeface = Typeface.MONOSPACE
setPadding(0, 0, 0, 40)
}

val restartBtn = Button(this).apply {
text = "RESTART SYSTEM [SAFE MODE]"
setBackgroundColor(Color.parseColor("#1B5E20"))
setTextColor(Color.WHITE)
typeface = Typeface.MONOSPACE
setPadding(20, 20, 20, 20)
setOnClickListener {
val intent = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
startActivity(intent)
finish()
android.os.Process.killProcess(android.os.Process.myPid())
}
}

container.addView(terminalHeader)
container.addView(exceptionMsg)
container.addView(traceView)
container.addView(restartBtn)
crashRoot.addView(container)

setContentView(crashRoot)
} catch (e: Exception) {
e.printStackTrace()
}
}
}
}
}