package org.nghru_uk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_uk.ghru.ui.camera.CameraFragment
import org.nghru_uk.ghru.ui.camera.CameraFragmentNew
import org.nghru_uk.ghru.ui.consent.ScanBarcodeFragment
import org.nghru_uk.ghru.ui.checkout.alreadycheckout.AlreadyCheckoutDialogFragment
import org.nghru_uk.ghru.ui.checkout.alreadycheckout.cancel.CancelDialogFragment
import org.nghru_uk.ghru.ui.checkout.alreadycheckout.completed.CompletedDialogFragment
import org.nghru_uk.ghru.ui.checkout.completion.CheckoutCompletionFragment
import org.nghru_uk.ghru.ui.checkout.notcomplete.NotCompleteDialogFragment
import org.nghru_uk.ghru.ui.consent.getassetdialog.GetAssetDialogFragment
import org.nghru_uk.ghru.ui.consent.manualentry.ManualEntryConsentFragment
import org.nghru_uk.ghru.ui.consent.upload.UploadConsentFragment
import org.nghru_uk.ghru.ui.registerpatient.consent.ConsentFragment
import org.nghru_uk.ghru.ui.registerpatient.consent.uploadcopleted.UploadCompletedDialogFragment
import org.nghru_uk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment

@Suppress("unused")
@Module
abstract class ConsentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeScanbarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryFragment(): ManualEntryConsentFragment

    @ContributesAndroidInjector
    abstract fun contributeAlreadyFragment(): AlreadyCheckoutDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeNotcompleteFragment(): NotCompleteDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCheckoutCompletionFragment(): CheckoutCompletionFragment

    @ContributesAndroidInjector
    abstract fun contributeCancelDialogFragment(): CancelDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeUploadConsentFragment(): UploadConsentFragment

    @ContributesAndroidInjector
    abstract fun contributeGetAssetDialogFragment(): GetAssetDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCameraFragment(): CameraFragment

    @ContributesAndroidInjector
    abstract fun contributeCameraFragmentNew(): CameraFragmentNew

    @ContributesAndroidInjector
    abstract fun contributeUploadCompletedFragment(): UploadCompletedDialogFragment
}
