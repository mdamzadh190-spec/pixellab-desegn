package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AnimationPreset
import com.example.model.CustomCanvasDimension
import com.example.model.MeasurementUnit
import com.example.model.ShapeType
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class ToolButtonItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tintColor: Color = AccentCyan,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonTableDialog(
    onAddText: () -> Unit,
    onOpenQuotes: () -> Unit,
    onAddShape: (ShapeType) -> Unit,
    onAddSticker: (String) -> Unit,
    onOpenDimensionDialog: () -> Unit,
    onQuickUnitSet: (MeasurementUnit) -> Unit,
    onQuickPresetSet: (CustomCanvasDimension) -> Unit,
    onApplyAnimationToSelected: (AnimationPreset) -> Unit,
    onTogglePlayAnimation: () -> Unit,
    isPlayingAnimation: Boolean,
    onOpenPdfExport: () -> Unit,
    onOpenImageExport: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val categories = listOf("সব বাটন / All", "পরিমাপ ও PDF", "এনিমেশন", "টাইপোগ্রাফি", "শেপ ও স্টিকার")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(540.dp)
                .padding(horizontal = 14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(AccentCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("বাটন টেবিল (Button Matrix)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("সব ফিচার ও কমান্ডের দ্রুত অ্যাক্সেস টেবিল", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Tab Row
            TabRow(
                selectedTabIndex = selectedCategoryIndex,
                containerColor = DarkSurfaceVariant,
                contentColor = AccentCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedCategoryIndex]),
                        color = AccentCyan
                    )
                }
            ) {
                categories.forEachIndexed { index, cat ->
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        text = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tool Buttons Collection
            val unitButtons = listOf(
                ToolButtonItem("Pixel (px)", "ডিজিটাল রেজোলিউশন", Icons.Default.Crop, AccentCyan) {
                    onQuickUnitSet(MeasurementUnit.PIXEL)
                    onDismiss()
                },
                ToolButtonItem("Inch (in)", "প্রিন্ট ইঞ্চি সাইজ", Icons.Default.Straighten, AccentAmber) {
                    onQuickUnitSet(MeasurementUnit.INCH)
                    onDismiss()
                },
                ToolButtonItem("Centimeter (cm)", "সেন্টিমিটার স্কেলিং", Icons.Default.Straighten, Color(0xFF00E676)) {
                    onQuickUnitSet(MeasurementUnit.CENTIMETER)
                    onDismiss()
                },
                ToolButtonItem("Millimeter (mm)", "মিলিমিটার প্রিন্ট লেআউট", Icons.Default.Straighten, Color(0xFFFF5252)) {
                    onQuickUnitSet(MeasurementUnit.MILLIMETER)
                    onDismiss()
                },
                ToolButtonItem("PDF এক্সপোর্ট", "হাই-রেজোলিউশন PDF তৈরি", Icons.Default.PictureAsPdf, Color(0xFFFF3366)) {
                    onOpenPdfExport()
                    onDismiss()
                },
                ToolButtonItem("A4 সাইজ (210×297mm)", "স্ট্যান্ডার্ড A4 পেপার", Icons.Default.Print, AccentCyan) {
                    onQuickPresetSet(CustomCanvasDimension.PRESET_A4)
                    onDismiss()
                },
                ToolButtonItem("Business Card", "৮৫ × ৫৫ মিমি কার্ড", Icons.Default.Print, AccentAmber) {
                    onQuickPresetSet(CustomCanvasDimension.PRESET_BUSINESS_CARD)
                    onDismiss()
                },
                ToolButtonItem("কাস্টম পরিমাপ", "ইঞ্চি, px, cm, mm ইনপুট", Icons.Default.Straighten, AccentCyan) {
                    onOpenDimensionDialog()
                    onDismiss()
                }
            )

            val animationButtons = listOf(
                ToolButtonItem(if (isPlayingAnimation) "Stop Motion" else "Play Animation", "ক্যানভাস এনিমেশন প্রিভিউ", Icons.Default.PlayArrow, if (isPlayingAnimation) Color(0xFFFF5252) else AccentCyan) {
                    onTogglePlayAnimation()
                    onDismiss()
                },
                ToolButtonItem("Pulse (স্পন্দন)", "স্কেল আপ-ডাউন পালস", Icons.Default.Animation, AccentCyan) {
                    onApplyAnimationToSelected(AnimationPreset.PULSE)
                    onDismiss()
                },
                ToolButtonItem("Float (ভাসমান)", "স্মুথ উল্লম্ব নড়াচড়া", Icons.Default.Animation, AccentAmber) {
                    onApplyAnimationToSelected(AnimationPreset.FLOAT)
                    onDismiss()
                },
                ToolButtonItem("Bounce (লাফানো)", "বাউন্স ও জাম্প মোশন", Icons.Default.Animation, Color(0xFF00E676)) {
                    onApplyAnimationToSelected(AnimationPreset.BOUNCE)
                    onDismiss()
                },
                ToolButtonItem("Rotate 360°", "বৃত্তাকার অবিরাম ঘূর্ণন", Icons.Default.Animation, Color(0xFFA855F7)) {
                    onApplyAnimationToSelected(AnimationPreset.ROTATE_LOOP)
                    onDismiss()
                },
                ToolButtonItem("Wiggle (ঝলক)", "পাশে পাশে কম্পন", Icons.Default.Animation, Color(0xFFFF9F1C)) {
                    onApplyAnimationToSelected(AnimationPreset.SHIMMER)
                    onDismiss()
                },
                ToolButtonItem("Breathe (শ্বাসপ্রশ্বাস)", "স্বচ্ছতার স্মুথ ওয়েভ", Icons.Default.Animation, Color(0xFF4CC9F0)) {
                    onApplyAnimationToSelected(AnimationPreset.BREATHE)
                    onDismiss()
                },
                ToolButtonItem("None (স্থির)", "এনিমেশন বন্ধ করুন", Icons.Default.Close, TextSecondary) {
                    onApplyAnimationToSelected(AnimationPreset.NONE)
                    onDismiss()
                }
            )

            val textButtons = listOf(
                ToolButtonItem("+ New Text", "নতুন টেক্সট যোগ করুন", Icons.Default.TextFields, AccentCyan) {
                    onAddText()
                    onDismiss()
                },
                ToolButtonItem("বাংলা ও ইংরেজি উক্তি", "মোটিভেশনাল কোটস লাইব্রেরি", Icons.Default.FormatQuote, AccentAmber) {
                    onOpenQuotes()
                    onDismiss()
                },
                ToolButtonItem("3D Effect", "ত্রিমাত্রিক টেক্সট ডেপথ", Icons.Default.TextFields, Color(0xFF00E5FF)) {
                    onAddText()
                    onDismiss()
                }
            )

            val shapeButtons = listOf(
                ToolButtonItem("Rounded Rect", "গোলাকার আয়তক্ষেত্র", Icons.Default.Category, AccentCyan) {
                    onAddShape(ShapeType.ROUNDED_RECT)
                    onDismiss()
                },
                ToolButtonItem("Circle", "বৃত্তাকার শেপ", Icons.Default.Category, AccentAmber) {
                    onAddShape(ShapeType.CIRCLE)
                    onDismiss()
                },
                ToolButtonItem("Star", "তারা শেপ", Icons.Default.Category, Color(0xFFFFD700)) {
                    onAddShape(ShapeType.STAR)
                    onDismiss()
                },
                ToolButtonItem("Heart", "ভালোবাসার হার্ট শেপ", Icons.Default.Category, Color(0xFFFF3366)) {
                    onAddShape(ShapeType.HEART)
                    onDismiss()
                },
                ToolButtonItem("Shield", "নিরাপত্তা শিল্ড শেপ", Icons.Default.Category, Color(0xFFA855F7)) {
                    onAddShape(ShapeType.SHIELD)
                    onDismiss()
                },
                ToolButtonItem("HOT ব্যাজ", "হট প্রমোশনাল স্টিকার", Icons.Default.Category, Color(0xFFFF5252)) {
                    onAddSticker("HOT")
                    onDismiss()
                },
                ToolButtonItem("SALE ব্যাজ", "সেল ডিসকাউন্ট ব্যাজ", Icons.Default.Category, AccentAmber) {
                    onAddSticker("SALE")
                    onDismiss()
                },
                ToolButtonItem("50% OFF ব্যাজ", "অফার ব্যাজ", Icons.Default.Category, Color(0xFFE71D36)) {
                    onAddSticker("50% OFF")
                    onDismiss()
                }
            )

            val activeItems = when (selectedCategoryIndex) {
                1 -> unitButtons
                2 -> animationButtons
                3 -> textButtons
                4 -> shapeButtons
                else -> unitButtons + animationButtons + textButtons + shapeButtons
            }

            // Grid of Buttons
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(activeItems) { btn ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                            .clickable { btn.onClick() }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(btn.tintColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(btn.icon, contentDescription = null, tint = btn.tintColor, modifier = Modifier.size(18.dp))
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(btn.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                                Text(btn.subtitle, color = TextSecondary, fontSize = 10.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}
