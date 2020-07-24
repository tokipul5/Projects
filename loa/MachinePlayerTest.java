/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import org.junit.Test;

import java.util.List;

import static loa.Piece.*;

/** Test of MachinePlayer
 *  @author Keeyou Kim
 */

public class MachinePlayerTest {
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
    @Test
    public void testBoardHeuristic() {
        Game game = null;
        Board b1 = new Board(BOARD1, WP);
        MachinePlayer mp = new MachinePlayer(WP, game);
        List<Board> temp = mp.getBoardHeuristic(b1);
        for (int i = 0; i < temp.size(); i++) {
            System.out.print(temp.get(i).getHeuristic() + " ");
        }
        System.out.println();

        int result = mp.findMove(b1,
                3, true, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println(result);
        System.out.println("WP " + b1.getTotalDistanceFromRegion(WP));
        System.out.println("BP " + b1.getTotalDistanceFromRegion(BP));

        Board b3 = new Board(BOARD3, BP);
        System.out.println(b3.legalMoves());
        MachinePlayer mp2 = new MachinePlayer(BP, game);
        temp = mp2.getBoardHeuristic(b3);
        for (int i = 0; i < temp.size(); i++) {
            System.out.print(temp.get(i).getHeuristic() + " ");
        }
        result = mp2.findMove(b3,
                3, true, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println();
        System.out.println(result);
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MachinePlayerTest.class));
    }
}
