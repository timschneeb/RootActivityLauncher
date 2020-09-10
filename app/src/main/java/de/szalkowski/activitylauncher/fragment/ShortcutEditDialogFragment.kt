package de.szalkowski.activitylauncher.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import de.szalkowski.activitylauncher.R
import de.szalkowski.activitylauncher.adapter.IconListAdapter
import de.szalkowski.activitylauncher.info.MyActivityInfo
import de.szalkowski.activitylauncher.utils.LauncherIconCreator.createLauncherIcon

class ShortcutEditDialogFragment : DialogFragment() {
    private var activity: MyActivityInfo? = null
    private var textName: EditText? = null
    private var textPackage: EditText? = null
    private var textClass: EditText? = null
    private var textIcon: EditText? = null
    private var imageIcon: ImageButton? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity =
            arguments!!.getParcelable<Parcelable>("activity") as ComponentName
        this.activity = MyActivityInfo(activity, getActivity()!!.packageManager)
        val builder =
            AlertDialog.Builder(getActivity())
        val inflater =
            getActivity()!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_edit_activity, null)
        textName = view.findViewById<View>(R.id.editText_name) as EditText
        textName!!.setText(this.activity!!.name)
        textPackage = view.findViewById<View>(R.id.editText_package) as EditText
        textPackage!!.setText(this.activity!!.componentName.packageName)
        textClass = view.findViewById<View>(R.id.editText_class) as EditText
        textClass!!.setText(this.activity!!.componentName.className)
        textIcon = view.findViewById<View>(R.id.editText_icon) as EditText
        textIcon!!.setText(this.activity!!.iconResourceName)
        textIcon!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                val pm = getActivity()!!.packageManager
                val drawIcon = IconListAdapter.getIcon(s.toString(), pm)
                imageIcon!!.setImageDrawable(drawIcon)
            }
        })
        imageIcon = view.findViewById<View>(R.id.iconButton) as ImageButton
        imageIcon!!.setImageDrawable(this.activity!!.icon)
        imageIcon!!.setOnClickListener {
            val dialog = IconPickerDialogFragment()
            dialog.attachIconPickerListener (object : IconPickerDialogFragment.IconPickerListener {
                override fun iconPicked(icon: String?) {
                    if(icon == null)
                        return

                    textIcon!!.setText(icon)
                    val pm = getActivity()!!.packageManager
                    val drawIcon = IconListAdapter.getIcon(icon, pm)
                    imageIcon!!.setImageDrawable(drawIcon)
                }

            })

            dialog.show(fragmentManager!!, "icon picker")
        }
        builder.setTitle(this.activity!!.name)
            .setView(view)
            .setIcon(this.activity!!.icon)
            .setPositiveButton(
                R.string.context_action_shortcut
            ) { _, _ ->
                this@ShortcutEditDialogFragment.activity!!.name =
                    textName!!.text.toString()
                val componentPackage =
                    textPackage!!.text.toString()
                val componentClass =
                    textClass!!.text.toString()
                val component =
                    ComponentName(componentPackage, componentClass)
                this@ShortcutEditDialogFragment.activity!!.componentName = component
                this@ShortcutEditDialogFragment.activity!!.iconResourceName =
                    textIcon!!.text.toString()
                val pm = getActivity()!!.packageManager
                try {
                    val iconResourceString =
                        this@ShortcutEditDialogFragment.activity!!.iconResourceName
                    val pack =
                        iconResourceString!!.substring(0, iconResourceString.indexOf(':'))
                    val type = iconResourceString.substring(
                        iconResourceString.indexOf(':') + 1,
                        iconResourceString.indexOf('/')
                    )
                    val name = iconResourceString.substring(
                        iconResourceString.indexOf('/') + 1,
                        iconResourceString.length
                    )
                    val resources =
                        pm.getResourcesForApplication(pack)
                    this@ShortcutEditDialogFragment.activity!!.iconResource =
                        resources.getIdentifier(name, type, pack)
                    if (this@ShortcutEditDialogFragment.activity!!.iconResource != 0) {
                        this@ShortcutEditDialogFragment.activity!!.icon =
                            ResourcesCompat.getDrawable(
                                resources,
                                this@ShortcutEditDialogFragment.activity!!.iconResource, null) as BitmapDrawable
                    } else {
                        this@ShortcutEditDialogFragment.activity!!.icon =
                            pm.defaultActivityIcon
                        Toast.makeText(
                            getActivity(),
                            R.string.error_invalid_icon_resource,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    this@ShortcutEditDialogFragment.activity!!.icon =
                        pm.defaultActivityIcon
                    Toast.makeText(
                        getActivity(),
                        R.string.error_invalid_icon_resource,
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    this@ShortcutEditDialogFragment.activity!!.icon =
                        pm.defaultActivityIcon
                    Toast.makeText(
                        getActivity(),
                        R.string.error_invalid_icon_format,
                        Toast.LENGTH_LONG
                    ).show()
                }
                createLauncherIcon(
                    getActivity()!!,
                    this@ShortcutEditDialogFragment.activity!!
                )
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog!!.cancel() }
        return builder.create()
    }
}