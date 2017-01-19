package com.holmes.ponderosa.ui.action;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.sql.model.QuickAction;

public class FabMiniRow extends LinearLayout {
  @BindView(R.id.mini_fab_title) TextView title;
  @BindView(R.id.min_fab_fab) FloatingActionButton fab;

  public FabMiniRow(Context context, AttributeSet attrs) {
    super(context, attrs);
    LayoutInflater.from(context).inflate(R.layout.fab_mini_row, this);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
  }

  public void show(QuickAction quickAction) throws Exception {
    this.setVisibility(VISIBLE);

    // TODO set fab image based on the QuickAction.
    this.title.setText(quickAction.name());
  }

  public void hide() {
    this.setVisibility(GONE);
  }
}
