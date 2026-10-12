package com.example.smartmacro

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val Ink = Color(0xFF17182F)
private val Violet = Color(0xFF7657FF)
private val Cyan = Color(0xFF35D7D0)
private val Pink = Color(0xFFFF5E9A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme = darkColorScheme(primary = Violet, secondary = Cyan, background = Ink)) { Dashboard() } }
    }
}

@Composable
private fun Dashboard() {
    val context = LocalContext.current
    var overlayEnabled by remember { mutableStateOf(false) }
    var editorOpen by remember { mutableStateOf(false) }
    var macroName by remember { mutableStateOf("Vùng chạm 1") }
    var radius by remember { mutableFloatStateOf(42f) }
    var opacity by remember { mutableFloatStateOf(.65f) }
    var stepType by remember { mutableStateOf(StepType.CLICK) }
    var stepDuration by remember { mutableFloatStateOf(100f) }
    var appName by remember { mutableStateOf("") }
    var profiles by remember { mutableStateOf(listOf<AppProfile>()) }
    var macros by remember { mutableStateOf(listOf(TouchMacro())) }

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Ink, Color(0xFF2A2052))))) {
        LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text("SMART MACRO", color = Cyan, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text("Studio", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                Text("Cấu hình vùng chạm và chuỗi thao tác", color = Color(0xFFB9BAD0))
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF292B49)), shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Menu nổi", color = Color.White, fontWeight = FontWeight.Bold)
                                Text(if (overlayEnabled) "Đang bật" else "Đang tắt", color = if (overlayEnabled) Cyan else Color.LightGray)
                            }
                            Switch(checked = overlayEnabled, onCheckedChange = { wanted ->
                                if (wanted && !Settings.canDrawOverlays(context)) {
                                    context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")))
                                    overlayEnabled = false
                                } else {
                                    overlayEnabled = wanted
                                    if (wanted) context.startService(Intent(context, FloatingOverlayService::class.java))
                                    else context.stopService(Intent(context, FloatingOverlayService::class.java))
                                }
                            })
                        }
                        Text("Android yêu cầu cấp quyền hiển thị trên ứng dụng khác và bật Trợ năng thủ công.", color = Color(0xFFB9BAD0), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionCard("Vùng chạm", "Tên • kích thước • độ mờ", Violet, Modifier.weight(1f)) { editorOpen = true }
                    ActionCard("Hồ sơ game", "Danh sách ứng dụng", Pink, Modifier.weight(1f)) { appName = "" }
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Vùng chạm của tôi", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Button(onClick = { macros = macros + TouchMacro(name = "Vùng chạm ${macros.size + 1}"); editorOpen = true }, shape = RoundedCornerShape(12.dp)) { Text("+ Thêm") }
                }
            }
            items(macros, key = { it.id }) { macro ->
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF292B49)), shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).background(Violet.copy(alpha = macro.opacity), CircleShape), contentAlignment = Alignment.Center) {
                            Text(macro.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(macro.name, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("${macro.steps.size} bước • ${macro.radiusDp.toInt()} dp", color = Color(0xFFB9BAD0), style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { macroName = macro.name; radius = macro.radiusDp; opacity = macro.opacity; editorOpen = true }) { Text("Sửa") }
                    }
                }
            }
            item {
                Text("Ứng dụng / game", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = appName, onValueChange = { appName = it }, modifier = Modifier.weight(1f), label = { Text("Tên hoặc package app") }, singleLine = true)
                    Button(onClick = { if (appName.isNotBlank()) { profiles = profiles + AppProfile(appName.trim(), appName.trim()); appName = "" } }, shape = RoundedCornerShape(12.dp)) { Text("Thêm") }
                }
            }
            items(profiles) { profile ->
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF292B49)), shape = RoundedCornerShape(14.dp)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(profile.label, color = Color.White, modifier = Modifier.weight(1f))
                        Text("Đã thêm", color = Cyan)
                    }
                }
            }
            item {
                Button(onClick = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Ink)) {
                    Text("Mở cài đặt Trợ năng", fontWeight = FontWeight.Bold)
                }
                Text("Lưu ý: bản này là MVP. Danh sách hồ sơ và macro chưa được lưu bền vững sau khi đóng ứng dụng.", color = Color(0xFFB9BAD0), style = MaterialTheme.typography.bodySmall)
            }
        }

        if (editorOpen) {
            AlertDialog(
                onDismissRequest = { editorOpen = false },
                containerColor = Color(0xFF222440),
                title = { Text("Chỉnh sửa vùng chạm", color = Color.White) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = macroName, onValueChange = { macroName = it }, label = { Text("Tên vùng chạm") }, singleLine = true)
                        Text("Kích thước: ${radius.toInt()} dp", color = Color.White)
                        Slider(value = radius, onValueChange = { radius = it }, valueRange = 20f..100f)
                        Text("Độ mờ: ${(opacity * 100).toInt()}%", color = Color.White)
                        Slider(value = opacity, onValueChange = { opacity = it }, valueRange = .1f..1f)
                        Text("Thêm bước macro", color = Cyan, fontWeight = FontWeight.Bold)
                        StepType.entries.forEach { type ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = stepType == type, onClick = { stepType = type })
                                Text(type.label, color = Color.White)
                            }
                        }
                        Text("Thời lượng / Delay: ${stepDuration.toInt()} ms", color = Color.White)
                        Slider(value = stepDuration, onValueChange = { stepDuration = it }, valueRange = 50f..3000f)
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val updated = TouchMacro(name = macroName.ifBlank { "Vùng chạm" }, radiusDp = radius, opacity = opacity,
                            steps = listOf(MacroStep(type = stepType, durationMs = stepDuration.toLong())))
                        macros = if (macros.isEmpty()) listOf(updated) else {
                            val idx = macros.indexOfFirst { it.name == macroName }
                            if (idx >= 0) macros.toMutableList().also { it[idx] = updated } else macros + updated
                        }
                        editorOpen = false
                    }, shape = RoundedCornerShape(12.dp)) { Text("Lưu") }
                },
                dismissButton = { TextButton(onClick = { editorOpen = false }) { Text("Hủy") } }
            )
        }
    }
}

@Composable
private fun ActionCard(title: String, subtitle: String, accent: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color(0xFF292B49)), shape = RoundedCornerShape(22.dp), onClick = onClick) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(38.dp).background(accent, CircleShape), contentAlignment = Alignment.Center) {
                Text(title.take(1), color = Color.White, fontWeight = FontWeight.ExtraBold)
            }
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFFB9BAD0), style = MaterialTheme.typography.bodySmall)
        }
    }
}
