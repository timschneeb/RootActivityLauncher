/**
 * Based on code from Stackoverflow.com under CC BY-SA 3.0
 * Url: http://stackoverflow.com/questions/6493518/create-a-shortcut-for-any-app-on-desktop
 * By:  http://stackoverflow.com/users/815400/xuso
 *
 * and
 *
 * Url: http://stackoverflow.com/questions/3298908/creating-a-shortcut-how-can-i-work-with-a-drawable-as-icon
 * By:  http://stackoverflow.com/users/327402/waza-be
 */
package de.szalkowski.activitylauncher.utils

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.ShortcutIconResource
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.Toast
import de.szalkowski.activitylauncher.R
import de.szalkowski.activitylauncher.info.MyActivityInfo
import de.szalkowski.activitylauncher.info.MyPackageInfo


object LauncherIconCreator {
    private fun getActivityIntent(activity: ComponentName?): Intent {
        val intent = Intent()
        intent.component = activity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return intent
    }

    fun createLauncherIcon(
        context: Context?,
        activity: MyActivityInfo
    ): Boolean {
        val pack: String =
            activity.iconResourceName!!.substring(0, activity.iconResourceName!!.indexOf(':'))

        // Use bitmap version if icon from different package is used
        if (pack != activity.componentName.packageName) {
            createShortcut(
                context!!,
                activity.name!!,
                activity.icon!!,
                getActivityIntent(activity.componentName),
                null
            )
        } else {
            createShortcut(
                context!!, activity.name!!, activity.icon!!, getActivityIntent(activity.componentName),
                activity.iconResourceName
            )
        }
        return true
    }

    fun createLauncherIcon(
        context: Context,
        pack: MyPackageInfo
    ): Boolean {
        val intent =
            context.packageManager.getLaunchIntentForPackage(pack.packageName)
                ?: return false
        createShortcut(context, pack.name!!, pack.abstractIcon!!, intent, pack.iconResourceName)
        return true
    }

    private fun createShortcut(
        context: Context,
        appName: String,
        draw: Drawable,
        intent: Intent,
        iconResourceName: String?
    ) {
        Toast.makeText(
            context,
            String.format(
                context.getText(R.string.creating_application_shortcut).toString(),
                appName
            ),
            Toast.LENGTH_LONG
        ).show()
        if (Build.VERSION.SDK_INT >= 26) {
            doCreateShortcut(context, appName, draw, intent)
        } else {
            doCreateShortcut(context, appName, intent, iconResourceName)
        }
    }

    @Suppress("DEPRECATION")
    @TargetApi(14)
    private fun doCreateShortcut(
        context: Context,
        appName: String?,
        intent: Intent,
        iconResourceName: String?
    ) {
        val shortcutIntent = Intent()
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent)
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName)
        if (iconResourceName != null) {
            val ir = ShortcutIconResource()
            if (intent.component == null) {
                ir.packageName = intent.getPackage()
            } else {
                ir.packageName = intent.component!!.packageName
            }
            ir.resourceName = iconResourceName
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, ir)
        }
        shortcutIntent.action = "com.android.launcher.action.INSTALL_SHORTCUT"
        context.sendBroadcast(shortcutIntent)
    }

    @TargetApi(26)
    private fun doCreateShortcut(
        context: Context,
        appName: String,
        draw: Drawable,
        intent: Intent
    ) {
        val shortcutManager: ShortcutManager
                = context.getSystemService(ShortcutManager::class.java)

        if (shortcutManager.isRequestPinShortcutSupported) {
            val bitmap: Bitmap = DrawableUtils.getBitmap(draw)
            intent.action = Intent.ACTION_CREATE_SHORTCUT
            val shortcutInfo = ShortcutInfo.Builder(context, appName)
                .setShortLabel(appName)
                .setLongLabel(appName)
                .setIcon(Icon.createWithBitmap(bitmap))
                .setIntent(intent)
                .build()
            shortcutManager.requestPinShortcut(shortcutInfo, null)
        } else {
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.error_pin_shortcut))
                .setMessage(context.getText(R.string.error_verbose_pin_shortcut))
                .setPositiveButton(
                    context.getText(android.R.string.ok)
                ) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
        }
    }
}