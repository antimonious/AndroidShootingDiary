package com.antimonious.shootingdiary

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentTransaction

@Suppress("DEPRECATION")
class MenuFragment : Fragment() {
    private lateinit var user: String
    private var lightStatusBar: Boolean = false
    private var lightStatusUndefined: Boolean = false
    
    private fun nightCheck(view: View) {
        val nightMode = view.context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        if (nightMode == Configuration.UI_MODE_NIGHT_NO)
            lightStatusBar = true

        else if (nightMode == Configuration.UI_MODE_NIGHT_UNDEFINED)
            lightStatusUndefined = true
    }

    private fun colorChange() {
        if (!lightStatusUndefined) {
            activity?.let {
                activity?.window?.decorView?.let { it1 ->
                    WindowInsetsControllerCompat(it.window, it1)
                        .isAppearanceLightStatusBars = !lightStatusBar
                }
            }

            if (lightStatusBar) activity?.window?.statusBarColor = resources.getColor(R.color.black)
            else activity?.window?.statusBarColor = resources.getColor(R.color.offwhite)
        }
    }

    private fun colorRevert() {
        if (!lightStatusUndefined) {
            activity?.let {
                activity?.window?.decorView?.let { it1 ->
                    WindowInsetsControllerCompat(it.window, it1)
                        .isAppearanceLightStatusBars = lightStatusBar
                }
            }

            if (lightStatusBar) activity?.window?.statusBarColor =
                resources.getColor(R.color.offwhite)
            else activity?.window?.statusBarColor = resources.getColor(R.color.black)
        }
    }

    private fun transitionToMatchList() {
        val matchListFragment = MatchListFragment()
        val bundle = Bundle()
        bundle.putString("user", user)
        matchListFragment.arguments = bundle

        colorRevert()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments?.getString("user").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?):
            View? {
        val view = inflater.inflate(
            R.layout.fragment_menu,
            container,
            false)

        nightCheck(view)
        colorChange()

        view.findViewById<ImageButton>(R.id.closeMenuButton).setOnClickListener {
            transitionToMatchList()
        }

        view.findViewById<TextView>(R.id.refreshButton).setOnClickListener {
            transitionToMatchList()
        }

        view.findViewById<TextView>(R.id.signOutButton).setOnClickListener {
            colorRevert()

            val splashScreen = SplashScreen()
            val fragmentTransaction: FragmentTransaction? =
                activity
                    ?.supportFragmentManager
                    ?.beginTransaction()
            fragmentTransaction
                ?.replace(
                    R.id.fragmentContainerView,
                    splashScreen)
            fragmentTransaction?.commit()
        }

        return view
    }
}