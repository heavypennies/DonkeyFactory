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

package free.util;

import java.util.StringTokenizer;
import java.awt.Rectangle;
import java.awt.Color;


/**
 * A utility class which parses certain common types from Strings.
 *
 * @see StringEncoder
 */

public class StringParser{

  
  /**
   * Parses the given String as a Rectangle object. The expected format is:
   * "<x>;<y>;<width>;<height>" where <value> is replaced by an integer value.
   *
   * @throws FormatException if the given String is not in the expected wrong
   * format.
   *
   * @see StringEncoder#encodeRectangle(Rectangle)
   */

  public static Rectangle parseRectangle(String rectString){
    StringTokenizer tokenizer = new StringTokenizer(rectString,";");
    if (tokenizer.countTokens()!=4)
      throw new FormatException("Wrong Rectangle format: "+rectString);
    
    int x,y,width,height;
    try{
      x = Integer.parseInt(tokenizer.nextToken());
      y = Integer.parseInt(tokenizer.nextToken());
      width = Integer.parseInt(tokenizer.nextToken());
      height = Integer.parseInt(tokenizer.nextToken());
    } catch (NumberFormatException e){
        throw new FormatException(e,"Wrong Rectangle format: "+rectString);
      }

    return new Rectangle(x,y,width,height);
  }




  /**
   * Parses the given string as a color. The expected format is a hexadecimal
   * value in the range [0..0xffffff] in RGB format.
   *
   * @throws FormatException if the given String is not in the correct format.
   */

  public static Color parseColor(String colorString){
    try{
      int colorInt = Integer.parseInt(colorString,16);
      if ((colorInt<0)||(colorInt>0xffffffL))
        throw new FormatException("Wrong Color format: "+colorString);

      return new Color(colorInt);
    } catch (NumberFormatException e){
        throw new FormatException(e,"Wrong Color format: "+colorString);
      }
  }

}
