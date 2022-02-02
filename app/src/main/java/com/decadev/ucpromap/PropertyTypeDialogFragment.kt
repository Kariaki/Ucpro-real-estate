package com.decadev.ucpromap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.decadev.ucpromap.utils.listOfProperties

class PropertyTypeDialogFragment : DialogFragment() {
    var count = 0

    lateinit var propertyTypeEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.property_type_layout, container, false)

        val leftArrow = view.findViewById<ImageView>(R.id.arrow_left)
        val rightArrow = view.findViewById<ImageView>(R.id.arrow_right)
        val roomConfigEditText = view.findViewById<EditText>(R.id.room_config_edit_text)
        val previewButton = view.findViewById<Button>(R.id.preview_button)

        propertyTypeEditText = view.findViewById(R.id.property_type_edit_text)

        propertyTypeEditText.setText(listOfProperties.typesOfProperty[count])

        rightArrow.setOnClickListener {
            if (count < 2) {
                count++
            }
            propertyTypeEditText.setText(listOfProperties.typesOfProperty[count])

            when (count) {
                1 -> roomConfigEditText.visibility = View.GONE
                2 -> roomConfigEditText.visibility = View.GONE
                else -> roomConfigEditText.visibility = View.VISIBLE
            }
        }

        leftArrow.setOnClickListener {
            if (count > 0) {
                count--
            }
            propertyTypeEditText.setText(listOfProperties.typesOfProperty[count])

            when (count) {
                1 -> roomConfigEditText.visibility = View.GONE
                2 -> roomConfigEditText.visibility = View.GONE
                else -> roomConfigEditText.visibility = View.VISIBLE
            }
        }

        previewButton.setOnClickListener {
            val dialog = PropertySelectedDialogFragment()

            dialog.show(parentFragmentManager, "customDialog")
            dismiss()
        }


        return view
    }
}