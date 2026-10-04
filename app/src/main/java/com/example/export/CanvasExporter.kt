package com.example.export

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.model.BackgroundConfig
import com.example.model.BackgroundType
import com.example.model.CanvasRatio
import com.example.model.DesignLayer
import com.example.model.LayerType
import com.example.model.ShapeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object CanvasExporter {

    suspend fun renderToBitmap(
        context: Context,
        ratio: CanvasRatio,
        background: BackgroundConfig,
        layers: List<DesignLayer>,
        targetWidth: Int = 1200
    ): Bitmap = withContext(Dispatchers.Default) {
        val targetHeight = ((targetWidth / ratio.widthRatio) * ratio.heightRatio).toInt().coerceAtLeast(100)
        val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Background
        when (background.type) {
            BackgroundType.TRANSPARENT -> {
                // Clear transparent
                canvas.drawColor(0x00000000)
            }
            BackgroundType.SOLID_COLOR -> {
                canvas.drawColor(background.color.toInt())
            }
            BackgroundType.GRADIENT -> {
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                val colors = background.gradientColors.map { it.toInt() }.toIntArray()
                val positions = if (colors.size == 2) floatArrayOf(0f, 1f) else null
                val shader = LinearGradient(
                    0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(),
                    colors, positions, Shader.TileMode.CLAMP
                )
                paint.shader = shader
                canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), paint)
            }
            BackgroundType.TEXTURE -> {
                canvas.drawColor(background.color.toInt())
                val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0x22FFFFFF
                    strokeWidth = 2f
                    style = Paint.Style.STROKE
                }
                val step = 40f
                var x = 0f
                while (x < targetWidth) {
                    canvas.drawLine(x, 0f, x, targetHeight.toFloat(), gridPaint)
                    x += step
                }
                var y = 0f
                while (y < targetHeight) {
                    canvas.drawLine(0f, y, targetWidth.toFloat(), y, gridPaint)
                    y += step
                }
            }
            BackgroundType.IMAGE -> {
                canvas.drawColor(background.color.toInt())
                if (background.imageUri != null) {
                    try {
                        val uri = Uri.parse(background.imageUri)
                        context.contentResolver.openInputStream(uri)?.use { stream ->
                            val bgBitmap = android.graphics.BitmapFactory.decodeStream(stream)
                            if (bgBitmap != null) {
                                val destRect = RectF(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat())
                                canvas.drawBitmap(bgBitmap, null, destRect, Paint(Paint.FILTER_BITMAP_FLAG))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        // 2. Render visible layers sorted by zIndex
        val sortedLayers = layers.filter { it.isVisible }.sortedBy { it.zIndex }
        for (layer in sortedLayers) {
            val layerCenterX = layer.xPercent * targetWidth
            val layerCenterY = layer.yPercent * targetHeight

            canvas.save()
            canvas.translate(layerCenterX, layerCenterY)
            canvas.rotate(layer.rotation)
            canvas.scale(layer.scale, layer.scale)

            when (layer.type) {
                LayerType.TEXT -> drawTextLayer(canvas, layer)
                LayerType.SHAPE -> drawShapeLayer(canvas, layer)
                LayerType.STICKER -> drawStickerLayer(canvas, layer)
                LayerType.IMAGE -> drawImageLayer(context, canvas, layer)
            }

            canvas.restore()
        }

        bitmap
    }

    private fun drawTextLayer(canvas: Canvas, layer: DesignLayer) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = layer.fontSize * 2.2f // scale up for export resolution
            isFakeBoldText = layer.isBold
            typeface = when (layer.fontStyleName) {
                "Serif" -> Typeface.SERIF
                "Monospace" -> Typeface.MONOSPACE
                "Sans" -> Typeface.SANS_SERIF
                else -> if (layer.isBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            }
            if (layer.isItalic) {
                textSkewX = -0.25f
            }
            letterSpacing = layer.letterSpacing
            alpha = (layer.opacity * 255).toInt()
        }

        val displayText = if (layer.isAllUppercase) layer.text.uppercase() else layer.text
        val lines = displayText.split("\n")
        val fontMetrics = paint.fontMetrics
        val lineHeight = fontMetrics.bottom - fontMetrics.top + 10f

        val totalHeight = lines.size * lineHeight
        val startY = -totalHeight / 2f - fontMetrics.top

        // Optional badge background
        if (layer.hasBackgroundBadge) {
            var maxLineWidth = 0f
            for (line in lines) {
                val w = paint.measureText(line)
                if (w > maxLineWidth) maxLineWidth = w
            }
            val pad = layer.badgePadding * 2f
            val badgeRect = RectF(
                -maxLineWidth / 2f - pad,
                -totalHeight / 2f - pad / 2f,
                maxLineWidth / 2f + pad,
                totalHeight / 2f + pad / 2f
            )
            val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = layer.badgeColor.toInt()
                alpha = (layer.opacity * 255).toInt()
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(badgeRect, layer.badgeCornerRadius * 2f, layer.badgeCornerRadius * 2f, badgePaint)
        }

        // Draw each line
        lines.forEachIndexed { index, line ->
            val lineWidth = paint.measureText(line)
            val lineY = startY + index * lineHeight

            val lineX = when (layer.textAlign) {
                "LEFT" -> -lineWidth / 2f // or adjust as desired
                "RIGHT" -> lineWidth / 2f - lineWidth
                else -> -lineWidth / 2f
            }

            // 3D Depth Effect (drawing extruded shadow layers)
            if (layer.has3DEffect) {
                val depthPaint = Paint(paint).apply {
                    color = layer.color3D.toInt()
                    style = Paint.Style.FILL
                    clearShadowLayer()
                }
                val depthSteps = layer.depth3D.toInt().coerceIn(1, 20)
                for (d in depthSteps downTo 1) {
                    canvas.drawText(line, lineX + d * 1.5f, lineY + d * 1.5f, depthPaint)
                }
            }

            // Outer Shadow
            if (layer.hasShadow) {
                paint.setShadowLayer(
                    layer.shadowRadius * 2f,
                    layer.shadowDx * 2f,
                    layer.shadowDy * 2f,
                    layer.shadowColor.toInt()
                )
            } else {
                paint.clearShadowLayer()
            }

            // Stroke / Outline
            if (layer.hasStroke) {
                val strokePaint = Paint(paint).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = layer.strokeWidth * 2.5f
                    color = layer.strokeColor.toInt()
                    strokeJoin = Paint.Join.ROUND
                    strokeCap = Paint.Cap.ROUND
                }
                canvas.drawText(line, lineX, lineY, strokePaint)
            }

            // Main Text Fill
            paint.style = Paint.Style.FILL
            paint.color = layer.textColor.toInt()
            canvas.drawText(line, lineX, lineY, paint)
        }
    }

    private fun drawShapeLayer(canvas: Canvas, layer: DesignLayer) {
        val baseSize = 160f
        val w = baseSize * layer.widthScale
        val h = baseSize * layer.heightScale
        val rect = RectF(-w / 2f, -h / 2f, w / 2f, h / 2f)

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = layer.fillColor.toInt()
            alpha = (layer.opacity * 255).toInt()
            style = Paint.Style.FILL
        }

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = layer.shapeStrokeColor.toInt()
            alpha = (layer.opacity * 255).toInt()
            style = Paint.Style.STROKE
            strokeWidth = layer.shapeStrokeWidth * 2f
        }

        when (layer.shapeType) {
            ShapeType.RECTANGLE -> {
                canvas.drawRect(rect, fillPaint)
                if (layer.shapeStrokeWidth > 0) canvas.drawRect(rect, strokePaint)
            }
            ShapeType.ROUNDED_RECT -> {
                val cr = layer.cornerRadius * 2f
                canvas.drawRoundRect(rect, cr, cr, fillPaint)
                if (layer.shapeStrokeWidth > 0) canvas.drawRoundRect(rect, cr, cr, strokePaint)
            }
            ShapeType.CIRCLE -> {
                val radius = (w.coerceAtMost(h)) / 2f
                canvas.drawCircle(0f, 0f, radius, fillPaint)
                if (layer.shapeStrokeWidth > 0) canvas.drawCircle(0f, 0f, radius, strokePaint)
            }
            ShapeType.STAR -> {
                val path = createStarPath(5, w / 2f, w / 4f)
                canvas.drawPath(path, fillPaint)
                if (layer.shapeStrokeWidth > 0) canvas.drawPath(path, strokePaint)
            }
            ShapeType.HEART -> {
                val path = createHeartPath(w, h)
                canvas.drawPath(path, fillPaint)
                if (layer.shapeStrokeWidth > 0) canvas.drawPath(path, strokePaint)
            }
            ShapeType.DIAMOND -> {
                val path = Path().apply {
                    moveTo(0f, -h / 2f)
                    lineTo(w / 2f, 0f)
                    lineTo(0f, h / 2f)
                    lineTo(-w / 2f, 0f)
                    close()
                }
                canvas.drawPath(path, fillPaint)
                if (layer.shapeStrokeWidth > 0) canvas.drawPath(path, strokePaint)
            }
            ShapeType.BADGE -> {
                val path = createBadgePath(w / 2f)
                canvas.drawPath(path, fillPaint)
                if (layer.shapeStrokeWidth > 0) canvas.drawPath(path, strokePaint)
            }
            ShapeType.HEXAGON -> {
                val path = createPolygonPath(6, w / 2f)
                canvas.drawPath(path, fillPaint)
                if (layer.shapeStrokeWidth > 0) canvas.drawPath(path, strokePaint)
            }
            ShapeType.SHIELD -> {
                val path = createShieldPath(w, h)
                canvas.drawPath(path, fillPaint)
                if (layer.shapeStrokeWidth > 0) canvas.drawPath(path, strokePaint)
            }
        }
    }

    private fun drawStickerLayer(canvas: Canvas, layer: DesignLayer) {
        val size = 120f
        val rect = RectF(-size / 2f, -size / 2f, size / 2f, size / 2f)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = layer.stickerTint.toInt()
            alpha = (layer.opacity * 255).toInt()
        }

        // Draw stylish badge / sticker graphic with label
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = layer.stickerTint.toInt()
            style = Paint.Style.FILL
            alpha = (layer.opacity * 255).toInt()
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }

        when (layer.stickerTag) {
            "HOT", "SALE", "NEW", "50% OFF", "PRO", "OFFER", "VIP" -> {
                // Ribbed pill / star badge
                canvas.drawRoundRect(rect, 24f, 24f, bgPaint)
                val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFFFFFFFF.toInt()
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                }
                canvas.drawRoundRect(rect, 24f, 24f, borderPaint)
                val fm = textPaint.fontMetrics
                val textY = -(fm.ascent + fm.descent) / 2f
                canvas.drawText(layer.stickerTag, 0f, textY, textPaint)
            }
            "STAR" -> {
                val path = createStarPath(5, size / 2f, size / 4f)
                canvas.drawPath(path, bgPaint)
            }
            "HEART" -> {
                val path = createHeartPath(size, size)
                canvas.drawPath(path, bgPaint)
            }
            "VERIFIED" -> {
                canvas.drawCircle(0f, 0f, size / 2f, bgPaint)
                val checkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFFFFFFFF.toInt()
                    style = Paint.Style.STROKE
                    strokeWidth = 8f
                    strokeCap = Paint.Cap.ROUND
                }
                canvas.drawLine(-size / 5f, 0f, -size / 15f, size / 5f, checkPaint)
                canvas.drawLine(-size / 15f, size / 5f, size / 4f, -size / 6f, checkPaint)
            }
            else -> {
                canvas.drawRoundRect(rect, 16f, 16f, bgPaint)
                val fm = textPaint.fontMetrics
                val textY = -(fm.ascent + fm.descent) / 2f
                canvas.drawText(layer.stickerTag, 0f, textY, textPaint)
            }
        }
    }

    private fun drawImageLayer(context: Context, canvas: Canvas, layer: DesignLayer) {
        if (layer.imageUri == null) return
        try {
            val uri = Uri.parse(layer.imageUri)
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val origBitmap = android.graphics.BitmapFactory.decodeStream(stream)
                if (origBitmap != null) {
                    val size = 200f
                    val rect = RectF(-size / 2f, -size / 2f, size / 2f, size / 2f)
                    val paint = Paint(Paint.FILTER_BITMAP_FLAG).apply {
                        alpha = (layer.opacity * 255).toInt()
                    }
                    if (layer.imageCropCircle) {
                        val path = Path().apply {
                            addCircle(0f, 0f, size / 2f, Path.Direction.CW)
                        }
                        canvas.save()
                        canvas.clipPath(path)
                        canvas.drawBitmap(origBitmap, null, rect, paint)
                        canvas.restore()
                    } else {
                        canvas.drawBitmap(origBitmap, null, rect, paint)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createStarPath(spikes: Int, outerRadius: Float, innerRadius: Float): Path {
        val path = Path()
        val step = Math.PI / spikes
        var rot = Math.PI / 2 * 3
        var x = 0f
        var y = -outerRadius
        path.moveTo(x, y)

        for (i in 0 until spikes) {
            x = (Math.cos(rot) * outerRadius).toFloat()
            y = (Math.sin(rot) * outerRadius).toFloat()
            path.lineTo(x, y)
            rot += step

            x = (Math.cos(rot) * innerRadius).toFloat()
            y = (Math.sin(rot) * innerRadius).toFloat()
            path.lineTo(x, y)
            rot += step
        }
        path.close()
        return path
    }

    private fun createHeartPath(width: Float, height: Float): Path {
        val path = Path()
        val w = width / 2f
        val h = height / 2f
        path.moveTo(0f, -h / 4f)
        path.cubicTo(-w, -h * 0.9f, -w * 1.1f, h * 0.2f, 0f, h * 0.9f)
        path.cubicTo(w * 1.1f, h * 0.2f, w, -h * 0.9f, 0f, -h / 4f)
        path.close()
        return path
    }

    private fun createPolygonPath(sides: Int, radius: Float): Path {
        val path = Path()
        val angle = 2.0 * Math.PI / sides
        for (i in 0 until sides) {
            val a = i * angle - Math.PI / 2
            val x = (radius * Math.cos(a)).toFloat()
            val y = (radius * Math.sin(a)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return path
    }

    private fun createBadgePath(radius: Float): Path {
        return createStarPath(12, radius, radius * 0.82f)
    }

    private fun createShieldPath(width: Float, height: Float): Path {
        val path = Path()
        val w = width / 2f
        val h = height / 2f
        path.moveTo(-w, -h)
        path.lineTo(w, -h)
        path.lineTo(w, 0f)
        path.quadTo(w, h * 0.8f, 0f, h)
        path.quadTo(-w, h * 0.8f, -w, 0f)
        path.close()
        return path
    }

    // Save Bitmap to Cache and return Uri for Sharing
    suspend fun saveToCache(context: Context, bitmap: Bitmap, isPng: Boolean = true): Uri = withContext(Dispatchers.IO) {
        val cacheFolder = File(context.cacheDir, "images").apply { mkdirs() }
        val ext = if (isPng) "png" else "jpg"
        val file = File(cacheFolder, "pixellab_export_${System.currentTimeMillis()}.$ext")
        FileOutputStream(file).use { out ->
            val format = if (isPng) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
            bitmap.compress(format, 100, out)
        }
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    // Save Bitmap to Device MediaStore Pictures
    suspend fun saveToGallery(context: Context, bitmap: Bitmap, isPng: Boolean = true): Boolean = withContext(Dispatchers.IO) {
        try {
            val filename = "PixelLab_${System.currentTimeMillis()}.${if (isPng) "png" else "jpg"}"
            val mimeType = if (isPng) "image/png" else "image/jpeg"
            val format = if (isPng) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PixelLab")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(format, 100, out)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, values, null, null)
                }
                return@withContext true
            }
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun shareImage(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Graphic via PixelLab"))
    }

    suspend fun exportToPdf(
        context: Context,
        ratio: CanvasRatio,
        background: BackgroundConfig,
        layers: List<DesignLayer>,
        customDimension: com.example.model.CustomCanvasDimension?
    ): Uri = withContext(Dispatchers.IO) {
        val pdfDocument = android.graphics.pdf.PdfDocument()

        val pageWidth = customDimension?.toPdfPointsWidth() ?: if (ratio.widthRatio >= ratio.heightRatio) 842 else 595
        val pageHeight = customDimension?.toPdfPointsHeight() ?: if (ratio.widthRatio >= ratio.heightRatio) 595 else 842

        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val renderPxWidth = customDimension?.toPixelWidth() ?: 2400
        val bitmap = renderToBitmap(context, ratio, background, layers, renderPxWidth)
        val destRect = RectF(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat())
        canvas.drawBitmap(bitmap, null, destRect, Paint(Paint.FILTER_BITMAP_FLAG))

        pdfDocument.finishPage(page)

        val cacheFolder = File(context.cacheDir, "images").apply { mkdirs() }
        val pdfFile = File(cacheFolder, "PixelLab_Document_${System.currentTimeMillis()}.pdf")
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        bitmap.recycle()

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
    }

    suspend fun savePdfToDocuments(
        context: Context,
        ratio: CanvasRatio,
        background: BackgroundConfig,
        layers: List<DesignLayer>,
        customDimension: com.example.model.CustomCanvasDimension?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val pdfDocument = android.graphics.pdf.PdfDocument()
            val pageWidth = customDimension?.toPdfPointsWidth() ?: if (ratio.widthRatio >= ratio.heightRatio) 842 else 595
            val pageHeight = customDimension?.toPdfPointsHeight() ?: if (ratio.widthRatio >= ratio.heightRatio) 595 else 842

            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val renderPxWidth = customDimension?.toPixelWidth() ?: 2400
            val bitmap = renderToBitmap(context, ratio, background, layers, renderPxWidth)
            val destRect = RectF(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat())
            canvas.drawBitmap(bitmap, null, destRect, Paint(Paint.FILTER_BITMAP_FLAG))

            pdfDocument.finishPage(page)

            val filename = "PixelLab_${System.currentTimeMillis()}.pdf"
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/PixelLab")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val uri = context.contentResolver.insert(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                else MediaStore.Files.getContentUri("external"),
                values
            )

            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    pdfDocument.writeTo(out)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    context.contentResolver.update(uri, values, null, null)
                }
                pdfDocument.close()
                bitmap.recycle()
                return@withContext true
            }
            pdfDocument.close()
            bitmap.recycle()
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun sharePdf(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share PDF Document via PixelLab"))
    }
}

