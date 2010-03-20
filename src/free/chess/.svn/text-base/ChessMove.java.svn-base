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
 * An extension of the Move class for variants which have rules similar to 
 * regular chess. Mainly, it just defines the existance of concepts such as
 * en-passant, short and long castling, captures and promotion. It provides two
 * constructors, one which lets the caller to specify all the properties and
 * another one which determines these properties by using the stardard chess
 * rules.
 */

public class ChessMove extends Move{ 



  /**
   * True if this move is an en-passant, false otherwise.
   */

  private final boolean isEnPassant;




  /**
   * True if this move is a short castling move, false otherwise.
   */

  private final boolean isShortCastling;




  /**
   * True if this move is a long castling move, false otherwise.
   */

  private final boolean isLongCastling;



  /**
   * The captured piece, null if this move is not a capture.
   */

  private final ChessPiece capturedPiece;




  /**
   * The piece to which the moving pawn was promoted.
   */

  private final ChessPiece promotionTarget;



  
  /**
   * Creates a new ChessMove with the given properties. If the move is not a
   * promotion, the promotion target should be <code>null</code>. The
   * <code>moveSAN</code> (the move in SAN format) argument may be
   * <code>null</code>.
   */

  public ChessMove(Square startingSquare, Square endingSquare, Player movingPlayer,
      boolean isEnPassant, boolean isShortCastling, boolean isLongCastling,
      ChessPiece capturedPiece, ChessPiece promotionTarget, String moveSAN){

    super(startingSquare, endingSquare, movingPlayer, moveSAN);

    if (startingSquare == null)
      throw new IllegalArgumentException("Starting square may not be null");
    if (endingSquare == null)
      throw new IllegalArgumentException("Ending square may not be null");

    this.promotionTarget = promotionTarget;
    this.isEnPassant = isEnPassant;
    this.isShortCastling = isShortCastling;
    this.isLongCastling = isLongCastling;
    this.capturedPiece = capturedPiece;
  }





  /**
   * Creates a new ChessMove from the given properties. The missing information
   * (en-passant, castling and other information provided by the more complete
   * constructor) is determined according to the rules of standard chess.
   * The <code>moveSAN</code> (the move in SAN format) argument may be
   * <code>null</code>.
   *
   * @throws IllegalArgumentException If the there is no piece at the starting
   * square.
   */

  public ChessMove(Position pos, Square startingSquare, Square endingSquare, 
      ChessPiece promotionTarget, String moveSAN){
    
    super(startingSquare, endingSquare, pos.getCurrentPlayer(), moveSAN);

    if (startingSquare == null)
      throw new IllegalArgumentException("The starting square may not be null");

    if (endingSquare == null)
      throw new IllegalArgumentException("The ending square may not be null");

    if (pos.getPieceAt(startingSquare) == null)
      throw new IllegalArgumentException("The moving piece may not be null");

    this.promotionTarget = promotionTarget;
    this.isEnPassant = Chess.getInstance().isEnPassant(pos, startingSquare, endingSquare, promotionTarget);
    this.isShortCastling = Chess.getInstance().isShortCastling(pos, startingSquare, endingSquare, promotionTarget);
    this.isLongCastling = Chess.getInstance().isLongCastling(pos, startingSquare, endingSquare, promotionTarget);
    this.capturedPiece = Chess.getInstance().getCapturedPiece(pos, startingSquare, endingSquare, promotionTarget, isEnPassant);
  }





  /**
   * Returns a string representing this move in the notation invented by
   * Warren Smith. Refer to <A HREF="http://www.chessclub.com/chessviewer/smith.html">http://www.chessclub.com/chessviewer/smith.html</A>
   * for a description of the notation.
   *
   * @return a string representing this move in the Warren Smith notation.
   */

  public String getWarrenSmithString(){
    StringBuffer buf = new StringBuffer();
    buf.append(startingSquare.toString());
    buf.append(endingSquare.toString());

    if (isEnPassant())
      buf.append('E');
    else if (isShortCastling())
      buf.append('c');
    else if (isLongCastling())
      buf.append('C');
    else if (isCapture()){
      ChessPiece capturedPiece = getCapturedPiece();
      if (capturedPiece.isPawn())
        buf.append('p');
      else if (capturedPiece.isKnight())
        buf.append('n');
      else if (capturedPiece.isBishop())
        buf.append('b');
      else if (capturedPiece.isRook())
        buf.append('r');
      else if (capturedPiece.isQueen())
        buf.append('q');
      else if (capturedPiece.isKing())
        buf.append('k');
    }

    if (isPromotion()){
      ChessPiece promotionTarget = getPromotionTarget();
      if (promotionTarget.isKnight())
        buf.append('N');
      else if (promotionTarget.isBishop())
        buf.append('B');
      else if (promotionTarget.isRook())
        buf.append('R');
      else if (promotionTarget.isQueen())
        buf.append('Q');
    }

    return buf.toString();
  }




  /**
   * Returns the SAN representation of the move, or <code>null</code> if none
   * was specified in the constructor.
   */

  public String getSAN(){
    return getStringRepresentation();
  }
 




  /**
   * Returns true if this move is a capture.
   */

  public boolean isCapture(){
    return (capturedPiece!=null);
  }




  /**
   * Returns the piece captured by this move, or null if this move
   * is not a capture.
   */

  public ChessPiece getCapturedPiece(){
    return capturedPiece;
  }




  /**
   * Returns true if this move is a castling move, false otherwise.
   */

  public boolean isCastling(){
    return (isShortCastling||isLongCastling);
  }




  /**
   * Returns true if this move is short castling move, false otherwise.
   */

  public boolean isShortCastling(){
    return isShortCastling;
  }



  /**
   * Returns true if this move if a long castling move, false otherwise.
   */

  public boolean isLongCastling(){
    return isLongCastling;
  }



  /**
   * Returns true if this move is an en-passant move, false otherwise.
   */

  public boolean isEnPassant(){
    return isEnPassant;
  }





  /**
   * Returns true if this move is a promotion.
   */

  public boolean isPromotion(){
    return (promotionTarget!=null);
  }




  /**
   * Returns the piece to which the moving pawn was promoted, or null if this
   * move is not a promotion.
   */

  public ChessPiece getPromotionTarget(){
    return promotionTarget;
  }




  /**
   * Returns a textual representation of this ChessMove based on the move data.
   */

  public String getMoveString(){
    if (isShortCastling())
      return "O-O";
    else if (isLongCastling())
      return "O-O-O";
    else{
      String moveString = getStartingSquare().toString() + getEndingSquare().toString();
      if (isPromotion())
        return moveString+"="+getPromotionTarget().toShortString();
      else
        return moveString;
    }
  }


}
