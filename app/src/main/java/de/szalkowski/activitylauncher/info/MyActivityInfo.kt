package de.szalkowski.activitylauncher.info

import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

class MyActivityInfo(var componentName: ComponentName, pm: PackageManager) :
    Comparable<MyActivityInfo> {

	var icon: Drawable? = null
	var iconResource = 0
    var iconResourceName: String?
	var name: String? = null

    override fun compareTo(other: MyActivityInfo): Int {
        val cmpName = name!!.compareTo(other.name!!)
        return if (cmpName != 0) cmpName else componentName.compareTo(other.componentName)
    }

    override fun equals(other: Any?): Boolean {
        other ?: return false

        if (other.javaClass != MyPackageInfo::class.java) {
            return false
        }

        return componentName == (other as MyActivityInfo).componentName
    }

    init {
        val act: ActivityInfo
        try {
            act = pm.getActivityInfo(componentName, 0)
            name = act.loadLabel(pm).toString()
            try {
                icon = act.loadIcon(pm) as Drawable
            } catch (e: ClassCastException) {
                icon = pm.defaultActivityIcon
            }
            iconResource = act.iconResource
        } catch (e: PackageManager.NameNotFoundException) {
            name = componentName.shortClassName
            icon = pm.defaultActivityIcon
            iconResource = 0
        }
        iconResourceName = null
        if (iconResource != 0) {
            try {
                iconResourceName =
                    pm.getResourcesForActivity(componentName).getResourceName(iconResource)
            } catch (e: Exception) {
            }
        }
    }
}