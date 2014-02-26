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
    public static ArrayList<String> courseNamesBak = new ArrayList<String>();
    public static ArrayList<String> artNamesBak    = new ArrayList<String>();
    public static ArrayList<String> courseNames    = new ArrayList<String>();
    public static ArrayList<String> artNames       = new ArrayList<String>();
    public static HashMap<String, Course> courses = new HashMap<String, Course>();
    public static HashMap<String, Person> people  = new HashMap<String, Person>();
    public static HashMap<String, Room>   rooms   = new HashMap<String, Room>();
    
    private static boolean repeatDays = false;
    private static boolean allowLunch = true;
    
    public static boolean danceIsArt   = false;
    public static boolean musicIsArt   = true;
    public static boolean artIsArt     = true;
    public static boolean theaterIsArt = false;
    
    //private static ArrayList<String> floaters = new ArrayList<String>();
    
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
        for (int i=0; i < attemptsHistory.size(); i++){
            avgAttempts += attemptsHistory.get(i);
        }
        
        System.out.println("AVG: " + avgAttempts/attemptsHistory.size());
       
        System.out.println("Outputting schedules");
        output();
        
        System.out.println("Goodbye, world!");
    }
    
    protected static void input(){
    	/* Read input and construct all of the objects. */
        File dir = new File(Input.path);
        
        /* Make sure the teachers all exist before you start trying to restrict them. */
        for (File child : dir.listFiles()){
            String fname = child.getAbsolutePath();
            if (fname.contains(Input.crsFEnd)){
                Input.parseCrsFile(fname);
            }
        }
        
        courseNames = new ArrayList<String>(courses.keySet());
        
        for (File child : dir.listFiles()){
            String fname = child.getAbsolutePath();
            if (fname.contains(Input.tchFEnd)){
                Input.parseTchFile(fname);
            }
        }
    }
    
     protected static int schedule(){
        /* Start your schedulers! */
        boolean Scheduled = false;
        
        /* If at first you don't succeed... */
        int attempts = 0;
        while (!Scheduled){
            setup();
        	Scheduled = doAcademics();
            attempts += 1;
            
            System.out.println(attempts);
        }
        
        System.out.println("Attempts: " + attempts);
        
            
        return attempts;
    }
    
    protected static void output(){
        /* Output all data in its prescribed location. */
        for (Room room : rooms.values()){
            Output.writeSch(room.sch.schedule, "schedules/rooms/" + room.name);
        }
        
        /*
        for (Person person : people.values()){
            if (person.sch.openings.size() < 10){
                System.out.println(person.name);
            } if (person instanceof Student){
                Output.writeSch(person.sch.schedule, "schedules/students/" + person.name);
        	} else {
                Output.writeSch(person.sch.schedule, "schedules/teachers/" + person.name);
        	}
        }
        //*/
    }
    
    protected static boolean doAcademics(){
    	 boolean Scheduled = true;
         while (!courseNames.isEmpty()){
         	Course cur = courses.get(courseNames.get(0));
             if (!findTimeFor(cur)){
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
         
         return Scheduled;
    }
    
    protected static boolean doArts(){
		/* Try to schedule arts, generate a list of people who weren't scheduled */
    	
    	backUp();
    	boolean scheduled = false;
    	
    	/*
    	 + for each art class make a set of openings for the teacher
    	 + map each opening to a list of students that share that opening
    	 + find the student who shares the most, remove all other openings and add that student to a list
    	 + repeat until there is no student who shares more than 1
    	 * the list is the new class
    	 * anyone not on the list gets thrown onto the list of unscheduled people
    	 * print the list of unscheduled students by class
    	 * */
    	
		for (String art : artNames){
			ArrayList<String> students = new ArrayList<String>(courses.get(art).getStudents());
			HashSet<Opening>  possible = new HashSet<Opening>(people.get(courses.get(art).getTeacher()).sch.openings);
			HashMap<Opening, ArrayList<String>> openingMap           = new HashMap<Opening, ArrayList<String>>();
			HashMap<String, Integer>		    studentOpeningShares = new HashMap<String, Integer>();
			
			for (String student : students){
				studentOpeningShares.put(student, new Integer(0));
			}
			
			for (Opening opening : possible){
				ArrayList<String> workingStudents = new ArrayList<String>();
    			for (String student : students){
    				if (people.get(student).sch.openings.contains(opening)){ //not sure if this works
    					workingStudents.add(student);
    					studentOpeningShares.put(student, new Integer(studentOpeningShares.get(student).intValue() + 1));
    				}
    			}
    			
    			openingMap.put(opening, workingStudents);
			}
			
			boolean done = false;
			do {
				int max = 0;
				String maxStudent = "";
				for (String student : students){
					if (studentOpeningShares.get(student).intValue() > max){
						max = studentOpeningShares.get(student).intValue();
						maxStudent = student;
					}
				}
				
				if (max <= 1){
					done = true;
				} else {
					ArrayList<Opening> noLongerViable = new ArrayList<Opening>();
					for (Opening opening : possible){
						if (!openingMap.get(opening).contains(maxStudent)){ //don't know if this works
							noLongerViable.add(opening);
						}
					}
					
					for (Opening opening : noLongerViable){
						possible.remove(opening);
						openingMap.remove(opening);
					}
				}
				
			} while (!done);
		}
		
    	return true;
    }
    

    
    /* Precondition: course is in artNames */ 
    protected static ArrayList<String> getNeighbors(String course){
    	String[] courseParts = course.split(" ");
    	String detailedLevel = courseParts[courseParts.length - 1];
    	String courseName = "";
    	
    	for (int i = 0; i < courseParts.length-1; i++){
    		courseName += courseParts[i];
    		if (i != courseParts.length-2){
    			courseName += " ";
    		}
    	}
    	
    	ArrayList<String> neighbors = new ArrayList<String>();
    	for (String artName : artNames){
    		if (artName.contains(courseName)){
    			int Level = Integer.parseInt(detailedLevel.replaceAll("[a-zA-Z]", ""));
    			if (artName.contains(new Integer(Level).toString())
    			 || artName.contains(new Integer(Level - 1).toString())
    			 ||	artName.contains(new Integer(Level + 1).toString())){
    				neighbors.add(artName);
    			}
    		}
    	}
    	
    	return neighbors;
    }
    
    protected static boolean findOpenings(Course course, HashSet<Opening> openings){
        boolean[] openDays = {true, true, true, true, true};
        boolean found = true; // Whether a room has been found
        
        course.prioritizeRooms();
        ArrayList<String> roomsList = new ArrayList<String>(course.rooms);
        
        for (int i = 0; i < course.freq; i++){
            found = false;
            
            rooms_list: for (String room : roomsList){
                /* Stop checking rooms if you found one already. */
                if (found){
                    break rooms_list;
                } else {
                    openings_loop: for (Opening opening : openings){
                        /* Find an opening that is not on a day already used
                         * and that works for the room as well as the people. */
                        if (opening.time == Schedule.LUNCH && !allowLunch){
                            continue openings_loop;
                        } else if (!openDays[opening.day] && !repeatDays){
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
                            openDays[opening.day] = false;

                            found = true;
                            
                            ArrayList<Opening> viable = new ArrayList<Opening>();
                            for (Opening ope : openings){
                                if (!repeatDays){
                                    if (ope.day != opening.day){
                                        viable.add(ope);
                                    }
                                } else {
                                    if (ope.day != opening.day || ope.time != opening.time){
                                        viable.add(ope);
                                    }
                                }
                            }
                            
                            openings = new HashSet<Opening>(viable);
                            break openings_loop;
                        }
                    }
                }
            }

            if (!found){
                return false;
            }
        }
        
        return found;
    }
    
    protected static boolean findTimeFor(Course course){
        /* Check if there are any openings first. */
        HashSet<Opening> possible = checkPossible(course);
        if (possible.isEmpty()){
            return false;
        } else {
            /* Return true only if findOpenings was successful. */
            return findOpenings(course, possible);
        }
    }
    
    protected static HashSet<Opening> checkPossible(Course course){
        /* Find the intersection of the openings all people. */
        ArrayList<String> teacherOpenings = people.get(course.getTeacher()).sch.getOpenings();
        HashSet<String> possible = new HashSet<String>(teacherOpenings);

        /* retainAll() uses "==", so you need to store possible as a hashset of
         * strings, then go through openings and remove the overlap manually. */
        for (String student : course.getStudents()){
            possible.retainAll(people.get(student).sch.getOpenings());
        }
        
        HashSet<Opening> Out = new HashSet<Opening>();
        for (String opening : possible){
            String[] parts = opening.split(" ");
            Out.add(new Opening(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));       
        }
        
        return Out;
    }
    
    protected static void restore(){
    	for (Person person : people.values()){
            person.sch.restore();
            person.coursesLeftToSchedule = person.coursesLeftToScheduleBak;
        }
        
        for (Room room : rooms.values()){
            room.sch.restore();
        }
        
        for (Course course : courses.values()){
    		for (int i = 0; i < Schedule.D; i++){
    			course.roomsByDay[i] = course.roomsByDayBak[i];
    		}
            course.getPriority();
        }
    }
    
    protected static void backUp(){
    	for (Person person : people.values()){
            person.sch.backUp();
            person.coursesLeftToScheduleBak = person.coursesLeftToSchedule;
        }
        
        for (Room room : rooms.values()){
            room.sch.backUp();
        }
        
        for (Course course : courses.values()){
    		for (int i = 0; i < Schedule.D; i++){
    			course.roomsByDayBak[i] = course.roomsByDay[i];
    		}
            course.getPriority();
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
        courseNames = new ArrayList<String>(courseNamesBak);
        courseNames = qsort(courseNames);
        
        artNames = new ArrayList<String>(artNamesBak);
        artNames = qsort(artNames);
        
        
        /* Shh! */
        //calebAndMatthewClaws();
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
}

/*
    protected static boolean doArts(){
    	* Attempt to schedule the arts classes and shuffle students around to make it happen *
    	
    	/* 
    	 * -Try with current classes
    	 * -If that fails, try just scheduling the most restricted student in each course
    	 * and make sub-classes out of the old class based on shared openings with that
    	 * student IF the class if big enough (probably an 8-10 min)
    	 * -If that fails or the class is too small, start moving the most restricted
    	 * students up or down based on who they share the most classes with.
    	 * -If that fails, try the other direction
    	 * -If that fails, return false and start completely over
    	 *

    	backUp();
    	
    	boolean Scheduled = true;
    	
    	/* Try scheduling with current courses *
    	while (Scheduled && !artNames.isEmpty()){
			Course cur = courses.get(artNames.get(0));
	        if (!findTimeFor(cur)){
	            Scheduled = false;
	            //System.out.println("Failed to fully schedule " + artNames.get(0));
	            failPoints.put(artNames.get(0), failPoints.get(artNames.get(0)) + 1);
	        }
	    
	        artNames.remove(0);
	        for (Course course : courses.values()){
	    		course.getPriority();
	        }
    	}
    	
    	if (!Scheduled){
	    	/* Try scheduling just one student *
	    	restore();
	    	Scheduled = true;
	    	
	    	while (Scheduled && !artNames.isEmpty() && !floaters.isEmpty()){
	    		String mostRestrictedStudent = "ERR404";
	    		
	    		if (!artNames.isEmpty()){
			    	Course cur = courses.get(artNames.get(0));
			    	mostRestrictedStudent = cur.getMostRestrictedStudent();
	    		} else if (!floaters.isEmpty()) {
	    			mostRestrictedStudent = floaters.get(0); //can change to find restricted floater
	    		}
	    		
		    	if (mostRestrictedStudent.equals("ERR404")){
		    		System.out.println("Something went horribly wrong, please contact a professional");
		    		return false;
		    	}
		    	
		    	Scheduled = scheduleOneStudent(mostRestrictedStudent);
	    	}
	    	
    	}
    	
    
    	if (!Scheduled){
    		restore();
    		//change mostRestrictedStudent to be up / down
    		///one course based on who he shares the most classes
    		///with.
    		//do the same again
    	}
    	
    	
    	if (!Scheduled){
    		//try splitting bigger classes into sub-classes
    		///and schedule them as separate (e.g. music 3 
    		///-> music 3a, music 3b)
    		//do the same again
    	}
    	
		return Scheduled;
    }
    
    protected static boolean scheduleOneStudent(String student){
    	boolean openDays[] = {true, true, true, true, true};
	    boolean found = true; // Whether a room has been found
	    ArrayList<Opening> openingsUsed = new ArrayList<Opening>();
	    
		for (String courseName : artNames){
			Course course = courses.get(courseName);
			if (!course.getStudents().contains(student)){
				continue;
			}
	
		    course.prioritizeRooms();
		    ArrayList<String> roomsList = new ArrayList<String>(course.rooms);
		    
		    for (int i = 0; i < course.freq; i++){
		        found = false;
		        
		        rooms_list: for (String room : roomsList){
		            /* Stop checking rooms if you found one already. *
		            if (found){
		                break rooms_list;
		            } else {
		                openings_loop: for (Opening opening : people.get(student).sch.openings){
		                    /* Find an opening that is not on a day already used
		                     * and that works for the room as well as the people. *
		                    if (opening.time == Schedule.LUNCH && !allowLunch){
		                        continue openings_loop;
		                    } else if (!openDays[opening.day] && !repeatDays){
		                        continue openings_loop;
		                    } else if (!people.get(course.getTeacher()).sch.openings.contains(opening)){
		                    	continue openings_loop;
		                    } else if (!rooms.get(room).sch.restricted(opening)){
		                        /* Finalize the choice in everyone's schedule. *
		                      //Room
		                        course.roomsByDay[opening.day] = room;
		                        rooms.get(room).sch.scheduleCourse(opening, course);
		                      //Teacher
		                        people.get(course.getTeacher()).sch.scheduleCourse(opening, course);
		                        people.get(course.getTeacher()).coursesLeftToSchedule -= 1;
		                      //Students
	                            people.get(student).sch.scheduleCourse(opening, course);
	                            people.get(student).coursesLeftToSchedule -= 1;
                            	
		                        /* Mark that the day has been used. *
		                        openDays[opening.day] = false;
                        		openingsUsed.add(opening);
		                        
		                        found = true;
		                        break openings_loop;
		                    }
		                }
		            }
		        }
		    }
		    
		    
			//make a list of people in that course, 1 up, and 1 down
		    ArrayList<String> neighbors = getNeighbors(courseName);
		    
			//schedule everyone who works
		    ArrayList<String> doneFloaters = new ArrayList<String>();
		    for (String s : floaters){
		    	boolean available = true;
		    	Student studly = (Student)people.get(s);
		    	for (Opening opening : openingsUsed){
		    		if (studly.sch.restricted(opening)){
		    			available = false;
		    			break;
		    		}
		    	}
		    	
		    	if (available){
		    		course.addPerson(s);
		    		doneFloaters.add(s);
		    		for (Opening opening : openingsUsed){
		    			studly.sch.scheduleCourse(opening, course);
		    		}
	    		}
		    }
		    
		    for (String s : doneFloaters){
		    	floaters.remove(s);
		    }
		    
		    for (String n : neighbors){
			    for (String s : courses.get(n).getStudents()){
			    	boolean available = true;
			    	Student studly = (Student)people.get(s);
			    	for (Opening opening : openingsUsed){
			    		if (studly.sch.restricted(opening)){
			    			available = false;
			    			break;
			    		}
			    	}
			    	
			    	if (available){
			    		course.addPerson(s);
			    		courses.get(n).removePerson(s);
			    		for (Opening opening : openingsUsed){
			    			studly.sch.scheduleCourse(opening, course);
			    		}
			    	} else if (course.getStudents().contains(s)){
		    			floaters.add(s);
			    	}
			    }
		    }
		}
		
		return true;
    }
//*/