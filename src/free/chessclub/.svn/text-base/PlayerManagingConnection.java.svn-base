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


package free.chessclub;

import free.chessclub.level2.*;
import java.util.Hashtable;
import java.io.*;


/**
 * A Session which also manages lists of players online and their properties.
 * Note that if you tell the PlayerManagingConnection to track any of the properties
 * of the players, it will try to use DG_PLAYER_ARRIVED which is considered a
 * verbose datagram (16/05/2001) and is only sent to TDs. If you specify not to
 * track any properties, it will use PLAYER_ARRIVED_SIMPLE, which is not considered
 * verbose and can be sent to everyone.
 */

public class PlayerManagingConnection extends ChessclubConnection{

  
  /**
   * A hashtable mapping player names to Player objects.
   */

  private final Hashtable players = new Hashtable();




  /**
   * The DGs specifying player properties that we will keep track of.
   */

  private final int [] trackedDGs;




  /**
   * Creates a new PlayerManagingConnection which will connect to the given server
   * on the given port and will use the given username and the given password.
   * The created PlayerManagingConnection will keep track of the player properties given
   * in the trackedDG array. For example if the array contains DG_BULLET, the bullet
   * rating of all the players online will be available.
   */

  public PlayerManagingConnection(String hostname, int port, String username, String password, PrintStream echoStream, int [] trackedDGs){
    super(hostname,port,username,password,echoStream);

    for (int i=0;i<trackedDGs.length;i++){
      switch (trackedDGs[i]){
        case Datagram.DG_BULLET:
        case Datagram.DG_BLITZ:
        case Datagram.DG_STANDARD:
        case Datagram.DG_WILD:
        case Datagram.DG_BUGHOUSE:
        case Datagram.DG_LOSERS:
        case Datagram.DG_TIMESTAMP:
        case Datagram.DG_TITLES:
        case Datagram.DG_OPEN:
        case Datagram.DG_STATE:
          continue;
        default:
          throw new IllegalArgumentException("Datagram "+trackedDGs[i]+" is not a datagram specifying a player property");
      }
    }

    this.trackedDGs = (int [])trackedDGs.clone();

    setDGState(Datagram.DG_PLAYER_ARRIVED,true);
    setDGState(Datagram.DG_PLAYER_LEFT,true);

    for (int i=0;i<trackedDGs.length;i++)
      setDGState(trackedDGs[i],true);
  }




  /**
   * Returns true if the player with the given name is online.
   */

  public boolean isOnline(String playerName){
    return players.get(playerName.toLowerCase())!=null;
  }




  /**
   * Returns the Player with the given name, or null if the player with the given
   * name is not online.
   */

  public Player getPlayer(String playerName){
    return (Player)players.get(playerName.toLowerCase());
  }





  /**
   * Returns true if the given datagram is essential for the correct operation of
   * this Session, returns false otherwise.
   */

  protected boolean isEssentialDG(int dgNumber){
    for (int i=0;i<trackedDGs.length;i++)
      if (dgNumber==trackedDGs[i])
        return true;

    switch(dgNumber){
      case Datagram.DG_PLAYER_ARRIVED:
      case Datagram.DG_PLAYER_LEFT:
        return true;
    }
    return super.isEssentialDG(dgNumber);
  } 





  /**
   * Adds the arriving player to the list of players online.
   */

  protected void processPlayerArrived(Player player){
    players.put(player.getUsername().toLowerCase(),player);
  }



  /**
   * Adds the arriving player to the list of players online only if DG_PLAYER_ARRIVED is off,
   * which means we are working for client who didn't ask for any player properties.
   */

  protected void processPlayerArrivedSimple(String playerName){
    if (!isDGOn(Datagram.DG_PLAYER_ARRIVED)){
      Player player = new Player(playerName,"",new int[RATING_CATEGORIES_COUNT],
        new int[RATING_CATEGORIES_COUNT],0,true,0,0);
      players.put(playerName.toLowerCase(),player);
    }
  }



  /**
   * Removes the arriving player from the list of players online.
   */

  protected void processPlayerLeft(String playerName){
    players.remove(playerName);
  }



  /**
   * Updates the bullet rating of the player.
   */

  protected void processBulletRatingChanged(String playerName, int newRating, int newRatingType){
    Player player = getPlayer(playerName);
    player.setRating(BULLET,newRating,newRatingType);
  }




  /**
   * Updates the blitz rating of the player.
   */

  protected void processBlitzRatingChanged(String playerName, int newRating, int newRatingType){
    Player player = getPlayer(playerName);
    player.setRating(BLITZ,newRating,newRatingType);
  }




  /**
   * Updates the standard rating of the player.
   */

  protected void processStandardRatingChanged(String playerName, int newRating, int newRatingType){
    Player player = getPlayer(playerName);
    player.setRating(STANDARD,newRating,newRatingType);
  }




  /**
   * Updates the wild rating of the player.
   */

  protected void processWildRatingChanged(String playerName, int newRating, int newRatingType){
    Player player = getPlayer(playerName);
    player.setRating(WILD,newRating,newRatingType);
  }




  /**
   * Updates the bughouse rating of the player.
   */

  protected void processBughouseRatingChanged(String playerName, int newRating, int newRatingType){
    Player player = getPlayer(playerName);
    player.setRating(BUGHOUSE,newRating,newRatingType);
  }





  /**
   * Updates the losers rating of the player.
   */

  protected void processLosersRatingChanged(String playerName, int newRating, int newRatingType){
    Player player = getPlayer(playerName);
    player.setRating(LOSERS,newRating,newRatingType);
  }




  /**
   * Updates the titles of the player.
   */

  protected void processTitlesChanged(String playerName, String newTitles){
    Player player = getPlayer(playerName);
    player.setTitles(newTitles);
  }




  /**
   * Updates the open flag of the player.
   */

  protected void processOpenStateChanged(String playerName, boolean newOpenState){
    Player player = getPlayer(playerName);
    player.setOpen(newOpenState);
  }




  /**
   * Updates the state of the player.
   */

  protected void processPlayerStateChanged(String playerName, int newPlayerState, int gameNumber){
    Player player = getPlayer(playerName);
    player.setState(newPlayerState,gameNumber);
  }



}
