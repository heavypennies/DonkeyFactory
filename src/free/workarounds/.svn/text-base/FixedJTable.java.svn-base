/**
 * The workarounds library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The workarounds library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The workarounds library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the workarounds library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package free.workarounds;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.ListSelectionModel;
import java.awt.Dimension;
import java.util.Vector;


/**
 * A fix for JTable. Fixes the following bugs:
 * <UL>
 *   <LI>  <A HREF="http://developer.java.sun.com/developer/bugParade/bugs/4276838.html">
 *         TableColumn.getHeaderRenderer() default to null</A>
 *   <LI>  <A HREF="http://developer.java.sun.com/developer/bugParade/bugs/4104863.html">
 *         JTable returns a big (hardcoded) preferred size</A>
 * </UL>
 */

public class FixedJTable extends JTable{


  /**
   * Constructs a default FixedJTable which is initialized with a default data model,
   * a default column model, and a default selection model. 
   */

  public FixedJTable(){
    super();
  }



  /**
   * Constructs a FixedJTable with numRows and numColumns of empty cells using the
   * DefaultTableModel. 
   */

  public FixedJTable(int numRows, int numColumns){
    super(numRows, numColumns);
  }



  /**
   * Constructs a FixedJTable to display the values in the two dimensional array, 
   * rowData, with column names, columnNames. 
   */

  public FixedJTable(Object[][] rowData, Object[] columnNames){
    super(rowData, columnNames);
  }


  
  /**
   * Constructs a FixedJTable which is initialized with dm as the data model, a 
   * default column model, and a default selection model. 
   */

  public FixedJTable(TableModel dm){
    super(dm);
  }


  
  /**
   * Constructs a FixedJTable which is initialized with dm as the data model, cm as 
   * the column model, and a default selection model. 
   */

  public FixedJTable(TableModel dm, TableColumnModel cm){
    super(dm, cm);
  }



  /**
   * Constructs a FixedJTable which is initialized with dm as the data model, cm as 
   * the column model, and sm as the selection model. 
   */
          
  public FixedJTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm){
    super(dm, cm, sm);
  }
          

  
  /**
   *Constructs a FixedJTable to display the values in the Vector of Vectors, rowData,
   * with column names, columnNames. 
   */

  public FixedJTable(Vector rowData, Vector columnNames){
    super(rowData, columnNames);
  }



  /**
   * Fixes bug 4276838 by using FixedTableColumn instead of TableColumn.
   */

  public void createDefaultColumnsFromModel() {
    TableModel m = getModel();
    if (m != null) {
      // Remove any current columns
      TableColumnModel cm = getColumnModel();
      cm.removeColumnModelListener(this);
      while (cm.getColumnCount() > 0)
        cm.removeColumn(cm.getColumn(0));

      // Create new columns from the data model info
      for (int i = 0; i < m.getColumnCount(); i++) {
        FixedTableColumn newColumn = new FixedTableColumn(i);
        addColumn(newColumn);
      }
      cm.addColumnModelListener(this);
    }
  }


          
  
  /**
   * Fixes bug 4104863 by setting the preferredScrollableViewportSize to null.
   * The <code>getPreferredScrollableViewportSize()</code> method in this class
   * cooperates with this method and returns the preferred JTable size as long
   * as the preferredScrollableViewportSize is null.
   */

  protected void initializeLocalVars(){
    super.initializeLocalVars();
    preferredViewportSize = null;
  }




  /**
   * Fixes bug 4104863 by returning the preferred size of the JTable as long
   * as preferredScrollableViewportSize is null. Also see the
   * <code>initializeLocalVars()</code> method.
   */

  public Dimension getPreferredScrollableViewportSize(){
    if (preferredViewportSize==null)
      return getPreferredSize();
    return preferredViewportSize;
  }

}
