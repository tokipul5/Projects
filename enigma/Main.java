package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Keeyou Kim
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        _machine = readConfig();
        String input = _input.nextLine();
        if (!input.contains("*")) {
            throw new EnigmaException("No * in setting");
        }
        while (_input.hasNextLine()) {
            String setting = input;
            if (!setting.contains("*")) {
                throw new EnigmaException("No * in setting");
            }
            numMoving = 0;
            setUp(_machine, setting);
            if (_machine.numPawls() != numMoving) {
                throw new EnigmaException(
                        "The number of moving rotors does not match.");
            }
            input = _input.nextLine();
            input = input.replaceAll(" ", "");
            while (!(input.contains("*"))) {
                input = _machine.convert(input);
                if (input.isEmpty()) {
                    _output.println();
                } else {
                    printMessageLine(input);
                }
                if (!_input.hasNextLine()) {
                    input = "*";
                } else {
                    input = _input.nextLine();
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alpha = _config.next();
            _alphabet = new Alphabet(alpha);
            if (!_config.hasNextInt()) {
                throw new EnigmaException("Provide number of rotors.");
            }
            int numRotor = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw new EnigmaException("Provide number of pawls.");
            }
            int numMovingR = _config.nextInt();
            _allRotors = new ArrayList<Rotor>();
            next = (_config.next()).toUpperCase();
            while (_config.hasNext()) {
                _name = next;
                _notch = (_config.next()).toUpperCase();
                _allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotor, numMovingR, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String perm = "";
            next = (_config.next());
            while (next.contains("(")
                    && next.contains(")")
                    && _config.hasNext()) {
                perm += next;
                perm += " ";
                next = (_config.next());
            }
            if (!_config.hasNext()) {
                perm += next;
            }
            if (_notch.charAt(0) == 'M') {
                return new MovingRotor(_name,
                        new Permutation(perm, _alphabet), _notch.substring(1));
            } else if (_notch.charAt(0) == 'N') {
                return new FixedRotor(_name, new Permutation(perm, _alphabet));
            } else {
                return new Reflector(_name, new Permutation(perm, _alphabet));
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] arrSetting = settings.split(" ");
        String[] strRotor = new String[M.numRotors()];
        String firstR = arrSetting[1];
        for (int i = 0; i < _allRotors.size(); i++) {
            Rotor rotorInConf = _allRotors.get(i);
            if (firstR.equals(rotorInConf.name())) {
                if (!(rotorInConf.reflecting())) {
                    throw new EnigmaException(
                            "The first rotor must be a reflector");
                }
            }
        }
        for (int i = 1; i < M.numRotors() + 1; i++) {
            String curr = arrSetting[i].toUpperCase();
            boolean found = false;
            for (int j = 0; j < _allRotors.size(); j++) {
                Rotor rotorInConf = _allRotors.get(j);
                if (curr.equals(rotorInConf.name().toUpperCase())) {
                    found = true;
                    if (rotorInConf.rotates()) {
                        numMoving++;
                    }
                }
            }
            if (!found) {
                throw new EnigmaException("Wrong name of a rotor");
            }
            strRotor[i - 1] = arrSetting[i];
        }

        for (int i = 0; i < strRotor.length; i++) {
            for (int j = i + 1; j < strRotor.length; j++) {
                if (strRotor[i].equals(strRotor[j])) {
                    throw new EnigmaException("Duplicated Rotors found");
                }
            }
        }
        int count = 1;
        M.insertRotors(strRotor);
        M.setRotors(arrSetting[M.numRotors() + count]);
        count++;
        if ((M.numRotors() + count) >= arrSetting.length) {
            boolean emptyBlock = true;
        } else if (arrSetting[M.numRotors() + count] instanceof String
                && !(arrSetting[M.numRotors() + count].contains("(")
                    && arrSetting[M.numRotors() + count].contains(")"))) {
            M.setRing(arrSetting[M.numRotors() + count]);
            count++;
        }
        String pairs = "";
        for (int i = (M.numRotors() + count); i < arrSetting.length; i++) {
            if (!(arrSetting[i].contains("(") && arrSetting[i].contains(")"))) {
                throw new EnigmaException(
                        "In order to set plugboard, put parenthesis.");
            }
            pairs += arrSetting[i]; pairs += " ";
        }
        M.setPlugboard(new Permutation(pairs, _alphabet));
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            if ((msg.length() - i) > 5) {
                _output.print(msg.substring(i, i + 5) + " ");
                i += 4;
            } else {
                _output.println(msg.substring(i));
                break;
            }
        }
    }
    /** An object of Machine. **/
    private Machine _machine;

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Name of rotor. **/
    private String _name;

    /** Store notches of rotor. **/
    private String _notch;

    /** Store all rotors. **/
    private ArrayList<Rotor> _allRotors;

    /** Stores next input. **/
    private String next;

    /** Stores the total number of moving rotors. **/
    private static int numMoving;
}
