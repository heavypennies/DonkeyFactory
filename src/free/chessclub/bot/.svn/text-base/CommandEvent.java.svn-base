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

import java.util.EventObject;


/**
 * The event object passed to CommandHandlers.
 */

public class CommandEvent extends EventObject{


  /**
   * The command.
   */

  private final String command;



  /**
   * The arguments.
   */

  private final String [] args;



  /**
   * The raw message as sent by the user.
   */

  private final String message;



  /**
   * The player who issued the command.
   */

  private final String playerName;



  /**
   * The titles of the player who issued the command.
   */

  private final String titles;





  /**
   * Creates a new CommandEvent.
   *
   * @param bot The bot to which the command was issued.
   * @param command The command issued by the user.
   * @param args The arguments to the command given by the user.
   * @param message The raw, unparsed message sent by the user.
   * @param playerName The ICC handle of the player who issued the command.
   * @param titles The ICC titles of the player who issued the command.
   */

  public CommandEvent(Bot bot, String command, String [] args, String message, 
      String playerName, String titles){

    super(bot);
    this.command = command;
    this.args = (String [])args.clone();
    this.message = message;
    this.playerName = playerName;
    this.titles = titles;
  }




  /**
   * Returns the Bot to which the command was issued.
   */

  public Bot getBot(){
    return (Bot)getSource();
  }




  /**
   * Returns the command issued by the user.
   */

  public String getCommand(){
    return command;
  }




  /**
   * Returns the arguments to the command given by the user.
   */

  public String [] getArguments(){
    return args;
  }



  /**
   * Returns the raw and untouched message as sent by the user.
   */

  public String getMessage(){
    return message;
  }



  /**
   * Returns the ICC nickname of the player who issued the command.
   */

  public String getPlayerName(){
    return playerName;
  }



  /**
   * Returns the ICC titles of the player who issued the command.
   */

  public String getPlayerTitles(){
    return titles;
  }



  /**
   * Returns true if the player who issed the command is an ICC admin with his
   * '*' turned on. Returns false otherwise.
   */

  public boolean isPlayerAdmin(){
    return playerHasTitle("*");
  } 



  /**
   * Returns true if the player who issued the command is a titled player. This
   * includes the following titles: FM, IM, GM, WIM, WGM and doesn't include DM.
   */

  public boolean isPlayerTitled(){
    return playerHasTitle("FM")||playerHasTitle("IM")||playerHasTitle("GM")||
      playerHasTitle("WIM")||playerHasTitle("WGM");
  }



  /**
   * Returns true if the player who issued the command has the given title.
   * Returns false otherwise.
   */

  public boolean playerHasTitle(String title){
    return titles.indexOf(title)!=-1;
  }


}

