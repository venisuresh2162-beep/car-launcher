package com.example.carlauncher

import android.content.Context

/** Remembers which installed app is assigned to each home-screen slot. */
class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("launcher", Context.MODE_PRIVATE)

    fun get(slot: Slot): String? = sp.getString(slot.prefKey, null)

    fun set(slot: Slot, packageName: String?) {
        sp.edit().putString(slot.prefKey, packageName).apply()
    }
}
