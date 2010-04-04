/* $Id$ */

package chess.engine.model;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public class Piece implements Cloneable, Comparable<Piece>
{
  public static final boolean WHITE = true;
  public static final boolean BLACK = false;

  public static final int PAWN = 0;
  public static final int KNIGHT = 1;
  public static final int BISHOP = 2;
  public static final int ROOK = 3;
  public static final int QUEEN = 4;
  public static final int KING = 5;

  public static int[] TYPE_VALUES = { 100, 311, 323, 500, 900, 0 };
  public static int[] MATERIAL_VALUES = { 1, 3, 3, 5, 9, 0 };
  public static int[] MASKS = { 1, 2, 4, 8, 16, 32 };
  public static boolean[][] TYPE_INDEX =
          {
                  { true, false, false, false, false, false},
                  { false, true, true, false, false, false},
                  { true, true, true, false, false, false},
                  { false, false, false, true, false, false},
                  { false, false, false, false, true, false},
                  { false, false, false, false, false, false},
          };

  public int color;
  public int type;
  public Square square;
  public int value;
  public int materialValue;
  public Board board;

  public boolean kingsideRook = false;
  public boolean queensideRook = false;

  public int index;

  public int moveCount;

  public Piece(int index, Board board, int color, int type, Square square)
  {
    this.index = index;
    this.board = board;
    this.color = color;
    this.type = type;
    this.square = square;
    this.value = getValue();
    this.materialValue = getMaterialValue();

    if(type == ROOK)
    {
      if(square.file == Constants.FILE_A)
      {
        queensideRook = true;
      }
      else if(square.file == Constants.FILE_H)
      {
        kingsideRook = true;
      }
    }

    board.setPieceOnSquare(this, square);
    board.pieces[index] = this;

    if(type == KING)
    {
      if(color == 1)
      {
        board.whiteKing = this;
      }
      else
      {
        board.blackKing = this;
      }
    }
  }

  public int getValue()
  {
    return color == 1 ? TYPE_VALUES[type] : -TYPE_VALUES[type];
  }

  public int getMaterialValue()
  {
    return color == 1 ? MATERIAL_VALUES[type] : -MATERIAL_VALUES[type];
  }

  public boolean equals(Object o)
  {
    if(o instanceof Piece)
    {
      Piece piece = (Piece)o;

      return piece.type == type && piece.color == piece.color && piece.square == piece.square;
    }
    return false;
  }

  public String toString()
  {
    return toString(type, color);
  }

  public static String toString(int type, int color)
  {
    switch(type)
    {
      case PAWN :
      {
        return color == 1 ? "1" : "0";
      }
      case KNIGHT :
      {
        return color == 1 ? "N" : "n";
      }
      case BISHOP :
      {
        return color == 1 ? "B" : "b";
      }
      case ROOK :
      {
        return color == 1 ? "R" : "r";
      }
      case QUEEN :
      {
        return color == 1 ? "Q" : "q";
      }
      case KING :
      {
        return color == 1 ? "K" : "k";
      }
      default :
      {
        throw new RuntimeException("Piece has no type");
      }
    }
  }

  public int compareTo(Piece piece)
  {
    return piece.index > index ? 1 : -1;
  }
}
