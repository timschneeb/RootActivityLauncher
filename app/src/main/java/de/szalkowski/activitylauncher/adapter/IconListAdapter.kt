package de.szalkowski.activitylauncher.adapter

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import de.szalkowski.activitylauncher.info.PackageManagerCache.Companion.getPackageManagerCache
import de.szalkowski.activitylauncher.provider.AsyncProvider

class IconListAdapter(context: Context, updater: AsyncProvider<IconListAdapter?>.Updater?) :
    BaseAdapter() {
    private val icons: Array<String>
    private val context: Context
    private val pm: PackageManager
    override fun getCount(): Int {
        return icons.size
    }

    override fun getItem(position: Int): Any {
        return icons[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = ImageView(context)
        val layout = AbsListView.LayoutParams(50, 50)
        view.layoutParams = layout
        val iconResourceString = icons[position]
        view.setImageDrawable(getIcon(iconResourceString, pm))
        return view
    }

    companion object {
        fun getIcon(icon_resource_string: String, pm: PackageManager): Drawable? {
            return try {
                val pack =
                    icon_resource_string.substring(0, icon_resource_string.indexOf(':'))
                val type = icon_resource_string.substring(
                    icon_resource_string.indexOf(':') + 1,
                    icon_resource_string.indexOf('/')
                )
                val name = icon_resource_string.substring(
                    icon_resource_string.indexOf('/') + 1,
                    icon_resource_string.length
                )
                val res = pm.getResourcesForApplication(pack)
                ResourcesCompat.getDrawable(
                    res,
                    res.getIdentifier(name, type, pack),
                    null)
            } catch (e: Exception) {
                pm.defaultActivityIcon
            }
        }
    }

    init {
        val icons = ArrayList<String>()
        this.context = context
        pm = context.packageManager
        val allPackages = pm.getInstalledPackages(0)
        updater?.updateMax(allPackages.size)
        updater?.update(0)
        val cache = getPackageManagerCache(pm)
        for (i in allPackages.indices) {
            updater?.update(i + 1)
            val pack = allPackages[i]
            try {
                val mypack = cache!!.getPackageInfo(pack.packageName)
                for (j in 0 until mypack!!.activitiesCount) {
                    val iconResourceName = mypack.getActivity(j)!!.iconResourceName
                    if (iconResourceName != null) {
                        icons.add(iconResourceName)
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
            }
        }
        this.icons = icons.toTypedArray()
    }
}