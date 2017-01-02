package com.holmes.ponderosa.ui.event;

import android.support.v7.widget.RecyclerView;
import com.holmes.ponderosa.EventModel;
import com.holmes.ponderosa.data.DataFetcher;
import com.holmes.ponderosa.data.api.HomeSeerService;
import com.holmes.ponderosa.data.sql.model.Event;
import com.holmes.ponderosa.ui.action.ActionPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.sqlbrite.BriteDatabase;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@Singleton
public final class EventPresenter implements ActionPresenter, EventAdapter.EventClickListener {
  private final BriteDatabase db;
  private final DataFetcher dataFetcher;
  private final Picasso picasso;
  private final HomeSeerService homeSeerService;
  private final EventAdapter eventAdapter;

  @Inject
  public EventPresenter(BriteDatabase db, DataFetcher dataFetcher, Picasso picasso, HomeSeerService homeSeerService) {
    this.db = db;
    this.dataFetcher = dataFetcher;
    this.picasso = picasso;
    this.homeSeerService = homeSeerService;
    this.eventAdapter = new EventAdapter(this.picasso, this);
  }

  @Override public Subscription loadData(Action1<Integer> countAction) {
    Observable<List<Event>> events =
        db.createQuery(EventModel.TABLE_NAME, EventModel.SELECT_ALL).mapToList(Event.SELECT_ALL_MAPPER::map) //
            .observeOn(AndroidSchedulers.mainThread());

    CompositeSubscription subscriptions = new CompositeSubscription();
    subscriptions.add(events.subscribe(eventAdapter::updateEvents));
    subscriptions.add(events.map(List::size).subscribe(countAction));

    return subscriptions;
  }

  @Override public RecyclerView.Adapter getAdapter() {
    return eventAdapter;
  }

  @Override public void onEventTapped(Event event) {
    homeSeerService.runEvent(event.id())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(hsDevicesResponseResult -> dataFetcher.refresh());
  }
}
