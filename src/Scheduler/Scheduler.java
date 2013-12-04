package Scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Course.Course;
import Course.Room;
import People.Teacher;
import People.Student;
import Schedule.Opening;
import Schedule.Schedule;


public class Scheduler {
    public static final int runTimes = 1; // Used only for testing efficiency
    
    private static final String MF = "Matthew";
    private static final String ML = "Huggins";
    private static final String CF = "Caleb";
    private static final String CL = "Shapiro";
    
    public static HashMap<String, Integer> failPoints = new HashMap<String, Integer>();
    public static HashMap<String, Course> courses = new HashMap<String, Course>();
    public static ArrayList<String> teacherNames = new ArrayList<String>();
    public static HashMap<String, Teacher> teachers = new HashMap<String, Teacher>();
    public static HashMap<String, Student> students  = new HashMap<String, Student>();
    public static HashMap<String, Room>   rooms   = new HashMap<String, Room>();
    
    private static boolean repeatDays = false;
    private static boolean allowLunch = false;
    
    public static void main(String[] args) {
        ArrayList<Integer> attemptsHistory = new ArrayList<Integer>();
        /** Pinnacle of coding. **/
        System.out.println("Hello, world!");
        
        System.out.println("Reading files.");
        input();
        
        System.out.println("Scheduling courses.");
        while (attemptsHistory.size() != runTimes){
            attemptsHistory.add(schedule());
            output();
        }
        
        int avgAttempts = 0;
        for (int i=0; i < attemptsHistory.size(); i++)
            avgAttempts += attemptsHistory.get(i);
        System.out.println("AVG: " + avgAttempts/attemptsHistory.size());
       
        System.out.println("Outputting schedules");
        output();
        
        System.out.println("Goodbye, world!");
    }
    
    
    public static ArrayList<String> qsort(ArrayList<String> list){
        if (list.size() == 0){
            return list;
        } else {
            String pivot = list.get(0);
            ArrayList<String> LHS = new ArrayList<String>();
            ArrayList<String> RHS = new ArrayList<String>();
            ArrayList<String> PIV = new ArrayList<String>();
            
            double pPriority = teachers.get(pivot).priority;
            for (int i = 0; i < list.size(); i++){
                double iPriority = teachers.get(list.get(i)).priority;
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
    
    protected static void input(){
        /* Read input and construct all of the objects. */
        File dir = new File(Input.path);
        
        /* Make sure the teachers all exist before you start trying to restrict them. */
        for (File child : dir.listFiles()){
            String fname = child.getAbsolutePath();
            if (fname.contains(Input.crsFEnd))
                Input.parseCrsFile(fname);
        }
        
        for (File child : dir.listFiles()){
            String fname = child.getAbsolutePath();
            if (fname.contains(Input.tchFEnd))
                Input.parseTchFile(fname);
        }
        
        teacherNames = new ArrayList<String>(teachers.keySet());
    }
    
    protected static void calebAndMatthewClaws(){
        for (Student student : students.values()){
            if (student.sch.openings.size() > 5){
                if (student.name.contains(MF) && student.name.contains(ML)){
                    student.sch.restrictTime(0);
                } else if (student.name.contains(CF) && student.name.contains(CL)){
                    student.sch.restrictTime(0);
                }
            }
        }
    }
    
    protected static void setup(){
        /* Reset all courses, rooms, and people */
        for (Student student : students.values()){
            student.sch.refresh();
            student.coursesLeftToSchedule = student.coursesToSchedule;
        }
        
        for (Teacher teacher : teachers.values()){
        	teacher.refresh();
        	teacher.coursesLeftToSchedule = teacher.coursesToSchedule;
        }
        
        for (Room room : rooms.values()){
            room.sch.refresh();
        }
        
        for (Course course : courses.values()){
            course.refresh();
            course.getPriority();
        }
        
        /* Queue classes based on priority. */
        teacherNames = new ArrayList<String>(teachers.keySet());
        teacherNames = qsort(teacherNames);
        
        /* Shh! */
        //calebAndMatthewClaws();
    }
    
    protected static HashSet<Opening> checkPossible(Course course){
        /* Find the intersection of the openings all people. */
        ArrayList<String> TeacherOpenings = teachers.get(course.teacher).sch.getOpenings();
        HashSet<String> Possible = new HashSet<String>(TeacherOpenings);

        /* retainAll() uses "==", so you need to store possible as a hashset of
         * strings, then go through openings and remove the overlap manually. */
        for (String student : course.students){
            Possible.retainAll(students.get(student).sch.getOpenings());
        }
        
        HashSet<Opening> Out = new HashSet<Opening>();
        for (String opening : Possible){
            String[] parts = opening.split(" ");
            Out.add(new Opening(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));       
        }
        
        return Out;
    }
    
    protected static boolean findOpenings(Course course, HashSet<Opening> openings){
        boolean[] OpenDays = {true, true, true, true, true};
        boolean Found = true; // Whether a room has been found
        
        course.prioritizeRooms();
        ArrayList<String> roomsList = new ArrayList<String>(course.rooms);
        
        for (int i = 0; i < course.freq; i++){
            Found = false;
            
            rooms_list: for (String room : roomsList){
                /* Stop checking rooms if you found one already. */
                if (Found){
                    break rooms_list;
                } else {
                    openings_loop: for (Opening opening : openings){
                        /* Find an opening that is not on a day already used
                         * and that works for the room as well as the people. */
                        if (opening.time == Schedule.LUNCH && !allowLunch){
                            continue openings_loop;
                        } else if (!OpenDays[opening.day] && !repeatDays){
                            continue openings_loop;
                        } else if (!rooms.get(room).sch.restricted(opening)){
                            /* Finalize the choice in everyone's schedule. */
                          //Room
                            course.roomsByDay[opening.day] = room;
                            rooms.get(room).sch.scheduleCourse(opening, course);
                          //Teacher
                            teachers.get(course.teacher).sch.scheduleCourse(opening, course);
                            teachers.get(course.teacher).coursesLeftToSchedule -= 1;
                            //Students
                            for (String student : course.getStudents()){
                                students.get(student).sch.scheduleCourse(opening, course);
                                students.get(student).coursesLeftToSchedule -= 1;
                            }

                            /* Mark that the day has been used. */
                            OpenDays[opening.day] = false;

                            Found = true;
                            
                            ArrayList<Opening> viable = new ArrayList<Opening>();
                            for (Opening ope : openings){
                                if (!repeatDays){
                                    if (ope.day != opening.day)
                                        viable.add(ope);
                                } else {
                                    if (ope.day != opening.day || ope.time != opening.time)
                                        viable.add(ope);
                                }
                            }
                            
                            break openings_loop;
                        }
                    }
                }
            }

            if (!Found)
                return false;
        }
        
        return Found;
    }
    
    protected static boolean findTimeFor(Course course){
        /* Check if there are any openings first. */
        HashSet<Opening> Possible = checkPossible(course);
        if (Possible.isEmpty()){
            return false;
        } else {
            /* Return true only if findOpenings was successful. */
            return findOpenings(course, Possible);
        }
    }
    
    protected static int schedule(){
        /* Start your schedulers! */
        boolean Scheduled = false;
        
        /* If at first you don't succeed... */
        int attempts = 0;
        while (!Scheduled){
            Scheduled = true;
            setup();
            
            while (!teacherNames.isEmpty()){
            	String name = teacherNames.get(0);
            	for (String course : teachers.get(name).getCourses()){
	                if (!findTimeFor(courses.get(course))){
	                    Scheduled = false;
	                    //System.out.println("Failed to fully schedule " + teacherNames.get(0));
	                    failPoints.put(course, failPoints.get(course) + 1);
	                }
            	}
            	
				teacherNames.remove(0);
				teacherNames = qsort(teacherNames);
            }
            
            attempts += 1;
        }
        
        System.out.println("Attempts: " + attempts);
        for (Course course : courses.values())
            Scheduler.failPoints.put(course.name, 0);
        return attempts;
    }
    
    protected static void output(){
        /* Output all data in its prescribed location. */
        for (Student student : students.values()){
            Output.writeSch(student.sch.schedule, "schedules/students/" + student.name);       
        }
        
        for (Teacher teacher : teachers.values()){
        	Output.writeSch(teacher.sch.schedule, "schedules/teachers/" + teacher.name);
        }
    }
    
}