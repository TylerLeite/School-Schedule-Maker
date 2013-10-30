package People;

import Schedule.Schedule;


public abstract class Person {
	public Schedule sch = new Schedule();
	public String name; // Full name, (First M Last)
	
	public Person(String name){
		this.name = name;
	}
}
