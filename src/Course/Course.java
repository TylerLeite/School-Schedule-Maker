package Course;

import java.util.ArrayList;

import Scheduler.Scheduler;
import Schedule.Schedule;

public class Course {
    public String name;
    public ArrayList<String> people = new ArrayList<String>(); // Important that teacher is 1st element!
    public ArrayList<String> rooms  = new ArrayList<String>();
    public String[] roomsByDay = new String[Schedule.D]; // Caleb, you better think of something clever
    public String[] roomsByDayBak = new String[Schedule.D];
    public int freq;
    public double priority;
    public boolean isMutable = false;
    
    public Course(String name, int freq){
        this.name = name;
        this.freq = freq;
    }
    
    public void refresh(){
        roomsByDay = new String[Schedule.D];
        getPriority();
    }
    
    public String getString(int day){
        /* Return course name, room name, and teacher name for output. */
        return String.format("%s with %s in %s", name, people.get(0), roomsByDay[day]);
    }
    
    public void getPriority(){ //Good work, Caleb!
    	/* Implemented as a setter so randomness in priority doesn't mess with qsort */

    	double result = 1;
        int teacherDenom = Scheduler.people.get(people.get(0)).sch.openings.size();
        int teacherNum = Scheduler.people.get(people.get(0)).coursesLeftToSchedule;
        
        // When num/denom is closer to 1, the more filled up, so we "1 -" it
        double teacherRatio = 1 - (double)teacherNum/teacherDenom; // Always 0 <= ratio <= 1
        result = teacherRatio * Math.pow(.9, Scheduler.failPoints.get(name)); //10% more priority each fail-time
 
        
        priority = result;// + Math.random();
    }
    
    public String getMostRestrictedStudent(){
    	String mostRestrictedStudent = "ERR404";
    	double mostRestrictedStudentRestriction = 1;
    	for (String stud : getStudents()){
    		int studentDenom = Scheduler.people.get(stud).sch.openings.size();
	        int studentNom   = Scheduler.people.get(stud).coursesLeftToSchedule;
	        double result = 1 - studentNom/studentDenom;
	        if (result < mostRestrictedStudentRestriction){
	        	mostRestrictedStudentRestriction = result;
	        	mostRestrictedStudent = stud;
	        }
    	}
    	
    	return mostRestrictedStudent;
    }
    
    public static ArrayList<String> qsort(ArrayList<String> list){
        if (list.size() == 0){
            return list;
        } else {
            String pivot = list.get(0);
            ArrayList<String> LHS = new ArrayList<String>();
            ArrayList<String> RHS = new ArrayList<String>();
            ArrayList<String> PIV = new ArrayList<String>();
            
            int pPriority = 45 - Scheduler.rooms.get(pivot).sch.openings.size();
            for (int i = 0; i < list.size(); i++){
                int iPriority = 45 - Scheduler.rooms.get(list.get(i)).sch.openings.size();
                if (iPriority < pPriority){
                    LHS.add(list.get(i));
                } else if (iPriority > pPriority){
                    RHS.add(list.get(i));
                } else {
                    PIV.add(list.get(i));
                }
            }
            
            ArrayList<String> sorted = new ArrayList<String>();
            sorted.addAll(qsort(LHS));
            sorted.addAll(PIV);
            sorted.addAll(qsort(RHS));
            return sorted;
        }
    }
    
    public void prioritizeRooms(){
        rooms = qsort(rooms);
    }
    
    public void addRoom(String room){
        /* Add a possible room, used during input. */
        if (!rooms.contains(room)){ //Prevent bugs, Caleb, it's good for you.
            rooms.add(room);
        }
    }
    
    public void addPerson(String person){
        /* Add am enrolled student, used during input. */
        if (!people.contains(person)){ //Prevent bugs, Caleb, it's good for you.
            people.add(person);
        }
    }
    
    public void removePerson(String person){
    	people.remove(person);
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
        for (int i = 1; i < people.size(); i++){
            Students.add(people.get(i));
        }
        
        return Students;
    }
}
