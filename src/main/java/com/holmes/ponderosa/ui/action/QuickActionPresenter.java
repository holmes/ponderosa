package com.holmes.ponderosa.ui.action;

import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.sql.model.QuickAction;
import com.squareup.sqlbrite.BriteDatabase;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import static android.view.View.VISIBLE;

@Singleton public class QuickActionPresenter {
  @BindView(R.id.actions_fab) FloatingActionButton floatingActionButton;
  @BindView(R.id.actions_fab_menu) LinearLayout floatingActionMenu;
  @BindView(R.id.fab_mini_1) FabMiniRow fabRow1;
  @BindView(R.id.fab_mini_2) FabMiniRow fabRow2;
  @BindView(R.id.fab_mini_3) FabMiniRow fabRow3;
  @BindView(R.id.fab_mini_4) FabMiniRow fabRow4;
  @BindView(R.id.fab_mini_5) FabMiniRow fabRow5;
  @BindViews(value = { R.id.fab_mini_1, R.id.fab_mini_2, R.id.fab_mini_3, R.id.fab_mini_4, R.id.fab_mini_5 })
  FabMiniRow[] rows;

  private final Observable<List<QuickAction>> actions;
  private final CompositeDisposable disposable;

  @Inject public QuickActionPresenter(BriteDatabase db) {
    disposable = new CompositeDisposable();
    actions = RxJavaInterop.toV2Observable( //
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
            if (rowIndex < quickActions.size() - 1) {
              QuickAction quickAction = quickActions.get(rowIndex);
              rows[rowIndex].show(quickAction);
            } else {
              rows[rowIndex].hide();
            }
          }
        }));

    return disposable;
  }

  @OnClick(R.id.actions_fab) void onFABTapped() {
    floatingActionMenu.setVisibility(VISIBLE);
  }
}
