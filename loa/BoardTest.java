/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

import static loa.Piece.*;
import static loa.Square.sq;
import static loa.Move.mv;

/** Tests of the Board class API.
 *  @author Keeyou Kim
 */
public class BoardTest {

    /** A "general" position. */
    static final Piece[][] BOARD1 = {
        { EMP, BP,  EMP,  BP,  BP, EMP, EMP, EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP,  BP,  BP, EMP, WP  },
        { WP,  EMP,  BP, EMP, EMP,  WP, EMP, EMP  },
        { WP,  EMP,  WP,  WP, EMP,  WP, EMP, EMP  },
        { WP,  EMP, EMP, EMP,  BP, EMP, EMP, WP  },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
        { EMP, BP,  BP,  BP,  EMP,  BP,  BP, EMP }
    };

    /** A position in which black, but not white, pieces are contiguous. */
    static final Piece[][] BOARD2 = {
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP,  BP,  WP,  BP,  BP,  BP, EMP, EMP },
        { EMP,  WP,  BP,  WP,  WP, EMP, EMP, EMP },
        { EMP, EMP,  BP,  BP,  WP,  WP, EMP,  WP },
        { EMP,  WP,  WP,  BP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP,  BP, EMP, EMP, EMP, EMP },
    };

    /** A position in which black, but not white, pieces are contiguous. */
    static final Piece[][] BOARD3 = {
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP,  BP,  WP,  BP,  WP, EMP, EMP, EMP },
        { EMP,  WP,  BP,  WP,  WP, EMP, EMP, EMP },
        { EMP, EMP,  BP,  BP,  WP,  WP,  WP, EMP },
        { EMP,  WP,  WP,  WP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
    };

    static final Piece[][] BOARD4 = {
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP,  BP, EMP, EMP, EMP },
            { EMP,  BP,  WP, EMP,  WP,  BP, EMP, EMP },
            { EMP,  WP,  BP, EMP,  WP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP,  WP, EMP },
            { EMP,  WP, EMP,  WP, EMP, EMP, EMP, EMP },
            { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
    };


    static final String BOARD1_STRING =
        "===\n"
        + "    - b b b - b b - \n"
        + "    - - - - - - - - \n"
        + "    w - - - b - - w \n"
        + "    w - w w - w - - \n"
        + "    w - b - - w - - \n"
        + "    w - - - b b - w \n"
        + "    w - - - - - - w \n"
        + "    - b - b b - - - \n"
        + "Next move: black\n"
        + "===";

    /** Test display */
    @Test
    public void toStringTest() {
        assertEquals(BOARD1_STRING, new Board(BOARD1, BP).toString());
    }

    /** Test legal moves. */
    @Test
    public void testLegality1() {
        Board b = new Board(BOARD1, BP);
        assertTrue("f3-d5", b.isLegal(mv("f3-d5")));
        assertTrue("f3-h5", b.isLegal(mv("f3-h5")));
        assertTrue("f3-h1", b.isLegal(mv("f3-h1")));
        assertTrue("f3-b3", b.isLegal(mv("f3-b3")));
        assertFalse("f3-d1", b.isLegal(mv("f3-d1")));
        assertFalse("f3-h3", b.isLegal(mv("f3-h3")));
        assertFalse("f3-e4", b.isLegal(mv("f3-e4")));
        assertFalse("c4-c7", b.isLegal(mv("c4-c7")));
        assertFalse("b1-b4", b.isLegal(mv("b1-b4")));
    }

    @Test
    public void testIsLegal() {
        Board b = new Board(BOARD1, WP);
        assertFalse("d5-g8", b.isLegal(mv("d5-g8")));
        assertTrue("f4-c4", b.isLegal(mv("f4-c4")));
        assertTrue("h6-e6", b.isLegal(mv("h6-e6")));
    }

    /** Test contiguity. */
    @Test
    public void testContiguous1() {
        Board b1 = new Board(BOARD1, BP);
        assertFalse("Board 1 black contiguous?", b1.piecesContiguous(BP));
        assertFalse("Board 1 white contiguous?", b1.piecesContiguous(WP));
        assertFalse("Board 1 game over?", b1.gameOver());
        Board b2 = new Board(BOARD2, BP);
        assertTrue("Board 2 black contiguous?", b2.piecesContiguous(BP));
        assertFalse("Board 2 white contiguous?", b2.piecesContiguous(WP));
        assertTrue("Board 2 game over", b2.gameOver());
        Board b3 = new Board(BOARD3, BP);
        assertTrue("Board 3 white contiguous?", b3.piecesContiguous(WP));
        assertTrue("Board 3 black contiguous?", b3.piecesContiguous(WP));
        assertTrue("Board 3 game over", b2.gameOver());
    }

    @Test
    public void testCountContiguous() {
        Board b1 = new Board(BOARD1, BP);
        ArrayList<Integer> black1
                = new ArrayList<>(Arrays.asList(3, 2, 2, 2, 1, 1, 1));
        assertEquals(black1, b1.getRegionSizes(BP));
        assertEquals(Arrays.asList(5, 2, 2, 2, 1), b1.getRegionSizes(WP));
        assertEquals(Arrays.asList(3, 2, 2, 2, 1, 1, 1), b1.getRegionSizes(BP));
        Board b2 = new Board(BOARD2, BP);
        assertEquals(Arrays.asList(9), b2.getRegionSizes(BP));
        assertEquals(Arrays.asList(6, 2, 1), b2.getRegionSizes(WP));
        Board b3 = new Board(BOARD3, BP);
        assertEquals(Arrays.asList(5), b3.getRegionSizes(BP));
        assertEquals(Arrays.asList(11), b3.getRegionSizes(WP));
        Board b4 = new Board(BOARD4, BP);
        assertEquals(Arrays.asList(2, 2), b4.getRegionSizes(BP));
        assertEquals(Arrays.asList(2, 2, 1, 1, 1), b4.getRegionSizes(WP));
    }

    @Test
    public void testEquals1() {
        Board b1 = new Board(BOARD1, BP);
        Board b2 = new Board(BOARD1, BP);

        assertEquals("Board 1 equals Board 1", b1, b2);
    }

    @Test
    public void testMove1() {
        Board b0 = new Board(BOARD1, BP);
        Board b1 = new Board(BOARD1, BP);
        assertEquals(BP, b1.turn());
        b1.makeMove(mv("f3-d5"));
        assertEquals("square d5 after f3-d5", BP, b1.get(sq(3, 4)));
        assertEquals("square f3 after f3-d5", EMP, b1.get(sq(5, 2)));
        assertEquals("Check move count for board 1 after one move",
                     1, b1.movesMade());
        assertEquals(WP, b1.turn());
        b1.retract();
        assertEquals("Check for board 1 restored after retraction", b0, b1);
        assertEquals("Check move count for board 1 after move + retraction",
                     0, b1.movesMade());
        assertEquals(BP, b1.turn());
    }

    /*@Test
    public void testLegalMoves() {
        Board b0 = new Board(BOARD1, WP);
        assertEquals(Arrays.asList(5), b0.legalMoves());
    }*/

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(BoardTest.class));
    }

}
