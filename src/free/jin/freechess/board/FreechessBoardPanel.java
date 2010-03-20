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

import javax.swing.*;
import free.jin.board.BoardPanel;
import free.jin.board.BoardManager;
import free.jin.Game;


/**
 * Extends BoardPanel to provide freechess.org specific functionalities.
 */

public class FreechessBoardPanel extends BoardPanel{



  /**
   * Creates a new <code>FreechessBoardPanel</code> with the given
   * <code>BoardManager</code> and <code>Game</code>.
   */

  public FreechessBoardPanel(BoardManager boardManager, Game game){
    super(boardManager, game);
  }



  /**
   * Overrides BoardPanel.createWhiteLabel(Game) to return a chessclub.com
   * specific version.
   */

  protected JLabel createWhiteLabel(Game game){
    JLabel label = super.createWhiteLabel(game);
    int rating = game.getWhiteRating();
    String ratingString = (rating > 0) ? (" "+rating) : "";
    label.setText(game.getWhiteName()+game.getWhiteTitles()+ratingString);

    return label;
  }




  /**
   * Overrides BoardPanel.createBlackLabel(Game) to return a chessclub.com
   * specific version.
   */

  protected JLabel createBlackLabel(Game game){
    JLabel label = super.createBlackLabel(game);
    int rating = game.getBlackRating();
    String ratingString = (rating > 0) ? (" "+rating) : "";
    label.setText(game.getBlackName()+game.getBlackTitles()+ratingString);

    return label;
  }




  /**
   * Overrides BoardPanel.createGameLabel(Game) to return a chessclub.com
   * specific version.
   */

  protected JLabel createGameLabel(Game game){
    JLabel label = super.createGameLabel(game);
    free.chess.WildVariant variant = game.getVariant();
    String category = variant.equals(free.chess.Chess.getInstance()) ? game.getRatingCategoryString() : variant.getName();
    label.setText((game.isRated() ? "Rated" : "Unrated") + " " + game.getTCString()+ " " + category);

    return label;
  }



}
