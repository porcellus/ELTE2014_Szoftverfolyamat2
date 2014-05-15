package com.eltecalendar;

import java.util.ArrayList;
import java.util.List;

import com.eltecalendar.Occasion.Day;

public class MockHelper {
	public static ArrayList<Subject> _subjects = new ArrayList<Subject>();
	public static ArrayList<Course> _courses = new ArrayList<Course>();
	static {		
		Subject szoftfoly = new Subject();
		szoftfoly.name = "Szoftverfolyamatok fejlesztése 2 gyakorlat";
		szoftfoly.code = "AS123";
		szoftfoly.term = 20142;
		
		Subject akarmi = new Subject();
		akarmi.name = "Akarmi EA";
		akarmi.code = "AS12";
		akarmi.term = 20142;

		Course course = new Course();
		course.subject = szoftfoly;
		Occasion occ = new Occasion();
		occ.onDay = Day.WEDNESDAY;
		occ.startTimeHour = 10;
		occ.startTimeMinute = 0;
		occ.endTimeHour = 11;
		occ.endTimeMinute = 30;
		occ.building = "Déli";
		occ.room = "0.817";
		course.courseNumber = 1;
		course.timetable.add(occ);

		Course course2 = new Course();
		course2.subject = szoftfoly;
		Occasion occ2 = new Occasion();
		occ2.onDay = Day.WEDNESDAY;
		occ2.startTimeHour = 12;
		occ2.startTimeMinute = 0;
		occ2.endTimeHour = 13;
		occ2.endTimeMinute = 30;
		occ2.building = "Déli";
		occ2.room = "0.817";
		course2.courseNumber = 2;
		course2.timetable.add(occ2);
		

		Course course3 = new Course();
		course3.subject = akarmi;
		Occasion occ3 = new Occasion();
		occ3.onDay = Day.THURSDAY;
		occ3.startTimeHour = 12;
		occ3.startTimeMinute = 0;
		occ3.endTimeHour = 13;
		occ3.endTimeMinute = 30;
		occ3.building = "Déli";
		occ3.room = "0.821";
		course3.courseNumber = 1;
		course3.timetable.add(occ3);

		_courses.add(course);
		_courses.add(course2);
		_courses.add(course3);
		
		_subjects.add(szoftfoly);
		_subjects.add(akarmi);
	}
	
	public static List<Subject> getSubjects(String name){
		ArrayList<Subject> ret = new ArrayList<Subject>();
		for(Subject c : _subjects){
			if(c.name.contains(name)) ret.add(c);
		}
		return ret;
	}
	
	public static List<Course> getCourses(Subject s){
		ArrayList<Course> ret = new ArrayList<Course>();
		for(Course c : _courses){
			if(c.subject == s) ret.add(c);
		}
		return ret;
	}
}
