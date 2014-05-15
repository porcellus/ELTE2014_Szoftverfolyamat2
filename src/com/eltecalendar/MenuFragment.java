package com.eltecalendar;

import java.util.ArrayList;
import java.util.List;

import com.eltecalendar.MyExpandableListAdapter.Group;
import com.eltecalendar.Occasion.Day;

import android.app.Fragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuFragment extends Fragment {
	private class TimeTableObserver extends DataSetObserver {
		
		Group _group;
		MainActivity _act;
		MyExpandableListAdapter _listAdapter;
		public TimeTableObserver(Group group, MainActivity activity, MyExpandableListAdapter listAdapter){
			_group=group;
			_act=activity;
			_listAdapter=listAdapter;
			onChanged();
		}
		
		@Override
		public void onChanged(){
			while(_group.children.size() > 0)_group.removeChild(0);
			for(final Course c: _act.getTimeTable().getCourses()){
				_group.addChild(new Group.Child(c.subject.name + "-" + c.courseNumber){
					@Override
					public boolean onChildClick(ExpandableListView parent, View v,
							int groupPosition, int childPosition, long id) {
						MainActivity act = (MainActivity)getActivity();
						act.getTimeTable().remove(c);
						return false;
					}
				});
			}
			_listAdapter.notifyDataSetChanged();
		}
	}
	
	public class SearchBox extends Group.Child {
		public SearchBox() {
			super("Keresés bevitel");
		}
		/*
		@Override
		public View getView(int groupPosition, final int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent){
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);
            }
            return convertView;
    	}*/
	}
	
	public class CourseItem extends Group.Child {
		public Course course;
		public CourseItem(Course pCourse){
			super(pCourse.subject.name + " " + pCourse.courseNumber);
			course = pCourse;
		}
		
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			android.util.Log.w("debug",course.subject.name+ course.courseNumber + " clicked, occasions: " + course.timetable.size());
			MainActivity act = (MainActivity)getActivity();
			if(act.getTimeTable().contains(course)) act.getTimeTable().remove(course);
			else act.getTimeTable().addCourse(course);
			return false;
		}
		/*
		@Override
    	public View getView(int groupPosition, final int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent){
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);
            }
     
            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);
            txtListChild.setText(name);
            txtListChild.setTextColor(Color.MAGENTA);
            android.util.Log.w("debug", course.subject.name + course.courseNumber + ".getView: " + txtListChild.getText());
            return convertView;
    	}*/
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		final DatabaseHelper dbh = new DatabaseHelper(getActivity());
		DataMiner wm = new DataMiner(dbh);
		
		try{
			wm.getNewDatabase();
		} catch(Exception ex) {
			android.util.Log.e("dataminer exception", ex.toString());
		}
		
		View ret = inflater.inflate(R.layout.fragment_menu, container, false);
		ExpandableListView listView = (ExpandableListView) ret.findViewById(R.id.expandableListView1);
		MyExpandableListAdapter list = new MyExpandableListAdapter(getActivity());

		Group kereses = new MyExpandableListAdapter.Group("Tárgyfelvétel");
		
		kereses.addChild(new SearchBox());
		
		kereses.addChild(
			new Group.Child("Keresés"){
				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
					MyExpandableListAdapter adapter = (MyExpandableListAdapter)parent.getExpandableListAdapter();
					Group group = (MyExpandableListAdapter.Group)adapter.getGroup(0);
					List<Subject> found = dbh.getSubjects("tervezés és kivitelezés");
					if(found.isEmpty()) found = MockHelper.getSubjects(
							//((EditText)parent.findViewById(R.id.searchbox)).getText().toString()
									""
									);
					final int size = found.size();
					while(group.children.size() > 2) group.removeChild(2);
					if(!found.isEmpty()){
						for(final Subject c: found){
							group.addChild(new Group.Child(c.name){
								@Override
								public boolean onChildClick(ExpandableListView parent, View v,
										int groupPosition, int childPosition, long id) {

									MyExpandableListAdapter adapter = (MyExpandableListAdapter)parent.getExpandableListAdapter();
									Group group = (MyExpandableListAdapter.Group)adapter.getGroup(0);
									List<Course> children = dbh.getCourses(c).isEmpty() ? MockHelper.getCourses(c) : dbh.getCourses(c);
									android.util.Log.w("debug",c.name + " clicked ->" + children.size());
									if(group.children.size() > size + 2)
										while(group.children.size() > size + 2) group.removeChild(size + 2);
									else {
										group.addChild("Separator");
										for(Course c: children){
											android.util.Log.w("debug",c.subject.name+ c.courseNumber + " added, occasions: " + c.timetable.size());
											group.addChild(new CourseItem(c));
										}
									}
									adapter.notifyDataSetChanged();
									return false;
								}
							});
						}
					} else group.addChild("Nincs találat");
					adapter.notifyDataSetChanged();
					return false;
				}
			});
		
		MyExpandableListAdapter.Group felvett = new MyExpandableListAdapter.Group("Felvett tárgyak");
		MainActivity act = (MainActivity)getActivity();
		act.getTimeTable().registerDataSetObserver(new TimeTableObserver(felvett,act,list));

		
		list.addGroup(kereses);
		list.addGroup(felvett);
		
		/*
		MyExpandableListAdapter.Group test = new MyExpandableListAdapter.Group("Teszt");
		test.addChild(new MyExpandableListAdapter.Group.Child("Teszt felvétel"){
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
			Course course = new Course();
			course.subject = new Subject();
			course.subject.name = "Szoftverfolyamatok fejlesztése 2 gyakorlat";
			course.timetable = new ArrayList<Occasion>();
			Occasion occ = new Occasion();
			occ.onDay = Day.WEDNESDAY;
			occ.startTimeHour = 10;
			occ.startTimeMinute = 0;
			occ.endTimeHour = 11;
			occ.endTimeMinute = 30;
			occ.building = "Déli";
			occ.room = "0.817";
			course.timetable.add(occ);
			MainActivity act = (MainActivity)getActivity();
			if(act.getTimeTable().contains(course)) act.getTimeTable().remove(course);
			else act.getTimeTable().addCourse(course );
			return false;
		}});
		list.addGroup(test);
		*/
		
		list.attachToExpandableListView(listView);
		
		return ret; 
	}

}
