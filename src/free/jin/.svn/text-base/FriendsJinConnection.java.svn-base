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

package free.jin;

import free.jin.event.FriendsJinListenerManager;


/**
 * An extension of the JinConnection interface which adds support for friend
 * lists (notify lists on ICC).
 */

public interface FriendsJinConnection extends JinConnection{


  
  /**
   * Returns the FriendsJinListenerManager which allows registering and
   * unregistering FriendsListeners.
   */

  FriendsJinListenerManager getFriendsJinListenerManager();
  



  /**
   * Adds the given player to the list of "friends".
   */

  void addFriend(String name);




  /**
   * Removes the given player from the list of "friends".
   */

  void removeFriend(String name);




  /**
   * Returns the current list of friends. Note that if you've recently called
   * <code>addFriend</code> or <code>removeFriend</code> than the list may not
   * be updated yet, because the actual list may be stored on the server, which
   * hasn't had time to echo the change to us yet.
   * To make sure you get the complete list, first register as a FriendsListener
   * and then call this method.
   */

  String [] getFriends();




  /**
   * Returns <code>true</code> if the given person is a friend. Returns
   * <code>false</code> otherwise.
   */

  boolean isFriend(String playerName);




  /**
   * Returns the current list of online friends. Note that if you've recently
   * called <code>addFriend</code> or <code>removeFriend</code>, this list
   * may not be updated yet, because the actual list may be stored on the
   * server, which hasn't had time to echo the change to us yet.
   * To make sure you get the complete list, first register as a FriendsListener
   * and then call this method.
   */

  String [] getOnlineFriends();

  



  /**
   * Returns <code>true</code> if the given person is a friend and is online.
   * Note that if he's only been added recently, the server might not have had
   * yet time to echo to use that he's online.
   */

  boolean isFriendOnline(String playerName);


}
