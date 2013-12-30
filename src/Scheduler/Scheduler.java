package Scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Course.Course;
import Course.Room;
import People.Person;
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
    public static ArrayList<String> courseNames = new ArrayList<String>();
    public static HashMap<String, Course> courses = new HashMap<String, Course>();
    public static HashMap<String, Person> people  = new HashMap<String, Person>();
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
            
            double pPriority = courses.get(pivot).priority;
            for (int i = 0; i < list.size(); i++){
                double iPriority = courses.get(list.get(i)).priority;
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
        
        courseNames = new ArrayList<String>(courses.keySet());
        
        for (File child : dir.listFiles()){
            String fname = child.getAbsolutePath();
            if (fname.contains(Input.tchFEnd))
                Input.parseTchFile(fname);
        }
    }
    
    protected static void calebAndMatthewClaws(){
        for (Person person : people.values()){
            if (person.sch.openings.size() > 5){
                if (person.name.contains(MF) && person.name.contains(ML)){
                    person.sch.restrictTime(0);
                } else if (person.name.contains(CF) && person.name.contains(CL)){
                    person.sch.restrictTime(0);
                }
            }
        }
    }
    
    protected static void setup(){
        /* Reset all courses, rooms, and people */
        for (Person person : people.values()){
            person.sch.refresh();
            person.coursesLeftToSchedule = person.totalCourses;
        }
        
        for (Room room : rooms.values()){
            room.sch.refresh();
        }
        
        for (Course course : courses.values()){
            course.refresh();
            course.getPriority();
        }
        
        /* Queue classes based on priority. */
        courseNames = new ArrayList<String>(courses.keySet());
        courseNames = qsort(courseNames);
        
        /* Shh! */
        //calebAndMatthewClaws();
    }
    
    protected static HashSet<Opening> checkPossible(Course course){
        /* Find the intersection of the openings all people. */
        ArrayList<String> TeacherOpenings = people.get(course.getTeacher()).sch.getOpenings();
        HashSet<String> Possible = new HashSet<String>(TeacherOpenings);

        /* retainAll() uses "==", so you need to store possible as a hashset of
         * strings, then go through openings and remove the overlap manually. */
        for (String student : course.getStudents()){
            Possible.retainAll(people.get(student).sch.getOpenings());
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
                            people.get(course.getTeacher()).sch.scheduleCourse(opening, course);
                            people.get(course.getTeacher()).coursesLeftToSchedule -= 1;
                            //Students
                            for (String student : course.getStudents()){
                                people.get(student).sch.scheduleCourse(opening, course);
                                people.get(student).coursesLeftToSchedule -= 1;
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
                            
                            openings = new HashSet<Opening>(viable);
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
    
    protected static HashMap<String, HashMap<String, Integer>> getConnectionMap(String course){
    	/* NOTE: Can be made faster by making students keep track of what classes
    	 * they are in. This can be done by adding to a list when the courses
    	 * are being created and then changing it in this part (shuffling). I
    	 * am going to be optimizing after I am sure everything works, so for now
    	 * it will stay as is. */
    	
    	/* Loop through students and see who shares the most courses */
    	HashMap<String, HashMap<String, Integer>> imTheMap = new HashMap<String, HashMap<String, Integer>>();
    	for (String student : courses.get(course).getStudents()){
    		entry = new HashMap<String, Integer>();
    		
    		for (String subStudent : courses.get(course).getStudents()){
    			if (imTheMap.contains(subStudent)){
    				/* Optimization: reuse results if we can */
    				entry.put(substudent, imTheMap.get(subStudent).get(student));
    			} else {
    				for (Course c : courses.values()){
    					if (c.getStudents().contains(student)){
    						if (c.getStudents().contains(subStudent)){
    							if (entry.contains(substudent))
    								entry.put(substudent, entry.get(substudent) + 1)
    							else
									entry.put(substudent, 1);
    						}
    					}
    				}
    			}
    		}
    		
    		HashMap.put(student, entry);
    	}
    	
    	return imTheMap;
    }
    
    protected static void optimizeArts(HashMap<String, HashMap<String, Integer>> map){
    	/* Arrange students into groups based on information from the connection map */
    	
    	ArrayList<ArrayList<String>> blubblub = new ArrayList<ArrayList<String>>(); // Because there is a kind of fish called a grouper
    	
    	//
    }
    
    protected static boolean move(string studentName, String oldCourse, String direction = "random"){
    	/* TODO: Add the ability to mark arts courses as "immutable" */
    	/* Remove student studentName from course oldCourse and add it to the
    	 * course that is one higher or one lower as specified. */
    	
    	courses.get(oldCourse).people.remove(studentName);
    	int lvl = Integer.parseInt(courseName.replaceAll("\\D+", "")); // Extract the level from the course's title
    	
    	if (direction.equals("up"))
    		lvl++;
    	else if (direction.equals("down"))
    		lvl--;
    	else
    		return false;
    	
    	courseName = courseName.replaceAll("\\D*$", lvl);
    	courses.get(oldCourse).addPerson(studentName);
    	
    	return true;
    }
    
    protected static void actuallyShuffle(String course, ArrayList<ArrayList<String>> groups){
    	int max = 0;
    	ArrayList<String> maxGroup;
    	
    	for (ArrayList<String> group : groups){
    		if (group.size() > max){
    			max = group.size();
    			maxGroup = group;
    		}
    	}
    	
    	groups.remove(maxGroup);
    	
    	String direction;
    	for (ArrayList<String> group : groups){
    		/* Randomly select up or down */
    		if (Math.random() < 0.5)
    			direction = "up";
    		else
    			direction = "down";
    		
    		for (String student : group){
    			move(student, course, direction);
    		}
    	}
    }
    
    protected static void shuffle(String course){
    	HashMap<String, HashMap<String, Integer>> conMap = getConnectionMap(course);
    	ArrayList<ArrayList<String>> groups = omptimizeArts(conMap);
    	actuallyShuffle(course, groups);
    }
    
    protected static int schedule(){
        /* Start your schedulers! */
        boolean Scheduled = false;
        
        /* If at first you don't succeed... */
        int attempts = 0;
        while (!Scheduled){
            Scheduled = true;
            setup();
            
            while (!courseNames.isEmpty()){
                if (!findTimeFor(courses.get(courseNames.get(0)))){
                    Scheduled = false;
                    //System.out.println("Failed to fully schedule " + courseNames.get(0));
                    failPoints.put(courseNames.get(0), failPoints.get(courseNames.get(0)) + 1);
                }
                
                courseNames.remove(0);
                for (Course course : courses.values()){
                    course.getPriority();
                }
                
                courseNames = qsort(courseNames);
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
        for (Room room : rooms.values())
            Output.writeSch(room.sch.schedule, "schedules/rooms/" + room.name);
        
        for (Person person : people.values()){
            if (person.sch.openings.size() < 10)
                System.out.println(person.name);
            if (person instanceof Student)
                Output.writeSch(person.sch.schedule, "schedules/students/" + person.name);
            else
                Output.writeSch(person.sch.schedule, "schedules/teachers/" + person.name);
        }
    }
}

/*//
	For the course shuffling:
		when an arts course fails:
			+look through the students and see who shares the most courses
			group them together that way
			if there is a vast majority (>70%):
				move the outliers into a new part based on shared courses
				if a part would be too small, shift up or down a level, based again on course sharing
			if there is no majority:
				first try to split courses up into parts, e.g. 3a, 3b, 3c
				then try to move people up or down 1 course from their original one
			
			This can get very slow and inefficient, so we may need to keep a 
			master list of what classes are most common among each arts level
//*/
