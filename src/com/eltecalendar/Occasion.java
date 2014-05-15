package com.eltecalendar;

public class Occasion {
	
	public enum Day {
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY{
	        @Override
	        public Day next() {
	            return null; // see below for options for this line
	        };
		};

	    public Day next() {
	        // No bounds checking required here, because the last instance overrides
	        return values()[ordinal() + 1];
	    }
	}
	
	public Day onDay;
	public int startTimeHour;
	public int startTimeMinute;
	public int endTimeHour;
	public int endTimeMinute;
	public String building;
	public String room;
}
