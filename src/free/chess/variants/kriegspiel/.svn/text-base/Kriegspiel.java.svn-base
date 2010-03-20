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

package free.chess.variants.kriegspiel;

import free.chess.*;


/**
 * Implements the Kriegspiel wild variant. See
 * <A HREF="http://www.chessclub.com/help/Kriegspiel">http://www.chessclub.com/help/Kriegspiel</A>
 * for a description of Kriegspiel.
 */

public class Kriegspiel implements WildVariant{


  /**
   * An array containing WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP and WHITE_KNIGHT.
   * These are the pieces to which a white pawn can be promoted.
   */

  private static final ChessPiece [] whitePromotionTargets = new ChessPiece[]{ChessPiece.WHITE_QUEEN,
    ChessPiece.WHITE_ROOK, ChessPiece.WHITE_BISHOP, ChessPiece.WHITE_KNIGHT};



  /**
   * An array containing BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP and BLACK_KNIGHT.
   * These are the pieces to which a black pawn can be promoted.
   */

  private static final ChessPiece [] blackPromotionTargets = new ChessPiece[]{ChessPiece.BLACK_QUEEN,
    ChessPiece.BLACK_ROOK, ChessPiece.BLACK_BISHOP, ChessPiece.BLACK_KNIGHT};




  /**
   * The sole instance of this class.
   */

  private static Kriegspiel instance = new Kriegspiel();




  /**
   * Returns an instance of Kriegspiel.
   */

  public static Kriegspiel getInstance(){
    return instance;
  }



  /**
   * Creates a new Kriegspiel object. This constructor is private and there is
   * only one instance of this class.
   */

  private Kriegspiel(){

  }




  /**
   * Checks if the given position's wild variant is Kriegspiel and returns
   * normally if it is, throws an IllegalArgumentException otherwise.
   */

  private void checkPosition(Position pos){
    if (!pos.getVariant().equals(this))
      throw new IllegalArgumentException("Wrong position variant: "+pos.getVariant());
  }




  /**
   * Initializes the given position.
   *
   * @throws IllegalArgumentException If the given Position's wild variant is
   * not Kriegspiel.
   */

  public void init(Position pos){
    checkPosition(pos);

    pos.setLexigraphic(Chess.INITIAL_POSITION_LEXIGRAPHIC);
  }





  /**
   * If the a move created by the given starting square and ending square in the
   * given position is a promotion, returns an array containing a knight, bishop,
   * rook and queen of the color of the promoted pawn. Otherwise returns null.
   * 
   * @throws IllegalArgumentException If the given Position's wild variant is
   * not Kriegspiel.
   */

  public Piece [] getPromotionTargets(Position pos, Square startingSquare, Square endingSquare){
    checkPosition(pos);

    return ChesslikeGenericVariant.getChessPromotionTargets(pos, startingSquare, endingSquare);
  }




  /**
   * Creates a new KriegspielMove or ChessMove based on the given arguments.
   * If the end square is null, the created move will be a completely hidden
   * KriegspielMove. If the end square is not null, but the start square is,
   * the created move is a partially hidden KriegspielMove. If both are not
   * null, then it's a completely visible, regular ChessMove.
   *
   * @see KriegspielMove
   */

  public Move createMove(Position pos, Square startSquare, Square endSquare, 
    Piece promotionTarget, String stringRepresentation){

    checkPosition(pos);

    if (endSquare==null){
      return new KriegspielMove(pos.getCurrentPlayer(), stringRepresentation);
    }
    else if (startSquare==null){
      return new KriegspielMove(pos, endSquare, stringRepresentation);
    } 
    else{
      return new ChessMove(pos, startSquare, endSquare, (ChessPiece)promotionTarget, stringRepresentation);
    }
  }



  /**
   * Creates a short castling move for the current player in the specified
   * position.
   */

  public Move createShortCastling(Position pos){
    checkPosition(pos);

    Player currentPlayer = pos.getCurrentPlayer();
    if (currentPlayer.isWhite())
      return ChesslikeGenericVariant.WHITE_SHORT_CASTLING;
    else
      return ChesslikeGenericVariant.BLACK_SHORT_CASTLING;
  }




  /**
   * Creates a long castling move for the current player in the specified
   * position.
   */

  public Move createLongCastling(Position pos){
    checkPosition(pos);

    Player currentPlayer = pos.getCurrentPlayer();
    if (currentPlayer.isWhite())
      return ChesslikeGenericVariant.WHITE_LONG_CASTLING;
    else
      return ChesslikeGenericVariant.BLACK_LONG_CASTLING;
  }





  /**
   * Makes the given KriegspielMove on the given position.
   */

  public void makeMove(Move move, Position pos, Position.Modifier modifier){
    checkPosition(pos);

    if ((!(move instanceof KriegspielMove))&&(!(move instanceof ChessMove)))
      throw new IllegalArgumentException("The given move must be an instance of "+ChessMove.class.getName()+" or "+KriegspielMove.class.getName());

    if (move instanceof KriegspielMove){
      KriegspielMove kmove = (KriegspielMove)move;

      if (!kmove.isCompletelyHidden()){
        Square endSquare = kmove.getEndingSquare();
        modifier.setPieceAt(null, endSquare);
      }

      modifier.setCurrentPlayer(pos.getCurrentPlayer().getOpponent());
    }
    else{ // Completely visible
      ChesslikeGenericVariant.makeChessMove((ChessMove)move, pos, modifier);
    }
  }





  /**
   * Returns a ChessPiece corresponding to the given string. See
   * {@link free.chess.ChesslikeGenericVariant#parseChessPiece(String)} for more
   * details.
   *
   * @throws IllegalArgumentException if the given string is in a bad format.
   */

  public Piece parsePiece(String piece){
    return ChesslikeGenericVariant.parseChessPiece(piece);
  }




  /**
   * Returns a String corresponding to the given Piece. See
   * {@link free.chess.ChesslikeGenericVariant#chessPieceToString(ChessPiece)}
   * for more details.
   */

  public String pieceToString(Piece piece){
    if (!(piece instanceof ChessPiece))
      throw new IllegalArgumentException("The given Piece must be an instance of ChessPiece.");

    return ChesslikeGenericVariant.chessPieceToString((ChessPiece)piece);
  }





  /**
   * Returns an instance of <code>DefaultPiecePainter</code>.
   */

  public PiecePainter createDefaultPiecePainter(){
    return new DefaultPiecePainter();
  }





  /**
   * Returns an instance of <code>DefaultBoardPainter</code>.
   */

  public BoardPainter createDefaultBoardPainter(){
    return new DefaultBoardPainter();
  }




  /**
   * Returns the string "Kriegspiel".
   */

  public String getName(){
    return "Kriegspiel";
  } 



  /**
   * Returns a textual representation of this WildVariant.
   */

  public String toString(){
    return getName();
  }

}
