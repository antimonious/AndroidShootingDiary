package com.antimonious.shootingdiary

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddSeriesFragment : Fragment() {
    private val db = Firebase.firestore
    private lateinit var seriesAdapter: AddSeriesRecyclerAdapter
    private lateinit var user: String
    private lateinit var matchId: String
    private var counter: Int = 0
    private lateinit var series: ArrayList<Series>

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            counter = it.getInt("counter")
            matchId = it.getString("match").toString()
            user = it.getString("user").toString()
        }

        seriesAdapter = AddSeriesRecyclerAdapter()
        series = ArrayList()
        var i = counter

        while (i < counter + 2) {
            series.add(
                Series(
                    "${i+1}",
                    "0".toLong(),
                    "0.0".toDouble(),
                    "0".toLong(),
                    "",
                    "",
                    ""
                ))
            i++
        }

        seriesAdapter.postItemsList(series)
        seriesAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_add_series,
            container,
            false)

        val seriesList = view.findViewById<RecyclerView>(R.id.addSeriesList)

        seriesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = seriesAdapter
        }

        view.findViewById<ImageButton>(R.id.addSeriesDeleteButton).setOnClickListener {
            db.collection("matches")
                .document(matchId)
                .delete()
                .addOnFailureListener { exception ->
                    Log.w(
                        "MainActivity",
                        "Error deleting match in Firestore",
                        exception)
                    Toast.makeText(
                        context,
                        "Exception: Error deleting match in Firestore",
                        Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnSuccessListener {
                    Toast.makeText(
                        context,
                        getString(R.string.matchDeleteSuccess),
                        Toast.LENGTH_SHORT)
                        .show()

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
        }

        view.findViewById<ImageButton>(R.id.addSeriesSaveButton).setOnClickListener {
            var i = counter
            var cont = true

            while (i < counter + 2) {
                val recyclerItem = seriesList.layoutManager?.getChildAt(i - counter)

                val result = recyclerItem
                    ?.findViewById<EditText>(
                        R.id.addSeriesResultInput)
                    ?.text.toString()
                val decimal = recyclerItem
                    ?.findViewById<EditText>(
                        R.id.addSeriesDecimalInput)
                    ?.text.toString()
                val inner10s = recyclerItem
                    ?.findViewById<EditText>(
                        R.id.addSeriesInner10sInput)
                    ?.text.toString()
                val startTime = recyclerItem
                    ?.findViewById<EditText>(
                        R.id.addSeriesStartTimeInput)
                    ?.text.toString()
                val endTime = recyclerItem
                    ?.findViewById<EditText>(
                        R.id.addSeriesEndTimeInput)
                    ?.text.toString()
                val notes = recyclerItem
                    ?.findViewById<EditText>(
                        R.id.addSeriesNotesInput)
                    ?.text.toString()

                val checkStrings = ArrayList<String>()
                checkStrings.add(result)
                checkStrings.add(decimal)
                checkStrings.add(inner10s)
                checkStrings.add(startTime)
                checkStrings.add(endTime)

                for (string in checkStrings) {
                    if (string == "") {
                        Toast.makeText(
                            context,
                            "Series ${i+1}: ${getString(R.string.emptyWarning)}",
                            Toast.LENGTH_SHORT)
                            .show()
                        cont = false
                        break
                    }
                }

                if (cont && (result.toLong() < 0 || result.toLong() > 100)) {
                    Toast.makeText(
                        context,
                        "Series ${i+1}: ${getString(R.string.seriesResultRangeWarning)}",
                        Toast.LENGTH_SHORT)
                        .show()
                    cont = false
                    break
                }

                if (cont && (decimal.toDouble() < 0 || decimal.toDouble() > 109)) {
                    Toast.makeText(
                        context,
                        "Series ${i+1}: ${getString(R.string.seriesDecimalRangeWarning)}",
                        Toast.LENGTH_SHORT)
                        .show()
                    cont = false
                    break
                }

                if (cont && (inner10s.toLong() < 0 || inner10s.toLong() > 10)) {
                    Toast.makeText(
                        context,
                        "Series ${i+1}: ${getString(R.string.seriesInner10Warning)}",
                        Toast.LENGTH_SHORT)
                        .show()
                    cont = false
                    break
                }

                if (cont) {
                    series[i - counter].Result = result.toLong()
                    series[i - counter].Decimal = decimal.toDouble()
                    series[i - counter].Inner10s = inner10s.toLong()
                    series[i - counter].StartTime = startTime
                    series[i - counter].EndTime = endTime
                    series[i - counter].Notes = notes
                }

                i++
            }

            if (cont) {
                i = counter

                var seriesHash = hashMapOf(
                    "Result" to series[i - counter].Result,
                    "Decimal" to series[i - counter].Decimal,
                    "Inner10s" to series[i - counter].Inner10s,
                    "StartTime" to series[i - counter].StartTime,
                    "EndTime" to series[i - counter].EndTime,
                    "Notes" to series[i - counter].Notes
                )

                db.collection("matches")
                    .document(matchId)
                    .collection("series")
                    .document("${i + 1}")
                    .set(seriesHash)
                    .addOnFailureListener { exception ->
                        Log.w(
                            "MainActivity",
                            "Error adding series ${i + 1} in Firestore",
                            exception
                        )
                        Toast.makeText(
                            context,
                            "Exception: Error adding series ${i + 1} in Firestore",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                i++

                seriesHash = hashMapOf(
                    "Result" to series[i - counter].Result,
                    "Decimal" to series[i - counter].Decimal,
                    "Inner10s" to series[i - counter].Inner10s,
                    "StartTime" to series[i - counter].StartTime,
                    "EndTime" to series[i - counter].EndTime,
                    "Notes" to series[i - counter].Notes
                )

                db.collection("matches")
                    .document(matchId)
                    .collection("series")
                    .document("${i + 1}")
                    .set(seriesHash)
                    .addOnFailureListener { exception ->
                        Log.w(
                            "MainActivity",
                            "Error adding series ${i + 1} in Firestore",
                            exception
                        )
                        Toast.makeText(
                            context,
                            "Exception: Error adding series ${i + 1} in Firestore",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                i++
                counter += 2

                if (counter == 6){
                    Toast.makeText(
                        context,
                        getString(R.string.matchAdded),
                        Toast.LENGTH_SHORT)
                        .show()

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
                            seriesListFragment
                        )
                    fragmentTransaction?.commit()
                }

                else {
                    val addSeriesFragment = AddSeriesFragment()
                    val bundle = Bundle()
                    bundle.putInt("counter", counter)
                    bundle.putString("match", matchId)
                    bundle.putString("user", user)
                    addSeriesFragment.arguments = bundle

                    val fragmentTransaction: FragmentTransaction? =
                        activity
                            ?.supportFragmentManager
                            ?.beginTransaction()
                    fragmentTransaction
                        ?.replace(
                            R.id.fragmentContainerView,
                            addSeriesFragment)
                    fragmentTransaction?.commit()
                }
            }
        }

        return view
    }
}