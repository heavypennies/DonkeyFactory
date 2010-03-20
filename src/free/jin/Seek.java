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

import free.chess.WildVariant;
import free.chess.Player;
import free.util.Struct;


/**
 * A representation of a seek - a request for a player to play a game
 */

public class Seek extends Struct{



  /**
   * Creates a new Seek with the given properties.
   *
   * @param seekID A string identifying the game for the user.
   * @param seeker The handle of the player who issued the seek.
   * @param seekerTitle The title of the player who issued the seek.
   * @param rating The rating of the player who issued the seek.
   * @param isProvisional Is the rating of the player who issued the seek provisional?
   * @param isRegistered Is the seeker a registered player or a guest?
   * @param isComputer Is the seeker a computer player or a human?
   * @param variant The WildVariant of the sought game.
   * @param ratingCategoryString The name of the rating category to which the
   * sought game belongs.
   * @param time The amount of time on each player's clock when the game starts,
   * in milliseconds.
   * @param inc The increment to each player's clock after each move, in
   * milliseconds.
   * @param isRated True if the sought game is rated.
   * @param color The side on which the seeking player wants to be - Player.WHITE_PLAYER
   * if the seek is to play with white pieces, Player.BLACK_PLAYER if with black
   * or null if the seek is for a random color.
   * @param isRatingLimited Is the seeker limiting the rating of the players against
   * whom he wants to play. If this is false, the minRating and maxRating parameters
   * will be ignored.
   * @param minRating The minimum rating against which the seeking player is willing
   * to play.
   * @param maxRating The maximum rating against which the seeking player is willing
   * to play.
   * @param isManualAccept True if the seeking player wishes to manually confirm
   * that he wants to play against whoever accepts his seek.
   * @param isFormula True if in order to start the game, you must pass the seeking
   * player's formula.
   */

  public Seek(String seekID, String seeker, String seekerTitle, int rating, boolean isProvisional,
      boolean isRegistered, boolean isSeekerRated, boolean isComputer, WildVariant variant,
      String ratingCategoryString, int time, int inc, boolean isRated, Player color, boolean isRatingLimited,
      int minRating, int maxRating, boolean isManualAccept, boolean isFormula){

    setStringProperty("SeekID", seekID);
    setStringProperty("Seeker", seeker);
    setStringProperty("SeekerTitle", seekerTitle);
    setIntegerProperty("SeekerRating", rating);
    setBooleanProperty("IsProvisional", isProvisional);
    setBooleanProperty("IsRegistered", isRegistered);
    setBooleanProperty("IsSeekerRated", isSeekerRated);
    setBooleanProperty("IsComputer", isComputer);
    setProperty("Variant", variant);
    setStringProperty("RatingCategoryString", ratingCategoryString);
    setIntegerProperty("Time", time);
    setIntegerProperty("Inc", inc);
    setBooleanProperty("IsRated", isRated);
    if (color != null)
      setProperty("Color", color);
    setBooleanProperty("IsRatingLimited", isRatingLimited);
    setIntegerProperty("MinRating", minRating);
    setIntegerProperty("MaxRating", maxRating);
    setBooleanProperty("IsManualAccept", isManualAccept);
    setBooleanProperty("IsFormula", isFormula);
  }




  /**
   * Returns the ID of the game - this is something that should probably be
   * displayed to the user. 
   */

  public String getID(){
    return getStringProperty("SeekID");
  }




  /**
   * Returns the handle/nickname of the player who issued the seek.
   */

  public String getSeekerName(){
    return getStringProperty("Seeker");
  }




  /**
   * Returns the title of the player who issued the seek.
   */

  public String getSeekerTitle(){
    return getStringProperty("SeekerTitle");
  }




  /**
   * Returns the rating of the player who issued the seek.
   */

  public int getSeekerRating(){
    return getIntegerProperty("SeekerRating");
  }




  /**
   * Returns true if the rating of the player who issued the seek is provisional,
   * false otherwise.
   */

  public boolean isSeekerProvisional(){
    return getBooleanProperty("IsProvisional");
  }




  /**
   * Returns true if the player who issued the seek is a registered player, false
   * if he's a guest.
   */

  public boolean isSeekerRegistered(){
    return getBooleanProperty("IsRegistered");
  }





  /**
   * Returns <code>true</code> if the seeker is rated (has a rating),
   * <code>false</code> otherwise.
   */

  public boolean isSeekerRated(){
    return getBooleanProperty("IsSeekerRated");
  }




  /**
   * Returns true if the player who issued the seek is a computer player, false
   * if he's human.
   */

  public boolean isSeekerComputer(){
    return getBooleanProperty("IsComputer");
  }





  /**
   * Returns the WildVariant of the sought game. 
   */

  public WildVariant getVariant(){
    return (WildVariant)getProperty("Variant");
  }




  /**
   * Returns a String identifying the rating category of the game - this should
   * probably be displayed to the user.
   */

  public String getRatingCategoryString(){
    return getStringProperty("RatingCategoryString");
  }




  /**
   * Returns the amount of time on each player's clock at the start of the sought
   * game, in milliseconds.
   */

  public int getTime(){
    return getIntegerProperty("Time");
  }




  /**
   * Returns the amount of time added to each player's clock after he moves in
   * the sought game, in milliseconds.
   */

  public int getInc(){
    return getIntegerProperty("Inc");
  }




  /**
   * Returns true if the sought game is rated, false otherwise.
   */

  public boolean isRated(){
    return getBooleanProperty("IsRated");
  }




  /**
   * Returns a Player object representing what color the player who issued the
   * seek wants to play. This is null if the game will have a random color.
   */

  public Player getSoughtColor(){
    return (Player)getProperty("Color");
  }




  /**
   * Returns true if the seeker is limiting the rating of the players against
   * whom he wants to play. If this is false, the min rating and max rating
   * properties should be ignored.
   */

  public boolean isRatingLimited(){
    return getBooleanProperty("IsRatingLimited");
  }




  /**
   * Returns the minimum rating against which the player who issued the seek is
   * willing to play.
   */

  public int getMinRating(){
    return getIntegerProperty("MinRating");
  }




  /**
   * Returns the maximum rating against which the player who issued the seek is
   * willing to play.
   */

  public int getMaxRating(){
    return getIntegerProperty("MaxRating");
  }




  /**
   * Returns true if the player who issued the seek wants to manually confirm
   * that he wants to play whoever accepts his seek.
   */

  public boolean isManualAccept(){
    return getBooleanProperty("IsManualAccept");
  }




  /**
   * Returns true if the player who wants to accept the seek must pass the formula
   * of the player who issued the seek.
   */

  public boolean isFormula(){
    return getBooleanProperty("IsFormula");
  }

}
