package com.holmes.ponderosa.ui.action;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.holmes.ponderosa.R;
import com.jakewharton.rxbinding.view.RxView;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.disposables.EmptyDisposable;

public class FabMiniRow extends RelativeLayout {
  public static class FabMiniRowModel {
    final String title;
    final Consumer<String> consumer;

    public FabMiniRowModel() {
      title = null;
      consumer = null;
    }

    public FabMiniRowModel(String title, Consumer<String> consumer) {
      this.title = title;
      this.consumer = consumer;
    }
  }

  @BindView(R.id.mini_fab_title) TextView title;
  @BindView(R.id.min_fab_fab) FloatingActionButton fab;

  public FabMiniRow(Context context, AttributeSet attrs) {
    super(context, attrs);
    LayoutInflater.from(context).inflate(R.layout.fab_mini_row, this, true);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
  }

  public Disposable show(FabMiniRowModel model) throws Exception {
    if (model.title == null) {
      setVisibility(GONE);
      return EmptyDisposable.INSTANCE;
    }

    // TODO set fab image based on the QuickAction.
    this.title.setText(model.title);

    CompositeDisposable disposable = new CompositeDisposable();

    disposable.add(RxJavaInterop.toV2Observable( //
        RxView.clicks(title) //
            .map(aVoid -> model.title)) //
        .subscribe(model.consumer));

    disposable.add(RxJavaInterop.toV2Observable( //
        RxView.clicks(fab) //
            .map(aVoid -> model.title)) //
        .subscribe(model.consumer));

    return disposable;
  }
}
