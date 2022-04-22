package com.decadev.ucpromap

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*

class PlotSaleLease : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plot_sale_lease)


        val lease_btn = findViewById<RadioButton>(R.id.lease_btn)
        val sale_btn = findViewById<RadioButton>(R.id.sale_btn)

        val lease_cost = findViewById<LinearLayout>(R.id.lease_cost)
        val sale_cost = findViewById<LinearLayout>(R.id.sale_cost)
        val sale_lease = findViewById<RadioGroup>(R.id.sale_lease)


        val pox_details_line = findViewById<View>(R.id.pox_details_line)
        val pox_details_circle = findViewById<View>(R.id.pox_details_circle)
        val pox_details_text = findViewById<TextView>(R.id.pox_details_text)


        /**
         *
        Handler(Looper.getMainLooper()).postDelayed({

        pox_details_line.background.setTint(Color.rgb(253,216,53))
        pox_details_circle.background.setTint(Color.rgb(253,216,53))
        pox_details_text.setTextColor(Color.rgb(253,216,53))

        }, 1500)
         */



        sale_lease.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { radioGroup, i ->
                val rs_radio : RadioButton = findViewById(i)

                if (R.id.sale_btn == i){
                    sale_cost.visibility = View.VISIBLE
                    lease_cost.visibility = View.GONE
                } else if (R.id.lease_btn == i) {
                    lease_cost.visibility = View.VISIBLE
                    sale_cost.visibility = View.GONE
                } else {
                    lease_cost.visibility = View.GONE
                    sale_cost.visibility = View.GONE
                }
            })
    }

    fun goToBack(view: View) {
        this.finish()
    }
}