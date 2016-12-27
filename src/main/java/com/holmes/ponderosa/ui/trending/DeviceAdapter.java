package com.holmes.ponderosa.ui.trending;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.api.model.Device;
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

  public DeviceAdapter(Picasso picasso, DeviceClickListener deviceClickListener) {
    this.picasso = picasso;
    this.deviceClickListener = deviceClickListener;
    setHasStableIds(true);
  }

  @Override public void call(List<Device> devices) {
    this.devices = devices;
    notifyDataSetChanged();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    TrendingItemView view = (TrendingItemView) LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.trending_view_repository, viewGroup, false);
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

  public final class ViewHolder extends RecyclerView.ViewHolder {
    public final TrendingItemView itemView;

    public ViewHolder(TrendingItemView itemView) {
      super(itemView);
      this.itemView = itemView;
      this.itemView.setOnClickListener(v -> {
        Device device = devices.get(getAdapterPosition());
        deviceClickListener.onDeviceTapped(device);
      });
    }

    public void bindTo(Device device) {
      itemView.bindTo(device, picasso);
    }
  }
}
