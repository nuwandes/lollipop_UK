package org.nghru_uk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_uk.ghru.ui.dashboard.allparticipants.AllParticipantsFragment

@Suppress("unused")
@Module
abstract class DashboardAllParticipantsBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): AllParticipantsFragment

}
