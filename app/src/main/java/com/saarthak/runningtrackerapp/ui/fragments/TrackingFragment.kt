package com.saarthak.runningtrackerapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.saarthak.runningtrackerapp.R
import com.saarthak.runningtrackerapp.model.Run
import com.saarthak.runningtrackerapp.ui.viewmodels.MainViewModel
import com.saarthak.runningtrackerapp.util.Constants.ACTION_PAUSE_SERVICE
import com.saarthak.runningtrackerapp.util.Constants.ACTION_START_SERVICE
import com.saarthak.runningtrackerapp.util.Constants.ACTION_STOP_SERVICE
import com.saarthak.runningtrackerapp.util.Constants.MAP_ZOOM
import com.saarthak.runningtrackerapp.util.Constants.POLYLINE_COLOR
import com.saarthak.runningtrackerapp.util.Constants.POLYLINE_WIDTH
import com.saarthak.runningtrackerapp.util.TrackingService
import com.saarthak.runningtrackerapp.util.Utilities
import com.saarthak.runningtrackerapp.util.Coord
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment: Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var pathCoords = mutableListOf<Coord>()

    private var map: GoogleMap? = null

    private var curTimeInMs = 0L

    private var menu: Menu? = null

    @set:Inject
    var wt: Float = 75f

    private fun sendCmdToService(action: String){
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    private fun addAllPolyLines(){
        pathCoords.forEach{
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(it)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyLine(){
        if(pathCoords.isNotEmpty() && pathCoords.last().size > 1){
            val secLastCoord = pathCoords.last()[pathCoords.last().size - 2]
            val lastCoord = pathCoords.last().last()

            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(secLastCoord)
                .add(lastCoord)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun moveCamera(){
        if(pathCoords.isNotEmpty() && pathCoords.last().isNotEmpty()){
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(pathCoords.last().last(), MAP_ZOOM))
        }
    }

    private fun zoomToSeeWholeTrack(){
        val bounds = LatLngBounds.Builder()
        for(coord in pathCoords){
            for(pos in coord){
                bounds.include(pos)
            }
        }

        // don't animate camera as it takes some time to get to the required posn and we've to take a snapshot as soon as we zoom
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width, mapView.height,
                (mapView.height * .05f).toInt()
            )
        )
    }

    private fun endRunAndSave(){
        map?.snapshot { bm ->
            var dist_m = 0L
            for(coord in pathCoords){
                dist_m += Utilities.calcDist(coord).toLong()
            }

            val avgSpeed = round((dist_m / 1000f) / (curTimeInMs / 1000f / 3600) * 10) / 10f
            val timeStamp = Calendar.getInstance().timeInMillis
            val calBurnt = ((dist_m / 1000f) * wt).toLong()

            val run = Run(bm, timeStamp, avgSpeed, dist_m, curTimeInMs, calBurnt)
            viewModel.insertRun(run)

            Snackbar.make(requireActivity().findViewById(R.id.rootView), "Saved Successfully !", Snackbar.LENGTH_LONG).show()

            stopRun()
        }
    }

    private fun toggleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible = true
            sendCmdToService(ACTION_PAUSE_SERVICE)
        }
        else sendCmdToService(ACTION_START_SERVICE)
    }

    private fun updateTracking(tracking: Boolean){
        isTracking = tracking

        if(! tracking){
            btnToggleRun.text = "START"
            if(curTimeInMs > 0) btnFinishRun.visibility = View.VISIBLE
        }
        else{
            btnToggleRun.text = "PAUSE"
            btnFinishRun.visibility = View.GONE
            menu?.getItem(0)?.isVisible = true
        }
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathCoord.observe(viewLifecycleOwner, Observer {
            pathCoords = it
            addLatestPolyLine()
            moveCamera()
        })

        TrackingService.timeMs.observe(viewLifecycleOwner, Observer {
            curTimeInMs = it
            val ft = Utilities.getFormattedTime(curTimeInMs, true)
            timer_tv.text = ft
        })
    }

    private fun showCancelAlertDialog(){
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run")
            .setMessage("Are you sure that you want to Cancel the Run and Delete all it's data ?")
            .setIcon(R.drawable.ic_delete)
            .setBackground(ContextCompat.getDrawable(requireContext(), R.color.colorPrimaryDark))
            .setPositiveButton("Yes"){ _, _ ->
                stopRun()
            }
            .setNegativeButton("No"){ dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()

        dialog.show()
    }

    private fun stopRun() {
        timer_tv.text = "00 : 00 : 00 : 00"
        sendCmdToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(){
            map = it
            addAllPolyLines()
        }

        btnToggleRun.setOnClickListener {
            toggleRun()
        }

        btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSave()
        }

        subscribeToObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbaar_tracking_run, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        if(curTimeInMs > 0){
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.cancelTracking_m -> showCancelAlertDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}