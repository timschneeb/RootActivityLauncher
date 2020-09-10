package de.szalkowski.activitylauncher.info

import android.content.ComponentName
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import java.util.*

class MyPackageInfo(info: PackageInfo, pm: PackageManager, cache: PackageManagerCache) :
    Comparable<MyPackageInfo> {
    val activitiesCount: Int
        get() = activities.size

    fun getActivity(i: Int): MyActivityInfo? {
        return activities[i]
    }

    var packageName: String = info.packageName
    var abstractIcon: Drawable? = null
    var iconResource = 0
    var iconResourceName: String?

    @JvmField
	var name: String? = null
    var activities: Array<MyActivityInfo?>

    override fun compareTo(other: MyPackageInfo): Int {
        val cmpName = name!!.compareTo(other.name!!)
        return if (cmpName != 0) cmpName else packageName.compareTo(other.packageName)
    }

    override fun equals(other: Any?): Boolean {
        other ?: return false

        if (other.javaClass != MyPackageInfo::class.java) {
            return false
        }
        return packageName == (other as MyPackageInfo).packageName
    }

    companion object {
        private fun countActivitiesFromInfo(info: PackageInfo): Int {
            var nActivities = 0
            for (activity in info.activities) {
                nActivities++
            }
            return nActivities
        }
    }

    init {
        val app = info.applicationInfo
        if (app != null) {
            name = pm.getApplicationLabel(app).toString()
            try {
                abstractIcon = pm.getApplicationIcon(app)
            } catch (e: ClassCastException) {
                abstractIcon = pm.defaultActivityIcon
            }
            iconResource = app.icon
        } else {
            name = info.packageName
            abstractIcon = pm.defaultActivityIcon
            iconResource = 0
        }
        iconResourceName = null
        if (iconResource != 0) {
            try {
                iconResourceName =
                    pm.getResourcesForApplication(app).getResourceName(iconResource)
            } catch (e: Exception) {
            }
        }
        if (info.activities == null) {
            activities = arrayOfNulls(0)
        } else {
            val nActivities = countActivitiesFromInfo(info)
            activities = arrayOfNulls(nActivities)
            for((i, activity) in info.activities.withIndex()) {
                if(activity.packageName != info.packageName){
                    Log.w("PackageInfo", "${activity.packageName} != ${info.packageName}");
                }

                activities[i] = cache.getActivityInfo(
                    ComponentName(activity.packageName, activity.name)
                )
            }
            Arrays.sort(activities)
        }
    }
}