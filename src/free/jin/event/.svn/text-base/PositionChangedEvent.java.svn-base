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
import free.chess.Position;
import free.chess.ChesslikeGenericVariant;
import free.jin.JinConnection;
import free.jin.PGNJinConnection;


/**
 * The event sent when the position on the board changes in such a manner that
 * can't be described by MoveMadeEvents and TakeBackEvents (although it can be
 * sent in such cases too).
 */

public class PositionChangedEvent extends GameEvent{



  /**
   * The new Position.
   */

  private final Position position;




  /**
   * Creates a new PositionChangeEvent with the given source JinConnection, the
   * game in which the position changed and the new Position.
   */

  public PositionChangedEvent(JinConnection conn, Game game, Position position){
    super(conn, game);

    if ((conn instanceof PGNJinConnection) && (position.getVariant() instanceof ChesslikeGenericVariant) &&
        (position.getFEN() == null))
      throw new IllegalStateException("The source JinConnection implements PGNJinConnection, but did not provide a FEN representation of the position");

    this.position = position;
  } 




  /**
   * Returns the new Position.
   */

  public Position getPosition(){
    return position;
  }

}
