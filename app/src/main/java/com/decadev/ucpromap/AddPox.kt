package com.decadev.ucpromap

import android.content.Intent
import android.graphics.Color
import android.graphics.Color.YELLOW
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.core.graphics.rotationMatrix

class AddPox : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pox)


        val next_flat_btn = findViewById<LinearLayout>(R.id.next_flat_btn)
        val next_plot_btn = findViewById<LinearLayout>(R.id.next_plot_btn)
        val next_estab_btn = findViewById<LinearLayout>(R.id.next_estab_btn)
        val next_default_btn = findViewById<LinearLayout>(R.id.next_default_btn)


        val flat_property = findViewById<LinearLayout>(R.id.flat_property)
        val plot_property = findViewById<LinearLayout>(R.id.plot_property)
        val estab_property = findViewById<LinearLayout>(R.id.estab_property)

        val property_radio_group = findViewById<RadioGroup>(R.id.property_radio_group)

        val spinner = findViewById<Spinner>(R.id.facing_type_spinner)

        val pox_details_line = findViewById<View>(R.id.pox_details_line)
        val pox_details_circle = findViewById<View>(R.id.pox_details_circle)
        val pox_details_text = findViewById<TextView>(R.id.pox_details_text)

//        for animation code
        Handler(Looper.getMainLooper()).postDelayed({

            pox_details_line.background.setTint(Color.rgb(253,216,53))
            pox_details_circle.background.setTint(Color.rgb(253,216,53))
            pox_details_text.setTextColor(Color.rgb(253,216,53))

        }, 1500)



        property_radio_group.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { radioGroup, i ->
                val rs_radio : RadioButton = findViewById(i)

                if (R.id.flat_btn == i){
                    next_flat_btn.visibility = View.VISIBLE
                    next_plot_btn.visibility = View.GONE
                    next_estab_btn.visibility = View.GONE
                    next_default_btn.visibility = View.GONE
                    flat_property.visibility = View.VISIBLE
                    plot_property.visibility = View.GONE
                    estab_property.visibility = View.GONE
                } else if (R.id.plot_btn == i) {
                    next_plot_btn.visibility = View.VISIBLE
                    next_flat_btn.visibility = View.GONE
                    next_default_btn.visibility = View.GONE
                    next_estab_btn.visibility = View.GONE
                    plot_property.visibility = View.VISIBLE
                    estab_property.visibility = View.GONE
                    flat_property.visibility = View.GONE
                } else if (R.id.estab_btn == i) {
                    next_estab_btn.visibility = View.VISIBLE
                    next_flat_btn.visibility = View.GONE
                    next_plot_btn.visibility = View.GONE
                    next_default_btn.visibility = View.GONE
                    estab_property.visibility = View.VISIBLE
                    plot_property.visibility = View.GONE
                    flat_property.visibility = View.GONE
                } else {
                    next_flat_btn.visibility = View.GONE
                    next_plot_btn.visibility = View.GONE
                    next_default_btn.visibility = View.GONE
                    next_estab_btn.visibility = View.GONE
                    flat_property.visibility = View.GONE
                    plot_property.visibility = View.GONE
                    estab_property.visibility = View.GONE
                }
            })


        if (spinner != null){
            val adapter = ArrayAdapter.createFromResource(this, R.array.facing, R.layout.facing_type_front_show_spinner)
            adapter.setDropDownViewResource(R.layout.facing_type_spinner)
            spinner.adapter = adapter
    }


    }

    fun goToBack(view: View) {
        this.finish()
    }


    fun goingToNextFlat(view: View) {
        startActivity(Intent(this,FlatRentSale::class.java))
    }
    fun goingToNextPlot(view: View) {
        startActivity(Intent(this,PlotSaleLease::class.java))
    }
    fun goingToNextEstab(view: View) {
        startActivity(Intent(this, EstabRentSale::class.java))
    }

    fun goingToShowToast(view: View) {
        Toast.makeText(this,"Please select property type",Toast.LENGTH_LONG).show()
    }

}
