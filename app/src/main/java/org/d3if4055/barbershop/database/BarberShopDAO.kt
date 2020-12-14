package org.d3if4055.barbershop.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BarberShopDAO {

    @Insert
    fun insert(barberShop: BarberShop)

    @Update
    fun update(barberShop: BarberShop)

    @Query("SELECT * FROM barbershop")
    fun getBarberShop(): LiveData<List<BarberShop>>

    @Query("DELETE FROM barbershop")
    fun clear()

    @Query("DELETE FROM barbershop WHERE id = :barberShopId")
    fun delete(barberShopId: Long)

}