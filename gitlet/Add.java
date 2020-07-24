package gitlet;

import java.io.File;

/** Command Add.
 *  @author Keeyou Kim
 *  */

public class Add {

    /** Folder that store commitObj (.gitlet). */
    static final File STAGEADD = new File(".gitlet/stageAdd");

    /** Adds a copy of the file as it currently exists to the staging
     * area (see the description of the commit command). For this reason,
     * adding a file is also called staging the file for addition. Staging
     * an already-staged file overwrites the previous entry in the staging
     * area with the new contents. The staging area should be somewhere
     * in .gitlet. If the current working version of the file is identical
     * to the version in the current commit, do not stage it to be added,
     * and remove it from the staging area if it is already there (as can
     * happen when a file is changed, added, and then changed back). The
     * file will no longer be staged for removal (see gitlet rm), if it
     * was at the time of the command. Takes FILENAME as a parameter.
     */
    public static void add(String fileName) {
        File f = new File(fileName);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        File stageRm = new File(".gitlet/removal");
        if (stageRm.listFiles().length != 0) {
            for (File temp : stageRm.listFiles()) {
                if (temp.getName().equals(fileName)) {
                    temp.delete();
                }
            }
        }

        String contentOfFileName = Utils.readContentsAsString(f);
        Blob b = new Blob(fileName, contentOfFileName);

        if (b.getDiffer()) {
            File add = Utils.join(STAGEADD, fileName);
            Utils.writeContents(add, b.getUid());
        }
    }
}
