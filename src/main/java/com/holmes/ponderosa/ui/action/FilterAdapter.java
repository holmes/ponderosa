package com.holmes.ponderosa.ui.action;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.ui.misc.BindableAdapter;
import java.util.ArrayList;
import java.util.List;

final class FilterAdapter extends BindableAdapter<String> {
  private final List<String> options;

  public FilterAdapter(Context context) {
    super(context);
    this.options = new ArrayList<>();
  }

  void update(List<String> options) {
    this.options.clear();
    this.options.addAll(options);
    this.notifyDataSetChanged();
  }

  @Override public int getCount() {
    return options.size();
  }

  @Override public String getItem(int position) {
    return options.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
    return inflater.inflate(R.layout. filter_item_view, container, false);
  }

  @Override public void bindView(String item, int position, View view) {
    ((TextView) view).setText(item);
  }

  @Override public View newDropDownView(LayoutInflater inflater, int position, ViewGroup container) {
    return inflater.inflate(android.R.layout.simple_spinner_dropdown_item, container, false);
  }
}
