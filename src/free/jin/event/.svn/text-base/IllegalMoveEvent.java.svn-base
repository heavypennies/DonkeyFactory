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
import free.chess.Move;
import free.jin.JinConnection;


/**
 * The event fired when the user tried to make an illegal move and the server
 * reported that it's illegal. 
 */

public class IllegalMoveEvent extends GameEvent{



  /**
   * The attempted illegal move.
   */

  private final Move move;




  /**
   * Creates a new IllegalMoveEvent with the given source JinConnection, Game 
   * and the attempted illegal move.
   */

  public IllegalMoveEvent(JinConnection conn, Game game, Move move){
    super(conn, game);

    this.move = move;
  }




  /**
   * Returns the attempted illegal move.
   */

  public Move getMove(){
    return move;
  }

}
