package de.szalkowski.activitylauncher.provider

import android.content.Context
import de.szalkowski.activitylauncher.adapter.IconListAdapter

class IconListAsyncProvider(
    context: Context?,
    listener: Listener<IconListAdapter?>?
) : AsyncProvider<IconListAdapter?>(
    context!!,
    listener,
    false
) {
    override fun run(updater: Updater?): IconListAdapter {
        return IconListAdapter(context, updater)
    }
}