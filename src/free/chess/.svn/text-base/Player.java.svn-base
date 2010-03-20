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
 * Defines two constants, one for the player with the white pieces and one
 * for the player with the black pieces.
 */

public class Player{


  /**
   * A constant for the white player.
   */

  public static final Player WHITE_PLAYER = new Player();




  /**
   * A constant for the black player.
   */

  public static final Player BLACK_PLAYER = new Player();




  /**
   * Do not allow others to instantiate this class.
   */

  private Player(){}




  /**
   * Returns true if this player is the player with the White pieces,
   * false otherwise.
   */
  
  public boolean isWhite(){
    return (this==WHITE_PLAYER);
  }




  /**
   * Returns true if this player is the player with the Black pieces,
   * false otherwise.
   */

  public boolean isBlack(){
    return (this==BLACK_PLAYER);
  }




  /**
   * Returns the opponent of this player. 
   */

  public Player getOpponent(){
    if (this.isWhite())
      return BLACK_PLAYER;
    else
      return WHITE_PLAYER;
  }
  




  /**
   * Returns "White" for the player with the white pieces and "Black" for the
   * player with the black pieces.
   */

  public String toString(){
    if (this==WHITE_PLAYER)
      return "White";
    else
      return "Black";
  }

}
