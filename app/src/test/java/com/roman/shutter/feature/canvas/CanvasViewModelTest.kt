package com.roman.shutter.feature.canvas

import com.roman.shutter.feature.canvas.ui.*
import com.roman.shutter.feature.canvas.data.CanvasRepository
import org.junit.Assert.*
import org.junit.Test
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

private class FakeCanvasRepository : CanvasRepository {
    private var counter = 0
    override fun initialGallery(): List<GalleryItem> {
        return listOf(
            GalleryItem(id = "g0", imageResId = 1),
            GalleryItem(id = "g1", imageResId = 2),
            GalleryItem(id = "g2", imageResId = 3)
        )
    }

    override fun generatePlacedId(): String {
        counter += 1
        return "plc-$counter"
    }
}

class CanvasViewModelTest {

    private fun createVm(): CanvasViewModel = CanvasViewModel(repository = FakeCanvasRepository())

    @Test
    fun initialState_hasGalleryFromRepository() {
        val vm = createVm()
        val state = vm.uiState.value
        assertEquals(3, state.gallery.size)
        assertTrue(state.placedImages.isEmpty())
        assertNull(state.dragPreview)
    }

    @Test
    fun setBounds_updatesState() {
        val vm = createVm()
        val rect = Rect(0f, 0f, 100f, 100f)
        vm.onEvent(CanvasUiEvent.SetCanvasBounds(canvasBoundsInRoot = rect))
        assertEquals(rect, vm.uiState.value.canvasBoundsInRoot)
    }

    @Test
    fun startDrag_and_dropInsideCanvas_placesImage_andRemovesFromGallery() {
        val vm = createVm()
        val galleryItem = vm.uiState.value.gallery.first()
        val rect = Rect(0f, 0f, 100f, 100f)
        vm.onEvent(CanvasUiEvent.SetCanvasBounds(canvasBoundsInRoot = rect))
        vm.onEvent(
            CanvasUiEvent.StartDrag(
                sourceId = galleryItem.id,
                startPositionInRoot = Offset(30f, 40f),
                sourceIndex = 0,
                imageResId = galleryItem.imageResId
            )
        )
        vm.onEvent(CanvasUiEvent.Drop)

        val state = vm.uiState.value
        assertNull(state.dragPreview)
        assertEquals(1, state.placedImages.size)
        assertEquals(2, state.gallery.size) // removed the dropped item
        val placed = state.placedImages.first()
        assertEquals(Offset(30f, 40f), placed.centerInCanvas)
        assertEquals(galleryItem.imageResId, placed.imageResId)
        assertEquals(1f, placed.scale, 0.0001f)
    }

    @Test
    fun transformPlaced_updatesCenterAndScale_andClampsInsideBounds() {
        val vm = createVm()
        val first = vm.uiState.value.gallery.first()
        val rect = Rect(0f, 0f, 100f, 100f)
        vm.onEvent(CanvasUiEvent.SetCanvasBounds(canvasBoundsInRoot = rect))
        vm.onEvent(
            CanvasUiEvent.StartDrag(
                sourceId = first.id,
                startPositionInRoot = Offset(10f, 10f),
                sourceIndex = 0,
                imageResId = first.imageResId
            )
        )
        vm.onEvent(CanvasUiEvent.Drop)

        val placedId = vm.uiState.value.placedImages.first().id
        vm.onEvent(
            CanvasUiEvent.TransformPlaced(
                placedId = placedId,
                translationDelta = Offset(500f, 500f),
                scaleChange = 2f
            )
        )
        val updated = vm.uiState.value.placedImages.first()
        // Clamped to canvas size
        assertEquals(100f, updated.centerInCanvas.x, 0.0001f)
        assertEquals(100f, updated.centerInCanvas.y, 0.0001f)
        assertEquals(2f, updated.scale, 0.0001f)
    }

    @Test
    fun beginManipulation_bumpsZIndex() {
        val vm = createVm()
        val gallery = vm.uiState.value.gallery
        val rect = Rect(0f, 0f, 100f, 100f)
        vm.onEvent(CanvasUiEvent.SetCanvasBounds(canvasBoundsInRoot = rect))
        // Drop two items
        vm.onEvent(CanvasUiEvent.StartDrag(gallery[0].id, Offset(20f, 20f), 0, gallery[0].imageResId))
        vm.onEvent(CanvasUiEvent.Drop)
        vm.onEvent(CanvasUiEvent.StartDrag(gallery[1].id, Offset(30f, 30f), 1, gallery[1].imageResId))
        vm.onEvent(CanvasUiEvent.Drop)

        val s = vm.uiState.value
        val first = s.placedImages[0]
        val second = s.placedImages[1]
        // First is behind initially
        assertTrue(first.zIndex < second.zIndex)

        vm.onEvent(CanvasUiEvent.BeginManipulation(first.id))

        val after = vm.uiState.value
        val bumpedFirst = after.placedImages.first { it.id == first.id }
        assertTrue(bumpedFirst.zIndex > second.zIndex)
    }

    @Test
    fun dismissWalkthrough_setsFlagFalse() {
        val vm = createVm()
        assertTrue(vm.uiState.value.showWalkthrough)
        vm.onEvent(CanvasUiEvent.DismissWalkthrough)
        assertFalse(vm.uiState.value.showWalkthrough)
    }
}


