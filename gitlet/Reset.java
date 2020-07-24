package gitlet;

import java.io.File;
import java.util.Map;

/** Command reset.
 * @author Keeyou Kim
 */
public class Reset {
    /** Current working directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Takes UID and reset .gitlet. */
    public static void reset(String uid) {
        File commit = new File(".gitlet/commit/" + uid);
        if (!commit.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        for (File f : CWD.listFiles()) {
            if (!f.isDirectory()) {
                String name = f.getName();
                File untrackedFile = new File(name);
                String content = Utils.readContentsAsString(untrackedFile);
                String uidUntrackedFile = Utils.sha1(name + content);
                File blob = new File(".gitlet/blob/" + uidUntrackedFile);
                if (!blob.exists()) {
                    System.out.println(
                            "There is an untracked file in the way; delete"
                                    + " it, or add and commit it first.");
                    return;
                }
            }
        }
        StringSet contentCommit = Utils.readObject(commit, StringSet.class);
        StringSet filesInCommit = new StringSet();

        for (Map.Entry e : contentCommit.entrySet()) {
            if (!e.getKey().equals("parent")
                    && !e.getKey().equals("uid")
                    && !e.getKey().equals("message")
                    && !e.getKey().equals("date")) {
                filesInCommit.put((String) e.getKey(), (String) e.getValue());
            }
        }

        for (Map.Entry e : filesInCommit.entrySet()) {
            String fileName = (String) e.getKey();
            String uidFile = (String) e.getValue();
            File checkOutFile = new File(".gitlet/blob/" + uidFile);
            String content = Utils.readContentsAsString(checkOutFile);
            File output = new File(fileName);
            Utils.writeContents(output, content);
        }
        for (File f : CWD.listFiles()) {
            if (!filesInCommit.containsKey(f.getName())) {
                f.delete();
            }
        }
        File current = new File(".gitlet/heads/current");
        String currentBranchName = Utils.readContentsAsString(current);
        File branch = new File(".gitlet/heads/" + currentBranchName);
        Utils.writeContents(branch, uid);

        File stageAdd = new File(".gitlet/stageAdd");
        for (File f : stageAdd.listFiles()) {
            f.delete();
        }
        File head = new File(".gitlet/head");
        Utils.writeContents(head, uid);
    }
}
