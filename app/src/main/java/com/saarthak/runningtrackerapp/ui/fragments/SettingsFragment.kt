package com.saarthak.runningtrackerapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.saarthak.runningtrackerapp.R
import com.saarthak.runningtrackerapp.util.Constants.KEY_NAME
import com.saarthak.runningtrackerapp.util.Constants.KEY_WT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private fun saveChangesToSharedPref(): Boolean{
        val name = name_et_s.text.toString()
        val wt = weight_et_s.text.toString()

        if(name.isEmpty() || wt.isEmpty()) return false

        sharedPreferences.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WT, wt.toFloat())
            .apply()

        val toolBarTxt = "Let's Go, $name"
        requireActivity().toolbar_main.title = toolBarTxt
        return true
    }

    private fun loadChanges(){
        val name = sharedPreferences.getString(KEY_NAME, "")
        val wt = sharedPreferences.getFloat(KEY_WT, 75f)

        name_et_s.setText(name)
        weight_et_s.setText(wt.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadChanges()

        btnApplyChanges.setOnClickListener {
            val success = saveChangesToSharedPref()

            if(success) Snackbar.make(view, "Data Updated Successfully !", Snackbar.LENGTH_LONG).show()
            else Snackbar.make(view, "Empty fields not allowed !", Snackbar.LENGTH_LONG).show()
        }
    }

}