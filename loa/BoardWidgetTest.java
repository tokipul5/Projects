package loa;

import org.junit.Test;

import static loa.Piece.*;

public class BoardWidgetTest {
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
    public void test() {
        Square sq = Square.sq(0, 1);
    }
}
