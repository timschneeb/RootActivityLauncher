package de.szalkowski.activitylauncher.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import de.szalkowski.activitylauncher.R

class DisclaimerDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder =
            AlertDialog.Builder(activity)
        builder.setTitle(R.string.title_dialog_disclaimer)
            .setMessage(R.string.dialog_disclaimer)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                val editor =
                    activity!!.getPreferences(Context.MODE_PRIVATE).edit()
                editor.putBoolean("disclaimer_accepted", true)
                editor.apply()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                val editor =
                    activity!!.getPreferences(Context.MODE_PRIVATE).edit()
                editor.putBoolean("disclaimer_accepted", false)
                editor.apply()
                activity!!.finish()
            }
        return builder.create()
    }
}