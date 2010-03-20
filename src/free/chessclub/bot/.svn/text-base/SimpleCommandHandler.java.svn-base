/**
 * The chessclub.com connection library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The chessclub.com connection library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The chessclub.com connection library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the chessclub.com connection library; if not, write to the Free
 * Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package free.chessclub.bot;


/**
 * A command handler which sends the given string to the server when the command
 * it was registered with is issued.
 */

public class SimpleCommandHandler extends AbstractCommandHandler{



  /**
   * The string specified by the user to send to the server.
   */

  private final String commandString;
                                   


  /**
   * Creates a new SimpleCommandHandler with the given command to send to the 
   * server and list of people who are allowed to ask the bot to send that
   * command. If you want everyone to be authorized to issue the command, pass
   * <code>null</code> as the <code>authorizedPeopleList</code> argument.
   * If <code>isEcho</code> is true, a tell will be sent back to the
   * player informing him that the command was sent to the server successfully.
   * The strings "%n", where n is replaced by an argument index will be replaced
   * with the argument at that index when sending the command to the server. The
   * strings "%sender" and "%bot" will be replaced by the handle of the player
   * who sent the command and the name of the account accordingly.
   */
  
  public SimpleCommandHandler(String commandString, String [] authorizedPeopleList, boolean echo){
    super(authorizedPeopleList, echo);
    this.commandString = commandString;
  }





  /**
   * Creates a new SimpleCommandListener with the given command to send to the
   * server and a list of people who are allowed to ask the bot to send that
   * command. If you want everyone to be authorized to issue the command, pass
   * <code>null</code> as the <code>authorizedPeopleList</code> argument.
   */

  public SimpleCommandHandler(String commandString, String [] authorizedPeopleList){
    this(commandString, authorizedPeopleList, true);
  }




  /**
   * Creates a new SimpleCommandListener with the given command to send to the
   * server. Everyone will be allowed to ask the bot to issue that command.
   */

  public SimpleCommandHandler(String commandString){
    this(commandString, null, true);
  }





  /**
   * Returns the command sent to the server.
   */

  public String getCommandString(){
    return commandString;
  }




  /**
   * Processes the commandString in the context of the given command and returns
   * the actual string that should be sent to the server. Returns null if it's 
   * impossible to process the command string because it requires non existing 
   * arguments. This happens for example if the commandString is "tell %sender 
   * Hello %0, I %1 you", but there are less than 2 arguments.
   */

  public String getSentCommand(CommandEvent command){
    String commandString = getCommandString();
    String [] args = command.getArguments();
    String playerName = command.getPlayerName();
    Bot bot = command.getBot();

    int index = 0;
    while ((index = commandString.indexOf("%sender", index))!=-1){
      commandString = commandString.substring(0, index)+playerName+commandString.substring(index+"%sender".length());
      index += playerName.length();
    }
    index = 0;
    while ((index = commandString.indexOf("%bot", index))!=-1){
      commandString = commandString.substring(0, index)+bot.getUsername()+commandString.substring(index+"%bot".length());
      index += playerName.length();
    }
    index = 0;
    while ((index = commandString.indexOf("%", index))!=-1){
      int i = index+1;
      while ((i<commandString.length())&&Character.isDigit(commandString.charAt(i)))
        i++;
      if (i-(index+1)>0){
        String argIndexString = commandString.substring(index+1, i);
        int argIndex = Integer.parseInt(argIndexString);
        if (argIndex>=args.length)
          return null;
        commandString = commandString.substring(0, index)+args[argIndex]+commandString.substring(i);
        index = index+1+args[argIndex].length();
      }
      else
        index++;
    }
                     
    return commandString;
  }



  /**
   * Sends the required command to the server.
   */

  public boolean handleAuthorizedCommand(CommandEvent command){
    String issuedCommand = getSentCommand(command);
    Bot bot = command.getBot();
    if (issuedCommand==null){
      bot.sendTell(command.getPlayerName(), "Not enough arguments specified");
      return false;
    }
    else{
      bot.sendCommand(issuedCommand);
      return true;
    }
  }




  /**
   * Sends a tell to the player informing him that the command was issued
   * successfully.
   */

  public void sendEcho(CommandEvent command){
    String issuedCommand = getSentCommand(command);
    command.getBot().sendTell(command.getPlayerName(), "Issued the following command: "+issuedCommand);
  }

}
