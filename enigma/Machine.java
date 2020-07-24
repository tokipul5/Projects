package enigma;

import java.util.Collection;


/** Class that represents a complete enigma machine.
 *  @author Keeyou Kim
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors.toArray();
        _rotors = new Rotor[numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            for (int j = 0; j < _allRotors.length; j++) {
                String name = ((Rotor) _allRotors[j]).name();
                Permutation perm = ((Rotor) _allRotors[j]).permutation();
                String insertRotor = rotors[i].toUpperCase();
                if (insertRotor.equals(name.toUpperCase())) {
                    if (((Rotor) _allRotors[j]).reflecting()) {
                        _rotors[i] = new Reflector(name, perm);
                    } else if (!((Rotor) _allRotors[j]).rotates()) {
                        _rotors[i] = new FixedRotor(name, perm);
                    } else {
                        _rotors[i] = new MovingRotor(name, perm,
                                ((Rotor) _allRotors[j]).getNotch());
                    }
                }
            }
        }
        if (numRotors() != _rotors.length) {
            throw new EnigmaException("Not enough rotors to insert");
        } else if (!_rotors[0].reflecting()) {
            throw new EnigmaException("The first rotor must be a reflector");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != (numRotors() - 1)) {
            throw new EnigmaException("Wrong length for setting");
        }
        for (int i = 0; i < (numRotors() - 1); i++) {
            char settingAlpha = setting.charAt(i);
            if (!_alphabet.contains(settingAlpha)) {
                throw new EnigmaException(
                        "Does not contain setting alphabet in _alphabet");
            }
            _rotors[i + 1].set(settingAlpha);
        }
    }

    /** Set the ring setting of rotors according to SETTING. **/
    void setRing(String setting) {
        if (setting.length() != (numRotors() - 1)) {
            throw new EnigmaException("Wrong length for ring setting");
        }
        for (int i = 0; i < (numRotors() - 1); i++) {
            char settingRing = setting.charAt(i);
            if (!_alphabet.contains(settingRing)) {
                throw new EnigmaException(
                        "Does not contain setting alphabet in _alphabet");
            }
            _rotors[i + 1].setRing(settingRing);
            _rotors[i + 1].setNotch(settingRing);
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        int rightMost = numRotors() - 1;
        boolean doubleStepRotor4 = false;
        boolean doubleStepRotor3 = false;
        if (_rotors[rightMost].atNotch()) {
            doubleStepRotor4 = true;
        }
        if (_rotors[rightMost - 1].atNotch()) {
            doubleStepRotor3 = true;
        }
        if (doubleStepRotor3 && doubleStepRotor4) {
            boolean emptyStatement = true;
        } else if (doubleStepRotor4) {
            _rotors[rightMost - 1].advance();
        }
        if (doubleStepRotor3) {
            _rotors[rightMost - 2].advance();
            _rotors[rightMost - 1].advance();
        }
        _rotors[rightMost].advance();
        int result = _plugboard.permute(c);
        for (int i = rightMost; i >= 0; i--) {
            result = _rotors[i].convertForward(result);
        }
        for (int i = 1; i <= rightMost; i++) {
            result = _rotors[i].convertBackward(result);
        }
        result = _plugboard.permute(result);
        return result;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == ' ') {
                continue;
            }
            int toInt = _alphabet.toInt(msg.charAt(i));
            char convertedChar = _alphabet.toChar(convert(toInt));
            result += convertedChar;
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Stores a number of rotors. */
    private int _numRotors;

    /** Stores a number of moving rotors. */
    private int _pawls;

    /** An array stores all rotors. */
    private Object[] _allRotors;

    /** An Rotor array stores 5 using rotors. */
    private Rotor[] _rotors;

    /** A plugboard stores pairs to convert. */
    private Permutation _plugboard;
}
