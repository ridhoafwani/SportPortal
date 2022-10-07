package com.sportportal.activity

import android.content.Intent
import android.os.Handler

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.sportportal.R
import com.sportportal.R.id


@Suppress("DEPRECATION")
open class AdminNavigationDrawer : AppCompatActivity(), View.OnClickListener {

    private lateinit var drawerLayout : DrawerLayout
    private lateinit var container : FrameLayout
    private val auth : FirebaseAuth get() = Login.user


    override fun setContentView(view: View?) {
        drawerLayout = layoutInflater.inflate(R.layout.activity_admin_navigation_drawer, null) as DrawerLayout
        container = drawerLayout.findViewById(id.activityContainer)
        view.removeSelf()
        container.addView(view)


        super.setContentView(drawerLayout)

        val toolbar : Toolbar = drawerLayout.findViewById(id.toolbar)
        setSupportActionBar(toolbar)

//        var navigationView : NavigationView = drawerLayout.findViewById(id.nav_view)

        val toggle = ActionBarDrawerToggle(this, drawerLayout,toolbar,
            R.string.openDrawer, R.string.closeDrawer)

        val logout : LinearLayout = drawerLayout.findViewById(id.logout_menu)
        val lapangan : LinearLayout = drawerLayout.findViewById(id.lapangan_menu)
        val profile : LinearLayout = drawerLayout.findViewById(id.profile_menu)
        val pesanan : LinearLayout = drawerLayout.findViewById(id.pesanan_menu)
        val pengguna : LinearLayout = drawerLayout.findViewById(id.pengguna_menu)
        val saldo : LinearLayout = drawerLayout.findViewById(id.saldo_menu)
        val penarikan : LinearLayout = drawerLayout.findViewById(id.penarikan_menu)

        logout.setOnClickListener(this)
        lapangan.setOnClickListener(this)
        profile.setOnClickListener(this)
        pesanan.setOnClickListener(this)
        pengguna.setOnClickListener(this)
        saldo.setOnClickListener(this)
        penarikan.setOnClickListener(this)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


    }


    override fun onClick(v: View?) {
        when (v?.id) {
            id.logout_menu -> {
                Snackbar.make(
                    drawerLayout,
                    "Logging Out",
                    Snackbar.LENGTH_SHORT
                )
                    .apply {

                        show()
                    }

                Handler().postDelayed({
                    signOut()
                }, 1500)
            }

            id.lapangan_menu -> {
                goToLapangan()
            }

            id.pesanan_menu -> {
                goToPesanan()
            }

            id.pengguna_menu -> {
                goToPengguna()
            }

            id.profile_menu ->{
                goToProfile()
            }

            id.saldo_menu -> {
                goToSaldo()
            }

            id.penarikan_menu ->{
                goToPenarikan()
            }

        }
    }

    private fun goToPengguna() {
        val intent = Intent(baseContext, AdminPengguna::class.java)
        startActivity(intent)
        drawerLayout.close()
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(baseContext, Login::class.java))
        drawerLayout.close()
    }

    private fun goToPesanan(){
        val intent = Intent(baseContext,AdminPesanan::class.java)
        startActivity(intent)
        drawerLayout.close()
    }

    private fun goToLapangan(){
        val intent = Intent(baseContext,AdminLapanganList::class.java)
        startActivity(intent)
        drawerLayout.close()
    }

    private fun goToProfile(){
        val intent = Intent(baseContext,AdminProfile::class.java)
        startActivity(intent)
        drawerLayout.close()
    }

    private fun goToSaldo(){
        val intent = Intent(baseContext,AdminSaldo::class.java)
        startActivity(intent)
        drawerLayout.close()
    }

    private fun goToPenarikan(){
        val intent = Intent(baseContext,AdminPenarikan::class.java)
        startActivity(intent)
        drawerLayout.close()
    }

    private fun View?.removeSelf() {
        this ?: return
        val parentView = parent as? ViewGroup ?: return
        parentView.removeView(this)
        println("Removed")
    }

}











