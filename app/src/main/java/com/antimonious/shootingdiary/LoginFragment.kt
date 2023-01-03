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
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest

class LoginFragment : Fragment() {

    private var passwordVisibility: Boolean = false
    private val hasher: MessageDigest = MessageDigest.getInstance("SHA-512")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

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

        attemptLogin.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if(username == "") {
                Toast.makeText(context, "Please enter a username", Toast.LENGTH_SHORT).show()
            }

            else {
                if (password == "") {
                    Toast.makeText(context, "Please enter a password", Toast.LENGTH_SHORT)
                        .show()
                }

                else {
                    val db = Firebase.firestore

                    db.collection("users")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnFailureListener { exception ->
                            Log.w("MainActivity", "Error getting login documents",
                                exception)
                            Toast.makeText(
                                context, "Exception: error getting login documents",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnSuccessListener { result ->
                            if (result.isEmpty) {
                                Toast.makeText(
                                    context, "No user with such username exists",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else {
                                val data = result.documents[0]
                                val userId = data.id

                                val hashPass = hasher.digest(password.toByteArray())
                                    .joinToString(separator = "") {
                                            eachByte -> "%02x".format(eachByte)
                                    }

                                if (hashPass != data.get("password")) {
                                    Toast.makeText(
                                        context,
                                        "Incorrect password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else {
                                    val matchListFragment = MatchListFragment()
                                    val bundle = Bundle()
                                    bundle.putString("user", userId)
                                    matchListFragment.arguments = bundle

                                    val fragmentTransaction: FragmentTransaction? =
                                        activity?.supportFragmentManager?.beginTransaction()
                                    fragmentTransaction?.replace(
                                        R.id.fragmentContainerView,
                                        matchListFragment
                                    )
                                    fragmentTransaction?.commit()
                                }
                            }
                        }
                }
            }
        }
        return view
    }
}