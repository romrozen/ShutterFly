package com.roman.shutter.feature.canvas.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Small one-time hint overlay that explains the long-press drag interaction. */
@Composable
fun CanvasWalkthrough(
	onDismiss: () -> Unit
) {
	AnimatedVisibility(
		visible = true,
		enter = fadeIn(),
		exit = fadeOut()
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f))
				.clickable(onClick = onDismiss)
				.semantics { contentDescription = "walkthrough" },
			contentAlignment = Alignment.BottomCenter
		) {
			Card(
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
				modifier = Modifier
					.fillMaxWidth()
					.padding(30.dp)
					.alpha(0.85f)
			) {
				Row(modifier = Modifier
					.padding(16.dp),
					verticalAlignment = Alignment.CenterVertically) {
					Icon(imageVector = Icons.Default.TouchApp, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
					Spacer(modifier = Modifier.size(12.dp))
					Column(modifier = Modifier.weight(1f)) {
						Text("Long-press and drag onto the canvas.", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
					}
					Button(onClick = onDismiss) { Text("Got it") }
				}
			}
		}
	}
}


