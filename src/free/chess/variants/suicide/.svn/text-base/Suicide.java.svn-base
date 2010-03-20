/**
 * The chess framework library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2003 Alexander Maryanovsky.
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

package free.chess.variants.suicide;

import free.chess.*;
import free.chess.variants.NoCastlingVariant;


/**
 * Implements the "Suicide" wild variant. The "Suicide" variant is just like
 * "Giveaway" on ICC, but castling is not allowed.
 */

public class Suicide extends NoCastlingVariant{



  /**
   * An array containing WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT,
   * WHITE_PAWN and WHITE_KING. These are the pieces to which a white pawn can
   * be promoted to in Suicide.
   */

  private static final ChessPiece [] whitePromotionTargets = 
    new ChessPiece[]{ChessPiece.WHITE_QUEEN, ChessPiece.WHITE_ROOK, ChessPiece.WHITE_BISHOP, ChessPiece.WHITE_KNIGHT, ChessPiece.WHITE_PAWN, ChessPiece.WHITE_KING};



  /**
   * An array containing BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT,
   * BLACK_PAWN and BLACK_KING. These are the pieces to which a black pawn can
   * be promoted to in Suicide.
   */

  private static final ChessPiece [] blackPromotionTargets = 
    new ChessPiece[]{ChessPiece.BLACK_QUEEN, ChessPiece.BLACK_ROOK, ChessPiece.BLACK_BISHOP, ChessPiece.BLACK_KNIGHT, ChessPiece.BLACK_PAWN, ChessPiece.BLACK_KING};




  /**
   * The sole instance of this class.
   */

  private static final Suicide instance = new Suicide();



  /**
   * Returns an instance of this class.
   */

  public static Suicide getInstance(){
    return instance;
  }



  /**
   * Creates an instance of Suicide.
   */

  private Suicide(){
    super(Chess.INITIAL_POSITION_FEN, "Suicide");
  }




  /**
   * <P>If the a move created by the given starting square and ending square in
   * the given position is a promotion, returns an array containing a king,
   * pawn, knight, bishop, rook queen of the color of the promoted pawn.
   * Otherwise returns <code>null</code>.
   */

  public Piece [] getPromotionTargets(Position pos, Square startingSquare, Square endingSquare){
    checkPosition(pos);

    ChessPiece movingPiece = (ChessPiece)pos.getPieceAt(startingSquare);

    if ((endingSquare.getRank() == 7) && (movingPiece == ChessPiece.WHITE_PAWN))
      return (Piece [])whitePromotionTargets.clone();

    if ((endingSquare.getRank() == 0) && (movingPiece == ChessPiece.BLACK_PAWN))
      return (Piece [])blackPromotionTargets.clone();

    return null;
  }


}
