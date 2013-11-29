package Schedule;

public class Opening {
    public int day; 
    public int time;
    
    public Opening(int day, int time){
        this.day  = day; //0 = Mon, 4 = Fri
        this.time = time; //0 = 8:30(-9:15), 8 = 2:40(-3:25)
    }
    
    public String toString(){
        /* Outputs the opening in an easy-to-read form. */
        //String[] Days  = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        //String[] Times = {"8:30", "9:15", "10:00", "10:55", "11:40", "1:10", "1:55", "2:40"};
        
        //return Days[day] + " at " + Times[time];
        return day + " " + time;
    
    }
}