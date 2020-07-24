package gitlet;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

/** Command merge.
 * @author Keeyou Kim
 */
public class Merge {

    /** Current working directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Take BRANCH and return its StringSet. */
    public static StringSet branchNameToStringSet(String branch) {
        File file = new File(".gitlet/heads/" + branch);
        String head = Utils.readContentsAsString(file);
        File commitObj = new File(".gitlet/commit/" + head);
        StringSet result = Utils.readObject(commitObj, StringSet.class);
        return result;
    }

    /** Take StringSet SET of branch and return HashMap<String, MergeObj>. */
    public static HashMap<String, MergeObj> setToHashMap(StringSet set) {
        int i = 0;
        HashMap<String, MergeObj> result = new HashMap<>();
        File mergeFile = new File(".gitlet/merge/" + set.get("uid"));
        if (mergeFile.exists()) {
            String secondParent = Utils.readContentsAsString(mergeFile);
            MergeObj mergeObj = new MergeObj(set.get("uid"), secondParent, i);
            result.put("secondParent", mergeObj);
        }
        MergeObj mergeObj = new MergeObj(set.get("uid"), set.get("parent"), i);
        result.put(set.get("uid"), mergeObj);
        while (!set.get("message").equals("initial commit")) {
            i++;
            File parent = new File(".gitlet/commit/" + set.get("parent"));
            set = Utils.readObject(parent, StringSet.class);
            mergeFile = new File(".gitlet/merge/" + set.get("uid"));
            if (mergeFile.exists()) {
                String secondParent = Utils.readContentsAsString(mergeFile);
                mergeObj = new MergeObj(set.get("uid"), secondParent, i);
                result.put("secondParent", mergeObj);
            }
            mergeObj = new MergeObj(set.get("uid"), set.get("parent"), i);
            result.put(set.get("uid"), mergeObj);
        }
        return result;
    }

    /** From CURRENT and GIVEN,
     *  find the closest split point and return split point. */
    public static MergeObj findSplitPoint(HashMap<String, MergeObj> current,
                                          HashMap<String, MergeObj> given) {
        int minDistance = Integer.MAX_VALUE;
        MergeObj splitPoint = null;
        for (Map.Entry e : current.entrySet()) {
            if (given.containsKey(e.getKey())
                    && !e.getKey().equals("secondParent")) {
                MergeObj temp = (MergeObj) e.getValue();
                if (temp.getDistance() < minDistance) {
                    minDistance = temp.getDistance();
                    splitPoint = temp;
                }
            }
            if (e.getKey().equals("secondParent")) {
                MergeObj temp = (MergeObj) e.getValue();
                String uidSecondParent = temp.getParent();
                if (given.containsKey(uidSecondParent)) {
                    if (temp.getDistance() < minDistance) {
                        minDistance = temp.getDistance();
                        splitPoint = given.get(uidSecondParent);
                    }
                }
            }
            if (given.containsKey("secondParent")) {
                MergeObj temp = given.get("secondParent");
                String uidTemp = temp.getParent();
                if (e.getKey().equals(uidTemp)) {
                    if (temp.getDistance() < minDistance) {
                        minDistance = temp.getDistance();
                        splitPoint = temp;
                    }
                }
            }
        }
        return splitPoint;
    }

    /** Store all files in a commit F and return set. */
    public static StringSet fileToSetFiles(File f) {
        StringSet commit = Utils.readObject(f, StringSet.class);
        StringSet result = new StringSet();
        for (Map.Entry e : commit.entrySet()) {
            if (!e.getKey().equals("parent")
                    && !e.getKey().equals("uid")
                    && !e.getKey().equals("message")
                    && !e.getKey().equals("date")) {
                result.put((String) e.getKey(), (String) e.getValue());
            }
        }
        return result;
    }

    /** Take BRANCH name and return uid of it. */
    public static String branchNameToUid(String branch) {
        File fileBranch = new File(".gitlet/heads/" + branch);
        String uid = Utils.readContentsAsString(fileBranch);
        return uid;
    }

    /** Take StringSet FILESATSPLIT, FILES
     *  and return modified files in StringSet. */
    public static StringSet checkModifiedFiles(StringSet filesAtSplit,
                                               StringSet files) {
        StringSet result = new StringSet();
        if (filesAtSplit.isEmpty()) {
            result = files;
        } else {
            for (Map.Entry e : filesAtSplit.entrySet()) {
                String fileName = (String) e.getKey();
                String uidContent = (String) e.getValue();
                if (files.containsKey(fileName)) {
                    if (!uidContent.equals(files.get(fileName))) {
                        result.put(fileName, files.get(fileName));
                    }
                }
            }
        }
        return result;
    }

    /** Take StringSet FILESATSPLIT, FILES
     *  and return absent files in StringSet. */
    public static StringSet checkAbsentFiles(StringSet filesAtSplit,
                                             StringSet files) {
        StringSet result = new StringSet();
        for (Map.Entry e : filesAtSplit.entrySet()) {
            String fileName = (String) e.getKey();
            if (!files.containsKey(fileName)) {
                result.put(fileName, (String) e.getValue());
            }
        }
        return result;
    }

    /** Take StringSet FILESATSPLIT, CHECKING, OTHER
     *  and return only present files in StringSet. */
    public static StringSet checkOnlyPresentFiles(StringSet filesAtSplit,
                                                  StringSet checking,
                                                  StringSet other) {
        StringSet result = new StringSet();
        for (Map.Entry e : checking.entrySet()) {
            String fileName = (String) e.getKey();
            if (!filesAtSplit.containsKey(fileName)
                    && !other.containsKey(fileName)) {
                result.put(fileName, (String) e.getValue());
            }
        }
        return result;
    }

    /** Take StringSet CURRENT, GIVEN
     *  and return both modified files in StringSet. */
    public static StringSet checkBothModified(StringSet current,
                                              StringSet given) {
        StringSet result = new StringSet();
        for (Map.Entry e : current.entrySet()) {
            String fileName = (String) e.getKey();
            if (given.containsKey(fileName)) {
                result.put(fileName, (String) e.getValue());
            }
        }
        return result;
    }

    /** Take StringSet SETBOTH, CURR, GIVEN
     *  and return modified same files in StringSet. */
    public static boolean checkModifiedSame(StringSet setBoth,
                                            StringSet curr,
                                            StringSet given) {
        boolean result = true;
        for (Map.Entry e : setBoth.entrySet()) {
            String fileName = (String) e.getKey();
            String currContent = curr.get(fileName);
            String givenContent = given.get(fileName);
            if (!currContent.equals(givenContent)) {
                result = false;
            }
        }
        return result;
    }

    /** Take StringSet SETONLYPRESENTGIVEN and check out and stage the file. */
    public static void checkOutAndStaged(StringSet setOnlyPresentGiven) {
        for (Map.Entry e : setOnlyPresentGiven.entrySet()) {
            String fileUid = (String) e.getValue();
            File f = new File(".gitlet/object/" + fileUid);
            Blob b = Utils.readObject(f, Blob.class);
            File output = new File((String) e.getKey());
            Utils.writeContents(output, b.getContent());
            File stageDir = new File(".gitlet/stageAdd/");
            File add = Utils.join(stageDir, (String) e.getKey());
            Utils.writeContents(add, b.getUid());
        }
    }

    /** Take SETABSENTGIVEN and delete files in the StringSet. */
    public static void remove(StringSet setAbsentGiven) {
        for (Map.Entry e : setAbsentGiven.entrySet()) {
            String fileName = (String) e.getKey();
            File rmFile = new File(fileName);
            rmFile.delete();
        }
    }

    /** Remove FILENAME. */
    public static void removeFile(String fileName) {
        File rm = new File(fileName);
        rm.delete();
    }

    /** Print what contains in the StringSet S. */
    public static void print(StringSet s) {
        for (Map.Entry e : s.entrySet()) {
            System.out.println(".");
            System.out.println(e.getKey());
            System.out.println(e.getValue());
        }
    }

    /** Merge Commit takes String GIVENBRANCHNAME, CURRENTBRANCHNAME. */
    public static void mergeCommit(String givenBranchName,
                                   String currentBranchName) throws Exception {
        Commit.commit("Merged " + givenBranchName
                    + " into " + currentBranchName + ".");
        File headGiven = new File(".gitlet/heads/" + givenBranchName);
        String uidHeadGiven = Utils.readContentsAsString(headGiven);
        File currentFile = new File(".gitlet/heads/" + currentBranchName);
        String newCommitUid = Utils.readContentsAsString(currentFile);
        File mergeFolder = new File(".gitlet/merge/" + newCommitUid);
        Utils.writeContents(mergeFolder, uidHeadGiven);
    }

    /** Take StringSet SETBOTH, CURR, GIVEN and deal with conflich
     *  that both branches are modified. */
    public static void conflictBothModified(StringSet setBoth,
                                            StringSet curr,
                                            StringSet given) {
        System.out.println("Encountered a merge conflict.");
        for (Map.Entry e : setBoth.entrySet()) {
            File f = new File((String) e.getKey());
            File currBlob = new File(".gitlet/blob/" + curr.get(e.getKey()));
            String currText = Utils.readContentsAsString(currBlob);
            File givenBlob = new File(".gitlet/blob/" + given.get(e.getKey()));
            String givenText = Utils.readContentsAsString(givenBlob);
            String text = "<<<<<<< HEAD\n"
                    + currText
                    + "=======\n"
                    + givenText
                    + ">>>>>>>\n";
            Utils.writeContents(f, text);
            File stagedAdd = new File(".gitlet/stageAdd/" + e.getKey());
            Blob b = new Blob((String) e.getKey(), text);
            Utils.writeContents(stagedAdd, b.getUid());
        }
    }

    /** Take StringSet SETCURRMODIFIED, SETABSENTGIVEN and deal with a
     * conflict modified in curr and absent in given. */
    public static void conflictModifiedOneAndRmOther(
            StringSet setCurrModified, StringSet setAbsentGiven) {
        System.out.println("Encountered a merge conflict.");
        for (Map.Entry e : setCurrModified.entrySet()) {
            File f = new File((String) e.getKey());
            File currBlob = new File(".gitlet/blob/"
                    + setCurrModified.get(e.getKey()));
            String currText = Utils.readContentsAsString(currBlob);
            String givenText = "";
            String text = "<<<<<<< HEAD\n"
                    + currText
                    + "=======\n"
                    + givenText
                    + ">>>>>>>\n";
            Utils.writeContents(f, text);
            File stagedAdd = new File(".gitlet/stageAdd/" + e.getKey());
            Blob b = new Blob((String) e.getKey(), text);
            Utils.writeContents(stagedAdd, b.getUid());
        }
    }

    /** Take SETGIVENMODIFIED and SETCURRMODIFIED to find StringSet. */
    public static void modifiedGivenNotCurrent(StringSet setGivenModified,
                                               StringSet setCurrModified) {
        StringSet result = new StringSet();
        for (Map.Entry e : setGivenModified.entrySet()) {
            if (!setCurrModified.containsKey(e.getKey())) {
                result.put((String) e.getKey(), (String) e.getValue());
            }
        }
        checkOutAndStaged(result);
    }

    /** Take String BRANCHNAME and return error that Merge.java can make. */
    public static boolean printError(String branchName) {
        boolean error = false;
        File file = new File(".gitlet/heads/" + branchName);
        File currentHead = new File(".gitlet/heads/current");
        String fileNameCurrentBranch = Utils.readContentsAsString(currentHead);
        if (!file.exists()) {
            error = true;
            System.out.println("A branch with that name does not exist.");
        } else if (fileNameCurrentBranch.equals(branchName)) {
            error = true;
            System.out.println("Cannot merge a branch with itself.");
        }
        return error;
    }

    /** Check if there is untracked file, and return error. */
    public static boolean untrackedFileExist() {
        boolean error = false;
        for (File f : CWD.listFiles()) {
            if (!f.isDirectory()) {
                String fileName = f.getName();
                String content = Utils.readContentsAsString(f);
                String uid = Utils.sha1(fileName + content);
                File b = new File(".gitlet/blob/" + uid);
                if (!b.exists()) {
                    error = true;
                    System.out.println(
                            "There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                }
            }
        }
        return error;
    }

    /** Check if there is a file in stagedAdd or Removal, and return error. */
    public static boolean existStagedAddOrRemoval() {
        boolean error = false;
        File stagedAdd = new File(".gitlet/stageAdd");
        File removal = new File(".gitlet/removal");
        if (stagedAdd.listFiles().length != 0
                || removal.listFiles().length != 0) {
            System.out.println("You have uncommitted changes.");
            error = true;
        }
        return error;
    }

    /** Check if BRANCHNAME is an ancestor, and return error. */
    public static boolean checkAncestor(String branchName) {
        boolean error = false;
        File givenBranch = new File(".gitlet/heads/" + branchName);
        String uidGivenCommit = Utils.readContentsAsString(givenBranch);

        File current = new File(".gitlet/heads/current");
        String currentBranchFileName = Utils.readContentsAsString(current);
        File currentBranch = new File(".gitlet/heads/"
                + currentBranchFileName);
        String commitName = Utils.readContentsAsString(currentBranch);
        File commit = new File(".gitlet/commit/" + commitName);
        StringSet commitSet = Utils.readObject(commit, StringSet.class);
        String uidParentCurrentBranch = commitSet.get("parent");
        if (uidGivenCommit.equals(uidParentCurrentBranch)) {
            System.out.println(
                    "Given branch is an ancestor of the current branch.");
            error = true;
        }
        while (!uidParentCurrentBranch.isEmpty()) {
            currentBranch = new File(".gitlet/commit/"
                    + uidParentCurrentBranch);
            commitSet = Utils.readObject(currentBranch, StringSet.class);
            uidParentCurrentBranch = commitSet.get("parent");
            if (uidGivenCommit.equals(uidParentCurrentBranch)) {
                System.out.println(
                        "Given branch is an ancestor of the current branch.");
                error = true;
                break;
            }
        }
        return error;
    }

    /** Check if BRANCHNAME is a split point with given SPLITPOINT,
     *  and return error. */
    public static boolean checkCurrentEqualsSplitPoint(String branchName,
                                                       MergeObj splitPoint) {
        boolean error = false;
        File givenBranch = new File(".gitlet/heads/current");
        String currentFileName = Utils.readContentsAsString(givenBranch);
        File currentBranch = new File(".gitlet/heads/" + currentFileName);
        String uidCurrentBranch = Utils.readContentsAsString(currentBranch);
        if (uidCurrentBranch.equals(splitPoint.getUid())) {
            Checkout.checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            error = true;
        }
        return error;
    }

    /** Command merge takes BRANCHNAME. */
    public static void merge(String branchName) throws Exception {
        if (existStagedAddOrRemoval() || printError(branchName)
                || untrackedFileExist() || checkAncestor(branchName)) {
            return;
        }
        StringSet tempGiven = branchNameToStringSet(branchName);
        HashMap<String, MergeObj> givenMap = setToHashMap(tempGiven);
        File current = new File(".gitlet/heads/current");
        String fileNameCurrBranch = Utils.readContentsAsString(current);
        StringSet tempCurr = branchNameToStringSet(fileNameCurrBranch);
        HashMap<String, MergeObj> currentMap = setToHashMap(tempCurr);
        MergeObj splitPoint = findSplitPoint(currentMap, givenMap);
        if (checkCurrentEqualsSplitPoint(branchName, splitPoint)) {
            return;
        }
        String uidSplitPoint = splitPoint.getUid();
        File commitSplitPoint = new File(".gitlet/commit/" + uidSplitPoint);
        StringSet fSP = fileToSetFiles(commitSplitPoint);
        String uidGivenBranch = branchNameToUid(branchName);
        File commitGivenBranch = new File(".gitlet/commit/" + uidGivenBranch);
        StringSet fGivenBran = fileToSetFiles(commitGivenBranch);
        String uidCurrentBranch = branchNameToUid(fileNameCurrBranch);
        File commitCurrBranch = new File(".gitlet/commit/" + uidCurrentBranch);
        StringSet filesCurrBranch = fileToSetFiles(commitCurrBranch);
        StringSet setGivenModified = checkModifiedFiles(fSP, fGivenBran);
        StringSet setCurrModified = checkModifiedFiles(fSP, filesCurrBranch);
        StringSet setBothModified = checkBothModified(setCurrModified,
                                                        setGivenModified);
        StringSet setRmGiven = checkAbsentFiles(fSP, fGivenBran);
        StringSet setOnlyPresentGivenBranch = checkOnlyPresentFiles
                (fSP, fGivenBran, filesCurrBranch);
        if (!setBothModified.isEmpty()) {
            conflictBothModified(setBothModified, filesCurrBranch, fGivenBran);
        } else if (!setCurrModified.isEmpty() && !setRmGiven.isEmpty()) {
            for (Map.Entry e : setCurrModified.entrySet()) {
                if (setRmGiven.containsKey(e.getKey())) {
                    conflictModifiedOneAndRmOther(setCurrModified, setRmGiven);
                }
            }
        }
        if (!setGivenModified.isEmpty()) {
            modifiedGivenNotCurrent(setGivenModified, setCurrModified);
        }
        if (setCurrModified.isEmpty() && !setRmGiven.isEmpty()) {
            remove(setRmGiven);
        }
        if (!setRmGiven.isEmpty()) {
            for (Map.Entry e : setRmGiven.entrySet()) {
                if (filesCurrBranch.containsKey(e.getKey())) {
                    if (!setCurrModified.containsKey(e.getKey())) {
                        removeFile((String) e.getKey());
                    }
                }
            }
        }
        if (!setOnlyPresentGivenBranch.isEmpty()) {
            checkOutAndStaged(setOnlyPresentGivenBranch);
        }
        mergeCommit(branchName, fileNameCurrBranch);
    }
}

