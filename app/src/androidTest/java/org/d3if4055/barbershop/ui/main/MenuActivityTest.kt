package org.d3if4055.barbershop.ui.main

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.d3if4055.barbershop.R
import org.d3if4055.barbershop.database.BarberShop
import org.d3if4055.barbershop.database.BarberShopDatabase
import org.d3if4055.barbershop.ui.HomeActivity
import org.d3if4055.barbershop.utils.rupiah
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MenuActivityTest{

    companion object {
        private const val namaPelanggan = "Irgi"
        private const val hargaHaircutWash = 35000
        private const val hargaFullService = 45000
        private const val totalBayar = 50000
        private const val kembalian = totalBayar - hargaHaircutWash

        private const val namaPelangganUpdate = "Amau"
        private const val totalBayarUpdate = 100000
        private val TRANSAKSI_DUMMY = BarberShop(
            0,
            "Irgi Ahmad M",
            "Haircut & Wash",
            35000.0,
            50000.0,
            15000.0,
            System.currentTimeMillis()
        )
    }

    @Before
    fun setUp(){
        InstrumentationRegistry.getInstrumentation().targetContext
            .deleteDatabase(BarberShopDatabase.DATABASE_NAME)
    }

    @Test
    fun testClearForm(){
        val activityScenario = ActivityScenario.launch(MenuActivity::class.java)

        //click menu tambah transaksi
        onView(withId(R.id.btn_add)).perform(click())
        //fillForm
        fillForm()
        //click menu refresh
        onView(withId(R.id.item_reset)).perform(click())
        //check apakah form kosong setelah di clear
        onView(withText(namaPelanggan)).check(doesNotExist())
        onView(withText(rupiah(hargaHaircutWash.toDouble()))).check(doesNotExist())
        onView(withText(totalBayar)).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun testUpdateTransaksi(){
        runBlocking(Dispatchers.IO) {
            val dao = BarberShopDatabase.getInstance(getApplicationContext()).barberShopDAO
            dao.insert(TRANSAKSI_DUMMY)
        }

        val activityScenario = ActivityScenario.launch(HomeActivity::class.java)

        //click reyclerview
        onView(withId(R.id.rv_barbershop)).atItem(0, click())

        //cek data apakah sesuai dengan data yang di klik pada recyclerview
        onView(withText(TRANSAKSI_DUMMY.nama))
        onView(withId(R.id.rb_menu2)).check(matches(isChecked()))
        onView(withText(rupiah(TRANSAKSI_DUMMY.harga)))
        onView(withText(TRANSAKSI_DUMMY.bayar.toString()))

        //clear lalu type
        onView(withId(R.id.et_nama_pelanggan)).perform(clearText())
        onView(withId(R.id.et_nama_pelanggan)).perform(typeText(namaPelangganUpdate))

        onView(withId(R.id.rb_menu3)).perform(click())

        onView(withId(R.id.et_total_bayar)).perform(clearText())
        onView(withId(R.id.et_total_bayar)).perform(typeText(totalBayarUpdate.toString()))

        //proses lalu submit transaksi
        onView(withId(R.id.btn_proses)).perform(click())
        onView(withId(R.id.btn_submit_transaksi)).perform(click())

        //check perubahan data
        onView(withText(namaPelangganUpdate)).check(matches(isDisplayed()))
        onView(withText(rupiah(hargaFullService.toDouble()))).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun testDeleteTransaksi(){
        runBlocking(Dispatchers.IO) {
            val dao = BarberShopDatabase.getInstance(getApplicationContext()).barberShopDAO
            dao.insert(TRANSAKSI_DUMMY)
        }

        val activityScenario = ActivityScenario.launch(HomeActivity::class.java)

        //click reyclerview
        onView(withId(R.id.rv_barbershop)).atItem(0, click())

        //cek data apakah sesuai dengan data yang di klik pada recyclerview
        onView(withText(TRANSAKSI_DUMMY.nama))
        onView(withId(R.id.rb_menu2)).check(matches(isChecked()))
        onView(withText(rupiah(TRANSAKSI_DUMMY.harga)))
        onView(withText(TRANSAKSI_DUMMY.bayar.toString()))

        //konfirmasi panghapusan data
        onView(withId(R.id.item_delete)).perform(click())
        onView(withText(R.string.yes)).perform(click())

        //check perubahan data
        onView(withText(namaPelangganUpdate)).check(doesNotExist())
        onView(withText(rupiah(hargaFullService.toDouble()))).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun testValidateInput(){
        val activityScenario = ActivityScenario.launch(MenuActivity::class.java)

        onView(withId(R.id.btn_add)).perform(click())
        onView(withId(R.id.btn_proses)).perform(click())
        onView(withText("Nama pelanggan wajib diisi!")).check(matches(isDisplayed()))
        onView(withId(R.id.et_nama_pelanggan)).perform(typeText(namaPelanggan))

        onView(withId(R.id.btn_proses)).perform(click())
        onView(withText("Total harga wajib diisi!")).check(matches(isDisplayed()))
        onView(withId(R.id.rb_menu2)).perform(click())
        onView(withText(rupiah(hargaHaircutWash.toDouble()))).check(matches(isDisplayed()))

        onView(withId(R.id.btn_proses)).perform(click())
        onView(withText("Total bayar wajib diisi!")).check(matches(isDisplayed()))
        onView(withId(R.id.et_total_bayar)).perform(typeText(totalBayar.toString()))


        onView(withId(R.id.btn_proses)).perform(click())
        //check dialog muncul atau tidak
        onView(withText(R.string.konfirmasi_transaksi)).check(matches(isDisplayed()))

        //konfirmasi kesamaan data yang akan disubmit
        onView(withText(namaPelanggan)).check(matches(isDisplayed()))
        onView(withText("Haircut & Wash")).check(matches(isDisplayed()))
        onView(withText(rupiah(hargaHaircutWash.toDouble()))).check(matches(isDisplayed()))
        onView(withText(rupiah(totalBayar.toDouble()))).check(matches(isDisplayed()))
        onView(withText(rupiah(kembalian.toDouble()))).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun testInsetTransaksi() {
        val activityScenario = ActivityScenario.launch(MenuActivity::class.java)

        //click menu tambah transaksi
        onView(withId(R.id.btn_add)).perform(click())
        //fill form
        fillForm()
        //proses dan submit transaksi
        onView(withId(R.id.btn_proses)).perform(click())
        onView(withId(R.id.btn_submit_transaksi)).perform(click())
        //check perubahan data
        onView(withText(namaPelanggan)).check(matches(isDisplayed()))
        onView(withText(rupiah(hargaHaircutWash.toDouble()))).check(matches(isDisplayed()))

        activityScenario.close()
    }

    private fun fillForm(){
        onView(withId(R.id.et_nama_pelanggan)).perform(typeText(namaPelanggan))
        onView(withId(R.id.rb_menu2)).perform(click())
        onView(withText(rupiah(hargaHaircutWash.toDouble()))).check(matches(isDisplayed()))
        onView(withId(R.id.et_total_bayar)).perform(
            typeText(totalBayar.toString()),
            closeSoftKeyboard()
        )
    }

    private fun ViewInteraction.atItem(pos : Int, action : ViewAction){
        perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                pos, action
            )
        )
    }
}
