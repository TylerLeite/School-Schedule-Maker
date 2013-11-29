package Schedule;

import java.util.HashSet;
import java.util.ArrayList;

import Course.Course;


public class Schedule {
    public static final int T = 11; // 9 periods per day (Excluding Snack)
    public static final int D = 5; // Approximately 5 school days per week
    public static final int LUNCH = 5;
    
    public HashSet<Opening> openings = new HashSet<Opening>();
    public String[][] schedule = new String[D][T];
    
    public Schedule(){
        /* Initialize everything to "" so you don't have to deal with null. */
        for (int i = 0; i < D; i++){
            for (int j = 0; j < T; j++){
                if (j == LUNCH)
                    schedule[i][j] = "Lunch";
                else 
                    schedule[i][j] = "";
                openings.add(new Opening(i, j));
            }
        }
    }
    
    public void refresh(){
        openings = new HashSet<Opening>();
        for (int i = 0; i < D; i++){
            for (int j = 0; j < T; j++){
                if (!schedule[i][j].equals("DNF")){
                    schedule[i][j] = "";
                    openings.add(new Opening(i, j));
                }
                
                if (j == LUNCH)
                    schedule[i][j] = "Lunch";
            }
        }
    }
    
    public ArrayList<String> getOpenings(){
        ArrayList<String> open = new ArrayList<String>();
        for (Opening opening : openings)
            open.add(opening.toString());
        
        return open;
    }
    
    public boolean scheduleCourse(int day, int time, Course course){
        /* Remove the opening in both the openings HashSet and schedule array. */
        if (schedule[day][time].isEmpty() || schedule[day][time].equals("Lunch")){
            schedule[day][time] = course.getString(day);
            restrict(day, time);
            
            return true;
        } else {
            System.out.println("Schedule spot taken!");
            return false;
        }
    }
    
    public boolean scheduleCourse(Opening opening, Course course){
        return scheduleCourse(opening.day, opening.time, course);
    }
    
    public boolean restricted(int day, int time){
        /* Check if an opening is available. */
        for (Opening opening : openings){
            if (opening.day == day && opening.time == time)
                return false;
        }
        
        return true;
    }
    
    public boolean restricted(Opening check){
        return restricted(check.day, check.time);
    }
    
    public void restrict(int day, int time){
        for (Opening opening : openings){
            if (opening.day == day && opening.time == time){
                openings.remove(opening);
                break;
            }
        }
    }
    
    public void restrict(Opening opening){
        restrict(opening.day, opening.time);
    }
    
    public void restrictTime(int time){
        for (int i = 0; i < 5; i++)
            restrict(i, time);
    }
    
    public void restrictDay(int day){
        for (int i = 0; i < 8; i++)
            restrict(day, i);
    }
}