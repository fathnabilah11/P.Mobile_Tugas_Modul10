package org.d3if4055.barbershop.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "barbershop")
data class BarberShop (

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "nama")
    var nama: String,

    @ColumnInfo(name = "paket")
    var paket: String,

    @ColumnInfo(name = "harga")
    var harga: Double,

    @ColumnInfo(name = "bayar")
    var bayar: Double,

    @ColumnInfo(name = "kembalian")
    var kembalian: Double,

    @ColumnInfo(name = "tanggal")
    var tanggal: Long = System.currentTimeMillis()

) : Parcelable