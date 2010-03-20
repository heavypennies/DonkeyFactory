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
 * A unary boolean <code>Conditional</code> which returns the opposite value of
 * the value returned by the argument. A "not" operator.
 */

public final class Not extends UnaryBooleanConditional{


  /**
   * Creates a new <code>Not</code> <code>Conditional</code> with the
   * specified argument.
   */

  public Not(Conditional arg){
    super(arg);
  }



  /**
   * Returns <code>true</code> if the argument <code>Conditional</code> returns
   * <code>false</code> and vice versa.
   */

  public boolean eval(){
    return !getArg().eval();
  }


}
