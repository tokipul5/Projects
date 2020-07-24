package gitlet;


import java.io.File;
import java.util.Map;

/** Command checkout.
 * @author Keeyou Kim
 */
public class Checkout {

    /** Current working directory. */
    private static File cwd = new File(System.getProperty("user.dir"));

    /** Length of uid. */
    private static int lengthUID = Utils.sha1("a").length();

    /** Function takes FILENAME and check out that file. */
    public static void checkoutFile(String fileName) {
        File head = new File(".gitlet/head");
        String headCommit = Utils.readContentsAsString(head);
        File commit = new File(".gitlet/commit/" + headCommit);
        StringSet map = Utils.readObject(commit, StringSet.class);
        String fileUid = map.get(fileName);
        File file = new File(".gitlet/object/" + fileUid);
        if (!file.exists()) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        Blob b = Utils.readObject(file, Blob.class);
        File output = new File(fileName);
        Utils.writeContents(output, b.getContent());
    }

    /** Takes ID, DASH, FILENAME, and check out  that file. */
    public static void checkoutIdFileName(String id,
                                          String dash,
                                          String fileName) {
        if (!dash.equals("--")) {
            System.out.println("Incorrect operands.");
            return;
        }

        File commitFile = null;
        boolean exist = false;
        if (id.length() < lengthUID) {
            File commitDirectory = new File(".gitlet/commit");
            for (File f : commitDirectory.listFiles()) {
                if (f.getName().startsWith(id)) {
                    commitFile = new File(".gitlet/commit/" + f.getName());
                    exist = true;
                }
            }
            StringSet map = Utils.readObject(commitFile, StringSet.class);
            String fileUid = map.get(fileName);
            commitFile = new File(".gitlet/object/" + fileUid);
            if (!commitFile.exists()) {
                System.out.println("File does not exist in that commit.");
                return;
            }
        }

        File commit = new File(".gitlet/commit/" + id);
        if (!commit.exists() && !exist) {
            System.out.println("No commit with that id exists.");
            return;
        }

        if (!exist) {
            StringSet map = Utils.readObject(commit, StringSet.class);
            String fileUid = map.get(fileName);
            commitFile = new File(".gitlet/object/" + fileUid);
            if (!commitFile.exists()) {
                System.out.println("File does not exist in that commit.");
                return;
            }
        }
        Blob b = Utils.readObject(commitFile, Blob.class);
        File output = new File(fileName);
        Utils.writeContents(output, b.getContent());
    }

    /** Take BRANCHNAME and check out that branch. */
    public static void checkoutBranch(String branchName) {
        File head = new File(".gitlet/head");
        File branch = new File(".gitlet/heads/" + branchName);
        for (File f : cwd.listFiles()) {
            if (!f.isDirectory()) {
                String name = f.getName();
                File untrackedFile = new File(name);
                String content = Utils.readContentsAsString(untrackedFile);
                String uid = Utils.sha1(name + content);
                File blob = new File(".gitlet/blob/" + uid);
                if (!blob.exists()) {
                    System.out.println(
                            "There is an untracked file in the way; delete"
                                    + " it, or add and commit it first.");
                    return;
                }
            }
        }

        if (!branch.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        File currentBranch = new File(".gitlet/heads/current");
        String currentBranchName = Utils.readContentsAsString(currentBranch);
        if (branchName.equals(currentBranchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }


        String branchUid = Utils.readContentsAsString(branch);
        File commit = new File(".gitlet/commit/" + branchUid);
        StringSet map = Utils.readObject(commit, StringSet.class);
        String fileUid = "";

        for (Map.Entry e : map.entrySet()) {
            if (!e.getKey().equals("parent")
                    && !e.getKey().equals("uid")
                    && !e.getKey().equals("message")
                    && !e.getKey().equals("date")) {
                fileUid = (String) e.getValue();
                File objectFile = new File(".gitlet/object/" + fileUid);
                Blob b = Utils.readObject(objectFile, Blob.class);
                File outputFile = new File((String) e.getKey());
                Utils.writeContents(outputFile, b.getContent());
            }
        }

        for (File f : cwd.listFiles()) {
            if (f.isDirectory()) {
                continue;
            }
            if (!map.containsKey(f.getName())) {
                f.delete();
            }
        }
        Utils.writeContents(head, branchUid);
        Utils.writeContents(currentBranch, branchName);
    }
}
