package org.nghru_uk.ghru.jobs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import org.nghru_uk.ghru.BuildConfig
import org.nghru_uk.ghru.event.ConnecrtivityRxBus
import org.nghru_uk.ghru.sync.SyncResponseEventType
import java.net.URL
import kotlin.concurrent.thread

class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (isNetworkAvailable(context)) {
            thread() {
                var online: Boolean

                try {
                    val myUrl = URL(BuildConfig.SERVER_URL)
                    val connection = myUrl.openConnection()
                    connection.setConnectTimeout(5000)
                    connection.connect()
                    online = true
                } catch (e: Exception) {
                    // Handle your exceptions
                    online = false
                }

                if (JobManagerFactory.getJobManager() == null) {
                    JobManagerFactory.getJobManager(context)
                }
                if (online) {
                    ConnecrtivityRxBus.getInstance().post(SyncResponseEventType.SUCCESS)
                    JobManagerFactory.getJobManager().start()
                } else {
                    ConnecrtivityRxBus.getInstance().post(SyncResponseEventType.FAILED)
                    JobManagerFactory.getJobManager().stop()
                }
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }
}