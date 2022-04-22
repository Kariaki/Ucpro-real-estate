package com.decadev.ucpromap

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*

class EstabRentSale : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estab_rent_sale)

        val rent_btn = findViewById<RadioButton>(R.id.rent_btn)
        val sale_btn = findViewById<RadioButton>(R.id.sale_btn)
        val rent_cost = findViewById<LinearLayout>(R.id.rent_cost)
        val sale_cost = findViewById<LinearLayout>(R.id.sale_cost)
        val rent_sale = findViewById<RadioGroup>(R.id.rent_sale)


        val pox_details_line = findViewById<View>(R.id.pox_details_line)
        val pox_details_circle = findViewById<View>(R.id.pox_details_circle)
        val pox_details_text = findViewById<TextView>(R.id.pox_details_text)

//        for animation code

        /**
         *  Handler(Looper.getMainLooper()).postDelayed({

        pox_details_line.background.setTint(Color.rgb(253,216,53))
        pox_details_circle.background.setTint(Color.rgb(253,216,53))
        pox_details_text.setTextColor(Color.rgb(253,216,53))

        }, 1500)

         */


        rent_sale.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { radioGroup, i ->
                val rs_radio : RadioButton = findViewById(i)

                if (R.id.rent_btn == i){
                    rent_cost.visibility = View.VISIBLE
                    sale_cost.visibility = View.GONE
                } else if (R.id.sale_btn == i) {
                    rent_cost.visibility = View.GONE
                    sale_cost.visibility = View.VISIBLE
                } else {
                    rent_cost.visibility = View.GONE
                    sale_cost.visibility = View.GONE
                }
            })
    }

    fun goToBack(view: View) {
        this.finish()
    }
}