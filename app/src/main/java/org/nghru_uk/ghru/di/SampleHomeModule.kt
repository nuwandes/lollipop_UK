package org.nghru_uk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_uk.ghru.ui.samplemanagement.SampleMangementFragment

@Suppress("unused")
@Module
abstract class SampleHomeModule {
    @ContributesAndroidInjector
    abstract fun contributeSampleMangementFragment(): SampleMangementFragment

}
