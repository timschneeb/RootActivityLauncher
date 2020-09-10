package de.szalkowski.activitylauncher.info

import android.content.ComponentName
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import java.util.*

class PackageManagerCache private constructor(protected var pm: PackageManager) {
    private val packageInfos: MutableMap<String, MyPackageInfo?>
    private val activityInfos: MutableMap<ComponentName, MyActivityInfo?>

    @Throws(PackageManager.NameNotFoundException::class)
    fun getPackageInfo(packageName: String): MyPackageInfo? {
        var myInfo: MyPackageInfo
        synchronized(packageInfos) {
            if (packageInfos.containsKey(packageName)) {
                return packageInfos[packageName]
            }
            val info: PackageInfo
            info = try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            } catch (e: PackageManager.NameNotFoundException) {
                throw e
            }
            myInfo = MyPackageInfo(info, pm, this)
            packageInfos.put(packageName, myInfo)
        }
        return myInfo
    }

    fun getActivityInfo(activityName: ComponentName): MyActivityInfo? {
        var myInfo: MyActivityInfo
        synchronized(activityInfos) {
            if (activityInfos.containsKey(activityName)) {
                return activityInfos[activityName]
            }
            myInfo = MyActivityInfo(activityName, pm)
            activityInfos.put(activityName, myInfo)
        }
        return myInfo
    }

    companion object {
        private var instance: PackageManagerCache? = null
        @JvmStatic
		fun getPackageManagerCache(pm: PackageManager): PackageManagerCache? {
            if (instance == null) {
                instance = PackageManagerCache(pm)
            }
            return instance
        }
    }

    init {
        packageInfos = HashMap()
        activityInfos = HashMap()
    }
}