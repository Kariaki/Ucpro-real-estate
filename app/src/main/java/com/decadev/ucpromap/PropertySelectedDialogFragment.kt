package com.decadev.ucpromap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment

class PropertySelectedDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.property_selected_layout, container, false)

        val confirmButton = view.findViewById<Button>(R.id.confirm_button)
        val editButton = view.findViewById<Button>(R.id.edit_button)

        confirmButton.setOnClickListener {
            val dialog = SuccessMessageDialogFragment()

            dialog.show(parentFragmentManager, "custom dialog")

            dismiss()
        }

        editButton.setOnClickListener {
            val dialog = PropertyTypeDialogFragment()

            dialog.show(parentFragmentManager, "custom dialog")
            dismiss()
        }


        return view
    }
}