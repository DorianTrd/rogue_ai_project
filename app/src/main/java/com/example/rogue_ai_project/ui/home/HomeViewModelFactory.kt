package com.example.rogue_ai_project.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rogue_ai_project.data.GameRepository
import com.example.rogue_ai_project.util.SfxManager

/**
 * Factory for creating HomeViewModel instances.
 *
 * Allows passing dependencies (SfxManager, GameRepository) to the ViewModel.
 * Required because HomeViewModel has constructor parameters.
 */
class HomeViewModelFactory(
    private val sfxManager: SfxManager,
    private val gameRepository: GameRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                repository = gameRepository,
                sfxManager = sfxManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}