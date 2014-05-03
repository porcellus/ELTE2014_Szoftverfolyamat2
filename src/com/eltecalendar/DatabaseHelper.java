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
		Course course = new Course();
		Cursor row = this.getReadableDatabase().query("Courses", null, "SubjectId = ? and CourseNumber = ?", new String[] {Integer.toString(sId), Integer.toString(number)}, null, null, null);
		
		row.moveToNext();
		course.courseNumber = row.getInt(2);
		course.teacher = getTeacher(row.getInt(3));
		course.subject = subject;
		
		
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
			course.courseNumber = rows.getInt(2);
			course.subject = subject;
			course.teacher = getTeacher(rows.getInt(3));
			courses.add(course);
		}
		
		return courses;
	}
	
	public Teacher getTeacher(int Id)
	{
		Teacher teacher = new Teacher();
		
		Cursor row = this.getReadableDatabase().rawQuery("select * from Teachers where Id=?", new String[] {Integer.toString(Id)});
		row.moveToNext();
		teacher.Id = row.getInt(0);
		teacher.name = row.getString(1);
		
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
	}
	
	public void addTeacher(Teacher teacher)
	{
		ContentValues values = new ContentValues();
		values.put("Name", teacher.name);
		
		teacher.Id = (int)this.getWritableDatabase().insert("Teachers", null, values);
	}
}
