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
import java.awt.event.KeyEvent;
import java.awt.Component;


/**
 * Jin's focus manager. Basically used to implement custom functionality and
 * functionality that Swing is supposed to have but doesn't (like ctrl+tab
 * switching between internal windows).
 */

public class JinFocusManager extends DefaultFocusManager{



  /**
   * The main Jin frame.
   */

  private final JinFrame jinFrame;



  /**
   * Creates a new <code>JinFocusManager</code> with the specified Jin main
   * frame.
   */

  public JinFocusManager(JinFrame jinFrame){
    this.jinFrame = jinFrame;
  }


  

  /**
   * Overrides </code>DefaultFocusManager.processKeyEvent</code> to implement
   * window switching on ctrl+tab.
   */

  public void processKeyEvent(Component focusedComponent, KeyEvent evt){
    if (SwingUtilities.windowForComponent(evt.getComponent()) != jinFrame){
      super.processKeyEvent(focusedComponent, evt);
      return;
    }

    int keyCode = evt.getKeyCode();
    if (((keyCode == KeyEvent.VK_TAB) && evt.isControlDown()) ||
        ((keyCode == KeyEvent.VK_F6) && evt.isControlDown()) ||
        ((keyCode == KeyEvent.VK_BACK_QUOTE) && evt.isControlDown())){
      evt.consume();
      if (evt.getID() == KeyEvent.KEY_RELEASED)
        return;

      if (evt.isShiftDown())
        jinFrame.getInternalFrameSwitcher().selectPrevious();
      else
        jinFrame.getInternalFrameSwitcher().selectNext();
    }
    else{
      super.processKeyEvent(focusedComponent, evt);
    }
  }

}

