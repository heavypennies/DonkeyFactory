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
 * A binary boolean <code>Conditional</code> which returns <code>false</code>
 * if and only if both the argument <code>Conditionals</code> return
 * <code>false</code>. Basically, an "or" operator.
 */

public final class Or extends BinaryBooleanConditional{


  /**
   * Creates a new <code>Or</code> <code>Conditional</code> with the
   * specified arguments.
   */

  public Or(Conditional arg1, Conditional arg2){
    super(arg1, arg2);
  }



  /**
   * Returns <code>false</code> if and only if the two arguments return
   * <code>false</code>.
   */

  public boolean eval(){
    return getArg1().eval() || getArg2().eval();
  }


}
