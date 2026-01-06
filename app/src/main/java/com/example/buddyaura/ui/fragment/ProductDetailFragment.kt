package com.example.buddyaura.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.buddyaura.R

class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {

    private var quantity = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View references
        val image = view.findViewById<ImageView>(R.id.productImage)
        val name = view.findViewById<TextView>(R.id.productName)
        val desc = view.findViewById<TextView>(R.id.productDescription)
        val price = view.findViewById<TextView>(R.id.productPrice)
        val qty = view.findViewById<TextView>(R.id.quantityText)
        val plus = view.findViewById<Button>(R.id.btnPlus)
        val minus = view.findViewById<Button>(R.id.btnMinus)

        // Get data from arguments
        val imageRes = arguments?.getInt("imageRes") ?: 0
        val productName = arguments?.getString("name")
        val productDesc = arguments?.getString("description")
        val productPrice = arguments?.getInt("price") ?: 0

        // Set data
        image.setImageResource(imageRes)
        name.text = productName
        desc.text = productDesc
        price.text = "₹$productPrice"
        qty.text = quantity.toString()

        // Increase quantity
        plus.setOnClickListener {
            quantity++
            qty.text = quantity.toString()
        }

        // Decrease quantity
        minus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                qty.text = quantity.toString()
            }
        }
    }
}
