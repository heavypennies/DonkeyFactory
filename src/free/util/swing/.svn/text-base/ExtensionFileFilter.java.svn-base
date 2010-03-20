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

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 * An implementation of the <code>javax.swing.filechooser.FileFilter</code>
 * which accepts files whose name ends with a certain string. This makes it
 * useful for accepting files with a certain extension.
 */


public class ExtensionFileFilter extends FileFilter{



  /**
   * The name of the file filter.
   */

  private final String name;




  /**
   * The strings with one of which a file's name must end in order to pass the 
   * filter.
   */

  private final String [] endStrings;




  /**
   * <code>true</code> if the file filter is case sensitive, <code>false</code>
   * otherwise.
   */

  private final boolean isCaseSensitive;






  /**
   * <P>Creates a new ExtensionFileFilter with the given string. Only files
   * ending with that string will be accepted. Note that in order to use this
   * class to accept only files with a certain extension, you must also provide
   * the '.' character before the extension. For example, to accept only 'txt'
   * files, you must pass ".txt".
   * <P>The name of the FileFilter should be something that describes what kind
   * of files it accepts.
   */

  public ExtensionFileFilter(String fileFilterName, String endString, boolean isCaseSensitive){
    this(fileFilterName, new String[]{endString}, isCaseSensitive);
  }




  /**
   * <P>Creates a new ExtensionFileFilter with the given string array. Only
   * files ending with one of the strings in the given string array will be
   * accepted by the created ExtensionFileFilter.  Note that in order to use
   * this class to accept only files with a certain extension, you must also 
   * provide the '.' character before the extension. For example, to accept 
   * only 'txt' files, you must pass ".txt".
   * <P>The name of the FileFilter should be something that describes what kind
   * of files it accepts.
   */

  public ExtensionFileFilter(String fileFilterName, String [] endStrings, boolean isCaseSensitive){
    this.endStrings = new String[endStrings.length];
    for (int i=0;i<endStrings.length;i++){
      this.endStrings[i] = endStrings[i];
    }

    this.name = fileFilterName;
    this.isCaseSensitive = isCaseSensitive;
  }




  /**
   * Tests whether the specified file passes the filter. Returns true if the
   * file's name ends with one of the string specified in the constructor.
   */

  public boolean accept(File file){
    if (file.isDirectory())
      return true;

    for (int i = 0; i < endStrings.length; i++){
      String endString = endStrings[i];
      String filename = file.getName();
      String filenameEnd = filename.substring(filename.length() - endString.length());
      if (isCaseSensitive){
        if (filenameEnd.equals(endString))
          return true;
      }
      else if (filenameEnd.equalsIgnoreCase(endString))
        return true;
    }
    return false;
  }




  /**
   * Returns a description of this FileFilter.
   */

  public String getDescription(){
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < endStrings.length; i++)
      buf.append("*" + endStrings[i] + ", ");
    buf.setLength(buf.length() - 2);
    return name+" ("+buf.toString()+")";
  }


}
