package com.example.buddyaura.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buddyaura.R
import com.example.buddyaura.ui.adapter.CategoryProductAdapter
import com.example.buddyaura.util.WishlistManager

class WishlistFragment : Fragment(R.layout.fragment_wishlist) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.wishlistRecycler)
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        recycler.adapter =
            CategoryProductAdapter(WishlistManager.getWishlist().toMutableList())
    }
}
