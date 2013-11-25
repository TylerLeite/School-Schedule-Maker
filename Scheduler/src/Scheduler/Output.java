package Scheduler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import Schedule.Schedule;

public class Output {
    protected static void printToHtm(String content, String filename){
        try {
            /* Open the file "name.htm" for writing */
            File file = new File(filename); 
            
            /* Create the file if it does not exist */
            if (!file.exists()){
                file.createNewFile();
            }
        
            FileWriter     fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            
            /* Write the text. It should already be formatted by 
             * Output.format(schedule, name) */
            bw.write(content);
            bw.close();
 
        /* Catch IO errors and print the stack to console for review */
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    protected static String format(String[][] schedule, String name){
        /* Style tag to make the table look pretty */
        String out = 
               "<style>\n"
             + "    p{\n"
             + "        font: 22px helvetica, sans-serif;\n"
             + "        font-weight: bold;\n"
             + "        margin: 0;\n"
             + "    }\n"
             + "    table, th, td{\n"
             + "        margin: 0;\n"
             + "        border: 1px solid black;\n"
             + "    }\n"
             + "    td{\n"
             + "        width: 100px;\n"
             + "    }\n"
             + "    .time{\n"
             + "        width: 50px;\n"
             + "    }\n"
             + "</style>\n\n";
        
        /* Student/Teacher/Room name at the top. Also is the filename */
        out += "<p>" + name + "</p>\n";
        
        /* Table's first row. All bold because it looks good */
        out += "<table>\n"
             + "    <tr>\n"
             + "        <th class='time'></th>\n"
             + "        <th>Monday</th>\n"
             + "        <th>Tuesday</th>\n"
             + "        <th>Wednesday</th>\n"
             + "        <th>Thursday</th>\n"
             + "        <th>Friday</th>\n"
             + "    </tr>\n";
        
        /* Maybe one day we will have weekend school. You never know */
        final int TIMES = Schedule.T + 1, DAYS = 5;
        final int SNACK = 3;
        
        /* Get the time from this array to make the loops shorter */
        String[] times = {"8:30",  "9:15",  "10:00", "10:45", 
                          "10:55", "11:40", "12:25", 
                          "1:10",  "1:55",  "2:40"};
        
        /* Loop through times and days */
        for (int i = 0, Q = 0; Q < TIMES; i++, Q++){
            /* The time column is done first since it's not dependent
             * on the schedule, but rather the position in the loops */
            out +=
               "    <tr>\n"
             + "        <td class='time'>" + times[Q] + "</td>\n";
            for (int j = 0; j < DAYS; j++){
                /* Check if it's snack time */
                if (Q == SNACK){
                    out += "        <td>Snack</td>\n";
                } else if (schedule[j][i].equals("DNF")){
                    out += "        <td> </td>\n";
                /* Else print the space in the schedule that corresponds
                 * to the current position in the loops */
                } else {
                    out += "        <td>" + schedule[j][i] + "</td>\n";
                }
            }
            out += "    </tr>\n";
            
            /* This is a very ugly solution to the problem of having snack 
             * on the printed schedule but not in the array used in the
             * scheduling algorithm. I keep a value Q separate from i to
             * track how many times total the loop has run and use i to
             * access the correct cell in schedule[][]. Q is used to 
             * check whether a snack row should be added. */
            if (Q == SNACK){
                i--;
            }
        }
        
        /* Party! You're done!*/
        out += "</table>";
        
        return out;
    }
    
    public static void writeSch(String[][] schedule, String name){
        /* Format the schedule before printing*/
        String[] fname = name.split("/");
        String formatted = format(schedule, fname[fname.length-1]);
        /* Print the schedule to 'name.htm' */
        printToHtm(formatted, name + ".htm");
    }
}
