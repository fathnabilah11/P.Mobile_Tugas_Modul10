package org.d3if4055.barbershop.ui.home

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.*
import org.d3if4055.barbershop.database.BarberShop
import org.d3if4055.barbershop.database.BarberShopDAO

class BarberShopViewModel(
    val database: BarberShopDAO,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope =   CoroutineScope(Dispatchers.Main + viewModelJob)
    val barberShop = database.getBarberShop()

    fun onClickInsert(barberShop: BarberShop) {
        uiScope.launch {
            insert(barberShop)
        }
    }

    private suspend fun insert(barberShop: BarberShop) {
        withContext(Dispatchers.IO) {
            database.insert(barberShop)
        }
    }

    fun onClickUpdate(barberShop: BarberShop) {
        uiScope.launch {
            update(barberShop)
        }
    }

    private suspend fun update(barberShop: BarberShop) {
        withContext(Dispatchers.IO) {
            database.update(barberShop)
        }
    }

    fun onClickClear() {
        uiScope.launch {
            clear()
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    fun onClickDelete(id: Long) {
        uiScope.launch {
            delete(id)
        }
    }

    private suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) {
            database.delete(id)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory (
        private val dataSource: BarberShopDAO,
        private val application: Application
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(BarberShopViewModel::class.java)) {
                return BarberShopViewModel(
                    dataSource,
                    application
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")

        }

    }
}