package gitlet;

import java.io.File;
import java.text.ParseException;

/** Command Init.
 * @author Keeyou Kim
 */
public class Init {

    /** Current working directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** .gitlet directory. */
    static final File GITLET = new File(".gitlet");

    /** Creates a new Gitlet version-control system in the current directory.
     *  This system will automatically start with one commit: a commit that
     *  contains no files and has the commit message initial commit (just
     *  like that, with no punctuation). It will have a single branch:
     *  master, which initially points to this initial commit, and master
     *  will be the current branch. The timestamp for this initial commit
     *  will be 00:00:00 UTC, Thursday, 1 January 1970 in whatever format
     *  you choose for dates (this is called "The (Unix) Epoch", represented
     *  internally by the time 0.) Since the initial commit in all
     *  repositories created by Gitlet will have exactly the same content,
     *  it follows that all repositories will automatically share this
     *  commit (they will all have the same UID) and all commits in all
     *  repositories will trace back to it.
     */
    public static void init() throws ParseException {
        if (GITLET.exists()) {
            System.out.println("A Gitlet version-control system already "
                    + "exists in the current directory.");
            return;
        }
        GITLET.mkdir();

        File object = new File(".gitlet/object");
        object.mkdir();

        File stageAdd = new File(".gitlet/stageAdd");
        stageAdd.mkdir();

        File commit = new File(".gitlet/commit");
        commit.mkdir();
        CommitObj initial = new CommitObj("initial commit", "", null);


        File heads = new File(".gitlet/heads");
        heads.mkdir();
        Branch master = new Branch("master");
        File current = Utils.join(heads, "current");
        Utils.writeContents(current, "master");

        File blob = new File(".gitlet/blob");
        blob.mkdir();

        File rm = new File(".gitlet/removal");
        rm.mkdir();

        File merge = new File(".gitlet/merge");
        merge.mkdir();

        File untracked = new File(".gitlet/untracked");
        untracked.mkdir();

        File modification = new File(".gitlet/modifi");
        modification.mkdir();
    }
}
