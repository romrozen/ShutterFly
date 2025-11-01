package com.roman.shutter.feature.canvas.data

import com.roman.shutter.R
import com.roman.shutter.feature.canvas.ui.GalleryItem
import javax.inject.Inject
import javax.inject.Singleton

/** Supplies gallery data and generates IDs for newly placed items. */
interface CanvasRepository {
	fun initialGallery(): List<GalleryItem>
	fun generatePlacedId(): String
}

/** Default, in-memory implementation backed by drawable resources. */
@Singleton
class DefaultCanvasRepository @Inject constructor() : CanvasRepository {
	override fun initialGallery(): List<GalleryItem> {
		val samples = listOf(
			R.drawable.i1,
			R.drawable.i2,
			R.drawable.i3,
			R.drawable.i4,
			R.drawable.i5,
			R.drawable.i6,
			R.drawable.i7,
			R.drawable.i8
		)
		return samples.mapIndexed { idx, res -> GalleryItem(id = "g$idx", imageResId = res) }
	}

	override fun generatePlacedId(): String = "plc-${System.nanoTime()}"
}


