package com.example.composeepg.view

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeepg.R
import com.example.composeepg.data.ChannelRowItems
import com.example.composeepg.data.MockData
import com.example.composeepg.data.ProgramRowItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel: ViewModel() {
    val isPositionSet = MutableLiveData<Boolean>()
    private val selectedFilterList = MutableStateFlow(FilterList())
    private var programsList:MutableList<ProgramRowItems> = mutableListOf()
    private var channelList:MutableList<ChannelRowItems> = mutableListOf()
    private var hoursList = mutableListOf<String>()
    var offsetHours: Dp? = null

    init {
        loadProgramData()
    }

    fun setHoursFullList(reducedHours: MutableList<String>) {
        hoursList =reducedHours
    }
    fun getHoursFullList() :MutableList<String>{
        return hoursList
    }

    private fun loadProgramData() {
        programsList = MockData().createPrograms()
        channelList = MockData().createChannels()
    }

    fun getProgramsList(): List<ProgramRowItems>{
        return programsList
    }

    fun getProgramOnNow(indexReduced: String): ProgramRowItems? {
        return programsList.filter {
            it.programStart.substringBefore(".") == indexReduced
        }.firstOrNull()
    }

    fun getProgramsForChannel(channelId: Int): List<ProgramRowItems> {
        return programsList.filter { it.channelId == channelId }.toMutableList()
    }
    fun getProgramData(programId:Int,channelId: Int):ProgramRowItems {
        return programsList.filter { it.programID == programId && it.channelId == channelId }.first()
    }
    fun getChannelData(channelId:Int):ChannelRowItems {
        return channelList.filter { it.channelID == channelId }.first()
    }

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

    fun updateSelectedFilterList(filterList: FilterList) {
        selectedFilterList.value = filterList
    }

}
@Immutable
data class FilterList(val items: List<FilterCondition> = emptyList()) {
    fun toIdList(): List<Int> {
        if (items.isEmpty()) {
            return FilterCondition.None.idList
        }
        return items.asSequence().map {
            it.idList
        }.fold(emptyList()) { acc, ints ->
            acc + ints
        }
    }
}

@Immutable
enum class FilterCondition(val idList: List<Int>, @StringRes val labelId: Int) {
    None((0..28).toList(), R.string.favorites_unknown),
}
sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data object Error : HomeScreenUiState
    data class Ready(
        val channelList: MutableList<ChannelRowItems>,
        val programsList: MutableList<ProgramRowItems>,
    ) : HomeScreenUiState
}