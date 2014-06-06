CREATE TABLE Calendar
(
	Id INTEGER,
	Name TEXT,
	CONSTRAINT PK_Calendar PRIMARY KEY (Id)
)
;

CREATE TABLE Courses
(
	Id INTEGER,
	SubjectId INTEGER,
	CourseNumber INTEGER,
	TeacherId INTEGER,
	CONSTRAINT PK_Courses PRIMARY KEY (Id),
	CONSTRAINT FK_Courses_Subjects FOREIGN KEY (SubjectId)
		REFERENCES Subjects(Id) ,
	CONSTRAINT FK_Courses_Subjects FOREIGN KEY (SubjectId)
		REFERENCES Subjects(Id) ,
	CONSTRAINT FK_Courses_Teachers FOREIGN KEY (TeacherId)
		REFERENCES Teachers(Id) 
)
;

CREATE TABLE Schedule
(
	Id INTEGER,
	CourseId INTEGER,
	CalendarId INTEGER,
	CONSTRAINT PK_Schedule PRIMARY KEY (Id),
	CONSTRAINT FK_Schedule_Calendar FOREIGN KEY (CalendarId)
		REFERENCES Calendar(Id) ,
	CONSTRAINT FK_Schedule_Calendar FOREIGN KEY (CalendarId)
		REFERENCES Calendar(Id) ,
	CONSTRAINT FK_Schedule_Courses FOREIGN KEY (CourseId)
		REFERENCES Courses(Id) 
)
;

CREATE TABLE Subjects
(
	Id INTEGER,
	Name TEXT,
	Code TEXT,
	Term INTEGER,
	CONSTRAINT PK_Subjects PRIMARY KEY (Id)
)
;

CREATE TABLE Teachers
(
	Id INTEGER,
	Name TEXT,
	CONSTRAINT PK_Teachers PRIMARY KEY (Id)
)
;

CREATE TABLE TimePlace
(
	Id INTEGER,
	CourseId INTEGER,
	Begin TEXT,
	End TEXT,
	Place TEXT,
	Day TEXT,
	CONSTRAINT PK_TimePlace PRIMARY KEY (Id),
	CONSTRAINT FK_TimePlace_Courses FOREIGN KEY (CourseId)
		REFERENCES Courses(Id) 
)
;

CREATE TABLE Users
(
	Id INTEGER,
	Username TEXT,
	CONSTRAINT PK_Users PRIMARY KEY (Id)
)
;
