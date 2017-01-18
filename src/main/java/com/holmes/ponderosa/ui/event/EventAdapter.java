package com.holmes.ponderosa.ui.event;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.sql.model.Event;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.picasso.Picasso;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import java.util.Collections;
import java.util.List;

final class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
  public interface EventClickListener extends Consumer<Event> {
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
    EventItemView view =
        (EventItemView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_item_view, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {
    viewHolder.bindTo(events.get(i));
  }

  @Override public void onViewRecycled(ViewHolder holder) {
    holder.disposable.dispose();
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public int getItemCount() {
    return events.size();
  }

  final class ViewHolder extends RecyclerView.ViewHolder {
    private final PublishSubject<Event> event;
    private final CompositeDisposable disposable;

    ViewHolder(EventItemView itemView) {
      super(itemView);

      this.event = PublishSubject.create();
      this.disposable = new CompositeDisposable();

      this.disposable.add( //
          this.event.map(current -> {
            String title = current.name();
            return new EventItemView.EventItemViewModel(picasso, title);
          }).subscribe(itemView));

      this.disposable.add(RxJavaInterop.toV2Observable( //
          RxView.clicks(itemView) //
              .map(aVoid -> "")) // Hack for RxJava2.
          .map(aVoid -> events.get(getAdapterPosition())) //
          .subscribe(eventClickListener));
    }

    void bindTo(Event event) {
      this.event.onNext(event);
    }
  }
}
