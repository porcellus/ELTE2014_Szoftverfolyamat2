package com.eltecalendar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.text.Html;

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
			Subject s = new Subject();
			Course c = new Course();
			String cNum = new String();
			
			// course id
			s.code = stripHtml(mainList.get(i+2));
			
			// course name
			s.name = stripHtml(mainList.get(i+3));
			
			db.addSubject(s);
			
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

					// group number
					tmp = stripHtml(detailList.get(j+2));
					if (!tmp.equals(cNum)) {
						if (c.subject != null) {
							db.addCourse(c);
						}
						c.courseNumber = Integer.parseInt(tmp);
						cNum = tmp;
					
						// class type
						c.classType = stripHtml(detailList.get(j+1));
						
						c.subject = s;
						c.teacher = null;
						c.timetable.clear();
					}
					
					
					// day
					tmp = stripHtml(detailList.get(j+4));
					if (tmp.length() > 0) {
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
					}
					
					// start time
					tmp = stripHtml(detailList.get(j+5));
					if (tmp.length() > 0) {
						o.startTimeHour = Integer.parseInt(tmp.split(":")[0]);
						o.startTimeMinute = Integer.parseInt(tmp.split(":")[1]);
					}
										
					// end time
					tmp = stripHtml(detailList.get(j+6));
					if (tmp.length() > 0) {
						o.endTimeHour = Integer.parseInt(tmp.split(":")[0]);
						o.endTimeMinute = Integer.parseInt(tmp.split(":")[1]);
					}
					
					// building
					o.building = stripHtml(detailList.get(j+7));
					
					// room
					o.room = stripHtml(detailList.get(j+8));
					
					// teacher name
					t.name = stripHtml(detailList.get(j+9));
					
					// teacher department
					t.department = stripHtml(detailList.get(j+10));
					
					// teacher rank
					t.rank = stripHtml(detailList.get(j+11));
					
					// teacher email
					t.email = stripHtml(detailList.get(j+12));
					
					c.timetable.add(o);
					
					if (c.teacher != null) {
						db.addTeacher(t);
						c.teacher = t;
					}
				}
			}

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
				for (int j = 1; j + 5 < detailList.size(); j += 6) {
					Programme p = new Programme();
					
					// programme name
					p.name = stripHtml(detailList.get(j+1));
					
					// department (full time, part time, correspondence)
					p.department = stripHtml(detailList.get(j+2));
					
					// term
					tmp = stripHtml(detailList.get(j+4));
					p.term = Integer.parseInt(tmp);
					
					// type
					p.courseType = stripHtml(detailList.get(j+5));
					
					//TODO kezdeni valamit a szakkal
				}
			}
		}
	}

	public void updateDatabase() {
		// TODO
		// db.updateCourse();
	}
	
	String stripHtml(String html) {
		return Html.fromHtml(html).toString();
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
