package gitlet;

import java.io.File;

/** Command branch.
 * @author Keeyou Kim
 */
public class Branch {

    /** Store file name. */
    private String _fileName;

    /** A contructor takes BRANCHNAME. */
    public Branch(String branchName) {
        _fileName = branchName;
        File branch = Utils.join(".gitlet/heads", _fileName);
        if (branch.exists()) {
            System.out.println("A branch with that name already exists.");
        }
        File head = new File(".gitlet/head");
        Utils.writeContents(branch, Utils.readContentsAsString(head));
    }

    /** Change the current branch into BRANCHNAME. */
    public static void change(String branchName) {
        File current = new File(".gitlet/heads/current");
        Utils.writeContents(current, branchName);
    }
}
