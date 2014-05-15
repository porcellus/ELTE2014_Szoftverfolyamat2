package com.eltecalendar;

import java.util.ArrayList;
import java.util.HashMap;

import com.eltecalendar.Occasion.Day;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class TimeTable implements ListAdapter {
    private Context _context;
    
    private ArrayList<String> _headers;
    private ArrayList<Course> _courses;
    private ArrayList<String> _slots;
    
    private ArrayList<DataSetObserver> _observers;
    
    public TimeTable(Context context){
    	_context = context;
    	
    	set_courses(new ArrayList<Course>());
    	
    	_headers = new ArrayList<String>();
    	_headers.add("Idõ/Nap");
    	_headers.add("Hétfõ");
    	_headers.add("Kedd");
    	_headers.add("Szerda");
    	_headers.add("Csütörtök");
    	_headers.add("Péntek");
    	
    	_observers = new ArrayList<DataSetObserver>();
    	
    	_slots = new ArrayList<String>();
    	refreshTimeTable();
    }
    
    private void refreshTimeTable() {
		SparseArray<HashMap<Day,ArrayList<String> > > entries = new SparseArray<HashMap<Day,ArrayList<String> > >();
		for(Course c: getCourses()){
			String name = c.subject.name;
			android.util.Log.w("debug", c.subject.name + "->" + c.timetable.size());
			for(Occasion occ: c.timetable){
				if(entries.get(occ.startTimeHour) == null) entries.put(occ.startTimeHour, new HashMap<Day,ArrayList<String>>());
				if(!entries.get(occ.startTimeHour).containsKey(occ.onDay)) entries.get(occ.startTimeHour).put(occ.onDay, new ArrayList<String>());
				entries.get(occ.startTimeHour).get(occ.onDay).add(
						(name.length() > 17 ? name.substring(0, 14) + "..." :  name ) + "\n"
						+ occ.startTimeHour + ":"+ occ.startTimeMinute + "-" + occ.endTimeHour + ":" + occ.endTimeMinute + "\n"
						+ occ.building + " " + occ.room);
			}
		}
		_slots.clear();
		_slots.addAll(_headers);
		boolean empty, first;
		int h;
		for(int i=0;i<entries.size();++i){
			h = entries.keyAt(i);
			first = true;
			empty = false;
			while(!empty){
				_slots.add(first ? h + ":00" : "");
				first = false;
				empty = true;
				for(Day d: Day.values()){
					ArrayList<String> list = entries.get(h).get(d);
					if(list != null && !list.isEmpty()) {
						_slots.add(list.get(0));
						list.remove(0);
						if(!list.isEmpty()) empty = false;
					} else {
						_slots.add("");
					}
				}
			}
		}
		
		android.util.Log.w("debug", getCourses().size() + "->" + _slots.size());
		
    	
    	for(DataSetObserver ob : _observers){
    		ob.onChanged();
    	}
	}

	public void addCourse(Course course){
    	getCourses().add(course);
    	refreshTimeTable();
    }

	public void clear() {
		getCourses().clear();
    	refreshTimeTable();
	}
	
	public void remove(int ind){
		getCourses().remove(ind);
    	refreshTimeTable();
	}
	
	public void remove(Course course){
		getCourses().remove(course);
    	refreshTimeTable();
	}
    
	@Override
	public int getCount() {
		return _slots.size();
	}

	@Override
	public Object getItem(int position) {
		return _slots.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if(position < 6 || position%6 == 0) return 0;
		else return 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        final String text = (String) getItem(position);
 
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        if(position < _headers.size()){
        	txtListChild.setTypeface(null, Typeface.BOLD);
        	txtListChild.setHeight(60);
        }
        else txtListChild.setHeight(110);
        txtListChild.setText(text);
        return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		_observers.add(observer);		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		_observers.remove(observer);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position) != 0;
	}

	public boolean contains(Course c) {
		return getCourses().contains(c);
	}

	public ArrayList<Course> getCourses() {
		return _courses;
	}

	public void set_courses(ArrayList<Course> _courses) {
		this._courses = _courses;
	}
}
