package de.szalkowski.activitylauncher.adapter

import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import de.szalkowski.activitylauncher.R
import de.szalkowski.activitylauncher.info.MyActivityInfo
import de.szalkowski.activitylauncher.info.MyPackageInfo
import de.szalkowski.activitylauncher.info.PackageManagerCache.Companion.getPackageManagerCache
import de.szalkowski.activitylauncher.provider.AsyncProvider
import java.util.*

class AllTasksListAdapter(private var context: Context, updater: AsyncProvider<AllTasksListAdapter?>.Updater?) :
    BaseExpandableListAdapter() {
    private var packages: MutableList<MyPackageInfo>? = null
    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return packages!![groupPosition].getActivity(childPosition)!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val activity = getChild(groupPosition, childPosition) as MyActivityInfo
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.all_activities_child_item, null)
        val text1 = view.findViewById<View>(android.R.id.text1) as TextView
        val pm = context.packageManager
        try {
            val info = pm.getActivityInfo(activity.componentName, 0)
            val sb = StringBuilder()
            sb.append(activity.name)
            if (!info.isEnabled || !info.exported) sb.append(" (Root only)")
            text1.text = sb.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            text1.text = activity.name
            e.printStackTrace()
        }
        val text2 = view.findViewById<View>(android.R.id.text2) as TextView
        text2.text = activity.componentName.className
        val icon =
            view.findViewById<View>(android.R.id.icon) as ImageView
        icon.setImageDrawable(activity.icon)
        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return packages!![groupPosition].activitiesCount
    }

    override fun getGroup(groupPosition: Int): Any {
        return packages!![groupPosition]
    }

    override fun getGroupCount(): Int {
        return packages!!.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val pack = getGroup(groupPosition) as MyPackageInfo
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.all_activities_group_item, null)
        val text = view.findViewById<View>(android.R.id.text1) as TextView
        text.text = pack.name
        val icon =
            view.findViewById<View>(android.R.id.icon) as ImageView
        icon.setImageDrawable(pack.abstractIcon)
        return view
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    init {
        val pm = context.packageManager
        val cache = getPackageManagerCache(pm)
        val allPackages = pm.getInstalledPackages(0)
        packages = ArrayList(allPackages.size)
        updater?.updateMax(allPackages.size)
        updater?.update(0)
        for (i in allPackages.indices) {
            updater?.update(i + 1)
            val pack = allPackages[i]
            var mypack: MyPackageInfo?
            try {
                mypack = cache!!.getPackageInfo(pack.packageName)
                if (mypack!!.activitiesCount > 0) {
                    (packages as ArrayList<MyPackageInfo>).add(mypack)
                }
            } catch (ignored: PackageManager.NameNotFoundException) {
            }
        }
        (packages as ArrayList<MyPackageInfo>).sort()
    }
}