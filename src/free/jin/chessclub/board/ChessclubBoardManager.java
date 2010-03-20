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

package free.jin.chessclub.board;


import javax.swing.event.InternalFrameEvent;
import free.jin.board.BoardManager;
import free.jin.board.BoardPanel;
import free.jin.Game;
import free.jin.JinConnection;
import free.jin.event.GameStartEvent;
import free.jin.event.GameEndEvent;


/**
 * A chessclub.com specific extension of the BoardManager plugin.
 */

public class ChessclubBoardManager extends BoardManager{



  /**
   * The amount of games of type MY_GAME.
   */

  private int myGamesCount = 0;



  /**
   * The gameID of the primary/current game.
   */

  private Object primaryGameID = null;




  /**
   * Overrides BoardManager.createBoardPanel() to return a ChessclubBoardPanel.
   */

  protected BoardPanel createBoardPanel(Game game){
    BoardPanel boardPanel = new ChessclubBoardPanel(this, game);

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
    if (boardPanel.isActive()){
      int gameType = game.getGameType();
      Object gameID = game.getID();
      JinConnection conn = getConnection();

      if (!gameID.equals(primaryGameID)){
        if ((myGamesCount > 1) && (gameType == Game.MY_GAME))
          conn.sendCommand("goto "+gameID);
        else
          conn.sendCommand("primary "+gameID);
        primaryGameID = gameID;
      }
    }

    super.internalFrameActivated(e);
  }




  /**
   * Overrides the superclass' method to increment <code>myGamesCount</code>.
   */

  public void gameStarted(GameStartEvent evt){
    if (evt.getGame().getGameType() == Game.MY_GAME)
      myGamesCount++;

    super.gameStarted(evt);
  }



  /**
   * Overrides the superclass' method to decrement <code>myGamesCount</code>.
   */

  public void gameEnded(GameEndEvent evt){
    if (evt.getGame().getGameType() == Game.MY_GAME)
      myGamesCount--;

    super.gameEnded(evt);
  }



}
