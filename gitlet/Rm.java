package gitlet;

import java.io.File;
import java.util.HashMap;

/** Command rm.
 * @author Keeyou Kim
 */
public class Rm {

    /** current working directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit, stage it
     * for removal and remove the file from the working directory
     * if the user has not already done so (do not remove it
     * unless it is tracked in the current commit).
     * Takes FILENAME
     */
    public static void rm(String fileName) {
        boolean deleted = false;

        File stageAdd = new File(".gitlet/stageAdd");
        File removal = new File(".gitlet/removal");
        File fileRm = new File(fileName);
        for (File file : stageAdd.listFiles()) {
            if (file.getName().equals(fileName)) {
                file.delete();
                deleted = true;
            }
        }

        File p = new File(".gitlet/head");
        String parent = Utils.readContentsAsString(p);
        HashMap<String, String> mapCurrent = new HashMap<>();
        File commit = new File(".gitlet/commit");
        for (File o : commit.listFiles()) {
            if (o.getName().equals(parent)) {
                mapCurrent = Utils.readObject(o, StringSet.class);
                break;
            }
        }

        if (mapCurrent.containsKey(fileName)) {
            File fileRemoval = Utils.join(removal, fileName);
            Utils.writeContents(fileRemoval, mapCurrent.get(fileName));
            mapCurrent.remove(fileName);
            fileRm.delete();
            deleted = true;
        }

        if (!deleted) {
            System.out.println("No reason to remove the file.");
        }
    }
}
