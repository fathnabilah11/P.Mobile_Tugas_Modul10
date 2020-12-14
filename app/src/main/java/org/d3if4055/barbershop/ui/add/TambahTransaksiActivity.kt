package org.d3if4055.barbershop.ui.add

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import org.d3if4055.barbershop.R
import org.d3if4055.barbershop.database.BarberShop
import org.d3if4055.barbershop.database.BarberShopDatabase
import org.d3if4055.barbershop.databinding.ActivityTambahTransaksiBinding
import org.d3if4055.barbershop.ui.HomeActivity
import org.d3if4055.barbershop.ui.home.BarberShopViewModel
import org.d3if4055.barbershop.utils.rupiah

class TambahTransaksiActivity : AppCompatActivity() {

    private lateinit var viewModel: BarberShopViewModel
    private lateinit var binding: ActivityTambahTransaksiBinding
    private var paket = ""
    private var harga = 0.0
    private var kembalian = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "MAR Barbershop"
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tambah_transaksi)

        cekHarga(binding.etTotalHarga)
        binding.btnProses.setOnClickListener {
            inputCheck()
        }

        val application = requireNotNull(this).application
        val dataSource = BarberShopDatabase.getInstance(application).barberShopDAO
        val viewModelFactory = BarberShopViewModel.Factory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BarberShopViewModel::class.java)
    }

    private fun cekHarga(etHarga: EditText) {
        binding.rgMenu.setOnCheckedChangeListener{ _, _ ->
            when {
                binding.rbMenu1.isChecked -> {
                    harga = 30000.0
                    paket = "Haircut"
                    etHarga.setText(rupiah(harga))
                }
                binding.rbMenu2.isChecked -> {
                    harga = 35000.0
                    paket = "Haircut & Wash"
                    etHarga.setText(rupiah(harga))
                }
                binding.rbMenu3.isChecked -> {
                    harga = 45000.0
                    paket = "Full Service"
                    etHarga.setText(rupiah(harga))
                }
            }
        }
    }

    private fun inputCheck() {
        when {
            binding.etNamaPelanggan.text.trim().isEmpty() -> {
                binding.inputLayoutNama.error = getString(R.string.null_field, "Nama pelanggan")
            }
            binding.etTotalHarga.text.trim().isEmpty() -> {
                binding.inputLayoutTotalHarga.error = getString(R.string.null_field, "Total harga")
            }
            binding.etTotalBayar.text.trim().isEmpty() -> {
                binding.inputLayoutTotalBayar.error = getString(R.string.null_field, "Total bayar")
            }
            else -> doProcess()
        }
    }

    private fun doProcess() {
        val totalBayar = binding.etTotalBayar.text.toString().toDouble()

        if (hitungTransaksi(totalBayar, harga)) {
            val nama = binding.etNamaPelanggan.text.toString()
            val barberShop = BarberShop(0, nama, paket, harga, totalBayar, kembalian)

            TambahTransaksiDialogFragment(
                barberShop
            ).show(supportFragmentManager, "")
        } else {
            binding.inputLayoutTotalBayar.error = getString(R.string.uang_kurang)
        }
    }

    private fun hitungTransaksi(jumlahUang: Double, harga: Double): Boolean {
        return when {
            jumlahUang >= harga -> {
                kembalian = jumlahUang - harga
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.reset, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.item_reset -> {
                reset()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun reset() {
        binding.etNamaPelanggan.setText("")
        binding.inputLayoutNama.error = null
        binding.rgMenu.clearCheck()
        binding.etTotalHarga.setText("")
        binding.inputLayoutTotalHarga.error = null
        binding.etTotalBayar.setText("")
        binding.inputLayoutTotalBayar.error = null
    }
}
