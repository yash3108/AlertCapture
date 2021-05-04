package com.example.com.alertcapture

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var t1: TextView = findViewById(R.id.text1)

        var b1: Button = findViewById(R.id.button1)

        t1.isEnabled = false

//        val alertactivity_obj = AlertActivity()
//        var s1: Switch = findViewById(R.id.switch1)
//        if (s1.isChecked){
//            t1.isEnabled = true
//        }

        val sharedPreferences: SharedPreferences = this.getSharedPreferences("save", Context.MODE_PRIVATE)
        val b: Boolean = sharedPreferences.getBoolean("value", false)
        Log.i("Value of b", b.toString())

        if(b){
            t1.isEnabled = true
            Log.i("b", "Yes")
        }

        b1.setOnClickListener{
            val intent = Intent(this, AlertActivity::class.java)
            startActivity(intent)
        }
    }
}