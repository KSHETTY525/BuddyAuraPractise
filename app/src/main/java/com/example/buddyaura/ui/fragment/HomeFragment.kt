package com.example.buddyaura.ui.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.buddyaura.R
import com.example.buddyaura.data.Catalogue
import com.example.buddyaura.data.Category
import com.example.buddyaura.receiver.CartUpdateReceiver
import com.example.buddyaura.receiver.OfferAlarmReceiver
import com.example.buddyaura.ui.activity.HomeActivity
import com.example.buddyaura.ui.adapter.BannerAdapter
import com.example.buddyaura.ui.adapter.CatalogueAdapter
import com.example.buddyaura.ui.adapter.CategoryAdapter
import com.example.buddyaura.util.BroadcastActions
import com.example.buddyaura.util.CartManager
import com.example.buddyaura.util.RetrofitClient
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var bannerViewPager: ViewPager2
    private lateinit var bannerIndicator: TabLayout
    private lateinit var cartBadge: TextView
    private lateinit var cartReceiver: CartUpdateReceiver
    private lateinit var categoryRecycler: RecyclerView
    private lateinit var productRecycler: RecyclerView
    private lateinit var productAdapter: CatalogueAdapter
    private val bannerImages = listOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3
    )

    private val bannerHandler = Handler(Looper.getMainLooper())
    private var bannerPosition = 0

    private var currentPage = 1
    private val pageSize = 6
    private var isLoading = false
    private var isLastPage = false
    private var currentSearch: String? = null

    private val pagedSource = mutableListOf<Catalogue>()
    private val displayProducts = mutableListOf<Catalogue>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartBadge = requireActivity().findViewById(R.id.cartBadge)
        updateCartBadge(CartManager.getItemCount())

        cartReceiver = CartUpdateReceiver { updateCartBadge(it) }

        bannerViewPager = view.findViewById(R.id.bannerViewPager)
        bannerIndicator = view.findViewById(R.id.bannerIndicator)
        categoryRecycler = view.findViewById(R.id.categoryRecycler)
        productRecycler = view.findViewById(R.id.catalogueRecycler)

        setupBanner()
        setupCategories()
        setupProducts()
        loadInitialProducts()
        scheduleOfferAlarm()



    }

    // 🔴 CART BADGE
    private fun updateCartBadge(count: Int) {
        if (count > 0) {
            cartBadge.visibility = View.VISIBLE
            cartBadge.text = count.toString()
        } else {
            cartBadge.visibility = View.GONE
        }
    }

    // 🎯 BANNER
    private fun setupBanner() {
        bannerViewPager.adapter = BannerAdapter(bannerImages)

        TabLayoutMediator(bannerIndicator, bannerViewPager) { _, _ -> }.attach()

        bannerHandler.postDelayed(object : Runnable {
            override fun run() {
                bannerPosition = (bannerPosition + 1) % bannerImages.size
                bannerViewPager.setCurrentItem(bannerPosition, true)
                bannerHandler.postDelayed(this, 3000)
            }
        }, 3000)
    }

    // 📢 OFFER ALARM
    private fun scheduleOfferAlarm() {
        val alarmManager = requireContext()
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(requireContext(), OfferAlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + 10_000

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    // 🗂️ CATEGORIES
    private fun setupCategories() {
        categoryRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        categoryRecycler.adapter = CategoryAdapter(
            listOf(
                Category("Beauty", R.drawable.beauty),
                Category("Electronic", R.drawable.electronics),
                Category("Furniture", R.drawable.furniture),
                Category("Grocery", R.drawable.grocery),
                Category("Kids", R.drawable.kids),
                Category("Jewellery", R.drawable.accessories),
                Category("Mens", R.drawable.mens),
                Category("Womens", R.drawable.womens),
                Category("Toys and Games", R.drawable.toys)
            )
        ) {}
    }

    // 🛍️ PRODUCTS
    private fun setupProducts() {
        productRecycler.layoutManager = LinearLayoutManager(requireContext())
        productAdapter = CatalogueAdapter(displayProducts)
        productRecycler.adapter = productAdapter   // ✅ MUST be here

        productRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (!isLoading && !isLastPage && dy > 0) loadNextPage()
            }
        })
    }

    private fun fetchProducts(page: Int, search: String?) {
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getHomeFeed(page, pageSize, search)

                val products = response.data.catalogues.map {
                    Catalogue(
                        title = it.catalogueName,
                        description = it.description ?: "",
                        price = it.minPrice,
                        imageUrl = it.catalogueImage.firstOrNull()
                    )
                }

                if (page == 1) {
                    pagedSource.clear()
                    displayProducts.clear()
                }

                pagedSource.addAll(products)

                requireActivity().runOnUiThread {
                    loadNextPage()
                    productAdapter.notifyDataSetChanged()
                }

                isLastPage = page >= response.data.pagination.totalPages
                currentPage++

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    private fun loadInitialProducts() {
        fetchProducts(page = 1, search = null)
    }

    // 📄 PAGINATION DISPLAY
    private fun loadNextPage() {
        val start = displayProducts.size
        val end = minOf(start + pageSize, pagedSource.size)

        if (start >= pagedSource.size) return

        displayProducts.addAll(pagedSource.subList(start, end))
        productAdapter.notifyDataSetChanged()
    }

    // 🔍 SEARCH
    fun filterProducts(query: String) {
        currentPage = 1
        currentSearch = if (query.isEmpty()) null else query
        isLastPage = false
        fetchProducts(1, currentSearch)
    }

    // 📡 CART BROADCAST
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(BroadcastActions.CART_UPDATED)

        ContextCompat.registerReceiver(
            requireActivity(),
            cartReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).showSearchBar()
        (activity as HomeActivity).updateCartBadgeFromFragment()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(cartReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bannerHandler.removeCallbacksAndMessages(null)
    }
}
