package Scheduler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ArrayList;

import Course.Course;
import Course.Room;
import People.Student;
import People.Teacher;
import Schedule.Schedule;


public class Input {
    public static final String path = "dat"; // Path to class data files
    public static final String crsFEnd = ".dat"; // File ending for class data files
    public static final String tchFEnd = ".tch"; // File ending for teacher data files
    
    protected static String readFile(String path, Charset encoding){
        /* Read a file and return a String with its contents. */
        byte[] encoded;
        
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException e){
            System.out.println("Error reading file: " + path);
            return "error;-1;error;error;error"; // Formatted to avoid index out of range errors
        }
        
        /* Convert raw data to a String. */
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }
    
    public static void parseFile(String fileName){
        /* Determine how to parse a file... this is not used. */
        if (fileName.contains(crsFEnd)){
            parseCrsFile(fileName);
        } else if (fileName.contains(tchFEnd)){
            parseTchFile(fileName);
        } else {
            System.out.println("Unsupported file type");
        }
    }
    
    public static void parseCrsFile(String fileName){
        /* Read a course file to see who is in what class and what rooms can be used, 
         * then create people as dictated. This way, you only need to input class data,
         * not individual students and teachers. It also constructs a list of rooms 
         * dynamically so that if we start to use the basement or something, all we need
         * to change are the html / php files (no need to recompile or anything). */
        String[] rawData  = readFile(fileName, Charset.defaultCharset()).split(";"); // Course name
        
        /* Courses are stored in the form:
         * NAME;FREQ;TEACHER;STUDENT~STUDENT~...~STUDENT;1a~1b~...MR */
        
        /* Frequency, i.e. times per week the class occurs or how many times the for
         * loop is run when scheduling a course. */
        String courseName = rawData[0];
        int freq = Integer.parseInt(rawData[1]);
        Course course = new Course(courseName, freq);
        Scheduler.courses.put(courseName, course);
        Scheduler.failPoints.put(courseName,  0);
        
        /* Make a new teacher / room / student if and only if the program hasn't seen the 
         * name before. Either way, mark the addition in the course object. */
        
        /* Construct room list dynamically.*/
        ArrayList<String> RN = new ArrayList<String>(Arrays.asList(rawData[4].split("~")));
        RN.removeAll(Arrays.asList("", null));
        HashSet<String> roomNames = new HashSet<String>(RN);
        for (String room : roomNames){
            if (!Scheduler.rooms.containsKey(room))
                Scheduler.rooms.put(room, new Room(room));
            course.addRoom(room);
        }
        
        /* Teachers are people too. */
        String teacherName = rawData[2];
        if (!Scheduler.people.containsKey(teacherName))
            Scheduler.people.put(teacherName,  new Teacher(teacherName));
        course.addPerson(teacherName);
        
        /* Students aren't, but we let them think they are. */
        ArrayList<String> SN = new ArrayList<String>(Arrays.asList(rawData[3].split("~")));
        SN.removeAll(Arrays.asList("", null));
        HashSet<String> studentNames = new HashSet<String>(SN);
        for (String student : studentNames){
            if (!Scheduler.people.containsKey(student))
                Scheduler.people.put(student, new Student(student));
            course.addPerson(student);
            Scheduler.people.get(student).addCoursesToSchedule(course.freq);
        }
    }
    
    static public void parseTchFile(String fileName) {
        /* Read a teacher file and remove teacher openings accordingly. This is used to
         * let the scheduler know when a teacher can't be in at a certain time. */
        String[] rawData  = readFile(fileName, Charset.defaultCharset()).split(";");
        
        /* Openings are stored in the form:
         * NAME;DAY,TIME~DAY,TIME~DAY,TIME~...~DAY,TIME */
        
        String name = rawData[0];
        String[] openingData = rawData[1].split("~"); // List of openings in the form "DAY,TIME"
        
        for (int i = 0; i < openingData.length; i++){
            /* Don't try to remove empty openings, you will get errors. */
            if (openingData[i].isEmpty())
                continue;

            String[] dayTime = openingData[i].split(",");
            int day  = Integer.parseInt(dayTime[0]);
            int time = Integer.parseInt(dayTime[1]);
            
            if (time >= Schedule.LUNCH)
                time++;
            
            Scheduler.people.get(name).sch.restrict(day, time);
            Scheduler.people.get(name).sch.schedule[day][time] = "DNF";
        }
    }
}
