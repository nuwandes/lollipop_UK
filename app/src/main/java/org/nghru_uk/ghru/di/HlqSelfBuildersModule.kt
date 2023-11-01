package org.nghru_uk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_uk.ghru.ui.hlqself.CancelDialogFragment
import org.nghru_uk.ghru.ui.hlqself.confirmation.QuestionnaireConfirmationFragment
import org.nghru_uk.ghru.ui.hlqself.languagelist.QuestionnaireListFragment
import org.nghru_uk.ghru.ui.questionnaire.notcompleted.NotCompletedDialogFragment
import org.nghru_uk.ghru.ui.hlqself.scanbarcode.ScanBarcodeFragment
import org.nghru_uk.ghru.ui.hlqself.scanbarcode.manualentry.ManualEntryBarcodeFragment
import org.nghru_uk.ghru.ui.hlqself.web.WebFragment
import org.nghru_uk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.nghru_uk.ghru.ui.bodymeasurements.bp.completed.CompletedDialogFragment
import org.nghru_uk.ghru.ui.stationcheck.StationCheckDialogFragment


@Suppress("unused")
@Module
abstract class HlqSelfBuildersModule {


    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeWebFragment(): WebFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryBarcodeFragment(): ManualEntryBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteQuestionnaireListFragment(): QuestionnaireListFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCancelDialogFragment(): CancelDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteNotCompletedDialogFragment(): NotCompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteConfirmationFragment(): QuestionnaireConfirmationFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): CompletedDialogFragment


}
