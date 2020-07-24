package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Keeyou Kim
 */
class Alphabet {
    /** Store alphabet. **/
    private String _alphabet;

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _alphabet = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabet.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        boolean result = false;
        for (int i = 0; i < _alphabet.length(); i++) {
            if (ch == _alphabet.charAt(i)) {
                result = true;
            }
        }
        return result;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index < 0 || index >= size()) {
            throw new EnigmaException("Cannot find index in Alphabet");
        }
        return (char) _alphabet.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        boolean found = false;
        for (int i = 0; i < size(); i++) {
            if (_alphabet.charAt(i) == ch) {
                found = true;
            }
        }
        if (!found) {
            throw new EnigmaException("Character is not found in _alphabet");
        }
        return _alphabet.indexOf(ch);
    }

}
