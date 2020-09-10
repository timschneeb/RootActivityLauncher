package de.szalkowski.activitylauncher

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
            R.id.action_view_source -> {
                val i2 = Intent(Intent.ACTION_VIEW)
                i2.data = Uri.parse(this.getString(R.string.url_source))
                this.startActivity(i2)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}