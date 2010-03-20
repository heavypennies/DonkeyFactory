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

package free.chessclub.bot.commands;

import free.chessclub.bot.AbstractCommandHandler;
import free.chessclub.bot.CommandEvent;


/**
 * A handler for a "spoof" command. The handler will issue the command specified
 * by the first argument.
 */

public class SpoofCommandHandler extends AbstractCommandHandler{


  /**
   * Creates a new SpoofCommandHandler with the given list of people authorized
   * to spoof the bot and specifying whether the sent command will be echoed to
   * the user. The authorized people list may not be null.
   */

  public SpoofCommandHandler(String [] authorizedPeopleList, boolean isEchoed){
    super(authorizedPeopleList, isEchoed);
    if (authorizedPeopleList==null)
      throw new IllegalArgumentException("You may not allow everyone to spoof the bot.");
  }



  /**
   * Creates a new SpoofCommandHandler with the given list of people authorized
   * to spoof the bot and which echoed the issued command to the player.
   */

  public SpoofCommandHandler(String [] authorizedPeopleList){
    this(authorizedPeopleList, true);
  }




  /**
   * Sends the message sent by the user (command not included) to the server.
   */

  public boolean handleAuthorizedCommand(CommandEvent evt){
    String commandToSend = evt.getMessage().substring(evt.getCommand().length()+1);
    evt.getBot().sendCommand(commandToSend);
    return true;
  }

}