package gitlet;

import java.io.File;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Keeyou Kim
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws Exception {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        switch (args[0]) {
        case "init":
            Init.init();
            break;
        case "add":
            Add.add(args[1]);
            break;
        case "commit":
            Commit.commit(args[1]);
            break;
        case "rm":
            Rm.rm(args[1]);
            break;
        case "log":
            Log.log();
            break;
        case "global-log":
            GlobalLog.globalLog();
            break;
        case "status":
            callStatus();
            break;
        case "find":
            Find.find(args[1]);
            break;
        case "checkout":
            if (args.length == 3) {
                Checkout.checkoutFile(args[2]);
            } else if (args.length == 4) {
                Checkout.checkoutIdFileName(args[1], args[2], args[3]);
            } else if (args.length == 2) {
                Checkout.checkoutBranch(args[1]);
            }
            break;
        case "branch":
            Branch newBranch = new Branch(args[1]);
            break;
        case "rm-branch":
            RmBranch.rmBranch(args[1]);
            break;
        case "reset":
            Reset.reset(args[1]);
            break;
        case "merge":
            Merge.merge(args[1]);
            break;
        default:
            System.out.println("No command with that name exists.");
        }
    }

    /** Call command status. */
    public static void callStatus() {
        File current = new File(".gitlet/heads/current");
        if (current.exists()) {
            Status.status();
        } else {
            System.out.println("Not in an initialized Gitlet directory.");
        }
    }
}
