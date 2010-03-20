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

package free.jin.chessclub;


/**
 * Encapsulates information about a chess event on the server.
 */

public class ChessEvent{



  /**
   * The id of the event.
   */

  private final int id;



  /**
   * The description of the chess event.
   */

  private final String description;




  /**
   * The server commands to issue to join the event. This is <code>null</code>
   * when you can't join the event.
   */

  private final String [] joinCommands;




  /**
   * The server commands to issue to watch the event. This is <code>null</code>
   * when you can't watch the event.
   */

  private final String [] watchCommands;




  /**
   * The server commands to issue to get information about the event. This is
   * <code>null</code> when no such information is available.
   */

  private final String [] infoCommands;




  /**
   * The text to display when asking the user to confirm his joining. This is
   * <code>null</code> when no such confirmation is needed.
   */

  private final String confirmText;




  /**
   * Creates a new ChessEvent with the given information.
   *
   * @param id A number identifying the event uniquely at a given moment in
   * time.
   * @param description A description of the chess event.
   * @param joinCommands The commands to issue to join the event or 
   * <code>null</code> if you can't join the event.
   * @param watchCommands The commands to issue to watch the event or
   * <code>null</code> if you can't watch the event.
   * @param infoCommands The commands to issue to get information about the
   * event or <code>null</code> if no such information is available.
   * @param confirmText The text to display to the user when confirming his
   * joining the event or <code>null</code> if no such confirmation is needed.
   */

  public ChessEvent(int id, String description, String [] joinCommands,
      String [] watchCommands, String [] infoCommands, String confirmText){

    this.id = id;
    this.description = description;
    this.joinCommands = (joinCommands == null ? null : (String [])joinCommands.clone());
    this.watchCommands = (watchCommands == null ? null : (String [])watchCommands.clone());
    this.infoCommands = (infoCommands == null ? null : (String [])infoCommands.clone());
    this.confirmText = confirmText;
  }




  /**
   * Returns the index of this ChessEvent. This should identify the event
   * uniquely from other ChessEvents at a given moment in time.
   */

  public int getID(){
    return id;
  }




  /**
   * Returns a description of the ChessEvent.
   */

  public String getDescription(){
    return description;
  }




  /**
   * Returns an array containing the commands needed to execute to join the
   * event, or null if you can't join the event.
   */

  public String [] getJoinCommands(){
    return (joinCommands == null ? null : (String [])joinCommands.clone());
  }




  /**
   * Returns true if the user can join this event.
   */

  public boolean canJoin(){
    return joinCommands != null;
  }




  /**
   * Returns an array containing the commands needed to execute to watch the
   * event, or null if you can't watch the event.
   */

  public String [] getWatchCommands(){
    return (watchCommands == null ? null : (String [])watchCommands.clone());
  }




  /**
   * Returns true if the user can watch this event.
   */

  public boolean canWatch(){
    return watchCommands != null;
  }




  /**
   * Returns an array containing the commands needed to execute to get more
   * information about the event, or null if no such information is available.
   */

  public String [] getInformationCommands(){
    return (infoCommands == null ? null : (String [])infoCommands.clone());
  }




  /**
   * Returns true if there is additional information about this event. 
   */

  public boolean hasInformation(){
    return infoCommands != null;
  }




  /**
   * Returns the text to show to the user when asking him to confirm his joining
   * the event. Returns <code>null</code> if no such confirmation is necessary.
   */

  public String getConfirmationText(){
    return confirmText;
  }




  /**
   * Returns true if confirmation is necessary when the user asks to join the
   * event.
   */

  public boolean isConfirmationNeeded(){
    return confirmText!=null;
  }





  /**
   * Returns a textual representation of this ChessEvent.
   */
  
  public String toString(){
    return getDescription();
  }


}
