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

import free.jin.event.JinEvent;
import free.jin.JinConnection;


/**
 * The event fired when the state of the connection to the server changes.
 */

public class ConnectionEvent extends JinEvent{


  /**
   * The id constant specifying that a connection was established.
   */

  public static final int ESTABLISHED = 1;




  /**
   * The id constant specifying that the login procedure was finished.
   */

  public static final int LOGGED_IN = 2;



  /**
   * The id constant specifying that a connection was lost.
   */

  public static final int LOST = 3;




  /**
   * The ID of this ConnectionEvent.
   */

  private final int id;




  /**
   * Creates a new ConnectionEvent with the given source JinConnection and ID.
   * Possible ID values are {@link #ESTABLISHED}, {@link #LOGGED_IN} and {@link #LOST}.
   */

  public ConnectionEvent(JinConnection conn, int id){
    super(conn);
    switch(id){
      case ESTABLISHED:
      case LOGGED_IN:
      case LOST:
        break;
      default:
        throw new IllegalArgumentException("Wrong ID value: "+id);
    }

    this.id = id;
  }




  /**
   * Returns the ID of the event, either {@link #ESTABLISHED}, {@link #LOGGED_IN}
   * or {@link #LOST}.
   */

  public int getID(){
    return id;
  }


}
