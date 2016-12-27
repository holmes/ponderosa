package com.holmes.ponderosa.ui;

import android.support.v4.widget.DrawerLayout;

import com.holmes.ponderosa.PonderosaModule;
import com.holmes.ponderosa.ui.trending.TrendingView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
    addsTo = PonderosaModule.class,
    injects = TrendingView.class
)
public final class MainActivityModule {
  private final MainActivity mainActivity;

  MainActivityModule(MainActivity mainActivity) {
    this.mainActivity = mainActivity;
  }

  @Provides @Singleton DrawerLayout provideDrawerLayout() {
    return mainActivity.drawerLayout;
  }
}
