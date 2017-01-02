package com.holmes.ponderosa.ui.device;

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

public final class DeviceItemView extends RelativeLayout {
  @BindView(R.id.device_item_status_image) ImageView avatarView;
  @BindView(R.id.device_item_status) TextView statusView;
  @BindView(R.id.device_item_name) TextView nameView;

  private final CircleStrokeTransformation avatarTransformation;

  public DeviceItemView(Context context, AttributeSet attrs) {
    super(context, attrs);

    // TODO: Make this a singleton.
    avatarTransformation =
        new CircleStrokeTransformation(context, ContextCompat.getColor(context, R.color.avatar_stroke), 1);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
  }

  public void bindTo(DeviceItemViewModel model) {
    model.picasso.load(model.statusImage)
        .placeholder(R.drawable.avatar)
        .fit()
        .transform(avatarTransformation)
        .into(avatarView);

    nameView.setText(model.title);
    statusView.setText(model.status);
  }

  static class DeviceItemViewModel {
    final Picasso picasso;
    final String title;
    final String status;
    final String statusImage;

    public DeviceItemViewModel(Picasso picasso, String title, String status, String statusImage) {
      this.picasso = picasso;
      this.title = title;
      this.status = status;
      this.statusImage = statusImage;
    }
  }
}
