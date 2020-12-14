package org.d3if4055.barbershop.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*

import org.d3if4055.barbershop.R
import org.d3if4055.barbershop.database.BarberShop
import org.d3if4055.barbershop.database.BarberShopDatabase
import org.d3if4055.barbershop.databinding.FragmentHomeBinding
import org.d3if4055.barbershop.ui.HomeActivity
import org.d3if4055.barbershop.utils.RecyclerViewClickListener

class HomeFragment : Fragment(),
    RecyclerViewClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: BarberShopViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        judul()
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_home, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = BarberShopDatabase.getInstance(application).barberShopDAO
        val viewModelFactory = BarberShopViewModel.Factory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BarberShopViewModel::class.java)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.barberShop.observe(viewLifecycleOwner, Observer {
            val adapter = BarberShopAdapter(it)
            val recyclerView = binding.rvBarbershop
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

            // set listener
            adapter.listener = this
        })
        clearDataTransaksi()

//        binding.fabAddTransaksi.setOnClickListener {
//            it.findNavController().navigate(R.id.action_homeFragment_to_tambahTransaksiFragment)
//        }
    }

    private fun clearDataTransaksi(){
        imageView_clear_data_transaksi.setOnClickListener {
            AlertDialog.Builder(requireContext()).also {
                it.setTitle(getString(R.string.delete_all_confirmation))
                it.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.onClickClear()
                }
                it.setNegativeButton(getString(R.string.no)) {dialog, _ ->
                    dialog.dismiss()
                }
                it.setCancelable(false)
            }.create().show()
        }
    }

    override fun onRecyclerViewItemClicked(view: View, barberShop: BarberShop) {
        val bundle = bundleOf("dataBarberShop" to barberShop)

        when(view.id) {
            R.id.list_data_cukur -> {
                view.findNavController().navigate(R.id.action_homeFragment_to_editTransaksiFragment, bundle)
            }
        }

    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.delete, menu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        return when(item.itemId) {
//            R.id.item_delete -> {
//                AlertDialog.Builder(requireContext()).also {
//                    it.setTitle(getString(R.string.delete_confirmation))
//                    it.setPositiveButton(getString(R.string.yes)) { _, _ ->
//                        viewModel.onClickClear()
//                        Snackbar.make(requireView(), getString(R.string.success_delete), Snackbar.LENGTH_SHORT).show()
//                    }
//                }.create().show()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun judul() {
        val getActivity = activity!! as HomeActivity
        getActivity.supportActionBar?.hide()
    }
}
