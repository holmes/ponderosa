package com.holmes.ponderosa.ui.action;

import android.app.Application;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.sql.model.QuickAction;
import com.holmes.ponderosa.ui.action.FabMiniRow.FabMiniRowModel;
import com.squareup.sqlbrite.BriteDatabase;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@Singleton public class QuickActionPresenter {
  @BindView(R.id.fab_holder) ViewGroup fabHolder;
  @BindView(R.id.actions_fab) FloatingActionButton floatingActionButton;
  @BindView(R.id.actions_fab_menu) LinearLayout floatingActionMenu;
  @BindView(R.id.fab_mini_1) FabMiniRow fabRow1;
  @BindView(R.id.fab_mini_2) FabMiniRow fabRow2;
  @BindView(R.id.fab_mini_3) FabMiniRow fabRow3;
  @BindView(R.id.fab_mini_4) FabMiniRow fabRow4;
  @BindView(R.id.fab_mini_5) FabMiniRow fabRow5;
  @BindViews(value = { R.id.fab_mini_1, R.id.fab_mini_2, R.id.fab_mini_3, R.id.fab_mini_4, R.id.fab_mini_5 })
  FabMiniRow[] rows;

  @BindColor(R.color.fab_collapsed_action_cover) int collapsedBackgroundColor;
  @BindColor(R.color.fab_expanded_action_cover) int expandedBackgroundColor;
  private final Animation fabOpen;
  private final Animation fabClose;
  private final ActionPerformer actionPerformer;

  private final Observable<List<QuickAction>> actions;
  private final CompositeDisposable disposable;

  private boolean isFabMenuOpen;

  @Inject public QuickActionPresenter(Application application, BriteDatabase db, ActionPerformer actionPerformer) {
    this.actionPerformer = actionPerformer;
    this.fabOpen = AnimationUtils.loadAnimation(application, R.anim.fab_open);
    this.fabClose = AnimationUtils.loadAnimation(application, R.anim.fab_close);

    this.disposable = new CompositeDisposable();
    this.actions = RxJavaInterop.toV2Observable( //
        db.createQuery(QuickAction.TABLE_NAME, QuickAction.MOST_RECENT) //
            .mapToList(QuickAction.SELECT_ALL_MAPPER::map)) //
        .subscribeOn(Schedulers.io()) //
        .observeOn(AndroidSchedulers.mainThread());
  }

  public Disposable bind(View view) {
    ButterKnife.bind(this, view);

    disposable.add( //
        actions.subscribe(quickActions -> {
          for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {

            FabMiniRowModel model;
            if (rowIndex < quickActions.size()) {
              QuickAction quickAction = quickActions.get(rowIndex);
              String title = quickAction.name();
              Consumer<String> runQuickAction = aVoid -> {
                actionPerformer.runQuickAction(quickAction);
                collapseFabMenu();
              };
              model = new FabMiniRowModel(title, runQuickAction);
            } else {
              model = new FabMiniRowModel();
            }

            disposable.add(rows[rowIndex].show(model));
          }
        }));

    return disposable;
  }

  @OnClick(R.id.actions_fab) void onFABTapped() {
    if (isFabMenuOpen) {
      collapseFabMenu();
    } else {
      expandFabMenu();
    }
  }

  @OnClick(R.id.fab_holder) void onFABHolderTapped() {
    collapseFabMenu();
  }

  private void expandFabMenu() {
    fabHolder.setClickable(true);
    fabHolder.setBackgroundColor(expandedBackgroundColor);
    ColorDrawable[] color = { new ColorDrawable(collapsedBackgroundColor), new ColorDrawable(expandedBackgroundColor) };
    TransitionDrawable holderAnimation = new TransitionDrawable(color);
    holderAnimation.startTransition(100);

    ViewCompat.animate(floatingActionButton)
        .rotation(45.0F)
        .withLayer()
        .setDuration(300)
        .setInterpolator(new OvershootInterpolator(10.0F))
        .start();

    floatingActionMenu.setVisibility(VISIBLE);
    floatingActionMenu.startAnimation(fabOpen);
    isFabMenuOpen = true;
  }

  private void collapseFabMenu() {
    fabHolder.setClickable(false);
    fabHolder.setBackgroundColor(collapsedBackgroundColor);
    ColorDrawable[] color = { new ColorDrawable(expandedBackgroundColor), new ColorDrawable(collapsedBackgroundColor) };
    TransitionDrawable holderAnimation = new TransitionDrawable(color);
    holderAnimation.startTransition(3000);

    ViewCompat.animate(floatingActionButton)
        .rotation(0.0F)
        .withLayer()
        .setDuration(100)
        .setInterpolator(new OvershootInterpolator(10.0F))
        .start();

    floatingActionMenu.setVisibility(GONE);
    floatingActionMenu.startAnimation(fabClose);
    isFabMenuOpen = false;
  }
}
