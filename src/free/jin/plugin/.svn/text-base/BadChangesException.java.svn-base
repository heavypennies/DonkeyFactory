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

package free.jin.plugin;

import java.awt.Component;


/**
 * The exception thrown when the user's changes to the preferences/settings
 * were bad (unfinished) and couldn't be applied.
 */

public class BadChangesException extends Exception{



  /**
   * The component that lets the user to set/change the preference/setting that
   * was deemed bad.
   */

  private final Component errorComponent;



  /**
   * Creates a new BadChangesException with the specified error message and
   * the component that lets the user specify the preference/setting that was
   * deemed bad. The component can be <code>null</code> if unappliccable.
   */

  public BadChangesException(String errorMessage, Component errorComponent){
    super(errorMessage);

    this.errorComponent = errorComponent;
  }




  /**
   * Returns the component that allows the user to set/change the preference
   * setting that was deemed to be bad. Returns <code>null</code> if not
   * applicable.
   */

  public Component getErrorComponent(){
    return errorComponent;
  }



}
