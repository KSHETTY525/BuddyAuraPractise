package com.example.buddyaura.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buddyaura.R
import com.example.buddyaura.receiver.CartUpdateReceiver
import com.example.buddyaura.ui.activity.HomeActivity
import com.example.buddyaura.ui.adapter.CartAdapter
import com.example.buddyaura.util.BroadcastActions
import com.example.buddyaura.util.CartManager

class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var cartCount: TextView
    private lateinit var totalPriceText: TextView
    private lateinit var proceedBtn: Button
    private lateinit var removeSelectedBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartAdapter
    private lateinit var cartReceiver: CartUpdateReceiver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartCount = view.findViewById(R.id.cartCount)
        totalPriceText = view.findViewById(R.id.totalPriceText)
        proceedBtn = view.findViewById(R.id.btnProceed)
        removeSelectedBtn = view.findViewById(R.id.btnRemoveSelected)
        recyclerView = view.findViewById(R.id.cartRecycler)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        updateCartUI()

        cartReceiver = CartUpdateReceiver {
            updateCartUI()
        }

        proceedBtn.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Proceeding to buy (Total ₹${CartManager.getTotalPrice()})",
                Toast.LENGTH_SHORT
            ).show()
        }

        proceedBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AddressFragment())
                .addToBackStack(null)
                .commit()
        }


        removeSelectedBtn.setOnClickListener {

            val selectedCount = CartManager.getItems().count { it.isSelected }

            if (selectedCount == 0) {
                Toast.makeText(requireContext(), "No products selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔔 Confirmation Dialog
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes") { _, _ ->
                    CartManager.removeSelectedItems(requireContext())
                    updateCartUI()

                    // 🔥 Update cart badge instantly
                    (activity as? HomeActivity)?.updateCartBadgeFromFragment()

                    Toast.makeText(requireContext(), "Products removed", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun updateCartUI() {
        val items = CartManager.getItems()

        cartCount.text = "Items: ${items.size}"

        adapter = CartAdapter(items) {
            updateSummary()
        }

        recyclerView.adapter = adapter

        proceedBtn.isEnabled = items.isNotEmpty()
        updateSummary()
    }



    // ✅ MOVE THIS FUNCTION HERE ⬇⬇⬇
    private fun updateSummary() {
        val items = CartManager.getItems()
        val selectedCount = items.count { it.isSelected }

        totalPriceText.text =
            if (selectedCount > 0)
                "Total: ₹${CartManager.getSelectedTotalPrice()}"
            else
                "Total: ₹${CartManager.getTotalPrice()}"

        removeSelectedBtn.isEnabled = selectedCount > 0
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


}
