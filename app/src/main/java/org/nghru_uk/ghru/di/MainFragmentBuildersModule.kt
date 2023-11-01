package org.nghru_uk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_uk.ghru.ui.datamanagement.DataManagementListFragment
import org.nghru_uk.ghru.ui.devices.DevicesFragment
import org.nghru_uk.ghru.ui.enumeration.EnumerationFragment
import org.nghru_uk.ghru.ui.home.HomeFragment
import org.nghru_uk.ghru.ui.homeenumeration.HomeEnumerationFragment
import org.nghru_uk.ghru.ui.homeenumerationlist.HomeEmumerationListFragment
import org.nghru_uk.ghru.ui.logout.LogoutDialogFragment
import org.nghru_uk.ghru.ui.samplemanagement.SampleMangementFragment
import org.nghru_uk.ghru.ui.station.StationFragment
import org.nghru_uk.ghru.ui.usersetting.UserSettingFragment

@Suppress("unused")
@Module
abstract class MainFragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeEnumerationFragment(): EnumerationFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeEmumerationListFragment(): HomeEmumerationListFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeEnumerationFragment(): HomeEnumerationFragment

    @ContributesAndroidInjector
    abstract fun contributeStationFragment(): StationFragment

    @ContributesAndroidInjector
    abstract fun contributeDevicesFragment(): DevicesFragment


    @ContributesAndroidInjector
    abstract fun contributeSampleMangementFragment(): SampleMangementFragment

    @ContributesAndroidInjector
    abstract fun contributeUserSettingFragment(): UserSettingFragment

    @ContributesAndroidInjector
    abstract fun contributeLogoutDialogFragment(): LogoutDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeDataManagementListFragment() : DataManagementListFragment

}
