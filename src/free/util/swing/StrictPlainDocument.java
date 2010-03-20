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

import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.AbstractDocument;
import java.awt.Toolkit;


/**
 * A PlainDocument extension which allows to disallow certain changes,
 * by implementing the isChangeAccepted(String, String) method and returning
 * false. A beep is emitted when an attempt to make an unacceptable change is 
 * made.
 */

public abstract class StrictPlainDocument extends PlainDocument{



  public StrictPlainDocument(){
    super();
  }



  protected StrictPlainDocument(AbstractDocument.Content c){
    super(c);
  }



  /**
   * Overrides PlainDocument.insertString(int, String, AttributeSet) to
   * check whether the change is accepted before applying the change.
   */

  public void insertString(int offs, String str, AttributeSet a) throws BadLocationException{
    String oldString = getText(0,getLength());
    String newString = new StringBuffer(oldString).insert(offs,str).toString();

    if (isChangeAccepted(oldString,newString))
      super.insertString(offs,str,a);
    else
      Toolkit.getDefaultToolkit().beep();
  }



  /**
   * Returns true if the change from the old text to the new text is acceptable,
   * returns false otherwise.
   */

  public abstract boolean isChangeAccepted(String oldText, String newText);


}
