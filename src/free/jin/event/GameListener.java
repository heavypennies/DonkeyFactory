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

import java.util.EventListener;


/**
 * A listener for receiving notifications about game related events.
 */

public interface GameListener extends EventListener{



  /**
   * Gets called when a game starts.
   */

  void gameStarted(GameStartEvent evt);



  /**
   * Gets called when a move is made in a game.
   */

  void moveMade(MoveMadeEvent evt);




  /**
   * Gets called when the position in the game changes. This is <B>NOT</B>
   * called along with the other position altering methods (moveMade, etc.).
   */

  void positionChanged(PositionChangedEvent evt);




  /**
   * Gets called when one or more moves were taken back.
   */

  void takebackOccurred(TakebackEvent evt);



  /**
   * Gets called when the server reports that the user attempted to make an illegal
   * move.
   */

  void illegalMoveAttempted(IllegalMoveEvent evt);




  /**
   * Gets called when the server adjusts the clock of one of the players.
   */

  void clockAdjusted(ClockAdjustmentEvent evt);



  /**
   * Gets called when the server wants to flip the board.
   */

  void boardFlipped(BoardFlipEvent evt);




  /**
   * Gets called when a game ends.
   */

  void gameEnded(GameEndEvent evt);


}
