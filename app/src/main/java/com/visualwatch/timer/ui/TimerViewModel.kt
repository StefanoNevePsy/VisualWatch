package com.visualwatch.timer.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visualwatch.timer.data.AnimationType
import com.visualwatch.timer.data.PresetRepository
import com.visualwatch.timer.data.TimerPreset
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
