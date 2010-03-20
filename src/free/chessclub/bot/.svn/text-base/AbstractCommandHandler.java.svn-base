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

import java.util.Hashtable;


/**
 * An abstract implementation of the CommandHandler interface which implements
 * a basic command handler framework. It allows to specify who is allowed to
 * issue the command and whether a tell will be sent to the player informing him
 * that the command was issued successfully.
 */

public abstract class AbstractCommandHandler implements CommandHandler{



  /**
   * Whether to notify the player who sent the command that we issued the sent
   * command successfully.
   */

  private final boolean isEchoed;



  /**
   * The set of people allowed to issue the command. The hashtable maps
   * player names to themselves.
   */

  private final Hashtable authorizedPeople;




  /**
   * Creates a new AbstractCommandHandler with the given list of people
   * authorized to issue the command. Pass <code>null</code> if you want 
   * everyone to be authorized. The <code>isEchoed</code> argument specifies
   * whether a tell will be sent to the player informing him the command was
   * issued successfully.
   */

  public AbstractCommandHandler(String [] authorizedPeopleList, boolean isEchoed){
    this.isEchoed = isEchoed;
    if (authorizedPeopleList!=null){
      authorizedPeople = new Hashtable();
      for (int i=0; i<authorizedPeopleList.length; i++){
        String person = authorizedPeopleList[i].toLowerCase();
        authorizedPeople.put(person, person);
      }
    }
    else
      authorizedPeople = null;
  }



  /**
   * Returns true if a tell will be sent back to the player after issuing the
   * command.
   */

  public boolean isEchoed(){
    return isEchoed;
  }




  /**
   * Returns true if the given CommandEvent is authorized to be handled. The
   * default implementation returns true if the player who issued the command
   * is in the list of people authorized to issue it.
   */

  protected boolean isAuthorized(CommandEvent command){
    return (authorizedPeople==null)||authorizedPeople.containsKey(command.getPlayerName().toLowerCase());
  }




  /**
   * If the player is authorized to issue the command, this method delegates to
   * the <code>handleAuthorizedCommand</code> method. If the player is not
   * authorized, sends him an appropriate tell informing him he's not
   * authorized. If the player is authorized and the <code>isEchoed</code> method
   * returns true, the <code>sendEcho</code> method is called.
   */

  public final void handleCommand(CommandEvent command){
    if (isAuthorized(command)){
      boolean successful = handleAuthorizedCommand(command);
      if (successful&&isEchoed())
        sendEcho(command);
    }
    else
      command.getBot().sendTell(command.getPlayerName(), "You are not to authorized to use this command.");
  }




  /**
   * Handles the given command. This method is called only if the player is
   * authorized to issue the command. Returns <code>true</code> if the command 
   * was handled successfully, <code>false</code> otherwise.
   */

  public abstract boolean handleAuthorizedCommand(CommandEvent command);



  
  /**
   * Sends the player a tell informing him the command was handled successfully.
   */

  public void sendEcho(CommandEvent command){
    Bot bot = command.getBot();
    String playerName = command.getPlayerName();
    String issuedCommand = command.getCommand();
    bot.sendTell(playerName, "Succesfully processed command: "+issuedCommand);
  } 

}
