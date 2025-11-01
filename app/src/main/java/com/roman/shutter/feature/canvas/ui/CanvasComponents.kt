package com.roman.shutter.feature.canvas.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.alpha
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Rounded image tile used for both placed images and the drag preview. */
@Composable
fun PlacedImageTile(imageResId: Int, alpha: Float = 1f) {
	Surface(
		modifier = Modifier.fillMaxSize(),
		color = Color.Transparent,
		shape = RoundedCornerShape(12.dp),
		shadowElevation = 3.dp
	) {
		Image(
			painter = painterResource(id = imageResId),
			contentDescription = null,
			contentScale = ContentScale.Fit,
			modifier = Modifier
				.fillMaxSize()
				.alpha(alpha)
		)
	}
}


