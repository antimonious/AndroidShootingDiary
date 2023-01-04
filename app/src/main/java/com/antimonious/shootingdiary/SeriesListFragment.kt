package com.antimonious.shootingdiary

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SeriesListFragment : Fragment() {
    private val db = Firebase.firestore
    private lateinit var seriesAdapter: SeriesRecyclerAdapter
    private lateinit var user: String
    private lateinit var match: String

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getString("user").toString()
            match = it.getString("match").toString()
        }

        seriesAdapter = SeriesRecyclerAdapter { seriesId ->
            val seriesDetailsFragment = SeriesDetailsFragment()
            val bundle = Bundle()
            bundle.putString("series", seriesId)
            bundle.putString("match", match)
            bundle.putString("user", user)
            seriesDetailsFragment.arguments = bundle

            val fragmentTransaction: FragmentTransaction? =
                activity
                    ?.supportFragmentManager
                    ?.beginTransaction()
            fragmentTransaction
                ?.replace(
                    R.id.fragmentContainerView,
                    seriesDetailsFragment)
            fragmentTransaction?.commit()
        }

        db.collection("matches")
            .document(match)
            .collection("series")
            .get()
            .addOnFailureListener { exception ->
                Log.w(
                    "MainActivity",
                    "Error getting series documents",
                    exception)
                Toast.makeText(
                    context,
                    "Exception: error getting series documents",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(
                        context,
                        getString(R.string.noSeriesFound),
                        Toast.LENGTH_SHORT)
                        .show()
                }

                else {
                    val seriesList = ArrayList<Series>()
                    for (data in result.documents) {
                        seriesList.add(
                            Series(
                                data.id,
                                data.getLong("Result"),
                                data.getDouble("Decimal"),
                                data.getLong("Inner10s"),
                                data.getString("TimeSpan"),
                                data.getString("Notes")
                            )
                        )
                    }

                    seriesAdapter.postItemsList(seriesList)
                    seriesAdapter.notifyDataSetChanged()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_series_list,
            container,
            false)

        view.findViewById<RecyclerView>(R.id.seriesList).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = seriesAdapter
        }

        return view
    }
}