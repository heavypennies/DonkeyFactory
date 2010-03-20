/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2003 Alexander Maryanovsky.
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

package free.jin;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Rectangle;


/**
 * The <code>DesktopManager</code> for Jin's main frame.
 */

public class JinDesktopManager extends DefaultDesktopManager{


  /**
   * The main frame.
   */

  private final JinFrame jinFrame;



  /**
   * Creates a new JinDesktopManager with the specified <code>JinFrame</code>.
   */

  public JinDesktopManager(JinFrame jinFrame){
    this.jinFrame = jinFrame;
  }


  

  /**
   * Overrides <code>closeFrame</code> to transfer the focus to some other frame
   * if the superclass doesn't (happens in JDK 1.1).
   */

  public void closeFrame(JInternalFrame f){
    super.closeFrame(f);
   
    JInternalFrame [] frames = jinFrame.getDesktop().getAllFrames();
    boolean isSelected = false;
    for (int i = 0; i < frames.length ; i++){
      if (frames[i].isSelected()){
        isSelected = true;
        break;
      }
    }

    if (!isSelected)
      jinFrame.getInternalFrameSwitcher().selectPrevious();
  }




  /**
   * This method makes sure the user doesn't do stupid things like moving the
   * internal frame out of reach.
   */

  public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight){
    JDesktopPane desktop = jinFrame.getDesktop();
    Dimension desktopSize = desktop.getSize();
    Rectangle jifBounds = f.getBounds();
    final int MARGIN = 100;

    // Determine which border is being dragged by checking which sides are
    // not at their original locations.

    boolean left = (jifBounds.x != newX);
    boolean top = (jifBounds.y != newY);
    boolean right = (newX + newWidth != jifBounds.x + jifBounds.width);
    boolean bottom = (newY + newHeight != jifBounds.y + jifBounds.height);

    int x1 = newX;
    int y1 = newY;
    int x2 = x1 + newWidth;
    int y2 = y1 + newHeight;

    // Adjust the appropriate sides
    if (right)
      x2 = Math.min(Math.max(x2, MARGIN), x1 + desktopSize.width);
    if (bottom)
      y2 = Math.min(y2, y1 + desktopSize.height);
    if (left)
      x1 = Math.min(Math.max(x1, x2 - desktopSize.width), desktopSize.width - MARGIN);
    if (top)
      y1 = Math.max(Math.min(Math.max(y1, 0), desktopSize.height - MARGIN), y2 - desktopSize.height);
    
    super.resizeFrame(f, x1, y1, x2 - x1, y2 - y1);
  }


  /**
   * This method makes sure the user doesn't do stupid things like moving the
   * internal frame out of reach.
   */

  public void dragFrame(JComponent f, int newX, int newY){
    JDesktopPane desktop = jinFrame.getDesktop();
    Dimension desktopSize = desktop.getSize();

    newX = Math.max(Math.min(newX, desktopSize.width - 100), 100 - f.getWidth());
    newY = Math.max(Math.min(newY, desktopSize.height - 100), 0);

    super.dragFrame(f, newX, newY);
  }
      
}
