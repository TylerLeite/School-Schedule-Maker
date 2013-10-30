package Course;

import java.util.ArrayList;

import Scheduler.Scheduler;
import Schedule.Schedule;


public class Course {
	public String name;
	public ArrayList<String> people = new ArrayList<String>(); // Important that teacher is 1st element!
	public ArrayList<String> rooms  = new ArrayList<String>();
	public String[] roomsByDay = new String[Schedule.D]; // Caleb, you better think of something clever
	public int freq;
	
	public Course(String name, int freq){
		this.name = name;
		this.freq = freq;
	}
	
	public void refresh(){
		roomsByDay = new String[Schedule.D];
	}
	
	public String getString(int day){
		/* Return course name, room name, and teacher name for output. */
		return String.format("%s with %s in %s", name, people.get(0), roomsByDay[day]);
	}
	
	public int getPriority(){
		int rand = (int)(Math.random() * 6) - 2;
		return rooms.size() + Scheduler.people.get(people.get(0)).sch.openings.size() + rand;
	}
	
	public void addRoom(String room){
		/* Add a possible room, used during input. */
		if (!rooms.contains(room)) //Prevent bugs, Caleb, it's good for you.
			rooms.add(room);
	}
	
	public void addPerson(String person){
		/* Add am enrolled student, used during input. */
		if (!people.contains(person)) //Prevent bugs, Caleb, it's good for you.
			people.add(person);
	}
	
	public String getTeacher(){
		/* Make sure that the teacher is the first element in the person list.
		 * This is easy as long as input is structured correctly. Alternatively, 
		 * you can iterate through the whole list and check if a person is an 
		 * instance of Teacher, but I think this way is better. */
		return people.get(0);
	}
	
	public ArrayList<String> getStudents(){
		/* Return a list of all students, cast back to Students. */
		ArrayList<String> Students = new ArrayList<String>();
		for (int i = 1; i < people.size(); i++)
			Students.add(people.get(i));
		
		return Students;
	}
}
