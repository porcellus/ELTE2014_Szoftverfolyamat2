package com.eltecalendar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import android.os.AsyncTask;
import android.util.Log;

public class DataMiner {

	DatabaseHelper db;

	public DataMiner(DatabaseHelper dbh) {
		db = dbh;
		Log.d("sajt", "abrakadabra");
		System.out.println("cica");
		System.err.println("mica");
	}

	public void getNewDatabase() {
		// db.deleteDatabase();
		// db.createEmptyDatabase();

		// db.addCourse();
		
		String address = "http://ttkto.elte.hu/scripts/tanrende/";
		address += "kurznevalapjan.idc?tanevfelev=2013-2014-2&kurz=";

		HttpFetch hf = new HttpFetch();
		hf.execute(address);
		
		if (hf.getException() != null) {
			hf.getException().printStackTrace();
			System.exit(-1);
		}
	}

	public void updateDatabase() {

		// db.updateCourse();

	}
}

class HttpFetch extends AsyncTask<String, Void, Void> {

	private Exception exception;

	@Override
	protected Void doInBackground(String... params) {
		exception = null;
		try {
			URL url = new URL(params[0]);
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						httpConnection.getInputStream(), Charset.forName("ISO-8859-1")));
				for (int i = 0; i < 55; ++i) {
					String data = br.readLine();
					Log.i("site", data);
				}
				br.close();
			} catch (Exception e) {
				exception = e;
			} finally {
				httpConnection.disconnect();
			}
		} catch (Exception e) {
			exception = e;
		}

		return null;
	}

	public Exception getException() {
		return exception;
	}
}
