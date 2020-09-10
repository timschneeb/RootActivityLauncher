package de.szalkowski.activitylauncher.fragment

import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.ExpandableListView
import android.widget.ExpandableListView.ExpandableListContextMenuInfo
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import de.szalkowski.activitylauncher.R
import de.szalkowski.activitylauncher.adapter.AllTasksListAdapter
import de.szalkowski.activitylauncher.info.MyActivityInfo
import de.szalkowski.activitylauncher.info.MyPackageInfo
import de.szalkowski.activitylauncher.provider.AllTasksListAsyncProvider
import de.szalkowski.activitylauncher.provider.AsyncProvider
import de.szalkowski.activitylauncher.utils.LauncherIconCreator.createLauncherIcon
import de.szalkowski.activitylauncher.utils.RootActivityLauncher.launchActivity

class AllTasksListFragment : Fragment(),
    AsyncProvider.Listener<AllTasksListAdapter?> {
    private var list: ExpandableListView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.frament_all_list, null)
        list =
            view.findViewById<View>(R.id.expandableListView1) as ExpandableListView
        list!!.setOnChildClickListener { parent, _, groupPosition, childPosition, _ ->
            val adapter = parent.expandableListAdapter
            val info =
                adapter.getChild(groupPosition, childPosition) as MyActivityInfo
            launchActivity(activity!!, info.componentName)
            false
        }
        val provider =
            AllTasksListAsyncProvider(this.activity!!, this)
        provider.execute()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //ExpandableListView list = (ExpandableListView) getView().findViewById(R.id.expandableListView1);
        registerForContextMenu(list!!)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu, v: View,
        menuInfo: ContextMenuInfo?
    ) {
        menu.add(
            Menu.NONE,
            0,
            Menu.NONE,
            R.string.context_action_shortcut
        )
        menu.add(Menu.NONE, 1, Menu.NONE, R.string.context_action_launch)
        val info = menuInfo as ExpandableListContextMenuInfo?
        val list =
            view!!.findViewById<View>(R.id.expandableListView1) as ExpandableListView
        when (ExpandableListView.getPackedPositionType(info!!.packedPosition)) {
            ExpandableListView.PACKED_POSITION_TYPE_CHILD -> {
                val activity = list.expandableListAdapter.getChild(
                    ExpandableListView.getPackedPositionGroup(info.packedPosition),
                    ExpandableListView.getPackedPositionChild(info.packedPosition)
                ) as MyActivityInfo
                menu.setHeaderIcon(activity.icon)
                menu.setHeaderTitle(activity.name)
                menu.add(
                    Menu.NONE,
                    2,
                    Menu.NONE,
                    R.string.context_action_edit
                )
            }
            ExpandableListView.PACKED_POSITION_TYPE_GROUP -> {
                val pack = list.expandableListAdapter
                    .getGroup(ExpandableListView.getPackedPositionGroup(info.packedPosition)) as MyPackageInfo
                menu.setHeaderIcon(pack.abstractIcon)
                menu.setHeaderTitle(pack.name)
            }
        }
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        try {
            val info =
                item.menuInfo as ExpandableListContextMenuInfo
            val list =
                view!!.findViewById<View>(R.id.expandableListView1) as ExpandableListView
            when (ExpandableListView.getPackedPositionType(info.packedPosition)) {
                ExpandableListView.PACKED_POSITION_TYPE_CHILD -> {
                    val activity = list.expandableListAdapter.getChild(
                        ExpandableListView.getPackedPositionGroup(info.packedPosition),
                        ExpandableListView.getPackedPositionChild(info.packedPosition)
                    ) as MyActivityInfo
                    when (item.itemId) {
                        0 -> createLauncherIcon(getActivity()!!, activity)
                        1 -> launchActivity(
                            getActivity()!!,
                            activity.componentName
                        )
                        2 -> {
                            val dialog: DialogFragment =
                                ShortcutEditDialogFragment()
                            val args = Bundle()
                            args.putParcelable("activity", activity.componentName)
                            dialog.arguments = args
                            dialog.show(this.fragmentManager!!, "ShortcutEditor")
                        }
                    }
                }
                ExpandableListView.PACKED_POSITION_TYPE_GROUP -> {
                    val pack = list.expandableListAdapter
                        .getGroup(ExpandableListView.getPackedPositionGroup(info.packedPosition)) as MyPackageInfo
                    when (item.itemId) {
                        0 -> createLauncherIcon(activity!!, pack)
                        1 -> {
                            val pm = activity!!.packageManager
                            val intent = pm.getLaunchIntentForPackage(pack.packageName)
                            Toast.makeText(
                                activity,
                                String.format(
                                    getText(R.string.starting_application).toString(),
                                    pack.name
                                ),
                                Toast.LENGTH_LONG
                            ).show()
                            activity!!.startActivity(intent)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                activity,
                activity!!.getText(R.string.error).toString() + ": " + e.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
        return super.onContextItemSelected(item)
    }

    override fun onProviderFinished(
        task: AsyncProvider<AllTasksListAdapter?>?,
        value: AllTasksListAdapter?
    ) {
        try {
            list!!.setAdapter(value)
        } catch (e: Exception) {
            Toast.makeText(this.activity, R.string.error_tasks, Toast.LENGTH_SHORT).show()
        }
    }
}