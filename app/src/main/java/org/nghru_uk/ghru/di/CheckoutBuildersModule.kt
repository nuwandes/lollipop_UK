package org.nghru_uk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_uk.ghru.ui.checkout.ScanBarcodeFragment
import org.nghru_uk.ghru.ui.checkout.alreadycheckout.AlreadyCheckoutDialogFragment
import org.nghru_uk.ghru.ui.checkout.alreadycheckout.cancel.CancelDialogFragment
import org.nghru_uk.ghru.ui.checkout.alreadycheckout.completed.CompletedDialogFragment
import org.nghru_uk.ghru.ui.checkout.bankdetails.BankDetailsFragment
import org.nghru_uk.ghru.ui.checkout.completion.CheckoutCompletionFragment
import org.nghru_uk.ghru.ui.checkout.completion.paymentcompletion.PaymentCompletionFragment
import org.nghru_uk.ghru.ui.checkout.manualentry.ManualEntryCheckoutFragment
import org.nghru_uk.ghru.ui.checkout.notcomplete.NotCompleteDialogFragment
import org.nghru_uk.ghru.ui.checkout.selectedparticipant.SelectedParticipantFragment
import org.nghru_uk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment

@Suppress("unused")
@Module
abstract class CheckoutBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeScanbarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryFragment(): ManualEntryCheckoutFragment

    @ContributesAndroidInjector
    abstract fun contributeSelectedParticipantFragment(): SelectedParticipantFragment

    @ContributesAndroidInjector
    abstract fun contributeAlreadyFragment(): AlreadyCheckoutDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeNotcompleteFragment(): NotCompleteDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCheckoutCompletionFragment(): CheckoutCompletionFragment

    @ContributesAndroidInjector
    abstract fun contributeBankDetailsFragment(): BankDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributePaymentCompleteFragment(): PaymentCompletionFragment

    @ContributesAndroidInjector
    abstract fun contributeCancelDialogFragment(): CancelDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment
}
