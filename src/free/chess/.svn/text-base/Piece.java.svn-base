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
 * The superclass of all classes representing pieces in wild chess variants.
 */

public abstract class Piece{


  /**
   * A constant representing a white piece.
   */

  public static final int WHITE = 1;



  /**
   * A constant representing a black piece.
   */

  public static final int BLACK = -1;




  /**
   * An integer representing the piece. Negative values are black pieces and
   * positive are white pieces. The absolute value of this variable is the
   * type of the piece. Thus, the value of this integer is color*type;
   */

  protected final int val;




  /**
   * Creates a Piece of the given color and type.
   * 
   * @param color The color of the piece, either Piece.WHITE or Piece.BLACK
   * @param type The type of the piece, 0 is not allowed.
   */

  protected Piece(int color, int type){
    switch(color){
      case WHITE:
      case BLACK:
        break;
      default:
        throw new IllegalArgumentException("Unknown color constant: "+color);
    }
    if (type == 0)
      throw new IllegalArgumentException("Piece type may not be 0");
      
    this.val = color*type;
  }





  /**
   * Returns true if the given Piece has opposite color from
   * this piece.
   */

  public boolean isOppositeColor(Piece piece){
    return this.getColor() != piece.getColor();
  }




  /**
   * Returns trie of the given Piece has the same color as this Piece.
   */

  public boolean isSameColor(Piece piece){
    return this.getColor() == piece.getColor();
  }





  /**
   * Returns the <code>Player</code> this piece belongs to.
   */

  public Player getPlayer(){
    if (isWhite())
      return Player.WHITE_PLAYER;
    else
      return Player.BLACK_PLAYER;
  }





  /**
   * Returns the color of this Piece, either {@link #BLACK} or {@link #WHITE}.
   */

  public int getColor(){
    return val<0 ? BLACK : WHITE;
  }





  /**
   * Returns true if this Piece is white, returns false otherwise.
   */

  public boolean isWhite(){
    return (val>0);
  }



                                

  /**
   * Returns true if this Piece is black, returns false otherwise.
   */

  public boolean isBlack(){
    return (val<0);
  }





  /**
   * Returns a short (notational) string representing this chess piece ("N"
   * for a knight for example). Black and white pieces should be represented
   * by the same string. To differentiate between black and white pieces, use
   * the <code>toShortColorString</code> method.
   *
   * @return a short string representing this piece.
   */

  public abstract String toShortString();




  /**
   * Returns the same as toShortString() only lowercase for black pieces
   * and uppercase for white ones.
   */

  public String toShortColorString(){
    if (isWhite())
      return toShortString().toUpperCase();
    else
      return toShortString().toLowerCase();
  }





  /**
   * Returns a string representing the color of this piece, "White" for a
   * white piece and "Black" for a black piece. Returns "Colorless" for the empty
   * piece.
   */

  public String getColorName(){
    if (isWhite())
      return "White";
    else if (isBlack())
      return "Black";

    throw new Error("This may never happen");
  }





  /**
   * Returns a string representing the type of this piece ("Knight" for a
   * knight for example).
   */

  public abstract String getTypeName();




  /**
   * Returns a string representation of this chess piece. Returns "Empty" for
   * the empty piece.
   */

  public String toString(){
    return getColorName()+' '+getTypeName();
  }




  /**
   * Returns the hashcode of this Piece.
   */

  public int hashCode(){
    return val;
  }



  /**
   * Returns true if the given Piece is the same as this one.
   * This returns true if and only if the type (class) of this piece is the same
   * as the type of the given piece and their "piece" values are the same.
   * Subclasses implementing singleton pieces should override this and compare by
   * reference.
   */

  public boolean equals(Object o){
    if (o == null)
      return false;

    if (o.getClass() != this.getClass())
      return false;

    return val==((Piece)o).val;
  }


}
