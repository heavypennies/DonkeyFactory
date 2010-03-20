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

package free.jin.scripter;

import free.jin.event.JinEvent;
import free.jin.plugin.PluginContext;


/**
 * An abstract class that various script types extend. The sole abstract method
 * that needs to be implemented is <code>run(JinEvent)</code>.
 * IMPORTANT: When creating a new Script class, don't forget to properly
 * override the <code>createCopy</code> method.
 */

public abstract class Script{



  /**
   * The PluginContext we're working in.
   */

  private final PluginContext context;



  /**
   * The type of the event that must occur for this script to be run.
   */

  private final String eventType;



  /**
   * The list of event subtypes this <code>Script</code> is invoked on. May be
   * <code>null</code> if the event type of this <code>Script</code> doesn't
   * have any subtypes.
   */

  private final String [] eventSubtypes;



  /**
   * The name of the script.
   */

  private final String name;



  /**
   * Is the script currently enabled?
   */

  private boolean isEnabled = true;




  /**
   * Creates a new <code>Script</code> with the specified name and the type and
   * subtype of the event that must occur for this script to be invoked.
   */

  public Script(PluginContext context, String name, String eventType, String [] eventSubtypes){
    if (context == null)
      throw new IllegalArgumentException("A null plugin context is not allowed");
    if (eventType == null)
      throw new IllegalArgumentException("A null event type for a script is not allowed");
    if ((name == null) || (name.length() == 0))
      throw new IllegalArgumentException("A null/empty script name is not allowed");

    this.context = context;
    this.name = name;
    this.eventType = eventType;
    this.eventSubtypes = (eventSubtypes == null ? null : (String [])(eventSubtypes.clone()));
  }



  /**
   * Returns the plugin context we're running in.
   */

  public PluginContext getContext(){
    return context;
  }



  /**
   * Returns the name of the script.
   */

  public String getName(){
    return name;
  }



  /**
   * Returns the event type that must occur for this script to be run.
   */

  public String getEventType(){
    return eventType;
  }




  /**
   * Returns a list of event subtypes this <code>Script</code> is invoked on.
   * Returns <code>null</code> if the event type this script responds to doesn't
   * have any subtypes.
   */

  public String [] getEventSubtypes(){
    return (String [])(eventSubtypes.clone());
  }




  /**
   * Returns the type of this script. This should be a string uniquely
   * identifying the <code>Class</code> of the script, but needn't necessarily
   * be the name of the class. The default implementation simply returns the
   * full name of the class.
   */

  public String getType(){
    return getClass().getName();
  }



  /**
   * Returns <code>true</code> if this <code>Script</code> is enabled,
   * <code>false</code> otherwise. Disabled scripts are not run. The default is
   * <code>true</code>.
   */

  public boolean isEnabled(){
    return isEnabled;
  }



  /**
   * Sets the enabled status of this <code>Script</code> to the specified value.
   */

  public void setEnabled(boolean isEnabled){
    this.isEnabled = isEnabled;
  }




  /**
   * This method is invoked by the <code>Scripter</code> plugin each time an
   * event is received.
   *
   * @param evt The event that triggered this script to run.
   * @param eventSubtype The subtype of the event.
   * @param vars An array of variables and their values. Each element is an
   * array of length 2 where the first item is a String specifying the name of
   * the variable and the 2nd item is the variable value.
   */

  public abstract void run(JinEvent evt, String eventSubtype, Object [][] vars);



  /**
   * Returns the name of the script.
   */

  public String toString(){
    return getName();
  }



  /**
   * Returns a copy of this Script.
   */

  public abstract Script createCopy();


}
