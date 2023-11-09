package org.nghru_uk.ghru

import android.app.Activity
import android.app.Application
import android.content.res.Configuration
import android.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.pixplicity.easyprefs.library.Prefs
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.fabric.sdk.android.Fabric
import org.nghru_uk.ghru.di.AppInjector
import org.nghru_uk.ghru.jobs.JobManagerFactory
import org.nghru_uk.ghru.util.LocaleManager
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import android.app.KeyguardManager
import android.content.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import org.nghru_uk.ghru.db.AccessTokenDao
import org.nghru_uk.ghru.util.TokenManager
import org.nghru_uk.ghru.vo.Status
import kotlin.concurrent.schedule
import androidx.core.content.ContextCompat.getSystemService
import com.michaelflisar.lumberjack.T




class NghruApp : Application(), HasActivityInjector, LogoutDelegate {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    var prefs: SharedPreferences? = null
    var dateFormat: String = "yyyy-MM-dd hh:mm"

    var logoutTimerTask: TimerTask? = null
    var timerStarted: Boolean = false

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var accessTokenDao: AccessTokenDao

    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        //val lifeCycleHandler = AppLifecycleHandler(this)
        // registerLifecycleHandler(lifeCycleHandler)

        val lifeCycleHandler = AppLifecycleHandler(this)
        registerLifecycleHandler(lifeCycleHandler)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())

            setCustomDatabaseFiles(this)
        }

        AppInjector.init(this)
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
        Fabric.with(this, Crashlytics())
        JobManagerFactory.getJobManager(this);
        showDebugDBAddressLogToast(this)
    }

    override fun startTimer(context: Activity?) {
//        if (logoutTimerTask != null) {
//            logoutTimerTask?.cancel()
//            logoutTimerTask = null
//        }
//        timerStarted = true
//        logoutTimerTask = Timer("Log out", false).schedule(1*60*1000) {
//
//            val email = tokenManager.getEmail()
//            tokenManager.deleteToken()
//
//            if (email != null) {
//                val token = accessTokenDao.getTokerByEmailSync(email)
//                if (token != null) {
//                    token.status = false
//                    accessTokenDao.logout(token)
//
//                    displaySessionExpireMsg(context)
//                }
//            }
//
////            context?.finish()
//        }
    }

    override fun stopTimer() {
//        logoutTimerTask?.cancel()
//        logoutTimerTask = null
//        timerStarted = false
    }

//    override fun onAppBackgrounded() {
//        Log.d("Awww", "App in background")
//        prefs?.edit()?.putString("dateTime",getLocalTimeString())?.apply()
//    }
//
//    override fun onAppForegrounded() {
//        Log.d("Yeeey", "App in foreground")
//        val dato = Date()
//        var idleTime =prefs?.getString("dateTime", getLocalTimeString() )
//        var loginTime = prefs?.getString("loginDateTime", getLocalTimeString())
//
//        //Thu Jan 10 13:44:00 GMT+05:30 2019
//        val simpleDateFormat = SimpleDateFormat(dateFormat,Locale.US)
//        var idledate = simpleDateFormat.parse(idleTime);
//        var loginDate = simpleDateFormat.parse(loginTime);
//
//        var difference = dato.getTime() - idledate.getTime()
//        var days =  (difference / (1000*60*60*24));
//        var hours =   ((difference - (1000*60*60*24*days)) / (1000*60*60))
//
//        var differenceLogin = dato.getTime() - loginDate.getTime()
//        var daysLogin =  (differenceLogin / (1000*60*60*24));
//        var hoursLogin =   ((differenceLogin - (1000*60*60*24*daysLogin)) / (1000*60*60))
//
//        if(hours >= 1 || hoursLogin >=12)
//        {
//            prefs?.edit()?.putBoolean("isTimeOut", true)?.apply()
////            val intent = Intent(this, LoginActivity::class.java)
////            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK
////                    or Intent.FLAG_ACTIVITY_CLEAR_TASK
////                    or Intent.FLAG_ACTIVITY_CLEAR_TOP)
////            this.startActivity(intent)
//
//        }
//    }
//
//    override fun onScreenLocked() {
//
//        val myKM = this.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
//        val isPhoneLocked = myKM.inKeyguardRestrictedInputMode()
//
//        if(isPhoneLocked)
//        {
//            Log.d("Yeeey", "Screen locked")
//            prefs?.edit()?.putBoolean("locked", true)?.apply()
//            prefs?.edit()?.putString("dateTime", getLocalTimeString())?.apply()
//
//        }
//        else
//        {
//            var screeLocked = prefs?.getBoolean("locked", false)
//            if(screeLocked!!)
//            {
//                Log.d("Yeeey", "Screen unlocked")
//                prefs?.edit()?.putBoolean("locked", false)?.apply()
//
//                var idleTime =prefs?.getString("dateTime",getLocalTimeString())
//                var loginTime = prefs?.getString("loginDateTime",getLocalTimeString())
//
//                //Thu Jan 10 13:44:00 GMT+05:30 2019
//                val simpleDateFormat = SimpleDateFormat(dateFormat,Locale.US)
//                var idledate = simpleDateFormat.parse(idleTime);
//                var loginDate = simpleDateFormat.parse(loginTime);
//
//                var dato =Date();
//                var difference = dato.getTime() - idledate.getTime()
//                var days =  (difference / (1000*60*60*24));
//                var hours =   ((difference - (1000*60*60*24*days)) / (1000*60*60))
//
//                var differenceLogin = dato.getTime() - loginDate.getTime()
//                var daysLogin =  (differenceLogin / (1000*60*60*24));
//                var hoursLogin =   ((differenceLogin - (1000*60*60*24*daysLogin)) / (1000*60*60))
//
//                if(hours >= 8 || hoursLogin >=12)
//                {
////                    prefs?.edit()?.putBoolean("isTimeOut", true)?.apply()
////                    val intent = Intent(this, LoginActivity::class.java)
////                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK
////                            or Intent.FLAG_ACTIVITY_CLEAR_TASK
////                            or Intent.FLAG_ACTIVITY_CLEAR_TOP)
////                    this.startActivity(intent)
//
//                }
//
//            }
//        }
//    }
//
//    private fun registerLifecycleHandler(lifeCycleHandler: AppLifecycleHandler) {
//        registerActivityLifecycleCallbacks(lifeCycleHandler)
//        registerComponentCallbacks(lifeCycleHandler)
//    }

    private fun registerLifecycleHandler(lifeCycleHandler: AppLifecycleHandler) {
        registerActivityLifecycleCallbacks(lifeCycleHandler)
    }

    override fun activityInjector() = dispatchingAndroidInjector

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager(base).setLocale())

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager(applicationContext).setLocale()
    }

    private fun showDebugDBAddressLogToast(context: Context) {
        if (BuildConfig.DEBUG) {
            try {
                val debugDB = Class.forName("com.amitshekhar.DebugDB")
                val getAddressLog = debugDB.getMethod("getAddressLog")
                val value = getAddressLog.invoke(null)
                //Toast.makeText(context, value as String, Toast.LENGTH_LONG).show()
            } catch (ignore: Exception) {

            }

        }
    }

    fun getLocalTimeString(): String {
        val s = SimpleDateFormat(dateFormat, Locale.US)
        return s.format(Date())
    }

    fun setCustomDatabaseFiles(context: Context) {
        if (BuildConfig.DEBUG) {
            try {
                val debugDB = Class.forName("com.amitshekhar.DebugDB")
                val argTypes = arrayOf<Class<*>>(HashMap::class.java)
                val setCustomDatabaseFiles = debugDB.getMethod("setCustomDatabaseFiles", *argTypes)
                val customDatabaseFiles = HashMap<String, Pair<File, String>>()
                // set your custom database files
                customDatabaseFiles.put(
                    "nhealth.db",
                    Pair(
                        File(
                            (context.getFilesDir()).toString()
                                    +
                                    "/" + "nhealth.db"
                        ), ""
                    )
                )
                setCustomDatabaseFiles.invoke(null, customDatabaseFiles)
            } catch (ignore: Exception) {

            }

        }
    }

    fun displaySessionExpireMsg(context: Activity?)
    {
        Log.d("NGHRU_APP", "SESSION_EXPIRED...")

        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(context!!)


        builder.setTitle("Session Expired")
        builder.setMessage(getString(R.string.session_expire_msg))
        builder.setIcon(getDrawable(R.drawable.ic_icon_status_warning_yellow))



        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE ->
                {
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            or Intent.FLAG_ACTIVITY_CLEAR_TOP)

                    context.startActivity(intent)

                    context.finish()
                }
//                DialogInterface.BUTTON_NEGATIVE ->
//                {
//                    dialog.dismiss()
//                }
            }
        }



        builder.setPositiveButton(getString(R.string.login),dialogClickListener)
        //builder.setNegativeButton(getString(R.string.app_no),dialogClickListener)

//        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).visibility = View.INVISIBLE

        context.runOnUiThread {
            //dialog.getButton(AlertDialog.BUTTON_NEGATIVE).visibility = View.INVISIBLE
//            val button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
//            button.visibility = View.INVISIBLE
            dialog = builder.create()
            dialog.show()
        }
    }
}




