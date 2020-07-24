package gitlet;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Command globallog.
 * @author Keeyou Kim
 */
public class GlobalLog {

    /** Print out globallog. */
    public static void globalLog() throws ParseException {
        File commit = new File(".gitlet/commit");
        File[] list = commit.listFiles();
        for (File f : list) {
            StringSet temp = Utils.readObject(f, StringSet.class);
            String parent = temp.get("parent");
            System.out.println("===");
            System.out.println("commit " + f.getName());
            String time = temp.get("date");
            Date date = new SimpleDateFormat(
                    "HH:mm:ss z, EEE, d MMMM yyyy").parse(time);
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "EEE MMM d HH:mm:ss yyyy Z");
            time = formatter.format(date);
            System.out.println("Date: " + time);
            System.out.println(temp.get("message"));
            System.out.println();
        }
    }
}
