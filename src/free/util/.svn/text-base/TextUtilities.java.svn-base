/**
 * The utillib library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002-2003 Alexander Maryanovsky.
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


/**
 * A utility class which provides many useful text manipulation methods.
 */

public class TextUtilities{


  /**
   * Pads the beginning of the given String with the given character until it's
   * <code>length</code> characters long. If the given String's size is already
   * <code>length</code> or larger, the given string is returned as is.
   */

  public static String padStart(String s, char c, int length){
    if (s.length()>=length)
      return s;

    StringBuffer buf = new StringBuffer(s);
    for (int i=s.length();i<length;i++)
      buf.insert(0,c);

    return buf.toString();
  }



  /**
   * Pads the end of the given String with the given character until it's
   * <code>length</code> characters long. If the given String's size is already
   * <code>length</code> or larger, the given string is returned as is.
   */

  public static String padEnd(String s, char c, int length){
    if (s.length()>=length)
      return s;

    StringBuffer buf = new StringBuffer(s);
    for (int i=s.length();i<length;i++)
      buf.append(c);

    return buf.toString();
  }



  /**
   * Pads the given String on both sides equally (if possible) with the given 
   * character until it's <code>length</code> characters long. If the given 
   * String's size is already <code>length</code> or larger, the given 
   * string is returned as is.
   */

  public static String padSides(String s, char c, int length){
    if (s.length()>=length)
      return s;

    StringBuffer buf = new StringBuffer(s);
    for (int i=s.length();i<length-1;i+=2){
      buf.insert(0,c);
      buf.append(c);
    }

    if (buf.length()<length)
      buf.insert(0,c);

    return buf.toString();
  }





  /**
   * <P>Returns a substring of the given StringBuffer's string which consists of
   * the characters from the beginning of it until the first occurrence of the
   * given delimiter string or if the delimiter doesn't occur, until the end
   * of the string. The StringBuffer is modified so it no longer contains those
   * characters or the delimiter.
   * <P>Examples:
   * <UL>
   *   <LI>nextToken(new StringBuffer("abcdefgh"), "de") returns "abc" and
   *       the StringBuffer is modified to represent the string "fgh".
   *   <LI>nextToken(new StringBuffer("abcdefgh"), "a") returns an empty string
   *       and the StringBuffer is modified to represent the string "bcdefgh".
   *   <LI>nextToken(new StringBuffer("abcdefgh"), "k") returns "abcdefgh" and
   *       the StringBuffer is modified to represent an empty string.
   * </UL>
   */

  public static String nextToken(StringBuffer buf, String delimiter){
    String bufStr = buf.toString();
    int delimIndex = bufStr.indexOf(delimiter);

    if (delimIndex==-1){
      buf.setLength(0);
      return bufStr;
    }
      
    String str = bufStr.substring(0,delimIndex);
    buf.reverse();
    buf.setLength(buf.length()-delimIndex-delimiter.length());
    buf.reverse();

    return str;
  }





  /**
   * Returns an array of the tokens produced by the specified string with the
   * specified delimiter characters.
   */

  public static String [] getTokens(String text, String delimiters){
    StringTokenizer tokenizer = new StringTokenizer(text, delimiters);
    String [] tokens = new String[tokenizer.countTokens()];
    for (int i = 0; i < tokens.length; i++)
      tokens[i] = tokenizer.nextToken();

    return tokens;
  }


}
