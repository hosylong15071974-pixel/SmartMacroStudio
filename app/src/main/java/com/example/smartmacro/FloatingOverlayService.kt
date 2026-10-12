package com.example.smartmacro

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Color as AndroidColor
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

class FloatingOverlayService : Service() {
    private lateinit var wm: WindowManager
    private var handle: View? = null
    private var panel: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return }
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        createHandle()
    }

    private fun layoutParams(width: Int, height: Int): WindowManager.LayoutParams {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
        return WindowManager.LayoutParams(width, height, type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.TOP or Gravity.END
            y = 280
        }
    }

    private fun createHandle() {
        val v = TextView(this).apply {
            text = "‹"; textSize = 28f; gravity = Gravity.CENTER; setTextColor(AndroidColor.WHITE)
            background = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(0xFF7657FF.toInt(), 0xFFFF5E9A.toInt())).apply {
                cornerRadii = floatArrayOf(28f,28f,0f,0f,0f,0f,28f,28f)
            }
            setPadding(12, 22, 12, 22)
        }
        val lp = layoutParams(54, 112)
        v.setOnTouchListener(object : View.OnTouchListener {
            var downX = 0f; var downY = 0f; var startX = 0; var startY = 0; var moved = false
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { downX = event.rawX; downY = event.rawY; startX = lp.x; startY = lp.y; moved = false; return true }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - downX).toInt(); val dy = (event.rawY - downY).toInt()
                        if (kotlin.math.abs(dx) + kotlin.math.abs(dy) > 10) moved = true
                        lp.x = (startX - dx).coerceAtLeast(0); lp.y = (startY + dy).coerceAtLeast(0)
                        wm.updateViewLayout(v, lp); return true
                    }
                    MotionEvent.ACTION_UP -> { if (!moved) togglePanel(); return true }
                }
                return false
            }
        })
        handle = v
        wm.addView(v, lp)
    }

    private fun togglePanel() {
        panel?.let { try { wm.removeView(it) } catch (_: Exception) {}; panel = null; return }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(18, 18, 18, 18)
            background = GradientDrawable().apply { setColor(0xF01C1E38.toInt()); cornerRadius = 30f; setStroke(2, 0xFF7657FF.toInt()) }
        }
        root.addView(TextView(this).apply { text = "SMART MACRO"; textSize = 16f; setTextColor(0xFF35D7D0.toInt()); setPadding(4, 4, 4, 14) })
        listOf("Vùng chạm 1", "＋ Thêm vùng chạm", "Click", "Long press", "Slide", "Delay", "Multi press", "Đóng menu").forEach { label ->
            val row = TextView(this).apply {
                text = label; textSize = 15f; setTextColor(AndroidColor.WHITE); setPadding(14, 14, 14, 14)
                background = GradientDrawable().apply { setColor(0xFF303354.toInt()); cornerRadius = 18f }
            }
            root.addView(row, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 0, 0, 9) })
            if (label == "Đóng menu") row.setOnClickListener { togglePanel() }
        }
        panel = root
        wm.addView(root, layoutParams(290, -2).apply { x = 58; y = 250 })
    }

    override fun onDestroy() {
        try { handle?.let { wm.removeView(it) } } catch (_: Exception) {}
        try { panel?.let { wm.removeView(it) } } catch (_: Exception) {}
        super.onDestroy()
    }
}
