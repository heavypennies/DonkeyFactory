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

import javax.swing.JPasswordField;
import javax.swing.text.Document;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;


/**
 * A fix of JPasswordField. Fixes the following bugs:
 * <UL>
 *   <LI> <A HREF="http://developer.java.sun.com/developer/bugParade/bugs/4262163.html">
 *        Ibeam cursor not appearing on TextField or TextArea in editmode</A>.
 *   <LI> <A HREF="http://developer.java.sun.com/developer/bugParade/bugs/4133908.html">
 *        <Enter> in JTextComponent should activate DefaultButton
 * </UL>
 */

public class FixedJPasswordField extends JPasswordField{


  public FixedJPasswordField(){
    super();
  }


  public FixedJPasswordField(Document doc, String text, int columns){
    super(doc, text, columns);
  }


  public FixedJPasswordField(String text){
    super(text);
  }


  public FixedJPasswordField(int columns){
    super(columns);
  }


  public FixedJPasswordField(String text, int columns){
    super(text, columns);
  }


  
  public void setEditable(boolean editable){
    super.setEditable(editable);

    // http://developer.java.sun.com/developer/bugParade/bugs/4262163.html //

    Cursor cursor = getCursor();
    if (isEditable()&&((cursor==null)||(cursor.getType()!=Cursor.TEXT_CURSOR)))
      setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    else if (!isEditable()&&((cursor==null)||(cursor.getType()!=Cursor.TEXT_CURSOR)))
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    
    // http://developer.java.sun.com/developer/bugParade/bugs/4262163.html //

  }


  protected void processComponentKeyEvent(KeyEvent evt){
    
    // http://developer.java.sun.com/developer/bugParade/bugs/4133908.html

    if ((evt.getKeyCode()==KeyEvent.VK_ENTER)&&(listenerList.getListenerCount(ActionListener.class)==0))
      return;

      // Removes AWT compatibility regarding pressing ENTER generating an 
      // ActionEvent if there are no registered listeners.

    
    super.processComponentKeyEvent(evt);

    // http://developer.java.sun.com/developer/bugParade/bugs/4133908.html

  }

}
