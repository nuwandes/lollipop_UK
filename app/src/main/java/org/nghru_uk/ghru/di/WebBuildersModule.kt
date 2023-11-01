package org.nghru_uk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_uk.ghru.ui.questionnaire.cancel.CancelDialogFragment
import org.nghru_uk.ghru.ui.questionnaire.confirmation.QuestionnaireConfirmationFragment
import org.nghru_uk.ghru.ui.questionnaire.languagelist.QuestionnaireListFragment
import org.nghru_uk.ghru.ui.questionnaire.notcompleted.NotCompletedDialogFragment
import org.nghru_uk.ghru.ui.questionnaire.scanbarcode.ScanBarcodeFragment
import org.nghru_uk.ghru.ui.questionnaire.scanbarcode.manualentry.ManualEntryBarcodeFragment
import org.nghru_uk.ghru.ui.questionnaire.web.WebFragment
import org.nghru_uk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.nghru_uk.ghru.ui.stationcheck.StationCheckDialogFragment
import org.nghru_uk.ghru.ui.bodymeasurements.bp.completed.CompletedDialogFragment


@Suppress("unused")
@Module
abstract class WebBuildersModule {


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
