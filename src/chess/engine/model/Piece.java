/* $Id$ */

package chess.engine.model;

import chess.engine.utils.MoveGeneration;

/**
 * @author Joshua Levine <levinester@gmail.com>
 * @version $Revision$ $Name$ $Date$
 */
public final class Piece implements Cloneable, Comparable<Piece> {
  public static final boolean DEBUG = false;

  public static final int PAWN = 0;
  public static final int KNIGHT = 1;
  public static final int BISHOP = 2;
  public static final int ROOK = 3;
  public static final int QUEEN = 4;
  public static final int KING = 5;

  public static int ATTACKER_SHIFT_KING = 10;
  public static int ATTACKER_SHIFT_QUEEN = 8;
  public static int ATTACKER_SHIFT_ROOK = 6;
  public static int ATTACKER_SHIFT_BISHOP = 4;
  public static int ATTACKER_SHIFT_KNIGHT = 2;
  private static int ATTACKER_UMASK_KING = ~(3 << ATTACKER_SHIFT_KING);
  private static int ATTACKER_UMASK_QUEEN = ~(3 << ATTACKER_SHIFT_QUEEN);
  private static int ATTACKER_UMASK_ROOK = ~(3 << ATTACKER_SHIFT_ROOK);
  private static int ATTACKER_UMASK_BISHOP = ~(3 << ATTACKER_SHIFT_BISHOP);
  private static int ATTACKER_UMASK_KNIGHT = ~(3 << ATTACKER_SHIFT_KNIGHT);
  private static int ATTACKER_UMASK_PAWN = ~(3);

  public static int[] ATTACKER_SHIFT = {
          0, ATTACKER_SHIFT_KNIGHT, ATTACKER_SHIFT_BISHOP, ATTACKER_SHIFT_ROOK, ATTACKER_SHIFT_QUEEN, ATTACKER_SHIFT_KING
  };

  public static int[] ATTACKER_UMASK = {
          ~(3), ~(3 << ATTACKER_SHIFT_KNIGHT), ~(3 << ATTACKER_SHIFT_BISHOP), ~(3 << ATTACKER_SHIFT_ROOK), ~(3 << ATTACKER_SHIFT_QUEEN), ~(3 << ATTACKER_SHIFT_KING)
  };

  public static int[] TYPE_VALUES = {100, 311, 323, 500, 900, 10000};
  public static int[] MATERIAL_VALUES = {1, 3, 3, 5, 9, 0};
  public static int[] MASKS = {1, 2, 4, 8, 16, 32};
  public static boolean[][] TYPE_INDEX =
          {
                  {true, false, false, false, false, false},
                  {false, true, true, false, false, false},
                  {true, true, true, false, false, false},
                  {false, false, false, true, false, false},
                  {false, false, false, false, true, false},
                  {false, false, false, false, false, false},
          };

  public int color;
  public int type;
  public Square square;
  /**
   * 100, 311, 323, 500, 900, etc
   */
  public int value;
  /**
   * 1, 3, 3, 5, 9, etc
   */
  public int materialValue;
  public Board board;
  public long attacks;
  public long rams;

  public boolean kingsideRook = false;
  public boolean queensideRook = false;

  public int index;

  public int moveCount;

  public Piece(int index, Board board, int color, int type, Square square) {
    this.index = index;
    this.board = board;
    this.color = color;
    this.type = type;
    this.square = square;
    this.value = getValue();
    this.materialValue = getMaterialValue();

    if (type == ROOK) {
      if (square.file == Constants.FILE_A) {
        queensideRook = true;
      } else if (square.file == Constants.FILE_H) {
        kingsideRook = true;
      }
    }

    if (type == KING) {
      if (color == 1) {
        board.whiteKing = this;
      } else {
        board.blackKing = this;
      }
    }

    board.setPieceOnSquare(this, square);
    board.pieces[index] = this;
  }

  public int getValue() {
    return color == 1 ? TYPE_VALUES[type] : -TYPE_VALUES[type];
  }

  public int getMaterialValue() {
    return color == 1 ? MATERIAL_VALUES[type] : -MATERIAL_VALUES[type];
  }

  public boolean equals(Object o) {
    if (o instanceof Piece) {
      Piece piece = (Piece) o;

      return piece.type == type && piece.color == piece.color && piece.square == piece.square;
    }
    return false;
  }

  public String toString() {
    return toString(type, color);
  }

  public static String toString(int type, int color) {
    switch (type) {
      case PAWN: {
        return color == 1 ? "1" : "0";
      }
      case KNIGHT: {
        return color == 1 ? "N" : "n";
      }
      case BISHOP: {
        return color == 1 ? "B" : "b";
      }
      case ROOK: {
        return color == 1 ? "R" : "r";
      }
      case QUEEN: {
        return color == 1 ? "Q" : "q";
      }
      case KING: {
        return color == 1 ? "K" : "k";
      }
      default: {
        throw new RuntimeException("Piece has no type");
      }
    }
  }

  public int compareTo(Piece piece) {
    return piece.index > index ? 1 : -1;
  }

  public final void removeAttacks(Board board, Square square) {
    long attacks = this.attacks;
    attackerShift = ATTACKER_SHIFT[type];
    attackerUmask = ATTACKER_UMASK[type];
    while (attacks != 0) {
      int squareIndex = Board.getLeastSignificantBit(attacks);
      Square attackSquare = Board.SQUARES[squareIndex];
      attacks &= attackSquare.mask_off;
      board.squareAttackers[squareIndex] &= square.mask_off;
      if((board.squareAttackers[squareIndex] & board.pieceBoards[color][Board.ALL_PIECES]) == 0) {
        board.attacks[color] &= attackSquare.mask_off;
      }

      int attackerCount = (board.attackState[color][squareIndex] >> attackerShift) & 3;
      board.attackState[color][squareIndex] =
              (board.attackState[color][squareIndex] & attackerUmask) |
                      ((attackerCount == 0 ? 0 : attackerCount - 1) << attackerShift);
    }
    this.attacks = 0;
    long rams = this.rams;
    while (rams != 0) {
      int squareIndex = Board.getLeastSignificantBit(rams);
      Square attackSquare = Board.SQUARES[squareIndex];
      rams &= attackSquare.mask_off;
      board.squareRammers[squareIndex] &= square.mask_off;
      int attackerCount = (board.attackState[color][squareIndex] >> attackerShift) & 3;
      board.attackState[color][squareIndex] =
              (board.attackState[color][squareIndex] & attackerUmask) |
                      ((attackerCount == 0 ? 0 : attackerCount - 1) << attackerShift);
    }
    this.rams = 0;

/*
    if(DEBUG) System.err.println("Rem-" + this + "  @  "  + square.toString() +
            "\n  ATKS: " + board.getAllSquaresInBitboard(this.attacks) +
            "\n  RAMS: " + board.getAllSquaresInBitboard(this.rams)
    );
*/
  }

  int squareIndex;
  long mask;
  int attackerCount;
  public final void calculateAttacks(Board board, Square square) {

    long attacks = 0;
    long rams = 0;
    switch(type) {
      case PAWN: {
        attacks = (MoveGeneration.attackVectors[color ^ 1][Piece.PAWN][square.index64]);
        break;
      }
      case KNIGHT: {
        attacks = (MoveGeneration.attackVectors[color][Piece.KNIGHT][square.index64]);
        break;
      }
      case ROOK : {
        attacks = board.rookAttacks(square.index64);
        rams = attacks ^ board.rookAttacksXRay(square.index64, color);
        break;
      }
      case BISHOP : {
        attacks = board.bishopAttacks(square.index64);
        rams = attacks ^ board.bishopAttacksXRay(square.index64, color);
        break;
      }
      case QUEEN : {
        attacks = board.rookAttacks(square.index64) | board.bishopAttacks(square.index64);
        rams = attacks ^ (board.rookAttacksXRay(square.index64, color) | board.bishopAttacksXRay(square.index64, color));
        break;
      }
      case KING: {
        attacks = (MoveGeneration.attackVectors[color][Piece.KING][square.index64]);
        break;
      }
    }

    attackerShift = ATTACKER_SHIFT[type];
    attackerUmask = ATTACKER_UMASK[type];
    while (attacks != 0) {
      squareIndex = Board.getLeastSignificantBit(attacks);
      mask = 1L << squareIndex;
      attacks ^= mask;
      this.attacks |= mask;
      board.squareAttackers[squareIndex] |= square.mask_on;
      board.attacks[color] |= mask;

      attackerCount = (board.attackState[color][squareIndex] >> attackerShift) & 3;
      board.attackState[color][squareIndex] =
            (board.attackState[color][squareIndex] & attackerUmask) |
                    ((attackerCount == 2 ? 3 : attackerCount + 1) << attackerShift);
    }
    while (rams != 0) {
      squareIndex = Board.getLeastSignificantBit(rams);
      mask = 1L << squareIndex;
      rams ^= mask;
      this.rams |= mask;
      board.squareRammers[squareIndex] |= square.mask_on;

      attackerCount = (board.attackState[color][squareIndex] >> attackerShift) & 3;
      board.attackState[color][squareIndex] =
            (board.attackState[color][squareIndex] & attackerUmask) |
                    ((attackerCount == 2 ? 3 : attackerCount + 1) << attackerShift);
    }
/*
    if(DEBUG) System.err.println("Add-" + this + "  @  "  + square.toString() +
            "\n  ATKS: " + board.getAllSquaresInBitboard(this.attacks) +
            "\n  RAMS: " + board.getAllSquaresInBitboard(this.rams)
    );
*/
  }

  long removeAttacks;
  long removeRams;
  long addRams;
  int attackerShift;
  int attackerUmask;
  public void blockAttacks(Board board, Square square, Square attackerSquare) {
    removeAttacks = this.attacks;
    removeRams = 0;
    addRams = 0;
    final long pieceAndAttacker = square.mask_on | attackerSquare.mask_on;
    if(((pieceAndAttacker &
            board.pieceBoards[color][Board.QUEENS_BISHOPS]) == pieceAndAttacker &&
            MoveGeneration.attacksFromTo[0][Piece.BISHOP][square.index64][attackerSquare.index64])||
       ((pieceAndAttacker &
            board.pieceBoards[color][Board.QUEENS_ROOKS]) == pieceAndAttacker &&
            MoveGeneration.attacksFromTo[0][Piece.ROOK][square.index64][attackerSquare.index64])) {
      switch(type) {
        case ROOK : {
          addRams = board.rookAttacksXRay(attackerSquare.index64, color);
          break;
        }
        case BISHOP : {
          addRams = board.bishopAttacksXRay(attackerSquare.index64, color);
          break;
        }
        case QUEEN : {
          addRams = (board.rookAttacksXRay(attackerSquare.index64, color) | board.bishopAttacksXRay(attackerSquare.index64, color));
          break;
        }
      }
      addRams &= MoveGeneration.shadowVectors[attackerSquare.index128][square.index128] & ~this.rams;
    }
    else {
      removeRams = this.rams & MoveGeneration.shadowVectors[attackerSquare.index128][square.index128];
    }

    removeAttacks &= MoveGeneration.shadowVectors[attackerSquare.index128][square.index128];

/*
    if(DEBUG) System.err.println("Blk-" + this + "  @  "  + attackerSquare.toString() + " on " + square +
            "\n  ATKS: " + board.getAllSquaresInBitboard(this.attacks) +
            "\n  RAMS: " + board.getAllSquaresInBitboard(this.rams) +
            "\n  RATS: " + board.getAllSquaresInBitboard(removeAttacks) +
            "\n  ARMS: " + board.getAllSquaresInBitboard(addRams) +
            "\n  RRMS: " + board.getAllSquaresInBitboard(removeRams)
    );
*/

    this.attacks &= ~removeAttacks;
    this.rams |= addRams;
    this.rams &= ~removeRams;
    attackerShift = ATTACKER_SHIFT[type];
    attackerUmask = ATTACKER_UMASK[type];
    while (removeAttacks != 0) {
      int squareIndex = Board.getLeastSignificantBit(removeAttacks);
      Square attackSquare = Board.SQUARES[squareIndex];
      removeAttacks &= attackSquare.mask_off;
      board.squareAttackers[squareIndex] &= attackerSquare.mask_off;
      if((board.squareAttackers[squareIndex] & board.pieceBoards[color][Board.ALL_PIECES]) == 0) {
        board.attacks[color] &= attackSquare.mask_off;
      }

      int attackerCount = (board.attackState[color][squareIndex] >> attackerShift) & 3;
      board.attackState[color][squareIndex] = (board.attackState[color][squareIndex] & attackerUmask) |
                    ((attackerCount == 0 ? 0 : attackerCount - 1) << attackerShift);
    }

    while (addRams != 0) {
      squareIndex = Board.getLeastSignificantBit(addRams);
      mask = 1L << squareIndex;
      addRams &= ~mask;
      board.squareRammers[squareIndex] |= attackerSquare.mask_on;

      attackerCount = (board.attackState[color][squareIndex] >> attackerShift) & 3;
      board.attackState[color][squareIndex] = (board.attackState[color][squareIndex] & attackerUmask) |
                    ((attackerCount == 2 ? 3 : attackerCount + 1) << attackerShift);
    }

    while (removeRams != 0) {
      squareIndex = Board.getLeastSignificantBit(removeRams);
      mask = 1L << squareIndex;
      removeRams &= ~mask;
      board.squareRammers[squareIndex] &= attackerSquare.mask_off;

      attackerCount = (board.attackState[color][squareIndex] >> attackerShift) & 3;
      board.attackState[color][squareIndex] = (board.attackState[color][squareIndex] & attackerUmask) |
                    ((attackerCount == 0 ? 0 : attackerCount - 1) << attackerShift);
    }
  }

  long addAttacks;
  public void unblockAttacks(Board board, Square square, Square attackerSquare) {
    addAttacks = 0;
    addRams = 0;
    switch(type) {
      case ROOK : {
        addAttacks = board.rookAttacks(attackerSquare.index64);
        addRams = ~addAttacks & board.rookAttacksXRay(attackerSquare.index64, color);
        break;
      }
      case BISHOP : {
        addAttacks = board.bishopAttacks(attackerSquare.index64);
        addRams = ~addAttacks & board.bishopAttacksXRay(attackerSquare.index64, color);
        break;
      }
      case QUEEN : {
        addAttacks = board.rookAttacks(attackerSquare.index64) | board.bishopAttacks(attackerSquare.index64);
        addRams = ~addAttacks & (board.rookAttacksXRay(attackerSquare.index64, color) | board.bishopAttacksXRay(attackerSquare.index64, color));
        break;
      }
    }

    addAttacks &= MoveGeneration.shadowVectors[attackerSquare.index128][square.index128];
    addRams &= MoveGeneration.shadowVectors[attackerSquare.index128][square.index128] & ~this.rams;
    removeRams = this.rams & addAttacks;

/*
    if(DEBUG) System.err.println("Ubk-" + this + "  @  "  + attackerSquare.toString() + " on " + square +
            "\n  ATKS: " + board.getAllSquaresInBitboard(this.attacks) +
            "\n  RAMS: " + board.getAllSquaresInBitboard(this.rams) +
            "\n  AATS: " + board.getAllSquaresInBitboard(addAttacks) +
            "\n  ARMS: " + board.getAllSquaresInBitboard(addRams) +
            "\n  RRMS: " + board.getAllSquaresInBitboard(removeRams)
    );
*/

    this.attacks |= addAttacks;
    this.rams |= addRams;
    this.rams &= ~removeRams;
    attackerShift = ATTACKER_SHIFT[type];
    attackerUmask = ATTACKER_UMASK[type];
    while (addAttacks != 0) {
      squareIndex = Board.getLeastSignificantBit(addAttacks);
      mask = 1L << squareIndex;
      addAttacks &= ~mask;
      board.squareAttackers[squareIndex] |= attackerSquare.mask_on;
      board.attacks[color] |= mask;

      attackerCount = (board.attackState[color][squareIndex] >> attackerShift) & 3;
      board.attackState[color][squareIndex] = (board.attackState[color][squareIndex] & attackerUmask) |
                    ((attackerCount == 2 ? 3 : attackerCount + 1) << attackerShift);
    }

    while (addRams != 0) {
      squareIndex = Board.getLeastSignificantBit(addRams);
      Square attackSquare = Board.SQUARES[squareIndex];
      addRams &= attackSquare.mask_off;
      board.squareRammers[squareIndex] |= attackerSquare.mask_on;

      attackerCount = (board.attackState[color][squareIndex] >> attackerShift) & 3;
      board.attackState[color][squareIndex] = (board.attackState[color][squareIndex] & attackerUmask) |
                    ((attackerCount == 2 ? 3 : attackerCount + 1) << attackerShift);
    }

    while (removeRams != 0) {
      squareIndex = Board.getLeastSignificantBit(removeRams);
      mask = 1L << squareIndex;
      removeRams &= ~mask;
      board.squareRammers[squareIndex] &= attackerSquare.mask_off;

      attackerCount = (board.attackState[color][squareIndex] >> attackerShift) & 3;
      board.attackState[color][squareIndex] = (board.attackState[color][squareIndex] & attackerUmask) |
                    ((attackerCount == 0 ? 0 : attackerCount - 1) << attackerShift);
    }
  }
}


