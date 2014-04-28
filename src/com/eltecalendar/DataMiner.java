package com.eltecalendar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import android.os.AsyncTask;

public class DataMiner {

	DatabaseHelper db;
	ArrayList<String> mainList;

	public DataMiner(DatabaseHelper dbh) {
		db = dbh;
	}

	public void getNewDatabase() {
		// db.deleteDatabase();
		// db.createEmptyDatabase();

		// db.addCourse();
		
		String baseAddress = "http://ttkto.elte.hu/scripts/tanrende/";
		String address = baseAddress + "kurznevalapjan.idc?tanevfelev=2013-2014-2&kurz=";

		HttpFetch hf = new HttpFetch();
		hf.execute(address);
		
		if (hf.getException() != null) {
			hf.getException().printStackTrace();
			System.exit(-1);
		}
		
		try {
			mainList = hf.get();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// first <tr> skip
		for (int i = 1; i + 8 < mainList.size(); i += 9) {
			// link to details
			String tmp = mainList.get(i+1);
			
			// course id
			tmp = mainList.get(i+2);
			
			// course name
			tmp = mainList.get(i+3);
			
			// link to rest of details
			tmp = mainList.get(i+8);
			
		}
	}

	public void updateDatabase() {
		// db.updateCourse();
	}
}

class HttpFetch extends AsyncTask<String, Void, ArrayList<String>> {

	Exception exception;
	
	ArrayList<String> data = new ArrayList<String>();

	@Override
	protected ArrayList<String> doInBackground(String... params) {
		exception = null;
		try {
			URL url = new URL(params[0]);
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						httpConnection.getInputStream(), Charset.forName("ISO-8859-1")));
				String line;
				while ((line = br.readLine()) != null) {
					if (line.startsWith("<tr>") || line.startsWith("<td")) {
						data.add(line);
					}
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

		return data;
	}

	public Exception getException() {
		return exception;
	}
}
