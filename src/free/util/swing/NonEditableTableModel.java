/**
 * The utillib library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The utillib library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The utillib library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with utillib library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package free.util.swing;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;


/**
 * An extension of <code>javax.swing.table.DefaultTableModel</code> none of
 * whose cells is editable.
 */

public class NonEditableTableModel extends DefaultTableModel{


  /**
   * Constructs a default NonEditableTableModel which is a table of zero columns
   * and zero rows.
   */

  public NonEditableTableModel(){

  }



  /**
   * Constructs a NonEditableTableModel with <code>numRows</code> and 
   * <code>numColumns</code> of <b>null</b> object values.
   */

  public NonEditableTableModel(int numRows, int numColumns){
    super(numRows, numColumns);
  }



  /**
   * Constructs a NonEditableTableModel and initializes the table by passing
   * <code>data</code> and <code>columnNames</code> to the setDataVector() 
   * method. The first index in the Object[][] is the row index and the second 
   * is the column index.
   */

  public NonEditableTableModel(Object [][] data, Object [] columnNames){
    super(data, columnNames);
  }



  /**
   * Constructs a NonEditableTableModel with as many columns as there are 
   * elements in <code>columnNames</code> and <code>numRows</code> of
   * <b>null</b> object values. Each column's name will be taken from the
   * <code>columnNames</code> array.
   */

  public NonEditableTableModel(Object [] columnNames, int numRows){
    super(columnNames, numRows);
  }



  /**
   * Constructs a NonEditableTableModel with as many columns as there are 
   * elements in <code>columnNames</code> and <code>numRows</code> of
   * <b>null</b> object values. Each column's name will be taken from the
   * columnNames vector.
   */

  public NonEditableTableModel(Vector columnNames, int numRows){
    super(columnNames, numRows);
  }



  /**
   * Constructs a NonEditableTableModel and initializes the table by passing
   * <code>data</code> and <code>columnNames</code> to the setDataVector() 
   * method.
   */

  public NonEditableTableModel(Vector data, Vector columnNames){
    super(data, columnNames);
  }


  
  /**
   * Returns false regardless of the parameters passed.
   */

  public boolean isCellEditable(int row, int column){
    return false;
  }

}
