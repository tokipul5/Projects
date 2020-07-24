/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import static loa.Piece.*;

/** An automated Player.
 *  @author Keeyou Kim
 */
class MachinePlayer extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new MachinePlayer with no piece or controller (intended to produce
     *  a template). */
    MachinePlayer() {
        this(null, null);
    }

    /** A MachinePlayer that plays the SIDE pieces in GAME. */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
    }

    @Override
    String getMove() {
        Move choice;

        assert side() == getGame().getBoard().turn();
        int depth;
        choice = searchForMove();
        getGame().reportMove(choice);
        return choice.toString();
    }

    @Override
    Player create(Piece piece, Game game) {
        return new MachinePlayer(piece, game);
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private Move searchForMove() {
        Board work = new Board(getBoard());
        int value;
        assert side() == work.turn();
        _foundMove = null;
        if (side() == WP) {
            value = findMove(work, chooseDepth(), true, 1, -INFTY, INFTY);
        } else {
            value = findMove(work, chooseDepth(), true, -1, -INFTY, INFTY);
        }
        return _foundMove;
    }

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    public int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (depth == 0) {
            return heuristic(board);
        } else {
            List<Board> listBoard = getBoardHeuristic(board);
            int returnValue = 0;
            if (sense == 1) {
                int newAlpha = INFTY * -1;
                for (Board b : listBoard) {
                    int heuristic = findMove(b, depth - 1,
                            false, -1, alpha, beta);
                    if (heuristic > newAlpha && saveMove) {
                        returnM = b.getHeuristicMove();
                    }
                    newAlpha = Math.max(newAlpha, heuristic);
                    alpha = Math.max(alpha, newAlpha);
                    if (alpha > beta) {
                        returnValue = alpha;
                        break;
                    } else {
                        returnValue = newAlpha;
                    }
                }
            } else {
                int newBeta = INFTY;
                for (Board b : listBoard) {
                    int heuristic = findMove(b, depth - 1,
                            false, 1, alpha, beta);
                    if (heuristic < newBeta && saveMove) {
                        returnM = b.getHeuristicMove();
                    }
                    newBeta = Math.min(newBeta, heuristic);
                    beta = Math.min(beta, newBeta);
                    if (alpha > beta) {
                        returnValue = beta;
                        break;
                    } else {
                        returnValue = newBeta;
                    }
                }
            }
            if (saveMove) {
                _foundMove = returnM;
            }
            return returnValue;
        }
    }

    /** Return a search depth for the current position. */
    private int chooseDepth() {
        return 1;
    }

    /** Return a list of heuristic values of BOARD. */
    List<Board> getBoardHeuristic(Board board) {
        ArrayList<Board> result = new ArrayList<>();
        List<Move> listMove = board.legalMoves();
        Collections.shuffle(listMove);
        for (int i = 0; i < listMove.size(); i++) {
            Board temp = new Board(board);
            Move m = listMove.get(i);
            temp.makeMove(m);
            temp.set(null, null, side());
            temp.setHeuristicMove(m);
            temp.setHeuristic(heuristic(temp));
            result.add(temp);
        }
        return result;
    }

    /** Return a heuristic of BOARD. */
    private int heuristic(Board board) {
        List<Integer> whiteRegionSizes = board.getRegionSizes(WP);
        List<Integer> blackRegionSizes = board.getRegionSizes(BP);
        int whiteDistance = board.getTotalDistanceFromRegion(WP);
        int blackDistance = board.getTotalDistanceFromRegion(BP);
        int oppLeft = 0;
        for (int i = 1; i < blackRegionSizes.size(); i++) {
            oppLeft += blackRegionSizes.get(i);
        }
        int value;
        if (whiteRegionSizes.size() == 1) {
            if (board.turn() == WP) {
                return WINNING_VALUE;
            } else {
                return WINNING_VALUE * -1;
            }
        }
        int pLeft = 0;
        for (int i = 1; i < whiteRegionSizes.size(); i++) {
            pLeft += whiteRegionSizes.get(i);
        }

        value = whiteRegionSizes.size() * -1 - pLeft + oppLeft
                + blackRegionSizes.size() - whiteDistance + blackDistance;

        return value;
    }

    /** Used to store the move according to the heuristic value. */
    private Move returnM = null;

    /** Used to convey moves discovered by findMove. */
    private Move _foundMove;

}
