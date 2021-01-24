package com.saarthak.runningtrackerapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.saarthak.runningtrackerapp.R
import com.saarthak.runningtrackerapp.util.Constants.KEY_FIRST_TIME
import com.saarthak.runningtrackerapp.util.Constants.KEY_NAME
import com.saarthak.runningtrackerapp.util.Constants.KEY_WT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment: Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject // we've to write set as Boolean is a primitive data type
    var firstTime = true

    private fun saveDataToSP(): Boolean{
        val name = name_et.text.toString()
        val wt = wt_et.text.toString()

        if(name.isEmpty() || wt.isEmpty()) return false

        sharedPref.edit()
            .putFloat(KEY_WT, wt.toFloat())
            .putString(KEY_NAME, name)
            .putBoolean(KEY_FIRST_TIME, false)
            .apply()

            val toolBarTxt = "Let's Go, $name"
        requireActivity().toolbar_main.title = toolBarTxt
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(! firstTime){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()

            findNavController().navigate(R.id.action_setupFragment_to_runFragment, savedInstanceState, navOptions)
        }

        continue_tv.setOnClickListener {
            val success = saveDataToSP()

            if(success) findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            else Snackbar.make(requireView(), "Fields can't be empty !", Snackbar.LENGTH_SHORT).show()
        }
    }

}