package Course;

import Schedule.Schedule;


public class Room {
	public String name; //e.g. 1A (Eddie's Room) or DS (Dance Studio)
	public Schedule sch;
	
	public Room(String name){
		this.name = name;
		this.sch = new Schedule();
	}
}
