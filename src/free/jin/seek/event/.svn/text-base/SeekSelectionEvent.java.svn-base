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

package free.jin.seek.event;

import java.util.EventObject;
import free.jin.Seek;


/**
 * The event fired when the user selects a seek.
 */

public class SeekSelectionEvent extends EventObject{



  /**
   * The selected seek.
   */

  private final Seek seek;




  /**
   * Creates a new SeekSelectionEvent with the given source and selected Seek.
   */

  public SeekSelectionEvent(Object source, Seek seek){
    super(source);

    this.seek = seek;
  }



  /**
   * Returns the selected seek.
   */

  public Seek getSeek(){
    return seek;
  }


}
