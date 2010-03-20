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
import free.chess.ChessMove;
import free.jin.JinConnection;
import free.jin.PGNJinConnection;


/**
 * The event sent when a move is made in a game on a chess server. The source of
 * the event is the game in which the move was made.
 */

public class MoveMadeEvent extends GameEvent{



  /**
   * The move that was made.
   */

  private final Move move;



  /**
   * Is this is a "new" move? See the <code>isNew()</code> method for a
   * description on what "new" means.
   */

  private final boolean isNew;




  /**
   * Creates a new <code>MoveMadeEvent</code>.
   *
   * @param conn The <code>JinConnection</code>.
   * @param game The game in which the move was made.
   * @param move The made move.
   * @param isNew Is this is a "new" move.
   */

  public MoveMadeEvent(JinConnection conn, Game game, Move move, boolean isNew){
    super(conn, game);

    if ((conn instanceof PGNJinConnection) && (move instanceof ChessMove) && (((ChessMove)move).getSAN() == null))
      throw new IllegalStateException("The source JinConnection implements PGNJinConnection, but did not provide a SAN representation of the move");

    this.move = move;
    this.isNew = isNew;
  } 




  /**
   * Returns the move that was made.
   */

  public Move getMove(){
    return move;
  }                    




  /**
   * Returns <code>true</code> if the move was sent as a direct response to
   * a move by a player. Returns <code>false</code> if, for example, this is a
   * move that was actually done a while ago but we're only getting it now
   * because we've just started observing the game, making the move "old".
   */

  public boolean isNew(){
    return isNew;
  }


}
