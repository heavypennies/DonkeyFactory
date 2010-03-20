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


/**
 * Thrown when the format of something is wrong.
 */

public class FormatException extends IllegalArgumentException{



  /**
   * The "real" exception that was thrown.
   */

  private final Throwable realException;

  


  /**
   * Creates a new FormatException for the given real Throwable and the given
   * message.
   */

  public FormatException(Throwable realException, String message){
    super(message);
    this.realException = realException;
  }


  /**
   * Creates a new FormatException for the given real Throwable.
   */

  public FormatException(Throwable realException){
    this(realException,null);
  }


  /**
   * Creates a new FormatException with the given message.
   */

  public FormatException(String message){
    this(null,message);
  }



  /**
   * Creates a new FormatException.
   */

  public FormatException(){
    this(null,null);
  }



  /**
   * Returns the actual throwable that was thrown.
   */

  public Throwable getThrowable(){
    return realException;
  }



  /**
   * Prints the stack trace of this FormatException to the standard error stream.
   */

  public void printStackTrace(){ 
    printStackTrace(System.err);
  }



  /**
   * Prints the stack trace of this FormatException to the specified PrintStream.
   *
   * @param s <code>PrintStream</code> to use for output
   */

  public void printStackTrace(java.io.PrintStream s) { 
    synchronized(s){
      if (realException!=null){
        s.println("++++");
        super.printStackTrace(s);
        realException.printStackTrace(s);
        s.println("----");
      }
      else
        super.printStackTrace(s);
    }
  }



  /**
   * Prints the stack trace of this FormatException to the specified PrintWriter.
   *
   * @param s <code>PrintWriter</code> to use for output
   */

  public void printStackTrace(java.io.PrintWriter s) { 
    synchronized(s){
      if (realException!=null){
        s.println("++++");
        super.printStackTrace(s);
        realException.printStackTrace(s);
        s.println("----");
      }
      else
        super.printStackTrace(s);
    }
  }

}
