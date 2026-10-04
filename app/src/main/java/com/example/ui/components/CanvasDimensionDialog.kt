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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomCanvasDimension
import com.example.model.MeasurementUnit
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun CanvasDimensionDialog(
    initialDimension: CustomCanvasDimension?,
    onApplyDimension: (CustomCanvasDimension) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedUnit by remember {
        mutableStateOf(initialDimension?.unit ?: MeasurementUnit.MILLIMETER)
    }
    var widthInput by remember {
        mutableStateOf(
            initialDimension?.width?.let { String.format(Locale.US, "%.1f", it) } ?: "210"
        )
    }
    var heightInput by remember {
        mutableStateOf(
            initialDimension?.height?.let { String.format(Locale.US, "%.1f", it) } ?: "297"
        )
    }
    var selectedDpi by remember {
        mutableIntStateOf(initialDimension?.dpi ?: 300)
    }

    val currentW = widthInput.toFloatOrNull() ?: 100f
    val currentH = heightInput.toFloatOrNull() ?: 100f
    val currentDim = CustomCanvasDimension(currentW, currentH, selectedUnit, selectedDpi)

    val printPresets = listOf(
        CustomCanvasDimension.PRESET_A4,
        CustomCanvasDimension.PRESET_A3,
        CustomCanvasDimension.PRESET_BUSINESS_CARD,
        CustomCanvasDimension.PRESET_LETTER,
        CustomCanvasDimension.PRESET_POSTCARD,
        CustomCanvasDimension.PRESET_SQUARE_HD,
        CustomCanvasDimension.PRESET_YOUTUBE_THUMB
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Straighten, contentDescription = null, tint = AccentCyan, modifier = Modifier.padding(end = 8.dp))
                Column {
                    Text("Canvas Size & Units", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("inch, pixel, centimeter (cm), millimeter (mm)", color = TextSecondary, fontSize = 11.sp)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Unit Selector Tabs
                TabRow(
                    selectedTabIndex = selectedUnit.ordinal,
                    containerColor = DarkSurfaceElevated,
                    contentColor = AccentCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedUnit.ordinal]),
                            color = AccentCyan
                        )
                    }
                ) {
                    MeasurementUnit.values().forEach { unit ->
                        Tab(
                            selected = selectedUnit == unit,
                            onClick = {
                                // Convert current dimensions to the newly selected unit
                                val (newW, newH) = currentDim.convertTo(unit)
                                selectedUnit = unit
                                widthInput = String.format(Locale.US, "%.2f", newW).trimEnd('0').trimEnd('.')
                                heightInput = String.format(Locale.US, "%.2f", newH).trimEnd('0').trimEnd('.')
                            },
                            text = { Text(unit.symbol, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                    }
                }

                // 2. Width and Height Input Fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = widthInput,
                        onValueChange = { widthInput = it },
                        label = { Text("Width (${selectedUnit.symbol})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_width_input")
                    )

                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = { heightInput = it },
                        label = { Text("Height (${selectedUnit.symbol})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_height_input")
                    )
                }

                // 3. Live Pixel and PDF calculation display
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Render Resolution:", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                "${currentDim.toPixelWidth()} × ${currentDim.toPixelHeight()} px",
                                color = AccentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("PDF Page Size:", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                "${currentDim.toPdfPointsWidth()} × ${currentDim.toPdfPointsHeight()} pt",
                                color = AccentAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // 4. DPI Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Print DPI / PPI:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(72, 150, 300).forEach { dpiVal ->
                            FilterChip(
                                selected = selectedDpi == dpiVal,
                                onClick = { selectedDpi = dpiVal },
                                label = { Text("${dpiVal} DPI", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentCyan,
                                    selectedLabelColor = Color(0xFF00363D)
                                )
                            )
                        }
                    }
                }

                // 5. Standard Print Presets
                Text("Standard Print Presets (প্রিন্ট সাইজ):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    printPresets.forEach { preset ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                selectedUnit = preset.unit
                                widthInput = preset.width.toString()
                                heightInput = preset.height.toString()
                                selectedDpi = preset.dpi
                            },
                            label = { Text(preset.presetName ?: "", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextPrimary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyDimension(
                        CustomCanvasDimension(
                            width = widthInput.toFloatOrNull() ?: 100f,
                            height = heightInput.toFloatOrNull() ?: 100f,
                            unit = selectedUnit,
                            dpi = selectedDpi,
                            presetName = "Custom (${widthInput}×${heightInput} ${selectedUnit.symbol})"
                        )
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color(0xFF00363D)),
                modifier = Modifier.testTag("apply_dimension_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                Text("Apply Canvas Size")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkSurfaceVariant
    )
}
