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

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;
import free.workarounds.FixedJTable;
import free.jin.event.GameListEvent;
import free.jin.GameListItem;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import java.util.StringTokenizer;


/**
 * The table in which game list items are displayed in the console.
 */

public class GameListTable extends FixedJTable{



  /**
   * The console we're a part of.
   */

  protected final Console console;



  /**
   * The GameListEvent whose information this table is displaying.
   */

  protected final GameListEvent gameListEvent;



  /**
   * The popup we're currently displaying. We need this to fix a bug where the
   * popup doesn't get closed on the first click, only on the 2nd.
   */

  private JPopupMenu popup;




  /**
   * Creates a new GameListTable for the given GameListEvent and the Console
   * we're a part of.
   */

  public GameListTable(Console console, GameListEvent evt){
    this.console = console;
    this.gameListEvent = evt;

    setModel(createGameListModel(evt));

    setEnabled(false);

    javax.swing.ToolTipManager tooltipManager = javax.swing.ToolTipManager.sharedInstance();
    if (!tooltipManager.isEnabled())
      tooltipManager.setEnabled(true);

    // Note that this is redundant, as JTable registers itself also.
    // I do this because that behaviour is not documented, and so may change
    // in a future release.
    tooltipManager.registerComponent(this);

    enableEvents(MouseEvent.MOUSE_EVENT_MASK);
  }




  /**
   * Creates a TableModel for this GameListTable for the given GameListEvent.
   */

  protected TableModel createGameListModel(GameListEvent evt){
    return new GameListTableModel(evt);
  }




  /**
   * Creates and returns the JPopupMenu to be displayed on a popup trigger event.
   * Returns null if no popup should be displayed.
   * This method should take the current state of the table into consideration.
   * This is <B>not</B> an initializer method - it is called on every popup
   * trigger event.
   */

  protected JPopupMenu createPopup(){
    int numSelectedRows = getSelectedRowCount();
    if (numSelectedRows==0)
      return null;

    String issuedCommandName = commandNameForID(gameListEvent.getID());
    if (issuedCommandName==null)
      return null;

    String prefix = issuedCommandName+"-popup.";
    int numCommands = Integer.parseInt(console.getProperty(prefix+"num-commands", "0"));
    if (numCommands==0)
      return null;

    JPopupMenu popup = new JPopupMenu();
    int actualNumCommands = 0;
    for (int i=0;i<numCommands;i++){
      String command = console.getProperty(prefix+"command-"+i);

      if (command.equalsIgnoreCase("separator")){
        popup.addSeparator();
        continue;
      }

      String commandName = console.getProperty(prefix+"command-"+i+"-name");
      boolean isMultiSupported = new Boolean(console.getProperty(prefix+"command-"+i+"-multi-select-supported", "true")).booleanValue();
      if ((numSelectedRows>1)&&!isMultiSupported)
        continue;

      JMenuItem menuItem = new JMenuItem(commandName);
      menuItem.setActionCommand(command);
      menuItem.addActionListener(new ActionListener(){

        public void actionPerformed(ActionEvent evt){
          String actionCommand = ((AbstractButton)evt.getSource()).getActionCommand();
          int [] selectedRows = getSelectedRows();
          
          for (int rowIndex=0;rowIndex<selectedRows.length;rowIndex++){
            GameListItem item = gameListEvent.getItem(selectedRows[rowIndex]);
            String actualCommand = insertItemProperties(actionCommand, item, '$');
            console.issueCommand(new Command(actualCommand,0));
          }
        }
        
      });

      actualNumCommands++;
      popup.add(menuItem);
    }

    if (actualNumCommands==0)
      return null;

    return popup;
  }

  




  /**
   * Overrides <code>JComponent.getToolTipText(MouseEvent event)</code> to
   * display tooltips for cells whose preferred size is less than their actual
   * size.
   */

  public String getToolTipText(MouseEvent evt){
    Point point = evt.getPoint();
    int columnIndex = columnAtPoint(point);
    int rowIndex = rowAtPoint(point);

    if ((columnIndex==-1)||(rowIndex==-1))
      return null;

    int columnWidth = getColumnModel().getColumn(columnIndex).getWidth();

    TableCellRenderer cellRenderer = getCellRenderer(rowIndex, columnIndex);
    Object value = getModel().getValueAt(rowIndex, columnIndex);
    Component rendererComponent = cellRenderer.getTableCellRendererComponent(this, value, false, false, rowIndex, columnIndex);
    int prefWidth = rendererComponent.getPreferredSize().width;

    if (prefWidth>columnWidth)
      return String.valueOf(value);

    return null;
  }




  /**
   * Returns the location where the tooltip should be displayed.
   */

  public Point getToolTipLocation(MouseEvent evt){
    if (getToolTipText(evt)==null)
      return null;

    Point point = evt.getPoint();
    int columnIndex = columnAtPoint(point);
    int rowIndex = rowAtPoint(point);
    
    Rectangle cellRect = getCellRect(rowIndex, columnIndex, false);

    return cellRect.getLocation();
  }




  /**
   * Executes the action command when an item is double clicked.
   */

  protected void processMouseEvent(MouseEvent evt){
    super.processMouseEvent(evt);

    if ((evt.getID()==MouseEvent.MOUSE_CLICKED)&&(evt.getClickCount()==2)&&((evt.getModifiers()&MouseEvent.BUTTON1_MASK)!=0)){ // Double click.
      Point point = evt.getPoint();
      int rowIndex = rowAtPoint(point);
      GameListItem item = gameListEvent.getItem(rowIndex);
      String command = console.getProperty(commandNameForID(gameListEvent.getID())+"-game-list-action");
      command = insertItemProperties(command, item, '$');
      console.issueCommand(new Command(command, 0));
    }

    else if (evt.isPopupTrigger()){
      popup = createPopup();
      if (popup!=null){
        Component rootPane = SwingUtilities.getRootPane(this);
        Dimension rootPaneSize = rootPane.getSize();
        Point clickPointOnRootPane = SwingUtilities.convertPoint(this, evt.getPoint(), rootPane);
        int x = evt.getX();
        int y = evt.getY();
        int width = popup.getWidth();
        int height = popup.getHeight();
        if (clickPointOnRootPane.x+width>rootPaneSize.width)
          x = x-width;
        if (clickPointOnRootPane.y+height>rootPaneSize.height)
          y = y-height;
        
        popup.setLightWeightPopupEnabled(false); // Fixes a minor bug when the popup would go away only after the 2nd click somewhere
        popup.setSelected(null);
        popup.show(this,x,y);
      }
    }
  }




  /**
   * Processed the given command, replacing any substrings that look like
   * [delimiter][known game list item property name][delimiter] with the value
   * of the property for the given item.
   */

  private String insertItemProperties(String command, GameListItem item, char delimChar){
    String delim = String.valueOf(delimChar);
    StringTokenizer tokenizer = new StringTokenizer(command, delim, true);
    StringBuffer processedCommand = new StringBuffer(command.length());
    while (tokenizer.hasMoreTokens()){
      String token = tokenizer.nextToken();
      if (token.equals(delim)){
        if (tokenizer.hasMoreTokens()){
          String itemPropertyName = tokenizer.nextToken();
          if (tokenizer.hasMoreTokens()){
            tokenizer.nextToken(); // Kill the next delim
            String itemProperty = getItemProperty(item, itemPropertyName);
            if (itemProperty!=null)
              processedCommand.append(itemProperty);
            else
              processedCommand.append(delim).append(itemProperty).append(delim);
          }
          else
            processedCommand.append(token).append(itemPropertyName);
        }
        else
          processedCommand.append(token);
      }
      else
        processedCommand.append(token);
    }
    return processedCommand.toString();
  }




  /**
   * Returns the value of the property with the given name of the given
   * GameListItem. If the property name is unknown, returns null.
   */

  protected String getItemProperty(GameListItem item, String propertyName){
    if (propertyName.equals("id"))
      return item.getID();
    else if (propertyName.equals("index"))
      return String.valueOf(item.getIndex());
    return null;
  }
  



  /**
   * Maps GameListItem IDs to the names of the commands which issued them. 
   * Returns null if it's an unknown id.
   */

  protected String commandNameForID(int id){
    switch (id){
      case GameListEvent.HISTORY_LIST_EVENT_ID: return "history";
      case GameListEvent.STORED_LIST_EVENT_ID: return "stored";
      case GameListEvent.LIBLIST_EVENT_ID: return "liblist";
      case GameListEvent.SEARCH_LIST_EVENT_ID: return "search";
      default: return null;
    }
  }


}
