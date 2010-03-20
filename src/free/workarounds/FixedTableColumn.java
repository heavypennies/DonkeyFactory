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

import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;


/**
 * A fix for TableColumn. Fixes the following bugs:
 * <UL>
 *   <LI>  <A HREF="http://developer.java.sun.com/developer/bugParade/bugs/4276838.html">
 *         TableColumn.getHeaderRenderer() default to null</A>
 * </UL>
 */

public class FixedTableColumn extends TableColumn{


  /**
   * Creates a new FixedTableColumn using a default model index of 0, default
   * width of 75, a null renderer and a null editor. 
   */

  public FixedTableColumn(){
    super();
    fixBugs();
  }
          

  /**
   * Creates a new FixedTableColumn using a default width of 75, a null renderer
   * and a null editor. 
   */

  public FixedTableColumn(int modelIndex){
    super(modelIndex);
    fixBugs();
  }


  /**
   * Creates a new FixedTableColumn using a null renderer and a null editor. 
   */

  public FixedTableColumn(int modelIndex, int width){
    super(modelIndex, width);
    fixBugs();
  }


  /**
   * Creates and initializes an instance of FixedTableColumn with modelIndex. 
   */

  public FixedTableColumn(int modelIndex, int width, TableCellRenderer cellRenderer, TableCellEditor cellEditor){
    super(modelIndex, width, cellRenderer, cellEditor);
    fixBugs();
  }
          


  /**
   * Fixes various bugs that can be fixed in the constructor. This method is
   * called from all the constructors.
   */

  protected void fixBugs(){

    // http://developer.java.sun.com/developer/bugParade/bugs/4276838.html //

    if (getHeaderRenderer()==null)
      setHeaderRenderer(createDefaultHeaderRenderer());

    // http://developer.java.sun.com/developer/bugParade/bugs/4276838.html //
  }
  

}
