package org.nghru_uk.ghru.repository

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.nghru_uk.ghru.R
import org.nghru_uk.ghru.util.LocaleManager
import org.nghru_uk.ghru.vo.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HomeRepository @Inject constructor(
    private val context: Context,
    private val localeManager: LocaleManager
) {

    fun getHomeItems(): LiveData<Resource<List<HomeItem>>> {


        val mHomeItem = HomeItem(
            1,
            getStringByLocalBefore17(context, R.string.screening_register_participant, localeManager.getLanguage()),
            R.drawable.ic_icon_register_patient
        )

        val mHomeItem1 = HomeItem(
            2,
            getStringByLocalBefore17(context, R.string.screening_blood_pressure, localeManager.getLanguage()),
            R.drawable.ic_icon_bp
        )

        val mHomeItem2 = HomeItem(
            3,
            getStringByLocalBefore17(context, R.string.screening_body_measurements, localeManager.getLanguage()),
            R.drawable.ic_icon_body_measurements
        )

        val mHomeItem3 = HomeItem(
            4,
            getStringByLocalBefore17(context, R.string.screening_biological_samples, localeManager.getLanguage()),
            R.drawable.ic_icon_bio_samples
        )

        val mHomeItem4 = HomeItem(
            5,
            getStringByLocalBefore17(context, R.string.ecg, localeManager.getLanguage()),
            R.drawable.ic_icon_ecg
        )

        val mHomeItem5 = HomeItem(
            6,
            getStringByLocalBefore17(context, R.string.spirometry, localeManager.getLanguage()),
            R.drawable.ic_icon_spirometry
        )

        val mHomeItem6 = HomeItem(
            7,
            getStringByLocalBefore17(context, R.string.fundoscopy, localeManager.getLanguage()),
            R.drawable.ic_icon_fundoscopy
        )

        val mHomeItem7 = HomeItem(
            8,
            getStringByLocalBefore17(context, R.string.activity_tracker, localeManager.getLanguage()),
            R.drawable.ic_icon_activity_tracker
        )

        val mHomeItem8 = HomeItem(
            9,
            getStringByLocalBefore17(context, R.string.screening_hlq_staff, localeManager.getLanguage()),
            R.drawable.ic_icon_healthy_lifestyle
        )
        val mHomeItem9 = HomeItem(
            10,
            getStringByLocalBefore17(context, R.string.screening_intake24, localeManager.getLanguage()),
            R.drawable.ic_icon_intake
        )

        val mHomeItem10 = HomeItem(
            11,
            getStringByLocalBefore17(context, R.string.screening_hlq_self, localeManager.getLanguage()),
            R.drawable.self1_36
        )

        //  val mHomeItem7 = HomeItem(8, getStringByLocalBefore17(context, R.string.pathology, localeManager.getLanguage()), R.drawable.ic_icon_pathology)

        //   val mHomeItem8 = HomeItem(9, getStringByLocalBefore17(context, R.string.medical_report, localeManager.getLanguage()), R.drawable.ic_icon_medical_report)


        val test = ArrayList<HomeItem>()

        test.add(mHomeItem)
        test.add(mHomeItem1)
        test.add(mHomeItem2)

        test.add(mHomeItem4)
        test.add(mHomeItem5)
        test.add(mHomeItem6)

        test.add(mHomeItem3)
        test.add(mHomeItem7)
        //test.add(mHomeItem8)
        //test.add(mHomeItem9)

        test.add(mHomeItem10)


        val homeItems = MutableLiveData<Resource<List<HomeItem>>>()
        val resource = Resource(Status.SUCCESS, test, Message(null, null))
        homeItems.setValue(resource)

        return homeItems
    }


    fun getSampleItems(): LiveData<Resource<List<HomeItem>>> {


        val mHomeItem = HomeItem(
            1,
            getStringByLocalBefore17(context, R.string.sample_management_processing, localeManager.getLanguage()),
            R.drawable.ic_icon_pathology
        )

        val mHomeItem1 = HomeItem(
            2,
            getStringByLocalBefore17(context, R.string.sample_management_storage, localeManager.getLanguage()),
            R.drawable.ic_icon_cryo
        )


        val test = ArrayList<HomeItem>()

        test.add(mHomeItem)
        test.add(mHomeItem1)

        val homeItems = MutableLiveData<Resource<List<HomeItem>>>()
        val resource = Resource(Status.SUCCESS, test, Message(null, null))
        homeItems.setValue(resource)

        return homeItems

    }


    private fun getStringByLocalBefore17(context: Context, resId: Int, language: String): String {
        val currentResources = context.resources
        val assets = currentResources.assets
        val metrics = currentResources.displayMetrics
        val config = Configuration(currentResources.configuration)
        val locale = Locale(language)
        Locale.setDefault(locale)
        config.locale = locale
        val defaultLocaleResources = Resources(assets, metrics, config)
        val string = defaultLocaleResources.getString(resId)
        // Restore device-specific locale
        Resources(assets, metrics, currentResources.configuration)
        return string
    }

    fun getDashboardItems(): LiveData<Resource<List<HomeItem>>> {

        val mHomeItem0 = HomeItem(
            0,
            getStringByLocalBefore17(context, R.string.screening_body_measurements, localeManager.getLanguage()),
            R.drawable.ic_icon_body_measurements
        )

        val mHomeItem1 = HomeItem(
            1,
            getStringByLocalBefore17(context, R.string.screening_blood_pressure, localeManager.getLanguage()),
            R.drawable.ic_icon_bp
        )

        val mHomeItem2 = HomeItem(
            2,
            getStringByLocalBefore17(context, R.string.screening_biological_samples, localeManager.getLanguage()),
            R.drawable.ic_icon_bio_samples
        )

        val mHomeItem3 = HomeItem(
            3,
            getStringByLocalBefore17(context, R.string.ecg, localeManager.getLanguage()),
            R.drawable.ic_icon_ecg
        )

        val mHomeItem4 = HomeItem(
            4,
            getStringByLocalBefore17(context, R.string.spirometry, localeManager.getLanguage()),
            R.drawable.ic_icon_spirometry
        )

        val mHomeItem5 = HomeItem(
            5,
            getStringByLocalBefore17(context, R.string.fundoscopy, localeManager.getLanguage()),
            R.drawable.ic_icon_fundoscopy
        )

        val mHomeItem6 = HomeItem(
            6,
            getStringByLocalBefore17(context, R.string.activity_tracker, localeManager.getLanguage()),
            R.drawable.ic_icon_activity_tracker
        )

//        val mHomeItem7 = HomeItem(
//            7,
//            getStringByLocalBefore17(context, R.string.screening_hlq_staff, localeManager.getLanguage()),
//            R.drawable.ic_icon_healthy_lifestyle
//        )

        val mHomeItem8 = HomeItem(
            8,
            getStringByLocalBefore17(context, R.string.screening_hlq_self, localeManager.getLanguage()),
            R.drawable.self1_36
        )

        val mHomeItem9 = HomeItem(
            9,
            getStringByLocalBefore17(context, R.string.screening_intake24, localeManager.getLanguage()),
            R.drawable.ic_icon_intake
        )

        val mHomeItem10 = HomeItem(
            10,
            getStringByLocalBefore17(context, R.string.screening_checkout, localeManager.getLanguage()),
            R.drawable.checkout_36
        )


//        val mHomeItem11 = HomeItem(
//            11,
//            getStringByLocalBefore17(context, R.string.home_reports, localeManager.getLanguage()),
//            R.drawable.ic_icon_medical_report
//        )

        val test = ArrayList<HomeItem>()
        test.add(mHomeItem0)
        test.add(mHomeItem1)
        test.add(mHomeItem2)
        test.add(mHomeItem3)
        test.add(mHomeItem4)
        test.add(mHomeItem5)
        test.add(mHomeItem6)
        //test.add(mHomeItem7)
        test.add(mHomeItem8)
        test.add(mHomeItem9)

//        test.add(mHomeItem11)

        test.add(mHomeItem10)

        val homeItems = MutableLiveData<Resource<List<HomeItem>>>()
        val resource = Resource(Status.SUCCESS, test, Message(null, null))
        homeItems.setValue(resource)

        return homeItems
    }

    fun getSelectedStationData(): LiveData<Resource<List<StationItem>>> {

        val mHomeItem1 = StationItem( // height and weight
            2,
            "PAA-0001-1",
            "18/01/2021",
            R.drawable.status_cancel
        )

        val mHomeItem2 = StationItem(
            3,
            "PAA-0001-2",
            "21/01/2021",
            R.drawable.status_complete
        )

        val mHomeItem3 = StationItem( // hip and waist
            4,
            "PAA-0001-3",
            "25/01/2021",
            R.drawable.status_progress
        )



        val test = ArrayList<StationItem>()
        test.add(mHomeItem1)
        test.add(mHomeItem2)

        test.add(mHomeItem3)


        val homeItems = MutableLiveData<Resource<List<StationItem>>>()
        val resource = Resource(Status.SUCCESS, test, Message(null, null))
        homeItems.setValue(resource)

        return homeItems
    }

    fun getAllParticipantData(): LiveData<Resource<List<ParticipantStationsItem>>> {

        val mHomeItem1 = ParticipantStationsItem(
            1,
            "PAA-0001-1",
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress
        )

        val mHomeItem2 = ParticipantStationsItem(
            2,
            "PAA-0001-2",
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress

        )

        val mHomeItem3 = ParticipantStationsItem(
            3,
            "PAA-0001-3",
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress

        )

        val test = ArrayList<ParticipantStationsItem>()
        test.add(mHomeItem1)
        test.add(mHomeItem2)

        test.add(mHomeItem3)


        val homeItems = MutableLiveData<Resource<List<ParticipantStationsItem>>>()
        val resource = Resource(Status.SUCCESS, test, Message(null, null))
        homeItems.setValue(resource)

        return homeItems
    }
}
