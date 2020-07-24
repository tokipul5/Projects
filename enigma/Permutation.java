package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Keeyou Kim
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = new ArrayList<String>();
        String perm = "";
        for (int i = 0; i < cycles.length(); i++) {
            char temp = cycles.charAt(i);
            if (temp == '(' || temp == ' ') {
                continue;
            } else if (temp == ')') {
                _cycles.add(perm);
                perm = "";
            } else {
                perm += temp;
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles.add(cycle);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char curr = _alphabet.toChar(p);
        char pChar = ' ';
        for (int i = 0; i < _cycles.size(); i++) {
            for (int j = 0; j < _cycles.get(i).length(); j++) {
                if (_cycles.get(i).charAt(j) == curr) {
                    if ((j + 1) == _cycles.get(i).length()) {
                        pChar = _cycles.get(i).charAt(0);
                    } else {
                        pChar = _cycles.get(i).charAt(j + 1);
                    }
                    return wrap(_alphabet.toInt(pChar));
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char curr = _alphabet.toChar(c);
        char cChar = ' ';
        for (int i = 0; i < _cycles.size(); i++) {
            for (int j = 0; j < _cycles.get(i).length(); j++) {
                if (_cycles.get(i).charAt(j) == curr) {
                    if ((j - 1) < 0) {
                        cChar = _cycles.get(i).charAt
                                (_cycles.get(i).length() - 1);
                    } else {
                        cChar = _cycles.get(i).charAt(j - 1);
                    }
                    return wrap(_alphabet.toInt(cChar));
                }
            }
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int pos = _alphabet.toInt(p);
        return _alphabet.toChar(permute(pos));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int pos = _alphabet.toInt(c);
        return _alphabet.toChar(invert(pos));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int countAlpha = 0;
        for (int i = 0; i < _cycles.size(); i++) {
            if (_cycles.get(i).length() == 1) {
                return false;
            }
            countAlpha += _cycles.get(i).length();
        }
        if (countAlpha == size()) {
            return true;
        }
        return false;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** ArrayList stores pairs of permutation. */
    private ArrayList<String> _cycles;
}
