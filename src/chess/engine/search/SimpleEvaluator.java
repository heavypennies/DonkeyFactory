/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Piece;
import chess.engine.model.Square;
import chess.engine.utils.MoveGeneration;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public class SimpleEvaluator implements BoardEvaluator
{
  private static boolean DEBUG = false;

  // ...
  private static int CENTER_CHECK_BALANCE = 12;

  // Material Values
  private static double MATERIAL_DIVISOR = 31D;
  private static int TRADE_WHEN_LOSING_VALUE = 60;
  private static int TRADE_WHEN_UP_PAWNS_VALUE = 30;

  // Opening values
  private static int DEVELOPMENT_VALUE = 6;
  private static int PIECE_DOUBLE_MOVE_VALUE = 2;
  private static int QUEEN_TOO_EARLY_VALUE = 16;

  // Pawn values
  private static int[] WHITE_PASSED_PAWN_VALUES = new int[]{0,  7, 14, 40, 90, 140, 260, 650};
  private static int[] BLACK_PASSED_PAWN_VALUES = new int[]{650, 260, 140, 90, 40, 14,  7, 0};

  private static int PAWN_DOUBLED_VALUE = 36;
  private static int[] PAWN_BACKWARDS_VALUE = new int[]{ 15, 22, 25, 29, 29, 25, 22, 15};
  private static int UNMOVED_CENTER_PAWN = 3;
  private static int UNMOVED_CENTER_PAWN_BLOCKED = 35;
  private static int RUNAWAY_PAWN = 100;
  private static int UNMOVED_CENTER_PAWN_ALMOST_BLOCKED = 8;
  private static int PAWN_LOCKED_VALUE = 9;
  private static int PASSED_PAWN_WIDE_FILE_VALUE = 18;

  private static int PAWN_PHALANX_VALUE = 4;
  private static int[] PAWN_CHAIN_VALUE = {2, 3, 4, 6, 6, 4, 3, 2};

  // Knight Values
  private static int KNIGHT_TRAPPED_VALUE = 50;
  private static int KNIGHT_OUTPOST_VALUE = 14;

  // Bishop Values
  private static int TWO_BISHOPS_VALUE = 0;
  private static int BISHOP_TRAPPED_VALUE = 7;
  private static int BISHOP_OPEN_DIAGONAL_VALUE = 9;

  // Rook Values
  private static int ROOK_ON_OPEN_FILE = 18;
  private static int ROOK_ON_HALF_OPEN_FILE = 11;
  private static int TRAPPED_ROOK_VALUE = 240;
  private static int ROOK_OPPOSITE_KING_VALUE = 4;
  private static int ROOK_OPPOSITE_KING_HALF_OPEN_VALUE = 12;
  private static int ROOK_OPPOSITE_KING_OPEN_VALUE = 100;

  // King Values
  private static int KING_NO_CASTLE_VALUE = 12;
  private static int KING_FORFEIT_CASTLE_VALUE = 25;
  private static int KING_ON_OPEN_FILE = 91;
  private static int KING_ON_HALF_OPEN_FILE = 49;
  private static int WAITING_TO_CASTLE_VALUE = 8;
  private static int KING_OPEN_FILE = 23;
  private static int KING_HALF_OPEN_FILE = 16;
  private static int CASTLE_DESTINATION_VALUE = 12;

  private static int CHECK_IMMEDIATE_KING_VALUE = 3;
  private static int CHECK_NEARBY_KING_VALUE = 3;

  // Piece masks
  private static int PAWN_MASK = 8;
  private static int ROOK_MASK = 16;
  private static int MINOR_MASK = 32;
  private static int QUEEN_MASK = 64;
  private static int KING_MASK = 128;

  private MoveGeneration moveGeneration;

  public PawnHashtable pawnHash = new PawnHashtable();
  public int whiteKingScore;
  public int blackKingScore;

  public SimpleEvaluator(MoveGeneration moveGeneration)
  {
    this.moveGeneration = moveGeneration;
    for (int square = 0; square < 64; square++)
    {

      for(int color = 0;color < 2;color++)
      {
        KING_PAWN_AREA[color][square] = getPawnKingArea(Board.SQUARES[square], color);
        KING_TINY_AREA[color][square] = getTinyKingArea(Board.SQUARES[square], color);
        KING_STAGING_AREA[color][square] = getStagingKingArea(Board.SQUARES[square], color);
      }
      KING_AREA_A1_H8[square] = getKingAreaA1H8(Board.SQUARES[square]);
      KING_AREA_H1_A8[square] = getKingAreaH1A8(Board.SQUARES[square]);
    }
    // ABC vs ABC
    // ABC vs AB
    // ABC vs A C
    // ABC vs  BC
    // ABC vs A
    // ABC vs  B
    // ABC vs   C
    // ABC vs
    // etc
    for(int blackPawns = 0; blackPawns < 8; blackPawns++)
    {
      for(int whitePawns = 0; whitePawns < 8; whitePawns++)
      {
        PAWNSCORES[(whitePawns << 3) | blackPawns] = scorePawnWing(whitePawns, blackPawns);
      }
    }
  }


  static int[][] PAWN_WING_SCORES = new int[8][8];
  static
  {
    // 1 1 1 - 7
    //       - 0 - +++
    PAWN_WING_SCORES[7][0] = 60;

    // 1 1 1 - 7
    //     0 - 1 - ++
    PAWN_WING_SCORES[7][1] = 40;

    // 1 1 1 - 7
    //   0   - 2 - ++
    PAWN_WING_SCORES[7][2] = 40;

    // 1 1 1 - 7
    //   0 0 - 3 - +
    PAWN_WING_SCORES[7][3] = 20;

    // 1 1 1 - 7
    // 0     - 4 - ++
    PAWN_WING_SCORES[7][4] = 40;

    // 1 1 1 - 7
    // 0   0 - 5 - =
    PAWN_WING_SCORES[7][5] = 10;

    // 1 1 1 - 7
    // 0 0   - 6 - +
    PAWN_WING_SCORES[7][6] = 20;

    // 1 1 1 - 7
    // 0 0 0 - 7 - =
    PAWN_WING_SCORES[7][7] = 0;

    // 1 1   - 6
    //       - 0 - ++
    PAWN_WING_SCORES[6][0] = 40;

    // 1 1   - 6
    //     0 - 1 - +
    PAWN_WING_SCORES[6][1] = 30;

    // 1 1   - 6
    //   0   - 2 - +
    PAWN_WING_SCORES[6][2] = 20;

    // 1 1   - 6
    //   0 0 - 3 - =
    PAWN_WING_SCORES[6][3] = 0;

    // 1 1   - 6
    // 0     - 4 - +
    PAWN_WING_SCORES[6][4] = 20;

    // 1 1   - 6
    // 0   0 - 5 - =
    PAWN_WING_SCORES[6][5] = 0;

    // 1 1   - 6
    // 0 0   - 6 - =
    PAWN_WING_SCORES[6][6] = 0;

    // 1 1   - 6
    // 0 0 0 - 7 - -
    PAWN_WING_SCORES[6][7] = -20;

    // 1   1 - 5
    //       - 0 - ++
    PAWN_WING_SCORES[5][0] = 40;

    // 1   1 - 5
    //     0 - 1 - +
    PAWN_WING_SCORES[5][1] = 30;

    // 1   1 - 5
    //   0   - 2 - +
    PAWN_WING_SCORES[5][2] = 10;

    // 1   1 - 5
    //   0 0 - 3 - =
    PAWN_WING_SCORES[5][3] = 0;

    // 1   1 - 5
    // 0     - 4 - +
    PAWN_WING_SCORES[5][4] = 20;

    // 1   1 - 5
    // 0   0 - 5 - =
    PAWN_WING_SCORES[5][5] = 0;

    // 1   1 - 5
    // 0 0   - 6 - =
    PAWN_WING_SCORES[5][6] = 0;

    // 1   1 - 5
    // 0 0 0 - 7 - =
    PAWN_WING_SCORES[5][7] = -10;


    // 1     - 4
    //       - 0 - +
    PAWN_WING_SCORES[4][0] = 20;

    // 1     - 4
    //     0 - 1 - =
    PAWN_WING_SCORES[4][1] = 0;

    // 1     - 4
    //   0   - 2 - =
    PAWN_WING_SCORES[4][2] = 0;

    // 1     - 4
    //   0 0 - 3 - -
    PAWN_WING_SCORES[4][3] = -20;

    // 1     - 4
    // 0     - 4 - =
    PAWN_WING_SCORES[4][4] = 0;

    // 1     - 4
    // 0   0 - 5 - -
    PAWN_WING_SCORES[4][5] = -20;

    // 1     - 4
    // 0 0   - 6 - -
    PAWN_WING_SCORES[4][6] = -20;

    // 1     - 4
    // 0 0 0 - 7 - --
    PAWN_WING_SCORES[4][7] = -40;


    //   1 1 - 3
    //       - 0 - ++
    PAWN_WING_SCORES[3][0] = 40;

    //   1 1 - 3
    //     0 - 1 - +
    PAWN_WING_SCORES[3][1] = 20;

    //   1 1 - 3
    //   0   - 2 - +
    PAWN_WING_SCORES[3][2] = 20;

    //   1 1 - 3
    //   0 0 - 3 - =
    PAWN_WING_SCORES[3][3] = 0;

    //   1 1 - 3
    // 0     - 4 - =
    PAWN_WING_SCORES[3][4] = 0;

    //   1 1 - 3
    // 0   0 - 5 - =
    PAWN_WING_SCORES[3][5] = 0;

    //   1 1 - 3
    // 0 0   - 6 - =
    PAWN_WING_SCORES[3][6] = -10;

    //   1 1 - 3
    // 0 0 0 - 7 - -
    PAWN_WING_SCORES[3][7] = -20;


    //   1   - 2
    //       - 0 - +
    PAWN_WING_SCORES[2][0] = 20;

    //   1   - 2
    //     0 - 1 - =
    PAWN_WING_SCORES[2][1] = 0;

    //   1   - 2
    //   0   - 2 - =
    PAWN_WING_SCORES[2][2] = 0;

    //   1   - 2
    //   0 0 - 3 - -
    PAWN_WING_SCORES[2][3] = -20;

    //   1   - 2
    // 0     - 4 - =
    PAWN_WING_SCORES[2][4] = 0;

    //   1   - 2
    // 0   0 - 5 - -
    PAWN_WING_SCORES[2][5] = -20;

    //   1   - 2
    // 0 0   - 6 - -
    PAWN_WING_SCORES[2][6] = -30;

    //   1   - 2
    // 0 0 0 - 7 - --
    PAWN_WING_SCORES[2][7] = -40;

    //     1 - 1
    //       - 0 - +
    PAWN_WING_SCORES[2][0] = 20;

    //     1 - 1
    //     0 - 1 - =
    PAWN_WING_SCORES[1][1] = 0;

    //     1 - 1
    //   0   - 2 - =
    PAWN_WING_SCORES[1][2] = 0;

    //     1 - 1
    //   0 0 - 3 - -
    PAWN_WING_SCORES[1][3] = -20;

    //     1 - 1
    // 0     - 4 - =
    PAWN_WING_SCORES[1][4] = -10;

    //     1 - 1
    // 0   0 - 5 - -
    PAWN_WING_SCORES[1][5] = -20;

    //     1 - 1
    // 0 0   - 6 - -
    PAWN_WING_SCORES[1][6] = -20;

    //     1 - 1
    // 0 0 0 - 7 - --
    PAWN_WING_SCORES[1][7] = -40;


    //       - 0
    //       - 0 - =
    PAWN_WING_SCORES[0][0] = 0;

    //       - 0
    //     0 - 1 - -
    PAWN_WING_SCORES[0][1] = -20;

    //       - 0
    //   0   - 2 - -
    PAWN_WING_SCORES[0][2] = -20;

    //       - 0
    //   0 0 - 3 - --
    PAWN_WING_SCORES[0][3] = -40;

    //       - 0
    // 0     - 4 - -
    PAWN_WING_SCORES[0][4] = -20;

    //       - 0
    // 0   0 - 5 - --
    PAWN_WING_SCORES[0][5] = -40;

    //       - 0
    // 0 0   - 6 - --
    PAWN_WING_SCORES[0][6] = -40;

    //       - 0
    // 0 0 0 - 7 - ---
    PAWN_WING_SCORES[0][7] = -60;
  }
  int scorePawnWing(int whitePawns, int blackPawns)
  {
    return PAWN_WING_SCORES[whitePawns][blackPawns] >> 1;

    //return new PawnWingScorer(moveGeneration, this).scorePawnWing(whitePawns, blackPawns);
  }

  public void reset()
  {
    //pawnHash.clear();
  }

  static long DARK_SQUARES;
  static long LIGHT_SQUARES;
  static long[] FILES = new long[8];
  static long[] RANKS = new long[8];

  static long[] WHITE_PASSED_MASK = new long[64];
  static long[] BLACK_PASSED_MASK = new long[64];

  static long[] WHITE_BACKWARDS_MASK = new long[64];
  static long[] BLACK_BACKWARDS_MASK = new long[64];

  static long[] WHITE_ISO_MASK = new long[64];
  static long[] BLACK_ISO_MASK = new long[64];

  static long[] WHITE_RUNAWAY_PAWN_MASK = new long[64];
  static long[] BLACK_RUNAWAY_PAWN_MASK = new long[64];

  private static long[] WHITE_KNIGHT_OUTPOST_MASK = new long[64];
  private static long[] BLACK_KNIGHT_OUTPOST_MASK = new long[64];

  static long[][]  KING_PAWN_AREA = new long[2][64];
  static long[][] KING_STAGING_AREA = new long[2][64];
  static long[][] KING_TINY_AREA = new long[2][64];
  static long[] KING_AREA_A1_H8 = new long[64];
  static long[] KING_AREA_H1_A8 = new long[64];

  static long[] WHITE_HALF = new long[64];
  static long[] BLACK_HALF = new long[64];

  static
  {
    for (int square = 0; square < 64; square++)
    {
      if (square % 2 == 0)
      {
        DARK_SQUARES |= Board.SQUARES[square].mask_on;
      }
      else
      {
        LIGHT_SQUARES |= Board.SQUARES[square].mask_on;
      }

      FILES[Board.SQUARES[square].file] |= Board.SQUARES[square].mask_on;
      RANKS[Board.SQUARES[square].rank] |= Board.SQUARES[square].mask_on;
    }

    for (int square = 0; square < 64; square++)
    {
      // Initialize white passed pawn masks
      for (int rank = Board.SQUARES[square].rank + 1; rank < 7; rank++)
      {
        if (Board.SQUARES[square].file == 0)
        {
          WHITE_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank)].mask_on;
          WHITE_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank) + 1].mask_on;
        }
        else if (Board.SQUARES[square].file == 7)
        {
          WHITE_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank) - 1].mask_on;
          WHITE_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank)].mask_on;
        }
        else
        {
          WHITE_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank) - 1].mask_on;
          WHITE_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank)].mask_on;
          WHITE_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank) + 1].mask_on;
        }
        
      }

      // Initialize black passed pawn masks
      for (int rank = 0; rank < Board.SQUARES[square].rank; rank++)
      {
        if (Board.SQUARES[square].file == 0)
        {
          BLACK_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank)].mask_on;
          BLACK_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank) + 1].mask_on;
        }
        else if (Board.SQUARES[square].file == 7)
        {
          BLACK_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank) - 1].mask_on;
          BLACK_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank)].mask_on;
        }
        else
        {
          BLACK_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank) - 1].mask_on;
          BLACK_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank)].mask_on;
          BLACK_PASSED_MASK[square] |= Board.SQUARES[Board.SQUARES[square].file + (8 * rank) + 1].mask_on;
        }
      }

      for(int file = Math.max(Board.SQUARES[square].file - (7-Board.SQUARES[square].rank) , 0); file < Math.min(Board.SQUARES[square].file + (7-Board.SQUARES[square].rank), 7);file++)
      {
        for(int rank = Board.SQUARES[square].rank; rank < 7;rank++)
        {
          WHITE_RUNAWAY_PAWN_MASK[square] |= Board.SQUARES[file + (8 * rank)].mask_on;
        }
      }

      for(int file = Math.max(Board.SQUARES[square].file - (7-Board.SQUARES[square].rank ), 0); file < Math.min(Board.SQUARES[square].file + (7-Board.SQUARES[square].rank), 7);file++)
      {
        for(int rank = Board.SQUARES[square].rank; rank > 0;rank--)
        {
          BLACK_RUNAWAY_PAWN_MASK[square] |= Board.SQUARES[file + (8 * rank)].mask_on;
        }
      }


      WHITE_ISO_MASK[square] = BLACK_PASSED_MASK[square];
      if(Board.SQUARES[square].file > 0)
      {
        WHITE_ISO_MASK[square] |= Board.SQUARES[square - 1].mask_on;
      }
      if(Board.SQUARES[square].file < 7)
      {
        WHITE_ISO_MASK[square] |= Board.SQUARES[square + 1].mask_on;
      }
      
      BLACK_ISO_MASK[square] = WHITE_PASSED_MASK[square];
      if(Board.SQUARES[square].file > 0)
      {
        BLACK_ISO_MASK[square] |= Board.SQUARES[square - 1].mask_on;
      }
      if(Board.SQUARES[square].file < 7)
      {
        BLACK_ISO_MASK[square] |= Board.SQUARES[square + 1].mask_on;
      }

      for (int rank = Board.SQUARES[square].rank + 4; rank < 7; rank++)
      {
        BLACK_ISO_MASK[square] &= ~RANKS[rank];
      }

      for (int rank = Board.SQUARES[square].rank - 4; rank > 0; rank--)
      {
        WHITE_ISO_MASK[square] &= ~RANKS[rank];
      }

      WHITE_BACKWARDS_MASK[square] = BLACK_PASSED_MASK[square];
      BLACK_BACKWARDS_MASK[square] = WHITE_PASSED_MASK[square];

      if (Board.SQUARES[square].file > 0)
      {
        WHITE_BACKWARDS_MASK[square] |= Board.SQUARES[square - 1].mask_on;
        BLACK_BACKWARDS_MASK[square] |= Board.SQUARES[square - 1].mask_on;
      }
      if (Board.SQUARES[square].file < 7)
      {
        WHITE_BACKWARDS_MASK[square] |= Board.SQUARES[square + 1].mask_on;
        BLACK_BACKWARDS_MASK[square] |= Board.SQUARES[square + 1].mask_on;
      }

      WHITE_KNIGHT_OUTPOST_MASK[square] = WHITE_PASSED_MASK[square] & ~FILES[Board.file(square)];
      BLACK_KNIGHT_OUTPOST_MASK[square] = BLACK_PASSED_MASK[square] & ~FILES[Board.file(square)];

      for (int i = 0; i <= Board.SQUARES[square].rank; i++)
      {
        WHITE_HALF[square] |= RANKS[i];
      }
      for (int i = 7; i >= Board.SQUARES[square].rank; i--)
      {
        BLACK_HALF[square] |= RANKS[i];
      }
    }

  }

  static long FILES_ABCD = FILES[0] | FILES[1] | FILES[2] | FILES[3];
  static long FILES_EFGH = FILES[4] | FILES[5] | FILES[6] | FILES[7];

  static long FILES_AB = FILES[0] | FILES[1];
  static long FILES_CD = FILES[2] | FILES[3];
  static long FILES_EF = FILES[4] | FILES[6];
  static long FILES_GH = FILES[6] | FILES[7];

  static long FILES_DE = FILES[3] | FILES[4];

  static long CENTER = (FILES_CD | FILES_EF) & (RANKS[3] | RANKS[4]);

  static int[] PAWNSCORES = new int[64];


  static int[] CHECK_VALUE_TABLE_WHITE_O_O = new int[]{
          3, 3, 3, 5, 5, 7, 9, 9,
          2, 4, 4, 5, 5, 6, 7, 7,
          2, 2, 2, 4, 4, 5, 5, 5,
          1, 1, 2, 3, 4, 4, 4, 3,
  };
  static int[] CHECK_VALUE_TABLE_BLACK_O_O = new int[]{
          1, 1, 2, 3, 4, 4, 4, 3,
          2, 2, 2, 4, 4, 5, 5, 5,
          2, 4, 4, 5, 5, 6, 7, 7,
          3, 3, 3, 5, 5, 7, 9, 9,
  };
  static int[] CHECK_VALUE_TABLE_WHITE_O_O_O = new int[]{
          9, 9, 7, 5, 5, 3, 3, 3,
          7, 7, 6, 5, 5, 4, 4, 2,
          5, 5, 5, 4, 4, 2, 2, 2,
          3, 4, 4, 4, 3, 2, 1, 1,
  };
  static int[] CHECK_VALUE_TABLE_BLACK_O_O_O = new int[]{
          3, 4, 4, 4, 3, 2, 1, 1,
          5, 5, 5, 4, 4, 2, 2, 2,
          7, 7, 6, 5, 5, 4, 4, 2,
          9, 9, 7, 5, 5, 3, 3, 3,
  };
  static int[] CHECK_VALUE_TABLE_WHITE_CENTER = new int[]{
          5, 9, 9, 9, 9, 9, 9, 5,
          2, 2, 5, 5, 5, 5, 2, 2,
          1, 1, 3, 4, 4, 3, 1, 1,
          1, 2, 3, 4, 4, 3, 2, 1,
  };

  static int[] CHECK_VALUE_TABLE_BLACK_CENTER = new int[]{
          1, 2, 3, 4, 4, 3, 2, 1,
          1, 1, 3, 4, 4, 3, 1, 1,
          2, 2, 5, 5, 5, 5, 2, 2,
          5, 7, 9, 9, 9, 9, 7, 5,
  };

  static long mask2 = Square.A1.mask_on | Square.B1.mask_on;
  static long mask3 = Square.A1.mask_on | Square.B1.mask_on | Square.C1.mask_on;

  static long mask3x2 = Square.A1.mask_on | Square.B1.mask_on | Square.C1.mask_on |
                              Square.A2.mask_on | Square.B2.mask_on | Square.C2.mask_on;


  static
  {
    // White Pawn
    PIECE_VALUE_TABLES[1][Piece.PAWN] = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            1, 1, 4, 5, 5, 2, 1, 1,
            2, 2, 15, 18, 19,  7, 2, 2,
            5, 6, 7, 20, 21, 12, 7, 6,
            8, 12, 12, 24, 25, 14, 12, 8,
            10, 22, 32, 50, 51, 32, 22, 10,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    // Black Pawn
    PIECE_VALUE_TABLES[0][Piece.PAWN] = new int[]{
            0, 0, 0, 0, 0, 0, 0, 0,
            10, 22, 32, 50, 51, 32, 22, 10,
            8, 12, 12, 24, 25, 24, 12, 8,
            5, 6,  7, 20, 21, 7, 7, 6,
            2, 2, 15, 18, 19, 6, 2, 2,
            1, 1, 4, 5, 5, 2, 1, 1,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    // White knight
    PIECE_VALUE_TABLES[1][Piece.KNIGHT] = new int[]{
            -10, -3, -7, -5, -5, -7, -3, -10,
            -8, 1, 3, 4, 4, 3, 1, -8,
            -7, 8, 8, 10, 10, 9, 8, -7,
            -6, 5,  9, 14, 14,  9, 5, -6,
            -6, 6, 16, 15, 15, 16, 6, -6,
            -6, 5, 10, 12, 16, 15, 5, -6,
            1, 2, 9, 9, 9, 9, 2, 1,
            -10, -8, -7, -5, -5, -7, -8, -10,
    };
    // Black knight
    PIECE_VALUE_TABLES[0][Piece.KNIGHT] = new int[]{
            -10, -8, -7, -5, -5, -7, -8, -10,
            1, 2, 9, 9, 9, 9, 2, 1,
            -6, 5, 10, 12, 16, 15, 5, -6,
            -6, 6, 13, 15, 15, 16, 6, -6,
            -6, 5, 9, 14, 14,  9, 5, -6,
            -7, 8, 8, 10, 10, 9, 8, -7,
            -8, 1, 3, 4, 4, 3, 1, -8,
            -10, -3, -7, -5, -5, -7, -3, -10,
    };

    // White bishop
    PIECE_VALUE_TABLES[1][Piece.BISHOP] = new int[]{
            -4, -3, -2, -1, -1, -2, -3, -4,
            -5, 6, 1, 7, 7, 1, 6, -5,
            0, 5, 7, 8, 8, 7, 5, 0,
            0, 1, 13, 18, 18, 13, 1, 0,
            0, 8, 15, 18, 18, 15, 8, 0,
            0, 1, 9, 10, 10, 9, 1, 0,
            0, 2, 2, 3, 3, 2, 2, 0,
            -4, -3, -2, -1, -1, -2, -3, -4,
    };
    // Black bishop
    PIECE_VALUE_TABLES[0][Piece.BISHOP] = new int[]{
            -4, -3, -2, -1, -1, -2, -3, -4,
            0, 2, 2, 3, 3, 2, 2, 0,
            0, 1, 9, 10, 10, 9, 1, 0,
            0, 8, 15, 18, 18, 15, 8, 0,
            0, 1, 13, 18, 18, 13, 1, 0,
            0, 5, 7, 8, 8, 7, 5, 0,
            -5, 6, 1, 7, 7, 1, 6, -5,
            -4, -3, -2, -1, -1, -2, -3, -4,
    };

    // White rook
    PIECE_VALUE_TABLES[1][Piece.ROOK] = new int[]{
            -4, -5, 1, 6, 6, 3, -5, -4,
            -5, 0, 1, 6, 6, 1, 0, -5,
            -3, 1, 2, 7, 7, 2, 1, -3,
            -3, 2, 3, 8, 8, 3, 2, -3,
            -3, 3, 4, 9, 9, 4, 3, -3,
            -2, 3, 5, 10, 10, 5, 3, -2,
            12, 18, 22, 25, 25, 22, 18, 12,
            6, 6, 6, 6, 6, 6, 6, 6,
    };
    // Black rook
    PIECE_VALUE_TABLES[0][Piece.ROOK] = new int[]{
            6, 6, 6, 6, 6, 6, 6, 6,
            12, 18, 22, 25, 25, 22, 18, 12,
            -2, 3, 5, 10, 10, 5, 3, -2,
            -3, 3, 4, 9, 9, 4, 3, -3,
            -3, 2, 3, 8, 8, 3, 2, -3,
            -3, 1, 2, 7, 7, 2, 1, -3,
            -5, 0, 1, 6, 6, 1, 0, -5,
            -4, -5, 1, 6, 6, 3, -5, -4,
    };
    // White Queen
    PIECE_VALUE_TABLES[1][Piece.QUEEN] = new int[]{
            -12, -12, -8, 4, 4, -8, -12, -12,
            -6, -5, 1, 5, 5, 1, -5, -6,
            0, 1, 7, 11, 11, 7, 1, 0,
            0, 1, 8, 13, 13, 8, 1, 0,
            0, 1, 5, 11, 11, 5, 1, 0,
            0, 1, 5, 10, 10, 5, 1, 0,
            7, 8, 9, 11, 11, 9, 8, 7,
            5, 6, 6, 8, 8, 6, 6, 5,
    };
    // Black Queen
    PIECE_VALUE_TABLES[0][Piece.QUEEN] = new int[]{
            5, 6, 6, 8, 8, 6, 6, 5,
            7, 8, 9, 11, 11, 9, 8, 7,
            0, 1, 5, 10, 10, 5, 1, 0,
            0, 1, 5, 11, 11, 5, 1, 0,
            0, 1, 8, 13, 13, 8, 1, 0,
            0, 1, 7, 11, 11, 7, 1, 0,
            -6, -5, 1, 5, 5, 1, -5, -6,
            -12, -12, -8, 4, 4, -8, -12, -12,
    };
    // King
    PIECE_VALUE_TABLES[0][Piece.KING] = new int[]{
            2, 1, 1, 3, 1, 3, 1, 2,
            2, 2, 2, 3, 3, 3, 3, 2,
            3, 3, 4, 4, 4, 4, 3, 3,
            3, 4, 5, 6, 6, 5, 4, 3,
            3, 4, 5, 6, 6, 5, 4, 3,
            3, 3, 4, 4, 4, 4, 3, 3,
            2, 3, 2, 3, 3, 3, 3, 2,
            2, 1, 1, 3, 1, 3, 1, 2,
    };

  }

  public static class PawnFlags
  {
    public long whitePassedPawns;
    public long blackPassedPawns;
    public long openFiles;
    public long openRanks;
    public long lockedFiles;
    public int score;
    public int whitePawnCount;
    public int blackPawnCount;
    public long[] closedFiles;
    public int endgameScore;
    public int centerScore;

    public PawnFlags()
    {
      closedFiles = new long[]{0L, 0L};
      reset();
    }

    public void reset()
    {
      whitePassedPawns = 0;
      blackPassedPawns = 0;
      openFiles = ~0L;
      openRanks = ~0L;
      lockedFiles = 0;
      score = 0;
      whitePawnCount = 0;
      blackPawnCount = 0;
      closedFiles[0] = 0;
      closedFiles[1] = 0;

      endgameScore = 0;
      centerScore = 0;
    }
  }

  long[] pawnAttackers = new long[2];
  long[] tinyAttackers = new long[2]; 
  long[] stagingAttackers = new long[2]; 

  public int scorePosition(Board board, int alpha, int beta)
  {
//    moveGeneration.generateChecks(board);
    //if(DEBUG)System.err.println(board.toString());

    int score = 0;

    int whiteMaterial = 0;
    int blackMaterial = 0;

    score += board.materialScore;

    // pawn position (with hash)
    PawnFlags pawnFlags = scorePawns(board);

    int pieceValueScore = board.positionScore;

    // Count Undeveloped Pieces
    int undevelopedWhitePieceCount = Board.countBits((RANKS[0] & (board.pieceBoards[1][Piece.KNIGHT] | board.pieceBoards[1][Piece.BISHOP])) | (Square.E1.mask_on & board.pieceBoards[1][Piece.KING]));
    int undevelopedBlackPieceCount = Board.countBits((RANKS[7] & (board.pieceBoards[0][Piece.KNIGHT] | board.pieceBoards[0][Piece.BISHOP])) | (Square.E8.mask_on & board.pieceBoards[0][Piece.KING]));

    // Eval Piece by piece
    int squareIndex;
    long pieces = board.pieceBoards[1][Piece.KNIGHT];
    while (pieces != 0)
    {
      squareIndex = Board.getLeastSignificantBit(pieces);
      Square square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;
      pieceValueScore -= PIECE_VALUE_TABLES[1][Piece.KNIGHT][squareIndex];
      whiteMaterial += 3;

      pieceValueScore -= (undevelopedWhitePieceCount == 0 ? 3 : 3 + board .boardSquares[square.index128].piece.moveCount) * board.boardSquares[square.index128].piece.moveCount;
      pieceValueScore += ((Board.countBits(MoveGeneration.attackVectors[1][Piece.KNIGHT][squareIndex] & ~board.pieceBoards[1][6]))) - 6;

      if ((WHITE_KNIGHT_OUTPOST_MASK[squareIndex] & board.pieceBoards[0][Piece.PAWN]) == 0 &&
          square.rank >= 4 &&
          square.file > 0 &&
          square.file < 7)
      {
        pieceValueScore += KNIGHT_OUTPOST_VALUE + (2 * (Board.rank(squareIndex) - 4));
      }
    }

    pieces = board.pieceBoards[0][Piece.KNIGHT];
    while (pieces != 0)
    {
      squareIndex = Board.getLeastSignificantBit(pieces);
      Square square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;
      pieceValueScore += PIECE_VALUE_TABLES[0][Piece.KNIGHT][squareIndex];
      blackMaterial += 3;

      pieceValueScore += (undevelopedBlackPieceCount == 0 ? 3 : 3 + board .boardSquares[square.index128].piece.moveCount) * board.boardSquares[square.index128].piece.moveCount;
      pieceValueScore -= ((Board.countBits(MoveGeneration.attackVectors[0][Piece.KNIGHT][squareIndex] & ~board.pieceBoards[0][6]))) - 6;

      if ((BLACK_KNIGHT_OUTPOST_MASK[squareIndex] & board.pieceBoards[1][Piece.PAWN]) == 0 &&
          square.rank <= 3 &&
          square.file > 0 &&
          square.file < 7)
      {
        pieceValueScore -= KNIGHT_OUTPOST_VALUE + (2 * (4-Board.rank(squareIndex)));
      }
    }

    pieces = board.pieceBoards[1][Piece.BISHOP];
    while (pieces != 0)
    {
      squareIndex = Board.getLeastSignificantBit(pieces);
      Square square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;
//      pieceValueScore -= PIECE_VALUE_TABLES[1][Piece.BISHOP][squareIndex];
      whiteMaterial += 3;

      if (pieces != 0)
      {
        pieceValueScore += TWO_BISHOPS_VALUE;
      }

/*
      if(((MoveGeneration.attackVectors[1][Piece.KING][square] & ~MoveGeneration.attackVectors[1][Piece.ROOK][square]) & ~board.pieceBoards[1][Board.ALL_PIECES]) == 0)
      {
        pieceValueScore -= BISHOP_TRAPPED_VALUE;
      }
*/
      pieceValueScore -= (undevelopedWhitePieceCount == 0 ? 3 : 3 + board .boardSquares[square.index128].piece.moveCount) * board.boardSquares[square.index128].piece.moveCount;
      pieceValueScore += ((board.bishopMobility(squareIndex)) << 1);
    }

    pieces = board.pieceBoards[0][Piece.BISHOP];
    while (pieces != 0)
    {
      squareIndex = Board.getLeastSignificantBit(pieces);
      Square square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;
//      pieceValueScore += PIECE_VALUE_TABLES[0][Piece.BISHOP][squareIndex];
      blackMaterial += 3;
      if (pieces != 0)
      {
        pieceValueScore -= TWO_BISHOPS_VALUE;
      }

/*
      if(((MoveGeneration.attackVectors[0][Piece.KING][square] & ~MoveGeneration.attackVectors[0][Piece.ROOK][square]) & ~board.pieceBoards[0][Board.ALL_PIECES]) == 0)
      {
        pieceValueScore += BISHOP_TRAPPED_VALUE;
      }
*/
      pieceValueScore += (undevelopedBlackPieceCount == 0 ? 3 : 3 + board .boardSquares[square.index128].piece.moveCount) * board.boardSquares[square.index128].piece.moveCount;
      pieceValueScore -= ((board.bishopMobility(squareIndex)) << 1);
    }

    pieces = board.pieceBoards[1][Piece.ROOK];
    while (pieces != 0)
    {
      squareIndex = Board.getLeastSignificantBit(pieces);
      Square square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;
      pieceValueScore += board.rookMobility(squareIndex) << 1;
      whiteMaterial += 5;

/*
      if (squareIndex == Square.A1.index64 && board.whiteKing.square.index64 == Square.B1.index64)
      {
        pieceValueScore -= TRAPPED_ROOK_VALUE;
      }
      if (squareIndex == Square.H1.index64 && board.blackKing.square.index64 == Square.G1.index64)
      {
        pieceValueScore -= TRAPPED_ROOK_VALUE;
      }
*/

      if (((square.mask_on & pawnFlags.openFiles) != 0))
      {
        pieceValueScore += ROOK_ON_OPEN_FILE;
      }
      else if (((square.mask_on & pawnFlags.closedFiles[1]) == 0))
      {
        pieceValueScore += ROOK_ON_HALF_OPEN_FILE;
      }
      else if (((square.mask_on & pawnFlags.closedFiles[1]) != 0))
      {
        pieceValueScore -= ROOK_ON_HALF_OPEN_FILE;
      }
      if (((square.mask_on & pawnFlags.openRanks) != 0))
      {
        pieceValueScore += ROOK_ON_HALF_OPEN_FILE;
      }
/*
      else if (((Board.SQUARES[square].mask_on & pawnFlags.lockedFiles) != 0))
      {
        pieceValueScore -= ROOK_ON_OPEN_FILE;
      }
*/
      pieceValueScore -= (undevelopedWhitePieceCount == 0 ? 3 : 3 + board .boardSquares[square.index128].piece.moveCount) * board.boardSquares[square.index128].piece.moveCount;
    }

    pieces = board.pieceBoards[0][Piece.ROOK];
    while (pieces != 0)
    {
      squareIndex = Board.getLeastSignificantBit(pieces);
      Square square = Board.SQUARES[squareIndex];
      pieces &= square.mask_off;
      pieceValueScore -= board.rookMobility(squareIndex) << 1;
      blackMaterial += 5;

/*
      if (squareIndex == Square.A8.index64 && board.blackKing.square.index64 == Square.B8.index64)
      {
        pieceValueScore += TRAPPED_ROOK_VALUE;
      }
      if (squareIndex == Square.H8.index64 && board.blackKing.square.index64 == Square.G8.index64)
      {
        pieceValueScore += TRAPPED_ROOK_VALUE;
      }
*/

      if (((square.mask_on & pawnFlags.openFiles) != 0))
      {
        pieceValueScore -= ROOK_ON_OPEN_FILE;
      }
      else if (((square.mask_on & pawnFlags.closedFiles[0]) == 0))
      {
        pieceValueScore -= ROOK_ON_HALF_OPEN_FILE;
      }
      else if (((square.mask_on & pawnFlags.closedFiles[0]) != 0))
      {
        pieceValueScore += ROOK_ON_HALF_OPEN_FILE;
      }
      if (((square.mask_on & pawnFlags.openRanks) != 0))
      {
        pieceValueScore -= ROOK_ON_HALF_OPEN_FILE;
      }

      pieceValueScore += (undevelopedBlackPieceCount == 0 ? 3 : 3 + board .boardSquares[square.index128].piece.moveCount) * board.boardSquares[square.index128].piece.moveCount;
    }

    pieces = board.pieceBoards[1][Piece.QUEEN];
    while (pieces != 0)
    {
      squareIndex = Board.getLeastSignificantBit(pieces);
      Square square = Board.SQUARES[squareIndex];
      pieceValueScore -= PIECE_VALUE_TABLES[1][Piece.QUEEN][squareIndex];
      pieces &= square.mask_off;
      whiteMaterial += 9;
      if(undevelopedWhitePieceCount == 0)
      {
        pieceValueScore += board.rookMobility(squareIndex);
        pieceValueScore += board.bishopMobility(squareIndex);
      }
      pieceValueScore -= (undevelopedWhitePieceCount == 0 ? 3 : 3 + board .boardSquares[square.index128].piece.moveCount) * board.boardSquares[square.index128].piece.moveCount;
    }

    pieces = board.pieceBoards[0][Piece.QUEEN];
    while (pieces != 0)
    {
      squareIndex = Board.getLeastSignificantBit(pieces);
      Square square = Board.SQUARES[squareIndex];
      pieceValueScore += PIECE_VALUE_TABLES[0][Piece.QUEEN][squareIndex];
      pieces &= square.mask_off;
      blackMaterial += 9;
      if(undevelopedBlackPieceCount == 0)
      {
        pieceValueScore -= board.rookMobility(squareIndex);
        pieceValueScore -= board.bishopMobility(squareIndex);
      }
      pieceValueScore += (undevelopedBlackPieceCount == 0 ? 3 : 3 + board .boardSquares[square.index128].piece.moveCount) * board.boardSquares[square.index128].piece.moveCount;
    }

    score += pieceValueScore;

    double whiteMaterialRatio = 1 - (whiteMaterial / 31D);
    double blackMaterialRatio = 1 - (blackMaterial / 31D);

    // Attack the center
/*
    score += pawnFlags.centerScore > 0 ? pawnFlags.centerScore * (1 - whiteMaterialRatio) : pawnFlags.centerScore * (1 - blackMaterialRatio);
*/

    // Prepare for the endgame
    score += pawnFlags.endgameScore > 0 ? pawnFlags.endgameScore * blackMaterialRatio : pawnFlags.endgameScore * whiteMaterialRatio;

    if(undevelopedBlackPieceCount + undevelopedWhitePieceCount > 0)
    {
      // Develop pieces
      score += (undevelopedBlackPieceCount - undevelopedWhitePieceCount) * DEVELOPMENT_VALUE;

      // Dont move the queen too much too early
      if (undevelopedWhitePieceCount > 0 && board.pieceBoards[1][Piece.QUEEN] != 0 && board.stats.whitePieceMoves[Piece.QUEEN] > 1)
      {
        score -= min(44, board.stats.whitePieceMoves[Piece.QUEEN] * QUEEN_TOO_EARLY_VALUE);
      }
      if (undevelopedBlackPieceCount > 0 && board.pieceBoards[0][Piece.QUEEN] != 0 && board.stats.blackPieceMoves[Piece.QUEEN] > 1)
      {
        score += min(44, board.stats.blackPieceMoves[Piece.QUEEN] * QUEEN_TOO_EARLY_VALUE);
      }

      if ((board.pieceBoards[1][Piece.PAWN] & Square.E2.mask_on) != 0)
      {
        score -= UNMOVED_CENTER_PAWN;
        if ((board.pieceBoards[1][Board.ALL_PIECES] & Square.E3.mask_on) != 0)
        {
          score -= UNMOVED_CENTER_PAWN_BLOCKED;
        }
      }
      if ((board.pieceBoards[1][Piece.PAWN] & Square.D2.mask_on) != 0)
      {
        score -= UNMOVED_CENTER_PAWN;
        if ((board.pieceBoards[1][Board.ALL_PIECES] & Square.D3.mask_on) != 0)
        {
          score -= UNMOVED_CENTER_PAWN_BLOCKED;
        }
      }
      if ((board.pieceBoards[0][Piece.PAWN] & Square.E7.mask_on) != 0)
      {
        score += UNMOVED_CENTER_PAWN;
        if ((board.pieceBoards[0][Board.ALL_PIECES] & Square.E6.mask_on) != 0)
        {
          score += UNMOVED_CENTER_PAWN_BLOCKED;
        }
      }
      if ((board.pieceBoards[0][Piece.PAWN] & Square.D7.mask_on) != 0)
      {
        score += UNMOVED_CENTER_PAWN;
        if ((board.pieceBoards[0][Board.ALL_PIECES] & Square.D6.mask_on) != 0)
        {
          score += UNMOVED_CENTER_PAWN_BLOCKED;
        }
      }
    }

    // Don't trade when down material
    if (board.stats.originalMaterialDifference < 0 && blackMaterial > whiteMaterial)
    {
      if (whiteMaterial + blackMaterial < board.stats.originalMaterial)
      {
        score -= TRADE_WHEN_LOSING_VALUE;
      }
    }
    else if (board.stats.originalMaterialDifference > 0 && whiteMaterial > blackMaterial)
    {
      if (whiteMaterial + blackMaterial < board.stats.originalMaterial)
      {
        score += TRADE_WHEN_LOSING_VALUE;
      }
    }

    // Pawns
    int pawnScore = pawnFlags.score;
    score += pawnScore;

    //if(DEBUG)System.err.println("Material Ratio: w: " + whiteMaterialRatio + " b: " + blackMaterialRatio);
    //if(DEBUG)System.err.println("Pawns: " + pawnScore);

    // Passed Pawns
    if ((pawnFlags.whitePassedPawns | pawnFlags.blackPassedPawns) != 0)
    {
      int passedPawnScore = scorePassedPawns(board,
                                             pawnFlags.whitePassedPawns,
                                             pawnFlags.blackPassedPawns,
                                             whiteMaterial,
                                             blackMaterial);
      score += passedPawnScore;
      //if(DEBUG)System.err.println("Passed Pawns: " + passedPawnScore);
    }

/*
    if((board.turn == 1 ? score : -score) < alpha - 500)
    {
      return board.turn == 1 ?
             score :
             -score;
    }
*/


    // King Evals
    int whiteKingScore = evalWhiteKing(board, pawnFlags, blackMaterial);
    int blackKingScore = evalBlackKing(board, pawnFlags, whiteMaterial);

    score += whiteKingScore;
    score -= blackKingScore;

    //if(DEBUG)System.err.println("Pieces: " + pieceValueScore);
    //if(DEBUG)System.err.println("White King: " + whiteKingScore);
    //if(DEBUG)System.err.println("Black King: " + blackKingScore);
    //if(DEBUG)System.err.println("Score: " + score);

/*
    evalWhiteKing(board, pawnFlags);
    evalBlackKing(board, pawnFlags);
*/

    if (whiteMaterial < 5 && pawnFlags.whitePawnCount == 0)
    {
      score = min(score, 0);
    }
    if (blackMaterial < 5 && pawnFlags.blackPawnCount == 0)
    {
      score = max(score, 0);
    }

    return board.turn == 1 ?
           score :
           -score;
  }


  //////////////
  //   PAWNS
  //////////////
  private PawnFlags scorePawns(Board board)
  {
    // probe pawn hash
    PawnHashtable.HashEntry pawnHashEntry = pawnHash.getEntryNoNull(board);

    // if the entry is good
    if (pawnHashEntry.hash == board.pawnHash)
    {
      // return the score from the entry
      return pawnHashEntry.pawnFlags;
    }

    int score = 0;

    pawnHashEntry.pawnFlags.whitePawnCount = 0;
    pawnHashEntry.pawnFlags.blackPawnCount = 0;

    pawnHashEntry.pawnFlags.whitePassedPawns = 0;
    pawnHashEntry.pawnFlags.blackPassedPawns = 0;
    pawnHashEntry.pawnFlags.openFiles = ~0;
    pawnHashEntry.pawnFlags.openRanks = ~0;
    pawnHashEntry.pawnFlags.lockedFiles = 0;
    pawnHashEntry.pawnFlags.closedFiles[0] = 0;
    pawnHashEntry.pawnFlags.closedFiles[1] = 0;
    pawnHashEntry.pawnFlags.endgameScore = 0;

    long whitePawns = board.pieceBoards[1][Piece.PAWN];
    long blackPawns = board.pieceBoards[0][Piece.PAWN];

    int whitePawnScore = 0;
    int blackPawnScore = 0;

    int[] whitePawnValueTable = PIECE_VALUE_TABLES[1][Piece.PAWN];
    Square pawnSquare;
    while (whitePawns != 0)
    {
      int pawnSquareIndex = Board.getLeastSignificantBit(whitePawns);
      pawnSquare = Board.SQUARES[pawnSquareIndex];
      whitePawns ^= pawnSquare.mask_on;

      //if(DEBUG)System.err.println("  P@" + pawnSquare.toString());
//      whitePawnScore += Piece.TYPE_VALUES[Piece.PAWN];
      pawnHashEntry.pawnFlags.whitePawnCount++;
      pawnHashEntry.pawnFlags.openFiles &= ~FILES[pawnSquare.file];
      pawnHashEntry.pawnFlags.openRanks &= ~RANKS[pawnSquare.rank];
//      whitePawnScore += whitePawnValueTable[pawnSquareIndex];
      pawnHashEntry.pawnFlags.closedFiles[1] |= FILES[pawnSquare.file] & WHITE_HALF[pawnSquare.index64];

      if (pawnSquare.file == 0)
      {
        // white pawn chain (H1-A8)
        if ((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 7].mask_on) != 0)
        {
          whitePawnScore += whitePawnValueTable[pawnSquareIndex] >> 2;
          pawnHashEntry.pawnFlags.closedFiles[0] |= FILES[pawnSquare.file] & BLACK_HALF[pawnSquare.index64];
          //if(DEBUG)System.err.println("    H1-A8 Chain");
        }
      }
      else if (pawnSquare.file == 7)
      {
        // white pawn phalanx
        if ((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 1].mask_on) != 0 && pawnSquare.rank > 1)
        {
          whitePawnScore += whitePawnValueTable[pawnSquareIndex] >> 1;

          // pawn phalanx can create passed pawn?
          // pawn phalanx is immediately challenged
          // pawn phalanx is immediately challenged
          // pawn phalanx is opposed
          // pawn phalanx is disconnected

          //if(DEBUG)System.err.println("    Phalanx");
        }
        // white pawn chain (A1-H8)
        if (((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 9].mask_on) != 0))
        {
          whitePawnScore += whitePawnValueTable[pawnSquareIndex] >> 2;
          pawnHashEntry.pawnFlags.closedFiles[0] |= FILES[pawnSquare.file] & BLACK_HALF[pawnSquare.index64];
          //if(DEBUG)System.err.println("    A1-H8 Chain");
        }
      }
      else
      {
        // white pawn phalanx
        if ((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 1].mask_on) != 0 && pawnSquare.rank > 1)
        {
          whitePawnScore += whitePawnValueTable[pawnSquareIndex] >> 1;
          //if(DEBUG)System.err.println("    Phalanx");
        }
        // white pawn chain (A1-H8)
        if (((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 9].mask_on) != 0))
        {
          whitePawnScore += whitePawnValueTable[pawnSquareIndex] >> 2;
          pawnHashEntry.pawnFlags.closedFiles[0] |= FILES[pawnSquare.file] & BLACK_HALF[pawnSquare.index64];
          //if(DEBUG)System.err.println("    A1-H8 Chain");
        }
        // white pawn chain (H1-A8)
        if (((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 7].mask_on) != 0))
        {
          whitePawnScore += whitePawnValueTable[pawnSquareIndex] >> 2;
          pawnHashEntry.pawnFlags.closedFiles[0] |= FILES[pawnSquare.file] & BLACK_HALF[pawnSquare.index64];
          //if(DEBUG)System.err.println("    H1-A8 Chain");
        }
      }

      // locked files
      if ((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquare.index64 + 8].mask_on) != 0)
      {
        pawnHashEntry.pawnFlags.lockedFiles |= FILES[pawnSquare.file];
        //if(DEBUG)System.err.println("    Locked File");
//        continue;
      }

      // white backward pawns
      // white isolated pawns
      if ((board.pieceBoards[1][Piece.PAWN] & WHITE_BACKWARDS_MASK[pawnSquare.index64]) == 0 ||
          (board.pieceBoards[1][Piece.PAWN] & WHITE_ISO_MASK[pawnSquare.index64]) == 0)
      {
        //if(DEBUG)System.err.println("    Double Backwards");
        whitePawnScore -= PAWN_BACKWARDS_VALUE[pawnSquare.file] >> 2;
        //if(DEBUG)System.err.println("    Backwards");
        if((board.pieceBoards[0][Piece.PAWN] & FILES[pawnSquare.file]) == 0)
        {
          //if(DEBUG)System.err.println("    Double Backwards");
          whitePawnScore -= PAWN_BACKWARDS_VALUE[pawnSquare.file];
        }
      }

      // doubled pawns
      if (((board.pieceBoards[1][Piece.PAWN] & BLACK_PASSED_MASK[pawnSquare.index64] & FILES[pawnSquare.file])) != 0)
      {
        whitePawnScore -= PAWN_DOUBLED_VALUE;
        //if(DEBUG)System.err.println("    Doubled Pawn");
      }

      // white passed pawns
      if ((board.pieceBoards[0][Piece.PAWN] & WHITE_PASSED_MASK[pawnSquare.index64]) == 0)
      {
        pawnHashEntry.pawnFlags.whitePassedPawns |= pawnSquare.mask_on;
        //if(DEBUG)System.err.println("    Passed");
      }
    }

    int[] blackPawnValueTable = PIECE_VALUE_TABLES[0][Piece.PAWN];
    while (blackPawns != 0)
    {
      int pawnSquareIndex = Board.getLeastSignificantBit(blackPawns);
      pawnSquare = Board.SQUARES[pawnSquareIndex];
      blackPawns ^= pawnSquare.mask_on;

      //if(DEBUG)System.err.println("  p@" + pawnSquare.toString());

//      blackPawnScore += Piece.TYPE_VALUES[Piece.PAWN];
      pawnHashEntry.pawnFlags.blackPawnCount++;
      pawnHashEntry.pawnFlags.openFiles &= ~FILES[pawnSquare.file];
      pawnHashEntry.pawnFlags.openRanks &= ~RANKS[pawnSquare.rank];
//      blackPawnScore += blackPawnValueTable[pawnSquareIndex];
      pawnHashEntry.pawnFlags.closedFiles[0] |= FILES[pawnSquare.file] & BLACK_HALF[pawnSquare.index64];

      if (pawnSquare.file == 0)
      {
        // black pawn chain (A1-H8)
        if (((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquareIndex + 9].mask_on) != 0))
        {
          blackPawnScore += blackPawnValueTable[pawnSquareIndex] >> 2;
          pawnHashEntry.pawnFlags.closedFiles[1] |= FILES[pawnSquare.file] & WHITE_HALF[pawnSquare.index64];
          //if(DEBUG)System.err.println("    A1-H8 Chain");
        }
      }
      else if (pawnSquare.file == 7)
      {
        // black pawn phalanx
        if ((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 1].mask_on) != 0 && pawnSquare.rank < 6)
        {
          blackPawnScore += blackPawnValueTable[pawnSquareIndex] >> 1;
          //if(DEBUG)System.err.println("    Phalanx");
        }
        // black pawn chain (H1-A8)
        if (((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquareIndex + 7].mask_on) != 0))
        {
          blackPawnScore += blackPawnValueTable[pawnSquareIndex] >> 2;
          pawnHashEntry.pawnFlags.closedFiles[1] |= FILES[pawnSquare.file] & WHITE_HALF[pawnSquare.index64];
          //if(DEBUG)System.err.println("    H1-A8 Chain");
        }
      }
      else
      {
        // black pawn phalanx
        if ((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquareIndex - 1].mask_on) != 0 && pawnSquare.rank < 6)
        {
          blackPawnScore += blackPawnValueTable[pawnSquareIndex] >> 1;
          //if(DEBUG)System.err.println("    Phalanx");
        }
        // black pawn chain (A1-H8)
        if (((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquareIndex + 9].mask_on) != 0))
        {
          blackPawnScore += blackPawnValueTable[pawnSquareIndex] >> 2;
          pawnHashEntry.pawnFlags.closedFiles[1] |= FILES[pawnSquare.file] & WHITE_HALF[pawnSquare.index64];
          //if(DEBUG)System.err.println("    A1-H8 Chain");
        }
        // black pawn chain (H1-A8)
        if (((board.pieceBoards[0][Piece.PAWN] & Board.SQUARES[pawnSquareIndex + 7].mask_on) != 0))
        {
          blackPawnScore += blackPawnValueTable[pawnSquareIndex] >> 2;
          pawnHashEntry.pawnFlags.closedFiles[1] |= FILES[pawnSquare.file] & WHITE_HALF[pawnSquare.index64];
          //if(DEBUG)System.err.println("    H1-A8 Chain");
        }
      }

      // locked files
      if ((board.pieceBoards[1][Piece.PAWN] & Board.SQUARES[pawnSquare.index64 - 8].mask_on) != 0)
      {
        pawnHashEntry.pawnFlags.lockedFiles |= FILES[pawnSquare.file];
        //if(DEBUG)System.err.println("    Locked File");
//        continue;
      }


      // black backward pawns
      // black isolated pawns
      if ((board.pieceBoards[0][Piece.PAWN] & BLACK_BACKWARDS_MASK[pawnSquare.index64]) == 0 ||
          (board.pieceBoards[0][Piece.PAWN] & BLACK_ISO_MASK[pawnSquare.index64]) == 0)
      {
        blackPawnScore -= PAWN_BACKWARDS_VALUE[pawnSquare.file] >> 2;
        //if(DEBUG)System.err.println("    Backwards");
        if((board.pieceBoards[1][Piece.PAWN] & FILES[pawnSquare.file]) == 0)
        {
          blackPawnScore -= PAWN_BACKWARDS_VALUE[pawnSquare.file];
          //if(DEBUG)System.err.println("    Doubled Backwards");
        }
      }

      // doubled pawns
      if ((board.pieceBoards[0][Piece.PAWN] & WHITE_PASSED_MASK[pawnSquare.index64] & FILES[pawnSquare.file]) != 0)
      {
        blackPawnScore -= PAWN_DOUBLED_VALUE;
        //if(DEBUG)System.err.println("    Doubled Pawn");
      }

      // black passed pawns
      if ((board.pieceBoards[1][Piece.PAWN] & BLACK_PASSED_MASK[pawnSquare.index64]) == 0)
      {
        pawnHashEntry.pawnFlags.blackPassedPawns |= pawnSquare.mask_on;
        //if(DEBUG)System.err.println("    Passed");
      }
    }

    score += whitePawnScore - blackPawnScore;

    int whiteQueenside = normalizeQueensidePawns(board, pawnHashEntry.pawnFlags.lockedFiles, 1);
    int blackQueenside = normalizeQueensidePawns(board, pawnHashEntry.pawnFlags.lockedFiles, 0);
    int whiteKingside = normalizeKingsidePawns(board, pawnHashEntry.pawnFlags.lockedFiles, 1);
    int blackKingside = normalizeKingsidePawns(board, pawnHashEntry.pawnFlags.lockedFiles, 0);
    int whiteCenter = normalizeCenterPawns(board, 1);
    int blackCenter = normalizeCenterPawns(board, 0);

    int queensideScore = PAWNSCORES[ (whiteQueenside << 3) + blackQueenside];
    int kingsideScore = PAWNSCORES[ (whiteKingside << 3) + blackKingside];
    int centerScore = PAWNSCORES[ (whiteCenter << 3) + blackCenter];

    pawnHashEntry.pawnFlags.centerScore = centerScore;
    pawnHashEntry.pawnFlags.endgameScore = (queensideScore + kingsideScore);

    pawnHashEntry.pawnFlags.score = score;

    // Store pawn hash
    pawnHashEntry.hash = board.pawnHash;

    return pawnHashEntry.pawnFlags;
  }

  private int normalizeQueensidePawns(Board board, long lockedFiles, int color)
  {
    long pawns = board.pieceBoards[color][Piece.PAWN] & ~lockedFiles;
    if((pawns & FILES[0]) != 0)
    {
      if((pawns & FILES[1]) != 0)
      {
        // ABC
        if((pawns & FILES[2]) != 0)
        {
          return 7;
        }
        // AB
        else
        {
          return 6;
        }

      }
      // A C
      else if((pawns & FILES[2]) != 0)
      {
        return 5;
      }
      // A
      else
      {
        return 4;
      }
    }
    else if((pawns & FILES[1]) != 0)
    {
      //  BC
      if((pawns & FILES[2]) != 0)
      {
        return 3;
      }
      //  B
      else
      {
        return 2;
      }
    }
    //   C
    else if((pawns & FILES[2]) != 0)
    {
      return 1;
    }
    return 0;
  }

  private int normalizeKingsidePawns(Board board, long lockedFiles, int color)
  {
    long pawns = board.pieceBoards[color][Piece.PAWN] & ~lockedFiles;
    if((pawns & FILES[7]) != 0)
    {
      if((pawns & FILES[6]) != 0)
      {
        // HGF
        if((pawns & FILES[5]) != 0)
        {
          return 7;
        }
        // HG
        else
        {
          return 6;
        }

      }
      // H F
      else if((pawns & FILES[5]) != 0)
      {
        return 5;
      }
      // H
      else
      {
        return 4;
      }
    }
    else if((pawns & FILES[6]) != 0)
    {
      //  GF
      if((pawns & FILES[5]) != 0)
      {
        return 3;
      }
      //  G
      else
      {
        return 2;
      }
    }
    //   F
    else if((pawns & FILES[5]) != 0)
    {
      return 1;
    }
    return 0;
  }

  private int normalizeCenterPawns(Board board, int color)
  {
    long pawns = board.pieceBoards[color][Piece.PAWN];
    if((pawns & FILES[3]) != 0)
    {
      // ED
      if((pawns & FILES[4]) != 0)
      {
        return 3;
      }
      // E
      else
      {
        return 2;
      }

    }
    // D
    else if((pawns & FILES[4]) != 0)
    {
      return 1;
    }
    return 0;
  }

  private int scorePassedPawns(Board board,
                               long whitePassedPawns,
                               long blackPassedPawns,
                               int whiteMaterial,
                               int blackMaterial)
  {
/*
    if(whiteMaterial > 21 || blackMaterial > 21)
    {
      return 0;
    }
*/
    double whiteMaterialRatio = 1 - (whiteMaterial / 48D);
    double blackMaterialRatio = 1 - (blackMaterial / 48D);

    int whiteScore = 0;
    int blackScore = 0;

    while (whitePassedPawns != 0)
    {
      int pawnSquareIndex = Board.getLeastSignificantBit(whitePassedPawns);
      whitePassedPawns ^= 1L << pawnSquareIndex;

      Square square = Board.SQUARES[pawnSquareIndex];
/*
      if (square.file < 2 && square.file < board.blackKing.square.file - 2)
      {
        whiteScore += PASSED_PAWN_WIDE_FILE_VALUE;
      }
      else if (square.file > 5 && square.file > board.blackKing.square.file + 2)
      {
        whiteScore += PASSED_PAWN_WIDE_FILE_VALUE;
      }
*/

      int passedPawnValue = WHITE_PASSED_PAWN_VALUES[pawnSquareIndex >> 3];
      whiteScore += passedPawnValue >> 1;

      Square advancingSquare = Board.SQUARES[pawnSquareIndex + 8];
      if(board.boardSquares[advancingSquare.index128].piece == null)
      {
        whiteScore += passedPawnValue >> 3;
        int swap = swapMove(board, square, advancingSquare, 0, 100, 0);
        if (swap >= 0)
        {
          whiteScore += passedPawnValue >> 2;
          Square queeningSquare = Board.SQUARES[((pawnSquareIndex & 7) + 56)];
          swap = swapMove(board, square, queeningSquare, 0, 100, 0);
          if (swap >= 0)
          {
            whiteScore += passedPawnValue >> 3;
          }

          if((board.pieceBoards[0][6] & board.blackKing.square.mask_off) == 0 && (board.blackKing.square.mask_on & WHITE_RUNAWAY_PAWN_MASK[pawnSquareIndex - (board.turn == 1 ? 0 : 8)]) == 0)
          {
            // lone pawn, can the king intercept it?
            whiteScore += passedPawnValue >> 2;
          }
        }
      }
    }

    while (blackPassedPawns != 0)
    {
      int pawnSquareIndex = Board.getLeastSignificantBit(blackPassedPawns);
      blackPassedPawns ^= 1L << pawnSquareIndex;

      Square square = Board.SQUARES[pawnSquareIndex];
/*
      if (square.file < 2 && square.file < board.whiteKing.square.file - 2)
      {
        blackScore += PASSED_PAWN_WIDE_FILE_VALUE;
      }
      else if (square.file > 5 && square.file > board.whiteKing.square.file + 2)
      {
        blackScore += PASSED_PAWN_WIDE_FILE_VALUE;
      }
*/

      int passedPawnValue = BLACK_PASSED_PAWN_VALUES[pawnSquareIndex >> 3];
      blackScore += passedPawnValue >> 1;

      Square advancingSquare = Board.SQUARES[pawnSquareIndex - 8];
      if(board.boardSquares[advancingSquare.index128].piece == null)
      {
        blackScore += passedPawnValue >> 3;
        int swap = swapMove(board, square, advancingSquare, 1, 100, 0);
        if (swap >= 0)
        {
          blackScore += passedPawnValue >> 2;
          Square queeningSquare = Board.SQUARES[(pawnSquareIndex & 7)];
          swap = swapMove(board, square, queeningSquare, 1, 100, 0);
          if (swap >= 0)
          {
            blackScore += passedPawnValue >> 3;
          }

          if((board.pieceBoards[1][6] & board.whiteKing.square.mask_off) == 0 && (board.whiteKing.square.mask_on & BLACK_RUNAWAY_PAWN_MASK[pawnSquareIndex + (board.turn == 0 ? 0 : 8)]) == 0)
          {
            // lone pawn, can the king intercept it?
            blackScore += passedPawnValue >> 2;
          }
        }
      }
    }

    whiteScore *= blackMaterialRatio;
    blackScore *= whiteMaterialRatio;

    return whiteScore - blackScore;
  }

  //////////////////
  // EVAL WHITE KING
  //////////////////

  private int evalWhiteKing(Board board, PawnFlags pawnFlags, int blackMaterial)
  {
    Square kingSquare = board.whiteKing.square;
    int score = 0;

    if ((board.pieceBoards[0][Piece.QUEEN] == 0 && blackMaterial < 17))
    {
      score += PIECE_VALUE_TABLES[1][Piece.KNIGHT][kingSquare.index64];
      int nearbyPassedPawns = Board.countBits(MoveGeneration.attackVectors[1][Piece.KING][kingSquare.index64] & (pawnFlags.whitePassedPawns | pawnFlags.blackPassedPawns));
      score += nearbyPassedPawns * kingSquare.rank * 3;

      return score;
    }

    int attackers = scoreAttackingPieces(board, kingSquare, 0);
    int pawnShelter = scorePawnShelter(board, kingSquare, 0);
    if(board.stats.whiteKingMoves == 0 && board.stats.whiteCastleFlag == 0)
    {
      int queensidePawnShelter = 0;
      int kingsidePawnShelter = 0;

      if(board.stats.whiteKingsideRookMoves == 0)
      {
        kingsidePawnShelter = scorePawnShelter(board, Square.G1, 0);
      }
      else
      {
        kingsidePawnShelter = pawnShelter + KING_FORFEIT_CASTLE_VALUE;
      }

      if(board.stats.whiteQueensideRookMoves == 0)
      {
        queensidePawnShelter = scorePawnShelter(board, Square.B1, 0);
      }
      else
      {
        queensidePawnShelter = pawnShelter + KING_FORFEIT_CASTLE_VALUE;
      }

      attackers += (kingsidePawnShelter + queensidePawnShelter + pawnShelter + KING_FORFEIT_CASTLE_VALUE) / 3;
    }
    else
    {
      if(kingSquare.file < 6 && kingSquare.file > 2)
      {
        attackers += pawnShelter + KING_FORFEIT_CASTLE_VALUE;
      }
      else
      {
        attackers += pawnShelter;
      }
    }

    score -= attackers;

    return score;
  }

  //////////////////
  // EVAL BLACK KING
  //////////////////

  private int evalBlackKing(Board board, PawnFlags pawnFlags, int whiteMaterial)
  {
    Square kingSquare = board.blackKing.square;
    int score = 0;

    if ((board.pieceBoards[1][Piece.QUEEN] == 0 && whiteMaterial < 17))
    {
      score += PIECE_VALUE_TABLES[0][Piece.KNIGHT][kingSquare.index64];
      int nearbyPassedPawns = Board.countBits(MoveGeneration.attackVectors[0][Piece.KING][kingSquare.index64] & (pawnFlags.whitePassedPawns | pawnFlags.blackPassedPawns));
      score += nearbyPassedPawns * (7-kingSquare.rank) * 3;
      return score;
    }

    int attackers = scoreAttackingPieces(board, kingSquare, 1);
    int pawnShelter = scorePawnShelter(board, kingSquare, 1);
    if(board.stats.blackKingMoves == 0 && board.stats.blackCastleFlag == 0)
    {
      int queensidePawnShelter = 0;
      int kingsidePawnShelter = 0;

      if(board.stats.blackKingsideRookMoves == 0)
      {
        kingsidePawnShelter = scorePawnShelter(board, Square.G8, 1);
      }
      else
      {
        kingsidePawnShelter = pawnShelter + KING_FORFEIT_CASTLE_VALUE;
      }

      if(board.stats.blackQueensideRookMoves == 0)
      {
        queensidePawnShelter = scorePawnShelter(board, Square.B8, 1);
      }
      else
      {
        queensidePawnShelter = pawnShelter + KING_FORFEIT_CASTLE_VALUE;
      }

      score -= (kingsidePawnShelter + queensidePawnShelter + pawnShelter + KING_FORFEIT_CASTLE_VALUE) / 3;
    }
    else
    {
      if(kingSquare.file < 6 && kingSquare.file > 2)
      {
        score -= pawnShelter + KING_FORFEIT_CASTLE_VALUE;
      }
      else
      {
        score -= pawnShelter;
      }
    }
    score -= attackers;

    return score;
  }

  private static int[][] KING_SAFETY_STAGING_AREA = new int[2][3];
  private static int[][] KING_SAFETY_PAWN_AREA = new int[2][3];
  private static int[][] KING_SAFETY_TINY_AREA = new int[2][5];

  static
  {
    KING_SAFETY_STAGING_AREA[1] = new int[]{31, 32, 33};
    KING_SAFETY_STAGING_AREA[0] = new int[]{-31, -32, -33};

    KING_SAFETY_PAWN_AREA[1] = new int[]{15, 16, 17};
    KING_SAFETY_PAWN_AREA[0] = new int[]{-15, -16, -17};

    KING_SAFETY_TINY_AREA[1] = new int[]{1, -1, -15, -16, -17};
    KING_SAFETY_TINY_AREA[0] = new int[]{1, -1, 15, 16, 17};

  }

  /**
   * count
   * [1 - 3] [ P ] [ minor ] [ R ] [ Q ] [ K ]
   *
   * @param board
   * @param kingSquare
   * @param color
   * @return
   */
  private int scoreAttackingPieces(Board board,
                                   Square kingSquare,
                                   int color)
  {
    int score = 0;
    int defenderColor = color ^ 1;
    Square square;
    long attackers;
    long squares = KING_STAGING_AREA[defenderColor][kingSquare.index64];

    while(squares != 0)
    {
      square = Board.SQUARES[Board.getLeastSignificantBit(squares)];
      squares &= square.mask_off;

      attackers = moveGeneration.getAllAttackers(board, square, color) & ~board.pieceBoards[color][Piece.QUEEN];
      if((attackers) != 0)
      {
        int attacks = scoreAttacksToSquare(board, attackers, color);
        if(!board.isSquareAttackedByColor(square, defenderColor))
        {
          score += attacks;
        }
        else
        {
          score += attacks >> 2;
        }
      }
    }
    squares = KING_PAWN_AREA[defenderColor][kingSquare.index64];

    while(squares != 0)
    {
      square = Board.SQUARES[Board.getLeastSignificantBit(squares)];
      squares &= square.mask_off;

      attackers = moveGeneration.getAllAttackers(board, square, color);
      if((attackers) != 0)
      {
        int attacks = scoreAttacksToSquare(board, attackers, color);
        Piece piece = board.boardSquares[square.index128].piece;
        if(!board.isSquareAttackedByColor(square, defenderColor))
        {
          if(piece == null || (piece.color != defenderColor || (piece.type != Piece.PAWN && piece.type != Piece.BISHOP)))
          {
            score += attacks << 1;
          }
          else
          {
            score += attacks;
          }
        }
        else
        {
          if(piece == null || (piece.color != defenderColor || (piece.type != Piece.PAWN && piece.type != Piece.BISHOP)))
          {
            score += attacks >> 1;
          }
          else
          {
            score += attacks >> 2;
          }
        }
      }
    }

    squares = KING_TINY_AREA[defenderColor][kingSquare.index64];

    while(squares != 0)
    {
      square = Board.SQUARES[Board.getLeastSignificantBit(squares)];
      squares &= square.mask_off;

      attackers = moveGeneration.getAllAttackers(board, square, color);
      if((attackers) != 0)
      {
        int attacks = scoreAttacksToSquare(board, attackers, color);
        if(!board.isSquareAttackedByColor(square, defenderColor))
        {
          score += attacks;
        }
        else
        {
          score += attacks >> 1;
        }
      }
    }

    return score;

  }

  /**
   * count
   * [1 - 3] [ P ] [ minor ] [ R ] [ Q ] [ K ]
   *
   * @param board
   * @param kingSquare
   * @param color
   * @return
   */
  private int scorePawnShelter(Board board,
                               Square kingSquare,
                               int color)
  {
    int pawnScore = 0;
    int enemyColor = color ^ 1;
    Board.BoardSquare boardSquare;
    int squareIndex;

    for (int i = 0; i < 3; i++)
    {
      squareIndex = kingSquare.index128 + KING_SAFETY_PAWN_AREA[enemyColor][i];
      if ((squareIndex & 0x88) != 0)
      {
        continue;
      }
      boardSquare = board.boardSquares[squareIndex];

      if(boardSquare.piece == null || boardSquare.piece.color != enemyColor)
      {
        pawnScore += (board.mobilityFile(kingSquare.index64) +
                  board.mobilityDiaga1(kingSquare.index64) +
                  board.mobilityDiagh1(kingSquare.index64)) * (PIECE_VALUE_TABLES[0][Piece.KING][kingSquare.index64]);
      }
    }
    return max(pawnScore, 0);
  }

  public int scoreAttacksToSquare(Board board, long attackers, int color)
  {
    int score = 0;

    int pawns = 0;
    int minors = 0;
    int rooks = 0;
    int queens = 0;
    int king = 0;

    while (attackers != 0)
    {
      if ((board.pieceBoards[color][Piece.PAWN] & attackers) != 0)
      {
        pawns++;
        attackers ^= 1L << Board.getLeastSignificantBit(board.pieceBoards[color][Piece.PAWN] & attackers);
      }
      else if ((board.pieceBoards[color][Piece.KNIGHT] & attackers) != 0)
      {
        minors++;
        attackers ^= 1L << Board.getLeastSignificantBit(board.pieceBoards[color][Piece.KNIGHT] & attackers);
      }
      else if ((board.pieceBoards[color][Piece.BISHOP] & attackers) != 0)
      {
        minors++;
        attackers ^= 1L << Board.getLeastSignificantBit(board.pieceBoards[color][Piece.BISHOP] & attackers);
      }
      else if ((board.pieceBoards[color][Piece.ROOK] & attackers) != 0)
      {
        rooks++;
        attackers ^= 1L << Board.getLeastSignificantBit(board.pieceBoards[color][Piece.ROOK] & attackers);
      }
      else if ((board.pieceBoards[color][Piece.QUEEN] & attackers) != 0)
      {
        queens++;
        attackers ^= 1L << Board.getLeastSignificantBit(board.pieceBoards[color][Piece.QUEEN] & attackers);
      }
      else if ((board.pieceBoards[color][Piece.KING] & attackers) != 0)
      {
        king++;
        attackers ^= 1L << Board.getLeastSignificantBit(board.pieceBoards[color][Piece.KING] & attackers);
      }
      else
      {
        break;
      }
    }

    int pawnValue = 0;
    int minorValue = 0;
    int rookValue = 0;
    int queenValue = 0;

    pawnValue += 10 * (minors + queens + rooks + king + pawns);
    minorValue += 17 * (pawns + queens + rooks + king + minors);
    rookValue += 23 * (pawns + minors + queens + king + rooks);
    queenValue += 29 * (pawns + minors + rooks + king + (max(0, queens - 1)));
    queenValue += 15 * (queens);

    score = (pawns * pawnValue) + (minors * minorValue) + (rooks * rookValue) + (queens * queenValue) + king;

    return score;
  }

  public int scoreDefenseOfSquare(Board board, long bishopAttacks, long rookAttacks, Square square, int color, boolean rewardOccupancy)
  {
    int score = 0;
    int pawns = 0;
    int minors = 0;
    int rooks = 0;
    int queens = 0;
    int king = 0;


    long pawnAttackers = (MoveGeneration.attackVectors[color][Piece.PAWN][square.index64] & board.pieceBoards[color][Piece.PAWN]);
    while (pawnAttackers != 0)
    {
      pawnAttackers ^= 1L << Board.getLeastSignificantBit(pawnAttackers);
      pawns++;
    }
    long knightAttackers = (MoveGeneration.attackVectors[color][Piece.KNIGHT][square.index64] & board.pieceBoards[color][Piece.KNIGHT]);
    while (knightAttackers != 0)
    {
      knightAttackers ^= 1L << Board.getLeastSignificantBit(knightAttackers);
      minors++;
    }

    long attackers = bishopAttacks & board.pieceBoards[color][Piece.BISHOP];
    while (attackers != 0)
    {
      attackers ^= 1L << Board.getLeastSignificantBit(attackers);
      minors++;
    }
    attackers = rookAttacks & board.pieceBoards[color][Piece.ROOK];
    while (attackers != 0)
    {
      attackers ^= 1L << Board.getLeastSignificantBit(attackers);
      rooks++;
    }
    attackers = (bishopAttacks | rookAttacks) & board.pieceBoards[color][Piece.QUEEN];
    while (attackers != 0)
    {
      attackers ^= 1L << Board.getLeastSignificantBit(attackers);
      queens++;
    }
    attackers = (MoveGeneration.attackVectors[color][Piece.KING][square.index64] & board.pieceBoards[color][Piece.KING]);
    if (attackers != 0)
    {
      king++;
    }

    Piece pieceOnSquare = board.boardSquares[square.index128].piece;

    if (rewardOccupancy && pieceOnSquare != null && pieceOnSquare.color == color)
    {

      switch (pieceOnSquare.type)
      {
        case Piece.PAWN:
        {
          pawns++;
          break;
        }
        case Piece.KNIGHT:
        {
          minors++;
          break;
        }
        case Piece.BISHOP:
        {
          minors++;
          break;
        }
        case Piece.ROOK:
        {
          rooks++;
          break;
        }
        case Piece.QUEEN:
        {
          queens++;
          break;
        }
      }
    }

    int kingValue = 24;

    int pawnValue = 30 * (minors + queens + rooks + king);
    int minorValue = 20 * (pawns + queens + rooks + king);
    int rookValue = 10 * (pawns + minors + queens + king);
    int queenValue = 5 * (pawns + minors + rooks + king);

    pawnValue += 15 * (pawns);
    minorValue += 12 * (minors);
    rookValue += 5 * (rooks);
    queenValue += 2 * (queens);

    score = (pawns * pawnValue) + (minors * minorValue) + (rooks * rookValue) + (queens * queenValue) + (king * kingValue);

    return score;
  }

  private int countDefendingPieces(Board board, long kingArea, int color)
  {
    int attackers = Board.countBits(kingArea & (board.pieceBoards[color][Piece.KNIGHT] |
                                                board.pieceBoards[color][Piece.BISHOP] |
                                                board.pieceBoards[color][Piece.QUEEN] |
                                                board.pieceBoards[color][Piece.ROOK]));
    return attackers;
  }

  private long getStagingKingArea(Square square, int color)
  {
    if(color == 1)
    {
      return square.rank > 5 ? 0 :
             (square.file > 0 ? Board.SQUARES[square.index64 + 15].mask_on : 0) |
             Board.SQUARES[square.index64 + 16].mask_on |
             (square.file < 7 ? Board.SQUARES[square.index64 + 17].mask_on : 0);
    }
    return square.rank < 2 ? 0 : 
           (square.file > 0 ? Board.SQUARES[square.index64 - 17].mask_on : 0) |
           Board.SQUARES[square.index64 - 16].mask_on |
           (square.file < 7 ? Board.SQUARES[square.index64 - 15].mask_on : 0);
  }

  private long getPawnKingArea(Square square, int color)
  {
    if(color == 1)
    {
      return square.rank == 7 ? 0 :
             (square.file > 0 ? Board.SQUARES[square.index64 + 7].mask_on : 0) |
             Board.SQUARES[square.index64 + 8].mask_on |
             (square.file < 7 ? Board.SQUARES[square.index64 + 9].mask_on : 0);
    }
    return square.rank == 0 ? 0 :
           (square.file > 0 ? Board.SQUARES[square.index64 - 9].mask_on : 0) |
           Board.SQUARES[square.index64 - 8].mask_on |
           (square.file < 7 ? Board.SQUARES[square.index64 - 7].mask_on : 0);
  }

  private long getTinyKingArea(Square square, int color)
  {
    if(color == 1)
    {
      return (square.rank == 0 ? 0 : (square.file > 0 ? Board.SQUARES[square.index64 - 9].mask_on : 0)) |
             (square.rank == 0 ? 0 : Board.SQUARES[square.index64 - 8].mask_on) |
             (square.rank == 0 ? 0 : (square.file < 7 ? Board.SQUARES[square.index64 - 7].mask_on : 0)) |
             (square.file > 0 ? Board.SQUARES[square.index64 - 1].mask_on : 0) |
             (square.file < 7 ? Board.SQUARES[square.index64 + 1].mask_on : 0);
    }
    return (square.rank == 7 ? 0 : (square.file > 0 ? Board.SQUARES[square.index64 + 7].mask_on : 0)) |
           (square.rank == 7 ? 0 : Board.SQUARES[square.index64 + 8].mask_on) |
           (square.rank == 7 ? 0 : (square.file < 7 ? Board.SQUARES[square.index64 + 9].mask_on : 0)) |
           (square.file > 0 ? Board.SQUARES[square.index64 - 1].mask_on : 0) |
           (square.file < 7 ? Board.SQUARES[square.index64 + 1].mask_on : 0);
  }

  
  private static long getKingAreaA1H8(Square square)
  {
    return MoveGeneration.attackVectors[0][7][square.index64]
           | (square.file > 0 ?
              MoveGeneration.attackVectors[0][7][square.index64 - 1] :
              0)
           | (square.file < 7 ?
              MoveGeneration.attackVectors[0][7][square.index64 + 1] :
              0)
           | (square.rank > 0 ?
              MoveGeneration.attackVectors[0][7][square.index64 - 8] :
              0)
           | (square.rank < 7 ?
              MoveGeneration.attackVectors[0][7][square.index64 + 8] :
              0)
           | (square.rank < 7 && square.file > 0 ?
              MoveGeneration.attackVectors[0][7][square.index64 + 7] :
              0)
           | (square.rank < 7 && square.file < 7 ?
              MoveGeneration.attackVectors[0][7][square.index64 + 9] :
              0)
           | (square.rank > 0 && square.file > 0 ?
              MoveGeneration.attackVectors[0][7][square.index64 - 9] :
              0)
           | (square.rank > 0 && square.file < 7 ?
              MoveGeneration.attackVectors[0][7][square.index64 - 7] :
              0);
  }

  private static long getKingAreaH1A8(Square square)
  {
    return MoveGeneration.attackVectors[0][8][square.index64]
           | (square.file > 0 ?
              MoveGeneration.attackVectors[0][8][square.index64 - 1] :
              0)
           | (square.file < 7 ?
              MoveGeneration.attackVectors[0][8][square.index64 + 1] :
              0)
           | (square.rank > 0 ?
              MoveGeneration.attackVectors[0][8][square.index64 - 8] :
              0)
           | (square.rank < 7 ?
              MoveGeneration.attackVectors[0][8][square.index64 + 8] :
              0)
           | (square.rank < 7 && square.file > 0 ?
              MoveGeneration.attackVectors[0][8][square.index64 + 7] :
              0)
           | (square.rank < 7 && square.file < 7 ?
              MoveGeneration.attackVectors[0][8][square.index64 + 9] :
              0)
           | (square.rank > 0 && square.file > 0 ?
              MoveGeneration.attackVectors[0][8][square.index64 - 9] :
              0)
           | (square.rank > 0 && square.file < 7 ?
              MoveGeneration.attackVectors[0][8][square.index64 - 7] :
              0);
  }

  private int max(int a, int b)
  {
    return a > b ?
           a :
           b;
  }

  private int min(int a, int b)
  {
    return a < b ?
           a :
           b;
  }

  private double max(double a, double b)
  {
    return a > b ?
           a :
           b;
  }

  private double min(double a, double b)
  {
    return a > b ?
           a :
           b;
  }

  public static void multiplyAll(int[] original, int factor)
  {
    for (int i = 0; i < original.length; i++)
    {
      original[i] *= factor;
    }
  }

  public int getMaterial(Board board)
  {
    int whiteKnightCount = Board.countBits(board.pieceBoards[1][Piece.KNIGHT]);
    int blackKnightCount = Board.countBits(board.pieceBoards[0][Piece.KNIGHT]);

    int whiteBishopCount = Board.countBits(board.pieceBoards[1][Piece.BISHOP]);
    int blackBishopCount = Board.countBits(board.pieceBoards[0][Piece.BISHOP]);

    int whiteRookCount = Board.countBits(board.pieceBoards[1][Piece.ROOK]);
    int blackRookCount = Board.countBits(board.pieceBoards[0][Piece.ROOK]);

    int whiteQueenCount = Board.countBits(board.pieceBoards[1][Piece.QUEEN]);
    int blackQueenCount = Board.countBits(board.pieceBoards[0][Piece.QUEEN]);

    int whiteMaterial = (whiteKnightCount * 3) + (whiteBishopCount * 3) + (whiteRookCount * 5) + (whiteQueenCount * 9);
    int blackMaterial = (blackKnightCount * 3) + (blackBishopCount * 3) + (blackRookCount * 5) + (blackQueenCount * 9);

    return whiteMaterial + blackMaterial;
  }

  public int getMaterialDifference(Board board)
  {
    int whiteKnightCount = Board.countBits(board.pieceBoards[1][Piece.KNIGHT]);
    int blackKnightCount = Board.countBits(board.pieceBoards[0][Piece.KNIGHT]);

    int whiteBishopCount = Board.countBits(board.pieceBoards[1][Piece.BISHOP]);
    int blackBishopCount = Board.countBits(board.pieceBoards[0][Piece.BISHOP]);

    int whiteRookCount = Board.countBits(board.pieceBoards[1][Piece.ROOK]);
    int blackRookCount = Board.countBits(board.pieceBoards[0][Piece.ROOK]);

    int whiteQueenCount = Board.countBits(board.pieceBoards[1][Piece.QUEEN]);
    int blackQueenCount = Board.countBits(board.pieceBoards[0][Piece.QUEEN]);

    int whiteMaterial = (whiteKnightCount * 3) + (whiteBishopCount * 3) + (whiteRookCount * 5) + (whiteQueenCount * 9);
    int blackMaterial = (blackKnightCount * 3) + (blackBishopCount * 3) + (blackRookCount * 5) + (blackQueenCount * 9);

    return whiteMaterial - blackMaterial;
  }


  public int getBlackKingSafety(Board board)
  {
    int whiteKnightCount = Board.countBits(board.pieceBoards[1][Piece.KNIGHT]);
    int whiteBishopCount = Board.countBits(board.pieceBoards[1][Piece.BISHOP]);
    int whiteRookCount = Board.countBits(board.pieceBoards[1][Piece.ROOK]);
    int whiteQueenCount = Board.countBits(board.pieceBoards[1][Piece.QUEEN]);
    int whiteMaterial = (whiteKnightCount * 3) + (whiteBishopCount * 3) + (whiteRookCount * 5) + (whiteQueenCount * 9);

    PawnFlags pawnFlags = scorePawns(board);

    return evalBlackKing(board, pawnFlags, whiteMaterial);
  }

  public int getWhiteKingSafety(Board board)
  {
    int blackKnightCount = Board.countBits(board.pieceBoards[0][Piece.KNIGHT]);
    int blackBishopCount = Board.countBits(board.pieceBoards[0][Piece.BISHOP]);
    int blackRookCount = Board.countBits(board.pieceBoards[0][Piece.ROOK]);
    int blackQueenCount = Board.countBits(board.pieceBoards[0][Piece.QUEEN]);
    int blackMaterial = (blackKnightCount * 3) + (blackBishopCount * 3) + (blackRookCount * 5) + (blackQueenCount * 9);

    PawnFlags pawnFlags = scorePawns(board);

    return evalWhiteKing(board, pawnFlags, blackMaterial);
  }

  @Override
  public int getLastBlackKingSafety() {
    return blackKingScore;
  }

  @Override
  public int getLastWhiteKingSafety() {
    return whiteKingScore;
  }


  public int getKingSafety(Board board)
  {
    return getWhiteKingSafety(board) - getBlackKingSafety(board);
  }

  public int getPawns(Board board)
  {
    int whitePawnCount = Board.countBits(board.pieceBoards[1][Piece.KNIGHT]);
    int blackPawnCount = Board.countBits(board.pieceBoards[0][Piece.KNIGHT]);

    return whitePawnCount + blackPawnCount;
  }

  public int getPawnsDifference(Board board)
  {
    int whitePawnCount = Board.countBits(board.pieceBoards[1][Piece.KNIGHT]);
    int blackPawnCount = Board.countBits(board.pieceBoards[0][Piece.KNIGHT]);

    return whitePawnCount - blackPawnCount;
  }

  int[] swapScores = new int[32];

  private int swapMove(Board board, Square fromSquare, Square toSquare, int color, int movedValue, int takenValue)
  {
    int swapIndex = 1;

    long attackers = moveGeneration.getAllAttackers(board,
                                                    toSquare,
                                                    board.turn ^ 1) |
                     moveGeneration.getAllAttackers(board,
                                                    toSquare,
                                                    board.turn) |
                     moveGeneration.getNewAttackers(board, fromSquare, toSquare, board.turn) |
                     moveGeneration.getNewAttackers(board, fromSquare, toSquare, board.turn ^ 1);

    swapScores[0] = takenValue;
    int attackedPiece = movedValue;

//    swapScores[swapIndex++] = -swapScores[swapIndex - 1] + attackedPiece;
    attackers &= fromSquare.mask_off;


    while ((attackers & board.pieceBoards[color][Board.ALL_PIECES]) != 0)
    {
      swapScores[swapIndex] = -swapScores[swapIndex - 1] + attackedPiece;
      swapIndex++;

      if ((board.pieceBoards[color][Piece.PAWN] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.PAWN];
        attackers &= Board.SQUARES[Board.getLeastSignificantBit(board.pieceBoards[color][Piece.PAWN] & attackers)].mask_off;
      }
      else if ((board.pieceBoards[color][Piece.KNIGHT] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.KNIGHT];
        attackers &= Board.SQUARES[Board.getLeastSignificantBit(board.pieceBoards[color][Piece.KNIGHT] & attackers)].mask_off;
      }
      else if ((board.pieceBoards[color][Piece.BISHOP] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.BISHOP];
        attackers &= Board.SQUARES[Board.getLeastSignificantBit(board.pieceBoards[color][Piece.BISHOP] & attackers)].mask_off;
      }
      else if ((board.pieceBoards[color][Piece.ROOK] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.ROOK];
        attackers &= Board.SQUARES[Board.getLeastSignificantBit(board.pieceBoards[color][Piece.ROOK] & attackers)].mask_off;
      }
      else if ((board.pieceBoards[color][Piece.QUEEN] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.QUEEN];
        attackers &= Board.SQUARES[Board.getLeastSignificantBit(board.pieceBoards[color][Piece.QUEEN] & attackers)].mask_off;
      }
      else if ((board.pieceBoards[color][Piece.KING] & attackers) != 0)
      {
        attackedPiece = Piece.TYPE_VALUES[Piece.KING];
        attackers &= Board.SQUARES[Board.getLeastSignificantBit(board.pieceBoards[color][Piece.KING] & attackers)].mask_off;
      }
      else
      {
        break;
      }

      color = color ^ 1;
    }


    while (--swapIndex != 0)
    {
      if (swapScores[swapIndex] > -swapScores[swapIndex - 1])
      {
        swapScores[swapIndex - 1] = -swapScores[swapIndex];
      }
    }
    return (swapScores[0]);
  }
}