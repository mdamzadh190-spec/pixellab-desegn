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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TableChart

@Composable
fun StudioTopBar(
    projectTitle: String,
    canUndo: Boolean,
    canRedo: Boolean,
    showGrid: Boolean,
    isPlayingAnimation: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onToggleGrid: () -> Unit,
    onToggleAnimation: () -> Unit,
    onOpenButtonTable: () -> Unit,
    onSaveProject: (String) -> Unit,
    onOpenExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var currentTitleInput by remember { mutableStateOf(projectTitle) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title & Project Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    currentTitleInput = projectTitle
                    showRenameDialog = true
                }
                .padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(AccentCyan, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("P", color = Color(0xFF00363D), fontWeight = FontWeight.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PixelLab",
                        color = AccentCyan,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Design",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = projectTitle,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }

        // Action Buttons Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Undo
            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier.size(36.dp).testTag("top_bar_undo")
            ) {
                Icon(
                    Icons.Default.Undo,
                    contentDescription = "Undo",
                    tint = if (canUndo) TextPrimary else TextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Redo
            IconButton(
                onClick = onRedo,
                enabled = canRedo,
                modifier = Modifier.size(36.dp).testTag("top_bar_redo")
            ) {
                Icon(
                    Icons.Default.Redo,
                    contentDescription = "Redo",
                    tint = if (canRedo) TextPrimary else TextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Button Table (বাটন টেবিল)
            IconButton(
                onClick = onOpenButtonTable,
                modifier = Modifier
                    .size(36.dp)
                    .background(DarkSurfaceVariant, CircleShape)
                    .testTag("top_bar_button_table")
            ) {
                Icon(
                    Icons.Default.TableChart,
                    contentDescription = "Button Table",
                    tint = AccentCyan,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Animation Play / Pause Toggle
            IconButton(
                onClick = onToggleAnimation,
                modifier = Modifier
                    .size(36.dp)
                    .background(if (isPlayingAnimation) AccentCyan.copy(alpha = 0.25f) else DarkSurfaceVariant, CircleShape)
                    .testTag("top_bar_play_animation")
            ) {
                Icon(
                    if (isPlayingAnimation) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isPlayingAnimation) "Stop Motion" else "Play Motion",
                    tint = if (isPlayingAnimation) Color(0xFFFF5252) else AccentAmber,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Grid toggle
            IconButton(
                onClick = onToggleGrid,
                modifier = Modifier
                    .size(36.dp)
                    .background(if (showGrid) AccentCyan.copy(alpha = 0.2f) else Color.Transparent, CircleShape)
                    .testTag("top_bar_grid")
            ) {
                Icon(
                    Icons.Default.GridOn,
                    contentDescription = "Grid",
                    tint = if (showGrid) AccentCyan else TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Save Project
            IconButton(
                onClick = {
                    currentTitleInput = projectTitle
                    showRenameDialog = true
                },
                modifier = Modifier.size(36.dp).testTag("top_bar_save")
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Project", tint = AccentAmber, modifier = Modifier.size(18.dp))
            }

            // Export / Share
            Box(
                modifier = Modifier
                    .background(AccentCyan, RoundedCornerShape(8.dp))
                    .clickable { onOpenExport() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("top_bar_export_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF00363D), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export", color = Color(0xFF00363D), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }

    // Save & Rename Dialog
    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Save Project", color = TextPrimary) },
            text = {
                Column {
                    Text("Enter project name:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = currentTitleInput,
                        onValueChange = { currentTitleInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("project_name_input"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveProject(currentTitleInput)
                        showRenameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color.Black)
                ) {
                    Text("Save Project")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceVariant
        )
    }
}
