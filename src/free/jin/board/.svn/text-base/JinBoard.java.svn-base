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

package free.jin.board;

import free.chess.*;

import java.awt.*;
import java.util.Vector;
import free.chess.Position;
import free.chess.Square;
import free.util.PaintHook;
import free.jin.board.event.ArrowCircleListener;


/**
 * An extension of <code>free.chess.JBoard</code> which implements certain Jin
 * specific features. This class adds the arrow/circle functionality but doesn't
 * listen to the appropriate mouse events to actually add arrows. This is done
 * in the server specific subclasses. The "official" purpose of this is to allow
 * server specific gestures to generate arrows/circles. The "unofficial" reason
 * is so that it's possible to add arrows/circles programmatically on any
 * server.
 */

public class JinBoard extends JBoard implements PaintHook{




  /**
   * A Vector holding triplets of [fromSquare], [toSquare], [Color] for every
   * arrow on the board.
   */

  private final Vector arrows = new Vector(3);




  /**
   * A Vector holding couples of [circleSquare], [Color] for every circle on
   * the board.
   */

  private final Vector circles = new Vector(2);




  /**
   * The default arrow color.
   */

  private Color defaultArrowColor = Color.lightGray;
  



  /**
   * The default circle color.
   */

  private Color defaultCircleColor = Color.lightGray;




  /**
   * Is the arrow/circle adding functionality enabled (for the user).
   */

  private boolean isArrowCircleEnabled = true;




  /**
   * Creates a new <code>JinBoard</code> with the given initial
   * <code>Position</code>.
   */

  public JinBoard(Position initPosition){
    super(initPosition);

    addPaintHook(this);
  }




  /**
   * Returns <code>true</code> if the arrow/circle adding functionality enabled
   * (for the user), <code>false</code> otherwise.
   */

  public boolean isArrowCircleEnabled(){
    return isArrowCircleEnabled;
  }




  /**
   * Sets whether the arrow/circle adding functionality is enabled for the user.
   */

  public void setArrowCircleEnabled(boolean enabled){
    this.isArrowCircleEnabled = enabled;
  }




  /**
   * Sets the default arrow color.
   */

  public void setDefaultArrowColor(Color color){
    this.defaultArrowColor = color;
  }




  /**
   * Sets the default circle color.
   */

  public void setDefaultCircleColor(Color color){
    this.defaultCircleColor = color;
  }




  /**
   * Returns the default arrow color.
   */

  public Color getDefaultArrowColor(){
    return defaultArrowColor;
  }




  /**
   * Returns the default circle color.
   */

  public Color getDefaultCircleColor(){
    return defaultCircleColor;
  }




  /**
   * Adds the given ArrowCircleListener to the list of listeners receiving
   * notifications when a circle or an arrow is added.
   */

  public void addArrowCircleListener(ArrowCircleListener listener){
    listenerList.add(ArrowCircleListener.class, listener);
  }




  /**
   * Removes the given ArrowCircleListener from the list of listeners receiving
   * notifications when a circle or an arrow is added.
   */

  public void removeArrowCircleListener(ArrowCircleListener listener){
    listenerList.remove(ArrowCircleListener.class, listener);
  }



  
  /**
   * Notifies all registered ArrowCircleListeners that an arrow has been added.
   */

  protected void fireArrowAdded(Square from, Square to){
   Object [] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i += 2){
      if (listeners[i]==ArrowCircleListener.class){
        ArrowCircleListener listener = (ArrowCircleListener)listeners[i+1];
        listener.arrowAdded(this, from, to);
      }
    }
  }




  /**
   * Notifies all registered ArrowCircleListeners that a circle has been added.
   */

  protected void fireCircleAdded(Square circleSquare){
   Object [] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i += 2){
      if (listeners[i]==ArrowCircleListener.class){
        ArrowCircleListener listener = (ArrowCircleListener)listeners[i+1];
        listener.circleAdded(this, circleSquare);
      }
    }
  }




  /**
   * Notifies all registered ArrowCircleListeners that an arrow has been
   * removed.
   */

  protected void fireArrowRemoved(Square from, Square to){
   Object [] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i += 2){
      if (listeners[i]==ArrowCircleListener.class){
        ArrowCircleListener listener = (ArrowCircleListener)listeners[i+1];
        listener.arrowRemoved(this, from, to);
      }
    }
  }




  /**
   * Notifies all registered ArrowCircleListeners that a circle has been
   * removed.
   */

  protected void fireCircleRemoved(Square circleSquare){
   Object [] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i += 2){
      if (listeners[i]==ArrowCircleListener.class){
        ArrowCircleListener listener = (ArrowCircleListener)listeners[i+1];
        listener.circleRemoved(this, circleSquare);
      }
    }
  }




  /**
   * Calculates the arrow size for the specified square size.
   */

  protected int calcArrowSize(int squareWidth, int squareHeight){
    return squareWidth/7;
  }

  


  /**
   * Calculates the circle size for the specified square size.
   */

  protected int calcCircleSize(int squareWidth, int squareHeight){
    return squareWidth/10;
  }




  /**
   * PaintHook implementation.
   */

  public void paint(Component component, Graphics g){
    if (component != this)
      throw new IllegalArgumentException("Can only paint on this");

    Rectangle rect = squareToRect(0, 0, null);

    int arrowSize = calcArrowSize(rect.width, rect.height);
    int circleSize = calcCircleSize(rect.width, rect.height);

    int arrowCount = arrows.size()/3;
    
    for (int i = 0; i < arrowCount; i++)
      drawArrow(g, (Square)arrows.elementAt(i*3), (Square)arrows.elementAt(i*3+1), arrowSize, (Color)arrows.elementAt(i*3+2));

    int circleCount = circles.size()/2;
    for (int i = 0; i < circleCount; i++)
      drawSquare(g, (Square)circles.elementAt(i*2), circleSize, (Color)circles.elementAt(i*2+1));
  }




  /**
   * Adds an arrow with the given color to the board.
   */

  public void addArrow(Square from, Square to, Color color){
    arrows.addElement(from);
    arrows.addElement(to);
    arrows.addElement(color);

    fireArrowAdded(from, to);

    repaint();
  }




  /**
   * Removes the specified arrow (or arrows if there's more than one) from the
   * board.
   */

  public void removeArrow(Square from, Square to){
    for (int i = 0; i < arrows.size()/3; i++){
      Square fromSquare = (Square)arrows.elementAt(i*3);
      Square toSquare = (Square)arrows.elementAt(i*3+1);

      if (from.equals(fromSquare) && to.equals(toSquare)){
        arrows.removeElementAt(i*3+2);
        arrows.removeElementAt(i*3+1);
        arrows.removeElementAt(i*3);
        i--;
      }
    }

    fireArrowRemoved(from, to);

    repaint();
  }





  /**
   * Removes all arrows.
   */

  public void removeAllArrows(){
    for (int i = arrows.size() - 3; i >= 0; i -= 3){
      Square from = (Square)arrows.elementAt(i);
      Square to = (Square)arrows.elementAt(i+1);
      
      arrows.removeElementAt(i+2);
      arrows.removeElementAt(i+1);
      arrows.removeElementAt(i);

      fireArrowRemoved(from, to);
    }

    repaint();
  }




  /**
   * Adds a circle with the given color to the board.
   */

  public void addCircle(Square circleSquare, Color color){
    circles.addElement(circleSquare);
    circles.addElement(color);

    fireCircleAdded(circleSquare);

    repaint();
  }




  /**
   * Removes the specified circle (or circles, if there's more than one) from 
   * the board.
   */

  public void removeCircle(Square circleSquare){
    for (int i = 0; i < circles.size()/2; i++){
      Square square = (Square)circles.elementAt(i*2);

      if (square.equals(circleSquare)){
        circles.removeElementAt(i*2+1);
        circles.removeElementAt(i*2);
        i--;
      }
    }

    fireCircleRemoved(circleSquare);

    repaint();
  }





  /**
   * Removes all circles.
   */

  public void removeAllCircles(){
    for (int i = circles.size() - 2; i >= 0; i -= 2){
      Square square = (Square)circles.elementAt(i);
      
      circles.removeElementAt(i+1);
      circles.removeElementAt(i);

      fireCircleRemoved(square);
    }
  }



}
