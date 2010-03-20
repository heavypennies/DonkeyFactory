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

import free.chess.Player;
import free.jin.Game;
import free.jin.JinConnection;


/**
 * The event fired when the server sends a clock update. The following 
 * restrictions/rules apply on this event:
 * <UL>
 *   <LI> The server must fire this event to start the clocks at the beginning
 *        of the game - before this event is fired, the clocks must be stopped.
 *   <LI> To change the currently running clock, the server must first fire an
 *        event to stop the running clock and then another event to start the
 *        other clock (the server cannot reverse the order of these events).
 *   <LI> The server may not fire an event indicating that the clock of a player
 *        whose opponent currently has the turn should be running.
 * </UL>
 */

public class ClockAdjustmentEvent extends GameEvent{


  /**
   * The player whose clock is updated.
   */

  private final Player player;



  /**
   * The amount of time remaining on the clock, in milliseconds.
   */

  private final int time;



  /**
   * Whether the clock is running or stopped.
   */

  private final boolean running;




  /**
   * Creates a new ClockAdjustmentEvent with the given source JinConnection, Game,
   * the Player whose clock is adjusted, the amount of milliseconds on his clock
   * and whether the clock is running or stopped.
   */

  public ClockAdjustmentEvent(JinConnection conn, Game game, Player player, int time, boolean running){
    super(conn, game);

    this.player = player;
    this.time = time;
    this.running = running;
  }




  /**
   * Returns the Player whose clock is adjusted.
   */

  public Player getPlayer(){
    return player;
  }



  /**
   * Returns the amount of time remaining on the clock, in milliseconds.
   */

  public int getTime(){
    return time;
  }



  /**
   * Returns true if the clock is running or false if stopped.
   */

  public boolean isClockRunning(){
    return running;
  }  

}
