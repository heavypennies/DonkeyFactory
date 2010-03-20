/**
 * The chessclub.com connection library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The chessclub.com connection library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The chessclub.com connection library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the chessclub.com connection library; if not, write to the Free
 * Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package free.chessclub;


/**
 * A container for information the chessclub.com server sends for a single move.
 * Note that the fields only contain valid information if the setting turning
 * on the sending of that information is on. For example, if DG_MOVE_SMITH is off,
 * a MoveStruct arriving within a DG_SEND_MOVES datagram will contain an invalid
 * (garbage) smithMove field. See
 * <A HREF="ftp://ftp.chessclub.com/pub/icc/formats/formats.txt">ftp://ftp.chessclub.com/pub/icc/formats/formats.txt</A>
 * for information which settings turn on sending which fields.
 */

public class MoveStruct{


  /**
   * Possible values for the variationCode field.
   * Main line, part of a DG_POSITION_BEGIN (either play or examine mode)
   */

  public static final int MOVE_INITIAL = 0;   



  /**
   * Main line, play mode.
   */

	public static final int MOVE_PLAYED = 1;



  /**
   * Main line, examine mode, the result of "forward 1", for example.
   */

	public static final int MOVE_FORWARD = 2;



  /**
   * A move in examine mode, make what of it you will.
   */
     
	public static final int MOVE_EXAMINE = 3;



  /**
   * A move in examine mode, intended to overwrite a previous line.  (Not implemented yet)
   */

  public static final int MOVE_OVERWRITE = 4;



  /**
   * A move in examine mode, intended to be the new main line.  (Not implemented yet)
   */

	public static final int MOVE_MAINLINE = 5;



  /**
   * A move in examine mdoe, intended to be a side line.  (Not implemented yet)
   */

  public static final int MOVE_SIDELINE = 6;



  /**
   * The move in algebraic notation.
   */

  public final String algebraicMove;


  /**
   * The move in smith notation.
   */
  
  public final String smithMove;


  /**
   * The time taken to make this move, in seconds.
   */

  public final int time;


  /**
   * The amount of time remaining on the player's clock after making this move 
   * (after incrementing).
   */
  
  public final int clock;



  /**
   * An integer describing which state the game is in when
   * this move is made. Possible values are {@link #MOVE_INITIAL},
   * {@link #MOVE_PLAYED}, {@link #MOVE_FORWARD}, {@link #MOVE_EXAMINE}, 
   * {@link #MOVE_OVERWRITE}, {@link #MOVE_MAINLINE} and {@link #MOVE_SIDELINE}.
   */

  public final int variationCode;




  /**
   * Creates a new MoveStruct with the given values.
   *
   * @param algebraicMove The move in algebraic notation.
   * @param smithMove The move in smith notation.
   * @param time The amount of seconds it took to make the move.
   * @param clock The amount seconds remaining on the player's clock after
   * making the move.
   * @param variationCode An integer describing which state the game is in when
   * this move is made.
   */

  public MoveStruct(String algebraicMove, String smithMove, int time, int clock, int variationCode){
    this.algebraicMove = algebraicMove;
    this.smithMove = smithMove;
    this.time = time;
    this.clock = clock;
    this.variationCode = variationCode;
  }



}
