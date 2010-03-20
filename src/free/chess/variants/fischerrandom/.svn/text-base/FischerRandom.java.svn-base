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

package free.chess.variants.fischerrandom;

import free.chess.*;


/**
 * Implements the Fischer Random wild variant. Quoting an ICC help file:
 * <PRE>
 * The usual set of pieces is arranged randomly
 * on the first and eighth ranks, with bishops on opposite colors, and the
 * king between the two rooks, and Black's arrangement a mirror of White's.
 * Castling O-O puts the king at g1 (g8 for Black), the rook at f1 (f8).
 * Castling O-O-O puts the king at c1 (c8), rook at d1 (d8). 
 * </PRE>
 * More information is available at
 * <A HREF="http://www.chessclub.com/help/Fischer-random">http://www.chessclub.com/help/Fischer-random</A>
 */

public class FischerRandom extends ChesslikeGenericVariant{



  /**
   * The sole instance of this class.
   */

  private static final FischerRandom instance = new FischerRandom();



  /**
   * Returns an instance of this class.
   */

  public static FischerRandom getInstance(){
    return instance;
  }



  /**
   * Creates an instance of FischerRandom.
   */

  private FischerRandom(){
    super(Chess.INITIAL_POSITION_FEN /* Not used anyway */, "Fischer Random");
  }




  /**
   * Returns <code>true</code> if the move defined by the given arguments is a 
   * short castling move according to the rules of the "fischer random" variant.
   * Returns <code>false</code> otherwise. The result for an illegal move is
   * undefined, but it should throw no exceptions.
   */

  public boolean isShortCastling(Position pos, Square startingSquare,
      Square endingSquare, ChessPiece promotionTarget){

    if (promotionTarget!=null)
      return false;

    ChessPiece movingPiece = (ChessPiece)pos.getPieceAt(startingSquare);
    ChessPiece takenPiece = (ChessPiece)pos.getPieceAt(endingSquare);

    if (movingPiece == ChessPiece.WHITE_KING){
      if (startingSquare.getRank()!=0)
        return false;
      else if ((takenPiece!=null)&&(takenPiece!=ChessPiece.WHITE_ROOK))
        return false;
      else if (!endingSquare.equals("g1"))
        return false;
      else if (startingSquare.equals("f1")){
        if (takenPiece == ChessPiece.WHITE_ROOK)
          return true;
        else
          return false; // This *could* be a castling, but there is currently 
                        // no way to indicate whether it is one.
      }

      int rank = startingSquare.getRank();
      int file = startingSquare.getFile()+1;
      while (file<=7){
        Piece piece = pos.getPieceAt(file, rank);
        if (piece != null){
          if (piece == ChessPiece.WHITE_ROOK){
            if ((takenPiece == ChessPiece.WHITE_ROOK)&&(!endingSquare.equals(file, rank)))
              return false;
            else
              return true;
          }
          else
            return false;
        }
        file++;
      }
    }
    else if (movingPiece == ChessPiece.BLACK_KING){
      if (startingSquare.getRank()!=7)
        return false;
      else if ((takenPiece!=null)&&(takenPiece!=ChessPiece.BLACK_ROOK))
        return false;
      else if (!endingSquare.equals("g8"))
        return false;
      else if (startingSquare.equals("f8")){
        if (takenPiece == ChessPiece.BLACK_ROOK)
          return true;
        else
          return false; // This *could* be a castling, but there is currently 
                        // no way to indicate whether it is one.
      }

      int rank = startingSquare.getRank();
      int file = startingSquare.getFile()+1;
      while (file<=7){
        Piece piece = pos.getPieceAt(file, rank);
        if (piece != null){
          if (piece == ChessPiece.BLACK_ROOK){
            if ((takenPiece == ChessPiece.BLACK_ROOK)&&(!endingSquare.equals(file, rank)))
              return false;
            else
              return true;
          }
          else
            return false;
        }
        file++;
      }
    }

    return false;
  }




  /**
   * Returns <code>true</code> if the move defined by the given arguments is a 
   * long castling move. Returns <code>false</code> otherwise. The result for
   * an illegal move is undefined, but it should throw no exceptions.
   */

  public boolean isLongCastling(Position pos, Square startingSquare,
      Square endingSquare, ChessPiece promotionTarget){

    if (promotionTarget!=null)
      return false;

    ChessPiece movingPiece = (ChessPiece)pos.getPieceAt(startingSquare);
    ChessPiece takenPiece = (ChessPiece)pos.getPieceAt(endingSquare);

    if (movingPiece == ChessPiece.WHITE_KING){
      if (startingSquare.getRank()!=0)
        return false;
      else if ((takenPiece!=null)&&(takenPiece!=ChessPiece.WHITE_ROOK))
        return false;
      else if (!endingSquare.equals("c1"))
        return false;
      else if (startingSquare.equals("b1"))
        return false; // This *could* be a castling, but there is currently 
                      // no way to indicate whether it is one.
      else if (startingSquare.equals("d1")){
        if (takenPiece == ChessPiece.WHITE_ROOK)
          return true;
        else
          return false; // This *could* be a castling, but there is currently 
                        // no way to indicate whether it is one.
      }

      int rank = startingSquare.getRank();
      int file = startingSquare.getFile()-1;
      while (file>=0){
        Piece piece = pos.getPieceAt(file, rank);
        if (piece != null){
          if (piece == ChessPiece.WHITE_ROOK){
            if ((takenPiece == ChessPiece.WHITE_ROOK)&&(!endingSquare.equals(file, rank)))
              return false;
            else
              return true;
          }
          else
            return false;
        }
        file--;
      }
    }
    else if (movingPiece == ChessPiece.BLACK_KING){
      if (startingSquare.getRank()!=7)
        return false;
      else if ((takenPiece!=null)&&(takenPiece!=ChessPiece.BLACK_ROOK))
        return false;
      else if (!endingSquare.equals("c8"))
        return false;
      else if (startingSquare.equals("b8"))
        return false; // This *could* be a castling, but there is currently 
                      // no way to indicate whether it is one.
      else if (startingSquare.equals("d8")){
        if (takenPiece == ChessPiece.BLACK_ROOK)
          return true;
        else
          return false; // This *could* be a castling, but there is currently 
                        // no way to indicate whether it is one.
      }

      int rank = startingSquare.getRank();
      int file = startingSquare.getFile()-1;
      while (file>=0){
        Piece piece = pos.getPieceAt(file, rank);
        if (piece != null){
          if (piece == ChessPiece.BLACK_ROOK){
            if ((takenPiece == ChessPiece.BLACK_ROOK)&&(!endingSquare.equals(file, rank)))
              return false;
            else
              return true;
          }
          else
            return false;
        }
        file--;
      }
    }

    return false;
  }



  /**
   * Creates a short castling move for the current player in the specified
   * position. Short castling must be legal in the specified position.
   */

  public Move createShortCastling(Position pos){
    checkPosition(pos);

    Player currentPlayer = pos.getCurrentPlayer();
    if (currentPlayer.isWhite()){
      Square startSquare = findPieceOnRow(pos, ChessPiece.WHITE_KING, 0);
      if (startSquare == null)
        throw new IllegalArgumentException("Castling is not allowed in the specified position");

      return new ChessMove(startSquare, Square.parseSquare("g1"), Player.WHITE_PLAYER,
                             false, true, false, null, null, "O-O");
    }
    else{
      Square startSquare = findPieceOnRow(pos, ChessPiece.BLACK_KING, 7);
      if (startSquare == null)
        throw new IllegalArgumentException("Castling is not allowed in the specified position");

      return new ChessMove(startSquare, Square.parseSquare("g8"), Player.BLACK_PLAYER,
                             false, true, false, null, null, "O-O");
    }
  }




  /**
   * Creates a long castling move for the current player in the specified
   * position. Long castling must be legal in the specified position.
   */

  public Move createLongCastling(Position pos){
    checkPosition(pos);

    Player currentPlayer = pos.getCurrentPlayer();
    if (currentPlayer.isWhite()){
      Square startSquare = findPieceOnRow(pos, ChessPiece.WHITE_KING, 0);
      if (startSquare == null)
        throw new IllegalArgumentException("Castling is not allowed in the specified position");

      return new ChessMove(startSquare, Square.parseSquare("c1"), Player.WHITE_PLAYER,
                             false, false, true, null, null, "O-O");
    }
    else{
      Square startSquare = findPieceOnRow(pos, ChessPiece.BLACK_KING, 7);
      if (startSquare == null)
        throw new IllegalArgumentException("Castling is not allowed in the specified position");

      return new ChessMove(startSquare, Square.parseSquare("c8"), Player.BLACK_PLAYER,
                             false, false, true, null, null, "O-O");
    }
  }



  /**
   * Returns the square of the specified piece on the specified row, or null if
   * the specified piece is not on the specified row.
   */

  private static Square findPieceOnRow(Position pos, Piece piece, int rank){
    for (int i = 0; i < 8; i++)
      if (piece.equals(pos.getPieceAt(i, rank)))
        return Square.getInstance(i, rank);

    return null;
  }



  /**
   * Initializes the given position to a random state subject to the constraints
   * specified in the rules.
   *
   * @throws IllegalArgumentException If the given Position's wild variant is
   * not FischerRandom.
   */

  public void init(Position pos){
    checkPosition(pos);

    pos.setFEN(createRandomInitialFEN());
  }




  /**
   * Creates a random initial position subject to the constraints specified in
   * the rules of Fischer Random. The position is encoded and returned in
   * FEN format.
   */

  private static String createRandomInitialFEN(){
    StringBuffer pieces = new StringBuffer("--------");

    int pos;

    // First bishop
    while(true){
      pos = randomInt(8);
      if ((pos%2 == 0) && (pieces.charAt(pos) == '-')){
        pieces.setCharAt(pos, 'B');
        break;
      }
    }

    // 2nd bishop
    while(true){
      pos = randomInt(8);
      if ((pos%2 == 1) && (pieces.charAt(pos) == '-')){
        pieces.setCharAt(pos, 'B');
        break;
      }
    } 

    // 1st knight
    while (true){
      pos = randomInt(8);
      if (pieces.charAt(pos) == '-'){
        pieces.setCharAt(pos, 'N');
        break;
      }
    }

    // 2nd knight
    while (true){
      pos = randomInt(8);
      if (pieces.charAt(pos) == '-'){
        pieces.setCharAt(pos, 'N');
        break;
      }
    }

    // queen
    while (true){
      pos = randomInt(8);
      if (pieces.charAt(pos) == '-'){
        pieces.setCharAt(pos, 'Q');
        break;
      }
    }
    
    // 1st rook
    pos = 0;
    while (pos < 6){
      if (pieces.charAt(pos) == '-')
        break;
      pos++;
    }
    pieces.setCharAt(pos++, 'R');

    // king
    while (pos < 7){
      if (pieces.charAt(pos) == '-')
        break;
      pos++;
    }
    pieces.setCharAt(pos++, 'K');

    // 2nd rook
    while (pos < 8){
      if (pieces.charAt(pos)=='-')
        break;
      pos++;
    }
    pieces.setCharAt(pos, 'R');

    String whitePieces = pieces.toString();
    String blackPieces = whitePieces.toLowerCase();

    return blackPieces + "/pppppppp/8/8/8/8/PPPPPPPP/" + whitePieces + " w KQkq - 0 1";
  }




  /**
   * Returns a random int, in the range [0..max).  This method is used
   * when creating the initial, random position.
   */

  private static int randomInt(int max){
    return (int)(Math.random()*max);
  }



  /**
   * Makes the given ChessMove on the given position.
   */

  public void makeMove(Move move, Position pos, Position.Modifier modifier){
    checkPosition(pos);

    if (!(move instanceof ChessMove))
      throw new IllegalArgumentException("The given move must be an instance of "+ChessMove.class.getName());

    ChessMove cmove = (ChessMove)move;
    Square startingSquare = cmove.getStartingSquare();
    Square endingSquare = cmove.getEndingSquare();
    ChessPiece movingPiece = (ChessPiece)pos.getPieceAt(startingSquare);

    if (cmove.isCastling()){
      int dir = cmove.isShortCastling() ? 1 : -1;
      int file = startingSquare.getFile() + dir;
      int rank = startingSquare.getRank();

      while ((file >= 0) && (file <= 7)){
        ChessPiece piece = (ChessPiece)pos.getPieceAt(file, rank);
        if (piece!=null){
          if (!piece.isRook() || !piece.isSameColorAs(movingPiece))
            throw new IllegalArgumentException("The given move may not be a castling move");
          else
            break;
        }
        file+=dir;
      }

      int rookStartFile = file;
      int rookEndFile = cmove.isShortCastling() ? 5 : 3;

      Square rookStartingSquare = Square.getInstance(rookStartFile, startingSquare.getRank());
      Square rookEndingSquare = Square.getInstance(rookEndFile, startingSquare.getRank());
      ChessPiece rook = (ChessPiece)pos.getPieceAt(rookStartingSquare);

      modifier.setPieceAt(null, startingSquare);
      modifier.setPieceAt(null, rookStartingSquare);

      modifier.setPieceAt(movingPiece, endingSquare);
      modifier.setPieceAt(rook, rookEndingSquare);

      modifier.setCurrentPlayer(pos.getCurrentPlayer().getOpponent());
    }
    else
      super.makeMove(move, pos, modifier);
  }

}
