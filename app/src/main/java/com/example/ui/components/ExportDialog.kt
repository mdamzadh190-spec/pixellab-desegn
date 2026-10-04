package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomCanvasDimension
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class ExportTargetFormat {
    PDF,
    PNG,
    JPEG
}

@Composable
fun ExportDialog(
    isExporting: Boolean,
    customDimension: CustomCanvasDimension?,
    onSaveToGallery: (isPng: Boolean, targetWidth: Int) -> Unit,
    onShare: (isPng: Boolean, targetWidth: Int) -> Unit,
    onSavePdf: () -> Unit,
    onSharePdf: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFormat by remember { mutableStateOf(ExportTargetFormat.PDF) }
    var selectedResolutionWidth by remember { mutableIntStateOf(1920) }

    AlertDialog(
        onDismissRequest = { if (!isExporting) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (selectedFormat == ExportTargetFormat.PDF) Icons.Default.PictureAsPdf else Icons.Default.Share,
                    contentDescription = null,
                    tint = if (selectedFormat == ExportTargetFormat.PDF) Color(0xFFFF3366) else AccentCyan,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Export & Share Design", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (isExporting) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = AccentCyan)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Processing Document / Image...", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                } else {
                    // 1. Format Choice (PDF, PNG, JPEG)
                    Text("Select Format (ডকুমেন্ট বা ইমেজ ফরম্যাট):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedFormat == ExportTargetFormat.PDF,
                            onClick = { selectedFormat = ExportTargetFormat.PDF },
                            label = { Text("PDF Doc", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFF3366),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).testTag("format_pdf_chip")
                        )

                        FilterChip(
                            selected = selectedFormat == ExportTargetFormat.PNG,
                            onClick = { selectedFormat = ExportTargetFormat.PNG },
                            label = { Text("PNG", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentCyan,
                                selectedLabelColor = Color(0xFF00363D)
                            ),
                            modifier = Modifier.weight(1f).testTag("format_png_chip")
                        )

                        FilterChip(
                            selected = selectedFormat == ExportTargetFormat.JPEG,
                            onClick = { selectedFormat = ExportTargetFormat.JPEG },
                            label = { Text("JPEG", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentCyan,
                                selectedLabelColor = Color(0xFF00363D)
                            ),
                            modifier = Modifier.weight(1f).testTag("format_jpeg_chip")
                        )
                    }

                    // Format Specific Details
                    if (selectedFormat == ExportTargetFormat.PDF) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("PDF Print Configuration:", color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                val unitStr = customDimension?.unit?.displayName ?: "Standard Vector"
                                val dimStr = if (customDimension != null) {
                                    "${customDimension.width} × ${customDimension.height} ${customDimension.unit.symbol}"
                                } else "A4 Auto (210×297 mm)"
                                Text("• Page Dimension: $dimStr", color = TextPrimary, fontSize = 12.sp)
                                Text("• Print Resolution: ${customDimension?.dpi ?: 300} DPI High Quality", color = TextPrimary, fontSize = 12.sp)
                                Text("• Measurement Unit: $unitStr", color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        // PDF Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onSavePdf,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3366), contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("save_pdf_button")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onSharePdf,
                                colors = ButtonDefaults.buttonColors(containerColor = AccentAmber, contentColor = Color(0xFF432C00)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("share_pdf_button")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Image Resolution Selector (720p, 1080p, 4K)
                        Text("Image Resolution (রেজোলিউশন):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                Triple("Standard HD (720p)", 1280, "Quick & Light"),
                                Triple("Full HD (1080p)", 1920, "Social Media Standard"),
                                Triple("Ultra 4K UHD", 3840, "Maximum Detail")
                            ).forEach { (label, res, desc) ->
                                val isSelected = selectedResolutionWidth == res
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isSelected) AccentCyan.copy(alpha = 0.15f) else DarkSurfaceElevated, RoundedCornerShape(8.dp))
                                        .border(1.dp, if (isSelected) AccentCyan else DarkBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedResolutionWidth = res }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(label, color = if (isSelected) AccentCyan else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(desc, color = TextSecondary, fontSize = 10.sp)
                                    }
                                    Text("${res}px", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Image Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onSaveToGallery(selectedFormat == ExportTargetFormat.PNG, selectedResolutionWidth)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color(0xFF00363D)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("save_gallery_button")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save Image", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    onShare(selectedFormat == ExportTargetFormat.PNG, selectedResolutionWidth)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentAmber, contentColor = Color(0xFF432C00)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("share_image_button")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share Image", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isExporting) {
                TextButton(onClick = onDismiss) {
                    Text("Close", color = TextSecondary)
                }
            }
        },
        containerColor = DarkSurfaceVariant
    )
}

