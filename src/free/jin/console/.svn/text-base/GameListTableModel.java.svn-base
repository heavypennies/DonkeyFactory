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

package free.jin.console;

import javax.swing.table.*;
import free.jin.*;
import javax.swing.event.TableModelListener;
import free.jin.event.GameListEvent;
import free.chess.Player;


/**
 * An implementation of the TableModel for <code>GameListTable</code>.
 */

public class GameListTableModel implements TableModel{


  /**
   * The data in the table.
   */

  private final String [][] tableData;



  /**
   * The columnNames.
   */

  private final String [] columnNames;




  /**
   * Creates a new GameListTableModel for the given GameListEvent.
   */

  public GameListTableModel(GameListEvent evt){
    int id = evt.getID();
    int itemCount = evt.getItemCount();

    if (id==GameListEvent.HISTORY_LIST_EVENT_ID){
      tableData = new String[itemCount][12];

      for (int i=0;i<itemCount;i++){
        HistoryListItem item = (HistoryListItem)evt.getItem(i);
        Player player = item.getPlayer();
        tableData[i][0] = String.valueOf(item.getIndex()); // Index
        tableData[i][1] = getHistoryResultCodeString(item.getResultStatus(), player); // Result string
        tableData[i][2] = String.valueOf(player.isWhite() ? item.getWhiteRating() : item.getBlackRating()); // Player rating.
        tableData[i][3] = player.toString().substring(0,1); // Player color;
        tableData[i][4] = String.valueOf(player.isWhite() ? item.getBlackRating() : item.getWhiteRating()); // Opponent rating
        tableData[i][5] = player.isWhite() ? item.getBlackName() : item.getWhiteName(); // Opponent nickname.
        tableData[i][6] = getTCString(item.getVariantName(), item.getWhiteTime(), item.getWhiteInc(), item.getBlackTime(), item.getBlackInc());
        tableData[i][7] = item.isRated() ? "r" : "u"; // rated?
        tableData[i][8] = item.getRatingCategoryName(); // Rating category name
        tableData[i][9] = item.getECO(); // ECO code
        tableData[i][10] = item.getEndExplanationString(); // end explanation
        tableData[i][11] = item.getDateString()+" "+item.getTimeString(); // Time of the game
      }

      columnNames = new String[]{"#", "Result", "Rating", "Color", "Opp. rating", "Opponent", "Time controls", "Rated", "Category", "ECO", "End", "Date game started"};
    }
    else if (id==GameListEvent.SEARCH_LIST_EVENT_ID){
      tableData = new String[itemCount][11];

      for (int i=0;i<itemCount;i++){
        SearchListItem item = (SearchListItem)evt.getItem(i);
        tableData[i][0] = String.valueOf(item.getIndex()); // Index
        tableData[i][1] = item.getWhiteName(); // White's nickname
        tableData[i][2] = item.getWhiteRating() == -1 ? "?" : String.valueOf(item.getWhiteRating()); // White's rating
        tableData[i][3] = item.getBlackName(); // Black's nickname
        tableData[i][4] = item.getBlackRating() == -1 ? "?" : String.valueOf(item.getBlackRating()); // Black's rating
        tableData[i][5] = item.getEndExplanationString(); // end explanation
        tableData[i][6] = getTCString(item.getVariantName(), item.getWhiteTime(), item.getWhiteInc(), item.getBlackTime(), item.getBlackInc());
        tableData[i][7] = item.isRated() ? "r" : "u"; // rated?
        tableData[i][8] = item.getRatingCategoryName(); // Rating category name
        tableData[i][9] = item.getECO(); // ECO code
        tableData[i][10] = item.getDateString()+" "+item.getTimeString(); // Time of the game
      }

      columnNames = new String[]{"#", "White Player", "Rating", "Black Player", "Rating", "Result", "Time controls", "Rated", "Category", "ECO", "Date game started"};
    }
    else if (id==GameListEvent.LIBLIST_EVENT_ID){
      tableData = new String[itemCount][12];

      for (int i=0;i<itemCount;i++){
        LibListItem item = (LibListItem)evt.getItem(i);
        tableData[i][0] = String.valueOf(item.getIndex()); // Index
        tableData[i][1] = item.getWhiteName(); // White's nickname
        tableData[i][2] = item.getWhiteRating() == -1 ? "?" : String.valueOf(item.getWhiteRating()); // White's rating
        tableData[i][3] = item.getBlackName(); // Black's nickname
        tableData[i][4] = item.getBlackRating() == -1 ? "?" : String.valueOf(item.getBlackRating()); // Black's rating
        tableData[i][5] = item.getEndExplanationString(); // end explanation
        tableData[i][6] = getTCString(item.getVariantName(), item.getWhiteTime(), item.getWhiteInc(), item.getBlackTime(), item.getBlackInc());
        tableData[i][7] = item.isRated() ? "r" : "u"; // rated?
        tableData[i][8] = item.getRatingCategoryName(); // Rating category name
        tableData[i][9] = item.getECO(); // ECO code
        tableData[i][10] = item.getDateString()+" "+item.getTimeString(); // Time of the game
        tableData[i][11] = item.getNote(); // Note
      }

      columnNames = new String[]{"#", "White Player", "Rating", "Black Player", "Rating", "Result", "Time controls", "Rated", "Category", "ECO", "Date game started", "Note"};
    }
    else if (id==GameListEvent.STORED_LIST_EVENT_ID){
      tableData = new String[itemCount][11];

      for (int i=0;i<itemCount;i++){
        StoredListItem item = (StoredListItem)evt.getItem(i);
        Player player = item.getPlayer();
        tableData[i][0] = item.isOpponentPresent() ? "Present" : "";
        tableData[i][1] = String.valueOf(player.isWhite() ? item.getWhiteRating() : item.getBlackRating()); // Player rating.
        tableData[i][2] = player.toString().substring(0,1); // Player color;
        tableData[i][3] = String.valueOf(player.isWhite() ? item.getBlackRating() : item.getWhiteRating()); // Opponent rating
        tableData[i][4] = player.isWhite() ? item.getBlackName() : item.getWhiteName(); // Opponent nickname.
        tableData[i][5] = getTCString(item.getVariantName(), item.getWhiteTime(), item.getWhiteInc(), item.getBlackTime(), item.getBlackInc());
        tableData[i][6] = item.isRated() ? "r" : "u"; // rated?
        tableData[i][7] = item.getRatingCategoryName(); // Rating category name
        tableData[i][8] = item.getECO(); // ECO code
        tableData[i][9] = item.getDateString()+" "+item.getTimeString(); // Time of the game
        tableData[i][10] = item.getAdjournmentReason();
      }

      columnNames = new String[]{"Opp. present?", "Rating", "Color", "Opp. rating", "Opponent", "Time controls", "Rated", "Category", "ECO", "Date game started", "Game adjourned"};
    }
    else 
      throw new IllegalArgumentException("Unknown GameListEvent ID encountered");
  }



  /**
   * Returns the string that should be displayed in the history list for the 
   * given game result code and player object representing the color of the
   * player whose history is being displayed. Possible result codes are defined
   * in <code>JinConnection</code>.
   */

  protected String getHistoryResultCodeString(int resultCode, Player player){
    switch (resultCode){
      case JinConnection.WHITE_WON:
        if (player.isWhite())
          return "+";
        else
          return "-";
      case JinConnection.WHITE_LOST:
        if (player.isBlack())
          return "+";
        else
          return "-";
      case JinConnection.DRAWN:
        return "=";
      case JinConnection.ABORTED:
        return "a";
      default:
        throw new IllegalArgumentException("Illegal result code encountered");
    }
  }




  /**
   * Returns the string to be displayed for the given time control. The times
   * are given in milliseconds.
   */

  protected String getTCString(String wildName, int whiteTime, int whiteInc, int blackTime, int blackInc){
    if ((whiteTime==blackTime)&&(whiteInc==blackInc)) // regular.
      return ""+(whiteTime/(60*1000))+" "+(whiteInc/1000)+" "+wildName;
    else
      return ""+(whiteTime/(60*1000))+" "+(whiteInc/1000)+" : "+(blackTime/(60*1000))+" "+(blackInc/1000)+" "+wildName;
  }




  /**
   * Always returns String.class, since we're not displaying anything but
   * strings.
   */

  public Class getColumnClass(int columnIndex){
    return String.class;
  }



  /**
   * Returns the amount of columns in this TableModel.
   */

  public int getColumnCount(){
    return columnNames.length;
  }




  /**
   * Returns the name of the given column.
   */

  public String getColumnName(int index){
    return columnNames[index];
  }




  /**
   * Returns the amount of rows in this TableModel.
   */

  public int getRowCount(){
    return tableData.length;
  }




  /**
   * Returns the value at the given cell.
   */

  public Object getValueAt(int rowIndex, int columnIndex){
    return tableData[rowIndex][columnIndex];
  }




  /**
   * Always returns false.
   */

  public boolean isCellEditable(int rowIndex, int columnIndex){
    return false;
  }



  /**
   * This method always throws an UnsupportedOperationException, since the data
   * of this table isn't meant to be modified.
   */

  public void setValueAt(Object aValue, int rowIndex, int columnIndex){
    throw new free.util.UnsupportedOperationException("Changing the values of this table is not supported");
  }




  /**
   * This method doesn't do anything at all - there's no point registering TableModelListeners
   * since the data of this table never changes.
   */

  public void addTableModelListener(TableModelListener l){}




  /**
   * This method doesn't do anything at all - see <code>addTableModelListener</code>.
   */

  public void removeTableModelListener(TableModelListener l){}

}
