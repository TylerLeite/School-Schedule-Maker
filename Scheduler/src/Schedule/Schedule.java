package Schedule;

import java.util.HashSet;
import java.util.ArrayList;


import Course.Course;


public class Schedule {
	public static final int T = 8; // 8 periods per day (excluding Lunch and Snack)
	public static final int D = 5; // Approximately 5 school days per week
	
	public HashSet<Opening> openings = new HashSet<Opening>();
	public String[][] schedule = new String[D][T];
	
	public Schedule(){
		/* Initialize everything to "" so you don't have to deal with null. */
		for (int i = 0; i < D; i++){
			for (int j = 0; j < T; j++){
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
			}
		}
	}
	
	public ArrayList<String> getOpenings(){
		ArrayList<String> open = new ArrayList<String>();
		for (Opening opening : openings)
			open.add(opening.toString());
		
		return open;
	}
	
	public boolean schedule(Opening opening, Course course){
		/* Remove the opening in both the openings HashSet and schedule array. */
		if (schedule[opening.day][opening.time].isEmpty()){
			schedule[opening.day][opening.time] = course.getString(opening.day);
			restrict(opening.day, opening.time);
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean schedule(int day, int time, Course course){
		/* Two separate schedule functions to minimize object creation (compared
		 * to calling one from the other). */
		if (schedule[day][time].isEmpty()){
			schedule[day][time] = course.getString(day);
			restrict(day, time);
			
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean restricted(Opening check){
		/* Check if an opening is available. */
		for (Opening opening : openings){
			if (opening.day == check.day && opening.time == check.time)
				return false;
		}
		
		return true;
	}
	
	public boolean restricted(int day, int time){
		/* Check if an opening is available. */
		for (Opening opening : openings){
			if (opening.day == day && opening.time == time)
				return false;
		}
		
		return true;
	}
	
	public void restrict(int day, int time){
		for (Opening opening : openings){
		    if (opening.day == day && opening.time == time){
		        openings.remove(opening);
		        break;
		    }
		}
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