package org.nghru_uk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_uk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.nghru_uk.ghru.ui.report.scanbarcode.ScanBarcodeFragment
import org.nghru_uk.ghru.ui.report.scanbarcode.manualentry.ManualEntryBarcodeFragment
import org.nghru_uk.ghru.ui.report.web.WebFragment
import org.nghru_uk.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class ReportBuildersModule {


    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeWebFragment(): WebFragment


    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contrivuteManualEntryBarcode(): ManualEntryBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.nghru_uk.ghru.ui.report.web.completed.CompletedDialogFragment
}
