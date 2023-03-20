package ru.kolyagin.worktracker.ui.main

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.kolyagin.worktracker.utils.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

): BaseViewModel<MainScreenState, MainEvent>(MainScreenState()) {

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
//        updateState {
//            CardState.Working
//        }
    }

    fun onClickFinishWork() {
        //TODO обработать
//        updateState {
//            CardState.Results(Time(0, 0)) // or CardState.WorkNotStarted
//        }
    }

    fun onClickStartPause() {
        //TODO обработать
//        updateState {
//            CardState.Pause
//        }
    }

    fun onClickEndPause() {
        //TODO обработать
//        updateState {
//            CardState.Working // or CardState.WorkNotStarted or CardState.Results
//        }
    }

    fun onClickGoToDinner() {
        //TODO обработать
//        updateState {
//            CardState.Dinnering
//        }
    }

    fun onClickReturnFromDinner(){
        //TODO обработать
//        updateState {
//            CardState.Working // or CardState.WorkNotStarted or CardState.Results
//        }
    }
}