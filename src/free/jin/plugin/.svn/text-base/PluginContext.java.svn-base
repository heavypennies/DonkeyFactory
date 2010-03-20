/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
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

package free.jin.plugin;

import java.util.Hashtable;
import java.util.Properties;
import free.jin.JinConnection;
import free.jin.JinFrame;
import free.jin.User;

/**
 * The context in which a plugin is running.
 */

public class PluginContext{


  /**
   * The User (what more can I say?).
   */

  private final User user;
  


  /**
   * The JinConnection to the server.
   */

  private final JinConnection conn;


  /**
   * The Jin's main frame - JinFrame.
   */

  private final JinFrame mainFrame;



  /**
   * Maps plugin property names to property values.
   */

  private final Properties properties;




  /**
   * Maps plugin names to Plugins.
   */

  private final Hashtable plugins;



  /**
   * Creates a new PluginContext with the given User, JinConnection, JinFrame, properties
   * and Hashtable mapping plugin names to plugins.
   */

  public PluginContext(User user, JinConnection conn, JinFrame mainFrame, Properties properties, Hashtable plugins){
    this.user = user;
    this.conn = conn;
    this.mainFrame = mainFrame;
    this.properties = properties;
    this.plugins = plugins;
  }



  /**
   * Returns the User using the plugin.
   */

  public User getUser(){
    return user;
  }




  /**
   * Returns the connection to the server.
   */

  public JinConnection getConnection(){
    return conn;
  }



  /**
   * Returns Jin's main frame.
   */

  public JinFrame getMainFrame(){
    return mainFrame;
  }



  /**
   * Returns the Plugin with the given name.
   */

  public Plugin getPlugin(String pluginName){
    return (Plugin)plugins.get(pluginName);
  }



  /**
   * Returns the plugin property with the given name, or null if no property
   * with the given name exists.
   */

  public String getProperty(String parameterName){
    return properties.getProperty(parameterName);    
  }



  /**
   * Returns the plugin properties. Note that these are the actual properties,
   * modifying them will affect the plugin.
   */

  public Properties getProperties(){
    return properties;
  }



}
