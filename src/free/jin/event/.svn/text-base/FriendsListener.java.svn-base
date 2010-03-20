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
 * The interface for listening to FriendsEvents.
 */

public interface FriendsListener extends EventListener{



  /**
   * Called to notify that the given friend is online. Note that this does not
   * necessarily mean that he logged on - it could be that he was already online
   * but only now got added to our friends list.
   */

  void friendOnline(FriendsEvent evt);




  /**
   * Called when a friend logs on to the server.
   */

  void friendConnected(FriendsEvent evt);





  /**
   * Called when a friend logs out from the server.
   */

  void friendDisconnected(FriendsEvent evt);




  /**
   * Called when a new friend is added to the friend list.
   */

  void friendAdded(FriendsEvent evt);




  /**
   * Called when a friend is removed form the friend list.
   */

  void friendRemoved(FriendsEvent evt);



}
