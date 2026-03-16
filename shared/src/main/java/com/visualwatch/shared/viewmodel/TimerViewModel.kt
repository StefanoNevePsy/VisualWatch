package com.visualwatch.shared.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visualwatch.shared.data.PresetRepository
import com.visualwatch.shared.data.TimerPreset
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    val repository = PresetRepository(application)
    val presets = repository.presets

    fun addPreset(preset: TimerPreset) {
        viewModelScope.launch { repository.addPreset(preset) }
    }

    fun updatePreset(preset: TimerPreset) {
        viewModelScope.launch { repository.updatePreset(preset) }
    }

    fun deletePreset(id: String) {
        viewModelScope.launch { repository.deletePreset(id) }
    }

    fun savePreset(preset: TimerPreset, isNew: Boolean) {
        if (isNew) addPreset(preset) else updatePreset(preset)
    }
}
