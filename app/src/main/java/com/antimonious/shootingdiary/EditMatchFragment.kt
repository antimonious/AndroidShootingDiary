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

class EditMatchFragment : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_edit_match,
            container,
            false
        )

        val backButton = view.findViewById<ImageButton>(R.id.editMatchBackButton)

        val dateInput = view.findViewById<EditText>(R.id.editMatchDateInput)
        val startTimeInput = view.findViewById<EditText>(R.id.editMatchStartTimeInput)
        val endTimeInput = view.findViewById<EditText>(R.id.editMatchEndTimeInput)
        val locationInput = view.findViewById<EditText>(R.id.editMatchLocationInput)
        val resultInput = view.findViewById<EditText>(R.id.editMatchResultInput)
        val inner10sInput = view.findViewById<EditText>(R.id.editMatchInner10sInput)
        val temperatureInput = view.findViewById<EditText>(R.id.editMatchTemperatureInput)
        val humidityInput = view.findViewById<EditText>(R.id.editMatchHumidityInput)
        val airPressureInput = view.findViewById<EditText>(R.id.editMatchAirPressureInput)
        val moodInput = view.findViewById<EditText>(R.id.editMatchMoodInput)
        val notesInput = view.findViewById<EditText>(R.id.editMatchNotesInput)

        if (matchId != "null") {
            db.collection("matches")
                .document(matchId)
                .get()
                .addOnFailureListener { exception ->
                    Log.w(
                        "MainActivity",
                        "Error getting match details",
                        exception
                    )
                    Toast.makeText(
                        context,
                        "Exception: error getting match details",
                        Toast.LENGTH_SHORT
                    )
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
                            result.getString("Notes"))

                        val editableFactory = Editable.Factory.getInstance()

                        dateInput.text =
                            editableFactory.newEditable(match.Date)

                        startTimeInput.text =
                            editableFactory.newEditable(match.StartTime)

                        endTimeInput.text =
                            editableFactory.newEditable(match.EndTime)

                        locationInput.text =
                            editableFactory.newEditable(match.Location)

                        resultInput.text =
                            editableFactory.newEditable(match.Result.toString())

                        inner10sInput.text =
                            editableFactory.newEditable(match.Inner10s.toString())

                        temperatureInput.text =
                            editableFactory.newEditable(match.Temperature.toString())

                        humidityInput.text =
                            editableFactory.newEditable(match.Humidity.toString())

                        airPressureInput.text =
                            editableFactory.newEditable(match.AirPressure.toString())

                        moodInput.text =
                            editableFactory.newEditable(match.Mood)

                        notesInput.text =
                            editableFactory.newEditable(match.Notes)
                    }

                    else {
                        Toast.makeText(
                            context,
                            getString(R.string.matchNotFound),
                            Toast.LENGTH_SHORT)
                            .show()
                    }
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

        view.findViewById<ImageButton>(R.id.saveMatchButton).setOnClickListener {
            val date = dateInput.text.toString()
            val startTime = startTimeInput.text.toString()
            val endTime = endTimeInput.text.toString()
            val location = locationInput.text.toString()
            val result = resultInput.text.toString()
            val inner10s = inner10sInput.text.toString()
            val temperature = temperatureInput.text.toString()
            val humidity = humidityInput.text.toString()
            val airPressure = airPressureInput.text.toString()
            val mood = moodInput.text.toString()
            val notes = notesInput.text.toString()

            val checkStrings = ArrayList<String>()
            checkStrings.add(date)
            checkStrings.add(startTime)
            checkStrings.add(endTime)
            checkStrings.add(location)
            checkStrings.add(result)
            checkStrings.add(inner10s)
            checkStrings.add(temperature)
            checkStrings.add(humidity)
            checkStrings.add(airPressure)
            checkStrings.add(mood)

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

            if (cont && (result.toLong() < 0 || result.toLong() > 600)) {
                Toast.makeText(
                    context,
                    getString(R.string.matchResultRangeWarning),
                    Toast.LENGTH_SHORT)
                    .show()
                cont = false
            }

            if (cont && (inner10s.toLong() < 0 || inner10s.toLong() > 60)) {
                Toast.makeText(
                    context,
                    getString(R.string.matchInner10Warning),
                    Toast.LENGTH_SHORT)
                    .show()
                cont = false
            }

            if (cont && (humidity.toLong() < 0 || humidity.toLong() > 100)) {
                Toast.makeText(
                    context,
                    getString(R.string.humidityWarning),
                    Toast.LENGTH_SHORT)
                    .show()
                cont = false
            }

            if (cont && airPressure.toLong() < 0) {
                Toast.makeText(
                    context,
                    getString(R.string.airPressureNegative),
                    Toast.LENGTH_SHORT)
                    .show()
                cont = false
            }

            if (cont) {
                val match = hashMapOf(
                    "userId" to user,
                    "Date" to date,
                    "StartTime" to startTime,
                    "EndTime" to endTime,
                    "Location" to location,
                    "Result" to result.toLong(),
                    "Inner10s" to inner10s.toLong(),
                    "Temperature" to temperature.toLong(),
                    "Humidity" to humidity.toLong(),
                    "AirPressure" to airPressure.toLong(),
                    "Mood" to mood,
                    "Notes" to notes)

                if (matchId != "null") {
                    db.collection("matches")
                        .document(matchId)
                        .set(match)
                        .addOnFailureListener { exception ->
                            Log.w(
                                "MainActivity",
                                "Error updating match in Firestore",
                                exception)
                            Toast.makeText(
                                context,
                                "Exception: Error updating match in Firestore",
                                Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                getString(R.string.matchUpdated),
                                Toast.LENGTH_SHORT)
                                .show()
                            backButton.performClick()
                        }
                }

                else {
                    db.collection("matches")
                        .add(match)
                        .addOnFailureListener { exception ->
                            Log.w(
                                "MainActivity",
                                "Error creating match in Firestore",
                                exception)
                            Toast.makeText(
                                context,
                                "Exception: Error creating match in Firestore",
                                Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnSuccessListener { matchResult ->
                            val addSeriesFragment = AddSeriesFragment()
                            val bundle = Bundle()
                            bundle.putString("match", matchResult.id)
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
        }

        return view
    }
}