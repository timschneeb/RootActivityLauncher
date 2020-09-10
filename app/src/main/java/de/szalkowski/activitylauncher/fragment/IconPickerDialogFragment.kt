package de.szalkowski.activitylauncher.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import de.szalkowski.activitylauncher.R
import de.szalkowski.activitylauncher.adapter.IconListAdapter
import de.szalkowski.activitylauncher.provider.AsyncProvider
import de.szalkowski.activitylauncher.provider.IconListAsyncProvider

class IconPickerDialogFragment : DialogFragment(),
    AsyncProvider.Listener<IconListAdapter?> {
    interface IconPickerListener {
        fun iconPicked(icon: String?)
    }

    private var grid: GridView? = null
    private var listener: IconPickerListener? = null
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        val provider = IconListAsyncProvider(activity, this)
        provider.execute()
    }

    fun attachIconPickerListener(listener: IconPickerListener?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder =
            AlertDialog.Builder(activity)
        val inflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.icon_picker, null)
        grid = view as GridView
        grid!!.onItemClickListener = OnItemClickListener { v, _, index, _ ->
            if (listener != null) {
                listener!!.iconPicked(
                    v.adapter.getItem(index).toString()
                )
                this@IconPickerDialogFragment.dialog!!.dismiss()
            }
        }
        builder.setTitle(R.string.title_dialog_icon_picker)
            .setView(view)
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog!!.cancel() }
        return builder.create()
    }

    override fun onProviderFinished(
        task: AsyncProvider<IconListAdapter?>?,
        value: IconListAdapter?
    ) {
        try {
            grid!!.adapter = value
        } catch (e: Exception) {
            Toast.makeText(this.activity, R.string.error_icons, Toast.LENGTH_SHORT).show()
        }
    }
}