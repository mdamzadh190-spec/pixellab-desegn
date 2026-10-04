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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AnimationPreset
import com.example.model.DesignLayer
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PaletteColors
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TextToolsPanel(
    selectedLayer: DesignLayer?,
    onAddText: () -> Unit,
    onOpenQuotes: () -> Unit,
    onUpdateLayer: ((DesignLayer) -> DesignLayer) -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onCenterAlign: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editTextValue by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Quick Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onAddText,
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color(0xFF00363D)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_text_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ New Text", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = onOpenQuotes,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentAmber),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(AccentAmber)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("quotes_button")
            ) {
                Text("✨ Quotes (উক্তি)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            if (selectedLayer != null) {
                IconButton(
                    onClick = {
                        editTextValue = selectedLayer.text
                        showEditDialog = true
                    },
                    modifier = Modifier
                        .background(DarkSurfaceVariant, CircleShape)
                        .size(38.dp)
                        .testTag("edit_text_content_button")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Text", tint = AccentCyan, modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = onCenterAlign,
                    modifier = Modifier
                        .background(DarkSurfaceVariant, CircleShape)
                        .size(38.dp)
                        .testTag("center_align_button")
                ) {
                    Icon(Icons.Default.FormatAlignCenter, contentDescription = "Center On Canvas", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = onDuplicate,
                    modifier = Modifier
                        .background(DarkSurfaceVariant, CircleShape)
                        .size(38.dp)
                        .testTag("duplicate_text_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .background(DarkSurfaceVariant, CircleShape)
                        .size(38.dp)
                        .testTag("delete_text_button")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedLayer == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap on a text layer to edit, or tap '+ New Text' / 'Quotes'",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        } else {
            // Scrollable detailed text property controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Font Size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Size: ${selectedLayer.fontSize.toInt()}sp", color = TextPrimary, fontSize = 12.sp, modifier = Modifier.width(76.dp))
                    Slider(
                        value = selectedLayer.fontSize,
                        onValueChange = { newSize ->
                            onUpdateLayer { it.copy(fontSize = newSize) }
                        },
                        valueRange = 12f..96f,
                        colors = SliderDefaults.colors(thumbColor = AccentCyan, activeTrackColor = AccentCyan),
                        modifier = Modifier.weight(1f).testTag("font_size_slider")
                    )
                }

                // 2. Text Color Palette
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Color:", color = TextSecondary, fontSize = 12.sp)
                    PaletteColors.forEach { color ->
                        val argb = color.value.toLong()
                        val isSelected = selectedLayer.textColor == argb
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
                                .clickable {
                                    onUpdateLayer { it.copy(textColor = argb) }
                                }
                        )
                    }
                }

                // 3. Typography & Styling Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = selectedLayer.isBold,
                        onClick = { onUpdateLayer { it.copy(isBold = !it.isBold) } },
                        label = { Text("Bold", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AccentCyan, selectedLabelColor = Color.Black)
                    )
                    FilterChip(
                        selected = selectedLayer.isItalic,
                        onClick = { onUpdateLayer { it.copy(isItalic = !it.isItalic) } },
                        label = { Text("Italic", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AccentCyan, selectedLabelColor = Color.Black)
                    )
                    FilterChip(
                        selected = selectedLayer.isAllUppercase,
                        onClick = { onUpdateLayer { it.copy(isAllUppercase = !it.isAllUppercase) } },
                        label = { Text("ALL CAPS", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AccentCyan, selectedLabelColor = Color.Black)
                    )
                    // Fonts
                    listOf("Default", "Serif", "Monospace", "Sans").forEach { font ->
                        FilterChip(
                            selected = selectedLayer.fontStyleName == font,
                            onClick = { onUpdateLayer { it.copy(fontStyleName = font) } },
                            label = { Text(font, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AccentAmber, selectedLabelColor = Color.Black)
                        )
                    }
                }

                // 4. 3D Text Effect Control (PixelLab iconic feature)
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("3D Text Effect (ত্রিমাত্রিক লেখা)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Switch(
                                checked = selectedLayer.has3DEffect,
                                onCheckedChange = { onUpdateLayer { it.copy(has3DEffect = !it.has3DEffect) } },
                                colors = SwitchDefaults.colors(checkedThumbColor = AccentCyan, checkedTrackColor = Color(0xFF004F58))
                            )
                        }
                        if (selectedLayer.has3DEffect) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Depth: ${selectedLayer.depth3D.toInt()}", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.width(60.dp))
                                Slider(
                                    value = selectedLayer.depth3D,
                                    onValueChange = { d -> onUpdateLayer { it.copy(depth3D = d) } },
                                    valueRange = 2f..18f,
                                    colors = SliderDefaults.colors(thumbColor = AccentCyan, activeTrackColor = AccentCyan),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // 5. Stroke & Shadow
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Stroke Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Stroke", color = TextPrimary, fontSize = 12.sp)
                                Switch(
                                    checked = selectedLayer.hasStroke,
                                    onCheckedChange = { onUpdateLayer { it.copy(hasStroke = !it.hasStroke) } },
                                    colors = SwitchDefaults.colors(checkedThumbColor = AccentCyan, checkedTrackColor = Color(0xFF004F58))
                                )
                            }
                            if (selectedLayer.hasStroke) {
                                Slider(
                                    value = selectedLayer.strokeWidth,
                                    onValueChange = { w -> onUpdateLayer { it.copy(strokeWidth = w) } },
                                    valueRange = 1f..12f,
                                    colors = SliderDefaults.colors(thumbColor = AccentCyan)
                                )
                            }
                        }
                    }

                    // Shadow Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Shadow", color = TextPrimary, fontSize = 12.sp)
                                Switch(
                                    checked = selectedLayer.hasShadow,
                                    onCheckedChange = { onUpdateLayer { it.copy(hasShadow = !it.hasShadow) } },
                                    colors = SwitchDefaults.colors(checkedThumbColor = AccentAmber, checkedTrackColor = Color(0xFF5D4037))
                                )
                            }
                            if (selectedLayer.hasShadow) {
                                Slider(
                                    value = selectedLayer.shadowRadius,
                                    onValueChange = { r -> onUpdateLayer { it.copy(shadowRadius = r) } },
                                    valueRange = 2f..24f,
                                    colors = SliderDefaults.colors(thumbColor = AccentAmber)
                                )
                            }
                        }
                    }
                }

                // 6. Background Badge for Text
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Text Background Badge", color = TextPrimary, fontSize = 12.sp)
                            Switch(
                                checked = selectedLayer.hasBackgroundBadge,
                                onCheckedChange = { onUpdateLayer { it.copy(hasBackgroundBadge = !it.hasBackgroundBadge) } },
                                colors = SwitchDefaults.colors(checkedThumbColor = AccentCyan)
                            )
                        }
                        if (selectedLayer.hasBackgroundBadge) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                PaletteColors.forEach { c ->
                                    val argb = c.value.toLong()
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(c)
                                            .border(1.dp, if (selectedLayer.badgeColor == argb) Color.White else Color.Transparent, CircleShape)
                                            .clickable { onUpdateLayer { it.copy(badgeColor = argb) } }
                                    )
                                }
                            }
                        }
                    }
                }

                // 7. Layer Animation (এনিমেশন)
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Layer Animation (এনিমেশন)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(selectedLayer.animationPreset.displayName, color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AnimationPreset.values().forEach { preset ->
                                FilterChip(
                                    selected = selectedLayer.animationPreset == preset,
                                    onClick = { onUpdateLayer { it.copy(animationPreset = preset) } },
                                    label = { Text(preset.displayName, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentCyan,
                                        selectedLabelColor = Color(0xFF00363D)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    // Edit Text Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Text", color = TextPrimary) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editTextValue,
                        onValueChange = { editTextValue = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_text_input"),
                        label = { Text("Text / বাংলা লিখুন") },
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateLayer { it.copy(text = editTextValue, name = "Text: ${editTextValue.take(12)}") }
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color.Black)
                ) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceVariant
        )
    }
}
