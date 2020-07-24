package gitlet;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/** Command log.
 * @author Keeyou Kim
 */
public class Log {
    /** Starting at the current head commit, display information
     * about each commit backwards along the commit tree until the
     * initial commit, following the first parent commit links,
     * ignoring any second parents found in merge commits.
     */
    public static void log() throws ParseException {
        File f = new File(".gitlet/head");
        String head = Utils.readContentsAsString(f);
        File currCommit = new File(".gitlet/commit/" + head);
        HashMap<String, String> currHeadMap = Utils.readObject(currCommit,
                                                            StringSet.class);
        String parent = currHeadMap.get("parent");
        System.out.println("===");
        System.out.println("commit " + head);
        String time = currHeadMap.get("date");
        Date date = new SimpleDateFormat(
                "HH:mm:ss z, EEE, d MMMM yyyy").parse(time);
        SimpleDateFormat formatter = new SimpleDateFormat(
                "EEE MMM d HH:mm:ss yyyy Z");
        time = formatter.format(date);
        System.out.println("Date: " + time);
        System.out.println(currHeadMap.get("message"));
        System.out.println();
        while (!parent.isEmpty()) {
            currCommit = new File(".gitlet/commit/" + parent);
            currHeadMap = Utils.readObject(currCommit, StringSet.class);
            parent = currHeadMap.get("parent");
            System.out.println("===");
            System.out.println("commit " + currHeadMap.get("uid"));
            time = currHeadMap.get("date");
            date = new SimpleDateFormat(
                    "HH:mm:ss z, EEE, d MMMM yyyy").parse(time);
            formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
            time = formatter.format(date);
            System.out.println("Date: " + time);
            System.out.println(currHeadMap.get("message"));
            System.out.println();
        }
    }
}
