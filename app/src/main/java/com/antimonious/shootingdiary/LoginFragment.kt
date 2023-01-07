package com.antimonious.shootingdiary

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
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

class LoginFragment : Fragment() {
    private val db = Firebase.firestore
    private var passwordVisibility: Boolean = false
    private val hasher: MessageDigest = MessageDigest.getInstance("SHA-512")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?):
            View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val backButton = view.findViewById<ImageButton>(R.id.loginBackButton)
        val passwordVisibilityButton = view.findViewById<Button>(R.id.loginShowHidePassword)
        val passwordInput = view.findViewById<EditText>(R.id.loginPasswordInput)
        val attemptLogin = view.findViewById<Button>(R.id.attemptLoginButton)
        val usernameInput = view.findViewById<EditText>(R.id.loginUsernameInput)

        passwordVisibilityButton.setOnClickListener {
            passwordVisibility = !passwordVisibility

            if(passwordVisibility) {
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

        attemptLogin.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            var cont = true

            if (username == "") {
                Toast.makeText(
                    context,
                    getString(R.string.enterUsername),
                    Toast.LENGTH_SHORT
                )
                    .show()
                cont = false
            }

            if (cont && password == "") {
                Toast.makeText(
                    context,
                    getString(R.string.enterPassword),
                    Toast.LENGTH_SHORT
                )
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
                            "Error getting login documents",
                            exception)
                        Toast.makeText(
                            context,
                            "Exception: error getting login documents",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnSuccessListener { result ->
                        if (result.isEmpty) {
                            Toast.makeText(
                                context,
                                getString(R.string.usernameNotExists),
                                Toast.LENGTH_SHORT)
                                .show()
                            cont = false
                        }

                        if (cont) {
                            val data = result.documents[0]
                            val userId = data.id

                            val hashPass = hasher
                                .digest(
                                    password.toByteArray())
                                .joinToString(separator = "") { eachByte ->
                                    "%02x".format(eachByte)
                                }

                            if (hashPass != data.get("password")) {
                                Toast.makeText(
                                    context,
                                    getString(R.string.wrongPassword),
                                    Toast.LENGTH_SHORT)
                                    .show()
                                cont = false
                            }

                            if (cont) {
                                val matchListFragment = MatchListFragment()
                                val bundle = Bundle()
                                bundle.putString("user", userId)
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
        return view
    }
}