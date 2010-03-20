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

package free.jin.console;

import javax.swing.text.Position;


/**
 * An class encapsulating a link in a ConsoleTextPane. A Link consists of 2 integers
 * specifying the start and end of the link in the text and the 
 */

public class Link{


  /**
   * The starting position of the link.
   */

  private final Position startPosition;



  /**
   * The ending position of the link.
   */

  private final Position endPosition;



  /**
   * The Command to execute when the link is clicked.
   */

  private final Command command;




  /**
   * Creates a new Link with the given starting and ending positions and the 
   * <code>Command</code> to execute when the link is clicked.
   */

  public Link(Position startPosition, Position endPosition, Command command){
    this.startPosition = startPosition;
    this.endPosition = endPosition;
    this.command = command;
  }




  /**
   * Returns the starting <code>Position</code> of the link in the text.
   */

  public Position getStartPosition(){
    return startPosition;
  }



  /**
   * Returns the ending <code>Position</code> of the link in the text.
   */

  public Position getEndPosition(){
    return endPosition;
  }



  /**
   * Returns the Command to be issued when the link is clicked.
   */

  public Command getCommand(){
    return command;
  }


}
