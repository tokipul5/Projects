package gitlet;

import java.io.File;
import java.util.Map;

/** Command commit.
 * @author Keeyou Kim
 */
public class Commit {

    /** Saves a snapshot of certain files in the current commit
     * and staging area so they can be restored at a later time,
     * creating a new commit. The commit is said to be tracking
     * the saved files. By default, each commit's snapshot of
     * files will be exactly the same as its parent commit's
     * snapshot of files; it will keep versions of files exactly
     * as they are, and not update them. A commit will only update
     * the contents of files it is tracking that have been staged
     * for addition at the time of commit, in which case the
     * commit will now include the version of the file that was
     * staged instead of the version it got from its parent. A
     * commit will save and start tracking any files that were staged
     * for addition but weren't tracked by its parent. Finally, files
     * tracked in the current commit may be untracked in the new commit
     * as a result being staged for removal by the rm command (below).
     *
     * @param: MESSAGE
     */
    public static void commit(String message) throws Exception {
        File stageAdd = new File(".gitlet/stageAdd");
        File stageRm = new File(".gitlet/removal");
        if (stageAdd.list().length == 0 && stageRm.listFiles().length == 0) {
            System.out.println("No changes added to the commit.");
            return;
        } else if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        File p = new File(".gitlet/head");
        String currUid = Utils.readContentsAsString(p);

        StringSet blobMap = new StringSet();
        File commit = new File(".gitlet/commit/" + currUid);
        StringSet map = Utils.readObject(commit, StringSet.class);
        for (Map.Entry e : map.entrySet()) {
            if (!e.getKey().equals("parent")
                    && !e.getKey().equals("uid")
                    && !e.getKey().equals("message")
                    && !e.getKey().equals("date")) {
                blobMap.put((String) e.getKey(), (String) e.getValue());
            }
        }
        for (Map.Entry e : map.entrySet()) {
            if (!e.getKey().equals("parent")
                    && !e.getKey().equals("uid")
                    && !e.getKey().equals("message")
                    && !e.getKey().equals("date")) {
                for (File temp : stageRm.listFiles()) {
                    if (e.getKey().equals(temp.getName())) {
                        blobMap.remove(e.getKey(), e.getValue());
                        temp.delete();
                    }
                }
            }
        }
        for (File blob : stageAdd.listFiles()) {
            String uid = Utils.readContentsAsString(blob);
            String fileName = blob.getName();
            if (blobMap == null) {
                blobMap = new StringSet();
                blobMap.put(fileName, uid);
            } else {
                if (blobMap.containsKey(fileName)) {
                    if (blobMap.get(fileName) != uid) {
                        blobMap.replace(fileName, uid);
                    }
                } else {
                    blobMap.put(fileName, uid);
                }
            }
            blob.delete();
        }
        clearAll();
        CommitObj com = new CommitObj(message, currUid, blobMap);
    }

    /** Delete all files in removal, stageAdd, and modifi. */
    public static void clearAll() {
        File removalFile = new File(".gitlet/removal");
        for (File f : removalFile.listFiles()) {
            f.delete();
        }

        File untracked = new File(".gitlet/untracked");
        for (File f : untracked.listFiles()) {
            f.delete();
        }

        File modifi = new File(".gitlet/modifi");
        for (File f : modifi.listFiles()) {
            f.delete();
        }
    }

}
