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
 * The event fired when a game ends, or the user stopped 
 * playing/examining/observing it.
 */

public class GameEndEvent extends GameEvent{



  /**
   * The result of the game.
   */

  private final int result;




  /**
   * Creates a new GameEndEvent with the given Game, source JinConnection and
   * result. Possible result values are defined in the <code>Game</code> class.
   * <code>GAME_IN_PROGRESS</code> is not a valid result, though.
   */

  public GameEndEvent(JinConnection conn, Game game, int result){
    super(conn, game);

    if (game.getResult() == Game.GAME_IN_PROGRESS)
      throw new IllegalStateException("The specified game reports it's still in progress");

    if (result != game.getResult())
      throw new IllegalArgumentException("The specified game result differs from the result returned by Game.getResult()");

    this.result = result;
  }



  /**
   * Returns the result of the game. Possible values are defined in the
   * <code>Game</code> class.
   */
  
  public int getResult(){
    return result;
  }

}
