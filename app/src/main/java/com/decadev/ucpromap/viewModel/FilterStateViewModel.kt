package com.decadev.ucpromap.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.decadev.ucpromap.utils.FilterStates
import com.decadev.ucpromap.utils.OptionSet

class FilterStateViewModel() : ViewModel() {

    private val _filterState: MutableLiveData<FilterStates> = MutableLiveData(FilterStates.FLAT)
    val filterStates: LiveData<FilterStates> = _filterState
    private val _selectedFilterOption: MutableLiveData<OptionSet> = MutableLiveData()
    val selectedFilterOption: LiveData<OptionSet> = _selectedFilterOption

    fun publishState(states: FilterStates) {
        _filterState.postValue(states)
    }

    fun publishFilterOption(optionSet: OptionSet){
        _selectedFilterOption.postValue(optionSet)
    }

}