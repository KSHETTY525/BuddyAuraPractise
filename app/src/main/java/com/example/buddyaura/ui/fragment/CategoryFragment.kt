package com.example.buddyaura.ui.fragment

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buddyaura.R
import com.example.buddyaura.data.Catalogue
import com.example.buddyaura.data.Category
import com.example.buddyaura.receiver.CartUpdateReceiver
import com.example.buddyaura.ui.activity.HomeActivity
import com.example.buddyaura.ui.adapter.CategoryAdapter
import com.example.buddyaura.ui.adapter.CategoryProductAdapter
import com.example.buddyaura.util.BroadcastActions
import com.example.buddyaura.util.CartManager

class CategoryFragment : Fragment(R.layout.fragment_category) {

    private lateinit var categoryRecycler: RecyclerView
    private lateinit var productRecycler: RecyclerView
    private lateinit var productAdapter: CategoryProductAdapter
    private lateinit var cartBadge: TextView
    private lateinit var cartReceiver: CartUpdateReceiver

    // 🔹 Master list (always full data)
    private val allProducts = mutableListOf<Catalogue>()

    // 🔹 Display list (filtered)
    private val productList = mutableListOf<Catalogue>()

    private val categories = listOf(
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartBadge = requireActivity().findViewById(R.id.cartBadge)
        updateCartBadgeFromFragment(CartManager.getItemCount())

        cartReceiver = CartUpdateReceiver { count ->
            updateCartBadgeFromFragment(count)
        }

        view.findViewById<TextView>(R.id.btnSort).setOnClickListener {
            showSortBottomSheet()
        }

        categoryRecycler = view.findViewById(R.id.leftCategoryRecycler)
        productRecycler = view.findViewById(R.id.productGridRecycler)

        categoryRecycler.layoutManager = LinearLayoutManager(requireContext())
        categoryRecycler.adapter = CategoryAdapter(categories) { category ->
            loadProducts(category.name)
        }

        productRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        productAdapter = CategoryProductAdapter(productList)
        productRecycler.adapter = productAdapter

        // Load first category by default
        loadProducts(categories.first().name)
    }

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

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(cartReceiver)
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).showSearchBar()
        (activity as HomeActivity).updateCartBadgeFromFragment()
    }

    private fun showSortBottomSheet() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_sort, null)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroupSort)
        val lowToHigh = view.findViewById<RadioButton>(R.id.rbLowToHigh)
        val highToLow = view.findViewById<RadioButton>(R.id.rbHighToLow)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbLowToHigh -> {
                    sortProductsLowToHigh()
                    dialog.dismiss()
                }

                R.id.rbHighToLow -> {
                    sortProductsHighToLow()
                    dialog.dismiss()
                }
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun sortProductsLowToHigh() {
        val sorted = productList.sortedBy { it.price }
        productAdapter.updateList(sorted)
    }

    private fun sortProductsHighToLow() {
        val sorted = productList.sortedByDescending { it.price }
        productAdapter.updateList(sorted)
    }

    // 🔴 Cart Badge Update
    private fun updateCartBadgeFromFragment(count: Int) {
        if (count > 0) {
            cartBadge.visibility = View.VISIBLE
            cartBadge.text = count.toString()
        } else {
            cartBadge.visibility = View.GONE
        }
    }

    // 🛒 Load products per category
    private fun loadProducts(category: String) {
        Log.d("CATEGORY", "Products loaded: ${productList.size}")

        allProducts.clear()
        productList.clear()

        val items = when (category) {
            "Beauty" -> listOf(
                Catalogue(title = "Nivea Moisturizer", description = "Skin care", price = 349, imageRes = R.drawable.nivea),
                Catalogue(title="Makeup Kit", description = "Eyeshadow palette", price=799, imageRes=R.drawable.makeup),
                Catalogue(title="Maybelline Mascara", description="Black Bold Mascara", price=349, imageRes = R.drawable.mascara),
                Catalogue(title = "Elle 18 Kajal", description = "Black Smoky Kajal", price = 120, imageRes = R.drawable.kajal),
                Catalogue(title = "Pilgrim Serum", description = "Skin care", price = 449, imageRes = R.drawable.pilgrim),
                Catalogue(title = "Lip gloss", description = "Lip gloss", price = 99, imageRes = R.drawable.gloss),
                Catalogue(title = "Maybelline cheek tint", description = "Cherry Blush", price = 679, imageRes = R.drawable.blush),
                Catalogue(title = "Lakme Lipstick", description = "Matte Brown", price = 920, imageRes = R.drawable.lipstick)
            )

            "Electronic" -> listOf(
                Catalogue(title = "Headphones", description = "Wireless", price = 1999, imageRes = R.drawable.electronics),
                Catalogue(title = "Laptop", description = "Office use", price = 45999, imageRes = R.drawable.electronics),
                Catalogue(title = "Tv", description = "Television ", price = 650, imageRes = R.drawable.tv),
                Catalogue(title = "Pendrive", description = "wireless", price = 1000, imageRes = R.drawable.pendrive),
                Catalogue(title = "Iron", description = "Home use", price = 2999, imageRes = R.drawable.iron),
                Catalogue(title = "Speaker", description = "Wireless", 4999, imageRes = R.drawable.speaker)
            )

            "Furniture" -> listOf(
                Catalogue(title = "Table", description = "Wooden table", price = 2999, imageRes = R.drawable.electronics),
                Catalogue(title = "Chair", description = "Office use", price = 1999, imageRes = R.drawable.electronics),
                Catalogue(title = "Cupboard", description = "Wooden table", price = 5999, imageRes = R.drawable.cupboard),
                Catalogue(title = "Dinning Table", description = "Marble", price = 3999, imageRes = R.drawable.dinningtable),
                Catalogue(title = "Sofa", description = "", price = 6999, imageRes = R.drawable.sofa),
                Catalogue(title = "Rope Swing", description = "Comfort", price = 1999, imageRes = R.drawable.swing)
            )

            "Grocery" -> listOf(
                Catalogue(title = "Onion 1kg", description = "Fresh", price = 42, imageRes = R.drawable.onion),
                Catalogue(title = "Potato 1kg", description = "Organic", price = 35, imageRes = R.drawable.potato),
                Catalogue(title = "Tomato 1kg", description = "Fresh", price = 35, imageRes = R.drawable.tomato),
                Catalogue(title = "Lady Finger 1kg", description = "Organic", price = 35, imageRes = R.drawable.ladyfinger),
                Catalogue(title = "Spinach 1kg", description = "Fresh", price = 42, imageRes = R.drawable.spinach),
                Catalogue(title = "Capsicum 1kg", description = "Organic", price = 35, imageRes = R.drawable.capsicum)
            )

            "Womens" -> listOf(
                Catalogue(title = "Skirt", description = "Red Leather Skirt", price = 799, imageRes = R.drawable.skirt),
                Catalogue(title = "Red Top", description = "Red cube design top", price = 399, imageRes = R.drawable.top_women)
            )

            else -> emptyList()
        }

        allProducts.addAll(items)
        productList.addAll(items)
        productAdapter.notifyDataSetChanged()
    }

    // 🔍 Search + Clear Logic (FIXED)
    fun filterProducts(query: String) {
        val filtered = if (query.isEmpty()) {
            allProducts
        } else {
            allProducts.filter {
                it.title.contains(query, true)
            }
        }
        productAdapter.updateList(filtered)
    }
}
