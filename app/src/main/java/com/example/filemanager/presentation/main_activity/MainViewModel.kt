package com.example.filemanager.presentation.main_activity

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.domain.usecases.app_entry.ReadAppEntry
import com.example.filemanager.presentation.navgraph.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val readAppEntry: ReadAppEntry
) : ViewModel() {

    private val _startDestination = mutableStateOf(Route.AppStartNavigation.route)
    val startDestination: State<String> = _startDestination

    fun onEvent() {
        readAppEntry().onEach { startfromSplash ->
            if (startfromSplash) {
                _startDestination.value = Route.DashboardNavigation.route
            } else {
                _startDestination.value = Route.AppStartNavigation.route
            }
        }.launchIn(viewModelScope)
    }
}