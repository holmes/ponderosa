package com.holmes.ponderosa.data.sql;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.holmes.ponderosa.DeviceControlModel;
import com.holmes.ponderosa.DeviceModel;
import com.holmes.ponderosa.EventModel;

public class PonderosaSQLiteOpenHelper extends SQLiteOpenHelper {
  public PonderosaSQLiteOpenHelper(Application app) {
    super(app, "ponderosa", null, 1);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    db.execSQL(DeviceModel.CREATE_TABLE);
    db.execSQL(DeviceControlModel.CREATE_TABLE);
    db.execSQL(DeviceControlModel.UNIQUE_KEY);

    db.execSQL(EventModel.CREATE_TABLE);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}
