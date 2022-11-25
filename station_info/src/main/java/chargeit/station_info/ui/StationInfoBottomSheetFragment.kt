package chargeit.station_info.ui

import android.location.Address
import android.location.Geocoder
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import chargeit.data.domain.model.ElectricStationEntity
import chargeit.data.domain.model.Socket
import chargeit.station_info.R
import chargeit.station_info.databinding.FragmentStationInfoBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Locale
import kotlin.properties.Delegates

class StationInfoBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentStationInfoBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var distance: Double? = null
    private var electricStationEntity: ElectricStationEntity? = null
    private var adapter = InfoSocketListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStationInfoBottomSheetBinding.bind(inflater.inflate(R.layout.fragment_station_info_bottom_sheet, container, false))
        arguments?.let {
            distance = it.getDouble(DISTANCE_EXTRA)
            electricStationEntity = it.getParcelable(INFO_EXTRA)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.moreInfoButton.setOnClickListener {
            Toast.makeText(requireContext(), "Info button clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.distanceButton.setOnClickListener {
            Toast.makeText(requireContext(), "Distance button clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.closeSignImageView.setOnClickListener {
            this.dismiss()
        }

        if (electricStationEntity != null && distance != null) {
            adapter.setData(electricStationEntity!!.listOfSockets)
            with(binding) {
                stationConnectorListRecyclerView.adapter = adapter
                distanceButton.text = "$distance км"
                stationAddressTextView.text = getAddressFromCoordinate(electricStationEntity!!.lat, electricStationEntity!!.lon)
            }
        } else {
            makeViewsInvisible()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAddressFromCoordinate(lat: Double, lon: Double) : String {
        val stringList: MutableList<String> = mutableListOf()
        val geocoder = Geocoder(requireContext(), Locale("RU"))
        val addresses = geocoder.getFromLocation(lat, lon, 1)

        stringList.add(addresses[0].thoroughfare)
        stringList.add(", ")
        stringList.add(addresses[0].subThoroughfare)
        stringList.add(", ")
        stringList.add(addresses[0].locality)
        stringList.add("\n")
        stringList.add(addresses[0].countryName)
        stringList.add(", ")
        stringList.add(addresses[0].postalCode)

        return getFullAddress(stringList)
    }

    private fun makeViewsInvisible() {
        with(binding) {
            stationInfoTextView.text = getString(R.string.no_info_about_station_text)
            moreInfoButton.visibility = View.GONE
            distanceButton.visibility = View.GONE
        }
    }

    private fun getFullAddress(stringList: List<String>) : String {
        var address: String = ""

        for (str in stringList) {
            if (str != null) address += str
        }
        return address
    }

    companion object {
        const val TAG = "StationInfoBottomSheetFragment"
        const val INFO_EXTRA = "Station info"
        const val DISTANCE_EXTRA = "Distance"
    }

}