package Course;

import java.util.ArrayList;

import Scheduler.Scheduler;
import Schedule.Schedule;
import People.Person;


public class Course {
    public String name;
    public String teacher;
    public ArrayList<String> students = new ArrayList<String>(); // Important that teacher is 1st element!
    public ArrayList<String> rooms = new ArrayList<String>();
    public String[] roomsByDay = new String[Schedule.D]; // Caleb, you better think of something clever
    public int freq;
    public double priority;
    
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
        return String.format("%s with %s in %s", name, teacher, roomsByDay[day]);
    }

    public void getPriority(){ //Good work, Caleb!
        /* Implemented as a setter so randomness in priority doesn't mess with qsort */
        double sResult = 1;
        
        for (String student : students){
            Person person = Scheduler.students.get(student);
                int sCC = person.coursesToSchedule;
                int sO= person.sch.openings.size();
                if (sResult < 1 - (double)sCC/sO)
                    sResult = 1 - (double)sCC/sO;
            }
        
        //sResult /= people.size();
        double result = sResult;
        result *= Math.pow(0.1, Scheduler.failPoints.get(name));
        //result += Math.random();
        
        priority = result;
    }
    
    public static ArrayList<String> qsort(ArrayList<String> list){
        if (list.size() == 0)
            return list;
        else {
            String pivot = list.get(0);
            ArrayList<String> LHS = new ArrayList<String>();
            ArrayList<String> RHS = new ArrayList<String>();
            ArrayList<String> PIV = new ArrayList<String>();
            
            int pPriority = 45 - Scheduler.rooms.get(pivot).sch.openings.size();
            for (int i = 0; i < list.size(); i++){
                int iPriority = 45 - Scheduler.rooms.get(list.get(i)).sch.openings.size();
                if (iPriority < pPriority)
                    LHS.add(list.get(i));
                else if (iPriority > pPriority)
                    RHS.add(list.get(i));
                else
                    PIV.add(list.get(i));
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
        if (!rooms.contains(room)) //Prevent bugs, Caleb, it's good for you.
            rooms.add(room);
    }
    
    public void addStudent(String student){
        /* Add an enrolled student, used during input. */
        if (!students.contains(student)) //Prevent bugs, Caleb, it's good for you.
            students.add(student);
    }
    
    public ArrayList<String> getStudents(){
        /* Return a list of all students, cast back to Students. */
        ArrayList<String> Students = new ArrayList<String>();
        for (int i = 0; i < students.size(); i++)
            Students.add(students.get(i));
        
        return Students;
    }
}