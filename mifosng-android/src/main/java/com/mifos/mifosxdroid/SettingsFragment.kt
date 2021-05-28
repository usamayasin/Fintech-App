package com.mifos.mifosxdroid

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog.SyncSurveysDialogFragment
import com.mifos.utils.FragmentConstants
import com.mifos.utils.ThemeHelper
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * Created by mayankjindal on 22/07/17.
 */
class SettingsFragment : PreferenceFragment() {

    lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sc_sync_survey.setOnClickListener {
            showSurveyDialog()
        }
        sc_dark_mode.setOnClickListener {
            showDarkModeDialog()
        }
    }

    fun showDarkModeDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(rootView.context)
        alertDialog.setTitle("AlertDialog")
        val items = arrayOf("Light", "Dark"/*, "Default"*/)
        val checkedItem = 0
        alertDialog.setSingleChoiceItems(items, checkedItem, DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                0 -> ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE)
                1 -> ThemeHelper.applyTheme(ThemeHelper.DARK_MODE)
                /*2 -> ThemeHelper.applyTheme(ThemeHelper.DEFAULT_MODE)*/
            }
            startActivity(Intent(activity, activity.javaClass))
        })
        val alert: AlertDialog = alertDialog.create()
        alert.show()
    }

    fun showSurveyDialog() {
        val syncSurveysDialogFragment = SyncSurveysDialogFragment.newInstance()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SURVEYS_SYNC)
        syncSurveysDialogFragment.isCancelable = false
        syncSurveysDialogFragment.show(fragmentTransaction,
                resources.getString(R.string.sync_clients))
    }

    companion object {
        fun newInstance(): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}