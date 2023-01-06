package com.antimonious.shootingdiary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SeriesDetailsFragment : Fragment() {
    private val db = Firebase.firestore
    private lateinit var matchId: String
    private lateinit var seriesId: String
    private lateinit var user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            matchId = it.getString("match").toString()
            seriesId = it.getString("series").toString()
            user = it.getString("user").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_series_details,
            container,
            false)

        db.collection("matches")
            .document(matchId)
            .collection("series")
            .document(seriesId)
            .get()
            .addOnFailureListener { exception ->
                Log.w(
                    "MainActivity",
                    "Error getting series details",
                    exception)
                Toast.makeText(
                    context,
                    "Exception: error getting series details",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    val series = Series(
                        result.id,
                        result.getLong("Result"),
                        result.getDouble("Decimal"),
                        result.getLong("Inner10s"),
                        result.getString("TimeSpan"),
                        result.getString("Notes")
                    )

                    view.findViewById<TextView>(R.id.seriesDetailsResultView).text =
                        series.Result.toString()
                    view.findViewById<TextView>(R.id.seriesDetailsDecimalResultView).text =
                        series.Decimal.toString()
                    view.findViewById<TextView>(R.id.seriesDetailsInner10sView).text =
                        series.Inner10s.toString()
                    view.findViewById<TextView>(R.id.seriesDetailsTimeView).text =
                        series.TimeSpan
                    view.findViewById<TextView>(R.id.seriesDetailsNotesView).text =
                        series.Notes
                }

                else {
                    Toast.makeText(
                        context,
                        getString(R.string.seriesNotFound),
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }

        view.findViewById<ImageButton>(R.id.seriesDetailsBackButton).setOnClickListener {
            val seriesListFragment = SeriesListFragment()
            val bundle = Bundle()
            bundle.putString("user", user)
            bundle.putString("match", matchId)
            seriesListFragment.arguments = bundle

            val fragmentTransaction: FragmentTransaction? =
                activity
                    ?.supportFragmentManager
                    ?.beginTransaction()
            fragmentTransaction
                ?.replace(
                    R.id.fragmentContainerView,
                    seriesListFragment)
            fragmentTransaction?.commit()
        }

        view.findViewById<ImageButton>(R.id.seriesDetailsEditButton).setOnClickListener {
            //TODO: enable series editing
        }

        return view
    }


}