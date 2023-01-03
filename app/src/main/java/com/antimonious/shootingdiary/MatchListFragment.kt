package com.antimonious.shootingdiary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MatchListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var matchAdapter = MatchRecyclerAdapter()
        val view = inflater.inflate(R.layout.fragment_match_list, container, false)
        val user = arguments?.getString("user").toString()
        val db = Firebase.firestore

        view.findViewById<RecyclerView>(R.id.matchList).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = matchAdapter
        }



        view.findViewById<ImageButton>(R.id.menuButton).setOnClickListener {

        }

        view.findViewById<ImageButton>(R.id.addMatchButton).setOnClickListener {

        }

        return view
    }
}