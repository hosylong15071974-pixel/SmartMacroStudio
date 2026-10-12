package com.example.smartmacro

enum class StepType(val label: String) {
    CLICK("Click"), LONG_PRESS("Long press"), SLIDE("Slide"), DELAY("Delay"), MULTI_PRESS("Multi press")
}

data class MacroStep(
    val id: Long = System.nanoTime(),
    val type: StepType,
    val x: Float = .5f,
    val y: Float = .5f,
    val endX: Float = .7f,
    val endY: Float = .5f,
    val durationMs: Long = 100L,
    val repeatCount: Int = 2
)

data class TouchMacro(
    val id: Long = System.nanoTime(),
    val name: String = "Vùng chạm mới",
    val x: Float = .5f,
    val y: Float = .5f,
    val radiusDp: Float = 42f,
    val opacity: Float = .65f,
    val steps: List<MacroStep> = listOf(MacroStep(type = StepType.CLICK))
)

data class AppProfile(val label: String, val packageName: String, val enabled: Boolean = true)
