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


/**
 * Encapsulates a Command issued by the user. A Command consists of the command
 * string and boolean flags specifying properties of the command. A Command can
 * be blanked, in which case it won't be echoed to the screen or saved anywhere.
 * A command can also be special, in which case it will be processed by special
 * code which is familiar with the particular command string. A regular Command
 * will simply have its command string be sent to the server and echoed to the screen.
 */

public class Command{



  /**
   * The special command flag.
   */

  public static final long SPECIAL_MASK = 0x1;




  /**
   * The blanked command flag.
   */

  public static final long BLANKED_MASK = 0x2;



  /**
   * The Command string.
   */

  private final String commandString;



  /**
   * The modifiers.
   */

  private final long modifiers;



  /**
   * Creates a new Command with the given command string and modifiers.
   */

  public Command(String commandString, long modifiers){
    this.commandString = commandString;
    this.modifiers = modifiers;
  }



  /**
   * Returns the command string.
   */

  public String getCommandString(){
    return commandString;
  }


  
  /**
   * Returns the modifiers of this Command.
   */

  public long getModifiers(){
    return modifiers;
  }




  /**
   * Returns true if this is a special command.
   */

  public boolean isSpecial(){
    return (modifiers&SPECIAL_MASK)!=0;
  }




  /**
   * Returns true if this is a blanked command.
   */

  public boolean isBlanked(){
    return (modifiers&BLANKED_MASK)!=0;
  }

 
}
