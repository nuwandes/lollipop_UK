package org.nghru_uk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_uk.ghru.ui.codeheck.CodeCheckDialogFragment
import org.nghru_uk.ghru.ui.samplemanagement.fastingbloodglucose.FastingBloodGlucoseFragment
import org.nghru_uk.ghru.ui.samplemanagement.hb1ac.Hb1AcFragment
import org.nghru_uk.ghru.ui.samplemanagement.hdl.HDLFragment
import org.nghru_uk.ghru.ui.samplemanagement.hemoglobin.HemoglobinFragment
import org.nghru_uk.ghru.ui.samplemanagement.hogtt.HOGTTFragment
import org.nghru_uk.ghru.ui.samplemanagement.home.SampleMangementHomeFragment
import org.nghru_uk.ghru.ui.samplemanagement.lipidprofile.LipidProfileFragment
import org.nghru_uk.ghru.ui.samplemanagement.pendingsamplelist.PendingSampleListFragment
import org.nghru_uk.ghru.ui.samplemanagement.storage.StorageFragment
import org.nghru_uk.ghru.ui.samplemanagement.storage.manualentry.ManualEntryBarcodeFragment
import org.nghru_uk.ghru.ui.samplemanagement.storage.manualentry.ManualEntryFragment
import org.nghru_uk.ghru.ui.samplemanagement.storage.reason.ReasonDialogFragment
import org.nghru_uk.ghru.ui.samplemanagement.storage.scanqrcode.ScanBarcodeFragment
import org.nghru_uk.ghru.ui.samplemanagement.totalcholesterol.TotalCholesterolFragment
import org.nghru_uk.ghru.ui.samplemanagement.triglycerides.TriglyceridesFragment
import org.nghru_uk.ghru.ui.samplemanagement.tubescanbarcode.TubeScanBarcodeFragment
import org.nghru_uk.ghru.ui.samplemanagement.tubescanbarcode.errordialog.ErrorDialogFragment
import org.nghru_uk.ghru.ui.samplemanagement.tubescanbarcode.manualentry.TubeScanManualBarcodeFragment

@Suppress("unused")
@Module
abstract class SampleProcessingBuildersModule {


    @ContributesAndroidInjector
    abstract fun contributePendingSampleListFragment(): PendingSampleListFragment

    @ContributesAndroidInjector
    abstract fun contributeTubeScanBarcodeFragment(): TubeScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeSampleMangementHomeFragment(): SampleMangementHomeFragment

    @ContributesAndroidInjector
    abstract fun contributeLipidProfileFragment(): LipidProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeFastingBloodGlucoseFragment(): FastingBloodGlucoseFragment

    @ContributesAndroidInjector
    abstract fun contributeHb1AcFragment(): Hb1AcFragment


    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment


    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragmentXX(): org.nghru_uk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeTubeScanManualBarcodeFragment(): TubeScanManualBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeScanFragment(): org.nghru_uk.ghru.ui.samplemanagement.storage.scanbarcode.ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeStorageFragment(): StorageFragment

    @ContributesAndroidInjector
    abstract fun contributeScanQRCodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragment(): ReasonDialogFragment


    @ContributesAndroidInjector
    abstract fun contributeManualEntryFragment(): ManualEntryFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryBarcodeFragment(): ManualEntryBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeHOGTTFragment(): HOGTTFragment

    @ContributesAndroidInjector
    abstract fun contributeCodeCheckDialogFragment(): CodeCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeTotalCholesterolFragment(): TotalCholesterolFragment

    @ContributesAndroidInjector
    abstract fun contributeHDLFragment(): HDLFragment

    @ContributesAndroidInjector
    abstract fun contributeHemoglobinFragment(): HemoglobinFragment

    @ContributesAndroidInjector
    abstract fun contributeTriglyceridesFragment(): TriglyceridesFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.nghru_uk.ghru.ui.samplemanagement.storage.completed.CompletedDialogFragment


}

