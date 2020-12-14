package org.d3if4055.barbershop.ui.edit

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar

import org.d3if4055.barbershop.R
import org.d3if4055.barbershop.database.BarberShop
import org.d3if4055.barbershop.database.BarberShopDatabase
import org.d3if4055.barbershop.databinding.FragmentEditTransaksiBinding
import org.d3if4055.barbershop.ui.HomeActivity
import org.d3if4055.barbershop.utils.rupiah
import org.d3if4055.barbershop.ui.home.BarberShopViewModel

class EditTransaksiFragment : Fragment() {

    private lateinit var binding: FragmentEditTransaksiBinding
    private lateinit var viewModel: BarberShopViewModel
    private lateinit var data: BarberShop
    private var paket = ""
    private var harga = 0.0
    private var kembalian = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        judul()
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_edit_transaksi, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = BarberShopDatabase.getInstance(application).barberShopDAO
        val viewModelFactory = BarberShopViewModel.Factory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BarberShopViewModel::class.java)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            data = arguments?.getParcelable("dataBarberShop")!!
        }
        // set input text
        binding.etNamaPelanggan.setText(data.nama)
        binding.etTotalHarga.setText(rupiah(data.harga))
        binding.etTotalBayar.setText("${data.bayar.toInt()}")

        // radio button check
        when (data.paket) {
            "Haircut" -> binding.rbMenu1.isChecked = true
            "Haircut & Wash" -> binding.rbMenu2.isChecked = true
            "Full Service" -> binding.rbMenu3.isChecked = true
        }

        // set default paket & harga
        paket = data.paket
        harga = data.harga

        // cek harga lewat radio button
        cekHarga(binding.etTotalHarga)

        // onclick transaction
        binding.btnProses.setOnClickListener {
            inputCheck()
        }

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

            val barberShop = BarberShop(data.id, nama, paket, harga, totalBayar, kembalian, System.currentTimeMillis())
            UpdateTransaksiDialogFragment(
                barberShop
            ).show(childFragmentManager, "")
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete, menu)
        inflater.inflate(R.menu.reset, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.item_delete -> {
                AlertDialog.Builder(requireContext()).also {
                    it.setTitle(getString(R.string.delete_confirmation))
                    it.setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel.onClickDelete(data.id)
                        startActivity(
                            Intent(requireContext(), HomeActivity::class.java).addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        Toast.makeText(requireContext(), getString(R.string.success_delete), Toast.LENGTH_SHORT).show()
                    }
                    it.setNegativeButton(getString(R.string.no)) {dialog, _ ->
                        dialog.dismiss()
                    }
                    it.setCancelable(false)
                }.create().show()
                true
            }
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

    private fun judul() {
        val getActivity = activity!! as HomeActivity
        getActivity.supportActionBar?.show()
        getActivity.supportActionBar?.title = "MAR Barbershop"
    }
}
