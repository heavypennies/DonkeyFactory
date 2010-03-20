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

package free.jin;

import javax.swing.filechooser.FileFilter;
import java.io.*;


/**
 * Implements a FileChooser which accepts only user files which correspond to
 * a given server.
 */

public class ServerSpecificUserFileFilter extends FileFilter{


  /**
   * The Server whose user files we accept.
   */

  private final Server server;


  /**
   * Creates a new ServerSpecificUserFileFilter which accepts only the user
   * files of the given server.
   */

  public ServerSpecificUserFileFilter(Server server){
    this.server = server;
  }



  /**
   * Returns true if the given file has an extension equal to the short name of
   * the server.
   */

  public boolean accept(File file){
    return file.isDirectory() || file.getName().endsWith("."+server.getName());
  }


  /**
   * Returns the description of this file filter.
   */

  public String getDescription(){
    return server.getProperty("name.long")+" User Files (*."+server.getName()+")";
  }


}
