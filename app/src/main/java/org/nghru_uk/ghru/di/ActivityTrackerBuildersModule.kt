package org.nghru_uk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_uk.ghru.ui.activitytracker.activitytracker.ActivityTackeFragment
import org.nghru_uk.ghru.ui.activitytracker.activitytracker.reason.ReasonDialogFragment
import org.nghru_uk.ghru.ui.activitytracker.scanbarcode.ScanBarcodeFragment
import org.nghru_uk.ghru.ui.activitytracker.scanbarcode.manualentry.ManualEntryBarcodeFragment
import org.nghru_uk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.nghru_uk.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class ActivityTrackerBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contrivuteManualEntryBarcode(): ManualEntryBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contrivuteActivityTackeFragment(): ActivityTackeFragment

    @ContributesAndroidInjector
    abstract fun contrivuteReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.nghru_uk.ghru.ui.activitytracker.activitytracker.completed.CompletedDialogFragment
}
