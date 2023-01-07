package com.antimonious.shootingdiary

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditSeriesFragment : Fragment() {
    private val db = Firebase.firestore
    private lateinit var matchId: String
    private lateinit var seriesId: String
    private lateinit var user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            matchId = it.getString("matchId").toString()
            seriesId = it.getString("seriesId").toString()
            user = it.getString("user").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_edit_series,
            container,
            false)

        val backButton = view.findViewById<ImageButton>(R.id.editSeriesBackButton)

        val resultInput = view.findViewById<EditText>(R.id.editSeriesResultInput)
        val decimalInput = view.findViewById<EditText>(R.id.editSeriesDecimalInput)
        val inner10sInput = view.findViewById<EditText>(R.id.editSeriesInner10sInput)
        val startTimeInput = view.findViewById<EditText>(R.id.editSeriesStartTimeInput)
        val endTimeInput = view.findViewById<EditText>(R.id.editSeriesEndTimeInput)
        val notesInput = view.findViewById<EditText>(R.id.editSeriesNotesInput)

        db.collection("matches")
            .document(matchId)
            .collection("series")
            .document(seriesId)
            .get()
            .addOnFailureListener { exception ->
                Log.w(
                    "MainActivity",
                    "Error getting series details",
                    exception
                )
                Toast.makeText(
                    context,
                    "Exception: error getting series details",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    val series = Series(
                        result.id,
                        result.getLong("Result"),
                        result.getDouble("Decimal"),
                        result.getLong("Inner10s"),
                        result.getString("StartTime"),
                        result.getString("EndTime"),
                        result.getString("Notes"))

                    val editableFactory = Editable.Factory.getInstance()

                    resultInput.text =
                        editableFactory.newEditable(series.Result.toString())

                    decimalInput.text =
                        editableFactory.newEditable(series.Decimal.toString())

                    inner10sInput.text =
                        editableFactory.newEditable(series.Inner10s.toString())

                    startTimeInput.text =
                        editableFactory.newEditable(series.StartTime)

                    endTimeInput.text =
                        editableFactory.newEditable(series.EndTime)

                    notesInput.text =
                        editableFactory.newEditable(series.Notes)
                }

                else {
                    Toast.makeText(
                        context,
                        getString(R.string.seriesNotFound),
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }

        backButton.setOnClickListener {
            val seriesDetailsFragment = SeriesDetailsFragment()
            val bundle = Bundle()
            bundle.putString("series", seriesId)
            bundle.putString("match", matchId)
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

        view.findViewById<ImageButton>(R.id.editSeriesSaveButton).setOnClickListener {
            val result = resultInput.text.toString()
            val decimal = decimalInput.text.toString()
            val inner10s = inner10sInput.text.toString()
            val startTime = startTimeInput.text.toString()
            val endTime = endTimeInput.text.toString()
            val notes = notesInput.text.toString()

            val checkStrings = ArrayList<String>()
            checkStrings.add(result)
            checkStrings.add(decimal)
            checkStrings.add(inner10s)
            checkStrings.add(startTime)
            checkStrings.add(endTime)

            var cont = true
            for (string in checkStrings) {
                if (string == "") {
                    Toast.makeText(
                        context,
                        getString(R.string.emptyWarning),
                        Toast.LENGTH_SHORT)
                        .show()
                    cont = false
                    break
                }
            }

            if (cont && (result.toLong() < 0 || result.toLong() > 100)) {
                Toast.makeText(
                    context,
                    getString(R.string.seriesResultRangeWarning),
                    Toast.LENGTH_SHORT)
                    .show()
                cont = false
            }

            if (cont && (decimal.toDouble() < 0 || decimal.toDouble() > 109)) {
                Toast.makeText(
                    context,
                    getString(R.string.seriesDecimalRangeWarning),
                    Toast.LENGTH_SHORT)
                    .show()
                cont = false
            }

            if (cont && (inner10s.toLong() < 0 || inner10s.toLong() > 10)) {
                Toast.makeText(
                    context,
                    getString(R.string.seriesInner10Warning),
                    Toast.LENGTH_SHORT)
                    .show()
                cont = false
            }

            if (cont) {
                val series = hashMapOf(
                    "Result" to result.toLong(),
                    "Decimal" to decimal.toDouble(),
                    "Inner10s" to inner10s.toLong(),
                    "StartTime" to startTime,
                    "EndTime" to endTime,
                    "Notes" to notes)

                db.collection("matches")
                    .document(matchId)
                    .collection("series")
                    .document(seriesId)
                    .set(series)
                    .addOnFailureListener { exception ->
                        Log.w(
                            "MainActivity",
                            "Error updating series in Firestore",
                            exception)
                        Toast.makeText(
                            context,
                            "Exception: Error updating series in Firestore",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            getString(R.string.seriesUpdated),
                            Toast.LENGTH_SHORT)
                            .show()
                        backButton.performClick()
                    }
            }
        }

        return view
    }
}