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

package free.chess.variants;

import free.chess.*;


/**
 * Implements a wild variant which differs from regular chess in that castling
 * is not allowed. This class extends
 * {@link ChesslikeGenericVariant} without defining the initial
 * position and name, so this is still a generic variant.
 */

public class NoCastlingVariant extends ChesslikeGenericVariant{


  /**
   * Creates a new instance of NoCastlingVariant with the given initial
   * position and name.
   */

  public NoCastlingVariant(String initialPositionFEN, String variantName){
    super(initialPositionFEN, variantName);
  }




  /**
   * Returns false, as this is a variant which disallows castling.
   */

  public boolean isShortCastling(Position pos, Square startingSquare,
      Square endingSquare, ChessPiece promotionTarget){
    
    return false;
  }





  /**
   * Returns false, as this is a variant which disallows castling.
   */

  public boolean isLongCastling(Position pos, Square startingSquare,
      Square endingSquare, ChessPiece promotionTarget){
    
    return false;
  }



  /**
   * Always throws <code>UnsupportedOperationException</code>.
   */

  public Move createShortCastling(Position pos){
    throw new free.util.UnsupportedOperationException("Can't castle");
  }




  /**
   * Always throws <code>UnsupportedOperationException</code>.
   */

  public Move createLongCastling(Position pos){
    throw new free.util.UnsupportedOperationException("Can't castle");
  }


}
