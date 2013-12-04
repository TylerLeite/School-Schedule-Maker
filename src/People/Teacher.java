package People;

import java.util.ArrayList;

import Scheduler.Scheduler;


public class Teacher extends Person {
    public double priority = 0;
    public ArrayList<String> courses = new ArrayList<String>();
    public ArrayList<String> coursesLeft = new ArrayList<String>();
    
    public Teacher(String name){
        super(name);
    }
    
    public void getPriority(){
    	priority = coursesLeftToSchedule/sch.openings.size();
    }
    
    public void addCourseToSchedule(String name, int freq){
    	coursesToSchedule += freq;
    	courses.add(name);
    	coursesLeft.add(name);
    }
    
    public static ArrayList<String> qsort(ArrayList<String> list){
        if (list.size() == 0){
            return list;
        } else {
            String pivot = list.get(0);
            ArrayList<String> LHS = new ArrayList<String>();
            ArrayList<String> RHS = new ArrayList<String>();
            ArrayList<String> PIV = new ArrayList<String>();
            
            double pPriority = Scheduler.courses.get(pivot).priority;
            for (int i = 0; i < list.size(); i++){
                double iPriority = Scheduler.courses.get(list.get(i)).priority;
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
    
    public ArrayList<String> getCourses(){
    	return qsort(courses);
    }
    
    public void refresh(){
    	sch.refresh();
    	coursesLeft = courses;
    }
}