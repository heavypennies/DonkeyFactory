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

package free.chess.variants;

import free.chess.*;


/**
 * Implements a wild variant which differs from regular chess in that kings can
 * be initially placed on the d file in which case short castling refers to
 * moving the king to the b file and the rook to the c file.
 * This class extends {@link ChesslikeGenericVariant} without defining the
 * initial position and name, so this is still a generic variant.
 */

public class BothSidesCastlingVariant extends ChesslikeGenericVariant{



  /**
   * Creates a new instance of <code>BothSideCastlingVariant</code> with the
   * specified initial position and name.
   */

  public BothSidesCastlingVariant(String initialPositionFEN, String variantName){
    super(initialPositionFEN, variantName);
  }




  /**
   * Returns true if a move from the specified starting square to the specified
   * ending square is a short castling. This variant allows castling by kings
   * from the d1 and d8 to the b1 and b8 SQUARES (respectively) as well.
   */

  public boolean isShortCastling(Position pos, Square startSquare,
      Square endSquare, ChessPiece promotionTarget){
    
    if (promotionTarget != null)
      return false;

    ChessPiece movingPiece = (ChessPiece)pos.getPieceAt(startSquare);
    ChessPiece takenPiece = (ChessPiece)pos.getPieceAt(endSquare);

    if (takenPiece != null)
      return false;

    if (movingPiece == ChessPiece.WHITE_KING){
      if (startSquare.equals("e1") && 
          endSquare.equals("g1") && 
          (pos.getPieceAt("h1") == ChessPiece.WHITE_ROOK) &&
          (pos.getPieceAt("f1") == null))
        return true;

      if (startSquare.equals("d1") && 
          endSquare.equals("b1") && 
          (pos.getPieceAt("a1") == ChessPiece.WHITE_ROOK) &&
          (pos.getPieceAt("c1") == null))
        return true;
    }
    else if (movingPiece == ChessPiece.BLACK_KING){
      if (startSquare.equals("e8") && 
          endSquare.equals("g8") && 
          (pos.getPieceAt("h8") == ChessPiece.BLACK_ROOK) &&
          (pos.getPieceAt("f8") == null))
        return true;

      if (startSquare.equals("d8") && 
          endSquare.equals("b8") && 
          (pos.getPieceAt("a8") == ChessPiece.BLACK_ROOK) &&
          (pos.getPieceAt("c8") == null))
        return true;
    }

    return false;
  }





  /**
   * Returns <code>true</code> if a move from the specified starting square to
   * the specified ending square is a short castling. This variant allows
   * castling by kings from the d1 and d8 to the f1 and f8 SQUARES
   * (respectively) as well.
   */

  public boolean isLongCastling(Position pos, Square startSquare,
      Square endSquare, ChessPiece promotionTarget){
    
    if (promotionTarget != null)
      return false;

    ChessPiece movingPiece = (ChessPiece)pos.getPieceAt(startSquare);
    ChessPiece takenPiece = (ChessPiece)pos.getPieceAt(endSquare);

    if (takenPiece != null)
      return false;

    if (movingPiece == ChessPiece.WHITE_KING){
      if (startSquare.equals("e1") && 
          endSquare.equals("c1") && 
          (pos.getPieceAt("a1") == ChessPiece.WHITE_ROOK) &&
          (pos.getPieceAt("b1") == null) &&
          (pos.getPieceAt("d1") == null))
        return true;

      if (startSquare.equals("d1") && 
          endSquare.equals("f1") && 
          (pos.getPieceAt("h1") == ChessPiece.WHITE_ROOK) &&
          (pos.getPieceAt("e1") == null) &&
          (pos.getPieceAt("g1") == null))
        return true;

      return false;
    }
    else if (movingPiece == ChessPiece.BLACK_KING){
      if (startSquare.equals("e8") && 
          endSquare.equals("c8") && 
          (pos.getPieceAt("a8") == ChessPiece.BLACK_ROOK) &&
          (pos.getPieceAt("b8") == null) &&
          (pos.getPieceAt("d8") == null))
        return true;

      if (startSquare.equals("d8") && 
          endSquare.equals("f8") && 
          (pos.getPieceAt("h8") == ChessPiece.BLACK_ROOK) &&
          (pos.getPieceAt("e8") == null) &&
          (pos.getPieceAt("g8") == null)){
        return true;
      }

      return false;
    }
    else
      return false;
  }




  /**
   * Makes the given ChessMove on the given position.
   */

  public void makeMove(Move move, Position pos, Position.Modifier modifier){
    checkPosition(pos);

    if (!(move instanceof ChessMove))
      throw new IllegalArgumentException("The given move must be an instance of "+ChessMove.class.getName());

    ChessMove cmove = (ChessMove)move;
    Square startSquare = cmove.getStartingSquare();
    Square endSquare = cmove.getEndingSquare();
    ChessPiece movingPiece = (ChessPiece)pos.getPieceAt(startSquare);

    if (cmove.isCastling()){
      int dir = cmove.isShortCastling()^(startSquare.getFile() == 3) ? 1 : -1;

      int rookStartFile = dir == -1 ? 0 : 7;

      Square rookStartSquare = Square.getInstance(rookStartFile, startSquare.getRank());
      Square rookEndSquare = Square.getInstance(endSquare.getFile() - dir , startSquare.getRank());
      ChessPiece rook = (ChessPiece)pos.getPieceAt(rookStartSquare);

      modifier.setPieceAt(null, startSquare);
      modifier.setPieceAt(null, rookStartSquare);

      modifier.setPieceAt(movingPiece, endSquare);
      modifier.setPieceAt(rook, rookEndSquare);

      modifier.setCurrentPlayer(pos.getCurrentPlayer().getOpponent());
    }
    else
      super.makeMove(move, pos, modifier);
  }



  /**
   * Creates a short castling move for the current player in the specified
   * position.
   */

  public Move createShortCastling(Position pos){
    checkPosition(pos);

    Player currentPlayer = pos.getCurrentPlayer();
    if (currentPlayer.isWhite()){
      if (pos.getPieceAt(Square.parseSquare("e1")) == ChessPiece.WHITE_KING)
        return WHITE_SHORT_CASTLING;
      else if (pos.getPieceAt(Square.parseSquare("d1")) == ChessPiece.WHITE_KING)
        return new ChessMove(Square.parseSquare("d1"), Square.parseSquare("b1"), Player.WHITE_PLAYER,
                             false, true, false, null, null, "O-O");
      else throw new IllegalArgumentException("Castling is not allowed in the specified position");
    }
    else{
      if (pos.getPieceAt(Square.parseSquare("e8")) == ChessPiece.BLACK_KING)
        return BLACK_SHORT_CASTLING;
      else if (pos.getPieceAt(Square.parseSquare("d8")) == ChessPiece.BLACK_KING)
        return new ChessMove(Square.parseSquare("d8"), Square.parseSquare("b8"), Player.BLACK_PLAYER,
                             false, true, false, null, null, "O-O");
      else throw new IllegalArgumentException("Castling is not allowed in the specified position");
    }
  }




  /**
   * Creates a long castling move for the current player in the specified
   * position.
   */

  public Move createLongCastling(Position pos){
    checkPosition(pos);

    Player currentPlayer = pos.getCurrentPlayer();
    if (currentPlayer.isWhite()){
      if (pos.getPieceAt(Square.parseSquare("e1")) == ChessPiece.WHITE_KING)
        return WHITE_LONG_CASTLING;
      else if (pos.getPieceAt(Square.parseSquare("d1")) == ChessPiece.WHITE_KING)
        return new ChessMove(Square.parseSquare("d1"), Square.parseSquare("f1"), Player.WHITE_PLAYER,
                             false, false, true, null, null, "O-O-O");
      else throw new IllegalArgumentException("Castling is not allowed in the specified position");
    }
    else{
      if (pos.getPieceAt(Square.parseSquare("e8")) == ChessPiece.BLACK_KING)
        return BLACK_LONG_CASTLING;
      else if (pos.getPieceAt(Square.parseSquare("d8")) == ChessPiece.BLACK_KING)
        return new ChessMove(Square.parseSquare("d8"), Square.parseSquare("f8"), Player.BLACK_PLAYER,
                             false, false, true, null, null, "O-O-O");
      else throw new IllegalArgumentException("Castling is not allowed in the specified position");
    }
  }


}
