package com.holmes.ponderosa.ui.event;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import com.holmes.ponderosa.EventModel;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.DataFetcher;
import com.holmes.ponderosa.data.api.HomeSeerService;
import com.holmes.ponderosa.data.sql.model.Event;
import com.holmes.ponderosa.ui.action.ActionPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.sqlbrite.BriteDatabase;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.stream.Collectors.toList;

@Singleton public final class EventPresenter implements ActionPresenter, EventAdapter.EventClickListener {
  private final DataFetcher dataFetcher;
  private final Picasso picasso;
  private final Resources resources;
  private final HomeSeerService homeSeerService;
  private final EventAdapter eventAdapter;

  private Observable<List<Event>> events;
  private Observable<List<String>> filters;

  @Inject public EventPresenter(BriteDatabase db, DataFetcher dataFetcher, Picasso picasso, Resources resources,
      HomeSeerService homeSeerService) {
    this.dataFetcher = dataFetcher;
    this.picasso = picasso;
    this.resources = resources;
    this.homeSeerService = homeSeerService;
    this.eventAdapter = new EventAdapter(this.picasso, this);

    events = RxJavaInterop.toV2Observable( //
        db.createQuery(EventModel.TABLE_NAME, EventModel.SELECT_ALL) //
            .mapToList(Event.SELECT_ALL_MAPPER::map)) //
        .subscribeOn(Schedulers.io()) //
        .observeOn(AndroidSchedulers.mainThread());

    filters = events.map((foundDevices) -> foundDevices.stream() //
        .map(Event::group_name) //
        .distinct() //
        .collect(toList())) //
        .map(groups -> {
          groups.add(0, this.resources.getString(R.string.events_filter_all));
          return groups;
        });
  }

  @Override public Disposable loadData(Observable<String> selectedFilter, Consumer<Integer> countAction) {
    Disposable adapterSubscription = Observable.combineLatest(events, selectedFilter, //
        (devices1, groupName) -> devices1.stream() //
            .filter(event -> { //
              String allTitle = resources.getString(R.string.events_filter_all); //
              return Objects.equals(groupName, allTitle) || event.group_name().equals(groupName);
            }) //
            .sorted((o1, o2) -> {
              if (o1.group_name().equals(o2.group_name())) {
                return o1.name().compareTo(o2.name());
              }
              return o1.group_name().compareTo(o2.group_name());
            })
            .collect(Collectors.toList())) //
        .subscribe(eventAdapter::updateEvents);

    CompositeDisposable subscriptions = new CompositeDisposable();
    subscriptions.add(adapterSubscription);
    subscriptions.add(events.map(List::size).subscribe(countAction));

    return subscriptions;
  }

  @Override public int getActionTitle() {
    return R.string.action_title_event;
  }

  @Override public RecyclerView.Adapter getAdapter() {
    return eventAdapter;
  }

  @Override public Observable<List<String>> getFilterOptions() {
    return filters;
  }

  @Override public void onEventTapped(Event event) {
    homeSeerService.runEvent(event.id())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(hsDevicesResponseResult -> dataFetcher.refresh());
  }
}
