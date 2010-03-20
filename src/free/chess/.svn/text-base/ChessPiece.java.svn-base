/**
 * The chess framework library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The chess framework library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The chess framework library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the chess framework library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package free.chess;


/**
 * Represents a "soldier" in the game of chess - a chess piece.
 * This class does not allow to create instances of itself, instead it
 * defines constants that represent each chess piece. This means that two
 * Pieces are the same piece only if they reference the same object.
 * An empty square is represented by null.
 */

public class ChessPiece extends Piece{



  /**
   * A constant representing a pawn.
   */

  protected static final int PAWN = 1;



  /**
   * A constant representing a knight.
   */

  protected static final int KNIGHT = 2;




  /**
   * A constant representing a bishop.
   */
                                        
  protected static final int BISHOP = 3;



  /**
   * A constant representing a rook.
   */

  protected static final int ROOK = 4;



  /**
   * A constant representing a queen.
   */

  protected static final int QUEEN = 5;



  /**
   * A constant representing a king.
   */

  protected static final int KING = 6;




  // Final objects representing each piece.

  public static final ChessPiece WHITE_PAWN   = new ChessPiece(WHITE,PAWN);
  public static final ChessPiece WHITE_KNIGHT = new ChessPiece(WHITE,KNIGHT);
  public static final ChessPiece WHITE_BISHOP = new ChessPiece(WHITE,BISHOP);
  public static final ChessPiece WHITE_ROOK   = new ChessPiece(WHITE,ROOK);
  public static final ChessPiece WHITE_QUEEN  = new ChessPiece(WHITE,QUEEN);
  public static final ChessPiece WHITE_KING   = new ChessPiece(WHITE,KING);
  
  public static final ChessPiece BLACK_PAWN   = new ChessPiece(BLACK,PAWN);
  public static final ChessPiece BLACK_KNIGHT = new ChessPiece(BLACK,KNIGHT);
  public static final ChessPiece BLACK_BISHOP = new ChessPiece(BLACK,BISHOP);
  public static final ChessPiece BLACK_ROOK   = new ChessPiece(BLACK,ROOK);
  public static final ChessPiece BLACK_QUEEN  = new ChessPiece(BLACK,QUEEN);
  public static final ChessPiece BLACK_KING   = new ChessPiece(BLACK,KING);





  /**
   * Creates a ChessPiece of the given color and type.
   * Possible values for both type and color are defined in this class.
   * 
   * @param color The color of the piece, either Piece.WHITE or Piece.BLACK
   * @param type The type of the piece (Piece.KNIGHT, Piece.BISHOP etc.)
   */

  private ChessPiece(int color, int type){
    super(color, type);
  }




  /**
   * Returns the ChessPiece corresponding to the given string. The string is expected
   * to be in the format returned by the {@link #toShortString()} method.
   */

  public static ChessPiece fromShortString(String piece){
    if (piece.length()!=1)
      throw new IllegalArgumentException("The given string must be exactly one character long.");
    
    char c = piece.charAt(0);

    switch(c){
     case '-': return null;
     case 'P': return ChessPiece.WHITE_PAWN;
     case 'N': return ChessPiece.WHITE_KNIGHT;
     case 'B': return ChessPiece.WHITE_BISHOP;
     case 'R': return ChessPiece.WHITE_ROOK;
     case 'Q': return ChessPiece.WHITE_QUEEN;
     case 'K': return ChessPiece.WHITE_KING;
     case 'p': return ChessPiece.BLACK_PAWN;
     case 'n': return ChessPiece.BLACK_KNIGHT;
     case 'b': return ChessPiece.BLACK_BISHOP;
     case 'r': return ChessPiece.BLACK_ROOK;
     case 'q': return ChessPiece.BLACK_QUEEN;
     case 'k': return ChessPiece.BLACK_KING;
    }

    throw new IllegalArgumentException("Unknown piece: "+piece);
  }




  /**
   * Returns <code>true</code> if this chess piece is of the same color as the
   * given piece.
   */

  public final boolean isSameColorAs(ChessPiece otherPiece){
    return this.val*otherPiece.val>0;
  }




  /**
   * Returns <code>true</code> if this chess piece is of the same type (rook,
   * queen, etc.) as the given piece.
   */

  public final boolean isSameTypeAs(ChessPiece otherPiece){
    return Math.abs(this.val)==Math.abs(otherPiece.val);
  }




  /**
   * Returns true if this Piece is a pawn, returns false otherwise.
   */

  public final boolean isPawn(){
    return (this==WHITE_PAWN)||(this==BLACK_PAWN);
  }






  /**
   * Returns true if this Piece is a knight, returns false otherwise.
   */

  public final boolean isKnight(){
    return (this==WHITE_KNIGHT)||(this==BLACK_KNIGHT);
  }






  /**
   * Returns true if this Piece is a bishop, returns false otherwise.
   */

  public final boolean isBishop(){
    return (this==WHITE_BISHOP)||(this==BLACK_BISHOP);
  }






  /**
   * Returns true if this Piece is a rook, returns false otherwise.
   */

  public final boolean isRook(){
    return (this==WHITE_ROOK)||(this==BLACK_ROOK);
  }





  /**
   * Returns true if this Piece is a queen, returns false otherwise.
   */

  public final boolean isQueen(){
    return (this==WHITE_QUEEN)||(this==BLACK_QUEEN);
  }






  /**
   * Returns true if this Piece is a king, returns false otherwise.
   */

  public final boolean isKing(){
    return (this==WHITE_KING)||(this==BLACK_KING);
  }





  /**
   * Returns a short (notational) string representing this chess piece ("N"
   * for a white knight for example, and "P" for a black pawn).
   *
   * @return a short string representing this piece.
   *
   * @see #fromShortString(String)
   */

  public String toShortString(){
    if (isPawn())
      return "P";
    else if (isKnight())
      return "N";
    else if (isBishop())
      return "B";
    else if (isRook())
      return "R";
    else if (isQueen())
      return "Q";
    else if (isKing())
      return "K";

    throw new Error("This may never happen");
  }





  /**
   * Returns a string representing the type of this piece ("Knight" for a
   * knight for example).
   */

  public String getTypeName(){
    if (isPawn())
      return "Pawn";
    else if (isKnight())
      return "Knight";
    else if (isBishop())
      return "Bishop";
    else if (isRook())
      return "Rook";
    else if (isQueen())
      return "Queen";
    else if (isKing())
      return "King";

    throw new Error("This may never happen");
  }



}
