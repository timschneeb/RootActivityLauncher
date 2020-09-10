package de.szalkowski.activitylauncher.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.stericson.RootShell.RootShell
import com.stericson.RootShell.exceptions.RootDeniedException
import com.stericson.RootShell.execution.Command
import de.szalkowski.activitylauncher.R
import java.io.IOException
import java.util.concurrent.TimeoutException

/**
 * Created by Derek on 12/30/2015.
 *
 * Credits: https://github.com/DerekZiemba/RootActivityLauncher
 */
object RootActivityLauncher {
    private fun hasRootAccess(): Boolean {
        return RootShell.isRootAvailable() && RootShell.isAccessGiven()
    }

    private fun getActivityIntent(activity: ComponentName?): Intent {
        val intent = Intent()
        intent.component = activity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return intent
    }

    @JvmStatic
    fun launchActivity(context: Context, activity: ComponentName) {
        val intent = getActivityIntent(activity)
        Toast.makeText(
            context,
            String.format(
                context.getText(R.string.starting_activity).toString(),
                activity.flattenToShortString()
            ),
            Toast.LENGTH_LONG
        ).show()
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            launchActivityRoot(context, activity)
        }
    }

    private fun launchActivityRoot(
        context: Context?,
        activity: ComponentName
    ) {
        if (hasRootAccess()) {
            val name = activity.flattenToShortString()
            val sCommand = "am start -a android.intent.action.MAIN -n $name"
            try {
                val shell = RootShell.getShell(true)
                val cmd =
                    Command(0, sCommand)
                shell.add(cmd)
                shell.close()
            } catch (e: IOException) {
                Toast.makeText(context, "IOException: $e", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } catch (e: TimeoutException) {
                Toast.makeText(context, context!!.getString(R.string.root_timeout), Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } catch (e: RootDeniedException) {
                Toast.makeText(context, context!!.getString(R.string.root_denied), Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        } else {
            Toast.makeText(context, context!!.getString(R.string.root_unavailable), Toast.LENGTH_LONG).show()
        }
    }
}