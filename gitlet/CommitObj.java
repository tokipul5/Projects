package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/** Commit object for commit.
 * @author Keeyou Kim
 */
public class CommitObj implements Serializable {
    /** Folder that store commitObj (.gitlet). **/
    static final File COMMIT = new File(".gitlet/commit");


    /** Store the message of commit. **/
    private String _message;

    /** Store parent of commitObj. **/
    private String _parent;

    /** Store the time commit created. **/
    private String _time;

    /** Store the blob which is the content. */
    private StringSet _blob;

    /** Store UID of commit. */
    private String _uid;

    /** A constructor takes M as a message, B as blob, and P as a parent. */
    public CommitObj(String m, String p, StringSet b) throws ParseException {
        _message = m;
        _parent = p;
        _blob = b;
        if (p.isEmpty()) {
            String defaultTime = "00:00:00 UTC, Thursday, 1 January 1970";
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "HH:mm:ss z, EEEE, d MMMM yyyy");
            Date date = formatter.parse(defaultTime);
            _time = formatter.format(date);
        } else {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "HH:mm:ss z, EEEE, d MMMM yyyy");
            _time = formatter.format(date);
        }
        StringSet input = new StringSet();
        input.put("date", _time);
        input.put("message", _message);
        input.put("parent", _parent);
        if (_blob != null) {
            for (Map.Entry element : _blob.entrySet()) {
                input.put((String) element.getKey(),
                        (String) element.getValue());
            }
        }
        _uid = Utils.sha1(_message + _parent + _blob + _time);
        input.put("uid", _uid);

        File initCommit = Utils.join(COMMIT, _uid);
        Utils.writeObject(initCommit, input);

        File head = Utils.join(".gitlet", "head");
        Utils.writeContents(head, _uid);

        if (!p.isEmpty()) {
            File currBranch = new File(".gitlet/heads/current");
            String branch = Utils.readContentsAsString(currBranch);
            File rightBranch = new File(".gitlet/heads/" + branch);
            Utils.writeContents(rightBranch, _uid);
        }
    }

    /** Return message. */
    public String getMessage() {
        return _message;
    }

    /** Return parent. */
    public String getParent() {
        return _parent;
    }

    /** Return time. */
    public String getTime() {
        return _time;
    }

    /** Return StringSet blob. */
    public StringSet getBlob() {
        return _blob;
    }

    /** Return uid. */
    public String getUid() {
        return _uid;
    }
}
