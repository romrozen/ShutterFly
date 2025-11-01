package com.roman.shutter.feature.canvas.ui

import androidx.lifecycle.ViewModel
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.roman.shutter.feature.canvas.data.CanvasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel for the canvas feature.
 *
 * Exposes immutable [CanvasUiState] and accepts [CanvasUiEvent] to mutate state.
 * Gesture math is kept simple and clamped to the canvas bounds when known.
 */
@HiltViewModel
class CanvasViewModel @Inject constructor(
	private val repository: CanvasRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(
		CanvasUiState(
			gallery = repository.initialGallery()
		)
	)
	val uiState: StateFlow<CanvasUiState> = _uiState

	private var nextZ: Float = 1f
	private var lastBumpedId: String? = null

	/** Dispatch UI events coming from the composables. */
	fun onEvent(event: CanvasUiEvent) {
		when (event) {
			is CanvasUiEvent.SetCanvasBounds -> setCanvasBounds(event.canvasBoundsInRoot)
			is CanvasUiEvent.StartDrag -> startDrag(event.sourceId, event.startPositionInRoot, event.sourceIndex, event.imageResId)
			is CanvasUiEvent.UpdateDrag -> updateDrag(event.positionInRoot)
			is CanvasUiEvent.CancelDrag -> cancelDrag()
			is CanvasUiEvent.Drop -> drop()
			is CanvasUiEvent.SelectPlaced -> selectPlaced(event.placedId)
			is CanvasUiEvent.BeginManipulation -> bringToFront(event.placedId)
			is CanvasUiEvent.TransformPlaced -> transformPlaced(event.placedId, event.translationDelta, event.scaleChange)
			is CanvasUiEvent.DismissWalkthrough -> dismissWalkthrough()
		}
	}

	private fun setCanvasBounds(canvasBoundsInRoot: Rect) {
		_uiState.update { it.copy(canvasBoundsInRoot = canvasBoundsInRoot) }
	}

	private fun startDrag(sourceId: String, startPositionInRoot: Offset, sourceIndex: Int, imageResId: Int) {
		_uiState.update { state ->
			state.copy(
				dragPreview = DragPreview(
					sourceId = sourceId,
					positionInRoot = startPositionInRoot,
					sourceIndex = sourceIndex,
					imageResId = imageResId
				)
			)
		}
	}

	private fun updateDrag(positionInRoot: Offset) {
		_uiState.update { state ->
			state.dragPreview?.let { state.copy(dragPreview = it.copy(positionInRoot = positionInRoot)) } ?: state
		}
	}

	private fun cancelDrag() {
		_uiState.update { it.copy(dragPreview = null) }
	}

	private fun drop() {
		val state = _uiState.value
		val preview = state.dragPreview ?: return
		val bounds = state.canvasBoundsInRoot
		if (bounds == null || !bounds.contains(preview.positionInRoot)) {
			cancelDrag()
			return
		}
		val newPlaced = PlacedImage(
			id = repository.generatePlacedId(),
			sourceId = preview.sourceId,
			centerInCanvas = preview.positionInRoot - bounds.topLeft,
			scale = 1f,
			zIndex = nextZ,
			imageResId = preview.imageResId
		)
		nextZ += 1f
		_uiState.update { s ->
			val updatedGallery = s.gallery.filterNot { it.id == preview.sourceId }
			s.copy(
				placedImages = s.placedImages + newPlaced,
				gallery = updatedGallery,
				dragPreview = null,
				selectedId = newPlaced.id
			)
		}
	}

	private fun selectPlaced(placedId: String) {
		_uiState.update { current ->
			val updated = current.placedImages.map { if (it.id == placedId) it.copy(zIndex = nextZ) else it }
			nextZ += 1f
			current.copy(placedImages = updated, selectedId = placedId)
		}
	}

	private fun bringToFront(placedId: String) {
		if (lastBumpedId == placedId) return
		_uiState.update { current ->
			val updated = current.placedImages.map { if (it.id == placedId) it.copy(zIndex = nextZ) else it }
			lastBumpedId = placedId
			nextZ += 1f
			current.copy(placedImages = updated, selectedId = placedId)
		}
	}

	private fun dismissWalkthrough() {
		_uiState.update { it.copy(showWalkthrough = false) }
	}

	/** Apply pan/zoom to a placed image; clamps center within the canvas. */
	private fun transformPlaced(placedId: String, translationDelta: Offset, scaleChange: Float) {
		val bounds = _uiState.value.canvasBoundsInRoot
		_uiState.update { current ->
			var zToUse = nextZ
			val updated = current.placedImages.map { placed ->
				if (placed.id != placedId) return@map placed
				val newScale = (placed.scale * scaleChange).coerceIn(0.4f, 4f)
				val unclampedCenter = placed.centerInCanvas + translationDelta
				val clampedCenter = bounds?.let { b ->
					val width = b.width
					val height = b.height
					Offset(
						x = unclampedCenter.x.coerceIn(0f, width),
						y = unclampedCenter.y.coerceIn(0f, height)
					)
				} ?: unclampedCenter
				val bumped = if (lastBumpedId != placedId) {
					val copy = placed.copy(zIndex = zToUse)
					zToUse += 1f
					copy
				} else placed
				bumped.copy(centerInCanvas = clampedCenter, scale = newScale)
			}
			if (lastBumpedId != placedId) {
				lastBumpedId = placedId
				nextZ = zToUse
			}
			current.copy(placedImages = updated, selectedId = placedId)
		}
	}
}


