package com.eltecalendar;

import java.util.ArrayList;
import java.util.List;

public class Course {
	public Subject subject;
	public int courseNumber;
	public String classType;
	
	public int Id;
	public Teacher teacher;
	public List<Occasion> timetable = new ArrayList<Occasion>();
	
	@Override
	public boolean equals(Object other){
		if(this == other) return true;
		if(other.getClass() != this.getClass()) return false;
		else {
			Course rhs = (Course) other;
			return rhs.subject.equals(subject) && rhs.courseNumber == courseNumber;
		}
	}
}
