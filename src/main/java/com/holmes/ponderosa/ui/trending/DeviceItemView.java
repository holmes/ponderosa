package com.holmes.ponderosa.ui.trending;

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
    //picasso.load(device.location())
    //    .placeholder(R.drawable.avatar)
    //    .fit()
    //    .transform(avatarTransformation)
    //    .into(avatarView);

    nameView.setText(model.title);
    statusView.setText(model.status);
  }

  public static class DeviceItemViewModel {
    final String title;
    final String status;

    public DeviceItemViewModel(String title, String status) {
      this.title = title;
      this.status = status;
    }
  }
}
