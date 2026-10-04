package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Texture
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Straighten
import com.example.model.BackgroundConfig
import com.example.model.BackgroundType
import com.example.model.CanvasRatio
import com.example.model.CustomCanvasDimension
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PaletteColors
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CanvasToolsPanel(
    currentRatio: CanvasRatio,
    customDimension: CustomCanvasDimension?,
    currentBackground: BackgroundConfig,
    onRatioChanged: (CanvasRatio) -> Unit,
    onOpenDimensionDialog: () -> Unit,
    onBackgroundChanged: (BackgroundConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    // Zero-permission modern Android photo picker for background image
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onBackgroundChanged(
                currentBackground.copy(
                    type = BackgroundType.IMAGE,
                    imageUri = uri.toString()
                )
            )
        }
    }

    val gradientPresets = listOf(
        listOf(0xFF0F172A, 0xFF1E1B4B, 0xFF0284C7), // Tech Indigo
        listOf(0xFF881337, 0xFF4C0519, 0xFF1C1917), // Crimson Rose
        listOf(0xFF064E3B, 0xFF022C22, 0xFF0F172A), // Emerald Forest
        listOf(0xFF3B0764, 0xFF1E1B4B, 0xFF0F172A), // Cyberpunk Violet
        listOf(0xFF431407, 0xFF7C2D12, 0xFFEA580C), // Sunset Amber
        listOf(0xFF0C4A6E, 0xFF075985, 0xFF0284C7), // Deep Ocean
        listOf(0xFF18181B, 0xFF27272A, 0xFF3F3F46), // Sleek Charcoal
        listOf(0xFF831843, 0xFFBE185D, 0xFFFB7185)  // Neon Pink
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .height(240.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Physical & Digital Dimensions Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Dimensions & Units (পরিমাপ ও ইউনিট):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                if (customDimension != null) {
                    Text(
                        "${customDimension.width} × ${customDimension.height} ${customDimension.unit.symbol} (${customDimension.toPixelWidth()}×${customDimension.toPixelHeight()} px)",
                        color = AccentCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Button(
                onClick = onOpenDimensionDialog,
                colors = ButtonDefaults.buttonColors(containerColor = AccentAmber, contentColor = Color(0xFF432C00)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("open_dimension_dialog_button")
            ) {
                Icon(Icons.Default.Straighten, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("inch, px, cm, mm", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 1. Aspect Ratio Presets
        Text("Canvas Ratio Presets (অনুপাত):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            CanvasRatio.values().forEach { ratio ->
                FilterChip(
                    selected = currentRatio == ratio,
                    onClick = { onRatioChanged(ratio) },
                    label = { Text(ratio.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    leadingIcon = {
                        Icon(Icons.Default.AspectRatio, contentDescription = null, modifier = Modifier.size(14.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentCyan,
                        selectedLabelColor = Color(0xFF00363D)
                    )
                )
            }
        }

        // 2. Background Mode Buttons
        Text("Background Style (ক্যানভাস ব্যাকগ্রাউন্ড):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Transparent Button
            OutlinedButton(
                onClick = {
                    onBackgroundChanged(currentBackground.copy(type = BackgroundType.TRANSPARENT))
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (currentBackground.type == BackgroundType.TRANSPARENT) AccentCyan.copy(alpha = 0.2f) else Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("transparent_bg_button")
            ) {
                Text("Transparent (PNG)", fontSize = 11.sp, color = TextPrimary)
            }

            // Photo from Gallery
            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentAmber, contentColor = Color(0xFF432C00)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("pick_bg_photo_button")
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Pick Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Grid Texture
            OutlinedButton(
                onClick = {
                    onBackgroundChanged(currentBackground.copy(type = BackgroundType.TEXTURE))
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (currentBackground.type == BackgroundType.TEXTURE) AccentCyan.copy(alpha = 0.2f) else Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Texture, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Grid Texture", fontSize = 11.sp, color = TextPrimary)
            }
        }

        // 3. Gradients Row
        Text("Studio Gradients:", color = TextSecondary, fontSize = 12.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            gradientPresets.forEach { gradColors ->
                val brush = Brush.linearGradient(gradColors.map { Color(it) })
                val isSelected = currentBackground.type == BackgroundType.GRADIENT &&
                        currentBackground.gradientColors == gradColors

                Box(
                    modifier = Modifier
                        .size(width = 60.dp, height = 36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brush)
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) AccentCyan else DarkBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            onBackgroundChanged(
                                currentBackground.copy(
                                    type = BackgroundType.GRADIENT,
                                    gradientColors = gradColors
                                )
                            )
                        }
                )
            }
        }

        // 4. Solid Colors
        Text("Solid Colors:", color = TextSecondary, fontSize = 12.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PaletteColors.forEach { color ->
                val argb = color.value.toLong()
                val isSelected = currentBackground.type == BackgroundType.SOLID_COLOR && currentBackground.color == argb
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
                            onBackgroundChanged(
                                currentBackground.copy(
                                    type = BackgroundType.SOLID_COLOR,
                                    color = argb
                                )
                            )
                        }
                )
            }
        }
    }
}
