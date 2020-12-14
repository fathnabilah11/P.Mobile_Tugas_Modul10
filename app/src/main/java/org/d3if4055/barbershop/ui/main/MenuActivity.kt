package org.d3if4055.barbershop.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import org.d3if4055.barbershop.R
import org.d3if4055.barbershop.databinding.ActivityMenuBinding
import org.d3if4055.barbershop.ui.HomeActivity
import org.d3if4055.barbershop.ui.add.TambahTransaksiActivity

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu)
        supportActionBar?.hide()

        // click listener
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, TambahTransaksiActivity::class.java))
        }
    }
}
