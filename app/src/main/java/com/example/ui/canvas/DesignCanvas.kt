package com.example.ui.canvas

import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.AnimationPreset
import com.example.model.BackgroundConfig
import com.example.model.BackgroundType
import com.example.model.CanvasRatio
import com.example.model.CustomCanvasDimension
import com.example.model.DesignLayer
import com.example.model.LayerType
import com.example.model.ShapeType
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun DesignCanvas(
    ratio: CanvasRatio,
    customDimension: CustomCanvasDimension?,
    background: BackgroundConfig,
    layers: List<DesignLayer>,
    selectedLayerId: String?,
    showGrid: Boolean,
    isPlayingAnimation: Boolean,
    onSelectLayer: (String?) -> Unit,
    onUpdateLayerPosition: (String, Float, Float) -> Unit,
    onUpdateLayerScale: (String, Float) -> Unit,
    onUpdateLayerRotation: (String, Float) -> Unit,
    onDeleteLayer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    onSelectLayer(null)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight
        val aspect = if (customDimension != null && customDimension.height > 0) {
            customDimension.width / customDimension.height
        } else {
            ratio.widthRatio / ratio.heightRatio
        }

        val (canvasWidth, canvasHeight) = if (availableWidth / availableHeight > aspect) {
            val h = availableHeight * 0.94f
            val w = h * aspect
            Pair(w, h)
        } else {
            val w = availableWidth * 0.94f
            val h = w / aspect
            Pair(w, h)
        }

        val density = LocalDensity.current
        val canvasWidthPx = with(density) { canvasWidth.toPx() }
        val canvasHeightPx = with(density) { canvasHeight.toPx() }

        // Canvas Frame
        Box(
            modifier = Modifier
                .size(canvasWidth, canvasHeight)
                .shadow(16.dp, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
        ) {
            // 1. Background Renderer
            CanvasBackground(background = background, modifier = Modifier.fillMaxSize())

            // 2. Layers Renderer
            val sortedLayers = layers.filter { it.isVisible }.sortedBy { it.zIndex }
            sortedLayers.forEach { layer ->
                val isSelected = layer.id == selectedLayerId

                LayerItemView(
                    layer = layer,
                    isSelected = isSelected,
                    canvasWidthPx = canvasWidthPx,
                    canvasHeightPx = canvasHeightPx,
                    isPlayingAnimation = isPlayingAnimation,
                    onSelect = { onSelectLayer(layer.id) },
                    onPositionChange = { newX, newY ->
                        onUpdateLayerPosition(layer.id, newX, newY)
                    },
                    onScaleChange = { newScale ->
                        onUpdateLayerScale(layer.id, newScale)
                    },
                    onRotationChange = { newRot ->
                        onUpdateLayerRotation(layer.id, newRot)
                    },
                    onDelete = { onDeleteLayer(layer.id) }
                )
            }

            // 3. Grid Overlay
            if (showGrid) {
                GridOverlay(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun CanvasBackground(background: BackgroundConfig, modifier: Modifier = Modifier) {
    when (background.type) {
        BackgroundType.TRANSPARENT -> {
            Canvas(modifier = modifier) {
                // Checkerboard
                val squareSize = 24.dp.toPx()
                var y = 0f
                var row = 0
                while (y < size.height) {
                    var x = 0f
                    var col = 0
                    while (x < size.width) {
                        val color = if ((row + col) % 2 == 0) Color(0xFF262A36) else Color(0xFF1E212B)
                        drawRect(color, Offset(x, y), Size(squareSize, squareSize))
                        x += squareSize
                        col++
                    }
                    y += squareSize
                    row++
                }
            }
        }
        BackgroundType.SOLID_COLOR -> {
            Box(modifier = modifier.background(Color(background.color)))
        }
        BackgroundType.GRADIENT -> {
            val colors = background.gradientColors.map { Color(it) }
            val brush = Brush.linearGradient(
                colors = colors,
                start = Offset.Zero,
                end = Offset.Infinite
            )
            Box(modifier = modifier.background(brush))
        }
        BackgroundType.TEXTURE -> {
            Canvas(modifier = modifier.background(Color(background.color))) {
                val step = 32.dp.toPx()
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        Color(0x22FFFFFF),
                        Offset(x, 0f),
                        Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += step
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        Color(0x22FFFFFF),
                        Offset(0f, y),
                        Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += step
                }
            }
        }
        BackgroundType.IMAGE -> {
            if (background.imageUri != null) {
                AsyncImage(
                    model = background.imageUri,
                    contentDescription = "Canvas Background Image",
                    modifier = modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Box(modifier = modifier.background(Color(background.color)))
            }
        }
    }
}

@Composable
fun GridOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cols = 6
        val rows = 6
        val colStep = size.width / cols
        val rowStep = size.height / rows

        for (i in 1 until cols) {
            val x = i * colStep
            drawLine(
                Color(0x5500E5FF),
                Offset(x, 0f),
                Offset(x, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }
        for (j in 1 until rows) {
            val y = j * rowStep
            drawLine(
                Color(0x5500E5FF),
                Offset(0f, y),
                Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        // Center crosshairs
        drawLine(
            Color(0x99FFB300),
            Offset(size.width / 2f, 0f),
            Offset(size.width / 2f, size.height),
            strokeWidth = 1.5.dp.toPx()
        )
        drawLine(
            Color(0x99FFB300),
            Offset(0f, size.height / 2f),
            Offset(size.width, size.height / 2f),
            strokeWidth = 1.5.dp.toPx()
        )
    }
}

@Composable
fun LayerItemView(
    layer: DesignLayer,
    isSelected: Boolean,
    canvasWidthPx: Float,
    canvasHeightPx: Float,
    isPlayingAnimation: Boolean,
    onSelect: () -> Unit,
    onPositionChange: (Float, Float) -> Unit,
    onScaleChange: (Float) -> Unit,
    onRotationChange: (Float) -> Unit,
    onDelete: () -> Unit
) {
    val density = LocalDensity.current
    val posX = layer.xPercent * canvasWidthPx
    val posY = layer.yPercent * canvasHeightPx

    val infiniteTransition = rememberInfiniteTransition(label = "layer_motion")

    val pulseScale by if (isPlayingAnimation && layer.animationPreset == AnimationPreset.PULSE) {
        infiniteTransition.animateFloat(
            initialValue = 0.90f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(700, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    val floatOffsetDy by if (isPlayingAnimation && layer.animationPreset == AnimationPreset.FLOAT) {
        infiniteTransition.animateFloat(
            initialValue = -12f,
            targetValue = 12f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "float"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val bounceOffsetDy by if (isPlayingAnimation && layer.animationPreset == AnimationPreset.BOUNCE) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -24f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bounce"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val rotateAngle by if (isPlayingAnimation && layer.animationPreset == AnimationPreset.ROTATE_LOOP) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotate_spin"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val shimmerAngle by if (isPlayingAnimation && layer.animationPreset == AnimationPreset.SHIMMER) {
        infiniteTransition.animateFloat(
            initialValue = -8f,
            targetValue = 8f,
            animationSpec = infiniteRepeatable(
                animation = tween(300, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "shimmer"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val breatheAlpha by if (isPlayingAnimation && layer.animationPreset == AnimationPreset.BREATHE) {
        infiniteTransition.animateFloat(
            initialValue = 0.35f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breathe"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (posX - 80.dp.toPx()).roundToInt(),
                    (posY - 60.dp.toPx() + floatOffsetDy.dp.toPx() + bounceOffsetDy.dp.toPx()).roundToInt()
                )
            }
            .rotate(layer.rotation + rotateAngle + shimmerAngle)
            .scale(layer.scale * pulseScale)
            .alpha(breatheAlpha)
            .pointerInput(layer.id, layer.isLocked) {
                if (!layer.isLocked) {
                    detectDragGestures(
                        onDragStart = { onSelect() },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val newXPercent = ((layer.xPercent * canvasWidthPx) + dragAmount.x) / canvasWidthPx
                            val newYPercent = ((layer.yPercent * canvasHeightPx) + dragAmount.y) / canvasHeightPx
                            onPositionChange(
                                newXPercent.coerceIn(0.05f, 0.95f),
                                newYPercent.coerceIn(0.05f, 0.95f)
                            )
                        }
                    )
                }
            }
            .pointerInput(layer.id) {
                detectTapGestures {
                    onSelect()
                }
            }
            .padding(12.dp)
    ) {
        // Selection Frame
        if (isSelected && !layer.isLocked) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(1.5.dp, AccentCyan, RoundedCornerShape(6.dp))
            )
        }

        // Layer Content
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (layer.type) {
                LayerType.TEXT -> RenderTextLayer(layer)
                LayerType.SHAPE -> RenderShapeLayer(layer)
                LayerType.STICKER -> RenderStickerLayer(layer)
                LayerType.IMAGE -> RenderImageLayer(layer)
            }
        }

        // Active Handles when selected
        if (isSelected && !layer.isLocked) {
            // Delete button (Top Left)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset((-6).dp, (-6).dp)
                    .size(24.dp)
                    .background(Color(0xFFFF5252), CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures { onDelete() }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Delete Layer",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Resize handle (Bottom Right)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(6.dp, 6.dp)
                    .size(24.dp)
                    .background(AccentCyan, CircleShape)
                    .pointerInput(layer.scale) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val delta = (dragAmount.x + dragAmount.y) / 100f
                            val newScale = (layer.scale + delta).coerceIn(0.4f, 4.0f)
                            onScaleChange(newScale)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF00363D), CircleShape)
                )
            }

            // Rotate handle (Top Right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(6.dp, (-6).dp)
                    .size(24.dp)
                    .background(AccentAmber, CircleShape)
                    .pointerInput(layer.rotation) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val deltaAngle = dragAmount.x * 0.8f
                            val newRot = (layer.rotation + deltaAngle) % 360f
                            onRotationChange(if (newRot < 0) newRot + 360f else newRot)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Rotate Layer",
                    tint = Color(0xFF432C00),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun RenderTextLayer(layer: DesignLayer) {
    val displayText = if (layer.isAllUppercase) layer.text.uppercase() else layer.text
    val fontFamily = when (layer.fontStyleName) {
        "Serif" -> FontFamily.Serif
        "Monospace" -> FontFamily.Monospace
        "Sans" -> FontFamily.SansSerif
        else -> FontFamily.Default
    }
    val fontWeight = if (layer.isBold) FontWeight.Bold else FontWeight.Normal
    val fontStyle = if (layer.isItalic) FontStyle.Italic else FontStyle.Normal
    val textAlign = when (layer.textAlign) {
        "LEFT" -> TextAlign.Left
        "RIGHT" -> TextAlign.Right
        else -> TextAlign.Center
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = if (layer.hasBackgroundBadge) {
            Modifier
                .background(
                    Color(layer.badgeColor).copy(alpha = layer.opacity),
                    RoundedCornerShape(layer.badgeCornerRadius.dp)
                )
                .padding(horizontal = layer.badgePadding.dp, vertical = (layer.badgePadding / 2).dp)
        } else Modifier
    ) {
        // 3D Shadow Extrusion layers
        if (layer.has3DEffect) {
            val depth = layer.depth3D.coerceIn(2f, 16f).toInt()
            for (i in depth downTo 1) {
                Text(
                    text = displayText,
                    fontSize = layer.fontSize.sp,
                    fontFamily = fontFamily,
                    fontWeight = fontWeight,
                    fontStyle = fontStyle,
                    textAlign = textAlign,
                    color = Color(layer.color3D).copy(alpha = layer.opacity * 0.9f),
                    modifier = Modifier.offset((i * 1.2f).dp, (i * 1.2f).dp)
                )
            }
        }

        // Shadow Layer
        if (layer.hasShadow) {
            Text(
                text = displayText,
                fontSize = layer.fontSize.sp,
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                textAlign = textAlign,
                color = Color(layer.shadowColor).copy(alpha = layer.opacity),
                modifier = Modifier.offset(layer.shadowDx.dp, layer.shadowDy.dp)
            )
        }

        // Stroke Layer simulation (if stroke enabled)
        if (layer.hasStroke) {
            val strokeOffsets = listOf(
                Pair(-1.5f, -1.5f), Pair(1.5f, -1.5f),
                Pair(-1.5f, 1.5f), Pair(1.5f, 1.5f)
            )
            strokeOffsets.forEach { (dx, dy) ->
                Text(
                    text = displayText,
                    fontSize = layer.fontSize.sp,
                    fontFamily = fontFamily,
                    fontWeight = fontWeight,
                    fontStyle = fontStyle,
                    textAlign = textAlign,
                    color = Color(layer.strokeColor).copy(alpha = layer.opacity),
                    modifier = Modifier.offset(dx.dp, dy.dp)
                )
            }
        }

        // Main Text
        Text(
            text = displayText,
            fontSize = layer.fontSize.sp,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            textAlign = textAlign,
            color = Color(layer.textColor).copy(alpha = layer.opacity),
            letterSpacing = layer.letterSpacing.sp
        )
    }
}

@Composable
fun RenderShapeLayer(layer: DesignLayer) {
    val baseSize = 80.dp
    val w = baseSize * layer.widthScale
    val h = baseSize * layer.heightScale

    Canvas(
        modifier = Modifier
            .size(w, h)
    ) {
        val fill = Color(layer.fillColor).copy(alpha = layer.opacity)
        val stroke = Color(layer.shapeStrokeColor).copy(alpha = layer.opacity)
        val strokeWidthPx = layer.shapeStrokeWidth.dp.toPx()

        when (layer.shapeType) {
            ShapeType.RECTANGLE -> {
                drawRect(fill)
                if (strokeWidthPx > 0) {
                    drawRect(stroke, style = Stroke(strokeWidthPx))
                }
            }
            ShapeType.ROUNDED_RECT -> {
                val cr = layer.cornerRadius.dp.toPx()
                drawRoundRect(fill, cornerRadius = androidx.compose.ui.geometry.CornerRadius(cr, cr))
                if (strokeWidthPx > 0) {
                    drawRoundRect(stroke, cornerRadius = androidx.compose.ui.geometry.CornerRadius(cr, cr), style = Stroke(strokeWidthPx))
                }
            }
            ShapeType.CIRCLE -> {
                drawCircle(fill)
                if (strokeWidthPx > 0) {
                    drawCircle(stroke, style = Stroke(strokeWidthPx))
                }
            }
            ShapeType.STAR -> {
                val path = buildStarPath(5, size.width / 2f, size.width / 4f, size.width / 2f, size.height / 2f)
                drawPath(path, fill)
                if (strokeWidthPx > 0) drawPath(path, stroke, style = Stroke(strokeWidthPx))
            }
            ShapeType.HEART -> {
                val path = buildHeartPath(size.width, size.height)
                drawPath(path, fill)
                if (strokeWidthPx > 0) drawPath(path, stroke, style = Stroke(strokeWidthPx))
            }
            ShapeType.DIAMOND -> {
                val path = Path().apply {
                    moveTo(size.width / 2f, 0f)
                    lineTo(size.width, size.height / 2f)
                    lineTo(size.width / 2f, size.height)
                    lineTo(0f, size.height / 2f)
                    close()
                }
                drawPath(path, fill)
                if (strokeWidthPx > 0) drawPath(path, stroke, style = Stroke(strokeWidthPx))
            }
            ShapeType.BADGE -> {
                val path = buildStarPath(12, size.width / 2f, size.width * 0.42f, size.width / 2f, size.height / 2f)
                drawPath(path, fill)
                if (strokeWidthPx > 0) drawPath(path, stroke, style = Stroke(strokeWidthPx))
            }
            ShapeType.HEXAGON -> {
                val path = buildPolygonPath(6, size.width / 2f, size.width / 2f, size.height / 2f)
                drawPath(path, fill)
                if (strokeWidthPx > 0) drawPath(path, stroke, style = Stroke(strokeWidthPx))
            }
            ShapeType.SHIELD -> {
                val path = Path().apply {
                    val halfW = size.width / 2f
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height * 0.5f)
                    quadraticTo(size.width, size.height * 0.9f, halfW, size.height)
                    quadraticTo(0f, size.height * 0.9f, 0f, size.height * 0.5f)
                    close()
                }
                drawPath(path, fill)
                if (strokeWidthPx > 0) drawPath(path, stroke, style = Stroke(strokeWidthPx))
            }
        }
    }
}

@Composable
fun RenderStickerLayer(layer: DesignLayer) {
    val size = 64.dp
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(layer.stickerTint).copy(alpha = layer.opacity), RoundedCornerShape(12.dp))
            .border(2.dp, Color.White.copy(alpha = layer.opacity), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = layer.stickerTag,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RenderImageLayer(layer: DesignLayer) {
    if (layer.imageUri != null) {
        val shape = if (layer.imageCropCircle) CircleShape else RoundedCornerShape(8.dp)
        AsyncImage(
            model = layer.imageUri,
            contentDescription = "User Layer Image",
            modifier = Modifier
                .size(100.dp)
                .clip(shape)
                .border(2.dp, Color.White.copy(alpha = layer.opacity), shape),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
    }
}

private fun buildStarPath(spikes: Int, outerR: Float, innerR: Float, cx: Float, cy: Float): Path {
    val path = Path()
    val step = PI / spikes
    var rot = -PI / 2
    var x = cx + cos(rot).toFloat() * outerR
    var y = cy + sin(rot).toFloat() * outerR
    path.moveTo(x, y)

    for (i in 0 until spikes) {
        x = cx + cos(rot).toFloat() * outerR
        y = cy + sin(rot).toFloat() * outerR
        path.lineTo(x, y)
        rot += step

        x = cx + cos(rot).toFloat() * innerR
        y = cy + sin(rot).toFloat() * innerR
        path.lineTo(x, y)
        rot += step
    }
    path.close()
    return path
}

private fun buildHeartPath(w: Float, h: Float): Path {
    val path = Path()
    val halfW = w / 2f
    val halfH = h / 2f
    path.moveTo(halfW, halfH * 0.7f)
    path.cubicTo(0f, -halfH * 0.4f, -halfW * 0.2f, halfH * 1.2f, halfW, h)
    path.cubicTo(w + halfW * 0.2f, halfH * 1.2f, w, -halfH * 0.4f, halfW, halfH * 0.7f)
    path.close()
    return path
}

private fun buildPolygonPath(sides: Int, radius: Float, cx: Float, cy: Float): Path {
    val path = Path()
    val angle = 2.0 * PI / sides
    for (i in 0 until sides) {
        val a = i * angle - PI / 2
        val x = cx + (radius * cos(a)).toFloat()
        val y = cy + (radius * sin(a)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
