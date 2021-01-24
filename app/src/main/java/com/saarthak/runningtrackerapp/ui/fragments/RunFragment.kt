package com.saarthak.runningtrackerapp.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.saarthak.runningtrackerapp.R
import com.saarthak.runningtrackerapp.ui.adapters.RunAdapter
import com.saarthak.runningtrackerapp.ui.viewmodels.MainViewModel
import com.saarthak.runningtrackerapp.util.Constants.REQ_CODE_LOCN_PERM
import com.saarthak.runningtrackerapp.util.SortType
import com.saarthak.runningtrackerapp.util.Utilities
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment: Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var runAdapter: RunAdapter

    private fun reqPermissions(){
        if(Utilities.locnPermissionsCheck(requireContext())) return

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(this, "You need to accept the location permissions for the app to work properly",
                REQ_CODE_LOCN_PERM,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        } else{
            EasyPermissions.requestPermissions(this, "You need to accept the location permissions for the app to work properly",
                REQ_CODE_LOCN_PERM,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    private fun setUpRv() = runs_rv.apply {
        runAdapter = RunAdapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(viewModel.sortType){
            SortType.DATE -> spFilter.setSelection(0)
            SortType.RUN_TIME -> spFilter.setSelection(1)
            SortType.DIST -> spFilter.setSelection(2)
            SortType.AVG_SPEED -> spFilter.setSelection(3)
            SortType.CAL_BURNT -> spFilter.setSelection(4)
        }

        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> viewModel.sortRuns(SortType.DATE)
                    1 -> viewModel.sortRuns(SortType.RUN_TIME)
                    2 -> viewModel.sortRuns(SortType.DIST)
                    3 -> viewModel.sortRuns(SortType.AVG_SPEED)
                    4 -> viewModel.sortRuns(SortType.CAL_BURNT)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }

        reqPermissions()

        setUpRv()
        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this)
                .build()
                .show()
        }
        else reqPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(REQ_CODE_LOCN_PERM, permissions, grantResults, this)
    }
}