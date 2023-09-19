package com.faris.storyapp.ui.activities.maps

import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.faris.storyapp.R
import com.faris.storyapp.databinding.FragmentMapsBinding
import com.faris.storyapp.ui.ViewModelFactory
import com.faris.storyapp.ui.activities.main.MainActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        (activity as MainActivity).setSupportActionBar(binding.toolbarMaps)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.map_options, menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.normal_type -> {
                        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                        true
                    }
                    R.id.satellite_type -> {
                        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                        true
                    }
                    R.id.terrain_type -> {
                        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                        true
                    }
                    R.id.hybrid_type -> {
                        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                        true
                    }
                    else -> {
                        false
                    }
                }

            }

        })
        observerViewModel()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()
    }

    private fun observerViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val mapsViewModel: MapsViewModel by viewModels { factory }
        mapsViewModel.getUserToken().observe(viewLifecycleOwner) { token ->
            if (token.isNullOrEmpty()) {
                view?.findNavController()?.navigate(R.id.action_homeFragment_to_loginFragment)
            } else {
                mapsViewModel.getStoriesMaps(token).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is com.faris.storyapp.data.Result.Loading -> {

                        }
                        is com.faris.storyapp.data.Result.Success -> {
                            for (story in result.data.listStory) {
                                mMap.addMarker(
                                    MarkerOptions().position(
                                        LatLng(story.lat, story.lon)
                                    )
                                        .title(story.name)
                                        .snippet(story.description)
                                )?.tag = story
                            }
                        }
                        is com.faris.storyapp.data.Result.Error -> {
                            Toast.makeText(context, "Lokasi gagal dimuat.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),
                    R.raw.map_style))
            if (!success) {
                Log.e("TAG", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("TAG", "Can't find style. Error: ", exception)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}