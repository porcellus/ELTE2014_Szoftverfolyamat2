package com.eltecalendar;

public class Occasion {
	
	public enum Day {
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY;
	}
	
	public Day onDay;
	public int startTimeHour;
	public int startTimeMinute;
	public int endTimeHour;
	public int endTimeMinute;
	public String building;
	public String room;
}
