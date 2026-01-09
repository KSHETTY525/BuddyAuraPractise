package com.example.buddyaura.ui.fragment

import android.content.IntentFilter
import android.os.Bundle
import android.view.View
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
        updateCartBadge(CartManager.getItemCount())

        cartReceiver = CartUpdateReceiver { count ->
            updateCartBadge(count)
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
        (activity as HomeActivity).updateCartBadge()
    }



    private fun updateCartBadge(count: Int) {
        if (count > 0) {
            cartBadge.visibility = View.VISIBLE
            cartBadge.text = count.toString()
        } else {
            cartBadge.visibility = View.GONE
        }
    }

    private fun loadProducts(category: String) {
        productList.clear()

        when (category) {
            "Beauty" -> productList.addAll(
                listOf(
                    Catalogue("Nivea Moisturizer", "Skin care", 349, R.drawable.nivea),
                    Catalogue("Makeup Kit", "Eyeshadow palette", 799, R.drawable.makeup),
                    Catalogue("Maybelline Mascara", "Black Bold Mascara", 349, R.drawable.mascara),
                    Catalogue("Elle 18 Kajal", "Black Smoky Kajal", 120, R.drawable.kajal),
                    Catalogue("Pilgrim Serum", "Skin care", 449, R.drawable.pilgrim),
                    Catalogue("Lip gloss", "Lip gloss", 99, R.drawable.gloss),
                    Catalogue("Maybelline cheek tint", "Cherry Blush", 679, R.drawable.blush),
                    Catalogue("Lakme Lipstick", "Matte Brown", 920, R.drawable.lipstick)
                )
            )
            "Electronic" -> productList.addAll(
                listOf(
                    Catalogue("Headphones", "Wireless", 1999, R.drawable.electronics),
                    Catalogue("Laptop", "Office use", 45999, R.drawable.electronics),
                    Catalogue("Tv", "Television ", 650, R.drawable.tv),
                    Catalogue("Pendrive", "wireless", 1000, R.drawable.pendrive),
                    Catalogue("Iron", "Home use", 2999, R.drawable.iron),
                    Catalogue("speaker", " ", 4999, R.drawable.speaker)
                )
            )
            "Furniture" -> productList.addAll(
                listOf(
                    Catalogue("Table", "Wooden table", 2999, R.drawable.electronics),
                    Catalogue("Chair", "Office use", 1999, R.drawable.electronics),
                    Catalogue("Cupboard", "Wooden table", 5999, R.drawable.cupboard),
                    Catalogue("Dinning Table", "Marble", 3999, R.drawable.dinningtable),
                    Catalogue("Sofa", "", 6999, R.drawable.sofa),
                    Catalogue("Rope Swing", "comfort", 1999, R.drawable.swing)
                )
            )
            "Grocery" -> productList.addAll(
                listOf(
                    Catalogue("Onion 1kg", "Fresh", 42, R.drawable.onion),
                    Catalogue("Potato 1kg", "Organic", 35, R.drawable.potato),
                    Catalogue("Tomato  1kg", "Fresh", 35, R.drawable.tomato),
                    Catalogue("Lady Finger 1kg", "Organic", 35, R.drawable.ladyfinger),
                    Catalogue("Spinach 1kg", "Fresh", 42, R.drawable.spinach),
                    Catalogue("Capsicum 1kg", "Organic", 35, R.drawable.capsicum)
                )
            )
            "Womens" -> productList.addAll(
                listOf(
                    Catalogue("Skirt", "Red Leather Skirt", 799, R.drawable.skirt),
                    Catalogue("Red Top", "Red cube design top", 399, R.drawable.top_women)
                )
            )
        }

        productAdapter.notifyDataSetChanged()
    }
}
