package com.example.buddyaura.ui.activity

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.buddyaura.R
import com.example.buddyaura.ui.fragment.CategoryFragment
import com.example.buddyaura.ui.fragment.HomeFragment
import com.example.buddyaura.ui.fragment.ProfileFragment
import com.example.buddyaura.ui.fragment.SharedFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import java.util.Locale

class  HomeActivity : AppCompatActivity() {

    private val VOICE_REQUEST_CODE = 1001

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var homeFragment: HomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // ---------------- Views ----------------
        drawerLayout = findViewById(R.id.drawerlayout)
        navigationView = findViewById(R.id.navigationView)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val searchInput = findViewById<EditText>(R.id.searchInput)
        val voiceIcon = findViewById<ImageView>(R.id.voiceIcon)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_popular -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, HomeFragment())
                        .commit()
                    true
                }

                R.id.nav_category -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, CategoryFragment())
                        .commit()
                    true
                }

                R.id.nav_shared -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, SharedFragment())
                        .commit()
                    true
                }

                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ProfileFragment())
                        .commit()
                    true
                }


                else -> false
            }
        }



        // ---------------- Toolbar ----------------
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // ---------------- Drawer ----------------
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open, R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // ---------------- Fragment Load ----------------
        if (savedInstanceState == null) {
            homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment)
                .commit()
        } else {
            homeFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainer) as HomeFragment
        }

        // ---------------- SEARCH LOGIC (UNCHANGED) ----------------
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                homeFragment.filterProducts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearIcon.setOnClickListener {
            searchInput.text.clear()
            clearIcon.visibility = View.GONE
            homeFragment.filterProducts("")
        }

        // ---------------- VOICE SEARCH ----------------
        voiceIcon.setOnClickListener {
            startVoiceSearch()
        }

        bottomNavigation.selectedItemId = R.id.nav_popular
    }

    private fun startVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        startActivityForResult(intent, VOICE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VOICE_REQUEST_CODE && resultCode == RESULT_OK) {
            val spokenText =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: return

            findViewById<EditText>(R.id.searchInput).setText(spokenText)
            homeFragment.filterProducts(spokenText)
        }
    }



    fun showSearchBar() {
        findViewById<View>(R.id.searchBarLayout).visibility = View.VISIBLE
    }

    fun hideSearchBar() {
        findViewById<View>(R.id.searchBarLayout).visibility = View.GONE
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }


}




