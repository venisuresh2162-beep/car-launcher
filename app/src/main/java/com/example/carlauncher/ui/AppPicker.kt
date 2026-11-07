package com.example.carlauncher.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.carlauncher.AppInfo
import com.example.carlauncher.Apps
import com.example.carlauncher.Slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Full-screen list of installed apps. Pick the one that should open for [slot]
 * (for example, whichever app on your unit handles Android Auto / CarPlay).
 */
@Composable
fun AppPicker(
    slot: Slot,
    onChoose: (String?) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<AppInfo>?>(null) }

    LaunchedEffect(Unit) {
        apps = withContext(Dispatchers.IO) { Apps.installed(context) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Palette.Bg)
            .padding(horizontal = 28.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Choose app for ${slot.title}",
                color = Palette.TextPrimary,
                fontSize = 30.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PillButton("Clear") { onChoose(null) }
                PillButton("Cancel", onClick = onCancel)
            }
        }

        val list = apps
        if (list == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading apps...", color = Palette.TextDim, fontSize = 20.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(list, key = { it.packageName }) { app ->
                    AppCell(app) { onChoose(app.packageName) }
                }
            }
        }
    }
}

@Composable
private fun PillButton(text: String, onClick: () -> Unit) {
    Text(
        text,
        color = Palette.TextPrimary,
        fontSize = 18.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Palette.Card)
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 12.dp)
    )
}

@Composable
private fun AppCell(app: AppInfo, onClick: () -> Unit) {
    // Convert the icon once per app, not on every recomposition.
    val bitmap = remember(app.packageName) { app.icon.toBitmap(96, 96).asImageBitmap() }
    Column(
        Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Palette.Card)
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Image(bitmap = bitmap, contentDescription = null, modifier = Modifier.size(56.dp))
        Text(
            app.label,
            color = Palette.TextPrimary,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
