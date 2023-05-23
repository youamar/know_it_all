package com.example.knowitall.screens.museums

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knowitall.R
import com.example.knowitall.databinding.FragmentMuseumsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder
import java.util.*
import java.util.Properties

class MuseumFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MuseumAdapter
    private lateinit var binding: FragmentMuseumsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var randomString: String

    companion object {
        private const val TAG = "MuseumFragment"
        private const val PERMISSIONS_REQUEST_LOCATION = 123
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMuseumsBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = binding.museumRecyclerView
        val editTextArea = binding.editTextArea
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MuseumAdapter()
        recyclerView.adapter = adapter

        randomString = getString(R.string.random_string)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        getLastKnownLocation()

        editTextArea.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val area = s.toString()
                fetchMuseums(area = area)
            }
        })

        return view
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_LOCATION)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    fetchMuseums(latitude = latitude, longitude = longitude)
                }
            }
            .addOnFailureListener {}
    }

    private fun fetchMuseums(area: String? = null, latitude: Double? = null, longitude: Double? = null) {
        val url = if (area != null) {
            val encodedArea = URLEncoder.encode(area, "UTF-8")
            "https://maps.googleapis.com/maps/api/place/textsearch/json?query=museums+in+$encodedArea&key=$randomString"
        } else if (latitude != null && longitude != null) {
            "https://maps.googleapis.com/maps/api/place/textsearch/json?location=$latitude,$longitude&query=museums&key=$randomString"
        } else {
            return
        }

        val museumsList = mutableListOf<Museum>()
        Thread {
            val result = URL(url).readText()
            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("results")
            for (i in 0 until jsonArray.length()) {
                val museumObject = jsonArray.getJSONObject(i)
                val name = museumObject.getString("name")
                val address = museumObject.getString("formatted_address")
                val museum = Museum(name, address)
                museumsList.add(museum)
            }
            // Update the UI
            activity?.runOnUiThread { updateRecyclerView(museumsList) }
        }.start()

        // Pass the list of museums to the adapter
        adapter.submitList(museumsList)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation()
            }
        }
    }

    private fun updateRecyclerView(museumsList: List<Museum>) {
        adapter.submitList(museumsList)
    }
}
