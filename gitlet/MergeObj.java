package gitlet;

/** Merge object.
 * @author Keeyou Kim
 */
public class MergeObj {
    /** Store uid. */
    private String _uid;

    /** Store parent. */
    private String _parent;

    /** Store distance. */
    private int _distance;

    /** A constructor for MergeObj takes UID, P, D. */
    public MergeObj(String uid, String p, int d) {
        _uid = uid;
        _parent = p;
        _distance = d;
    }

    /** Return distance. */
    public int getDistance() {
        return _distance;
    }

    /** Return uid. */
    public String getUid() {
        return _uid;
    }

    /** Return parent. */
    public String getParent() {
        return _parent;
    }
}
