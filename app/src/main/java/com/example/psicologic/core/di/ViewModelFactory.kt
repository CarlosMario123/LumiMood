package com.example.psicologic.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory para crear ViewModels con dependencias personalizadas
 * Esto elimina la necesidad de usar Hilt para inyección de dependencias
 *
 * @param creator Una función lambda que crea una instancia del ViewModel
 */
class ViewModelFactory<T : ViewModel>(private val creator: () -> T) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }

    companion object {
        /**
         * Crea una instancia de ViewModelFactory para un tipo específico de ViewModel
         */
        inline fun <reified VM : ViewModel> createFactory(noinline creator: () -> VM): ViewModelProvider.Factory {
            return ViewModelFactory(creator)
        }
    }
}