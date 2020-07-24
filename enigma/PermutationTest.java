package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Keeyou Kim
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testCheckPerm() {
        Alphabet a = new Alphabet("ABCD");
        perm = new Permutation("(DBAC)", a);
        checkPerm("Test1", "ABCD", "CADB");
    }

    @Test
    public void testAlphabet() {
        Alphabet a = new Alphabet("ABCD");
        Permutation p = new Permutation("(BACD)", a);
        assertEquals(p.alphabet(), a);
    }

    @Test
    public void testDerangement() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertTrue(p.derangement());
        Permutation p1 = new Permutation("(BAC) (D)", new Alphabet("ABCD"));
        assertFalse(p1.derangement());
    }

    @Test
    public void testInvertInt() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals(0, p.invert(2));
        assertEquals(2, p.invert(3));
        assertEquals(1, p.invert(0));
        Permutation p1 = new Permutation("(AB) (C) (D)", new Alphabet("ABCD"));
        assertEquals(1, p.invert(0));
        assertEquals(0, p.invert(2));
    }

    @Test
    public void testPermuteInt() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals(3, p.permute(2));
        assertEquals(2, p.permute(0));
        Permutation p1 = new Permutation("(B) (ADC)", new Alphabet("ABCD"));
        assertEquals(1, p1.permute(1));
        assertEquals(3, p1.permute(0));
        assertEquals(0, p1.permute(2));
        assertEquals(2, p1.permute(3));
    }

    @Test
    public void testSize() {
        Permutation p = new Permutation("(AB) (CD)", new Alphabet("CDBA"));
        assertEquals(4, p.size());
    }

    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('B', p.invert('A'));
        assertEquals('D', p.invert('B'));
        assertEquals('C', p.invert('D'));
        assertEquals('A', p.invert('C'));
        Permutation p1 = new Permutation("(BA) (C) (D)", new Alphabet("ABCD"));
        assertEquals('B', p1.invert('A'));
        assertEquals('C', p1.invert('C'));
        assertEquals('D', p1.invert('D'));
    }

    @Test
    public void testPermuteChar() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('A', p.permute('B'));
        assertEquals('C', p.permute('A'));
        assertEquals('D', p.permute('C'));
        Permutation p1 = new Permutation("(ERW) (Q)", new Alphabet("QWER"));
        assertEquals('Q', p1.permute('Q'));
        assertEquals('E', p1.permute('W'));
        Permutation p2 = new Permutation("(E)", new Alphabet("EFGH"));
        assertEquals('F', p2.permute('F'));
    }

    @Test
    public void testContains() {
        Alphabet a = new Alphabet("FGHJ");
        assertTrue(a.contains('F'));
        assertFalse(a.contains('A'));
        assertTrue(a.contains('J'));
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        p.permute('F');
        p.invert('G');
    }
}
