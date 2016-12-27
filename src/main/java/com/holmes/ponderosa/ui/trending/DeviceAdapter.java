package com.holmes.ponderosa.ui.trending;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.api.model.Device;
import com.holmes.ponderosa.data.api.model.DeviceControl;
import com.squareup.picasso.Picasso;
import java.util.Collections;
import java.util.List;
import rx.functions.Action1;

final class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder>
    implements Action1<List<Device>> {
  public interface DeviceClickListener {
    void onDeviceTapped(Device repository);
  }

  private final Picasso picasso;
  private final DeviceClickListener deviceClickListener;

  private List<Device> devices = Collections.emptyList();

  DeviceAdapter(Picasso picasso, DeviceClickListener deviceClickListener) {
    this.picasso = picasso;
    this.deviceClickListener = deviceClickListener;
    setHasStableIds(true);
  }

  @Override public void call(List<Device> devices) {
    this.devices = devices;
    notifyDataSetChanged();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    DeviceItemView view = (DeviceItemView) LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.device_item_view, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {
    viewHolder.bindTo(devices.get(i));
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public int getItemCount() {
    return devices.size();
  }

  final class ViewHolder extends RecyclerView.ViewHolder {
    final DeviceItemView itemView;

    ViewHolder(DeviceItemView itemView) {
      super(itemView);
      this.itemView = itemView;
      this.itemView.setOnClickListener(v -> {
        Device device = devices.get(getAdapterPosition());
        deviceClickListener.onDeviceTapped(device);
      });
    }

    void bindTo(Device device) {
      itemView.bindTo(device, picasso);
    }
  }
}
