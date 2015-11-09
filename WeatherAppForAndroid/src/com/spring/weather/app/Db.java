package com.spring.weather.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.spring.weather.model.City;

public class Db extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "db.sqlite";
	private static String DB_PATH;
	private static final int DATABASE_VERSION = 1;
	public static SQLiteDatabase db;
	public static Context context;
	public Db(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Db.context = context;
		DB_PATH = Environment.getDataDirectory()+ "/data/"+context.getPackageName()+"/";
	}
	public void copyDataBase() throws IOException {

		InputStream myInput = context.getAssets().open(DATABASE_NAME);

		String outFileName = DB_PATH + DATABASE_NAME;

		OutputStream myOutput = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public static void openDataBase() throws SQLException {
		String myPath = DB_PATH + DATABASE_NAME;
		db = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}
	public boolean checkDataBase() {

		File dbFile = new File(DB_PATH + DATABASE_NAME);

		return dbFile.exists();

	}
	@Override
	public void close() {
		db.close();
	}
	public ArrayList<City> getCity() {
		ArrayList<City> data = new ArrayList<City>();
		openDataBase();
		String selectQuery = "SELECT * FROM citydata";
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				City city = new City();
				city.setId(c.getString(0));
				city.setName(c.getString(1));
				city.setLat(c.getString(2));
				city.setLng(c.getString(3));
				city.setCode(c.getString(4));
				data.add(city);
			} while (c.moveToNext());
		}
		close();
		return data;

	}
	public static ArrayList<City> getWords(String string) {
		ArrayList<City> data = new ArrayList<City>();
		openDataBase();
		String selectQuery = "SELECT * FROM citydata WHERE name LIKE '"+string+"%' OR name LIKE '"+string+"' LIMIT 5";
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				City city = new City();
				city.setId(c.getString(0));
				city.setName(c.getString(1));
				city.setLat(c.getString(2));
				city.setLng(c.getString(3));
				city.setCode(c.getString(4));
				data.add(city);
			} while (c.moveToNext());
		}
		db.close();
		return data;
	}


}
