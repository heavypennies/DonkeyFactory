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


/**
 * A StrictPlainModel which only allows inputting an integer value in a certain
 * range.
 */

public class IntegerStrictPlainDocument extends StrictPlainDocument{

  
  /**
   * The minimum allowed value.
   */

  private final long minValue;



  /**
   * The maximum allowed value.
   */

  private final long maxValue;



  /**
   * The radix of the input.
   */

  private final int radix;




  /**
   * Creates a new IntegerStrictPlainDocument which will only allow inputting
   * integer values in the given radix in the range [minValue..maxValue].
   *
   * @param minValue The minimum allowed value.
   * @param maxValue The maximum allowed value.
   * @param radix The radix of the input.
   */

  public IntegerStrictPlainDocument(long minValue, long maxValue, int radix){
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.radix = radix;
  }




  /**
   * Creates a new IntegerStrictPlainDocument which will only allow inputting
   * integer values in the range [minValue..maxValue].
   *
   * @param minValue The minimum allowed value.
   * @param maxValue The maximum allowed value.
   */

  public IntegerStrictPlainDocument(long minValue, long maxValue){
    this(minValue, maxValue, 10);
  }




  /**
   * Returns true if the new text represents an integer in the correct range when
   * parsed using the appropriate radix, returns false otherwise.
   */

  public boolean isChangeAccepted(String oldText, String newText){
    if ("".equals(newText))
      return true;
    try{
      long val = Long.parseLong(newText,getRadix());
      if ((val<getMinValue())||(val>getMaxValue()))
        return false;
      else
        return true;
    } catch (NumberFormatException e){
        return false;
      }
  }




  /**
   * Returns the radix used by this IntegerStrictPlainDocument to parse the text.
   */
  
  public int getRadix(){
    return radix;
  }




  /**
   * Returns the minimal allowed value.
   */

  public long getMinValue(){
    return minValue;
  }



  /**
   * Returns the maximal allowed value. 
   */

  public long getMaxValue(){
    return maxValue;
  } 

  

}
