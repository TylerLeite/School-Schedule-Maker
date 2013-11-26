package People;

import Schedule.Schedule;


public abstract class Person {
    public Schedule sch = new Schedule();
    public String name; // Full name, (First M Last)
    
    public int coursesToSchedule = 0;
    public int coursesLeftToSchedule = 0;
    
    public Person(String name){
        this.name = name;
    }
    
    public void addCoursesToSchedule(int freq){
    	coursesToSchedule += freq;
    	coursesLeftToSchedule += freq;
    }
}
