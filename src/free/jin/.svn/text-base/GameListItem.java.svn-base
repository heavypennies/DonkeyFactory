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

import java.util.Hashtable;


/**
 * An encapsulation of the properties that any game list item has. The
 * superclass of all specific game list items. Instances of this class are
 * immutable, and so should instances of its subclasses be.
 */

public abstract class GameListItem{


  /**
   * All of the game properties are kept in this Hashtable. There are just too
   * many of them to be confortably kept in instance variables :-)
   */

  private final Hashtable gameProperties = new Hashtable(100, 0.25f);




  /**
   * Creates a new GameListItem with the given properties.
   *
   * @param index The index of the game. The actual meaning may be redefined by
   * the subclass.
   * @param gameID The id of the game - something that uniquely identifies it.
   * @param dateString The date of the game. Whether this is end, start, estimated
   * time of beginning or anything else depends on the subclass.
   * @param timeString The time of the game. Whether this is end, start, estimated
   * time of beginning or anything else depends on the subclass.
   * @param whiteName The name of the player with the white pieces.
   * @param blackName The name of the player with the black pieces.
   * @param whiteTime The initial amount of time on the clock of the player 
   * with the white pieces, in milliseconds.
   * @param whiteInc The amount of time added to the clock of the player with
   * the white pieces every time he makes a move, in milliseconds.
   * @param blackTime The initial amount of time on the clock of the player 
   * with the black pieces, in milliseconds.
   * @param blackInc The amount of time added to the clock of the player with
   * the black pieces every time he makes a move, in milliseconds.
   * @param whiteRating The rating of the player with the white pieces.
   * @param blackRating The rating of the player with the black pieces.
   * @param isRated True if the game is rated, false otherwise.
   * @param variantName The name of the wild variant of the game.
   * @param ratingCategoryString The name of the category to which the game
   * belongs.
   * @param eco The ECO code of the game.
   */

  public GameListItem(int index, String gameID, String dateString, String timeString,
      String whiteName, String blackName, int whiteTime, int whiteInc, 
      int blackTime, int blackInc, int whiteRating, int blackRating, 
      boolean isRated, String variantName, String ratingCategoryName, String eco){
    
    setProperty("Index", new Integer(index));
    setProperty("GameID", gameID);
    setProperty("DateString", dateString);
    setProperty("TimeString", timeString);
    setProperty("WhiteName", whiteName);
    setProperty("BlackName", blackName);
    setProperty("WhiteTime", new Integer(whiteTime));
    setProperty("WhiteInc", new Integer(whiteInc));
    setProperty("BlackTime", new Integer(blackTime));
    setProperty("BlackInc", new Integer(blackInc));
    setProperty("WhiteRating", new Integer(whiteRating));
    setProperty("BlackRating", new Integer(blackRating));
    setProperty("IsRated", new Boolean(isRated));
    setProperty("VariantName", variantName);
    setProperty("RatingCategoryName", ratingCategoryName);
    setProperty("ECO", eco);
  }




  /**
   * Returns the value of the given property.
   */

  protected final Object getProperty(Object property){
    return gameProperties.get(property);
  }




  /**
   * Returns the value of the given integer property.
   */

  protected final int getIntegerProperty(Object property){
    return ((Integer)getProperty(property)).intValue();
  }




  /**
   * Returns the value of the given string property.
   */

  protected final String getStringProperty(Object property){
    return (String)getProperty(property);
  }




  /**
   * Returns the value of the given boolean property.
   */

  protected final boolean getBooleanProperty(Object property){
    return ((Boolean)getProperty(property)).booleanValue();
  }






  /**
   * Sets the value of the given property. A property may not have its value reset.
   */

  protected final void setProperty(Object property, Object propertyValue){
    Object oldValue = gameProperties.put(property, propertyValue);
    if (oldValue!=null){
      gameProperties.put(property, oldValue);
      throw new IllegalArgumentException("A property's value may not be reset - attempted to reset the value of "+property);
    }
  }





  /**
   * Returns the index of the game. The actual meaning of the index depends on
   * the superclass.
   */

  public int getIndex(){
    return getIntegerProperty("Index");
  }




  /**
   * Returns the id of the game.
   */

  public String getID(){
    return getStringProperty("GameID");
  }




  /**
   * Returns the date of the game. What this date exactly means (start, end
   * or whatever other relevant point in time) depends on the subclass.
   */

  public String getDateString(){
    return getStringProperty("DateString");
  }



  /**
   * Returns the time of the game. What this date exactly means (start, end
   * or whatever other relevant point in time) depends on the subclass.
   */

  public String getTimeString(){
    return getStringProperty("TimeString");
  }




  /**
   * Returns the name of the player with the white pieces.
   */

  public String getWhiteName(){
    return getStringProperty("WhiteName");
  }




  /**
   * Returns the name of the player with the black pieces.
   */

  public String getBlackName(){
    return getStringProperty("BlackName");
  }




  /**
   * Returns the initial time on the clock of the player with the white pieces,
   * in milliseconds.
   */

  public int getWhiteTime(){
    return getIntegerProperty("WhiteTime");
  }



  /**
   * Returns the number of milliseconds by which the clock of the white player
   * is incremented each time he makes a move. This could be used to indicate
   * increment in Bronstein-clock style, depending on the clock style used by
   * the server.
   */

  public int getWhiteInc(){
    return getIntegerProperty("WhiteInc");
  }




  /**
   * Returns the initial time on the clock of the player with the black pieces,
   * in milliseconds.
   */

  public int getBlackTime(){
    return getIntegerProperty("BlackTime");
  }




  /**
   * Returns the rating of the player with the white pieces.
   */

  public int getWhiteRating(){
    return getIntegerProperty("WhiteRating");
  } 




  /**
   * Returns the rating of the player with the black pieces.
   */

  public int getBlackRating(){
    return getIntegerProperty("BlackRating");
  } 






  /**
   * Returns the number of milliseconds by which the clock of the black player
   * is incremented each time he makes a move. This could be used to indicate
   * increment in Bronstein-clock style, depending on the clock style used by
   * the server.
   */

  public int getBlackInc(){
    return getIntegerProperty("BlackInc");
  }




  /**
   * Returns true if this game is rated, false otherwise.
   */

  public boolean isRated(){
    return getBooleanProperty("IsRated");
  }



  
  /**
   * Returns the name of the wild variant of the game.
   */

  public String getVariantName(){
    return getStringProperty("VariantName");
  }




  /**
   * Returns the name of the rating category to which the game belongs.
   */

  public String getRatingCategoryName(){
    return getStringProperty("RatingCategoryName");
  }




  /**
   * Returns the ECO code of the game.
   */

  public String getECO(){
    return getStringProperty("ECO");
  }

  
}
