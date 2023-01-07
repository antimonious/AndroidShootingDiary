package com.antimonious.shootingdiary

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MatchDetailsFragment : Fragment() {
    private val db = Firebase.firestore
    private lateinit var matchId: String
    private lateinit var user: String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            matchId = it.getString("match").toString()
            user = it.getString("user").toString()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?):
            View? {
        val view = inflater.inflate(
            R.layout.fragment_match_details,
            container,
            false)

        val backButton = view.findViewById<ImageButton>(R.id.matchDetailsBackButton)

        db.collection("matches")
            .document(matchId)
            .get()
            .addOnFailureListener { exception ->
                Log.w(
                    "MainActivity",
                    "Error getting match details",
                    exception)
                Toast.makeText(
                    context,
                    "Exception: error getting match details",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    val match = Match(
                        result.id,
                        result.getString("Date"),
                        result.getString("StartTime"),
                        result.getString("EndTime"),
                        result.getString("Location"),
                        result.getLong("Result"),
                        result.getLong("Inner10s"),
                        result.getLong("Temperature"),
                        result.getLong("Humidity"),
                        result.getLong("AirPressure"),
                        result.getString("Mood"),
                        result.getString("Notes")
                    )

                    view.findViewById<TextView>(R.id.matchDetailsDateView).text =
                        match.Date

                    view.findViewById<TextView>(R.id.matchDetailsTimeSpanView).text =
                        "${match.StartTime} - ${match.EndTime}"

                    view.findViewById<TextView>(R.id.matchDetailsLocationView).text =
                        match.Location

                    view.findViewById<TextView>(R.id.matchDetailsScoreView).text =
                        match.Result.toString()

                    view.findViewById<TextView>(R.id.matchDetailsInner10sView).text =
                        match.Inner10s.toString()

                    view.findViewById<TextView>(R.id.matchDetailsTemperatureView).text =
                        match.Temperature.toString()

                    view.findViewById<TextView>(R.id.matchDetailsHumidityView).text =
                        match.Humidity.toString()

                    view.findViewById<TextView>(R.id.matchDetailsAirPressureView).text =
                        match.AirPressure.toString()

                    view.findViewById<TextView>(R.id.matchDetailsMoodView).text =
                        match.Mood

                    view.findViewById<TextView>(R.id.matchDetailsNotesView).text =
                        match.Notes
                }

                else {
                    Toast.makeText(
                        context,
                        getString(R.string.matchNotFound),
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }

        backButton.setOnClickListener {
            val matchListFragment = MatchListFragment()
            val bundle = Bundle()
            bundle.putString("user", user)
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

        view.findViewById<ImageButton>(R.id.matchDetailsEditButton).setOnClickListener {
            val editMatchFragment = EditMatchFragment()
            val bundle = Bundle()
            bundle.putString("user", user)
            bundle.putString("match", matchId)
            editMatchFragment.arguments = bundle

            val fragmentTransaction: FragmentTransaction? =
                activity
                    ?.supportFragmentManager
                    ?.beginTransaction()
            fragmentTransaction
                ?.replace(
                    R.id.fragmentContainerView,
                    editMatchFragment)
            fragmentTransaction?.commit()
        }

        view.findViewById<Button>(R.id.reviewSeriesButton).setOnClickListener {
            val seriesListFragment = SeriesListFragment()
            val bundle = Bundle()
            bundle.putString("match", matchId)
            bundle.putString("user", user)
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

        view.findViewById<Button>(R.id.deleteMatchButton).setOnClickListener {
            db.collection("matches")
                .document(matchId)
                .delete()
                .addOnFailureListener { exception ->
                    Log.w(
                        "MainActivity",
                        "Error deleting a match",
                        exception)
                    Toast.makeText(
                        context,
                        "Exception: error deleting a match",
                        Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnSuccessListener {
                    Toast.makeText(
                        context,
                        getString(R.string.matchDeleteSuccess),
                        Toast.LENGTH_SHORT)
                        .show()
                    backButton.performClick()
                }
        }

        return view
    }
}