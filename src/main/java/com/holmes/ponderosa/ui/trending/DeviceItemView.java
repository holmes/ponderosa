package com.holmes.ponderosa.ui.trending;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.holmes.ponderosa.data.api.model.Device;
import com.holmes.ponderosa.ui.transform.CircleStrokeTransformation;
import com.holmes.ponderosa.R;
import com.squareup.picasso.Picasso;

public final class DeviceItemView extends RelativeLayout {
  @BindView(R.id.device_item_status_image) ImageView avatarView;
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

  public void bindTo(Device device, Picasso picasso) {
    picasso.load(device.statusImage)
        .placeholder(R.drawable.avatar)
        .fit()
        .transform(avatarTransformation)
        .into(avatarView);

    nameView.setText(device.location + " " + device.name);
  }
}
