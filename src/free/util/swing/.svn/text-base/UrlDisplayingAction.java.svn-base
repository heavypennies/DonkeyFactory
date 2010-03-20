/**
 * The utillib library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2003 Alexander Maryanovsky.
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

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import free.util.BrowserControl;


/**
 * An <code>Action</code> menu item, which, when activated, opens a browser at a specified url.
 */

public class UrlDisplayingAction extends AbstractAction{


  /**
   * The URL.
   */

  private final String url;



  /**
   * The parent frame.
   */

  private final Frame parent;



  /**
   * Creates a new <code>UrlDisplayingAction</code> with the specified parent
   * frame, and url.
   */

  public UrlDisplayingAction(Frame parent, String url){
    super();

    if (parent == null)
      throw new IllegalArgumentException("The parent frame may not be null");
    if ((url == null) || (url.length() == 0))
      throw new IllegalArgumentException("The url may not be null or empty");

    this.url = url;
    this.parent = parent;
  }




  /**
   * Creates a new <code>UrlDisplayingAction</code> with the specified parent
   * frame, url and name.
   */

  public UrlDisplayingAction(Frame parent, String url, String name){
    super(name);

    if (parent == null)
      throw new IllegalArgumentException("The parent frame may not be null");
    if ((url == null) || (url.length() == 0))
      throw new IllegalArgumentException("The url may not be null or empty");

    this.url = url;
    this.parent = parent;
  }




  /**
   * Creates a new <code>UrlDisplayingAction</code> with the specified parent
   * frame, url, name and icon.
   */

  public UrlDisplayingAction(Frame parent, String url, String name, Icon icon){
    super(name, icon);

    if (parent == null)
      throw new IllegalArgumentException("The parent frame may not be null");
    if ((url == null) || (url.length() == 0))
      throw new IllegalArgumentException("The url may not be null or empty");

    this.url = url;
    this.parent = parent;
  }




  /**
   * Returns the URL.
   */

  public String getURL(){
    return url;
  }




  /**
   * Tries to show the url using <code>BrowserControl</code>. Displays an error
   * message to the user if fails.
   */

  public void actionPerformed(ActionEvent evt){
    try{
      BrowserControl.displayURL(getURL());
    } catch (java.io.IOException e){
        JOptionPane.showMessageDialog(parent, "Unable to display URL: "+getURL(), "Error", JOptionPane.ERROR_MESSAGE);
      }
  }



}
