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

package free.jin.chessclub.event;

import free.jin.event.JinEvent;
import free.jin.chessclub.ChessEvent;
import free.jin.chessclub.JinChessclubConnection;


/**
 * The Event sent when a chess event is added or removed.
 */

public class ChessEventEvent extends JinEvent{


  /**
   * The id for events when a chess event is added.
   */

  public static final int EVENT_ADDED = 1;




  /**
   * The id for events when a chess event is removed.
   */

  public static final int EVENT_REMOVED = 2;




  /**
   * The id of this event.
   */

  private final int id;




  /**
   * The ChessEvent.
   */

  private final ChessEvent chessEvent;




  /**
   * Creates a new ChessEventEvent with the given originating
   * JinChessclubConnection, id and ChessEvent.
   */

  public ChessEventEvent(JinChessclubConnection conn, int id, ChessEvent chessEvent){
    super(conn);

    switch(id){
      case EVENT_ADDED:
      case EVENT_REMOVED:
        break;
      default:
        throw new IllegalArgumentException("Invalid id: "+id);
    }

    if (chessEvent == null)
      throw new IllegalArgumentException("chessEvent may not be null");

    this.id = id;
    this.chessEvent = chessEvent;
  }




  /**
   * Returns the id of this event.
   */

  public int getID(){
    return id;
  }




  /**
   * Returns the ChessEvent.
   */

  public ChessEvent getChessEvent(){
    return chessEvent;
  }


}
