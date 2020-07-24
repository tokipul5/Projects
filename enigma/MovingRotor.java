package enigma;


/** Class that represents a rotating rotor in the enigma machine.
 *  @author Keeyou Kim
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    String getNotch() {
        return _notches;
    }

    @Override
    void setNotch(char setting) {
        int changed = alphabet().toInt(_notches.charAt(0))
                - alphabet().toInt(setting);
        int wrapChanged = wrap(changed);
        _notches = Character.toString(alphabet().toChar(wrapChanged));
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            if (alphabet().toInt(_notches.charAt(i)) == this.setting()) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        set(this.setting() + 1);
    }

    /** Stores value of notch. */
    private String _notches;
}
