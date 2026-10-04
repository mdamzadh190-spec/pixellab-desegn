package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.StudioTab

@Composable
fun StudioBottomNav(
    activeTab: StudioTab,
    onTabSelected: (StudioTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            title = "Projects",
            icon = Icons.Default.Style,
            isSelected = activeTab == StudioTab.TEMPLATES,
            onClick = { onTabSelected(StudioTab.TEMPLATES) },
            testTag = "nav_tab_templates"
        )

        BottomNavItem(
            title = "Text 'A'",
            icon = Icons.Default.TextFields,
            isSelected = activeTab == StudioTab.TEXT,
            onClick = { onTabSelected(StudioTab.TEXT) },
            testTag = "nav_tab_text"
        )

        BottomNavItem(
            title = "Shapes",
            icon = Icons.Default.Category,
            isSelected = activeTab == StudioTab.SHAPES,
            onClick = { onTabSelected(StudioTab.SHAPES) },
            testTag = "nav_tab_shapes"
        )

        BottomNavItem(
            title = "Canvas",
            icon = Icons.Default.Crop,
            isSelected = activeTab == StudioTab.BACKGROUND,
            onClick = { onTabSelected(StudioTab.BACKGROUND) },
            testTag = "nav_tab_canvas"
        )

        BottomNavItem(
            title = "Layers",
            icon = Icons.Default.Layers,
            isSelected = activeTab == StudioTab.LAYERS,
            onClick = { onTabSelected(StudioTab.LAYERS) },
            testTag = "nav_tab_layers"
        )
    }
}

@Composable
private fun BottomNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) AccentCyan else TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            color = if (isSelected) AccentCyan else TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
