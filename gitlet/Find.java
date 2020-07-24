package gitlet;

import java.io.File;
import java.util.HashMap;

/** Command find.
 * @author Keeyou Kim
 */
public class Find {
    /** Prints out the ids of all commits that have the given
     *  commit message, one per line. If there are multiple such
     *  commits, it prints the ids out on separate lines. The
     *  commit message is a single operand; to indicate a multiword
     *  message, put the operand in quotation marks, as for the
     *  commit command below.
     *
     *  Takes MESSAGE.
     */
    public static void find(String message) {
        boolean found = false;
        File commit = new File(".gitlet/commit");
        for (File f : commit.listFiles()) {
            HashMap<String, String> map = Utils.readObject(f, StringSet.class);
            if (map.get("message").equals(message)) {
                System.out.println(f.getName());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }
}
