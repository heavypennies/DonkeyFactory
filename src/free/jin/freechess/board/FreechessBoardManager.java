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

package free.jin.freechess.board;

import free.jin.Game;
import free.jin.JinConnection;
import free.jin.board.BoardManager;
import free.jin.board.BoardPanel;
import free.jin.event.GameEndEvent;
import free.jin.event.GameStartEvent;
import free.jin.freechess.JinFreechessConnection;

import javax.swing.event.InternalFrameEvent;


/**
 * A freechess.oorg specific extension of the BoardManager plugin.
 */

public class FreechessBoardManager extends BoardManager{



  /**
   * The current primary (observed) game.
   */

  private Object primaryObservedGameID = null;



  /**
   * The current primary (played) game.
   */

  private Object primaryPlayedGameID = null;




  /**
   * Overrides BoardManager.createBoardPanel() to return a FreechessBoardPanel.
   */

  protected BoardPanel createBoardPanel(Game game){
    BoardPanel boardPanel = new FreechessBoardPanel(this, game);

    return boardPanel;
  }




  /**
   * Overrides the superclass' method to set the primary game properly.
   */

  public void internalFrameActivated(InternalFrameEvent e){
    BoardPanel boardPanel = (BoardPanel)internalFramesToBoardPanels.get(e.getSource());
    if (boardPanel == null) // This means that the frame is in the process 
      return;               // of being initialized and isn't ready yet.

    Game game = boardPanel.getGame();
    Object gameID = game.getID();
    if (boardPanel.isActive()){
      int gameType = game.getGameType();
      JinConnection conn = getConnection();

      if (gameType == Game.OBSERVED_GAME){
        if (!gameID.equals(primaryObservedGameID)){
          conn.sendCommand("$$primary "+gameID);
          primaryObservedGameID = gameID;
        }
      }
      else if (gameType == Game.MY_GAME){
        if (!gameID.equals(primaryPlayedGameID)){
          conn.sendCommand("$$goboard "+gameID);
          primaryPlayedGameID = gameID;
        }
      }
    }

    super.internalFrameActivated(e);
  }


  /**
   * Overrides the superclass' method to set <code>primaryObservedGameID</code>
   * and <code>primaryPlayedGameID</code> properly.
   */

  public void gameStarted(GameStartEvent evt){
    int gameType = evt.getGame().getGameType();
    Object gameID = evt.getGame().getID();

    if ((gameType == Game.OBSERVED_GAME) && (primaryObservedGameID == null))
      primaryObservedGameID = gameID;
    else if ((gameType == Game.MY_GAME) && (primaryPlayedGameID == null))
      primaryPlayedGameID = gameID;

    super.gameStarted(evt);
  }



  /**
   * Overrides the superclass' method to set <code>primaryObservedGameID</code>
   * and <code>primaryPlayedGameID</code> properly.
   */

  public void gameEnded(GameEndEvent evt){
    int gameType = evt.getGame().getGameType();
    Object gameID = evt.getGame().getID();

    if ((gameType == Game.OBSERVED_GAME) && gameID.equals(primaryObservedGameID))
      primaryObservedGameID = null;
    else if ((gameType == Game.MY_GAME) && gameID.equals(primaryPlayedGameID))
      primaryPlayedGameID = null;

    super.gameEnded(evt);
  }




  /**
   * Overrides the superclass' method to set the server premove ivariable when
   * premove is turned on.
   */

  public void setMoveSendingMode(int moveSendingMode){
    JinFreechessConnection conn = (JinFreechessConnection)getConnection();
    conn.setPremove(moveSendingMode == PREMOVE_MOVE_SENDING_MODE);

    super.setMoveSendingMode(moveSendingMode);
  }




}
