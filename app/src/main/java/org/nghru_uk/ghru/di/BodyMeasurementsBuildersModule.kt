package org.nghru_uk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_uk.ghru.ui.bodymeasurements.bodycomposition.BodyCompositionFragment
import org.nghru_uk.ghru.ui.bodymeasurements.bp.BPFragment
import org.nghru_uk.ghru.ui.bodymeasurements.bp.info.InfoFragment
import org.nghru_uk.ghru.ui.bodymeasurements.bp.main.BPMainFragment
import org.nghru_uk.ghru.ui.bodymeasurements.bp.manual.one.BPManualOneFragment
import org.nghru_uk.ghru.ui.bodymeasurements.bp.manual.three.BPManualThreeFragment
import org.nghru_uk.ghru.ui.bodymeasurements.bp.manual.two.BPManualTwoFragment
import org.nghru_uk.ghru.ui.bodymeasurements.bp.skip.SkipDialogFragment
import org.nghru_uk.ghru.ui.bodymeasurements.height.HeightFragment
import org.nghru_uk.ghru.ui.bodymeasurements.hipwaist.HipWaistFragment
import org.nghru_uk.ghru.ui.bodymeasurements.home.BodyMeasurementHomeFragment
import org.nghru_uk.ghru.ui.bodymeasurements.manualentry.ManualEntryBodyMeasurement
import org.nghru_uk.ghru.ui.bodymeasurements.measurements.MeasurementsFragment
import org.nghru_uk.ghru.ui.bodymeasurements.measurements.second.MeasurementsSecondFragment
import org.nghru_uk.ghru.ui.bodymeasurements.review.ReviewFragment
import org.nghru_uk.ghru.ui.bodymeasurements.review.completed.CompletedDialogFragment
import org.nghru_uk.ghru.ui.bodymeasurements.scanbarcode.ScanBarcodeFragment
import org.nghru_uk.ghru.ui.bodymeasurements.verifyid.VerifyIDFragment
import org.nghru_uk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.nghru_uk.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class BodyMeasurementsBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeBPFragment(): BPFragment

    @ContributesAndroidInjector
    abstract fun contributeMeasurementsFragment(): MeasurementsFragment

    @ContributesAndroidInjector
    abstract fun contributeReviewFragment(): ReviewFragment

    @ContributesAndroidInjector
    abstract fun contributeVerifyIDFragment(): VerifyIDFragment

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeMeasurementsSecondFragment(): MeasurementsSecondFragment

    @ContributesAndroidInjector
    abstract fun contributeBPMainFragment(): BPMainFragment

    @ContributesAndroidInjector
    abstract fun contributeBPManualOneFragment(): BPManualOneFragment

    @ContributesAndroidInjector
    abstract fun contributeBPManualTwoFragment(): BPManualTwoFragment

    @ContributesAndroidInjector
    abstract fun contributeBPManualThreeFragment(): BPManualThreeFragment

    @ContributesAndroidInjector
    abstract fun contributeSkipDialogFragment(): SkipDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeInfoFragment(): InfoFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteManualEntryBodyMeasurement(): ManualEntryBodyMeasurement

    @ContributesAndroidInjector
    abstract fun contrubuteBodyMeasurementHomeFragment(): BodyMeasurementHomeFragment

    @ContributesAndroidInjector
    abstract fun contrubuteHeightFragment(): HeightFragment

    @ContributesAndroidInjector
    abstract fun contrubuteBodyCompositionFragment(): BodyCompositionFragment

    @ContributesAndroidInjector
    abstract fun contrubuteHipWaistFragment(): HipWaistFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteReasonDialogFragmentXX(): org.nghru_uk.ghru.ui.bodymeasurements.height.reason.ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteReasonDialogFragmentHome(): org.nghru_uk.ghru.ui.bodymeasurements.home.reason.ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteReasonHipDialogFragmentHome(): org.nghru_uk.ghru.ui.bodymeasurements.hipwaist.reason.ReasonDialogFragment


    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.nghru_uk.ghru.ui.bodymeasurements.home.completed.CompletedDialogFragment


    @ContributesAndroidInjector
    abstract fun contrubuteReasonBodycompositionDialogFragmentXX(): org.nghru_uk.ghru.ui.bodymeasurements.bodycomposition.reason.ReasonDialogFragment

}
