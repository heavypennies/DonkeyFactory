/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package free.jin.event;

import free.jin.Game;
import free.jin.JinConnection;

/**
 * The event fired when the server asks to flip the board.
 */

public class FlipBoardEvent extends GameEvent{


  /**
   * Whether the board should be flipped (black at bottom).
   */

  private final boolean isFlipped;



  /**
   * Creates a new FlipBoardEvent.
   */

  public FlipBoardEvent(JinConnection conn, Game game, boolean isFlipped){
    super(conn, game);

    this.isFlipped = isFlipped;
  }



  /**
   * Returns true if the server wants the board to be flipped
   * (black at the bottom), false otherwise.
   */

  public boolean isFlipped(){
    return isFlipped;
  }

}
