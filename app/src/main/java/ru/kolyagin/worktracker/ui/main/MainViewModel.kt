package ru.kolyagin.worktracker.ui.main

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.kolyagin.worktracker.utils.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

): BaseViewModel<MainScreenState, MainEvent>(MainScreenState()) {

    fun onClickOpenSettings() {
        trySendEvent(MainEvent.OpenSettings)
    }
}