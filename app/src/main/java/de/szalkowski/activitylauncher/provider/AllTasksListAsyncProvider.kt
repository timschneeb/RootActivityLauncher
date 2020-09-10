package de.szalkowski.activitylauncher.provider

import android.content.Context
import de.szalkowski.activitylauncher.adapter.AllTasksListAdapter

class AllTasksListAsyncProvider(
    context: Context,
    listener: Listener<AllTasksListAdapter?>?
) : AsyncProvider<AllTasksListAdapter?>(
    context,
    listener,
    true
) {

    override fun run(updater: Updater?): AllTasksListAdapter? {
        return AllTasksListAdapter(context, updater)
    }
}