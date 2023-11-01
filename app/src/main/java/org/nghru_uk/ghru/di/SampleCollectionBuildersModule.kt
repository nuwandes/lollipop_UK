package org.nghru_uk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_uk.ghru.ui.codeheck.CodeCheckDialogFragment
import org.nghru_uk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.nghru_uk.ghru.ui.samplecollection.bagscanbarcode.BagScanBarcodeFragment
import org.nghru_uk.ghru.ui.samplecollection.bagscanned.BagScannedFragment
import org.nghru_uk.ghru.ui.samplecollection.bagscanned.reason.ReasonDialogFragment
import org.nghru_uk.ghru.ui.samplecollection.fast.FastFragment
import org.nghru_uk.ghru.ui.samplecollection.fast.reshedule.ResheduleDialogFragment
import org.nghru_uk.ghru.ui.samplecollection.fasted.FastedFragment
import org.nghru_uk.ghru.ui.samplecollection.manualentry.ManualEntrySampleBagBarcodeFragment
import org.nghru_uk.ghru.ui.samplecollection.manualentry.ManualEntrySampleCollectionFragment
import org.nghru_uk.ghru.ui.samplecollection.scanbarcode.ScanBarcodeFragment
import org.nghru_uk.ghru.ui.samplecollection.verifyid.VerifyIDFragment
import org.nghru_uk.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class SampleCollectionBuildersModule {


    @ContributesAndroidInjector
    abstract fun contributeVerifyIDFragment(): VerifyIDFragment

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeFastFragment(): FastFragment

    @ContributesAndroidInjector
    abstract fun contributeResheduleDialogFragment(): ResheduleDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeFastedFragment(): FastedFragment

    @ContributesAndroidInjector
    abstract fun contributeBagScanBarcodeFragment(): BagScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeBagScannedFragment(): BagScannedFragment

    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntrySampleCollectionFragment(): ManualEntrySampleCollectionFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntrySampleBagBarcodeFragment(): ManualEntrySampleBagBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeCodeCheckDialogFragment(): CodeCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.nghru_uk.ghru.ui.samplecollection.bagscanned.completed.CompletedDialogFragment
}

