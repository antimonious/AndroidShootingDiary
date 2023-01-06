package com.antimonious.shootingdiary

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MatchListFragment : Fragment() {
    private val db = Firebase.firestore
    private lateinit var matchAdapter: MatchRecyclerAdapter
    private lateinit var user: String

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments?.getString("user").toString()

        matchAdapter = MatchRecyclerAdapter { matchId ->
            val matchDetailsFragment = MatchDetailsFragment()
            val bundle = Bundle()
            bundle.putString("match", matchId)
            bundle.putString("user", user)
            matchDetailsFragment.arguments = bundle

            val fragmentTransaction: FragmentTransaction? =
                activity
                    ?.supportFragmentManager
                    ?.beginTransaction()
            fragmentTransaction
                ?.replace(
                    R.id.fragmentContainerView,
                    matchDetailsFragment)
            fragmentTransaction?.commit()
        }

        db.collection("matches")
            .whereEqualTo("userId", user)
            .get()
            .addOnFailureListener { exception ->
                Log.w(
                    "MainActivity",
                    "Error getting match documents",
                    exception)
                Toast.makeText(
                    context,
                    "Exception: error getting match documents",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(
                        context,
                        getString(R.string.noMatchesForUser),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                else {
                    val matchList = ArrayList<Match>()
                    for (data in result.documents) {
                        matchList.add(
                            Match(
                                data.id,
                                data.getString("Date"),
                                data.getString("TimeSpan"),
                                data.getString("Location"),
                                data.getLong("Result"),
                                data.getLong("Inner10s"),
                                data.getLong("Temperature"),
                                data.getLong("Humidity"),
                                data.getLong("AirPressure"),
                                data.getString("Mood"),
                                data.getString("Notes")
                            )
                        )
                    }

                    matchAdapter.postItemsList(matchList)
                    matchAdapter.notifyDataSetChanged()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_match_list,
            container,
            false)

        view.findViewById<RecyclerView>(R.id.matchList).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = matchAdapter
        }

        view.findViewById<ImageButton>(R.id.menuButton).setOnClickListener {
            val menuFragment = MenuFragment()
            val bundle = Bundle()
            bundle.putString("user", user)
            menuFragment.arguments = bundle

            val fragmentTransaction: FragmentTransaction? =
                activity
                    ?.supportFragmentManager
                    ?.beginTransaction()
            fragmentTransaction
                ?.replace(
                    R.id.fragmentContainerView,
                    menuFragment)
            fragmentTransaction?.commit()
        }

        view.findViewById<ImageButton>(R.id.addMatchButton).setOnClickListener {
            //TODO: enable match adding
        }

        return view
    }
}