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

package free.jin.plugin;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * The superclass of panels containing preferences UI for plugins.
 */

public abstract class PreferencesPanel extends JPanel{



  /**
   * The sole ChangeEvent we need.
   */

  private final ChangeEvent changeEvent = new ChangeEvent(this);



  /**
   * Applies the changes done by the user.
   *
   * @throws BadChangesException if the changes done by the user are invalid.
   */

  public abstract void applyChanges() throws BadChangesException;




  /**
   * Adds a ChangeListener to the list of listeners receiving notifications when
   * the preferences change.
   */

  public void addChangeListener(ChangeListener listener){
    listenerList.add(ChangeListener.class, listener);
  }




  /**
   * Removes the given Changelistener from the list of listeners receiving
   * notifications when the preferences change.
   */

  public void removeChangeListener(ChangeListener listener){
    listenerList.remove(ChangeListener.class, listener);
  }




  /**
   * Fires a ChangeEvent to all interested listeners. This method should be
   * called by subclasses whenever the user changes one of the settings.
   */

  protected void fireStateChanged(){
    Object [] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i += 2){
      if (listeners[i] == ChangeListener.class){
        ChangeListener listener = (ChangeListener)listeners[i+1];
        listener.stateChanged(changeEvent);
      }
    }
  }


}
