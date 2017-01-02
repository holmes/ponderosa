package com.holmes.ponderosa.data.sql.model;

import com.google.auto.value.AutoValue;
import com.holmes.ponderosa.EventModel;
import com.squareup.sqldelight.RowMapper;

@AutoValue public abstract class Event implements EventModel {
  public static final EventModel.Factory<Event> FACTORY = new EventModel.Factory<>(AutoValue_Event::new);
  public static final RowMapper<Event> SELECT_ALL_MAPPER = FACTORY.select_allMapper();
}
