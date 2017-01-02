package com.holmes.ponderosa.ui.device;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.sql.model.Device;
import com.holmes.ponderosa.data.sql.model.DeviceControl;
import com.squareup.picasso.Picasso;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

final class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
  public interface DeviceClickListener {
    void onDeviceTapped(Device repository);
  }

  private final Picasso picasso;
  private final DeviceClickListener deviceClickListener;

  private List<Device> devices = Collections.emptyList();
  private Map<String, List<DeviceControl>> controls = Collections.emptyMap();

  DeviceAdapter(Picasso picasso, DeviceClickListener deviceClickListener) {
    this.picasso = picasso;
    this.deviceClickListener = deviceClickListener;
    setHasStableIds(true);
  }

  public void updateDevices(List<Device> devices) {
    this.devices = devices.stream() //
        .filter(allowableDevices()) //
        .collect(Collectors.toList());
    notifyDataSetChanged();
  }

  @NonNull private Predicate<Device> allowableDevices() {
    // TODO all we handle are lights for now.
    return device -> (device.device_type() == Device.Type.PLUG_IN && device.device_subtype() == 17);
  }

  public void updateControls(List<DeviceControl> controls) {
    this.controls = controls.stream().collect(Collectors.groupingBy(DeviceControl::device_ref));
    notifyDataSetChanged();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    DeviceItemView view = (DeviceItemView) LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.device_item_view, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {
    Device device = devices.get(i);
    List<DeviceControl> deviceControls = controls.get(device.ref());
    viewHolder.bindTo(device, deviceControls);
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

    void bindTo(Device device, List<DeviceControl> deviceControls) {
      String title = device.location() + " " + device.name();
      String status = device.status();
      String statusImage = "https://connected.homeseer.com/" + device.status_image();

      DeviceItemView.DeviceItemViewModel model =
          new DeviceItemView.DeviceItemViewModel(picasso, title, status, statusImage);
      itemView.bindTo(model);
    }
  }
}
