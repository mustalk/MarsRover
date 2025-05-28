package com.mustalk.seat.marsrover.presentation.ui.theme

import androidx.compose.ui.graphics.Color

// Mars-themed Primary Colors
val MarsRed = Color(0xFFD73502) // Primary Mars rust/orange
val MarsRedLight = Color(0xFFFF6434) // Lighter variant
val MarsRedDark = Color(0xFFA02600) // Darker variant

// Mars Surface & Terrain
val MarsDust = Color(0xFFE09F7D) // Light Mars dust
val MarsSurface = Color(0xFF8D5524) // Mars terrain base
val MarsRock = Color(0xFF6B4226) // Mars rock formations

// Space & Sky Colors
val SpaceBlue = Color(0xFF1B263B) // Deep space blue
val SpaceBlueDark = Color(0xFF0F1A2E) // Darker space
val StarWhite = Color(0xFFE0E1DD) // Star/moonlight white
val CosmicPurple = Color(0xFF415A77) // Cosmic purple-gray

// Utility Colors
val Success = Color(0xFF4CAF50) // Mission success
val Warning = Color(0xFFFF9800) // Mission warnings
val Error = Color(0xFFE53935) // Mission errors
val Info = Color(0xFF2196F3) // Information

// Light Theme Colors
val LightPrimary = MarsRed
val LightOnPrimary = Color.White
val LightPrimaryContainer = MarsDust
val LightOnPrimaryContainer = MarsRock

val LightSecondary = SpaceBlue
val LightOnSecondary = Color.White
val LightSecondaryContainer = CosmicPurple
val LightOnSecondaryContainer = Color.White

val LightBackground = Color(0xFFFFFBFE) // Clean white background
val LightOnBackground = Color(0xFF1C1B1F) // Dark text
val LightSurface = Color(0xFFFFFBFE) // Surface color
val LightOnSurface = Color(0xFF1C1B1F) // Text on surface

val LightSurfaceVariant = Color(0xFFF3E5F5) // Light surface variant
val LightOnSurfaceVariant = Color(0xFF49454F) // Text on surface variant

// Dark Theme Colors
val DarkPrimary = MarsRedLight
val DarkOnPrimary = Color.Black
val DarkPrimaryContainer = MarsRock
val DarkOnPrimaryContainer = MarsDust

val DarkSecondary = CosmicPurple
val DarkOnSecondary = Color.White
val DarkSecondaryContainer = SpaceBlueDark
val DarkOnSecondaryContainer = StarWhite

val DarkBackground = Color(0xFF1C1B1F) // Dark space background
val DarkOnBackground = Color(0xFFE6E1E5) // Light text
val DarkSurface = Color(0xFF1C1B1F) // Dark surface
val DarkOnSurface = Color(0xFFE6E1E5) // Light text on surface

val DarkSurfaceVariant = Color(0xFF49454F) // Dark surface variant
val DarkOnSurfaceVariant = Color(0xFFCAC4D0) // Light text on surface variant
