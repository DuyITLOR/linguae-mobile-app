package com.penguin.linguae.feature.home.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection


class CurvedBottomShape(
    private val curveDepth: Float = 0.85f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            quadraticTo(
                size.width / 2, size.height * curveDepth,
                0f, size.height
            )
            close()
        }
        return Outline.Generic(path)
    }
}