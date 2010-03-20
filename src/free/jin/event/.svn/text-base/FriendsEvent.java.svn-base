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

import free.jin.FriendsJinConnection;


/**
 * The event fired when something friend related occurs.
 */

public class FriendsEvent extends JinEvent{



  /**
   * The id for events when a friend has logged on.
   */

  public static final int FRIEND_CONNECTED = 1;




  /**
   * The id for events when a friend has logged off.
   */

  public static final int FRIEND_DISCONNECTED = 2;




  /**
   * The id for events when a new friend is added to the list of friends.
   */

  public static final int FRIEND_ADDED = 3;



  /**
   * The id for events when a friend is removed form the list of friends.
   */

  public static final int FRIEND_REMOVED = 4;



  /**
   * The id for events notifying that the friend is online. This differs from
   * the <code>FRIEND_CONNECTED</code> event in that in this one, it might be
   * that the friend was already online but we've just now added him to our
   * friends list, or it's us who logged in while the friend was already on.
   */

  public static final int FRIEND_ONLINE = 5;





  /**
   * The id of the event.
   */

  private final int id;




  /**
   * The name of the "friend.
   */

  private final String friendName;




  /**
   * Creates a new FriendsEvent with the given source FriendsJinConnection event
   * id and friend name.
   */

  public FriendsEvent(FriendsJinConnection source, int id, String friendName){
    super(source);

    switch (id){
      case FRIEND_CONNECTED:
      case FRIEND_DISCONNECTED:
      case FRIEND_ADDED:
      case FRIEND_REMOVED:
      case FRIEND_ONLINE:
        break;
      default:
        throw new IllegalArgumentException("Unknown FriendsEvent id: "+id);
    }

    this.id = id;
    this.friendName = friendName;
  }




  /**
   * Returns the id of the event.
   */

  public int getID(){
    return id;
  }




  /**
   * Returns the name of the friend.
   */

  public String getFriendName(){
    return friendName;
  }


}

