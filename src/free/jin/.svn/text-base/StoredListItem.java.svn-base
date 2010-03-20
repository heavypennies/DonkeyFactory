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

import free.chess.Player;


/**
 * An encapsulation of the properties of a stored list item.
 */

public class StoredListItem extends GameListItem{


  /**
   * Creates a new StoredListItem with the given GameListItem properties,
   * whether the opponent is present, the reason of the adjournmen and the player
   * for whom the command was issued.
   */

  public StoredListItem(int index, String gameID, String dateString, String timeString,
      String whiteName, String blackName, int whiteTime, int whiteInc, int blackTime,
      int blackInc, int whiteRating, int blackRating, boolean isRated,
      String variantName, String ratingCategoryName, String eco, boolean isOpponentPresent,
      String adjournmentReason, Player player){

    super(index, gameID, dateString, timeString, whiteName, blackName, whiteTime,
      whiteInc, blackTime, blackInc, whiteRating, blackRating, isRated,
      variantName, ratingCategoryName, eco);

    setProperty("IsOpponentPresent", new Boolean(isOpponentPresent));
    setProperty("AdjournmentReason", adjournmentReason);
    setProperty("Player", player);
  }



  /**
   * Returns true if the opponent is present, false otherwise.
   */

  public boolean isOpponentPresent(){
    return getBooleanProperty("IsOpponentPresent");
  }




  /**
   * Returns the reason for the adjournment.
   */

  public String getAdjournmentReason(){
    return getStringProperty("AdjournmentReason");
  }




  /**
   * Returns the player to whose stored games list this StoredListItem belongs.
   */

  public Player getPlayer(){
    return (Player)getProperty("Player");
  }


}
