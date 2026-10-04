package com.example.model

import androidx.compose.ui.graphics.Color
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

enum class CanvasRatio(var label: String, var widthRatio: Float, var heightRatio: Float, val description: String) {
    SQUARE("1:1 Square", 1f, 1f, "Instagram, Post, Logo"),
    YOUTUBE("16:9 Landscape", 16f, 9f, "YouTube Thumbnail, Cover"),
    STORY("9:16 Portrait", 9f, 16f, "Story, Reels, TikTok, Status"),
    PORTRAIT("4:5 Feed", 4f, 5f, "Instagram Portrait"),
    BANNER("2:1 Banner", 2f, 1f, "Twitter Header, Web Banner"),
    PHOTO("4:3 Standard", 4f, 3f, "Flyer, Poster, Card"),
    CUSTOM("Custom Dimension", 1f, 1f, "Custom px, in, cm, mm")
}

enum class MeasurementUnit(val symbol: String, val displayName: String) {
    PIXEL("px", "Pixel (px)"),
    INCH("in", "Inch (in)"),
    CENTIMETER("cm", "Centimeter (cm)"),
    MILLIMETER("mm", "Millimeter (mm)")
}

data class CustomCanvasDimension(
    val width: Float = 1080f,
    val height: Float = 1080f,
    val unit: MeasurementUnit = MeasurementUnit.PIXEL,
    val dpi: Int = 300,
    val presetName: String? = null
) {
    fun toPixelWidth(): Int {
        val px = when (unit) {
            MeasurementUnit.PIXEL -> width
            MeasurementUnit.INCH -> width * dpi
            MeasurementUnit.CENTIMETER -> (width / 2.54f) * dpi
            MeasurementUnit.MILLIMETER -> (width / 25.4f) * dpi
        }
        return px.toInt().coerceIn(100, 8000)
    }

    fun toPixelHeight(): Int {
        val px = when (unit) {
            MeasurementUnit.PIXEL -> height
            MeasurementUnit.INCH -> height * dpi
            MeasurementUnit.CENTIMETER -> (height / 2.54f) * dpi
            MeasurementUnit.MILLIMETER -> (height / 25.4f) * dpi
        }
        return px.toInt().coerceIn(100, 8000)
    }

    fun toPdfPointsWidth(): Int {
        val inches = when (unit) {
            MeasurementUnit.PIXEL -> width / dpi
            MeasurementUnit.INCH -> width
            MeasurementUnit.CENTIMETER -> width / 2.54f
            MeasurementUnit.MILLIMETER -> width / 25.4f
        }
        return (inches * 72f).toInt().coerceAtLeast(72)
    }

    fun toPdfPointsHeight(): Int {
        val inches = when (unit) {
            MeasurementUnit.PIXEL -> height / dpi
            MeasurementUnit.INCH -> height
            MeasurementUnit.CENTIMETER -> height / 2.54f
            MeasurementUnit.MILLIMETER -> height / 25.4f
        }
        return (inches * 72f).toInt().coerceAtLeast(72)
    }

    fun convertTo(targetUnit: MeasurementUnit): Pair<Float, Float> {
        val inchesW = when (unit) {
            MeasurementUnit.PIXEL -> width / dpi
            MeasurementUnit.INCH -> width
            MeasurementUnit.CENTIMETER -> width / 2.54f
            MeasurementUnit.MILLIMETER -> width / 25.4f
        }
        val inchesH = when (unit) {
            MeasurementUnit.PIXEL -> height / dpi
            MeasurementUnit.INCH -> height
            MeasurementUnit.CENTIMETER -> height / 2.54f
            MeasurementUnit.MILLIMETER -> height / 25.4f
        }
        val targetW = when (targetUnit) {
            MeasurementUnit.PIXEL -> inchesW * dpi
            MeasurementUnit.INCH -> inchesW
            MeasurementUnit.CENTIMETER -> inchesW * 2.54f
            MeasurementUnit.MILLIMETER -> inchesW * 25.4f
        }
        val targetH = when (targetUnit) {
            MeasurementUnit.PIXEL -> inchesH * dpi
            MeasurementUnit.INCH -> inchesH
            MeasurementUnit.CENTIMETER -> inchesH * 2.54f
            MeasurementUnit.MILLIMETER -> inchesH * 25.4f
        }
        return Pair(targetW, targetH)
    }

    companion object {
        val PRESET_A4 = CustomCanvasDimension(210f, 297f, MeasurementUnit.MILLIMETER, 300, "A4 Document (210×297 mm)")
        val PRESET_A3 = CustomCanvasDimension(297f, 420f, MeasurementUnit.MILLIMETER, 300, "A3 Poster (297×420 mm)")
        val PRESET_BUSINESS_CARD = CustomCanvasDimension(85f, 55f, MeasurementUnit.MILLIMETER, 300, "Business Card (85×55 mm)")
        val PRESET_LETTER = CustomCanvasDimension(8.5f, 11f, MeasurementUnit.INCH, 300, "US Letter (8.5×11 in)")
        val PRESET_POSTCARD = CustomCanvasDimension(4f, 6f, MeasurementUnit.INCH, 300, "Photo Postcard (4×6 in)")
        val PRESET_SQUARE_HD = CustomCanvasDimension(1080f, 1080f, MeasurementUnit.PIXEL, 72, "Square HD (1080×1080 px)")
        val PRESET_YOUTUBE_THUMB = CustomCanvasDimension(1280f, 720f, MeasurementUnit.PIXEL, 72, "YouTube Thumb (1280×720 px)")
    }
}

enum class AnimationPreset(val displayName: String, val description: String) {
    NONE("None (স্থির)", "No motion"),
    PULSE("Pulse (স্পন্দন)", "Scale pulsation"),
    FLOAT("Float (ভাসমান)", "Smooth vertical float"),
    BOUNCE("Bounce (লাফানো)", "Up & down bounce"),
    ROTATE_LOOP("Rotate (ঘূর্ণন)", "Continuous 360° spin"),
    SHIMMER("Wiggle (ঝলক)", "Side-to-side subtle shake"),
    BREATHE("Breathe (শ্বাসপ্রশ্বাস)", "Smooth opacity wave")
}

enum class BackgroundType {
    SOLID_COLOR,
    GRADIENT,
    TEXTURE,
    TRANSPARENT,
    IMAGE
}

data class BackgroundConfig(
    val type: BackgroundType = BackgroundType.SOLID_COLOR,
    val color: Long = 0xFF1E2230,
    val gradientColors: List<Long> = listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364),
    val gradientAngle: Float = 45f,
    val texturePattern: String = "GRID",
    val imageUri: String? = null
) {
    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("type", type.name)
        obj.put("color", color)
        val gradArray = JSONArray()
        gradientColors.forEach { gradArray.put(it) }
        obj.put("gradientColors", gradArray)
        obj.put("gradientAngle", gradientAngle.toDouble())
        obj.put("texturePattern", texturePattern)
        obj.put("imageUri", imageUri ?: "")
        return obj
    }

    companion object {
        fun fromJson(obj: JSONObject): BackgroundConfig {
            val typeStr = obj.optString("type", BackgroundType.SOLID_COLOR.name)
            val type = try { BackgroundType.valueOf(typeStr) } catch (e: Exception) { BackgroundType.SOLID_COLOR }
            val color = obj.optLong("color", 0xFF1E2230)
            val gradColors = mutableListOf<Long>()
            val gradArray = obj.optJSONArray("gradientColors")
            if (gradArray != null) {
                for (i in 0 until gradArray.length()) {
                    gradColors.add(gradArray.getLong(i))
                }
            } else {
                gradColors.addAll(listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364))
            }
            val angle = obj.optDouble("gradientAngle", 45.0).toFloat()
            val pattern = obj.optString("texturePattern", "GRID")
            val imageUri = obj.optString("imageUri", "").takeIf { it.isNotEmpty() }
            return BackgroundConfig(type, color, gradColors, angle, pattern, imageUri)
        }
    }
}

enum class LayerType {
    TEXT,
    SHAPE,
    STICKER,
    IMAGE
}

enum class ShapeType(val displayName: String) {
    RECTANGLE("Rectangle"),
    ROUNDED_RECT("Rounded"),
    CIRCLE("Circle"),
    STAR("Star"),
    HEART("Heart"),
    DIAMOND("Diamond"),
    BADGE("Badge"),
    HEXAGON("Hexagon"),
    SHIELD("Shield")
}

data class DesignLayer(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "Layer",
    val type: LayerType = LayerType.TEXT,
    var xPercent: Float = 0.5f,
    var yPercent: Float = 0.5f,
    var scale: Float = 1.0f,
    var rotation: Float = 0f,
    var opacity: Float = 1.0f,
    var isVisible: Boolean = true,
    var isLocked: Boolean = false,
    var zIndex: Int = 0,

    // Animation
    var animationPreset: AnimationPreset = AnimationPreset.NONE,
    var animationSpeed: Float = 1.0f,

    // Text Attributes
    var text: String = "New Text",
    var textColor: Long = 0xFFFFFFFF,
    var fontSize: Float = 32f,
    var fontStyleName: String = "BoldHeading",
    var isBold: Boolean = true,
    var isItalic: Boolean = false,
    var isAllUppercase: Boolean = false,
    var textAlign: String = "CENTER",
    var hasShadow: Boolean = true,
    var shadowColor: Long = 0xAA000000,
    var shadowRadius: Float = 10f,
    var shadowDx: Float = 4f,
    var shadowDy: Float = 6f,
    var hasStroke: Boolean = false,
    var strokeColor: Long = 0xFF000000,
    var strokeWidth: Float = 4f,
    var has3DEffect: Boolean = false,
    var depth3D: Float = 12f,
    var color3D: Long = 0xFF00838F,
    var hasBackgroundBadge: Boolean = false,
    var badgeColor: Long = 0xFF00E5FF,
    var badgeCornerRadius: Float = 12f,
    var badgePadding: Float = 16f,
    var letterSpacing: Float = 0f,

    // Shape Attributes
    var shapeType: ShapeType = ShapeType.ROUNDED_RECT,
    var fillColor: Long = 0xFF00E5FF,
    var shapeStrokeColor: Long = 0xFFFFFFFF,
    var shapeStrokeWidth: Float = 0f,
    var cornerRadius: Float = 24f,
    var widthScale: Float = 1.0f,
    var heightScale: Float = 1.0f,

    // Sticker Attributes
    var stickerTag: String = "HOT",
    var stickerTint: Long = 0xFFFFB300,

    // Image Attributes
    var imageUri: String? = null,
    var imageCropCircle: Boolean = false
) {
    fun copyLayer(): DesignLayer {
        return this.copy(
            id = UUID.randomUUID().toString(),
            name = "$name (Copy)",
            xPercent = (xPercent + 0.04f).coerceAtMost(0.9f),
            yPercent = (yPercent + 0.04f).coerceAtMost(0.9f)
        )
    }

    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("name", name)
        obj.put("type", type.name)
        obj.put("xPercent", xPercent.toDouble())
        obj.put("yPercent", yPercent.toDouble())
        obj.put("scale", scale.toDouble())
        obj.put("rotation", rotation.toDouble())
        obj.put("opacity", opacity.toDouble())
        obj.put("isVisible", isVisible)
        obj.put("isLocked", isLocked)
        obj.put("zIndex", zIndex)
        obj.put("animationPreset", animationPreset.name)
        obj.put("animationSpeed", animationSpeed.toDouble())

        // Text
        obj.put("text", text)
        obj.put("textColor", textColor)
        obj.put("fontSize", fontSize.toDouble())
        obj.put("fontStyleName", fontStyleName)
        obj.put("isBold", isBold)
        obj.put("isItalic", isItalic)
        obj.put("isAllUppercase", isAllUppercase)
        obj.put("textAlign", textAlign)
        obj.put("hasShadow", hasShadow)
        obj.put("shadowColor", shadowColor)
        obj.put("shadowRadius", shadowRadius.toDouble())
        obj.put("shadowDx", shadowDx.toDouble())
        obj.put("shadowDy", shadowDy.toDouble())
        obj.put("hasStroke", hasStroke)
        obj.put("strokeColor", strokeColor)
        obj.put("strokeWidth", strokeWidth.toDouble())
        obj.put("has3DEffect", has3DEffect)
        obj.put("depth3D", depth3D.toDouble())
        obj.put("color3D", color3D)
        obj.put("hasBackgroundBadge", hasBackgroundBadge)
        obj.put("badgeColor", badgeColor)
        obj.put("badgeCornerRadius", badgeCornerRadius.toDouble())
        obj.put("badgePadding", badgePadding.toDouble())
        obj.put("letterSpacing", letterSpacing.toDouble())

        // Shape
        obj.put("shapeType", shapeType.name)
        obj.put("fillColor", fillColor)
        obj.put("shapeStrokeColor", shapeStrokeColor)
        obj.put("shapeStrokeWidth", shapeStrokeWidth.toDouble())
        obj.put("cornerRadius", cornerRadius.toDouble())
        obj.put("widthScale", widthScale.toDouble())
        obj.put("heightScale", heightScale.toDouble())

        // Sticker
        obj.put("stickerTag", stickerTag)
        obj.put("stickerTint", stickerTint)

        // Image
        obj.put("imageUri", imageUri ?: "")
        obj.put("imageCropCircle", imageCropCircle)

        return obj
    }

    companion object {
        fun fromJson(obj: JSONObject): DesignLayer {
            val id = obj.optString("id", UUID.randomUUID().toString())
            val name = obj.optString("name", "Layer")
            val typeStr = obj.optString("type", LayerType.TEXT.name)
            val type = try { LayerType.valueOf(typeStr) } catch (e: Exception) { LayerType.TEXT }
            val xPercent = obj.optDouble("xPercent", 0.5).toFloat()
            val yPercent = obj.optDouble("yPercent", 0.5).toFloat()
            val scale = obj.optDouble("scale", 1.0).toFloat()
            val rotation = obj.optDouble("rotation", 0.0).toFloat()
            val opacity = obj.optDouble("opacity", 1.0).toFloat()
            val isVisible = obj.optBoolean("isVisible", true)
            val isLocked = obj.optBoolean("isLocked", false)
            val zIndex = obj.optInt("zIndex", 0)

            val animStr = obj.optString("animationPreset", AnimationPreset.NONE.name)
            val animationPreset = try { AnimationPreset.valueOf(animStr) } catch (e: Exception) { AnimationPreset.NONE }
            val animationSpeed = obj.optDouble("animationSpeed", 1.0).toFloat()

            val text = obj.optString("text", "New Text")
            val textColor = obj.optLong("textColor", 0xFFFFFFFF)
            val fontSize = obj.optDouble("fontSize", 32.0).toFloat()
            val fontStyleName = obj.optString("fontStyleName", "BoldHeading")
            val isBold = obj.optBoolean("isBold", true)
            val isItalic = obj.optBoolean("isItalic", false)
            val isAllUppercase = obj.optBoolean("isAllUppercase", false)
            val textAlign = obj.optString("textAlign", "CENTER")
            val hasShadow = obj.optBoolean("hasShadow", true)
            val shadowColor = obj.optLong("shadowColor", 0xAA000000)
            val shadowRadius = obj.optDouble("shadowRadius", 10.0).toFloat()
            val shadowDx = obj.optDouble("shadowDx", 4.0).toFloat()
            val shadowDy = obj.optDouble("shadowDy", 6.0).toFloat()
            val hasStroke = obj.optBoolean("hasStroke", false)
            val strokeColor = obj.optLong("strokeColor", 0xFF000000)
            val strokeWidth = obj.optDouble("strokeWidth", 4.0).toFloat()
            val has3DEffect = obj.optBoolean("has3DEffect", false)
            val depth3D = obj.optDouble("depth3D", 12.0).toFloat()
            val color3D = obj.optLong("color3D", 0xFF00838F)
            val hasBackgroundBadge = obj.optBoolean("hasBackgroundBadge", false)
            val badgeColor = obj.optLong("badgeColor", 0xFF00E5FF)
            val badgeCornerRadius = obj.optDouble("badgeCornerRadius", 12.0).toFloat()
            val badgePadding = obj.optDouble("badgePadding", 16.0).toFloat()
            val letterSpacing = obj.optDouble("letterSpacing", 0.0).toFloat()

            val shapeTypeStr = obj.optString("shapeType", ShapeType.ROUNDED_RECT.name)
            val shapeType = try { ShapeType.valueOf(shapeTypeStr) } catch (e: Exception) { ShapeType.ROUNDED_RECT }
            val fillColor = obj.optLong("fillColor", 0xFF00E5FF)
            val shapeStrokeColor = obj.optLong("shapeStrokeColor", 0xFFFFFFFF)
            val shapeStrokeWidth = obj.optDouble("shapeStrokeWidth", 0.0).toFloat()
            val cornerRadius = obj.optDouble("cornerRadius", 24.0).toFloat()
            val widthScale = obj.optDouble("widthScale", 1.0).toFloat()
            val heightScale = obj.optDouble("heightScale", 1.0).toFloat()

            val stickerTag = obj.optString("stickerTag", "HOT")
            val stickerTint = obj.optLong("stickerTint", 0xFFFFB300)

            val imageUri = obj.optString("imageUri", "").takeIf { it.isNotEmpty() }
            val imageCropCircle = obj.optBoolean("imageCropCircle", false)

            return DesignLayer(
                id = id,
                name = name,
                type = type,
                xPercent = xPercent,
                yPercent = yPercent,
                scale = scale,
                rotation = rotation,
                opacity = opacity,
                isVisible = isVisible,
                isLocked = isLocked,
                zIndex = zIndex,
                animationPreset = animationPreset,
                animationSpeed = animationSpeed,
                text = text,
                textColor = textColor,
                fontSize = fontSize,
                fontStyleName = fontStyleName,
                isBold = isBold,
                isItalic = isItalic,
                isAllUppercase = isAllUppercase,
                textAlign = textAlign,
                hasShadow = hasShadow,
                shadowColor = shadowColor,
                shadowRadius = shadowRadius,
                shadowDx = shadowDx,
                shadowDy = shadowDy,
                hasStroke = hasStroke,
                strokeColor = strokeColor,
                strokeWidth = strokeWidth,
                has3DEffect = has3DEffect,
                depth3D = depth3D,
                color3D = color3D,
                hasBackgroundBadge = hasBackgroundBadge,
                badgeColor = badgeColor,
                badgeCornerRadius = badgeCornerRadius,
                badgePadding = badgePadding,
                letterSpacing = letterSpacing,
                shapeType = shapeType,
                fillColor = fillColor,
                shapeStrokeColor = shapeStrokeColor,
                shapeStrokeWidth = shapeStrokeWidth,
                cornerRadius = cornerRadius,
                widthScale = widthScale,
                heightScale = heightScale,
                stickerTag = stickerTag,
                stickerTint = stickerTint,
                imageUri = imageUri,
                imageCropCircle = imageCropCircle
            )
        }
    }
}
