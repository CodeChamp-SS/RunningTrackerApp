package com.saarthak.runningtrackerapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.saarthak.runningtrackerapp.R
import com.saarthak.runningtrackerapp.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private fun navigateToTrackingFragment(intent: Intent?){
        if (intent?.action == Constants.ACTION_SHOW_TRACKING_FRAGMENT){
            navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar_main)

        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
        // in order to not to refresh even when we click on the fragment in which we currently are
        bottomNavigationView.setOnNavigationItemReselectedListener {  }    

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.runFragment, R.id.settingsFragment, R.id.statisticsFragment ->
                    bottomNavigationView.visibility = View.VISIBLE
                else ->
                    bottomNavigationView.visibility = View.GONE
            }
        }

        navigateToTrackingFragment(intent)
    }

    // when the activity wasn't destroyed then pending intent will call this fn
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        navigateToTrackingFragment(intent)
    }
}