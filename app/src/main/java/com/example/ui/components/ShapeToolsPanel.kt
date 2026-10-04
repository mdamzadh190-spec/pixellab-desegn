package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DesignLayer
import com.example.model.LayerType
import com.example.model.ShapeType
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PaletteColors
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ShapeToolsPanel(
    selectedLayer: DesignLayer?,
    onAddShape: (ShapeType) -> Unit,
    onAddSticker: (String, Long) -> Unit,
    onUpdateLayer: ((DesignLayer) -> DesignLayer) -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onCenterAlign: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Shapes & Badges Quick Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onAddShape(ShapeType.ROUNDED_RECT) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color(0xFF00363D)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_shape_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Shape", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Quick Add Stickers
            listOf("HOT", "SALE", "50% OFF", "NEW", "PRO", "VERIFIED", "STAR").forEach { tag ->
                val tint = when (tag) {
                    "HOT" -> 0xFFFF3366
                    "SALE" -> 0xFFFFB703
                    "50% OFF" -> 0xFFE71D36
                    "NEW" -> 0xFF00E5FF
                    "PRO" -> 0xFF8338EC
                    "VERIFIED" -> 0xFF3A86FF
                    else -> 0xFFFFD700
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(tint))
                        .clickable { onAddSticker(tag, tint) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(tag, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            if (selectedLayer != null) {
                IconButton(
                    onClick = onCenterAlign,
                    modifier = Modifier.background(DarkSurfaceVariant, CircleShape).size(36.dp)
                ) {
                    Icon(Icons.Default.FormatAlignCenter, contentDescription = "Center", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = onDuplicate,
                    modifier = Modifier.background(DarkSurfaceVariant, CircleShape).size(36.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.background(DarkSurfaceVariant, CircleShape).size(36.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedLayer == null || selectedLayer.type != LayerType.SHAPE) {
            // Select shape types to add
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Select Shape Geometry to Insert:", color = TextSecondary, fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShapeType.values().forEach { shape ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable { onAddShape(shape) }
                                .padding(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(shape.displayName, color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        } else {
            // Selected shape inspector
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Shape Type selector chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ShapeType.values().forEach { st ->
                        FilterChip(
                            selected = selectedLayer.shapeType == st,
                            onClick = { onUpdateLayer { it.copy(shapeType = st, name = "Shape: ${st.displayName}") } },
                            label = { Text(st.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AccentCyan, selectedLabelColor = Color.Black)
                        )
                    }
                }

                // Fill Color
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Fill:", color = TextSecondary, fontSize = 12.sp)
                    PaletteColors.forEach { color ->
                        val argb = color.value.toLong()
                        val isSelected = selectedLayer.fillColor == argb
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) AccentCyan else DarkBorder,
                                    shape = CircleShape
                                )
                                .clickable { onUpdateLayer { it.copy(fillColor = argb) } }
                        )
                    }
                }

                // Stroke Width & Color
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Stroke: ${selectedLayer.shapeStrokeWidth.toInt()}dp", color = TextPrimary, fontSize = 12.sp, modifier = Modifier.width(84.dp))
                    Slider(
                        value = selectedLayer.shapeStrokeWidth,
                        onValueChange = { sw -> onUpdateLayer { it.copy(shapeStrokeWidth = sw) } },
                        valueRange = 0f..20f,
                        colors = SliderDefaults.colors(thumbColor = AccentCyan, activeTrackColor = AccentCyan),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (selectedLayer.shapeStrokeWidth > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Border:", color = TextSecondary, fontSize = 12.sp)
                        PaletteColors.forEach { color ->
                            val argb = color.value.toLong()
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(1.dp, if (selectedLayer.shapeStrokeColor == argb) Color.White else Color.Transparent, CircleShape)
                                    .clickable { onUpdateLayer { it.copy(shapeStrokeColor = argb) } }
                            )
                        }
                    }
                }

                // Corner Radius (if rounded rect)
                if (selectedLayer.shapeType == ShapeType.ROUNDED_RECT) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Radius: ${selectedLayer.cornerRadius.toInt()}dp", color = TextPrimary, fontSize = 12.sp, modifier = Modifier.width(84.dp))
                        Slider(
                            value = selectedLayer.cornerRadius,
                            onValueChange = { cr -> onUpdateLayer { it.copy(cornerRadius = cr) } },
                            valueRange = 0f..60f,
                            colors = SliderDefaults.colors(thumbColor = AccentAmber, activeTrackColor = AccentAmber),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Opacity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Opacity: ${(selectedLayer.opacity * 100).toInt()}%", color = TextPrimary, fontSize = 12.sp, modifier = Modifier.width(84.dp))
                    Slider(
                        value = selectedLayer.opacity,
                        onValueChange = { op -> onUpdateLayer { it.copy(opacity = op) } },
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = AccentCyan, activeTrackColor = AccentCyan),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
