package com.eltecalendar;

public class Subject{
	public String code;
	public String name;
	
	public int Id;
	// TODO
	public int term; // ez itt nem értelmezhetõ
	
	@Override
	public boolean equals(Object other){
		if(this == other) return true;
		if(other.getClass() != this.getClass()) return false;
		else {
			Subject rhs = (Subject) other;
			return rhs.code == code && rhs.name == name && rhs.Id == Id;
		}
	}
}