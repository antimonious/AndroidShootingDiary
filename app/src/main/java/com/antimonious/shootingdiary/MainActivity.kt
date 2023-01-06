package com.antimonious.shootingdiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        for (fragment in this.supportFragmentManager.fragments) {
            if (fragment.isVisible) {
                when (fragment) {
                    is LoginFragment -> findViewById<ImageButton>(R.id.loginBackButton).performClick()
                    is RegisterFragment -> findViewById<ImageButton>(R.id.registerBackButton).performClick()
                    is MatchDetailsFragment -> findViewById<ImageButton>(R.id.matchDetailsBackButton).performClick()
                    is SeriesDetailsFragment -> findViewById<ImageButton>(R.id.seriesDetailsBackButton).performClick()
                    is SeriesListFragment -> findViewById<ImageButton>(R.id.seriesListBackButton).performClick()
                    is MenuFragment -> findViewById<ImageButton>(R.id.closeMenuButton).performClick()
                    else -> super.onBackPressed()
                }
                break
            }
        }
    }
}