package com.roman.shutter.feature.canvas.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt

/** Semi-transparent preview that follows the finger while dragging from the
 *  carousel before drop. */
@Composable
fun DragOverlay(preview: DragPreview, thumbnailSize: Dp) {
	val density = LocalDensity.current
	val sizePx = with(density) { thumbnailSize.toPx() }
	val half = sizePx / 2f
	val topLeft = Offset(preview.positionInRoot.x - half, preview.positionInRoot.y - half)
	Box(
		modifier = Modifier
			.fillMaxSize()
			.semantics { contentDescription = "dragOverlay" }
	) {
		Box(
			modifier = Modifier
				.offset { IntOffset(topLeft.x.roundToInt(), topLeft.y.roundToInt()) }
				.height(thumbnailSize)
				.aspectRatio(1f)
		) {
			PlacedImageTile(imageResId = preview.imageResId, alpha = 0.8f)
		}
	}
}


