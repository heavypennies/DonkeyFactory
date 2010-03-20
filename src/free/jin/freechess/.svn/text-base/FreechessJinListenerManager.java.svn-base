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

package free.jin.freechess;

import free.jin.event.BasicJinListenerManager;
import free.jin.event.SeekListener;


/**
 * A freechess.org specific extension of BasicJinListenerManager. Used by
 * <code>free.jin.freechess.JinFreechessConnection</code>
 */

public class FreechessJinListenerManager extends BasicJinListenerManager{



  /**
   * The source JinFreechessConnection.
   */

  private final JinFreechessConnection source;




  /**
   * Creates a new FreechessJinListenerManager with the given source
   * <code>JinFreechessConnection</code>.
   */

  public FreechessJinListenerManager(JinFreechessConnection source){
    super(source);

    this.source = source;
  }




  /**
   * Adds the given SeekListener to the list of listeners receiving notification
   * of SeekEvents.
   */

  public void addSeekListener(SeekListener listener){
    super.addSeekListener(listener);

    if (listenerList.getListenerCount(SeekListener.class) == 1)
      source.setSeekInfo(true);
    else
      source.notFirstListenerAdded(listener);
  }




  /**
   * Removes the given SeekListener from the list of listeners receiving 
   * notification of SeekEvents.
   */

  public void removeSeekListener(SeekListener listener){
    super.removeSeekListener(listener);

    if (listenerList.getListenerCount(SeekListener.class) == 0){
      source.setSeekInfo(false);

      source.lastSeekListenerRemoved();
    }
  }


}
