/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002, 2003 Alexander Maryanovsky.
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

import javax.swing.*;
import free.jin.event.*;
import free.jin.board.BoardPanel;
import free.jin.board.BoardManager;
import free.jin.board.JinBoard;
import free.jin.Game;
import free.chess.Player;
import free.chess.Square;
import free.jin.chessclub.UserImageInternalFrame;
import free.jin.chessclub.event.ChessclubGameListener;
import free.jin.chessclub.event.ArrowEvent;
import free.jin.chessclub.event.CircleEvent;
import free.jin.board.event.ArrowCircleListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Cursor;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;


/**
 * Extends BoardPanel to provide chessclub.com specific functionalities.
 */

public class ChessclubBoardPanel extends BoardPanel implements MouseListener,
  ChessclubGameListener, ArrowCircleListener{



  /**
   * We set this to true when we're handling a circle/arrow adding event from
   * the server to avoid responding to the board's event when we add the
   * circle/arrow to it.
   */

  private boolean handlingArrowCircleEvent = false;



  /**
   * Creates a new <code>ChessclubBoardPanel</code> with the given
   * <code>BoardManager</code> and <code>Game</code>.
   */

  public ChessclubBoardPanel(BoardManager boardManager, Game game){
    super(boardManager, game);

    new PlayerImageChecker(Player.WHITE_PLAYER).start();
    new PlayerImageChecker(Player.BLACK_PLAYER).start();
  }




  /**
   * Overrides createBoard(Game game) to return an instance of ChessclubJBoard.
   */

  protected JinBoard createBoard(Game game){
    return new ChessclubJBoard(game.getInitialPosition());
  }




  /**
   * Override configureBoard(Game, JinBoard) to add ourselves as an
   * ArrowCircleListener to the board.
   */

  protected void configureBoard(Game game, JinBoard board){
    super.configureBoard(game, board);

    if ((game.getGameType() == Game.MY_GAME) && !game.isPlayed()){
      board.addArrowCircleListener(this);
      board.setArrowCircleEnabled(true);
    }
    else
      ((ChessclubJBoard)board).setArrowCircleEnabled(false);
  }




  /**
   * Overrides moveMade(MoveMadeEvent) to clear the board of any arrows/circles.
   */

  public void moveMade(MoveMadeEvent evt){
    super.moveMade(evt);

    if (evt.getGame() != game)
      return;

    ((ChessclubJBoard)board).removeAllArrows();
    ((ChessclubJBoard)board).removeAllCircles();
  }




  /**
   * Overrides positionChanged(PositionChangedEvent) to clear the board of any
   * arrows/circles.
   */

  public void positionChanged(PositionChangedEvent evt){
    super.positionChanged(evt);

    if (evt.getGame() != game)
      return;

    ((ChessclubJBoard)board).removeAllArrows();
    ((ChessclubJBoard)board).removeAllCircles();
  }





  /**
   * Overrides takebackOccurred(TakebackEvent) to clear the board of any
   * arrows/circles.
   */

  public void takebackOccurred(TakebackEvent evt){
    super.takebackOccurred(evt);

    if (evt.getGame() != game)
      return;

    ((ChessclubJBoard)board).removeAllArrows();
    ((ChessclubJBoard)board).removeAllCircles();
  }





  /**
   * Overrides illegalMoveAttempted(IllegalMoveEvent) to clear the board of any
   * arrows/circles.
   */

  public void illegalMoveAttempted(IllegalMoveEvent evt){
    super.illegalMoveAttempted(evt);

    if (evt.getGame() != game)
      return;

    ((ChessclubJBoard)board).removeAllArrows();
    ((ChessclubJBoard)board).removeAllCircles();
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




  /**
   * MouseListener implementation.
   */

  public void mouseEntered(MouseEvent evt){}
  public void mouseExited(MouseEvent evt){}
  public void mousePressed(MouseEvent evt){}
  public void mouseReleased(MouseEvent evt){}




  /**
   * MouseListener implementation. Shows an internal frame with the picture of
   * the player whose label was clicked.
   */ 

  public void mouseClicked(MouseEvent evt){
    Object source = evt.getSource();

    if ((source == whiteLabel) || (source == blackLabel)){
      String name = (source == whiteLabel ? getGame().getWhiteName() : getGame().getBlackName());
      URL url;
      try{
        url = new URL("http://www.chessclub.com/mugshots/"+name+".jpg");
      } catch (MalformedURLException e){
          e.printStackTrace();
          return;
        }

      JInternalFrame frame = new UserImageInternalFrame(url, name);
      frame.setLocation(0,0);
      JDesktopPane desktop = boardManager.getPluginContext().getMainFrame().getDesktop();
      desktop.add(frame, JLayeredPane.PALETTE_LAYER);
      frame.setSize(frame.getPreferredSize());
      frame.setVisible(true);
      frame.toFront();
    }
  }




  /**
   * Gets called when an arrow is added to the board (by the server).
   */

  public void arrowAdded(ArrowEvent evt){
    if (evt.getGame() != game)
      return;

    handlingArrowCircleEvent = true;
    ((ChessclubJBoard)board).removeArrow(evt.getFromSquare(), evt.getToSquare());
    ((ChessclubJBoard)board).addArrow(evt.getFromSquare(), evt.getToSquare(), Color.blue);
    handlingArrowCircleEvent = false;
  }




  /**
   * Gets called when a circle is added to the board (by the server).
   */

  public void circleAdded(CircleEvent evt){
    if (evt.getGame() != game)
      return;

    handlingArrowCircleEvent = true;
    ((ChessclubJBoard)board).removeCircle(evt.getCircleSquare());
    ((ChessclubJBoard)board).addCircle(evt.getCircleSquare(), Color.blue);
    handlingArrowCircleEvent = false;
  }




  /**
   * Gets called when an arrow is added on the board (on the client, not server). 
   */

  public void arrowAdded(JinBoard board, Square fromSquare, Square toSquare){
    if (handlingArrowCircleEvent)
      return;

    boardManager.getConnection().sendCommand("arrow "+fromSquare+" "+toSquare);
  }



  /**
   * Gets called when an arrow is removed on the board (on the client, not the
   * server.
   */

  public void arrowRemoved(JinBoard board, Square fromSquare, Square toSquare){}




  /**
   * Gets called when a circle is added (on the client, not the server).
   */

  public void circleAdded(JinBoard board, Square circleSquare){
    if (handlingArrowCircleEvent)
      return;

    boardManager.getConnection().sendCommand("circle "+circleSquare);
  }



  /**
   * Gets called when a circle is removed (on the client, not the server).
   */

  public void circleRemoved(JinBoard board, Square circleSquare){}





  /**
   * This thread tries to open the URL of the picture of the given player and
   * if it succeeds, it makes the label representing his name clickable.
   */

  private class PlayerImageChecker extends Thread{


    /**
     * The Player whose image's existance we'll try to determine.
     */

    private final Player player;



    /**
     * Creates a new PlayerImageChecker with the given player's image.
     */

    public PlayerImageChecker(Player player){
      super("PlayerImageChecker");
      this.player = player;
    }



    /**
     * Tries to obtain the image. If an IOException is thrown, we'll assume that
     * no image exists. If everything goes fine, we make the label of that player
     * look like a link and register the ChessclubBoardPanel as a mouse listener.
     */

    public void run(){
      try{
        String playerName = player.isWhite() ? getGame().getWhiteName() : getGame().getBlackName();
        URL url = new URL("http://www.chessclub.com/mugshots/"+playerName+".jpg");
        InputStream in = url.openStream();
        in.close();

        // openStream() didn't throw an exception, so we'll assume it's ok.
        JLabel label = player.isWhite() ? whiteLabel : blackLabel;
        label.setForeground(Color.blue);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(ChessclubBoardPanel.this);
      } catch (IOException e){}
    }


  }

}
