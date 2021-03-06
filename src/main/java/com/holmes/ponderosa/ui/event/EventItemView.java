package com.holmes.ponderosa.ui.event;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.ui.transform.CircleStrokeTransformation;
import com.squareup.picasso.Picasso;
import io.reactivex.functions.Consumer;

public final class EventItemView extends RelativeLayout implements Consumer<EventItemView.EventItemViewModel> {
  @BindView(R.id.device_item_status_image) ImageView avatarView;
  @BindView(R.id.device_item_name) TextView nameView;

  private final CircleStrokeTransformation avatarTransformation;

  public EventItemView(Context context, AttributeSet attrs) {
    super(context, attrs);

    // TODO: Make this a singleton.
    this.avatarTransformation =
        new CircleStrokeTransformation(context, ContextCompat.getColor(context, R.color.avatar_stroke), 1);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
  }

  @Override public void accept(EventItemViewModel eventItemViewModel) throws Exception {
    this.nameView.setText(eventItemViewModel.title);

    //model.picasso.load(model.statusImage)
    //    .placeholder(R.drawable.avatar)
    //    .fit()
    //    .transform(avatarTransformation)
    //    .into(avatarView);
  }

  static class EventItemViewModel {
    final Picasso picasso;
    final String title;

    public EventItemViewModel(Picasso picasso, String title) {
      this.picasso = picasso;
      this.title = title;
    }
  }
}
