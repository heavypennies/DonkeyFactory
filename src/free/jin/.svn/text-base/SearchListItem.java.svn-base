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


/**
 * An encapsulation of the properties of a search result list item.
 */

public class SearchListItem extends GameListItem{


  /**
   * Creates a new SearchListItem with the given GameListItem properties,
   * given "game end explanation string" and result status. Possible result 
   * status values are defined in <code>JinConnection</code>.
   */

  public SearchListItem(int index, String gameID, String dateString, String timeString,
      String whiteName, String blackName, int whiteTime, int whiteInc, int blackTime,
      int blackInc, int whiteRating, int blackRating, boolean isRated,
      String variantName, String ratingCategoryName, String eco,
      String endExplanationString, int resultStatus){

    super(index, gameID, dateString, timeString, whiteName, blackName, whiteTime,
      whiteInc, blackTime, blackInc, whiteRating, blackRating, isRated,
      variantName, ratingCategoryName, eco);

    setProperty("EndExplanationString", endExplanationString);
    setProperty("ResultStatus", new Integer(resultStatus));
  }



  /**
   * Returns a string explanining how the game ended.
   */

  public String getEndExplanationString(){
    return getStringProperty("EndExplanationString");
  }




  /**
   * Returns the result status of the game.  Possible result status
   * values are defined in <code>JinConnection</code>.
   */

  public int getResultStatus(){
    return getIntegerProperty("ResultStatus");
  }

}
