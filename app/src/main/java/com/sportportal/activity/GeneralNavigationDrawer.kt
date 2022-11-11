package com.sportportal.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.sportportal.R

open class GeneralNavigationDrawer : AppCompatActivity() , View.OnClickListener {

    private lateinit var drawerLayout : DrawerLayout
    private lateinit var container : FrameLayout

    override fun setContentView(view: View?) {
        drawerLayout = layoutInflater.inflate(R.layout.activity_general_navigation_drawer, null) as DrawerLayout
        container = drawerLayout.findViewById(R.id.activityContainer)
        view.removeSelf()
        container.addView(view)

        super.setContentView(drawerLayout)

        val toolbar : Toolbar = drawerLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawerLayout,toolbar,
            R.string.openDrawer, R.string.closeDrawer)

        val login : LinearLayout = drawerLayout.findViewById(R.id.login_menu)

        login.setOnClickListener(this)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.login_menu ->{
                login()

            }
        }
    }

    private fun login() {
        startActivity(Intent(baseContext, Login::class.java))
        drawerLayout.close()
    }

    private fun View?.removeSelf() {
        this ?: return
        val parentView = parent as? ViewGroup ?: return
        parentView.removeView(this)
    }

}