package com.example.buddyaura.ui.fragment

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.buddyaura.R
import com.example.buddyaura.data.CartItem
import com.example.buddyaura.data.Catalogue
import com.example.buddyaura.receiver.CartUpdateReceiver
import com.example.buddyaura.ui.activity.HomeActivity
import com.example.buddyaura.util.BroadcastActions
import com.example.buddyaura.util.CartManager
import com.example.buddyaura.util.WishlistManager
import java.io.File
import java.io.FileOutputStream

class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {

    private var quantity = 1
    private lateinit var cartBadge: TextView
    private lateinit var cartReceiver: CartUpdateReceiver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cart badge
        cartBadge = requireActivity().findViewById(R.id.cartBadge)
        updateCartBadge(CartManager.getItemCount())

        cartReceiver = CartUpdateReceiver { count ->
            updateCartBadge(count)
        }

        // Views
        val productImage = view.findViewById<ImageView>(R.id.productImage)
        val name = view.findViewById<TextView>(R.id.productName)
        val price = view.findViewById<TextView>(R.id.productPrice)
        val originalPrice = view.findViewById<TextView>(R.id.productOriginalPrice)
        val offText = view.findViewById<TextView>(R.id.productOff)
        val productDescription = view.findViewById<TextView>(R.id.productDescription)

        val qty = view.findViewById<TextView>(R.id.quantityText)
        val plus = view.findViewById<Button>(R.id.btnPlus)
        val minus = view.findViewById<Button>(R.id.btnMinus)
        val addToCart = view.findViewById<Button>(R.id.btnAddToCart)
        val shareBtn = view.findViewById<ImageButton>(R.id.btnShare)
        val wishlistBtn = view.findViewById<ImageView>(R.id.btnWishlist)
        val backBtn = view.findViewById<ImageButton>(R.id.btnBack)

        // Arguments
        val productName = arguments?.getString("name") ?: ""
        val productDescText = arguments?.getString("productDescription") ?: ""
        val productPriceValue = arguments?.getInt("price") ?: 0
        val imageRes = arguments?.getInt("imageRes") ?: R.drawable.blush

        // Discount
        val discountPercent = 20
        val discountAmount = (productPriceValue * discountPercent) / 100
        val finalPrice = productPriceValue - discountAmount

        // UI setup
        productImage.setImageResource(imageRes)
        name.text = productName
        productDescription.text = productDescText
        price.text = "₹$finalPrice"
        originalPrice.text = "₹$productPriceValue"
        originalPrice.paintFlags =
            originalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        offText.text = "$discountPercent% OFF"
        qty.text = quantity.toString()

        // Quantity
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

        // 🔥 CURRENT PRODUCT (used for wishlist)
        val currentProduct = Catalogue(
            title = productName,
            description = productDescText,
            price = productPriceValue,
            imageRes = imageRes,
            imageUrl = null
        )

        // ❤️ Wishlist initial state
        wishlistBtn.setImageResource(
            if (WishlistManager.isWishlisted(currentProduct))
                R.drawable.ic_heart_filled
            else
                R.drawable.ic_heart_outline
        )

        // ❤️ Wishlist click
        wishlistBtn.setOnClickListener {
            if (WishlistManager.isWishlisted(currentProduct)) {
                WishlistManager.remove(currentProduct)
                wishlistBtn.setImageResource(R.drawable.ic_heart_outline)
                Toast.makeText(requireContext(), "Removed from Wishlist", Toast.LENGTH_SHORT).show()
            } else {
                WishlistManager.add(currentProduct)
                wishlistBtn.setImageResource(R.drawable.ic_heart_filled)
                Toast.makeText(requireContext(), "Added to Wishlist ❤️", Toast.LENGTH_SHORT).show()
            }
        }

        // 🛒 Add to cart
        addToCart.setOnClickListener {
            val item = CartItem(
                name = productName,
                description = productDescText,
                price = finalPrice,
                quantity = quantity,
                imageRes = imageRes
            )
            CartManager.addItem(requireContext(), item)
            (activity as? HomeActivity)?.updateCartBadgeFromFragment()
            Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show()
        }

        // 🔗 Share
        shareBtn.setOnClickListener {

            val shareText = """
                Check out this product 🛍️

                $productName
                $productDescText

                Price: ₹$finalPrice
                (20% OFF — Original ₹$productPriceValue)
            """.trimIndent()

            val bitmap = (productImage.drawable as BitmapDrawable).bitmap
            val file = File(requireContext().cacheDir, "product.png")

            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            val imageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        // 🔙 Back
        backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
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
