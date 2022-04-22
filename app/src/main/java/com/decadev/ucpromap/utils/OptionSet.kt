package com.decadev.ucpromap.utils

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.decadev.ucpromap.R

class OptionSet(val linear: LinearLayout, val group: RadioGroup, val button: ImageButton) {

    fun showView(context: Context) {
        linear.background=(ContextCompat.getDrawable(context, R.drawable.settings_option_selected))
        group.visibility = View.VISIBLE
        //   button.background = ContextCompat.getDrawable(context, R.drawable.ic_arrow_up)
        button.setBackgroundResource(R.drawable.ic_arrow_up)

        isVisible=true

    }

    var isVisible:Boolean = false

    fun hideView(context: Context) {
        linear.background=(ContextCompat.getDrawable(context, R.drawable.settings_option_unselected))
        group.visibility = View.GONE
        isVisible=false
        // button.background = ContextCompat.getDrawable(context, R.drawable.ic_arrow_down)
        button.setBackgroundResource(R.drawable.ic_arrow_down)


    }


}
