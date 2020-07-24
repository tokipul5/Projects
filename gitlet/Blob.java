package gitlet;

import java.io.File;
import java.io.Serializable;

/** Create files in Blob.
 *  @author Keeyou Kim
 */
public class Blob implements Serializable {
    /** Store content of blob. */
    private String _content;

    /** Store uid of blob. */
    private String _uid;

    /** Store difference of blob. */
    private boolean _differ;

    /** Store file name of blob. */
    private String _fileName;

    /** Folder that store commitObj (.gitlet). */
    static final File BLOB = new File(".gitlet/blob");

    /** Folder for object. */
    static final File OBJECT = new File(".gitlet/object");

    /** A constructor take F as file name and C as content to the blob. */
    public Blob(String f, String c) {
        _fileName = f;
        _content = c;
        _uid = Utils.sha1(f + c);
        _differ = true;

        File head = new File(".gitlet/head");
        String currentCommitUid = Utils.readContentsAsString(head);
        File currentCommit = new File(".gitlet/commit/" + currentCommitUid);
        StringSet mapCurrCommit = Utils.readObject(currentCommit,
                                                    StringSet.class);

        File b = Utils.join(BLOB, _uid);
        if (mapCurrCommit.containsKey(_fileName)
                && mapCurrCommit.get(_fileName).equals(_uid)) {
            _differ = false;
        } else {
            Utils.writeContents(b, _content);
            File blobObj = Utils.join(OBJECT, _uid);
            Utils.writeObject(blobObj, this);
        }
    }

    /** Return content of blob. */
    public String getContent() {
        return _content;
    }

    /** Return uid of blob. */
    public String getUid() {
        return _uid;
    }

    /** Return difference of blob. */
    public boolean getDiffer() {
        return _differ;
    }

    /** Return file name of blob. */
    public String getFileName() {
        return _fileName;
    }
}
