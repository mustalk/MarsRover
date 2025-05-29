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
val SpaceBlue = Color(0xFF1D2538) // Deep space blue
val SpaceBlueDark = Color(0xFF151725) // Darker space
val StarWhite = Color(0xFFE0E1DD) // Star/moonlight white
val CosmicPurple = Color(0xFF415A77) // Cosmic purple-gray

// Utility Colors
val Success = Color(0xFF4CAF50) // Mission success
val Warning = Color(0xFFFF9800) // Mission warnings
val Error = Color(0xFFE53935) // Mission errors
val Info = Color(0xFF2196F3) // Information

// System Bar Colors for Mars Theme
val MarsStatusBarLight = Color(0xFF8D5524) // Light theme status bar
val MarsStatusBarDark = Color(0xFF2D1B0F) // Dark theme status bar
val MarsNavigationBarLight = Color(0xFF6B4226) // Light theme navigation bar
val MarsNavigationBarDark = Color(0xFF1A0F08) // Dark theme navigation bar

// TopAppBar Colors (same as system bars for consistency)
val MarsTopAppBarLight = MarsStatusBarLight // Light theme top app bar
val MarsTopAppBarDark = MarsStatusBarDark // Dark theme top app bar

// Light Theme Colors - Optimized for Mars Background
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
val LightSurface = Color(0xB31B263B) // 70% transparent dark space blue for glassmorphism
val LightOnSurface = Color(0xFFE0E1DD) // Light text on dark surface

val LightSurfaceVariant = Color(0xB31B263B) // 70% transparent dark space blue
val LightOnSurfaceVariant = Color(0xFFCAC4D0) // Light gray text

// Dark Theme Colors - Optimized for Mars Background
val DarkPrimary = MarsRedLight
val DarkOnPrimary = Color.Black
val DarkPrimaryContainer = MarsRock
val DarkOnPrimaryContainer = MarsDust

val DarkSecondary = CosmicPurple
val DarkOnSecondary = Color.White
val DarkSecondaryContainer = SpaceBlueDark
val DarkOnSecondaryContainer = StarWhite

val DarkBackground = Color(0xFF121212) // Pure dark background
val DarkOnBackground = Color(0xFFE6E1E5) // Light text
val DarkSurface = Color(0xCC1A1A1A) // 80% transparent dark gray for glassmorphism
val DarkOnSurface = Color(0xFFE0E1DD) // Light text on dark surface

val DarkSurfaceVariant = Color(0xCC141414) // 80% transparent very dark gray
val DarkOnSurfaceVariant = Color(0xFFCAC4D0) // Light gray text
