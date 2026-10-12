package com.example.smartmacro

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

/**
 * Gesture helper. Android requires the user to enable this service in Settings.
 * Coordinates are screen pixels. This service is not yet connected to a complete macro runner.
 */
class MacroAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) { /* Foreground-app matching can be implemented here. */ }
    override fun onInterrupt() {}

    fun tap(x: Float, y: Float, durationMs: Long = 80L, callback: (() -> Unit)? = null) {
        val path = Path().apply { moveTo(x, y) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, durationMs.coerceAtLeast(1)))
            .build()
        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) { callback?.invoke() }
        }, null)
    }

    fun longPress(x: Float, y: Float, durationMs: Long = 650L) = tap(x, y, durationMs)

    fun swipe(x1: Float, y1: Float, x2: Float, y2: Float, durationMs: Long = 300L) {
        val path = Path().apply { moveTo(x1, y1); lineTo(x2, y2) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, durationMs.coerceAtLeast(1)))
            .build()
        dispatchGesture(GestureDescription.Builder().addStroke(
            GestureDescription.StrokeDescription(path, 0, durationMs.coerceAtLeast(1))
        ).build(), null, null)
    }
}
