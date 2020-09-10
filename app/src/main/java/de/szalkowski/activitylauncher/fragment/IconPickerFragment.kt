package de.szalkowski.activitylauncher.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.Fragment
import de.szalkowski.activitylauncher.R
import de.szalkowski.activitylauncher.adapter.IconListAdapter
import de.szalkowski.activitylauncher.provider.AsyncProvider
import de.szalkowski.activitylauncher.provider.IconListAsyncProvider

class IconPickerFragment : Fragment(),
    AsyncProvider.Listener<IconListAdapter?> {
    private var grid: GridView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.icon_picker, null)
        grid = view as GridView
        grid!!.onItemClickListener = OnItemClickListener { v, _, index, _ ->
            Toast.makeText(
                activity,
                v.adapter.getItem(index).toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
        return view
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        val provider = IconListAsyncProvider(activity, this)
        provider.execute()
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