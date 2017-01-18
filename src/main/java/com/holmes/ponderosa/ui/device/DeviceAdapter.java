package com.holmes.ponderosa.ui.device;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.sql.model.Device;
import com.holmes.ponderosa.data.sql.model.DeviceControl;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.picasso.Picasso;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
  public interface DeviceClickListener extends Consumer<Device> {
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
        .filter(DeviceFilters.allowableDevices()) //
        .collect(Collectors.toList());
    notifyDataSetChanged();
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
    private final PublishSubject<Device> device;
    private final CompositeDisposable disposable;

    ViewHolder(DeviceItemView itemView) {
      super(itemView);

      this.device = PublishSubject.create();
      this.disposable = new CompositeDisposable();

      this.disposable.add( //
          this.device.map(current -> {
            String title = current.location() + " " + current.name();
            String status = current.status();
            String statusImage = "https://connected.homeseer.com/" + current.status_image();

            return new DeviceItemView.DeviceItemViewModel(picasso, title, status, statusImage);
          }).subscribe(itemView));

      this.disposable.add(RxJavaInterop.toV2Observable( //
          RxView.clicks(itemView) //
              .map(aVoid -> "")) // Hack for RxJava2.
          .map(aVoid -> devices.get(getAdapterPosition())) //
          .subscribe(deviceClickListener));
    }

    void bindTo(Device device, List<DeviceControl> deviceControls) {
      this.device.onNext(device);
    }
  }
}
