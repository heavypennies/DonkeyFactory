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

package free.jin.chessclub; // Not in free.jin.chessclub.event because we need
                            // to call package friendly methods in JinChessclubConnection

import free.jin.chessclub.event.*;
import free.jin.event.*;
import free.chessclub.level2.Datagram;
import free.jin.event.BasicJinListenerManager;


/**
 * A chessclub.com specific extension of BasicJinListenerManager. Used by
 * <code>free.jin.chessclub.JinChessclubConnection</code>
 */

public class ChessclubJinListenerManager extends BasicJinListenerManager{



  /**
   * The source JinChessclubConnection.
   */

  private final JinChessclubConnection source;




  /**
   * Creates a new ChessclubJinListenerManager with the given source
   * <code>JinChessclubConnection</code>.
   */

  public ChessclubJinListenerManager(JinChessclubConnection source){
    super(source);

    this.source = source;
  }




  /**
   * Sets the state of the datagram with the given code.
   */

  private void setDGState(int dgCode, boolean state){
    source.setDGState(dgCode, state);
  }




  /**
   * Adds the given ChatListener to receive notification when chat related
   * messages arrive from the server.
   */

  public void addChatListener(ChatListener listener){
    super.addChatListener(listener);

    if (listenerList.getListenerCount(ChatListener.class) == 1){
      setDGState(Datagram.DG_PERSONAL_TELL, true);
      setDGState(Datagram.DG_PERSONAL_QTELL, true);
      setDGState(Datagram.DG_SHOUT, true);
      setDGState(Datagram.DG_CHANNEL_TELL, true);
      setDGState(Datagram.DG_CHANNEL_QTELL, true);
      setDGState(Datagram.DG_KIBITZ, true);
    }
  }




  /**
   * Removes the given ChatListener from the list of ChatListeners receiving
   * notification when chat related messages arrive from the server.
   */

  public void removeChatListener(ChatListener listener){
    super.removeChatListener(listener);

    if (listenerList.getListenerCount(ChatListener.class) == 0){
      setDGState(Datagram.DG_PERSONAL_TELL, false);
      setDGState(Datagram.DG_PERSONAL_QTELL, false);
      setDGState(Datagram.DG_SHOUT, false);
      setDGState(Datagram.DG_CHANNEL_TELL, false);
      setDGState(Datagram.DG_CHANNEL_QTELL, true);
      setDGState(Datagram.DG_KIBITZ, false);
    }
  }




  /**
   * Adds the given GameListener to the list of listeners receiving
   * notifications of GameEvents. This method will accept and handle properly 
   * ChessclubGameListeners (by calling the chessclub specific methods in that
   * interface when needed) as well as regular GameListeners.
   */

  public void addGameListener(GameListener listener){
    super.addGameListener(listener);

    if (listenerList.getListenerCount(GameListener.class) == 1){
      setDGState(Datagram.DG_MY_GAME_STARTED, true);
      setDGState(Datagram.DG_STARTED_OBSERVING, true);
      setDGState(Datagram.DG_ISOLATED_BOARD, true);
      setDGState(Datagram.DG_MY_GAME_CHANGE, true);
      setDGState(Datagram.DG_MY_GAME_RESULT, true);
      setDGState(Datagram.DG_POSITION_BEGIN, true);
      setDGState(Datagram.DG_MY_RELATION_TO_GAME, true);
      setDGState(Datagram.DG_SEND_MOVES, true);
      setDGState(Datagram.DG_MOVE_SMITH, true);
      setDGState(Datagram.DG_MOVE_ALGEBRAIC, true);
      setDGState(Datagram.DG_IS_VARIATION, true);
      setDGState(Datagram.DG_BACKWARD, true);
      setDGState(Datagram.DG_TAKEBACK, true);
      setDGState(Datagram.DG_ILLEGAL_MOVE, true);
      setDGState(Datagram.DG_MSEC, true);
      setDGState(Datagram.DG_MORETIME, true);
      setDGState(Datagram.DG_FLIP, true);
      setDGState(Datagram.DG_KNOWS_FISCHER_RANDOM, true);
      setDGState(Datagram.DG_ARROW, true);
      setDGState(Datagram.DG_CIRCLE, true);
      source.setStyle(13);
    }
  }



  /**
   * Removes the given GameListener from the list of listeners receiving 
   * notifications of GameEvents. This method can be also used to remove
   * ChessclubGameListeners added via the addGameListener(GameListener) method.
   */

  public void removeGameListener(GameListener listener){
    super.removeGameListener(listener);

    if (listenerList.getListenerCount(GameListener.class) == 0){
      setDGState(Datagram.DG_MY_GAME_STARTED, false);
      setDGState(Datagram.DG_STARTED_OBSERVING, false);
      setDGState(Datagram.DG_ISOLATED_BOARD, false);
      setDGState(Datagram.DG_MY_GAME_CHANGE, false);
      setDGState(Datagram.DG_MY_GAME_RESULT, false);
      setDGState(Datagram.DG_POSITION_BEGIN, false);
      setDGState(Datagram.DG_MY_RELATION_TO_GAME, false);
      setDGState(Datagram.DG_SEND_MOVES, false);
      setDGState(Datagram.DG_MOVE_SMITH, false);
      setDGState(Datagram.DG_MOVE_ALGEBRAIC, false);
      setDGState(Datagram.DG_IS_VARIATION, false);
      setDGState(Datagram.DG_BACKWARD, false);
      setDGState(Datagram.DG_TAKEBACK, false);
      setDGState(Datagram.DG_ILLEGAL_MOVE, false);
      setDGState(Datagram.DG_MSEC, false);
      setDGState(Datagram.DG_MORETIME, false);
      setDGState(Datagram.DG_FLIP, false);
      setDGState(Datagram.DG_KNOWS_FISCHER_RANDOM, false);
      setDGState(Datagram.DG_ARROW, false);
      setDGState(Datagram.DG_CIRCLE, false);
      source.setStyle(1);
      source.lastGameListenerRemoved();
    }
  }



  
  /**
   * Overrides BasicJinListenerManager.fireGameEvent to handling firing of
   * chessclub.com specific events.
   */

  public void fireGameEvent(GameEvent evt){
    if (!isChessclubSpecificEvent(evt))
      super.fireGameEvent(evt);
    else{
      Object [] listeners = listenerList.getListenerList();
      for (int i = 0; i < listeners.length; i += 2){
        if (listeners[i] == GameListener.class){
          GameListener listener = (GameListener)listeners[i+1];
          try{
            if (evt instanceof CircleEvent){ 
              if (listener instanceof ChessclubGameListener) 
                ((ChessclubGameListener)listener).circleAdded((CircleEvent)evt);
            }
            else if (evt instanceof ArrowEvent){
              if (listener instanceof ChessclubGameListener)
                ((ChessclubGameListener)listener).arrowAdded((ArrowEvent)evt);
            }
            else
              throw new IllegalArgumentException("Unknown GameEvent type: "+evt.getClass());
          } catch (RuntimeException e){
              e.printStackTrace();
            }
        }
      }
    }
  }




  /**
   * Returns true if the given event is a chessclub.com specific GameEvent.
   */

  private boolean isChessclubSpecificEvent(GameEvent evt){
    return (evt instanceof CircleEvent) || (evt instanceof ArrowEvent);
  }





  /**
   * Adds the given SeekListener to the list of listeners receiving notification
   * of SeekEvents.
   */

  public void addSeekListener(SeekListener listener){
    super.addSeekListener(listener);

    if (listenerList.getListenerCount(SeekListener.class) == 1){
      setDGState(Datagram.DG_SEEK, true);
      setDGState(Datagram.DG_SEEK_REMOVED, true);
    }
    else
      source.notFirstListenerAdded(listener);
  }




  /**
   * Removes the given SeekListener from the list of listeners receiving 
   * notification of SeekEvents.
   */

  public void removeSeekListener(SeekListener listener){
    super.removeSeekListener(listener);

    if (listenerList.getListenerCount(SeekListener.class) == 0){
      setDGState(Datagram.DG_SEEK, false);
      setDGState(Datagram.DG_SEEK_REMOVED, false);

      source.lastSeekListenerRemoved();
    }
  }




  /**
   * Adds the given GameListListener to receive notifications of GameListEvents.
   */

  public void addGameListListener(GameListListener listener){
    super.addGameListListener(listener);

    if (listenerList.getListenerCount(GameListListener.class)==1){
      setDGState(Datagram.DG_GAMELIST_BEGIN, true);
      setDGState(Datagram.DG_GAMELIST_ITEM, true);
    }
  }




  /**
   * Removes the given GameListListener from the list of listeners receiving
   * notifications of GameListEvents.
   */

  public void removeGameListListener(GameListListener listener){
    super.removeGameListListener(listener);

    if (listenerList.getListenerCount(GameListListener.class)==0){
      setDGState(Datagram.DG_GAMELIST_BEGIN, false);
      setDGState(Datagram.DG_GAMELIST_ITEM, false);
    }
  }




//  /**
//   * Adds the given FriendsListener to the list of listeners receiving
//   * notifications about friends.
//   */
//
//  public void addFriendsListener(FriendsListener listener){
//    super.addFriendsListener(listener);
//
//    if (listenerList.getListenerCount(FriendsListener.class)==1){
////      setDGState(Datagram.DG_NOTIFY_ARRIVED, true);
////      setDGState(Datagram.DG_NOTIFY_LEFT, true);
////      setDGState(Datagram.DG_MY_NOTIFY_LIST, true);
//
//      source.firstFriendsListenerAdded();
//    }
//  }
//
//
//
//
//
//  /**
//   * Removes the given FriendsListener from the list of listeners receiving
//   * notifications about friends.
//   */
//
//  public void removeFriendsListener(FriendsListener listener){
//    super.removeFriendsListener(listener);
//
//    if (listenerList.getListenerCount(FriendsListener.class)==0){
//      setDGState(Datagram.DG_NOTIFY_ARRIVED, true);
//      setDGState(Datagram.DG_NOTIFY_LEFT, true);
//      setDGState(Datagram.DG_MY_NOTIFY_LIST, true);
//
//      source.lastFriendsListenerRemoved();
//    }
//  }

 

 

  /**
   * Adds the given ChessEventListener to the list of listeners receiving
   * notifications when an event is added.
   */

  public void addChessEventListener(ChessEventListener listener){
    listenerList.add(ChessEventListener.class, listener);

    if (listenerList.getListenerCount(ChessEventListener.class)==1){
      setDGState(Datagram.DG_TOURNEY, true);
      setDGState(Datagram.DG_REMOVE_TOURNEY, true);
    }
  }



  /**
   * Removes the given ChessEventListener from the list of listeners receiving
   * notifications when an event is added.
   */

  public void removeChessEventListener(ChessEventListener listener){
    listenerList.remove(ChessEventListener.class, listener);

    if (listenerList.getListenerCount(ChessEventListener.class)==0){
      setDGState(Datagram.DG_TOURNEY, false);
      setDGState(Datagram.DG_REMOVE_TOURNEY, false);
    }
  }




  /**
   * Dispatches the given ChessEventEvent to all interested listeners.
   */

  public void fireChessEventEvent(ChessEventEvent evt){
    Object [] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i += 2){
      if (listeners[i] == ChessEventListener.class){
        ChessEventListener listener = (ChessEventListener)listeners[i+1];
        try{
          switch (evt.getID()){
            case ChessEventEvent.EVENT_ADDED:
              listener.chessEventAdded(evt);
              break;
            case ChessEventEvent.EVENT_REMOVED:
              listener.chessEventRemoved(evt);
              break;
          }
        } catch (RuntimeException e){
            e.printStackTrace();
          }
      }
    }
  }



}
