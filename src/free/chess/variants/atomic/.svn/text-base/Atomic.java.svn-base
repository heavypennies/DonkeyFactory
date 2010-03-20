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

package free.chess.variants.atomic;

import free.chess.*;


/**
 * Implements the "atomic" wild variant. Quoting an ICC help file:
 * <PRE>
 * Atomic chess (wild 27) is a chess variant in which any capture
 * also destroys the capturing piece (or pawn) and any piece
 * (but NOT pawns) in a one square radius (horizontal, vertical,
 * or diagonal).  There is no chain reaction -- only direct
 * captures detonate.  For captures en passant, ground-zero of
 * the explosion is the square on the sixth rank upon which
 * the capturing pawn lands.
 *
 * Win by capturing or destroying your opponent's king without
 * simultaneously destroying your own king.  You may not make a
 * move which  destroys your own king.  Check and checkmate are
 * not recognized; you may move into check, castle out of check,
 * or castle through check.
 * </PRE>
 */

public class Atomic extends ChesslikeGenericVariant{



  /**
   * The sole instance of this class.
   */

  private static final Atomic instance = new Atomic();



  /**
   * Returns an instance of this class.
   */

  public static Atomic getInstance(){
    return instance;
  }



  /**
   * Creates an instance of Giveaway.
   */

  private Atomic(){
    super(Chess.INITIAL_POSITION_FEN, "Atomic");
  }





  /**
   * <P>Makes the given ChessMove in the given Position. <B>This method
   * shoudln't (and can't) be called directly - call
   * {@link Position#makeMove(Move)} instead.</B>
   *
   * @throws IllegalArgumentException if the given Move is not an instance of
   * <code>ChessMove</code>.
   */

  public void makeMove(Move move, Position pos, Position.Modifier modifier){
    checkPosition(pos); 

    if (!(move instanceof ChessMove))
      throw new IllegalArgumentException("Wrong piece type: "+move.getClass());

    ChessMove cmove = (ChessMove)move;
    Square endingSquare = cmove.getEndingSquare();

    super.makeMove(cmove, pos, modifier);

    if (cmove.isCapture()){
      clearSquare(endingSquare.getFile()+1, endingSquare.getRank()+1, pos, modifier);
      clearSquare(endingSquare.getFile()+1, endingSquare.getRank(), pos, modifier);
      clearSquare(endingSquare.getFile()+1, endingSquare.getRank()-1, pos, modifier);
      clearSquare(endingSquare.getFile(), endingSquare.getRank()+1, pos, modifier);
      clearSquare(endingSquare.getFile(), endingSquare.getRank()-1, pos, modifier);
      clearSquare(endingSquare.getFile()-1, endingSquare.getRank()+1, pos, modifier);
      clearSquare(endingSquare.getFile()-1, endingSquare.getRank(), pos, modifier);
      clearSquare(endingSquare.getFile()-1, endingSquare.getRank()-1, pos, modifier);

      modifier.setPieceAt(null, endingSquare);
    }
  }





  /**
   * If the given file and rank represent a valid square and that square does
   * not contain a pawn, that square is cleared.
   */

  private void clearSquare(int file, int rank, Position pos, Position.Modifier modifier){
    if ((file<0) || (file>7) || (rank<0) || (rank>7))
      return;

    Square square = Square.getInstance(file, rank);
    ChessPiece piece = (ChessPiece)pos.getPieceAt(square);
    if ((piece!=null) && !piece.isPawn())
      modifier.setPieceAt(null, square);
  }

}
