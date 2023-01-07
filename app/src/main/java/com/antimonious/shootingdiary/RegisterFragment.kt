package com.antimonious.shootingdiary

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest

class RegisterFragment : Fragment() {
    private val db = Firebase.firestore
    private var passwordVisibility: Boolean = false
    private val hasher: MessageDigest = MessageDigest.getInstance("SHA-512")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?):
            View? {
        val view = inflater.inflate(
            R.layout.fragment_register,
            container,
            false)

        val backButton = view.findViewById<ImageButton>(R.id.registerBackButton)
        val passwordVisibilityButton = view.findViewById<Button>(R.id.registerShowHidePassword)
        val passwordInput = view.findViewById<EditText>(R.id.registerPasswordInput)
        val emailInput = view.findViewById<EditText>(R.id.registerEmailInput)
        val usernameInput = view.findViewById<EditText>(R.id.registerUsernameInput)
        val attemptRegister = view.findViewById<Button>(R.id.attemptRegisterButton)

        passwordVisibilityButton.setOnClickListener {
            passwordVisibility = !passwordVisibility

            if (passwordVisibility) {
                passwordVisibilityButton.text = getString(R.string.hidePassword)
                passwordInput.transformationMethod = null
            }
            else {
                passwordVisibilityButton.text = getString(R.string.showPassword)
                passwordInput.transformationMethod = PasswordTransformationMethod()
            }
        }

        backButton.setOnClickListener {
            val fragmentTransaction: FragmentTransaction? =
                activity
                    ?.supportFragmentManager
                    ?.beginTransaction()
            fragmentTransaction
                ?.replace(
                    R.id.fragmentContainerView,
                    SplashScreen())
            fragmentTransaction?.commit()
        }

        attemptRegister.setOnClickListener {
            val username = usernameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            var cont = true

            if (username == "") {
                Toast.makeText(
                    context,
                    getString(R.string.enterUsername),
                    Toast.LENGTH_SHORT)
                    .show()
                cont = false
            }

            if (cont && (email == "" ||
                !Patterns
                    .EMAIL_ADDRESS
                    .matcher(email)
                    .matches())) {
                Toast.makeText(
                    context,
                    getString(R.string.enterEmail),
                    Toast.LENGTH_SHORT)
                    .show()
                cont = false
            }

            if (cont && password == "") {
                Toast.makeText(
                    context,
                    getString(R.string.enterPassword),
                    Toast.LENGTH_SHORT)
                    .show()
                cont = false
            }

            if (cont && password.length < 8) {
                Toast.makeText(
                    context,
                    getString(R.string.passwordNotStrongEnough),
                    Toast.LENGTH_SHORT)
                    .show()
                cont = false
            }

            if (cont) {
                db.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnFailureListener { exception ->
                        Log.w(
                            "MainActivity",
                            "Error getting register documents",
                            exception)
                        Toast.makeText(
                            context,
                            "Exception: error getting register documents",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnSuccessListener { usernameResult ->
                        if (!usernameResult.isEmpty) {
                            Toast.makeText(
                                context,
                                getString(R.string.usernameExists),
                                Toast.LENGTH_SHORT)
                                .show()
                            cont = false
                        }

                        if (cont) {
                            db.collection("users")
                                .whereEqualTo("email", email)
                                .get()
                                .addOnFailureListener { exception ->
                                    Log.w(
                                        "MainActivity",
                                        "Error getting register documents",
                                        exception)
                                    Toast.makeText(
                                        context,
                                        "Exception: error getting register documents",
                                        Toast.LENGTH_SHORT)
                                        .show()
                                }
                                .addOnSuccessListener { emailResult ->
                                    if (!emailResult.isEmpty) {
                                        Toast.makeText(
                                            context,
                                            getString(R.string.emailExists),
                                            Toast.LENGTH_SHORT)
                                            .show()
                                        cont = false
                                    }

                                    if (cont) {
                                        val hashPass = hasher
                                            .digest(
                                                password.toByteArray())
                                            .joinToString(separator = "") { eachByte ->
                                                "%02x".format(eachByte)
                                            }

                                        val user = hashMapOf(
                                            "username" to username,
                                            "email" to email,
                                            "password" to hashPass)

                                        db.collection("users")
                                            .add(user)
                                            .addOnFailureListener { exception ->
                                                Log.w(
                                                    "MainActivity",
                                                    "Error creating user in Firestore",
                                                    exception)
                                                Toast.makeText(
                                                    context,
                                                    "Exception: Error creating user in Firestore",
                                                    Toast.LENGTH_SHORT)
                                                    .show()
                                            }
                                            .addOnSuccessListener { registerResult ->
                                                val matchListFragment = MatchListFragment()
                                                val bundle = Bundle()
                                                bundle.putString("user", registerResult.id)
                                                matchListFragment.arguments = bundle

                                                val fragmentTransaction: FragmentTransaction? =
                                                    activity
                                                        ?.supportFragmentManager
                                                        ?.beginTransaction()
                                                fragmentTransaction
                                                    ?.replace(
                                                        R.id.fragmentContainerView,
                                                        matchListFragment)
                                                fragmentTransaction?.commit()
                                            }
                                    }
                                }
                        }
                    }
            }
        }
        return view
    }
}