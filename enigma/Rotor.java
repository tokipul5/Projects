package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Keeyou Kim
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
    }

    /** Set the notch again because of the ring SETTING. **/
    void setNotch(char setting) {
    }

    /** Set the ring setting I. **/
    void setRing(char i) {
        _ring = alphabet().toInt(i);
        int changed = _setting - _ring;
        set(changed);
    }

    /** Return _ring the ring setting. **/
    public int getRing() {
        return _ring;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Return current notch. */
    String getNotch() {
        return "";
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = wrap(posn);
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _setting = alphabet().toInt(cposn);
    }

    /** Return NUM that is mod by size. */
    int wrap(int num) {
        int result = num % size();
        if (result < 0) {
            result += size();
        }
        return result;
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int convert = _permutation.permute(wrap(p + _setting));
        return wrap(convert - _setting);
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int convert = _permutation.invert(wrap(e + _setting));
        return wrap(convert - _setting);
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** Stores a setting of Rotor. */
    private int _setting;

    /** Store a ring setting of a Rotor. **/
    private int _ring;
}
