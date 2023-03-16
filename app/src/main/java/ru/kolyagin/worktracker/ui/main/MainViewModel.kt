package ru.kolyagin.worktracker.ui.main

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.utils.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

): BaseViewModel<MainScreenState, MainEvent>(MainScreenState.Init) {

    init {
        updateState {
            it //TODO сделать изменение состояния в зависимости от времени
        }
    }

    fun onClickOpenSettings() {
        trySendEvent(MainEvent.OpenSettings)
    }

    fun onClickStartWork() {
        //TODO обработать
        updateState {
            MainScreenState.Working
        }
    }

    fun onClickFinishWork() {
        //TODO обработать
        updateState {
            MainScreenState.Results(Time(0, 0)) // or MainScreenState.WorkNotStarted
        }
    }

    fun onClickStartPause() {
        //TODO обработать
        updateState {
            MainScreenState.Pause
        }
    }

    fun onClickEndPause() {
        //TODO обработать
        updateState {
            MainScreenState.Working // or MainScreenState.WorkNotStarted or MainScreenState.Results
        }
    }

    fun onClickGoToDinner() {
        //TODO обработать
        updateState {
            MainScreenState.Dinnering
        }
    }

    fun onClickReturnFromDinner(){
        //TODO обработать
        updateState {
            MainScreenState.Working // or MainScreenState.WorkNotStarted or MainScreenState.Results
        }
    }
}