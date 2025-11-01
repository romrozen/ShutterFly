package com.roman.shutter.feature.canvas.data.di

import com.roman.shutter.feature.canvas.data.CanvasRepository
import com.roman.shutter.feature.canvas.data.DefaultCanvasRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Hilt bindings for the canvas data layer. */
@Module
@InstallIn(SingletonComponent::class)
abstract class CanvasModule {
	@Binds
	@Singleton
	abstract fun bindCanvasRepository(impl: DefaultCanvasRepository): CanvasRepository
}


