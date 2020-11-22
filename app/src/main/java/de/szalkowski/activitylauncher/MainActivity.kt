package de.szalkowski.activitylauncher

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import de.szalkowski.activitylauncher.fragment.AllTasksListFragment
import de.szalkowski.activitylauncher.fragment.DisclaimerDialogFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the action bar to show a dropdown list.
        if (!getPreferences(Context.MODE_PRIVATE).getBoolean(
                "disclaimer_accepted",
                false
            )
        ) {
            DisclaimerDialogFragment()
                .show(supportFragmentManager, "DisclaimerDialogFragment")
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container,
                AllTasksListFragment()
            ).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_view_disclaimer -> {

                AlertDialog.Builder(this)
                    .setTitle(getText(R.string.title_dialog_disclaimer))
                    .setMessage(getText(R.string.dialog_disclaimer))
                    .setPositiveButton(getText(android.R.string.yes), null)
                    .show()

                true
            }
            R.id.action_view_source -> {
                val i2 = Intent(Intent.ACTION_VIEW)
                i2.data = Uri.parse(this.getString(R.string.url_source))
                this.startActivity(i2)
                true
            }
            R.id.action_view_original -> {
                val i2 = Intent(Intent.ACTION_VIEW)
                i2.data = Uri.parse(this.getString(R.string.url_original))
                this.startActivity(i2)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}