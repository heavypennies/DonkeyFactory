/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2003 Alexander Maryanovsky.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package free.jin.gamelogger;

import bsh.Interpreter;
import bsh.EvalError;

  
/**
 * A small class encapsulating a logging rule. The rule includes a name, a
 * condition (when to log) and a filename (where to log to).
 */

class LoggingRule{



  /**
   * The name of the rule.
   */

  private String name;



  /**
   * The condition.
   */

  private String condition;



  /**
   * The filename of the file into which to log the game.
   */

  private String filename;




  /**
   * Creates a new <code>LoggingRule</code> with the specified name, condition
   * and filename. An <code>EvalError</code> is thrown if the condition is not
   * parseable.
   */

  public LoggingRule(String name, String condition, String filename) throws EvalError{
    setName(name);
    setCondition(condition);
    setFilename(filename);
  }




  /**
   * Creates a copy of the specified <code>LoggingRule</code>.
   */

  public LoggingRule(LoggingRule rule){
    this.name = rule.name;
    this.condition = rule.condition;
    this.filename = rule.filename;
  }




  /**
   * Sets the condition for logging the game.
   * An <code>EvalError</code> is thrown if the condition is not a valid 
   * boolean expression.
   */

  public void setCondition(String condition) throws EvalError{
    if ((condition == null) || (condition.length() == 0))
      throw new IllegalArgumentException();

    Interpreter bsh = new Interpreter();
    String [][] availableVars = GameLogger.getAvailableVars();
    for (int i = 0; i < availableVars.length; i++)
      bsh.eval(availableVars[i][0] + " = " + availableVars[i][1]);

    Object val = bsh.eval(condition);
    if (!(val instanceof Boolean))
      throw new EvalError("Not a boolean expression");

    this.condition = condition;
  }




  /**
   * Returns the logging condition.
   */

  public String getCondition(){
    return condition;
  }




  /**
   * Sets the name of this logging rule.
   */

  public void setName(String name){
    if ((name == null) || "".equals(name))
      throw new IllegalArgumentException();

    this.name = name;
  }




  /**
   * Returns the name of this logging rule.
   */

  public String getName(){
    return name;
  }




  /**
   * Sets the filename of the file into which to save the game.
   */

  public void setFilename(String filename){
    if ((filename == null) || "".equals(filename))
      throw new IllegalArgumentException();

    this.filename = filename;
  }




  /**
   * Returns the filename of the file into which to save the game.
   */

  public String getFilename(){
    return filename;
  }




  /**
   * Returns the name of this logging rule.
   */

  public String toString(){
    return getName();
  }


}
