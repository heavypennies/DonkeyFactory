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

import java.awt.*;
import java.awt.event.MouseEvent;
import free.chess.Position;
import free.chess.Square;
import free.jin.board.JinBoard;


/**
 * An extension of JinBoard which implements chessclub.com specific
 * functionality.
 */

public class ChessclubJBoard extends JinBoard{



  /**
   * The temporary arrow's starting square.
   */

  private Square tmpArrowFrom = null;




  /**
   * The temporary arrow's ending square.
   */

  private Square tmpArrowTo = null;




  /**
   * Creates a new ChessclubJBoard with the given initial Position.
   */

  public ChessclubJBoard(Position initPosition){
    super(initPosition);

    enableEvents(MouseEvent.MOUSE_EVENT_MASK | MouseEvent.MOUSE_MOTION_EVENT_MASK);
  }




  /**
   * Paints the temporary arrow.
   */

  public void paint(Component component, Graphics g){
    super.paint(component, g);

    Rectangle rect = squareToRect(0, 0, null);

    int arrowSize = calcArrowSize(rect.width, rect.height);
    int circleSize = calcCircleSize(rect.width, rect.height);

    if ((tmpArrowFrom != null)&&(tmpArrowTo != null)&&!tmpArrowFrom.equals(tmpArrowTo))
      drawArrow(g, tmpArrowFrom, tmpArrowTo, arrowSize, getDefaultArrowColor());
  }




  /**
   * Processes a mouse event.
   */

  protected void processMouseEvent(MouseEvent evt){
    super.processMouseEvent(evt);

    if (!isEnabled())
      return;

    // Only take into account the right mouse button
    if ((evt.getModifiers()&MouseEvent.BUTTON3_MASK) != MouseEvent.BUTTON3_MASK)
      return;

    if (!isArrowCircleEnabled())
      return;

    int x = evt.getX();
    int y = evt.getY();

    Square square = locationToSquare(x, y);

    if (evt.getID() == MouseEvent.MOUSE_CLICKED)
      addCircle(square, getDefaultCircleColor());
    else if (evt.getID() == MouseEvent.MOUSE_PRESSED)
      tmpArrowFrom = square;
    else if (evt.getID() == MouseEvent.MOUSE_RELEASED){
      if ((tmpArrowFrom != null) && (tmpArrowTo != null) && !tmpArrowFrom.equals(tmpArrowTo))
        addArrow(tmpArrowFrom, tmpArrowTo, getDefaultArrowColor());
      tmpArrowFrom = null;
      tmpArrowTo = null;
    }
  }





  /**
   * Processes a mouse motion event.
   */

  protected void processMouseMotionEvent(MouseEvent evt){
    super.processMouseMotionEvent(evt);

    if (!isEnabled())
      return;

    // Only take into account the right mouse button
    if ((evt.getModifiers()&MouseEvent.BUTTON3_MASK) != MouseEvent.BUTTON3_MASK)
      return;

    if (!isArrowCircleEnabled())
      return;

    int x = evt.getX();
    int y = evt.getY();

    Square square = locationToSquare(x, y);

    if ((evt.getID() == MouseEvent.MOUSE_DRAGGED)&&(tmpArrowFrom != null)){
      tmpArrowTo = square;
      repaint();
    }

  }


}
