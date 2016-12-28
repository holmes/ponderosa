package com.holmes.ponderosa.data.sql;

import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.holmes.ponderosa.PonderosaApp;

public class PonderosaSQLiteOpenHelper extends SQLiteOpenHelper {
  public PonderosaSQLiteOpenHelper(PonderosaApp app, SQLiteDatabase.CursorFactory factory,
      int version, DatabaseErrorHandler errorHandler) {
    super(app, "ponderosa", factory, version, errorHandler);
  }

  @Override public void onCreate(SQLiteDatabase db) {

  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}
