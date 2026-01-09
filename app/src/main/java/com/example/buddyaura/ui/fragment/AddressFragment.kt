package com.example.buddyaura.ui.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.buddyaura.R
import org.json.JSONArray
import java.net.URL
import kotlin.concurrent.thread

class AddressFragment : Fragment(R.layout.fragment_address) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etFirst = view.findViewById<EditText>(R.id.etFirstName)
        val etLast = view.findViewById<EditText>(R.id.etLastName)
        val etPhone = view.findViewById<EditText>(R.id.etPhone)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)

        val etHouse = view.findViewById<EditText>(R.id.etHouse)
        val etStreet = view.findViewById<EditText>(R.id.etStreet)
        val etLandmark = view.findViewById<EditText>(R.id.etLandmark)
        val etCity = view.findViewById<EditText>(R.id.etCity)
        val etState = view.findViewById<EditText>(R.id.etState)
        val etPincode = view.findViewById<EditText>(R.id.etPincode)
        val etCountry = view.findViewById<EditText>(R.id.etCountry)

        val rgType = view.findViewById<RadioGroup>(R.id.rgType)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {

            val nameRegex = Regex("^[A-Za-z]{6,}$")
            val phoneRegex = Regex("^[6-9][0-9]{9}$")
            val pincodeRegex = Regex("^[1-9][0-9]{5}$")

            when {
                etFirst.text.isEmpty() -> toast("Enter First Name")
                !etFirst.text.toString().matches(nameRegex) ->
                    toast("First name must contain only letters (min 6 chars)")

                etLast.text.isEmpty() -> toast("Enter Last Name")
                !etLast.text.toString().matches(nameRegex) ->
                    toast("Last name must contain only letters (min 2 chars)")

                etPhone.text.isEmpty() -> toast("Enter Mobile Number")
                !etPhone.text.toString().matches(phoneRegex) ->
                    toast("Enter valid 10-digit Mobile Number")


                etEmail.text.isNotEmpty() &&
                        (
                                !etEmail.text.toString().endsWith("@gmail.com") ||
                                        !android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text).matches()
                                )
                    -> toast("Enter a valid Gmail address")


                etHouse.text.isEmpty() -> toast("Enter House / Building")
                etStreet.text.isEmpty() -> toast("Enter Street / Area")
                etCity.text.isEmpty() -> toast("Enter City")
                etState.text.isEmpty() -> toast("Enter State")

                etPincode.text.isEmpty() -> toast("Enter Pincode")
                !etPincode.text.toString().matches(pincodeRegex) ->
                    toast("Enter valid 6-digit Pincode")

                etCountry.text.isEmpty() -> toast("Enter Country")

                rgType.checkedRadioButtonId == -1 ->
                    toast("Select Address Type")

                else -> {
                    toast("Address Saved Successfully!")

                    // Clear all text fields
                    etFirst.text.clear()
                    etLast.text.clear()
                    etPhone.text.clear()
                    etEmail.text.clear()

                    etHouse.text.clear()
                    etStreet.text.clear()
                    etLandmark.text.clear()
                    etCity.text.clear()
                    etState.text.clear()
                    etPincode.text.clear()
                    etCountry.text.clear()

                    // Reset Address Type
                    rgType.clearCheck()

                    // If you have "Set as Default" checkbox
                    val cbDefault = view.findViewById<CheckBox>(R.id.cbDefault)
                    cbDefault?.isChecked = false
                }
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}
