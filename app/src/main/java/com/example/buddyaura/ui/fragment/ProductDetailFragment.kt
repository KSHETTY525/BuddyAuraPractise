package com.example.buddyaura.ui.fragment

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.buddyaura.R
import com.example.buddyaura.data.CartItem
import com.example.buddyaura.receiver.CartUpdateReceiver
import com.example.buddyaura.ui.activity.HomeActivity
import com.example.buddyaura.util.BroadcastActions
import com.example.buddyaura.util.CartManager

class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {

    private var quantity = 1

    private lateinit var cartBadge: TextView
    private lateinit var cartReceiver: CartUpdateReceiver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartBadge = requireActivity().findViewById(R.id.cartBadge)
        updateCartBadge(CartManager.getItemCount())

        cartReceiver = CartUpdateReceiver { count ->
            updateCartBadge(count)
        }

        val image = view.findViewById<ImageView>(R.id.productImage)
        val name = view.findViewById<TextView>(R.id.productName)
        val price = view.findViewById<TextView>(R.id.productPrice)
        val qty = view.findViewById<TextView>(R.id.quantityText)
        val plus = view.findViewById<Button>(R.id.btnPlus)
        val minus = view.findViewById<Button>(R.id.btnMinus)
        val addToCart = view.findViewById<Button>(R.id.btnAddToCart)
        val shareBtn = view.findViewById<ImageButton>(R.id.btnShare) // ✅ SHARE

        val productName = arguments?.getString("name") ?: ""
        val productPrice = arguments?.getInt("price") ?: 0
        val imageRes = arguments?.getInt("imageRes") ?: R.drawable.blush

        image.setImageResource(imageRes)
        name.text = productName
        price.text = "₹$productPrice"
        qty.text = quantity.toString()

        plus.setOnClickListener {
            quantity++
            qty.text = quantity.toString()
        }

        minus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                qty.text = quantity.toString()
            }
        }

        addToCart.setOnClickListener {

            val item = CartItem(
                name = productName,
                price = productPrice,
                quantity = quantity,
                imageRes = imageRes
            )

            CartManager.addItem(requireContext(), item)

            (activity as? HomeActivity)?.updateCartBadgeFromFragment()

            Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show()
        }

        // ✅ SHARE BUTTON WORKING
        shareBtn.setOnClickListener {

            val shareText = """
                Check out this product 🛍️
                
                Name: $productName
                Price: ₹$productPrice
            """.trimIndent()

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)

            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
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

    private fun updateCartBadge(count: Int) {
        if (count > 0) {
            cartBadge.visibility = View.VISIBLE
            cartBadge.text = count.toString()
        } else {
            cartBadge.visibility = View.GONE
        }
    }
}
