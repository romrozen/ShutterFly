package com.roman.shutter.feature.canvas.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

/** Item selectable in the bottom carousel. */
@Immutable
data class GalleryItem(
	val id: String,
	@DrawableRes val imageResId: Int
)

/** Image placed on the canvas. [centerInCanvas] is a canvas-local center point. */
@Immutable
data class PlacedImage(
	val id: String,
	val sourceId: String,
	val centerInCanvas: Offset,
	val scale: Float,
	val zIndex: Float,
	@DrawableRes val imageResId: Int
)

/** Active drag state shown as a floating preview while dragging from the carousel. */
@Immutable
data class DragPreview(
	val sourceId: String,
	val positionInRoot: Offset,
	val sourceIndex: Int,
	@DrawableRes val imageResId: Int
)

/** Immutable UI state for the canvas feature. */
@Immutable
data class CanvasUiState(
	val gallery: List<GalleryItem> = emptyList(),
	val placedImages: List<PlacedImage> = emptyList(),
	val selectedId: String? = null,
	val dragPreview: DragPreview? = null,
    val canvasBoundsInRoot: Rect? = null,
    val showWalkthrough: Boolean = true
)

/** UI events that describe user intent; the ViewModel interprets them. */
sealed interface CanvasUiEvent {
	data class SetCanvasBounds(val canvasBoundsInRoot: Rect) : CanvasUiEvent
	data class StartDrag(
		val sourceId: String,
		val startPositionInRoot: Offset,
		val sourceIndex: Int,
		@DrawableRes val imageResId: Int
	) : CanvasUiEvent
	data class UpdateDrag(val positionInRoot: Offset) : CanvasUiEvent
	object CancelDrag : CanvasUiEvent
	object Drop : CanvasUiEvent

	data class SelectPlaced(val placedId: String) : CanvasUiEvent
	data class BeginManipulation(val placedId: String) : CanvasUiEvent
	data class TransformPlaced(
		val placedId: String,
		val translationDelta: Offset,
		val scaleChange: Float
	) : CanvasUiEvent

    object DismissWalkthrough : CanvasUiEvent
}


