package com.mustalk.seat.marsrover.presentation.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Dashboard screen.
 * Manages the state of mission results and handles user interactions.
 */
@HiltViewModel
class DashboardViewModel
    @Inject
    constructor() : ViewModel() {
        private val _uiState = MutableStateFlow(DashboardUiState())
        val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

        /**
         * Handles the event when user wants to start a new mission.
         */
        fun onNewMissionClicked() {
            _uiState.value = _uiState.value.copy(showNewMissionDialog = true)
        }

        /**
         * Handles dismissing the new mission dialog.
         */
        fun onNewMissionDialogDismissed() {
            _uiState.value = _uiState.value.copy(showNewMissionDialog = false)
        }

        /**
         * Updates the dashboard with a new mission result.
         * This will be called from other screens when a mission is completed.
         */
        fun updateMissionResult(result: MissionResult) {
            viewModelScope.launch {
                _uiState.value =
                    _uiState.value.copy(
                        lastMissionResult = result,
                        isLoading = false,
                        errorMessage = null
                    )
            }
        }

        /**
         * Clears any error messages from the UI.
         */
        fun clearError() {
            _uiState.value = _uiState.value.copy(errorMessage = null)
        }

        /**
         * Sets a loading state for the dashboard.
         */
        fun setLoading(isLoading: Boolean) {
            _uiState.value = _uiState.value.copy(isLoading = isLoading)
        }

        /**
         * Displays an error message on the dashboard.
         */
        fun showError(message: String) {
            _uiState.value =
                _uiState.value.copy(
                    errorMessage = message,
                    isLoading = false
                )
        }
    }
