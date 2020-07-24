/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import java.util.regex.Pattern;

import static loa.Piece.*;
import static loa.Square.*;

/** Represents the state of a game of Lines of Action.
 *  @author Keeyou Kim
 */
class Board {

    /** Default number of moves for each side that results in a draw. */
    static final int DEFAULT_MOVE_LIMIT =100;

    /** Pattern describing a valid square designator (cr). */
    static final Pattern ROW_COL = Pattern.compile("^[a-h][1-8]$");

    /** A Board whose initial contents are taken from INITIALCONTENTS
     *  and in which the player playing TURN is to move. The resulting
     *  Board has
     *        get(col, row) == INITIALCONTENTS[row][col]
     *  Assumes that PLAYER is not null and INITIALCONTENTS is 8x8.
     *
     *  CAUTION: The natural written notation for arrays initializers puts
     *  the BOTTOM row of INITIALCONTENTS at the top.
     */
    Board(Piece[][] initialContents, Piece turn) {
        initialize(initialContents, turn);
    }

    /** A new board in the standard initial position. */
    Board() {
        this(INITIAL_PIECES, BP);
    }

    /** A Board whose initial contents and state are copied from
     *  BOARD. */
    Board(Board board) {
        this();
        copyFrom(board);
    }

    /** Set my state to CONTENTS with SIDE to move. */
    void initialize(Piece[][] contents, Piece side) {
        _whiteRegionSizes = new ArrayList<>();
        _blackRegionSizes = new ArrayList<>();
        _board = new Piece[BOARD_SIZE  * BOARD_SIZE];
        _moves = new ArrayList<>();
        biggestBlackRegion = 0;
        biggestWhiteRegion = 0;
        _sqBiggestBlack = new ArrayList<>();
        _sqBiggestWhite = new ArrayList<>();
        _sqBiggestBlackTemp = new ArrayList<>();
        _sqBiggestWhiteTemp = new ArrayList<>();
        _retract = new ArrayList<>();
        _subsetsInitialized = false;
        _winnerKnown = false;
        _winner = null;
        for (int i = BOARD_SIZE - 1; i >= 0; i--) {
            for (int j = BOARD_SIZE - 1; j >= 0; j--) {
                set(sq(j, i), contents[i][j]);
            }
        }
        _turn = side;
        _moveLimit = DEFAULT_MOVE_LIMIT;
    }

    /** Set me to the initial configuration. */
    void clear() {
        initialize(INITIAL_PIECES, BP);
    }

    /** Set my state to a copy of BOARD. */
    void copyFrom(Board board) {
        if (board == this) {
            return;
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                set(sq(i, j), board.get(sq(i, j)));
            }
        }
        _turn = board.turn();
        _moves.addAll(board._moves);
    }

    /** Return the contents of the square at SQ. */
    Piece get(Square sq) {
        return _board[sq.index()];
    }

    /** Set the square at SQ to V and set the side that is to move next
     *  to NEXT, if NEXT is not null. */
    void set(Square sq, Piece v, Piece next) {
        if (next != null) {
            _turn = next;
        }
        if (sq != null && v != null) {
            _board[sq.index()] = v;
        }
    }

    /** Set the square at SQ to V, without modifying the side that
     *  moves next. */
    void set(Square sq, Piece v) {
        set(sq, v, null);
    }

    /** Set limit on number of moves by each side that results in a tie to
     *  LIMIT, where 2 * LIMIT > movesMade(). */
    void setMoveLimit(int limit) {
        if (2 * limit <= movesMade()) {
            throw new IllegalArgumentException("move limit too small");
        }
        _moveLimit = 2 * limit;
    }

    /** Assuming isLegal(MOVE), make MOVE. This function assumes that
     *  MOVE.isCapture() will return false.  If it saves the move for
     *  later retraction, makeMove itself uses MOVE.captureMove() to produce
     *  the capturing move. */
    void makeMove(Move move) {
        assert isLegal(move);
        _moveLimit--;
        _moves.add(move);
        Square from = move.getFrom();
        Square to = move.getTo();
        _retract.add(_board[to.index()]);
        set(to, _board[from.index()], turn().opposite());
        set(from, EMP);
        _subsetsInitialized = false;
        computeRegions();
    }

    /** Retract (unmake) one move, returning to the state immediately before
     *  that move.  Requires that movesMade () > 0. */
    void retract() {
        assert movesMade() > 0;
        _moveLimit++;
        Move move = _moves.remove(_moves.size() - 1);
        Square from = move.getFrom();
        Square to = move.getTo();
        set(from, _board[to.index()]);
        Piece restore = _retract.remove(0);
        set(to, restore, turn().opposite());
    }

    /** Return the Piece representing who is next to move. */
    Piece turn() {
        return _turn;
    }

    /** Return true iff FROM - TO is a legal move for the player currently on
     *  move. */
    boolean isLegal(Square from, Square to) {
        if (!from.isValidMove(to)) {
            return false;
        }
        int dir = from.direction(to);
        int dis = from.distance(to);
        Piece pFrom = _board[from.index()];
        Piece pTo = _board[to.index()];
        for (int i = 1; i < dis; i++) {
            Square temp = from.moveDest(dir, i);
            if (_board[temp.index()].equals(pFrom.opposite())) {
                return false;
            }
        }
        if (pFrom.equals(pTo)) {
            return false;
        }
        int totalPiece = countPieceInDir(from, dir);
        if (totalPiece != dis) {
            return false;
        }
        return true;
    }

    /** Return the number of pieces in DIR direction of FROM. */
    int countPieceInDir(Square from, int dir) {
        List<Square> squareInDir = new ArrayList<>();
        squareInDir.add(from);
        int oppDir = 0;
        if (dir >= 4) {
            oppDir = dir - 4;
        } else {
            oppDir = dir + 4;
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            Square temp = from.moveDest(dir, i);
            if (temp !=  null && temp.row() < BOARD_SIZE
                    && temp.col() < BOARD_SIZE) {
                if (!_board[temp.index()].equals(EMP)) {
                    squareInDir.add(from.moveDest(dir, i));
                }
            }
            Square oppTemp = from.moveDest(oppDir, i);
            if (oppTemp != null && oppTemp.row() < BOARD_SIZE
                    && oppTemp.col() < BOARD_SIZE) {
                if (!_board[oppTemp.index()].equals(EMP)) {
                    squareInDir.add(from.moveDest(oppDir, i));
                }
            }
        }
        return squareInDir.size();
    }

    /** Return true iff MOVE is legal for the player currently on move.
     *  The isCapture() property is ignored. */
    boolean isLegal(Move move) {
        return isLegal(move.getFrom(), move.getTo());
    }

    /** Return a sequence of all legal moves from this position. */
    List<Move> legalMoves() {
        List<Move> list = new ArrayList<Move>();
        for (int i = 0; i < ALL_SQUARES.length; i++) {
            Square from = ALL_SQUARES[i];
            if (_board[from.index()].equals(_turn)) {
                for (int j = 0; j < ALL_SQUARES.length; j++) {
                    Square to = ALL_SQUARES[j];
                    if (isLegal(from, to)) {
                        list.add(Move.mv(from, to));
                    }
                }
            }
        }
        return list;
    }

    /** Return true iff the game is over (either player has all his
     *  pieces continguous or there is a tie). */
    boolean gameOver() {
        return winner() != null;
    }

    /** Return true iff SIDE's pieces are continguous. */
    boolean piecesContiguous(Piece side) {
        return getRegionSizes(side).size() == 1;
    }

    /** Return the winning side, if any.  If the game is not over, result is
     *  null.  If the game has ended in a tie, returns EMP. */
    Piece winner() {
        if (!_winnerKnown) {
            if (_moveLimit <= 0 && !piecesContiguous(BP)
                    && !piecesContiguous(WP)) {
                return EMP;
            }
            if (_turn == WP) {
                if (piecesContiguous(BP)) {
                    _winnerKnown = true;
                    _winner = BP;
                    return BP;
                } else if (piecesContiguous(WP)) {
                    _winnerKnown = true;
                    _winner = WP;
                    return WP;
                }
            } else if (_turn == BP) {
                if (piecesContiguous(WP)) {
                    _winnerKnown = true;
                    _winner = WP;
                    return WP;
                } else if (piecesContiguous(BP)) {
                    _winnerKnown = true;
                    _winner = BP;
                    return BP;
                }
            }
        }
        return _winner;
    }

    /** Return the total number of moves that have been made (and not
     *  retracted).  Each valid call to makeMove with a normal move increases
     *  this number by 1. */
    int movesMade() {
        return _moves.size();
    }

    @Override
    public boolean equals(Object obj) {
        Board b = (Board) obj;
        return Arrays.deepEquals(_board, b._board) && _turn == b._turn;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(_board) * 2 + _turn.hashCode();
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int r = BOARD_SIZE - 1; r >= 0; r -= 1) {
            out.format("    ");
            for (int c = 0; c < BOARD_SIZE; c += 1) {
                out.format("%s ", get(sq(c, r)).abbrev());
            }
            out.format("%n");
        }
        out.format("Next move: %s%n===", turn().fullName());
        return out.toString();
    }

    /** Return true if a move from FROM to TO is blocked by an opposing
     *  piece or by a friendly piece on the target square. */
    private boolean blocked(Square from, Square to) {
        int dir = from.direction(to);
        int dis = from.distance(to);
        boolean result = false;
        for (int i = 1; i < dis; i++) {
            Square temp = from.moveDest(dir, i);
            if (temp != null) {
                result = true;
            }
        }
        return result;
    }

    /** Return the size of the as-yet unvisited cluster of squares
     *  containing P at and adjacent to SQ.  VISITED indicates squares that
     *  have already been processed or are in different clusters.  Update
     *  VISITED to reflect squares counted. */
    private int numContig(Square sq, boolean[][] visited, Piece p) {
        int row = sq.row();
        int col = sq.col();
        if (!visited[row][col] && _board[sq.index()].equals(p)) {
            return numContigHelper(sq, visited, p, 1);
        }
        return 0;
    }

    /** Return count and numContig's helper function which
     * has same parameters SQ, VISITED, P,
     * and COUNT which counts the adjacent to SQ. */
    private int numContigHelper(Square sq, boolean[][] visited,
                                Piece p, int count) {
        int row = sq.row();
        int col = sq.col();
        if (!visited[row][col] && _board[sq.index()].equals(p)) {
            visited[row][col] = true;
            if (_board[sq.index()].equals(WP)
                    && !_sqBiggestWhiteTemp.contains(sq)) {
                _sqBiggestWhiteTemp.add(sq);
            } else if (_board[sq.index()].equals(BP)
                    && !_sqBiggestBlackTemp.contains(sq)) {
                _sqBiggestBlackTemp.add(sq);
            }
            for (int i = 0; i < BOARD_SIZE; i++) {
                Square temp = sq.moveDest(i, 1);
                if (temp != null) {
                    if (_board[temp.index()].equals(WP)) {
                        _sqBiggestWhiteTemp.add(temp);
                    } else if (_board[temp.index()].equals(BP)) {
                        _sqBiggestBlackTemp.add(temp);
                    }
                }
                if (temp != null && _board[temp.index()].equals(p)
                        && !visited[temp.row()][temp.col()]) {
                    count++;
                    count = numContigHelper(temp, visited, p, count);
                }
            }
        }
        return count;
    }

    /** Set the values of _whiteRegionSizes and _blackRegionSizes. */
    private void computeRegions() {
        if (_subsetsInitialized) {
            return;
        }
        _whiteRegionSizes.clear();
        _blackRegionSizes.clear();

        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Square temp = sq(i, j);
                int white = 0;
                if (_board[temp.index()].equals(WP)) {
                    white = numContig(temp, visited, WP);
                }
                int black = 0;
                if (_board[temp.index()].equals(BP)) {
                    black = numContig(temp, visited, BP);
                }
                if (white > biggestWhiteRegion) {
                    biggestWhiteRegion = white;
                    _sqBiggestWhite = _sqBiggestWhiteTemp;
                    _sqBiggestWhiteTemp = new ArrayList<>();
                }
                if (black > biggestBlackRegion) {
                    biggestBlackRegion = black;
                    _sqBiggestBlack = _sqBiggestBlackTemp;
                    _sqBiggestBlackTemp = new ArrayList<>();
                }
                if (white != 0) {
                    _whiteRegionSizes.add(white);
                } else if (black != 0) {
                    _blackRegionSizes.add(black);
                }
            }
        }
        Collections.sort(_whiteRegionSizes, Collections.reverseOrder());
        Collections.sort(_blackRegionSizes, Collections.reverseOrder());
        _subsetsInitialized = true;
    }

    /** Return the biggest list of black squares in the contiguous regions. */
    List<Square> getSqBiggestBlack() {
        return _sqBiggestBlack;
    }

    /** Return the biggest list of white squares in the contiguous regions. */
    List<Square> getSqBiggestWhite() {
        return _sqBiggestWhite;
    }

    /** Return the total distance that got from P. */
    int getTotalDistanceFromRegion(Piece p) {
        int result = 0;
        List<Square> listSq;
        if (p.equals(WP)) {
            listSq = getSqBiggestWhite();
        } else {
            listSq = getSqBiggestBlack();
        }
        for (Square sq : listSq) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    Square temp = sq(i, j);
                    if (get(temp).equals(p) && !listSq.contains(temp)) {
                        result += sq.distance(temp);
                    }
                }
            }
        }
        return result;
    }

    /** Return the sizes of all the regions in the current union-find
     *  structure for side S. */
    List<Integer> getRegionSizes(Piece s) {
        computeRegions();
        if (s == WP) {
            return _whiteRegionSizes;
        } else {
            return _blackRegionSizes;
        }
    }

    /** Set the heuristic VALUE for the current board. */
    void setHeuristic(int value) {
        heuristic = value;
    }

    /** Return the heuristic value. */
    int getHeuristic() {
        return heuristic;
    }

    /** Set the move M that causes the change
     *  in the heuristic value.
     */
    void setHeuristicMove(Move m) {
        _heuristicMove = m;
    }

    /** Return the heuristic move that got from setHeuristicMove. */
    Move getHeuristicMove() {
        return _heuristicMove;
    }

    /** Store heuristic move. */
    private Move _heuristicMove;

    /** Store heuristic value. */
    private int heuristic;

    /** The standard initial configuration for Lines of Action (bottom row
     *  first). */
    static final Piece[][] INITIAL_PIECES = {
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
    };

    /** Store the biggest size of black contiguous regions. */
    private int biggestBlackRegion;

    /** Store the biggest size of white contiguous regions. */
    private int biggestWhiteRegion;

    /** Store the pieces in the biggest black contiguous region. */
    private ArrayList<Square> _sqBiggestBlack = new ArrayList<>();

    /** Store the pieces in the biggest white contiguous region. */
    private ArrayList<Square> _sqBiggestWhite = new ArrayList<>();

    /** Store the temporary pieces in the biggest black contiguous region. */
    private ArrayList<Square> _sqBiggestBlackTemp = new ArrayList<>();

    /** Store the temporary pieces in the biggest white contiguous region. */
    private ArrayList<Square> _sqBiggestWhiteTemp = new ArrayList<>();

    /** Store pieces in order to retract. */
    private ArrayList<Piece> _retract = new ArrayList<>();

    /** Current contents of the board.  Square S is at _board[S.index()]. */
    private Piece[] _board = new Piece[BOARD_SIZE  * BOARD_SIZE];

    /** List of all unretracted moves on this board, in order. */
    private ArrayList<Move> _moves = new ArrayList<>();
    /** Current side on move. */
    private Piece _turn;
    /** Limit on number of moves before tie is declared.  */
    private int _moveLimit;
    /** True iff the value of _winner is known to be valid. */
    private boolean _winnerKnown;
    /** Cached value of the winner (BP, WP, EMP (for tie), or null (game still
     *  in progress).  Use only if _winnerKnown. */
    private Piece _winner;

    /** True iff subsets computation is up-to-date. */
    private boolean _subsetsInitialized;

    /** List of the sizes of contiguous clusters of pieces, by color. */
    private ArrayList<Integer>
        _whiteRegionSizes = new ArrayList<>(),
        _blackRegionSizes = new ArrayList<>();
}
