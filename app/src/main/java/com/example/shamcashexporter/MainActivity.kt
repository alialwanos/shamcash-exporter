package com.example.shamcashexporter

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.example.shamcashexporter.databinding.ActivityMainBinding
import com.example.shamcashexporter.storage.ExportStore

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.etPackage.setText("com.shamcash.shamcash")

        b.btnAccessibility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        b.btnStart.setOnClickListener {
            ExportStore.setTargetPackage(this, "com.shamcash.shamcash")
            ExportStore.setArmed(this, true)
            b.tvStatus.text = "الحالة: يعمل…"

            val launch = packageManager.getLaunchIntentForPackage("com.shamcash.shamcash")
            launch?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(launch)
        }
    }

    override fun onResume() {
        super.onResume()
        b.tvJson.text = ExportStore.readLastFile(this)
    }
}
