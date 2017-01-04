package com.holmes.ponderosa.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.Injector;
import com.holmes.ponderosa.data.api.auth.CredentialManager;
import com.holmes.ponderosa.ui.action.ActionsView;
import dagger.ObjectGraph;
import javax.inject.Inject;

import static com.holmes.ponderosa.ui.action.ActionsView.PresenterType.DEVICES;
import static com.holmes.ponderosa.ui.action.ActionsView.PresenterType.EVENTS;

public final class MainActivity extends Activity {
  @BindView(R.id.main_drawer_layout) DrawerLayout drawerLayout;
  @BindView(R.id.main_navigation) NavigationView drawer;
  @BindView(R.id.main_content) ViewGroup content;
  @BindColor(R.color.status_bar) int statusBarColor;

  @Inject CredentialManager credentialManager;
  @Inject ViewContainer viewContainer;

  private ObjectGraph activityGraph;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LayoutInflater inflater = getLayoutInflater();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      // Remove the status bar color. The DrawerLayout is responsible for drawing it from now on.
      setStatusBarColor(getWindow());
    }

    // Explicitly reference the application object since we don't want to match our own injector.
    ObjectGraph appGraph = Injector.obtain(getApplication());
    appGraph.inject(this);
    activityGraph = appGraph.plus(new MainActivityModule(this));

    ViewGroup container = viewContainer.forActivity(this);

    inflater.inflate(R.layout.main_activity, container);
    ButterKnife.bind(this, container);

    ViewGroup view = (ViewGroup) inflater.inflate(R.layout.actions_view, content);
    ActionsView actionsView = (ActionsView) view.findViewWithTag(ActionsView.TAG);

    drawerLayout.setStatusBarBackgroundColor(statusBarColor);
    drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    drawer.setNavigationItemSelectedListener(item -> {
      switch (item.getItemId()) {
        case R.id.nav_search:
          actionsView.loadPresenter(EVENTS);
          break;
        case R.id.nav_trending:
          actionsView.loadPresenter(DEVICES);
          break;
        case R.id.nav_credentials:
          signIn();
          return true;
        default:
          throw new IllegalStateException("Unknown navigation item: " + item.getTitle());
      }

      drawerLayout.closeDrawers();
      item.setChecked(true);

      return true;
    });
  }

  private void signIn() {
    ViewGroup dialogView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.dialog_sign_in, null);

    new AlertDialog.Builder(this)
        .setView(dialogView) //
        .setTitle(R.string.sign_in_title)
        .setPositiveButton(R.string.save, (dialog, which) -> {
          String username = ((TextView) dialogView.findViewById(R.id.sign_in_username)).getText().toString();
          String password = ((TextView) dialogView.findViewById(R.id.sign_in_password)).getText().toString();
          credentialManager.save(username, password);
          drawerLayout.closeDrawers();
        }).setNegativeButton(R.string.cancel, (dialog, which) -> drawerLayout.closeDrawers()) //
        .show();
  }

  @Override public Object getSystemService(@NonNull String name) {
    if (Injector.matchesService(name)) {
      return activityGraph;
    }
    return super.getSystemService(name);
  }

  @Override protected void onDestroy() {
    activityGraph = null;
    super.onDestroy();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) private static void setStatusBarColor(Window window) {
    window.setStatusBarColor(Color.TRANSPARENT);
  }
}
