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
	ArrayList<String> mainList, detailList;
	
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
		for (int i = 1; i + 8 < mainList.size() && i < 50; i += 9) {
			Course c = new Course();
			Subject s = new Subject();
			c.subject = s; 
			
			// link to details
			String tmp = mainList.get(i+1).split("\"")[1];
			
			address = baseAddress + tmp;
			hf = new HttpFetch();
			hf.execute(address);
		
			if (hf.getException() != null) {
				hf.getException().printStackTrace();
				System.exit(-1);
			}
			
			try {
				detailList = hf.get();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
			
			if (detailList.size() > 1) {
				for (int j = 1; j + 16 < detailList.size(); j += 17) {
					Occasion o = new Occasion();
					Teacher t = new Teacher();
					/*
					// class type
					tmp = detailList.get(j+1).split(">")[1].split("<")[0];
					c.classType = tmp;
					
					// group number
					tmp = detailList.get(j+2).split(">")[1].split("<")[0];
					c.courseNumber = Integer.parseInt(tmp);
					
					// day
					tmp = detailList.get(j+4).split(">")[1].split("<")[0];
					char ch = tmp.charAt(0);
					switch (ch) {
					case 'H': o.onDay = Occasion.Day.MONDAY;
						break;
					case 'K': o.onDay = Occasion.Day.TUESDAY;
						break;
					case 'S': o.onDay = Occasion.Day.WEDNESDAY;
						break;
					case 'C': o.onDay = Occasion.Day.THURSDAY;
						break;
					case 'P': o.onDay = Occasion.Day.FRIDAY;
						break;
					}
					
					// start time
					tmp = detailList.get(j+5).split(">")[1].split("<")[0];
					if (tmp != null) {
						o.startTimeHour = Integer.parseInt(tmp.split(":")[0]);
						o.startTimeMinute = Integer.parseInt(tmp.split(":")[1]);
					}
										
					// end time
					tmp = detailList.get(j+6).split(">")[1].split("<")[0];
					if (tmp != null) {
						o.endTimeHour = Integer.parseInt(tmp.split(":")[0]);
						o.endTimeMinute = Integer.parseInt(tmp.split(":")[1]);
					}
					
					// building
					tmp = detailList.get(j+7).split(">")[1].split("<")[0];
					o.building = tmp;
					
					// room
					tmp = detailList.get(j+8).split(">")[1].split("<")[0];
					o.room = tmp;
					
					// teacher name
					tmp = detailList.get(j+9).split(">")[1].split("<")[0];
					t.name = tmp;
					
					// teacher department
					tmp = detailList.get(j+10).split(">")[1].split("<")[0];
					t.department = tmp;
					
					// teacher rank
					tmp = detailList.get(j+11).split(">")[1].split("<")[0];
					t.rank = tmp;
					
					// teacher email
					tmp = detailList.get(j+12).split(">")[1].split("<")[0];
					t.email = tmp;
					
					*/
					// TODO kezdeni valamit occasionnel és teacherrel
				}
			}

			// course id
			tmp = mainList.get(i+2).replace(" ", "");
			tmp = tmp.substring(4, tmp.length() - 5);
			s.code = tmp;
			
			// course name
			tmp = mainList.get(i+3).split(">")[2].split("<")[0];
			s.name = tmp;
			
			// link to rest of details
			tmp = mainList.get(i+8).split("\"")[1];
			
			address = baseAddress + tmp;
			hf = new HttpFetch();
			hf.execute(address);
		
			if (hf.getException() != null) {
				hf.getException().printStackTrace();
				System.exit(-1);
			}
			
			try {
				detailList = hf.get();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
			
			if (detailList.size() > 1) {
				
			}
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
