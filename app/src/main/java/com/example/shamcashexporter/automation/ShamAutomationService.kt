package com.example.shamcashexporter.automation

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.shamcashexporter.storage.ExportStore
import com.google.gson.Gson
import java.util.regex.Pattern

class ShamAutomationService : AccessibilityService() {

    private val gson = Gson()
    private val TAB_NAME = "التحويلات"

    private val idPattern = Pattern.compile("(\d{6,})")
    private val dateTimePattern = Pattern.compile("(\d{4}/\d{2}/\d{2}).*(\d{2}:\d{2}:\d{2})")
    private val amountPattern = Pattern.compile("([+\-])\s*([0-9,]+)")

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!ExportStore.isArmed(this)) return
        val root = rootInActiveWindow ?: return
        if (root.packageName?.toString() != "com.shamcash.shamcash") return

        clickTransfersTab(root)
        val txs = parse(root)

        if (txs.isNotEmpty()) {
            val json = gson.toJson(mapOf("transactions" to txs))
            ExportStore.saveJsonToFile(this, json)
            ExportStore.setArmed(this, false)
            Log.d("ShamCashExporter", "Saved JSON")
        }
    }

    override fun onInterrupt() {}

    private fun clickTransfersTab(root: AccessibilityNodeInfo) {
        root.findAccessibilityNodeInfosByText(TAB_NAME)
            .firstOrNull()
            ?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    private fun parse(root: AccessibilityNodeInfo): List<Transaction> {
        val texts = mutableListOf<String>()
        collect(root, texts)
        val out = mutableListOf<Transaction>()

        for (i in texts.indices) {
            val id = idPattern.matcher(texts[i]).takeIf { it.find() }?.group(1)
            if (id != null) {
                val title = texts.getOrNull(i + 1)
                val dt = texts.getOrNull(i + 2)
                val amt = texts.getOrNull(i + 3)

                val dm = dateTimePattern.matcher(dt ?: "")
                val am = amountPattern.matcher(amt ?: "")

                out.add(
                    Transaction(
                        id = id,
                        title = title,
                        date = if (dm.find()) dm.group(1) else null,
                        time = if (dm.find()) dm.group(2) else null,
                        amount = if (am.find()) am.group(1) + am.group(2).replace(",", "") else null
                    )
                )
            }
        }
        return out
    }

    private fun collect(n: AccessibilityNodeInfo?, out: MutableList<String>) {
        if (n == null) return
        if (n.childCount == 0) n.text?.let { out.add(it.toString()) }
        for (i in 0 until n.childCount) collect(n.getChild(i), out)
    }
}
