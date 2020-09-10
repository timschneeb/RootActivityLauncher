package de.szalkowski.activitylauncher.provider

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import de.szalkowski.activitylauncher.R

abstract class AsyncProvider<ReturnType>(
    protected var context: Context,
    protected var listener: Listener<ReturnType>?,
    showProgressDialog: Boolean
) : AsyncTask<Void?, Int?, ReturnType>() {
    interface Listener<ReturnType> {
        fun onProviderFinished(
            task: AsyncProvider<ReturnType>?,
            value: ReturnType
        )
    }

    inner class Updater(private val provider: AsyncProvider<ReturnType>) {
        fun update(value: Int) {
            provider.publishProgress(value)
        }

        fun updateMax(value: Int) {
            provider.max = value
        }

    }

    protected var max = 0
    private var progress: ProgressDialog? = null
    override fun onProgressUpdate(vararg values: Int?) {
        if (progress != null && values.isNotEmpty()) {
            val value = values[0]
            if (value == 0) {
                progress!!.isIndeterminate = false
                progress!!.max = max
            }
            if (value != null) {
                progress!!.progress = value
            }
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        if (progress != null) {
            progress!!.setCancelable(false)
            progress!!.setMessage(context.getText(R.string.dialog_progress_loading))
            progress!!.isIndeterminate = true
            progress!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progress!!.show()
        }
    }

    override fun onPostExecute(result: ReturnType) {
        super.onPostExecute(result)
        if (listener != null) {
            listener!!.onProviderFinished(this, result)
        }
        if (progress != null) {
            progress!!.dismiss()
        }
    }

    protected abstract fun run(updater: Updater?): ReturnType
    override fun doInBackground(vararg params: Void?): ReturnType {
        return run(Updater(this))
    }

    init {
        if (showProgressDialog) {
            progress = ProgressDialog(context)
        } else {
            progress = null
        }
    }
}