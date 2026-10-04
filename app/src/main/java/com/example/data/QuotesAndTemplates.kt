package com.example.data

import com.example.model.BackgroundConfig
import com.example.model.BackgroundType
import com.example.model.CanvasRatio
import com.example.model.DesignLayer
import com.example.model.LayerType
import com.example.model.ShapeType
import java.util.UUID

data class QuoteItem(
    val category: String,
    val text: String,
    val author: String = "",
    val isBengali: Boolean = false
)

data class TemplateItem(
    val id: String,
    val title: String,
    val category: String,
    val ratio: CanvasRatio,
    val backgroundConfig: BackgroundConfig,
    val layers: List<DesignLayer>
)

object QuotesAndTemplates {

    val quotes = listOf(
        // Bengali Quotes
        QuoteItem("অনুপ্রেরণা", "স্বপ্ন সেটা নয় যা তুমি ঘুমিয়ে দেখ, স্বপ্ন সেটাই যা তোমাকে ঘুমাতে দেয় না।", "এ পি জে আব্দুল কালাম", true),
        QuoteItem("অনুপ্রেরণা", "চেষ্টা করো, জয় তোমার হবেই। কখনো হাল ছেড়ো না।", "PixelLab", true),
        QuoteItem("অনুপ্রেরণা", "নিজেকে বিশ্বাস করো, অসম্ভব বলে কিছু নেই।", "উক্তি", true),
        QuoteItem("অনুপ্রেরণা", "কঠিন পথই তোমাকে সুন্দর গন্তব্যে পৌঁছে দেবে।", "উক্তি", true),
        QuoteItem("সাফল্য", "পরিশ্রমই সাফল্যের চাবিকাঠি। প্রতিদিন নিজের সেরাটা দাও।", "উক্তি", true),
        QuoteItem("সাফল্য", "নতুন উদ্যমে শুরু হোক প্রতিটি দিন।", "PixelLab", true),
        QuoteItem("সাফল্য", "ধৈর্য এবং একাগ্রতা সব বাধা অতিক্রম করে।", "উক্তি", true),
        QuoteItem("ভালোবাসা ও জীবন", "জীবন সুন্দর, সুন্দরভাবে উপভোগ করো।", "উক্তি", true),
        QuoteItem("ভালোবাসা ও জীবন", "এক চিলতে হাসি বদলে দিতে পারে তোমার পুরো দিন।", "উক্তি", true),
        QuoteItem("উৎসব", "ঈদ মোবারক! আনন্দ ছড়িয়ে পড়ুক সবার মাঝে।", "উৎসব বার্তা", true),
        QuoteItem("উৎসব", "শুভ নববর্ষ! নতুন বছর আনুক অনাবিল সুখ ও সমৃদ্ধি।", "উৎসব বার্তা", true),
        QuoteItem("উৎসব", "বিজয় দিবসের রক্তিম শুভেচ্ছা ও শ্রদ্ধা।", "দেশপ্রেম", true),

        // English Quotes
        QuoteItem("Motivation", "Creativity is intelligence having fun.", "Albert Einstein"),
        QuoteItem("Motivation", "Make it happen. Shock everyone.", "Daily Grind"),
        QuoteItem("Motivation", "Dream big, work hard, stay humble.", "PixelLab"),
        QuoteItem("Motivation", "Turn your ideas into visual reality.", "Design Quote"),
        QuoteItem("Promotion", "MEGA SALE! UP TO 50% OFF TODAY", "Limited Offer"),
        QuoteItem("Promotion", "SPECIAL OFFER - GRAB YOURS NOW", "Hot Deal"),
        QuoteItem("Promotion", "NEW ARRIVALS - DISCOVER THE TREND", "Shop Now"),
        QuoteItem("Creator", "DON'T MISS THIS! WATCH TILL THE END", "YouTube"),
        QuoteItem("Creator", "NEW VIDEO OUT NOW - LINK IN BIO", "Social Post"),
        QuoteItem("Creator", "EPIC HIGHLIGHTS & BEST MOMENTS", "Gaming")
    )

    fun getSampleTemplates(): List<TemplateItem> {
        return listOf(
            // 1. YouTube Tech Review Thumbnail (16:9)
            TemplateItem(
                id = "tpl_yt_tech",
                title = "Tech Review Thumbnail",
                category = "YouTube",
                ratio = CanvasRatio.YOUTUBE,
                backgroundConfig = BackgroundConfig(
                    type = BackgroundType.GRADIENT,
                    gradientColors = listOf(0xFF0F172A, 0xFF1E1B4B, 0xFF0284C7),
                    gradientAngle = 45f
                ),
                layers = listOf(
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Accent Glow Shape",
                        type = LayerType.SHAPE,
                        xPercent = 0.8f,
                        yPercent = 0.5f,
                        scale = 1.3f,
                        shapeType = ShapeType.CIRCLE,
                        fillColor = 0x3300E5FF,
                        shapeStrokeWidth = 0f,
                        opacity = 0.7f,
                        zIndex = 0
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Review Badge",
                        type = LayerType.TEXT,
                        text = "NEW REVIEW 2026",
                        textColor = 0xFFFFFFFF,
                        fontSize = 18f,
                        xPercent = 0.35f,
                        yPercent = 0.28f,
                        hasBackgroundBadge = true,
                        badgeColor = 0xFFFF5252,
                        badgeCornerRadius = 8f,
                        badgePadding = 12f,
                        isBold = true,
                        zIndex = 1
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Main Tech Title",
                        type = LayerType.TEXT,
                        text = "ULTRA SPEED\nSMARTPHONE!",
                        textColor = 0xFF00E5FF,
                        fontSize = 38f,
                        xPercent = 0.42f,
                        yPercent = 0.58f,
                        fontStyleName = "BoldHeading",
                        isBold = true,
                        hasStroke = true,
                        strokeColor = 0xFF000000,
                        strokeWidth = 6f,
                        hasShadow = true,
                        shadowColor = 0xDD000000,
                        shadowRadius = 14f,
                        has3DEffect = true,
                        depth3D = 10f,
                        color3D = 0xFF005662,
                        zIndex = 2
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Hot Sticker",
                        type = LayerType.STICKER,
                        stickerTag = "HOT",
                        stickerTint = 0xFFFFB300,
                        xPercent = 0.85f,
                        yPercent = 0.25f,
                        scale = 1.4f,
                        rotation = 12f,
                        zIndex = 3
                    )
                )
            ),

            // 2. Mega Sale 50% Off Poster (1:1)
            TemplateItem(
                id = "tpl_mega_sale",
                title = "Mega Sale 50% OFF",
                category = "Marketing",
                ratio = CanvasRatio.SQUARE,
                backgroundConfig = BackgroundConfig(
                    type = BackgroundType.GRADIENT,
                    gradientColors = listOf(0xFF881337, 0xFF4C0519, 0xFF1C1917),
                    gradientAngle = 135f
                ),
                layers = listOf(
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Discount Shield",
                        type = LayerType.SHAPE,
                        shapeType = ShapeType.ROUNDED_RECT,
                        fillColor = 0x22FFFFFF,
                        shapeStrokeColor = 0xFFFFB703,
                        shapeStrokeWidth = 3f,
                        cornerRadius = 32f,
                        widthScale = 1.3f,
                        heightScale = 1.4f,
                        xPercent = 0.5f,
                        yPercent = 0.5f,
                        zIndex = 0
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Headline",
                        type = LayerType.TEXT,
                        text = "MEGA SALE",
                        textColor = 0xFFFFB703,
                        fontSize = 44f,
                        fontStyleName = "BoldHeading",
                        xPercent = 0.5f,
                        yPercent = 0.32f,
                        isBold = true,
                        hasShadow = true,
                        shadowColor = 0xFF000000,
                        hasStroke = true,
                        strokeColor = 0xFF000000,
                        strokeWidth = 4f,
                        zIndex = 1
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Percent Off",
                        type = LayerType.TEXT,
                        text = "50% OFF",
                        textColor = 0xFFFFFFFF,
                        fontSize = 58f,
                        fontStyleName = "BoldHeading",
                        xPercent = 0.5f,
                        yPercent = 0.52f,
                        isBold = true,
                        has3DEffect = true,
                        depth3D = 14f,
                        color3D = 0xFF9F1239,
                        hasShadow = true,
                        shadowColor = 0xEE000000,
                        zIndex = 2
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Call to Action",
                        type = LayerType.TEXT,
                        text = "SHOP NOW | LIMITED TIME",
                        textColor = 0xFF10121A,
                        fontSize = 18f,
                        xPercent = 0.5f,
                        yPercent = 0.74f,
                        hasBackgroundBadge = true,
                        badgeColor = 0xFFFFB703,
                        badgeCornerRadius = 24f,
                        badgePadding = 18f,
                        isBold = true,
                        zIndex = 3
                    )
                )
            ),

            // 3. Bengali Motivation Art (4:5)
            TemplateItem(
                id = "tpl_bengali_quote",
                title = "বাংলা অনুপ্রেরণা পোস্টার",
                category = "Quotes",
                ratio = CanvasRatio.PORTRAIT,
                backgroundConfig = BackgroundConfig(
                    type = BackgroundType.GRADIENT,
                    gradientColors = listOf(0xFF064E3B, 0xFF022C22, 0xFF0F172A),
                    gradientAngle = 60f
                ),
                layers = listOf(
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Decorative Frame",
                        type = LayerType.SHAPE,
                        shapeType = ShapeType.ROUNDED_RECT,
                        fillColor = 0x1100E676,
                        shapeStrokeColor = 0xFF34D399,
                        shapeStrokeWidth = 2.5f,
                        cornerRadius = 24f,
                        widthScale = 1.25f,
                        heightScale = 1.35f,
                        xPercent = 0.5f,
                        yPercent = 0.5f,
                        zIndex = 0
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Top Quote Tag",
                        type = LayerType.TEXT,
                        text = "• অনুপ্রেরণা বাণী •",
                        textColor = 0xFF6EE7B7,
                        fontSize = 16f,
                        xPercent = 0.5f,
                        yPercent = 0.28f,
                        isBold = true,
                        zIndex = 1
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Main Bengali Quote",
                        type = LayerType.TEXT,
                        text = "স্বপ্ন সত্যি হবেই!\nকখনো হাল ছেড়ো না।",
                        textColor = 0xFFFFFFFF,
                        fontSize = 32f,
                        xPercent = 0.5f,
                        yPercent = 0.48f,
                        isBold = true,
                        hasShadow = true,
                        shadowColor = 0xFF000000,
                        shadowRadius = 12f,
                        has3DEffect = true,
                        depth3D = 8f,
                        color3D = 0xFF047857,
                        zIndex = 2
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Subtext",
                        type = LayerType.TEXT,
                        text = "নিজের উপর বিশ্বাস রাখো, বিজয় তোমারই হবে।",
                        textColor = 0xFFA7F3D0,
                        fontSize = 18f,
                        xPercent = 0.5f,
                        yPercent = 0.68f,
                        isItalic = true,
                        zIndex = 3
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Verified Star",
                        type = LayerType.STICKER,
                        stickerTag = "STAR",
                        stickerTint = 0xFFFFD700,
                        xPercent = 0.5f,
                        yPercent = 0.82f,
                        scale = 1.2f,
                        zIndex = 4
                    )
                )
            ),

            // 4. Eid Festival Greeting (1:1)
            TemplateItem(
                id = "tpl_eid_fest",
                title = "Eid Mubarak Greeting",
                category = "Festival",
                ratio = CanvasRatio.SQUARE,
                backgroundConfig = BackgroundConfig(
                    type = BackgroundType.GRADIENT,
                    gradientColors = listOf(0xFF064E3B, 0xFF042F2E, 0xFF14532D),
                    gradientAngle = 45f
                ),
                layers = listOf(
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Badge Card",
                        type = LayerType.SHAPE,
                        shapeType = ShapeType.BADGE,
                        fillColor = 0x22FFFFFF,
                        shapeStrokeColor = 0xFFFFD700,
                        shapeStrokeWidth = 3f,
                        cornerRadius = 28f,
                        widthScale = 1.3f,
                        heightScale = 1.3f,
                        xPercent = 0.5f,
                        yPercent = 0.5f,
                        zIndex = 0
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "English Greeting",
                        type = LayerType.TEXT,
                        text = "EID MUBARAK",
                        textColor = 0xFFFFD700,
                        fontSize = 38f,
                        fontStyleName = "BoldHeading",
                        xPercent = 0.5f,
                        yPercent = 0.38f,
                        isBold = true,
                        hasStroke = true,
                        strokeColor = 0xFF000000,
                        strokeWidth = 3f,
                        hasShadow = true,
                        shadowColor = 0xFF000000,
                        zIndex = 1
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Bengali Greeting",
                        type = LayerType.TEXT,
                        text = "ঈদ মোবারক",
                        textColor = 0xFFFFFFFF,
                        fontSize = 42f,
                        xPercent = 0.5f,
                        yPercent = 0.55f,
                        isBold = true,
                        has3DEffect = true,
                        depth3D = 8f,
                        color3D = 0xFFB45309,
                        zIndex = 2
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Wishes Text",
                        type = LayerType.TEXT,
                        text = "আনন্দে কাটুক প্রতিটি মুহূর্ত",
                        textColor = 0xFFFEF08A,
                        fontSize = 18f,
                        xPercent = 0.5f,
                        yPercent = 0.72f,
                        zIndex = 3
                    )
                )
            ),

            // 5. Gaming Esports Poster (16:9)
            TemplateItem(
                id = "tpl_gaming_esports",
                title = "Esports Championship",
                category = "Gaming",
                ratio = CanvasRatio.YOUTUBE,
                backgroundConfig = BackgroundConfig(
                    type = BackgroundType.GRADIENT,
                    gradientColors = listOf(0xFF3B0764, 0xFF1E1B4B, 0xFF0F172A),
                    gradientAngle = 90f
                ),
                layers = listOf(
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Esports Shield",
                        type = LayerType.SHAPE,
                        shapeType = ShapeType.SHIELD,
                        fillColor = 0x33A855F7,
                        shapeStrokeColor = 0xFFC084FC,
                        shapeStrokeWidth = 3f,
                        widthScale = 1.1f,
                        heightScale = 1.2f,
                        xPercent = 0.5f,
                        yPercent = 0.48f,
                        zIndex = 0
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Title",
                        type = LayerType.TEXT,
                        text = "GRAND TOURNAMENT",
                        textColor = 0xFF00E5FF,
                        fontSize = 32f,
                        fontStyleName = "BoldHeading",
                        xPercent = 0.5f,
                        yPercent = 0.35f,
                        isBold = true,
                        hasStroke = true,
                        strokeColor = 0xFF000000,
                        strokeWidth = 4f,
                        hasShadow = true,
                        shadowColor = 0xFF000000,
                        zIndex = 1
                    ),
                    DesignLayer(
                        id = UUID.randomUUID().toString(),
                        name = "Prize Pool",
                        type = LayerType.TEXT,
                        text = "PRIZE POOL $10,000",
                        textColor = 0xFFFFD700,
                        fontSize = 24f,
                        xPercent = 0.5f,
                        yPercent = 0.56f,
                        isBold = true,
                        hasBackgroundBadge = true,
                        badgeColor = 0xFF7E22CE,
                        badgeCornerRadius = 12f,
                        badgePadding = 14f,
                        zIndex = 2
                    )
                )
            )
        )
    }
}
