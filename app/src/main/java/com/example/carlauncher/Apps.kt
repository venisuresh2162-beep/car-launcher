package com.example.carlauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.provider.Settings

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable
)

/** The slots on the home screen. The user assigns an installed app to each one. */
enum class Slot(val title: String, val prefKey: String) {
    ANDROID_AUTO("Android Auto", "slot_android_auto"),
    CARPLAY("CarPlay", "slot_carplay"),
    MAPS("Maps", "slot_maps"),
    MUSIC("Music", "slot_music"),
    RADIO("Radio", "slot_radio"),
    CAMERA("Camera", "slot_camera")
}

object Apps {

    /** Every app that has a launcher icon, except this launcher itself. */
    fun installed(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return pm.queryIntentActivities(intent, 0)
            .asSequence()
            .map { it.activityInfo.packageName to it }
            .filter { (pkg, _) -> pkg != context.packageName }
            .distinctBy { it.first }
            .map { (pkg, ri) -> AppInfo(ri.loadLabel(pm).toString(), pkg, ri.loadIcon(pm)) }
            .sortedBy { it.label.lowercase() }
            .toList()
    }

    fun launch(context: Context, packageName: String?): Boolean {
        if (packageName.isNullOrBlank()) return false
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun label(context: Context, packageName: String?): String? {
        if (packageName.isNullOrBlank()) return null
        return try {
            val pm = context.packageManager
            pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun openSystemSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
