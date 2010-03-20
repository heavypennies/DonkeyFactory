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

package free.chess.variants.giveaway;

import free.chess.*;


/**
 * Implements the "giveaway" wild variant. Quoting an ICC help file:
 * <PRE>
 * You must capture if possible.  The king plays no special
 * role and can be captured or left en prise.  Pawns can promote to king.
 * Win by losing all your pieces or getting stalemated.  See also wild 17,
 * Losers Chess.
 * </PRE>
 */

public class Giveaway extends ChesslikeGenericVariant{



  /**
   * An array containing WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT
   * and WHITE_KING. These are the pieces to which a white pawn can be promoted
   * to in Giveaway.
   */

  private static final ChessPiece [] whitePromotionTargets = 
    new ChessPiece[]{ChessPiece.WHITE_QUEEN, ChessPiece.WHITE_ROOK, 
                     ChessPiece.WHITE_BISHOP, ChessPiece.WHITE_KNIGHT, ChessPiece.WHITE_KING};



  /**
   * An array containing BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT
   * and BLACK_KING. These are the pieces to which a black pawn can be promoted
   * to in Giveaway.
   */

  private static final ChessPiece [] blackPromotionTargets = 
    new ChessPiece[]{ChessPiece.BLACK_QUEEN, ChessPiece.BLACK_ROOK, 
                     ChessPiece.BLACK_BISHOP, ChessPiece.BLACK_KNIGHT, ChessPiece.BLACK_KING};




  /**
   * The sole instance of this class.
   */

  private static final Giveaway instance = new Giveaway();



  /**
   * Returns an instance of this class.
   */

  public static Giveaway getInstance(){
    return instance;
  }



  /**
   * Creates an instance of Giveaway.
   */

  private Giveaway(){
    super(Chess.INITIAL_POSITION_FEN, "Giveaway");
  }




  /**
   * <P>If the a move created by the given starting square and ending square in
   * the given position is a promotion, returns an array containing a knight,
   * bishop, rook and queen of the color of the promoted pawn. Otherwise
   * returns null.
   */

  public Piece [] getPromotionTargets(Position pos, Square startingSquare, Square endingSquare){
    checkPosition(pos);

    ChessPiece movingPiece = (ChessPiece)pos.getPieceAt(startingSquare);

    if ((endingSquare.getRank()==7)&&(movingPiece==ChessPiece.WHITE_PAWN))
      return (Piece [])whitePromotionTargets.clone();

    if ((endingSquare.getRank()==0)&&(movingPiece==ChessPiece.BLACK_PAWN))
      return (Piece [])blackPromotionTargets.clone();

    return null;
  }


}
