package People;

import java.util.HashMap;

import Schedule.Schedule;


public abstract class Person {
    public Schedule sch = new Schedule();
    public String name; // Full name, (First M Last)
    public HashMap<String, Integer> artsLevels = new HashMap<String, Integer>(); // Level of theater, dance, music, art
    
    public int totalCourses = 0;
    public int coursesLeftToSchedule = 0;
    
    public Person(String name){
        this.name = name;
    }
    
    public void addCoursesToSchedule(int freq){
        totalCourses += freq;
        coursesLeftToSchedule += freq;
    }
}
