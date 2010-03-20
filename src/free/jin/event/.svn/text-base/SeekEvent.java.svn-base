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
import free.jin.SeekJinConnection;
import free.jin.Seek;


/**
 * The event fired when a seek is added to or removed from the sought games list.
 */

public class SeekEvent extends JinEvent{



  /**
   * The id specifying an event fired when a seek is added to the sought list.
   */

  public static final int SEEK_ADDED = 1;



  /**
   * The id specifying an event fired when a seek is removed from the sought list.
   */

  public static final int SEEK_REMOVED = 2;




  /**
   * The id of the SeekEvent.
   */

  private final int id;




  /**
   * The seek.
   */

  private final Seek seek;




  /**
   * Creates a new SeekEvent with the given source JinConnection.
   */

  public SeekEvent(SeekJinConnection conn, int id, Seek seek){
    super(conn);

    switch(id){
      case SEEK_ADDED:
      case SEEK_REMOVED:
        break;
      default:
        throw new IllegalArgumentException("Bad id: "+id);
    }

    this.id = id;
    this.seek = seek;
  }




  /**
   * Returns the id of this SeekEvent, possible values are {@link #SEEK_ADDED}
   * and {@link #SEEK_REMOVED}.
   */

  public int getID(){
    return id;
  }



  /**
   * Returns the seek.
   */

  public Seek getSeek(){
    return seek;
  }

}
