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


/**
 * <P>The interface for managing listeners of JinConnections.
 * See <code>JinConnection.getListenerManager()</code>.
 */

public interface JinListenerManager{



  /**
   * Adds the given ConnectionListener to receive notifications when the connection
   * to the server is established/lost.
   */

  void addConnectionListener(ConnectionListener listener);



  /**
   * Removes the given ConnectionListener from the list of listeners receiving
   * notifications when the connection to the server is established/lost.
   */

  void removeConnectionListener(ConnectionListener listener);



  /**
   * Adds the given PlainTextListener to receive notification when otherwise
   * unidentified text arrives from the server.
   */

  void addPlainTextListener(PlainTextListener listener);



  /**
   * Removes the given PlainTextListener from the list of PlainTextListeners
   * receiving notification when otherwise unidentified text arrives from 
   * the server.
   */

  void removePlainTextListener(PlainTextListener listener);





  /**
   * Adds the given ChatListener to receive notification when chat related
   * messages arrive from the server.
   */

  void addChatListener(ChatListener listener);



  /**
   * Removes the given ChatListener from the list of ChatListeners receiving
   * notification when chat related messages arrive from the server.
   */

  void removeChatListener(ChatListener listener);




  /**
   * Adds the given GameListener to the list of listeners receiving notifications
   * of GameEvents.
   */

  void addGameListener(GameListener listener);



  /**
   * Removes the given GameListener from the list of listeners receiving notifications
   * of GameEvents.
   */

  void removeGameListener(GameListener listener);


}
