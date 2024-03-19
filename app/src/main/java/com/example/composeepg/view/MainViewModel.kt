package com.example.composeepg.view

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeepg.data.ChannelRowItems
import com.example.composeepg.data.MockData
import com.example.composeepg.data.ProgramRowItems
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel: ViewModel() {
    val isPositionSet = MutableLiveData<Boolean>()
    /**
     * Start by gathering info then sets ready
     */
    val uiState: StateFlow<HomeScreenUiState> = combine(
        MockData().createChannelsFlow(),
        MockData().createProgramsFlow(),
    ) {
        channelList, programsList ->
        HomeScreenUiState.Ready(channelList, programsList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScreenUiState.Loading
    )

    /**
     * HashMap for storing time position X
     */
    val startTimePositions = mutableStateMapOf<String, Float>()
    var timeNowPosition:Float = 1F

}
sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data object Error : HomeScreenUiState
    data class Ready(
        val channelList: MutableList<ChannelRowItems>,
        val programsList: MutableList<ProgramRowItems>,
    ) : HomeScreenUiState
}