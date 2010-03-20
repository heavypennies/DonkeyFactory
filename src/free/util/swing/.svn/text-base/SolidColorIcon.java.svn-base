/**
 * The utillib library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The utillib library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The utillib library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with utillib library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package free.util.swing;

import java.awt.*;
import javax.swing.Icon;


/**
 * An implementation of the Icon interface which draws a solid color icon.
 * The actual drawing (the shape) is left to subclasses.
 */

public abstract class SolidColorIcon implements Icon{


  
  /**
   * The dimension of the icon.
   */

  private final Dimension size;




  /**
   * The color of the icon.
   */

  private final Color color;




  /**
   * Creates a new SolidColorIcon of the given size and Color.
   */

  public SolidColorIcon(Dimension size, Color color){
    if (size == null)
      throw new IllegalArgumentException("Size is null");
    if (color == null)
      throw new IllegalArgumentException("Color is null");

    this.size = new Dimension(size);
    this.color = color;
  }




  /**
   * Returns the width of the icon.
   */

  public int getIconWidth(){
    return size.width;
  }




  /**
   * Returns the height of the icon.
   */

  public int getIconHeight(){
    return size.height;
  }




  /**
   * Returns the color of the icon.
   */

  private Color getColor(){
    return color;
  }




  /**
   * Paints a solid rectangle at the given coordinates.
   */

  public void paintIcon(Component component, Graphics g, int x, int y){
    g.setColor(color);
    paintShape(component, g, x, y);
  }




  /**
   * Draws the actual shape of the icon. The color of the <code>Graphics</code>
   * object is already set to the icon's color.
   */

  public abstract void paintShape(Component component, Graphics g, int x, int y);

}
