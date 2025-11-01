package com.roman.shutter.feature.canvas.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Bottom carousel of images that can be long-pressed and dragged to the canvas. */
@Composable
fun Carousel(
	galleryItems: List<GalleryItem>,
	thumbnailSize: Dp,
	draggingItemId: String?,
	onEvent: (CanvasUiEvent) -> Unit
) {
	  val itemPadding = 12.dp

    LazyRow(
		modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 16.dp)
			.semantics { contentDescription = "carousel" },
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(itemPadding)
	) {
		itemsIndexed(galleryItems, key = { _, it -> it.id }) { index, galleryItem ->
			DraggableThumbnail(
				index = index,
				galleryItem = galleryItem,
				thumbnailSize = thumbnailSize,
				draggingItemId = draggingItemId,
				onEvent = onEvent
			)
		}
	}
}

/** A single thumbnail with long-press-to-drag semantics. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.DraggableThumbnail(
	index: Int,
	galleryItem: GalleryItem,
	thumbnailSize: Dp,
	draggingItemId: String?,
	onEvent: (CanvasUiEvent) -> Unit
) {
	var itemRootOffset by remember { mutableStateOf(Offset.Zero) }
	val haptics: HapticFeedback = LocalHapticFeedback.current
	val context = LocalContext.current
	val target = if (draggingItemId == galleryItem.id) 0.dp else thumbnailSize
	val animatedSize by animateDpAsState(targetValue = target, animationSpec = tween(180), label = "thumbSize")
	Card(
		modifier = Modifier
			.height(animatedSize)
			.aspectRatio(1f)
			.onGloballyPositioned { coords -> itemRootOffset = coords.positionInRoot() }
			.semantics { contentDescription = "thumb" },
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(containerColor = Color.Transparent)
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.clip(RoundedCornerShape(12.dp))
		) {
			Image(
				painter = painterResource(id = galleryItem.imageResId),
				contentDescription = null,
				contentScale = ContentScale.Crop,
				modifier = Modifier.fillMaxSize()
			)
			Box(
				Modifier
					.matchParentSize()
					.background(Color.Transparent)
					.pointerInput(galleryItem.id) {
						detectDragGesturesAfterLongPress(
							onDragStart = { localPos ->
								val start = itemRootOffset + localPos
							haptics.performHapticFeedback(HapticFeedbackType.LongPress)
							triggerHeavyHaptic(context)
								onEvent(
									CanvasUiEvent.StartDrag(
										sourceId = galleryItem.id,
										startPositionInRoot = start,
										sourceIndex = index,
										imageResId = galleryItem.imageResId
									)
								)
							},
							onDrag = { change, _ ->
								val pos = itemRootOffset + change.position
								onEvent(CanvasUiEvent.UpdateDrag(positionInRoot = pos))
								change.consume()
							},
							onDragCancel = { onEvent(CanvasUiEvent.CancelDrag) },
							onDragEnd = { onEvent(CanvasUiEvent.Drop) }
						)
					}
			)
		}
	}
}

private fun triggerHeavyHaptic(context: Context) {
	val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
		val vm = context.getSystemService(VibratorManager::class.java)
		vm?.defaultVibrator
	} else {
		@Suppress("DEPRECATION")
		context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
	}
	vibrator?.let { v ->
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			v.vibrate(VibrationEffect.createOneShot(40L, VibrationEffect.DEFAULT_AMPLITUDE))
		} else {
			@Suppress("DEPRECATION")
			v.vibrate(40L)
		}
	}
}


