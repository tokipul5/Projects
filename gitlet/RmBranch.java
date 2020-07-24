package gitlet;

import java.io.File;

/** Command remove branch.
 * @author Keeyou Kim
 */
public class RmBranch {
    /** Takes BRANCHNAME and remove that branch. */
    public static void rmBranch(String branchName) {
        File currentBranch = new File(".gitlet/heads/current");
        String currentBranchName = Utils.readContentsAsString(currentBranch);
        if (branchName.equals(currentBranchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }

        File branch = new File(".gitlet/heads/" + branchName);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        branch.delete();
    }
}
