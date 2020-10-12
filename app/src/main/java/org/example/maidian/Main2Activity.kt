package org.example.maidian

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.maidian.R
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        btn_back.setOnClickListener {
            finish()
        }
    }
}
