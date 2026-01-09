package com.example.buddyaura.ui.activity

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.buddyaura.R
import com.example.buddyaura.receiver.CartUpdateReceiver
import com.example.buddyaura.ui.fragment.*
import com.example.buddyaura.util.BroadcastActions
import com.example.buddyaura.util.CartManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var cartBadge: TextView
    private lateinit var cartReceiver: CartUpdateReceiver
    private lateinit var homeFragment: HomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val cartIcon = findViewById<ImageView>(R.id.cartIcon)
        cartIcon.setOnClickListener { openCartFragment() }

        drawerLayout = findViewById(R.id.drawerlayout)
        navigationView = findViewById(R.id.navigationView)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val searchInput = findViewById<EditText>(R.id.searchInput)
        val voiceIcon = findViewById<ImageView>(R.id.voiceIcon)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)

        cartBadge = findViewById(R.id.cartBadge)

        updateCartBadge()

        cartReceiver = CartUpdateReceiver {
            updateCartBadge()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment)
                .commit()
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_popular -> loadFragment(HomeFragment())
                R.id.nav_category -> loadFragment(CategoryFragment())
                R.id.nav_shared -> loadFragment(SharedFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
                else -> false
            }
        }

        bottomNavigation.selectedItemId = R.id.nav_popular
    }

    fun updateCartBadgeFromFragment() {
        val count = CartManager.getItemCount()

        if (count > 0) {
            cartBadge.visibility = View.VISIBLE
            cartBadge.text = count.toString()
        } else {
            cartBadge.visibility = View.GONE
        }
    }



    fun updateCartBadge() {
        val count = CartManager.getItemCount()
        if (count > 0) {
            cartBadge.visibility = View.VISIBLE
            cartBadge.text = count.toString()
        } else {
            cartBadge.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(BroadcastActions.CART_UPDATED)
        ContextCompat.registerReceiver(this, cartReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(cartReceiver)
    }

    private fun openCartFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CartFragment())
            .addToBackStack("Cart")
            .commit()
    }

    fun showSearchBar() {
        findViewById<View>(R.id.searchBarLayout).visibility = View.VISIBLE
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }
}
