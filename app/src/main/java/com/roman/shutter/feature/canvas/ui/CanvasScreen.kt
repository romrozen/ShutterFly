package com.roman.shutter.feature.canvas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roman.shutter.R
import com.roman.shutter.ui.theme.ShutterFlyTheme
import androidx.compose.ui.tooling.preview.Preview

/** Root screen for the canvas feature. Wires state from the ViewModel into
 *  smaller stateless composables. */
@Composable
fun CanvasScreen(
	viewModel: CanvasViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	CanvasContent(
		uiState = uiState,
		onEvent = viewModel::onEvent
	)
}

/** Pure UI for the screen (useful for previews/tests). */
@Composable
private fun CanvasContent(
	uiState: CanvasUiState,
	onEvent: (CanvasUiEvent) -> Unit,
	modifier: Modifier = Modifier
) {
	val thumbnailSize: Dp = 120.dp

	Box(modifier = modifier.fillMaxSize()) {
		Column(
			modifier = Modifier.fillMaxSize(),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			// Center square canvas
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f)
					.padding(horizontal = 16.dp, vertical = 16.dp),
				contentAlignment = Alignment.Center
			) {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.aspectRatio(1f)
						.clipToBounds()
						.background(MaterialTheme.colorScheme.surfaceVariant)
						.semantics { contentDescription = "canvas" }
						// Record canvas bounds in root coordinates so gestures can be
						// clamped and drops can be validated against the canvas area.
						.onGloballyPositioned { coordinates ->
							onEvent(
								CanvasUiEvent.SetCanvasBounds(
									canvasBoundsInRoot = coordinates.boundsInRoot()
								)
							)
						}
				) {
					CanvasStage(
						placedImages = uiState.placedImages,
						thumbnailSize = thumbnailSize,
						onEvent = onEvent
					)
				}
			}

			// Bottom carousel
			Carousel(
				galleryItems = uiState.gallery,
				thumbnailSize = thumbnailSize,
				draggingItemId = uiState.dragPreview?.sourceId,
				onEvent = onEvent
			)
		}

		// Drag preview overlay
		uiState.dragPreview?.let { preview ->
			DragOverlay(preview = preview, thumbnailSize = thumbnailSize)
		}

		// One-time walkthrough hint
		if (uiState.showWalkthrough) {
			CanvasWalkthrough(onDismiss = { onEvent(CanvasUiEvent.DismissWalkthrough) })
		}
	}
}

/** Stateless wrapper used by previews and tests to avoid Hilt. */
@Composable
fun CanvasScreenStateless(
	uiState: CanvasUiState,
	onEvent: (CanvasUiEvent) -> Unit = {}
) {
	CanvasContent(uiState = uiState, onEvent = onEvent)
}

/** Minimal preview state with a small gallery. */
fun sampleCanvasUiState(): CanvasUiState {
	val samples = listOf(
		R.drawable.i1,
		R.drawable.i2,
		R.drawable.i3,
		R.drawable.i4
	)
	val gallery = samples.mapIndexed { idx, res -> GalleryItem(id = "g$idx", imageResId = res) }
	return CanvasUiState(gallery = gallery)
}

@Preview(showBackground = true)
@Composable
private fun CanvasScreen_Preview() {
	ShutterFlyTheme {
		CanvasScreenStateless(uiState = sampleCanvasUiState())
	}
}
