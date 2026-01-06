package com.example.buddyaura.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.buddyaura.data.Catalogue
import com.example.buddyaura.data.Category
import com.example.buddyaura.ui.activity.HomeActivity
import com.example.buddyaura.R
import com.example.buddyaura.ui.adapter.BannerAdapter
import com.example.buddyaura.ui.adapter.CatalogueAdapter
import com.example.buddyaura.ui.adapter.CategoryAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var bannerViewPager: ViewPager2
    private lateinit var bannerAdapter: BannerAdapter

    private val bannerImages = listOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3
    )

    private var bannerPosition = 0
    private val bannerHandler = Handler(Looper.getMainLooper())


    private var currentPage = 1
    private val pageSize = 3
    private var isLoading = false
    private var isLastPage = false

    private lateinit var categoryRecycler: RecyclerView
    private lateinit var productRecycler: RecyclerView
    private lateinit var productAdapter: CatalogueAdapter

    private val allProducts = mutableListOf<Catalogue>()
    private val pagedSource = mutableListOf<Catalogue>()
    private val displayProducts = mutableListOf<Catalogue>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bannerViewPager = view.findViewById(R.id.bannerViewPager)
        setupBanner()


        categoryRecycler = view.findViewById(R.id.categoryRecycler)
        productRecycler = view.findViewById(R.id.catalogueRecycler)

        setupCategories()
        setupProducts()
        loadInitialProducts()
    }

    private fun setupBanner() {
        bannerAdapter = BannerAdapter(bannerImages)
        bannerViewPager.adapter = bannerAdapter

        // Auto-scroll every 3 seconds
        bannerHandler.postDelayed(object : Runnable {
            override fun run() {
                if (bannerImages.isNotEmpty()) {
                    bannerPosition = (bannerPosition + 1) % bannerImages.size
                    bannerViewPager.setCurrentItem(bannerPosition, true)
                }
                bannerHandler.postDelayed(this, 3000)
            }
        }, 3000)
    }

    fun filterProducts(query: String) {
        val filtered = if (query.isEmpty()) {
            allProducts
        } else {
            val lower = query.lowercase()
            allProducts.filter {
                it.title.lowercase().contains(lower) ||
                        it.description.lowercase().contains(lower)
            }
        }
        resetPagination(filtered)
    }

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

    private fun setupProducts() {
        productRecycler.layoutManager = LinearLayoutManager(requireContext())
        productAdapter = CatalogueAdapter(displayProducts)
        productRecycler.adapter = productAdapter

        productRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (!isLoading && !isLastPage && dy > 0) loadNextPage()
            }
        })
    }

    private fun loadInitialProducts() {
        allProducts.addAll(listOf(
            Catalogue("Men T-Shirt", "Soft cotton casual wear", 630, R.drawable.tshirt),
            Catalogue("Women Top", "Stylish summer wear", 799, R.drawable.top),
            Catalogue("Kids Jacket", "Warm and comfortable", 999, R.drawable.jacket),
            Catalogue("Shoes", "Running shoes", 1499, R.drawable.shoes),
            Catalogue("Bag", "Travel backpack", 1299, R.drawable.bag),
            Catalogue("Watch", "Smart watch", 1999, R.drawable.watch)
        ))
        resetPagination(allProducts)
    }

    private fun resetPagination(source: List<Catalogue>) {
        currentPage = 1
        isLastPage = false
        isLoading = false
        pagedSource.clear()
        pagedSource.addAll(source)
        displayProducts.clear()
        loadNextPage()
    }

    private fun loadNextPage() {
        isLoading = true
        val start = (currentPage - 1) * pageSize
        val end = minOf(start + pageSize, pagedSource.size)
        if (start >= pagedSource.size) {
            isLastPage = true
            return
        }
        displayProducts.addAll(pagedSource.subList(start, end))
        productAdapter.notifyDataSetChanged()
        currentPage++
        isLoading = false
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).showSearchBar()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        bannerHandler.removeCallbacksAndMessages(null)
    }

}