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

import free.chess.event.MoveEvent;
import free.chess.event.MoveListener;
import free.util.Utilities;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.util.StringTokenizer;


/**
 * Represents a position in one of the chess wild variants. The Position class
 * itself is generally variant independent, but it does make some assumptions,
 * such as the board being a 8x8 container of pieces, there being two players
 * and other things common to most chess variants.
 * <B>IMPORTANT:</B> This class is not thread safe.
 */

public class Position{



  /**
   * The WildVariant of this Position.
   */

  private WildVariant variant;




  /**
   * A matrix to keep references to the pieces.
   */

  private Piece [][] pieces = new Piece[8][8];





  /**
   * The Modifier of this Position.
   */

  private Modifier modifier;





  /**
   * The Player whose turn it currently is in this Position.
   */                                                                

  private Player currentPlayer;




  /**
   * A FEN representation of the position.
   */

  private String positionFEN;





  /**
   * Only one ChangeEvent is needed per model instance since the
   * event's only (read-only) state is the source property.  The source
   * of events generated here is always "this".
   */

  protected transient ChangeEvent changeEvent = null;




  /** 
   * The listeners waiting for model changes. 
   */

  protected EventListenerList listenerList = new EventListenerList();




  /**
   * Creates a new Position with the regular WildVariant (normal chess).
   *
   * @see #init()
   */

  public Position(){
    this(Chess.getInstance());
  }





  /**
   * Creates a new Position with the given WildVariant.
   */

  public Position(WildVariant variant){
    this.variant = variant;
    this.modifier = new Modifier(this);
    init();
  }




  /**
   * Creates a new Position which is exactly like the given Position.
   */

  public Position(Position source){
    this.variant = source.variant;
    this.modifier = new Modifier(this);
    copyFrom(source);
  }




  /**
   * Returns the WildVariant of this Position.
   */

  public WildVariant getVariant(){
    return variant;
  }




  /**
   * Returns the Piece at the given Square.
   *
   * @param square The location of the piece to return.
   */

  public Piece getPieceAt(Square square){
    return getPieceAt(square.getFile(),square.getRank());
  }





  /**
   * Returns the piece at the square with the given file and rank.
   */

  public Piece getPieceAt(int file, int rank){
    return pieces[file][rank];
  }




  /**
   * Returns the piece at the square specified by the given string, as if by
   * {@link Square#parseSquare(String)}.
   */

  public Piece getPieceAt(String square){
    return getPieceAt(Square.parseSquare(square));
  }





  /** 
   * Puts the given piece at the given square, replacing the piece that was
   * there before.
   *
   * @param piece The piece to put.
   * @param square The square where to put the piece.
   */

  public void setPieceAt(Piece piece, Square square){
    setPieceAtImpl(piece, square);
    fireStateChanged();
  }





  /** 
   * Puts the given piece at the given square, replacing the piece that was
   * there before.
   *
   * @param piece The piece to put.
   * @param square The square where to put the piece.
   */

  public void setPieceAt(Piece piece, String square){
    setPieceAt(piece, Square.parseSquare(square));    
  }




  /**
   * Returns the player whose turn it is in this position, the "current"
   * player.
   */

  public Player getCurrentPlayer(){
    return currentPlayer;
  }




  /**
   * Sets the current player in this position to be the given player.
   *
   * @param player The player whose turn it is next.
   */

  public void setCurrentPlayer(Player player){
    setCurrentPlayerImpl(player);
    fireStateChanged();
  }




  /**
   * Sets this Position to represent the position represented by 
   * the given string. The string should represent a position by specifying
   * 64 characters indicating what occupies (-PNBRQKpnbrqk) each square, in 
   * lexigraphic order (a8, b8, ..., h1). The player to move will be
   * the player with the white pieces.
   *
   * @param pos The string representing the position.
   *
   * @throws PositionFormatException if the given string is not in the 
   * expected format.
   */
  
  public void setLexigraphic(String pos){
    if (pos.length()<64)
      throw new PositionFormatException("Less than 64 letters in the string: "+pos);

    int i=0;
    try{
      for (int rank=7;rank>=0;rank--){
        for (int file=0;file<8;file++){
          setPieceAtImpl(variant.parsePiece(""+pos.charAt(i++)), Square.getInstance(file,rank));
        }
      }
    } catch (IllegalArgumentException e){
        throw new PositionFormatException(e);
      }

    setCurrentPlayerImpl(Player.WHITE_PLAYER);

    fireStateChanged();
  }





  /**
   * Returns a string representing the position by 64 characters indicating what
   * occupies (obtained by calling toShortColorString() on each piece, '-' for 
   * no piece) each square, in lexigraphic order (a8, b8, ..., h1).
   */

  public String getLexigraphic(){
    StringBuffer buf = new StringBuffer(64);
    for (int rank=7; rank>=0; rank--)
      for (int file=0; file<8; file++){
        Piece piece = getPieceAt(Square.getInstance(file, rank));
        if (piece==null)
          buf.append("-");
        else
          buf.append(piece.toShortColorString());
      }

    return buf.toString();
  }






  /**
   * Sets this Position to represent the position described by the given string
   * in FEN format. The FEN format is described at 
   * <A HREF="http://www.very-best.de/pgn-spec.htm#16.1">http://www.very-best.de/pgn-spec.htm#16.1</A>.
   * The characters describing pieces aren't limited to the chess set (like in
   * FEN), but are determined by the WildVariant of this position.
   *
   * @throws PositionFormatException if the given string is not in the expected
   * format.
   */

  public void setFEN(String fen){
    this.positionFEN = fen;

    StringTokenizer fenTokenizer = new StringTokenizer(fen, " ");
    if (fenTokenizer.countTokens()!=6)
      throw new PositionFormatException("Wrong amount of fields");
      
    String pos = fenTokenizer.nextToken();
    StringTokenizer ranks = new StringTokenizer(pos,"/");
    if (ranks.countTokens()!=8)
      throw new PositionFormatException("Wrong amount of ranks");

    for (int rank = 7; rank >= 0; rank--){
      String rankString = ranks.nextToken();
      int file = 0;
      for (int i=0;i<rankString.length();i++){
        if (file>7)
          throw new PositionFormatException("Rank "+rank+" extends beyond the board");

        char c = rankString.charAt(i);
        if (Character.isDigit(c)){
          int emptyFiles = Character.digit(c,10);
          while (emptyFiles-- > 0){
            setPieceAtImpl(null, Square.getInstance(file, rank));
            file++;
          }
        }
        else{
          try{
            setPieceAtImpl(variant.parsePiece(String.valueOf(c)), Square.getInstance(file, rank));
            file++;
          } catch (IllegalArgumentException e){
              throw new PositionFormatException(e);
            }
        }
      }
      if (file!=8)
        throw new PositionFormatException("Rank "+rank+" is a few files short");
    }

    String colorToMove = fenTokenizer.nextToken();
    if (colorToMove.length()!=1)
      throw new PositionFormatException("Wrong amount of characters in active color indicator: "+colorToMove);
    if (colorToMove.equals("w"))
      setCurrentPlayerImpl(Player.WHITE_PLAYER);
    else if (colorToMove.equals("b"))
      setCurrentPlayerImpl(Player.BLACK_PLAYER);
    else
      throw new PositionFormatException("Wrong active color indicator: "+colorToMove);
  }





  /**
   * Returns the FEN representation of this Position. May return
   * <code>null</code> if the position wasn't set via the setFEN method.
   */

  public String getFEN(){
    return positionFEN;
  }





  
  /**
   * Sets this Position to the initial position.
   */

  public void init(){
    variant.init(this);
  }




  /**
   * Clears this position of any pieces. The current player is set to the
   * player with the White pieces.
   */

  public void clear(){
    for (int file=0;file<8;file++)
      for (int rank=0;rank<8;rank++)
        setPieceAtImpl(null,Square.getInstance(file,rank));
    setCurrentPlayerImpl(Player.WHITE_PLAYER);

    fireStateChanged();
  }




  /**
   * Makes the given Move on this position. This method first fires a MoveEvent
   * and then a ChangeEvent.
   * 
   * @param move The move to make.
   *
   * @throws IllegalArgumentException if the given Move is incompatible with
   * the wild variant of this Position.
   */

  public void makeMove(Move move){
    variant.makeMove(move, this, modifier);
    fireMoveMade(move);
    fireStateChanged();
  }




  /**
   * Makes this position a copy of the given position by setting it to
   * the same state. The WildVariants of the Positions must match.
   *
   * @param position The position to copy.
   */

  public void copyFrom(Position position){
    if (!variant.equals(position.variant))
      throw new IllegalArgumentException("The WildVariants of the positions don't match");

    for (int file = 0; file < pieces.length; file++){
      for (int rank = 0; rank < pieces[file].length; rank++){
        pieces[file][rank] = position.pieces[file][rank];
      }
    }

    this.positionFEN = position.positionFEN;

    setCurrentPlayerImpl(position.getCurrentPlayer());
    fireStateChanged();
  }




  /** 
   * Puts the given piece at the given square, replacing the piece that was
   * there before. The difference between this and the setPieceAt(Piece,Square)
   * method is that this method does not fire a ChangeEvent. It's useful when
   * you want to do a series of changes and only then fire a ChangeEvent.
   *
   * @param piece The piece to put.
   * @param square The square where to put the piece.
   *
   * @see #setPieceAt(Piece, Square);
   */
  
  private void setPieceAtImpl(Piece piece, Square square){
    pieces[square.getFile()][square.getRank()] = piece;
  }




  /**
   * Sets the current player in this position to be the given player.
   * The difference between this and the setCurrentPlayer(Player)
   * method is that this method does not fire a ChangeEvent. It's useful
   * when you want to do a series of changes and only then fire a ChangeEvent.
   *
   * @param player The player whose turn it is next.
   */

  private void setCurrentPlayerImpl(Player player){
    this.currentPlayer = player;
  }




  /**
   * Adds a ChangeListener.  The change listeners are run each
   * time the Position changes.
   *
   * @param l the ChangeListener to add
   * @see #removeChangeListener
   */

  public void addChangeListener(ChangeListener l) {
    listenerList.add(ChangeListener.class, l);
  }
  



  /**
   * Removes a ChangeListener.
   *
   * @param l the ChangeListener to remove
   * @see #addChangeListener
   */

  public void removeChangeListener(ChangeListener l) {
    listenerList.remove(ChangeListener.class, l);
  }




  /** 
   * Run each ChangeListeners stateChanged() method.
   */

  protected void fireStateChanged(){
    Object [] listeners = listenerList.getListenerList();
    for (int i = listeners.length-2; i>=0; i-=2 ){
      if (listeners[i] == ChangeListener.class){
        if (changeEvent == null){
          changeEvent = new ChangeEvent(this);
        }
        ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
      }          
    }
  }   




  /**
   * Adds a MoveListener.  The change listeners are run each
   * time a move is made in this position.
   *
   * @param l the ChangeListener to add
   * @see #removeChangeListener
   */

  public void addMoveListener(MoveListener l) {
    listenerList.add(MoveListener.class, l);
  }
  


  /**
   * Removes a MoveListener.
   *
   * @param l the ChangeListener to remove
   * @see #addChangeListener
   */

  public void removeMoveListener(MoveListener l) {
    listenerList.remove(MoveListener.class, l);
  }




  /** 
   * Run each MoveListeners moveMade() method.
   *
   * @param move The Move that was made.
   */

  protected void fireMoveMade(Move move){
    MoveEvent evt = new MoveEvent(this,move);
    Object [] listeners = listenerList.getListenerList();
    for (int i = listeners.length-2; i>=0; i-=2 ){
      if (listeners[i] == MoveListener.class){
        ((MoveListener)listeners[i+1]).moveMade(evt);
      }          
    }
  }




  /**
   * Returns a textual representation of the board.
   */

  public String toString(){
    if (getCurrentPlayer().isWhite())
      return "White to move in "+getLexigraphic();
    else
      return "Black to move in "+getLexigraphic();
  }





  /**
   * Returns true iff the specified <code>Position</code> is the same as this
   * one.
   */

  public boolean equals(Position pos){
    if (!variant.equals(pos.variant))
      return false;

    if (!currentPlayer.equals(pos.currentPlayer))
      return false;

    for (int file = 0; file < pieces.length; file++)
      for (int rank = 0; rank < pieces[file].length; rank++)
        if (!Utilities.areEqual(pieces[file][rank], pos.pieces[file][rank]))
          return false;

    return true;
  }




  /**
   * Returns true iff the specified object is a <code>Position</code> and
   * represents the same position as this one.
   */

  public boolean equals(Object obj){
    if (!(obj instanceof Position))
      return false;

    return equals((Position)obj);
  }




  /**
   * Returns the hashcode of this position.
   */

  public int hashCode(){
    int result = 17;
    result = 37*result + variant.hashCode();
    result = 37*result + currentPlayer.hashCode();
    for (int file = 0; file < pieces.length; file++)
      for (int rank = 0; rank < pieces[file].length; rank++){
        Piece piece = pieces[file][rank];
        int c = (piece == null) ? 0 : piece.hashCode();
        result = 37*result + c;
      }

    return result;
  }



  /**
   * Instances of this class is allowed to make modifications to a Position
   * without triggering the Position instance to fire any events. It should only
   * be used by WildVariant implementations for making multi-step atomic changes 
   * which don't trigger firing many events to a Position and the firing an event
   * indicating the change is done.
   */

  public static class Modifier{

     

    /**
     * The Position we're modifying. Don't eliminate this by making the Modifier
     * class nonstatic because it causes Jikes to create invalid bytecode.
     * See http://www-124.ibm.com/pipermail/jikes-dev/2000-November/001585.html
     */

    private Position position;



    /**
     * Creates a new PositionModifier with the given Position.
     */

    private Modifier(Position position){
      this.position = position;
    }



    /**
     * Puts the given piece at the given Square.
     */

    public void setPieceAt(Piece piece, Square square){
      position.setPieceAtImpl(piece, square);
    }



    /**
     * Sets the current player.
     */

    public void setCurrentPlayer(Player player){
      position.setCurrentPlayerImpl(player);
    }


  }


}
