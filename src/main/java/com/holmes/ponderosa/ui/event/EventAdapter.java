package com.holmes.ponderosa.ui.event;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.sql.model.Event;
import com.squareup.picasso.Picasso;
import java.util.Collections;
import java.util.List;

final class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
  public interface EventClickListener {
    void onEventTapped(Event event);
  }

  private final Picasso picasso;
  private final EventClickListener eventClickListener;

  private List<Event> events = Collections.emptyList();

  EventAdapter(Picasso picasso, EventClickListener eventClickListener) {
    this.picasso = picasso;
    this.eventClickListener = eventClickListener;
    setHasStableIds(true);
  }

  public void updateEvents(List<Event> devices) {
    this.events = devices;
    notifyDataSetChanged();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    EventItemView view = (EventItemView) LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.event_item_view, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {
    viewHolder.bindTo(events.get(i));
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public int getItemCount() {
    return events.size();
  }

  final class ViewHolder extends RecyclerView.ViewHolder {
    final EventItemView itemView;

    ViewHolder(EventItemView itemView) {
      super(itemView);
      this.itemView = itemView;
      this.itemView.setOnClickListener(v -> {
        Event event = events.get(getAdapterPosition());
        eventClickListener.onEventTapped(event);
      });
    }

    void bindTo(Event event) {
      String title = event.name();
      EventItemView.EventItemViewModel model = new EventItemView.EventItemViewModel(picasso, title);
      itemView.bindTo(model);
    }
  }
}
