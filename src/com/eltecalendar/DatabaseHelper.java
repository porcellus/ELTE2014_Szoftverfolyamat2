package com.eltecalendar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	Context context;
	
	
	public DatabaseHelper(Context context)
	{
		super(context, "elteschedule", null, 2);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		StringBuilder query = new StringBuilder();
		
		try
		{
			InputStream sql = context.getResources().openRawResource(R.raw.schedule);
			BufferedReader br = new BufferedReader(new InputStreamReader(sql));
			
			String readline = new String();
			while ((readline = br.readLine()) != null)
			{
				query.append(readline);
				if (readline.contains(";"))
				{
					db.execSQL(query.toString());
					query.setLength(0);
				}
			}
			
		}
		catch (Exception e)
		{
			Log.v(e.getClass().toString(), e.getMessage());
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public Course getCourse(Subject subject, int number)
	{
		int sId = subject.Id;
		
		Cursor row = this.getReadableDatabase().query("Courses", null, "SubjectId = ? and CourseNumber = ?", new String[] {Integer.toString(sId), Integer.toString(number)}, null, null, null);
		
		row.moveToNext();
		Course course = new Course();
		course.subject = subject;
		course.teacher = getTeacher(row.getInt(3));
		course.courseNumber = row.getInt(2);
		course.Id = row.getInt(0);
		
		return course;
	}
	
	public List<Subject> getSubjects(String name)
	{
		List<Subject> subjects = new ArrayList<Subject>();
		
		String xy = "%" + name + "%";
		String[] args = new String[] {xy};
		
		Cursor rows = this.getReadableDatabase().query("Subjects", null, "Name like ?", args, null, null, null);
		
		while (rows.moveToNext())
		{
			Subject subject = new Subject();
			subject.name = rows.getString(1);
			subject.code = rows.getString(2);
			subject.Id = rows.getInt(0);
			subjects.add(subject);
		}
		
		return subjects;
	}
	
	public List<Course> getCourses(Subject subject)
	{
		int sId = subject.Id;
		List<Course> courses = new ArrayList<Course>();
		
		Cursor rows = this.getReadableDatabase().query("Courses", null, "SubjectId = ?", new String[] {Integer.toString(sId)}, null, null, null);
		while (rows.moveToNext())
		{
			Course course = new Course();
			course.subject = subject;
			course.teacher = getTeacher(rows.getInt(3));
			course.courseNumber = rows.getInt(1);
			course.Id = rows.getInt(0);
			courses.add(course);
			
			Cursor timeRows = this.getReadableDatabase().rawQuery("select * from TimePlace where CourseId = ?", new String[] {Integer.toString(course.Id)});
			while (timeRows.moveToNext())
			{
				
			}
		}
		
		return courses;
	}
	
	public Teacher getTeacher(int Id)
	{
		
		Cursor row = this.getReadableDatabase().rawQuery("select * from Teachers where Id=?", new String[] {Integer.toString(Id)});
		row.moveToNext();
		Teacher teacher = new Teacher();
		teacher.name = row.getString(1);
		teacher.Id = row.getInt(0);
		
		return teacher;
	}
	
	public void addSubject(Subject subject)
	{
		ContentValues values = new ContentValues();
		values.put("Name", subject.name);
		values.put("Code", subject.code);
		values.put("Term", subject.term);
		subject.Id = (int)this.getWritableDatabase().insert("Subjects", null, values);
	}
	
	public void addCourse(Course course)
	{
		ContentValues values = new ContentValues();
		
		values.put("SubjectId", course.subject.Id);
		values.put("CourseNumber", course.courseNumber);
		values.put("TeacherId", course.teacher.Id);
		
		course.Id = (int)this.getWritableDatabase().insert("Courses", null, values);
		
		for (Occasion i : course.timetable)
		{
			ContentValues occassion = new ContentValues();
			StringBuilder time = new StringBuilder();
			time.append(i.startTimeHour);
			time.append(":");
			time.append(i.startTimeMinute);
			occassion.put("CourseId", course.Id);
			occassion.put("Begin", time.toString());
			time.setLength(0);
			time.append(i.endTimeHour);
			time.append(":");
			time.append(i.endTimeMinute);
			occassion.put("End", time.toString());
			occassion.put("Place", i.room);
			occassion.put("Day", i.onDay.toString());
			this.getWritableDatabase().insert("TimePlace", null, occassion);
		}
	}
	
	public void addTeacher(Teacher teacher)
	{
		ContentValues values = new ContentValues();
		values.put("Name", teacher.name);
		
		teacher.Id = (int)this.getWritableDatabase().insert("Teachers", null, values);
	}
}
