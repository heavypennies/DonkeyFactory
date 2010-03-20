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

package free.jin.event;

import java.util.EventListener;


/**
 * The interface for receiving notifications about seeks being added to and 
 * removed from the sought list.
 */

public interface SeekListener extends EventListener{


  /**
   * This method is called when a seek is added to the sought list.
   */

  void seekAdded(SeekEvent evt);



  /**
   * This method is called when a seek is removed from the sought list.
   */

  void seekRemoved(SeekEvent evt);

}
