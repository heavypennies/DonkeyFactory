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

package free.util.bool;


/**
 * A superclass for all binary boolean <code>Conditionals</code>.
 */

public abstract class BinaryBooleanConditional implements Conditional{


  /**
   * The first argument.
   */

  private final Conditional arg1;




  /**
   * The second argument.
   */

  private final Conditional arg2;




  /**
   * Creates a new <code>BinaryBooleanConditional</code> with the specified
   * arguments.
   */

  public BinaryBooleanConditional(Conditional arg1, Conditional arg2){
    if (arg1 == null)
      throw new IllegalArgumentException("First argument may not be null");
    if (arg2 == null)
      throw new IllegalArgumentException("First argument may not be null");

    this.arg1 = arg1;
    this.arg2 = arg2;
  }



  /**
   * Returns the first argument <code>Conditional</code>.
   */

  public final Conditional getArg1(){
    return arg1;
  }



  /**
   * Returns the 2nd argument <code>Conditional</code>.
   */

  public final Conditional getArg2(){
    return arg2;
  }



}
