/**
 * The workarounds library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2003 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The workarounds library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The workarounds library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the workarounds library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package free.workarounds;

import java.awt.Component;
import java.awt.Window;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import java.beans.PropertyVetoException;


/**
 * A fix of JInternalFrame. Fixes the following bugs:
 * <UL>
 *   <LI><A HREF="http://developer.java.sun.com/developer/bugParade/bugs/4109910.html">
 *        When a JInternalFrame is activated, focus is not transferred to its children</A>.
 *   <LI><A HREF="http://developer.java.sun.com/developer/bugParade/bugs/4230389.html">
 *        Adding a desktop icon to JDesktopPane causes null pointer exception</A>.
 *        Note that the workaround for this bug is not completely transparent -
 *        it changes the time when PropertyChangeListeners are invoked for the
 *        iconified property to when the frame is added to another component.
 *   <LI><A HREF="http://developer.java.sun.com/developer/bugParade/bugs/4769773.html">
 *        JInternalFrame.setMaximum(true) before adding it to JDesktopPane causes NPE</A>.
 *        Note that the workaround for this bug is not completely transparent -
 *        it changes the time when PropertyChangeListeners are invoked for the
 *        iconified property to when the frame is added to another component.
 * </UL>
 */

public class FixedJInternalFrame extends JInternalFrame{


  /**
   * Creates a new <code>FixedJInternalFrame</code>, per the contract of the
   * equivalent <code>JInternalFrame</code> constructor.
   */

  public FixedJInternalFrame(){
    super();
  }



  /**
   * Creates a new <code>FixedJInternalFrame</code>, per the contract of the
   * corresponding <code>JInternalFrame</code> constructor.
   */

  public FixedJInternalFrame(String title){
    super(title);
  }



  /**
   * Creates a new <code>FixedJInternalFrame</code>, per the contract of the
   * corresponding <code>JInternalFrame</code> constructor.
   */

  public FixedJInternalFrame(String title, boolean resizable){
    super(title, resizable);
  }



  /**
   * Creates a new <code>FixedJInternalFrame</code>, per the contract of the
   * corresponding <code>JInternalFrame</code> constructor.
   */

  public FixedJInternalFrame(String title, boolean resizable, boolean closable){
    super(title, resizable, closable);
  }



  /**
   * Creates a new <code>FixedJInternalFrame</code>, per the contract of the
   * corresponding <code>JInternalFrame</code> constructor.
   */

  public FixedJInternalFrame(String title, boolean resizable, boolean closable,
      boolean maximizable){
    super(title, resizable, closable, maximizable);
  }



  /**
   * Creates a new <code>FixedJInternalFrame</code>, per the contract of the
   * corresponding <code>JInternalFrame</code> constructor.
   */

  public FixedJInternalFrame(String title, boolean resizable, boolean closable,
      boolean maximizable, boolean iconifiable){
    super(title, resizable, closable, maximizable, iconifiable);
  }



  // <4109910>


  /**
   * The component that had the focus when this internal frame became
   * deselected.
   */

  private Component previousFocusedComponent = null;



  public void setSelected(boolean selected) throws PropertyVetoException{
    super.setSelected(selected);

    if (selected){
      if (findFocusOwner() == null){
        if (previousFocusedComponent != null)
          previousFocusedComponent.requestFocus();
        else if (!requestDefaultFocus())
          requestFocus();
      }
    }
    else
      previousFocusedComponent = findFocusOwner();
  }




  /**
   * Returns the current focus owner of this JInternalFrame or null if it's not
   * active or isn't displayable.
   */

  private Component findFocusOwner(){
    if (isSelected()){
      Window window = SwingUtilities.windowForComponent(this);
      if (window == null)
        return null;
      Component component = window.getFocusOwner();
      return isAncestorOf(component) ? component : null;
        // The "null" case shouldn't happen, because we are selected, but just in case...
    }
    else
      return null;
  }



  // </4109910>



  // <4230389>


  /**
   * This variable is set to true if this JInternalFrame has been iconified
   * (via setIcon(true)) before being added to a container.
   */

  private boolean speciallyIconified = false;



  /**
   * Overrides setIcon to work around bug 4230389 by not calling the superclass'
   * method if we don't have a parent yet.
   */

  public void setIcon(boolean iconified) throws PropertyVetoException{
    if ((getParent() == null) && (desktopIcon.getParent() == null)){
      if (speciallyIconified == iconified)
        return;

      Boolean oldValue = speciallyIconified ? Boolean.TRUE : Boolean.FALSE; 
      Boolean newValue = iconified ? Boolean.TRUE : Boolean.FALSE;
      fireVetoableChange(IS_ICON_PROPERTY, oldValue, newValue);

      speciallyIconified = iconified;
    }
    else
      super.setIcon(iconified);
  }




  /**
   * Overrides isIcon to remain consistent with setIcon.
   */

  public boolean isIcon(){
    return super.isIcon() || speciallyIconified;
  }


  // </4230389>


  // <4769773>


  /**
   * This variable is set to true if this JInternalFrame has been maximized
   * (via setMaximum(true)) before being added to a container.
   */

  private boolean speciallyMaximized = false;



  /**
   * Overrides setMaximum to work around bug 4230389 by not calling the
   * superclass' method if we don't have a parent yet.
   */

  public void setMaximum(boolean maximized) throws PropertyVetoException{
    if (getParent() == null){
      if (speciallyMaximized == maximized)
        return;

      Boolean oldValue = speciallyMaximized ? Boolean.TRUE : Boolean.FALSE; 
      Boolean newValue = maximized ? Boolean.TRUE : Boolean.FALSE;
      fireVetoableChange(IS_ICON_PROPERTY, oldValue, newValue);

      speciallyMaximized = maximized;
    }
    else
      super.setMaximum(maximized);
  }




  /**
   * Overrides isMaximum to remain consistent with setMaximum.
   */

  public boolean isMaximum(){
    return super.isMaximum() || speciallyMaximized;
  }


  // </4769773>

  

  // <4230389> and <4769773>
  
  /**
   * Make ourselves really iconified/maximized when added to something.
   */

  public void addNotify(){
    super.addNotify();

    if (speciallyIconified){
      try{
        setIcon(speciallyIconified);
        speciallyIconified = false;
      } catch (PropertyVetoException e){}
    }

    if (speciallyMaximized){
      try{
        setMaximum(speciallyMaximized);
        speciallyMaximized = false;
      } catch (PropertyVetoException e){}
    }
  }

  // </4230389> and </4769773>

}
