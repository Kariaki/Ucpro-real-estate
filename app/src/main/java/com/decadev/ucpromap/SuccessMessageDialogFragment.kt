package com.decadev.ucpromap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment

class SuccessMessageDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.successfully_posted_layout, container, false)

        val viewPoxButton = view.findViewById<Button>(R.id.view_pox_button)

        viewPoxButton.setOnClickListener {
            dismiss()
        }


        return view
    }
}