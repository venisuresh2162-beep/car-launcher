package com.example.carlauncher.ui

import android.text.format.DateFormat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carlauncher.Apps
import com.example.carlauncher.Prefs
import com.example.carlauncher.Slot
import kotlinx.coroutines.delay
import java.util.Date

/**
 * Layout (designed for 1024x600):
 *  - top bar: clock + date + settings
 *  - big row: Android Auto and CarPlay tiles (the main use)
 *  - bottom row: Maps, Music, Radio, Camera
 * Tap = open the assigned app. Long-press = change which app is assigned.
 */
@Composable
fun HomeScreen(prefs: Prefs, refresh: Int, onPick: (Slot) -> Unit) {
    val context = LocalContext.current
    // `refresh` is bumped by MainActivity whenever an assignment changes,
    // so tiles re-read their labels.
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(2500)
            toast = null
        }
    }

    fun open(slot: Slot) {
        val pkg = prefs.get(slot)
        if (pkg == null) {
            onPick(slot)                       // nothing assigned yet: let the user choose
        } else if (!Apps.launch(context, pkg)) {
            toast = "${slot.title}: app not found. Long-press to choose another."
        }
    }

    Box(Modifier.fillMaxSize().background(Palette.Bg)) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            TopBar()

            Row(
                Modifier.fillMaxWidth().weight(1.5f),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                BigTile(Slot.ANDROID_AUTO, Palette.Accent, prefs, refresh, { open(Slot.ANDROID_AUTO) }, { onPick(Slot.ANDROID_AUTO) })
                BigTile(Slot.CARPLAY, Palette.AccentAlt, prefs, refresh, { open(Slot.CARPLAY) }, { onPick(Slot.CARPLAY) })
            }

            Row(
                Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                SmallTile(Slot.MAPS, prefs, refresh, { open(Slot.MAPS) }, { onPick(Slot.MAPS) })
                SmallTile(Slot.MUSIC, prefs, refresh, { open(Slot.MUSIC) }, { onPick(Slot.MUSIC) })
                SmallTile(Slot.RADIO, prefs, refresh, { open(Slot.RADIO) }, { onPick(Slot.RADIO) })
                SmallTile(Slot.CAMERA, prefs, refresh, { open(Slot.CAMERA) }, { onPick(Slot.CAMERA) })
            }
        }

        toast?.let {
            Text(
                it,
                color = Palette.TextPrimary,
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Palette.CardPressed)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun TopBar() {
    val context = LocalContext.current
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(15_000)
        }
    }
    val time = DateFormat.getTimeFormat(context).format(now)
    val date = DateFormat.format("EEEE, d MMMM", now).toString()

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(time, color = Palette.TextPrimary, fontSize = 44.sp, fontWeight = FontWeight.Light)
            Text(date, color = Palette.TextDim, fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
        }
        Text(
            "Settings",
            color = Palette.TextDim,
            fontSize = 18.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .combinedClickable(onClick = { Apps.openSystemSettings(context) })
                .padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RowScope.BigTile(
    slot: Slot,
    accent: Color,
    prefs: Prefs,
    refresh: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val context = LocalContext.current
    val assigned = remember(refresh) { Apps.label(context, prefs.get(slot)) }
    Box(
        Modifier
            .weight(1f)
            .fillMaxSize()
            .clip(RoundedCornerShape(28.dp))
            .background(Palette.Card)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        // Accent strip along the left edge
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .padding(vertical = 28.dp)
                .fillMaxHeight()
                .width(5.dp)
                .background(accent, RoundedCornerShape(4.dp))
        )
        Column(
            Modifier.align(Alignment.CenterStart).padding(start = 36.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(slot.title, color = Palette.TextPrimary, fontSize = 40.sp, fontWeight = FontWeight.SemiBold)
            Text(
                assigned ?: "Tap to choose app",
                color = if (assigned == null) accent else Palette.TextDim,
                fontSize = 20.sp
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RowScope.SmallTile(
    slot: Slot,
    prefs: Prefs,
    refresh: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val context = LocalContext.current
    val assigned = remember(refresh) { Apps.label(context, prefs.get(slot)) }
    Box(
        Modifier
            .weight(1f)
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Palette.Card)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(slot.title, color = Palette.TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Medium)
            Text(assigned ?: "Tap to choose", color = Palette.TextDim, fontSize = 15.sp)
        }
    }
}
