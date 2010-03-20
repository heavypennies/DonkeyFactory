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

package free.util;

import java.awt.LayoutManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;


/**
 * A LayoutManager which lays out a single component making sure its height is
 * always the same as its width.
 */

public class SquareLayout implements LayoutManager{


  /**
   * Creates a new SquareLayout.
   */

  public SquareLayout(){

  }



  /**
   * Adds the specified component with the specified name to the layout. 
   */

  public void addLayoutComponent(String name, Component component){

  }


  /**
   * Lays out the container in the specified panel. 
   */

  public void layoutContainer(Container container){
    Component child = container.getComponent(container.getComponentCount()-1);
    Dimension parentSize = container.getSize();
    int minSize = parentSize.width < parentSize.height ? parentSize.width : parentSize.height;
    child.setBounds(0, 0, minSize, minSize);
  }

  

  /**
   * Calculates the minimum size dimensions for the specified panel given the 
   * components in the specified parent container. 
   */

  public Dimension minimumLayoutSize(Container container){
    Component child = container.getComponent(container.getComponentCount()-1);
    Dimension childMinSize = child.getMinimumSize();
    int maxSize = childMinSize.width > childMinSize.height ? childMinSize.width : childMinSize.height;
    return new Dimension(maxSize, maxSize);
  }




  /**
   * Calculates the preferred size dimensions for the specified panel given the
   * components in the specified parent container. 
   */

  public Dimension preferredLayoutSize(Container container){
    Component child = container.getComponent(container.getComponentCount()-1);
    Dimension childPrefSize = child.getPreferredSize();
    int maxSize = childPrefSize.width > childPrefSize.height ? childPrefSize.width : childPrefSize.height;
    return new Dimension(maxSize, maxSize);
  }




  /**
   * Removes the specified component from the layout. 
   */

  public void removeLayoutComponent(Component component){

  }



}
