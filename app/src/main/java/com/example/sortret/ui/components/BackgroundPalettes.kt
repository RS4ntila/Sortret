package com.example.sortret.ui.components

import androidx.compose.ui.graphics.Color

data class BackgroundPalette(
    val name: String,
    val description: String,
    val gradientA: List<Color>,
    val gradientB: List<Color>,
    val blobColors: List<Pair<Color, Color>>,
    val previewColors: List<Color>,
    val blobStrength: Float = 1f,
    val softGlowStrength: Float = 1f,
    val experimental: Boolean = false
)

fun BackgroundPalette.primaryAccent(): Color = previewColors.getOrElse(2) { previewColors.last() }

fun BackgroundPalette.secondaryAccent(): Color = previewColors.getOrElse(3) { primaryAccent() }

fun BackgroundPalette.softAccent(): Color = previewColors.getOrElse(1) { primaryAccent() }

val backgroundPalettes = listOf(
    BackgroundPalette(
        name = "Рубиновый сон",
        description = "темный рубин, слива и теплый неоновый край",
        gradientA = listOf(Color(0xFF090307), Color(0xFF31101E), Color(0xFF5B102E)),
        gradientB = listOf(Color(0xFF13050B), Color(0xFF9B1D5B), Color(0xFFFF7A59)),
        blobColors = listOf(
            Color(0xFFFF2E63) to Color(0xFFFF7A59),
            Color(0xFF6A1B9A) to Color(0xFFFF4FB8),
            Color(0xFFFFA8C8) to Color(0xFFFFD166),
            Color(0xFFB0165A) to Color(0xFFFF6F91),
            Color(0xFF7C4DFF) to Color(0xFFFF2E63),
            Color(0xFFFF7A59) to Color(0xFFFFC2A1)
        ),
        previewColors = listOf(Color(0xFF090307), Color(0xFF5B102E), Color(0xFFFF2E63), Color(0xFFFF7A59)),
        blobStrength = 0.82f,
        softGlowStrength = 1.05f
    ),
    BackgroundPalette(
        name = "Синий ирис",
        description = "глубокий индиго, ледяной голубой и мягкая сирень",
        gradientA = listOf(Color(0xFF010410), Color(0xFF07113A), Color(0xFF143B75)),
        gradientB = listOf(Color(0xFF050512), Color(0xFF3F41D3), Color(0xFF8DCAD3)),
        blobColors = listOf(
            Color(0xFF2E51D3) to Color(0xFF8DCAD3),
            Color(0xFF5E2FD3) to Color(0xFF3ED8D3),
            Color(0xFF008ED3) to Color(0xFFC0A2E5),
            Color(0xFF906CD8) to Color(0xFF0096D3),
            Color(0xFF1F60CC) to Color(0xFF8DCAD3),
            Color(0xFF4F26D3) to Color(0xFF3ED8D3)
        ),
        previewColors = listOf(Color(0xFF010410), Color(0xFF07113A), Color(0xFF3F41D3), Color(0xFF8DCAD3)),
        blobStrength = 0.80f,
        softGlowStrength = 1.02f
    ),
    BackgroundPalette(
        name = "Космический гранат",
        description = "темный космос, гранатовый неон и ультрафиолет",
        gradientA = listOf(Color(0xFF06020B), Color(0xFF230B3F), Color(0xFF680F45)),
        gradientB = listOf(Color(0xFF100617), Color(0xFFFF2E63), Color(0xFF7C4DFF)),
        blobColors = listOf(
            Color(0xFFFF2E63) to Color(0xFF7C4DFF),
            Color(0xFFB0165A) to Color(0xFFFF4FB8),
            Color(0xFF6A3DFF) to Color(0xFFFF2E63),
            Color(0xFFFF7A90) to Color(0xFFB388FF),
            Color(0xFF3D0C5A) to Color(0xFFFF4FB8),
            Color(0xFF00B8FF) to Color(0xFF7C4DFF)
        ),
        previewColors = listOf(Color(0xFF06020B), Color(0xFF230B3F), Color(0xFFFF2E63), Color(0xFF7C4DFF)),
        blobStrength = 0.86f,
        softGlowStrength = 1.22f,
        experimental = true
    ),
    BackgroundPalette(
        name = "Фуксия хром",
        description = "ягодный свет, лазурь и горячий розовый",
        gradientA = listOf(Color(0xFF160624), Color(0xFF421083), Color(0xFF7422D8)),
        gradientB = listOf(Color(0xFF24102E), Color(0xFFBE2EE7), Color(0xFFFF3FA4)),
        blobColors = listOf(
            Color(0xFF4A148C) to Color(0xFF7A22F2),
            Color(0xFF8E24AA) to Color(0xFFFF3FA4),
            Color(0xFFC2185B) to Color(0xFFFF7AC8),
            Color(0xFF651FFF) to Color(0xFF00B0FF),
            Color(0xFFB832D8) to Color(0xFFFF4081),
            Color(0xFF1E88E5) to Color(0xFFB388FF)
        ),
        previewColors = listOf(Color(0xFF160624), Color(0xFF8E24AA), Color(0xFFFF3FA4), Color(0xFF00B0FF)),
        softGlowStrength = 0.92f
    ),
    BackgroundPalette(
        name = "Мята и коралл",
        description = "зеленое стекло, теплые вспышки и чистый циан",
        gradientA = listOf(Color(0xFF031C1C), Color(0xFF096B62), Color(0xFF0FC6A4)),
        gradientB = listOf(Color(0xFF14202A), Color(0xFFFF5A88), Color(0xFFFFC857)),
        blobColors = listOf(
            Color(0xFF00BFA5) to Color(0xFF8CFFDA),
            Color(0xFFFF4F79) to Color(0xFFFFC857),
            Color(0xFF13C4A3) to Color(0xFF00E5FF),
            Color(0xFFFF7A59) to Color(0xFFFFD166),
            Color(0xFF2DE2E6) to Color(0xFFFF5A88),
            Color(0xFF7CFF6B) to Color(0xFF00BFA5)
        ),
        previewColors = listOf(Color(0xFF031C1C), Color(0xFF00BFA5), Color(0xFFFF5A88), Color(0xFFFFC857)),
        blobStrength = 0.88f,
        softGlowStrength = 0.82f
    ),
    BackgroundPalette(
        name = "Черная сакура",
        description = "сливовая глубина, персик и мягкий лед",
        gradientA = listOf(Color(0xFF0B0710), Color(0xFF3A0B2E), Color(0xFFB0165A)),
        gradientB = listOf(Color(0xFF170C1B), Color(0xFFFF6F91), Color(0xFFFFC2A1)),
        blobColors = listOf(
            Color(0xFF4B123E) to Color(0xFFFF5C93),
            Color(0xFFFF7A90) to Color(0xFFFFC2A1),
            Color(0xFF9B5CFF) to Color(0xFFFF4FB8),
            Color(0xFF59D2FE) to Color(0xFFFFA8C8),
            Color(0xFFC9184A) to Color(0xFFFFD6A5),
            Color(0xFFB8F7FF) to Color(0xFFFF6F91)
        ),
        previewColors = listOf(Color(0xFF0B0710), Color(0xFFB0165A), Color(0xFFFF6F91), Color(0xFFFFC2A1)),
        blobStrength = 0.90f,
        softGlowStrength = 0.95f
    ),
    BackgroundPalette(
        name = "Электро лайм",
        description = "кислотный лайм, бирюза и розовый неон",
        gradientA = listOf(Color(0xFF07100E), Color(0xFF144E42), Color(0xFF86CC20)),
        gradientB = listOf(Color(0xFF121016), Color(0xFF00B09C), Color(0xFFD61E7A)),
        blobColors = listOf(
            Color(0xFF86CC20) to Color(0xFF00C7AC),
            Color(0xFFD61E7A) to Color(0xFFFFE66D),
            Color(0xFF00B09C) to Color(0xFF86CC20),
            Color(0xFFFFE66D) to Color(0xFFD61E7A),
            Color(0xFF22C379) to Color(0xFF068CBF),
            Color(0xFFD61E7A) to Color(0xFF86CC20)
        ),
        previewColors = listOf(Color(0xFF07100E), Color(0xFF86CC20), Color(0xFF00B09C), Color(0xFFD61E7A)),
        blobStrength = 0.76f,
        softGlowStrength = 0.72f
    ),
    BackgroundPalette(
        name = "Полярный неон",
        description = "аврора, холодная мята и ягодный блик",
        gradientA = listOf(Color(0xFF061313), Color(0xFF066B72), Color(0xFF824BD6)),
        gradientB = listOf(Color(0xFF140A1D), Color(0xFF1BB081), Color(0xFFD92E94)),
        blobColors = listOf(
            Color(0xFF1BB081) to Color(0xFF98DFE6),
            Color(0xFF653ECE) to Color(0xFFD92E94),
            Color(0xFF009ECC) to Color(0xFF1BB081),
            Color(0xFFD92E94) to Color(0xFFFFD166),
            Color(0xFF00D185) to Color(0xFF653ECE),
            Color(0xFF98DFE6) to Color(0xFFD92E94)
        ),
        previewColors = listOf(Color(0xFF061313), Color(0xFF1BB081), Color(0xFF824BD6), Color(0xFFD92E94)),
        blobStrength = 0.84f,
        softGlowStrength = 0.95f
    ),
    BackgroundPalette(
        name = "Голографит",
        description = "экспериментальный графит, опал и стеклянная бирюза",
        gradientA = listOf(Color(0xFF050609), Color(0xFF14122B), Color(0xFF034B52)),
        gradientB = listOf(Color(0xFF0A0712), Color(0xFF6A3DFF), Color(0xFF00E5D4)),
        blobColors = listOf(
            Color(0xFF00F5D4) to Color(0xFF8A5CFF),
            Color(0xFFFF4FD8) to Color(0xFF00B8FF),
            Color(0xFFB8F7FF) to Color(0xFF6246EA),
            Color(0xFF13F2B4) to Color(0xFFFFD166),
            Color(0xFF755CFF) to Color(0xFFFF4FB8),
            Color(0xFF00D9C0) to Color(0xFFE0C3FC)
        ),
        previewColors = listOf(Color(0xFF050609), Color(0xFF6A3DFF), Color(0xFF00E5D4), Color(0xFFFF4FD8)),
        blobStrength = 0.78f,
        softGlowStrength = 1.18f,
        experimental = true
    ),
    BackgroundPalette(
        name = "Туманная орхидея",
        description = "экспериментальная дымка, розовый лед и синий шелк",
        gradientA = listOf(Color(0xFF07040D), Color(0xFF2B0F34), Color(0xFF10285A)),
        gradientB = listOf(Color(0xFF110816), Color(0xFFFF4FA3), Color(0xFF5EE7FF)),
        blobColors = listOf(
            Color(0xFFFF4FA3) to Color(0xFFB388FF),
            Color(0xFF5EE7FF) to Color(0xFFFFA8C8),
            Color(0xFF7B2FF7) to Color(0xFFF107A3),
            Color(0xFF00C2FF) to Color(0xFFB8F7FF),
            Color(0xFFFF6F91) to Color(0xFF6A3DFF),
            Color(0xFFB8F7FF) to Color(0xFFFF4FA3)
        ),
        previewColors = listOf(Color(0xFF07040D), Color(0xFF2B0F34), Color(0xFFFF4FA3), Color(0xFF5EE7FF)),
        blobStrength = 0.82f,
        softGlowStrength = 1.25f,
        experimental = true
    ),
    BackgroundPalette(
        name = "Криптон лагуна",
        description = "экспериментальная темная вода, лайм и ультрамарин",
        gradientA = listOf(Color(0xFF020B0E), Color(0xFF063F42), Color(0xFF102464)),
        gradientB = listOf(Color(0xFF071015), Color(0xFF78FFCA), Color(0xFF4C6FFF)),
        blobColors = listOf(
            Color(0xFF78FFCA) to Color(0xFF00B8FF),
            Color(0xFF4C6FFF) to Color(0xFF00F5D4),
            Color(0xFFB6FF2E) to Color(0xFF13C4A3),
            Color(0xFF5EE7FF) to Color(0xFF755CFF),
            Color(0xFF00FFA3) to Color(0xFFFFF275),
            Color(0xFF00D9C0) to Color(0xFF4C6FFF)
        ),
        previewColors = listOf(Color(0xFF020B0E), Color(0xFF063F42), Color(0xFF78FFCA), Color(0xFF4C6FFF)),
        blobStrength = 0.72f,
        softGlowStrength = 1.12f,
        experimental = true
    ),
    BackgroundPalette(
        name = "Янтарный мед",
        description = "теплое золото, карамель и мягкий утренний свет",
        gradientA = listOf(Color(0xFF120805), Color(0xFF4A2108), Color(0xFFC06A16)),
        gradientB = listOf(Color(0xFF1A0D05), Color(0xFFFFA726), Color(0xFFFFD166)),
        blobColors = listOf(
            Color(0xFFFFA726) to Color(0xFFFFD166),
            Color(0xFFFF7A59) to Color(0xFFFFC857),
            Color(0xFFC06A16) to Color(0xFFFFE0A3),
            Color(0xFFFFC857) to Color(0xFFFF5A5F),
            Color(0xFF8D3B00) to Color(0xFFFFA726),
            Color(0xFFFFD166) to Color(0xFFFFF3B0)
        ),
        previewColors = listOf(Color(0xFF120805), Color(0xFFC06A16), Color(0xFFFFA726), Color(0xFFFFD166)),
        blobStrength = 0.74f,
        softGlowStrength = 1.10f
    ),
    BackgroundPalette(
        name = "Ледяная мята",
        description = "холодное стекло, мята и почти белое сияние",
        gradientA = listOf(Color(0xFF031115), Color(0xFF0B3F47), Color(0xFF5CC2D9)),
        gradientB = listOf(Color(0xFF08131A), Color(0xFF4DD9A5), Color(0xFFB0DFD6)),
        blobColors = listOf(
            Color(0xFF5CC2D9) to Color(0xFFB0DFD6),
            Color(0xFF4DD9A5) to Color(0xFF00B09C),
            Color(0xFF92DFE6) to Color(0xFF4CBFDB),
            Color(0xFFB0DFD6) to Color(0xFF4DD9A5),
            Color(0xFF009ECC) to Color(0xFF92DFE6),
            Color(0xFF26C3C6) to Color(0xFFB0DFD6)
        ),
        previewColors = listOf(Color(0xFF031115), Color(0xFF0B3F47), Color(0xFF4DD9A5), Color(0xFFB0DFD6)),
        blobStrength = 0.68f,
        softGlowStrength = 1.16f
    ),
    BackgroundPalette(
        name = "Лавандовый рассвет",
        description = "сиреневый рассвет, персик и спокойный голубой",
        gradientA = listOf(Color(0xFF080819), Color(0xFF35205D), Color(0xFFFFA8C8)),
        gradientB = listOf(Color(0xFF101124), Color(0xFFE0C3FC), Color(0xFF8EC5FC)),
        blobColors = listOf(
            Color(0xFFE0C3FC) to Color(0xFF8EC5FC),
            Color(0xFFFFA8C8) to Color(0xFFFFD6A5),
            Color(0xFFB388FF) to Color(0xFFB8F7FF),
            Color(0xFFFFC2A1) to Color(0xFFE0C3FC),
            Color(0xFF5D5FEF) to Color(0xFF8EC5FC),
            Color(0xFFFFD6A5) to Color(0xFFB388FF)
        ),
        previewColors = listOf(Color(0xFF080819), Color(0xFF35205D), Color(0xFFE0C3FC), Color(0xFFFFA8C8)),
        blobStrength = 0.78f,
        softGlowStrength = 1.08f
    )
)
