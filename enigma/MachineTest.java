package enigma;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;
import static enigma.TestUtils.*;


/**
 *  @author Keeyou Kim
 */

public class MachineTest {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    private Alphabet alpha = new Alphabet();
    private Rotor rotor1;
    private void setRotor1(String name, HashMap<String, String> rotors,
                           String notches) {
        rotor1 = new MovingRotor(name, new Permutation(rotors.get(name), UPPER),
                notches);
    }
    private Rotor rotor2;
    private void setRotor2(String name, HashMap<String, String> rotors,
                           String notches) {
        rotor2 = new MovingRotor(name, new Permutation(rotors.get(name), UPPER),
                notches);
    }
    private Rotor rotor3;
    private void setRotor3(String name, HashMap<String, String> rotors,
                           String notches) {
        rotor3 = new MovingRotor(name, new Permutation(rotors.get(name), UPPER),
                notches);
    }
    private Rotor rotor4;
    private void setRotor4(String name, HashMap<String, String> rotors,
                           String notches) {
        rotor4 = new MovingRotor(name, new Permutation(rotors.get(name), UPPER),
                notches);
    }
    private Rotor rotor5;
    private void setRotor5(String name, HashMap<String, String> rotors,
                           String notches) {
        rotor5 = new MovingRotor(name, new Permutation(rotors.get(name), UPPER),
                notches);
    }
    private Rotor rotor6;
    private void setRotor6(String name, HashMap<String, String> rotors,
                           String notches) {
        rotor6 = new MovingRotor(name, new Permutation(rotors.get(name), UPPER),
                notches);
    }
    private Rotor rotor7;
    private void setRotor7(String name, HashMap<String, String> rotors,
                           String notches) {
        rotor7 = new MovingRotor(name, new Permutation(rotors.get(name), UPPER),
                notches);
    }
    private Rotor rotor8;
    private void setRotor8(String name, HashMap<String, String> rotors,
                           String notches) {
        rotor8 = new MovingRotor(name, new Permutation(rotors.get(name), UPPER),
                notches);
    }
    private Rotor rotorBeta;
    private void setRotorBeta(String name, HashMap<String, String> rotors,
                           String notches) {
        rotorBeta = new FixedRotor(name,
                new Permutation(rotors.get(name), UPPER));
    }
    private Rotor rotorGamma;
    private void setRotorGamma(String name, HashMap<String, String> rotors,
                           String notches) {
        rotorGamma = new FixedRotor(name,
                new Permutation(rotors.get(name), UPPER));
    }
    private Rotor rotorB;
    private void setRotorB(String name, HashMap<String, String> rotors,
                           String notches) {
        rotorB = new Reflector(name, new Permutation(rotors.get(name), UPPER));
    }
    private Rotor rotorC;
    private void setRotorC(String name, HashMap<String, String> rotors,
                           String notches) {
        rotorC = new Reflector(name, new Permutation(rotors.get(name), UPPER));
    }

    ArrayList<Rotor> allCollection = new ArrayList<Rotor>();

    public void setRotors() {
        setRotor1("I", NAVALA, "Q");
        setRotor2("II", NAVALA, "E");
        setRotor3("III", NAVALA, "V");
        setRotor4("IV", NAVALA, "J");
        setRotor5("V", NAVALA, "Z");
        setRotor6("VI", NAVALA, "ZM");
        setRotor7("VII", NAVALA, "ZM");
        setRotor8("VIII", NAVALA, "ZM");
        setRotorBeta("Beta", NAVALA, "");
        setRotorGamma("Gamma", NAVALA, "");
        setRotorB("B", NAVALA, "");
        setRotorC("C", NAVALA, "");

        Rotor[] rotor = new Rotor[]{rotor1, rotor2, rotor3, rotor4,
                                    rotor5, rotor6, rotor7, rotor8,
                                    rotorBeta, rotorGamma, rotorB, rotorC};
        for (int i = 0; i < 12; i++) {
            allCollection.add(rotor[i]);
        }
    }

    @Test
    public void testConvertInt() {
        setRotors();

        Machine m = new Machine(alpha, 5, 3, allCollection);

        assertEquals(5, m.numRotors());
        assertEquals(3, m.numPawls());

        m.setPlugboard(new Permutation("(YF) (ZH)", alpha));
        m.insertRotors(new String[]{"B", "Beta", "III", "IV", "I"});

        m.setRotors("AXLE");
        assertEquals(25, m.convert(24));
    }

    @Test
    public void testConvertStr() {
        setRotors();
        Machine m = new Machine(alpha, 5, 3, allCollection);
        m.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", alpha));
        m.insertRotors(new String[]{"B", "Beta", "III", "IV", "I"});

        m.setRotors("AXLE");
        assertEquals("QVPQSOKOILPUBKJZPISFXDW",
                m.convert("FROM HIS SHOULDER HIAWATHA"));

    }

    @Test
    public void testRiptide() {
        setRotors();
        Machine m = new Machine(alpha, 5, 3, allCollection);
        m.setPlugboard(new Permutation("(TD) (KC) (JZ)", alpha));
        m.insertRotors(new String[]{"B", "Beta", "I", "II", "III"});

        m.setRotors("AAAA");
        assertEquals("HGJNBOKDWALBFKUCMUTJZUIO"
                        + "XTYQFBDZRGBYFZCASYRU"
                        + "UAAFWOAGFKOCJGMUMOPCHTAVRSA"
                        + "HXHFRUXOFCBLRYSDXFCZXGVFANA"
                        + "CNBZHSNQMCMNIRWMTTTQBRNKRXDRPN"
                        + "AJIRVIFOVCTKGNUCKUMBITFENV",
                m.convert("I WAS SCARED OF CODING IN "
                        + "JAVA I WAS SCARED OF USING GIT "
                        + "AND STARTING ALL THESE PROJECTS"
                        + "COMPILER KEEPS GETTING MAD AT ME"
                        + "NOW MY PROJECT ONLY RUNS IN MY DREAMS"
                        + "OH OH ALL THESE MERGE CONFLICTS"));
    }
}
