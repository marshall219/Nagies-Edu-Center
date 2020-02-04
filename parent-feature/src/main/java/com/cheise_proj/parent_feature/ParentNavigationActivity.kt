package com.cheise_proj.parent_feature

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.cheise_proj.common_module.DELAY_HANDLER
import com.cheise_proj.parent_feature.base.BaseActivity
import com.cheise_proj.parent_feature.utils.ConnectionLiveData
import com.cheise_proj.presentation.viewmodel.SharedViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_parent_navigation.*

class ParentNavigationActivity : BaseActivity() {
    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, ParentNavigationActivity::class.java)

    }

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var messageBadge: TextView
    private lateinit var circularBadge: TextView
    private lateinit var sharedViewModel: SharedViewModel
    private val textBadgeViews = arrayListOf<TextView>()
    override fun onResume() {
        super.onResume()
        setProfileData(navView)
        setBackgroundChanger()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_navigation)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val snack = Snackbar.make(root, "", Snackbar.LENGTH_INDEFINITE)
        navView = findViewById(R.id.nav_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        connectionLiveData = ConnectionLiveData(this)
        dialogUseBackgroundChanger()
        configureViewModel()
        initNavBadge()
        setupNavigation()
        openNavigationMenu()
        connectionLiveData.observe(this, Observer {
            if (!it) {
                root.background = baseContext.getDrawable(R.drawable.no_internet)
                snack.setText("No internet connection")
                snack.show()
            } else {
                root.background = null
                snack.dismiss()

            }
        })

    }

    private fun configureViewModel() {
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        sharedViewModel.getBadgeValue().observe(this, Observer {
            setNavMenuBadge(it)
        })
    }

    private fun setNavMenuBadge(badge: Pair<Int, Int?>?) {
        when (badge?.first) {
            R.id.messageFragment -> textBadgeViews[0].text = "${badge.second}+"
            R.id.circularFragment2 -> textBadgeViews[1].text = "${badge.second}+"
        }

    }

    private fun initNavBadge() {
        textBadgeViews.add( MenuItemCompat.getActionView(navView.menu.findItem(R.id.messageFragment)) as TextView)
        textBadgeViews.add( MenuItemCompat.getActionView(navView.menu.findItem(R.id.circularFragment2)) as TextView)

        textBadgeViews.forEach{
            it.apply {
                gravity = Gravity.CENTER_VERTICAL
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(ContextCompat.getColor(baseContext, android.R.color.holo_red_dark))
            }
        }

    }

    private fun openNavigationMenu() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(
            {
                drawerLayout.openDrawer(GravityCompat.START)
            }, 1500
        )
    }

    private fun dialogUseBackgroundChanger() {
        if (prefs.getFirstTimeLogin()) {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Set Auto Background Image")
            dialogBuilder.setMessage("Do you want to set auto background images ?")
            dialogBuilder.setPositiveButton("yes") { dialog, _ ->
                prefs.setBackgroundChanger(true)
                dialog.dismiss()
            }
            dialogBuilder.setNegativeButton("no") { dialog, _ ->
                prefs.setBackgroundChanger(false)
                dialog.dismiss()
            }
            prefs.setFirstTimeLogin(false)
            dialogBuilder.setCancelable(false)
            dialogBuilder.show()
        }

    }

    private fun setBackgroundChanger() {
        if (prefs.getBackgroundChanger()) {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                Glide.with(baseContext).load("https://picsum.photos/200/300/?blur")
                    .diskCacheStrategy(
                        DiskCacheStrategy.NONE
                    )
                    .into(object : CustomTarget<Drawable>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            root.background = resource
                        }
                    })
            }, DELAY_HANDLER)
        }

    }

    private fun setupNavigation() {
        navController = findNavController(R.id.fragment_socket)
        setupActionBarWithNavController(navController, drawerLayout)
        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
        NavigationUI.setupWithNavController(navView, navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> navigation.logout(this)
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}
