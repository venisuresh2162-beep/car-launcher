package com.example.carlauncher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.carlauncher.ui.AppPicker
import com.example.carlauncher.ui.CarTheme
import com.example.carlauncher.ui.HomeScreen

private const val HIDE_BARS = false

class MainActivity : ComponentActivity() {

    private lateinit var prefs: Prefs
    private var pickingSlot by mutableStateOf<Slot?>(null)
    private var refresh by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs(this)
        hideSystemBars()

        setContent {
            CarTheme {
                val slot = pickingSlot
                if (slot == null) {
                    HomeScreen(prefs, refresh) { pickingSlot = it }
                } else {
                    BackHandler { pickingSlot = null }
                    AppPicker(
                        slot = slot,
                        onChoose = { pkg ->
                            prefs.set(slot, pkg)
                            refresh++
                            pickingSlot = null
                        },
                        onCancel = { pickingSlot = null }
                    )
                }
            }
        }
    }

    /** Pressing Home while already on the launcher closes the picker and shows the home screen. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pickingSlot = null
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        // Off by default: some head units rely on the system bar for Home/Back.
        // Set to true once you have confirmed your unit has other navigation buttons.
        if (!HIDE_BARS) return
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}
