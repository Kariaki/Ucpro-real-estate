package com.decadev.ucpromap.ui

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.SpinnerAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.decadev.ucpromap.R
import com.decadev.ucpromap.databinding.FragmentFilterBinding
import com.decadev.ucpromap.ui.adapters.FilterPageAdapter
import com.decadev.ucpromap.ui.filterPages.EstabSettings
import com.decadev.ucpromap.ui.filterPages.FlatSettings
import com.decadev.ucpromap.ui.filterPages.PlotSettings
import com.decadev.ucpromap.utils.FilterStates
import com.decadev.ucpromap.viewModel.FilterStateViewModel


class FilterFragment : AppCompatActivity(), AdapterView.OnItemSelectedListener,
        View.OnClickListener {


    lateinit var binding: FragmentFilterBinding

    lateinit var stateViewModel: FilterStateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFilterBinding.inflate(layoutInflater)
        stateViewModel = ViewModelProviders.of(this).get(FilterStateViewModel::class.java)
        setContentView(binding.root)

        val adapter = ArrayAdapter<String>(
                this, android.R.layout.simple_dropdown_item_1line,
                listOf("Hello", "World", "good")
        )

        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter =
                FilterPageAdapter(listOf(FlatSettings(), PlotSettings(), EstabSettings()), this)

        binding.plot.setOnClickListener(this)
        binding.flat.setOnClickListener(this)
        binding.estab.setOnClickListener(this)

        stateViewModel.filterStates.observe(this) {

            updateUI(it!!)

            when (it) {
                FilterStates.FLAT -> binding.viewPager.setCurrentItem(0, false)
                FilterStates.PLOT -> binding.viewPager.setCurrentItem(1, false)
                FilterStates.ESTAB -> binding.viewPager.setCurrentItem(2, false)
            }
        }


    }

    private fun updateUI(filterStates: FilterStates) {
        when (filterStates) {
            FilterStates.FLAT -> {
                clearStates()
                binding.flat.background =
                        ContextCompat.getDrawable(this, R.drawable.filter_selected_background)
                binding.flat.elevation=5f
            }
            FilterStates.PLOT -> {

                clearStates()
                binding.plot.background =
                        ContextCompat.getDrawable(this, R.drawable.filter_selected_background)
                binding.flat.elevation=5f
            }
            FilterStates.ESTAB -> {

                clearStates()
                binding.estab.background =
                        ContextCompat.getDrawable(this, R.drawable.filter_selected_background)
                binding.flat.elevation=5f
            }
        }
    }

    private fun clearStates() {

        val allStates = listOf<LinearLayout>(binding.plot, binding.flat, binding.estab)
        allStates.forEach {
            setBackground(it)
        }
    }

    private fun setBackground(layout: LinearLayout) {
        layout.background = ContextCompat.getDrawable(this, R.drawable.filter_unselected_background)
        layout.elevation=5f
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.plot -> stateViewModel.publishState(FilterStates.PLOT)
            binding.flat -> stateViewModel.publishState(FilterStates.FLAT)
            binding.estab -> stateViewModel.publishState(FilterStates.ESTAB)
            else -> stateViewModel.publishState(FilterStates.FLAT)

        }
    }

}