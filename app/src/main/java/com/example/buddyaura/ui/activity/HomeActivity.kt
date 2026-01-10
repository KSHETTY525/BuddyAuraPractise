package com.example.buddyaura.ui.activity

import android.app.AlertDialog
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
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.buddyaura.R
import com.example.buddyaura.constants.Constants
import com.example.buddyaura.receiver.CartUpdateReceiver
import com.example.buddyaura.ui.fragment.*
import com.example.buddyaura.ui.login.MainActivity
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

        updateCartBadgeFromFragment()

        cartReceiver = CartUpdateReceiver {
            updateCartBadgeFromFragment()
        }

        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                }

                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                }

                R.id.nav_cart -> {
                    loadFragment(CartFragment())
                }

                R.id.nav_logout -> {
                    showLogoutDialog()
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                applySearchToCurrentFragment(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearIcon.setOnClickListener {
            searchInput.text.clear()
            clearIcon.visibility = View.GONE
            applySearchToCurrentFragment("")
        }

        voiceIcon.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            startActivityForResult(intent, 1001)
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

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->

                val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)

                // 🔐 Clear login session
                prefs.edit()
                    .putBoolean(Constants.KEY_IS_LOGGED_IN, false)
                    .remove(Constants.KEY_USERNAME)
                    .remove(Constants.KEY_PASSWORD)
                    .apply()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun applySearchToCurrentFragment(query: String) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        when (fragment) {
            is HomeFragment -> fragment.filterProducts(query)
            is CategoryFragment -> fragment.filterProducts(query)
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val spokenText =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: return

            findViewById<EditText>(R.id.searchInput).setText(spokenText)

            val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragment is HomeFragment) {
                fragment.filterProducts(spokenText)
            }
        }
    }

}
