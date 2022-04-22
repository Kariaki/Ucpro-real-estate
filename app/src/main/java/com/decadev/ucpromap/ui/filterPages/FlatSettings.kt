package com.decadev.ucpromap.ui.filterPages

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProviders
import com.decadev.ucpromap.R
import com.decadev.ucpromap.databinding.FragmentFlatSettingsBinding
import com.decadev.ucpromap.utils.OptionSet
import com.decadev.ucpromap.viewModel.FilterStateViewModel


class FlatSettings : Fragment(), RadioGroup.OnCheckedChangeListener, View.OnClickListener {


    lateinit var binding: FragmentFlatSettingsBinding

    lateinit var optionSets: List<OptionSet>

    lateinit var optionsViewModel: FilterStateViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFlatSettingsBinding.inflate(layoutInflater)
        optionsViewModel =
            ViewModelProviders.of(requireActivity()).get(FilterStateViewModel::class.java)

        optionSets = listOf(
            OptionSet(
                binding.sellingCostOption,
                binding.sellingCostRadioGroup,
                binding.sellingCostButton
            ),
            OptionSet(
                binding.rentOption,
                binding.rentRadioGroup,
                binding.rentButton
            ),
            OptionSet(
                binding.depositOption,
                binding.depositRadioGroup,
                binding.depositButton
            ),
            OptionSet(
                binding.roomConfigOption,
                binding.roomConfigRadioGroup,
                binding.roomConfigButton
            ),
            OptionSet(
                binding.builtUpAreaOption,
                binding.builtUpAreaRadioGroup,
                binding.builtUpButon
            ),
            OptionSet(
                binding.floorOption,
                binding.flooradioGroup,
                binding.floorButton
            ),

            )


        binding.topRadioGroup.setOnCheckedChangeListener(this)
        hideAllViews()

        binding.spinner.adapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.facing)
        )
        binding.spinner.setSelection(0)

        optionClicks()

        optionsViewModel.selectedFilterOption.observe(viewLifecycleOwner) {

            if (it.isVisible) {
                hideAllViews()
                it.showView(requireContext())
            }

        }

        return binding.root
    }

    private fun optionClicks() {
        binding.depositOption.setOnClickListener(this)
        binding.rentOption.setOnClickListener(this)
        binding.sellingCostOption.setOnClickListener(this)
        binding.builtUpAreaOption.setOnClickListener(this)
        binding.floorOption.setOnClickListener(this)
        binding.roomConfigOption.setOnClickListener(this)
    }

    override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
        if (p0 == binding.topRadioGroup) {

            val button = binding.root.findViewById<RadioButton>(p1)
            when (button) {
                binding.sale -> {

                    binding.sellingCostOption.visibility = View.VISIBLE
                    binding.rentOption.visibility = View.GONE
                    binding.depositOption.visibility = View.GONE
                }
                binding.rent -> {
                    binding.sellingCostOption.visibility = View.GONE
                    binding.rentOption.visibility = View.VISIBLE
                    binding.depositOption.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onClick(p0: View?) {

        val clickedOption = optionSets.find { it.linear == p0 }
        if (clickedOption != null) {
            optionsViewModel.publishFilterOption(clickedOption.apply { showView(requireContext()) })
        }
    }

    private fun hideAllViews() {
        optionSets.forEach { it.hideView(requireContext()) }
    }

    private fun showAllViews() {
        optionSets.forEach { it.showView(requireContext()) }
    }


}