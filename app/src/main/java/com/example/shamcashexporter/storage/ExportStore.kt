package com.example.shamcashexporter.storage

import android.content.Context
import androidx.core.content.edit
import java.io.File

object ExportStore {
    private const val PREF = "export_store"
    private const val KEY_TARGET_PKG = "target_pkg"
    private const val KEY_ARMED = "armed"

    fun setTargetPackage(ctx: Context, pkg: String) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit {
            putString(KEY_TARGET_PKG, pkg)
        }
    }

    fun getTargetPackage(ctx: Context): String {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_TARGET_PKG, "com.shamcash.shamcash")!!
    }

    fun setArmed(ctx: Context, armed: Boolean) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_ARMED, armed)
        }
    }

    fun isArmed(ctx: Context): Boolean {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY_ARMED, false)
    }

    fun saveJsonToFile(ctx: Context, json: String): File {
        val dir = File(ctx.filesDir, "exports")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "shamcash_transactions.json")
        file.writeText(json, Charsets.UTF_8)
        return file
    }

    fun readLastFile(ctx: Context): String {
        val file = File(File(ctx.filesDir, "exports"), "shamcash_transactions.json")
        return if (file.exists()) file.readText(Charsets.UTF_8) else ""
    }
}
