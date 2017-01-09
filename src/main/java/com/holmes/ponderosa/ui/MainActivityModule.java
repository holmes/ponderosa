package com.holmes.ponderosa.ui;

import android.app.Application;
import android.content.res.Resources;
import android.support.v4.widget.DrawerLayout;
import com.holmes.ponderosa.PonderosaModule;
import com.holmes.ponderosa.ui.action.ActionsView;
import com.holmes.ponderosa.ui.device.DevicePresenter;
import com.holmes.ponderosa.ui.event.EventPresenter;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    addsTo = PonderosaModule.class,
    injects = {
        ActionsView.class, DevicePresenter.class, EventPresenter.class
    }
)
public final class MainActivityModule {
  private final MainActivity mainActivity;

  MainActivityModule(MainActivity mainActivity) {
    this.mainActivity = mainActivity;
  }

  @Provides @Singleton Resources provideResources(Application application) {
    return application.getResources();
  }

  @Provides @Singleton DrawerLayout provideDrawerLayout() {
    return mainActivity.drawerLayout;
  }
}
