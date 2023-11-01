package org.nghru_uk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_uk.ghru.ui.setting.SettingFragment

@Suppress("unused")
@Module
abstract class SettingFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSettingFragment(): SettingFragment


}
