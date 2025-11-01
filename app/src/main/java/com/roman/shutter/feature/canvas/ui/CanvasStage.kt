package com.roman.shutter.feature.canvas.ui

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt

/**
 * Renders all placed images inside the canvas and forwards transform gestures.
 *
 * The model stores centers in canvas coordinates; we convert to top-left for
 * positioning and use a per-item transformable state to stream pan/zoom deltas
 * back to the ViewModel.
 */
@Composable
fun CanvasStage(
	placedImages: List<PlacedImage>,
	thumbnailSize: Dp,
	onEvent: (CanvasUiEvent) -> Unit
) {
	val density = LocalDensity.current
	// Placed images ordered by zIndex
	placedImages.sortedBy { it.zIndex }.forEach { placed ->
		key(placed.id) {
			val itemSizePx = with(density) { thumbnailSize.toPx() * placed.scale }
			val half = itemSizePx / 2f
			val topLeft = Offset(placed.centerInCanvas.x - half, placed.centerInCanvas.y - half)
			val transformState = rememberTransformableState { zoomChange, panChange, _ ->
				onEvent(CanvasUiEvent.BeginManipulation(placed.id))
				onEvent(
					CanvasUiEvent.TransformPlaced(
						placedId = placed.id,
						translationDelta = panChange,
						scaleChange = zoomChange
					)
				)
			}

			Box(
				modifier = Modifier
					.offset() {
						IntOffset(
							x = topLeft.x.roundToInt(),
							y = topLeft.y.roundToInt()
						)
					}
					.height(thumbnailSize * placed.scale)
					.aspectRatio(1f)
					.transformable(transformState)
					.semantics { contentDescription = "placed" }
			) {
				PlacedImageTile(imageResId = placed.imageResId)
			}
		}
	}
}


