package com.example.rogue_ai_project.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rogue_ai_project.data.GameRepository // Ajoutez GameRepository
import com.example.rogue_ai_project.util.SfxManager

class HomeViewModelFactory(
    private val sfxManager: SfxManager,
    private val gameRepository: GameRepository// Ajoutez la dépendance GameRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                repository = gameRepository, // Passez les deux arguments
                sfxManager = sfxManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}