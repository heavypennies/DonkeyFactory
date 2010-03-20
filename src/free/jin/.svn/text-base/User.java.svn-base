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

package free.jin;

import java.util.Properties;
import java.util.Hashtable;
import free.util.Utilities;
import free.util.MemoryFile;


/**
 * A holder for the user's settings and preferences. Throughout the
 * documentation (unless I forgot/mistyped), the actual user is referred to as
 * "user" (lowercase 'u') and a <code>User</code> object is referred to as
 * "User" (uppercase 'u'). 
 */

public class User{



  /**
   * The server of this user.
   */

  private final Server server;



  /**
   * The properties of the User.
   */

  private final Properties props;




  /**
   * Maps "file" names to instances of <code>MemoryFile</code> which contain the
   * data of those files. This allows plugins to write data which cannot fit in
   * the User's settings and then read it.
   */

  private final Hashtable userFiles;




  /**
   * Creates a new <code>User</code> with the specified <code>Server</code>,
   * properties and user files.
   */

  User(Server server, Properties props, Hashtable userFiles){
    if (server == null)
      throw new IllegalArgumentException("The specified Server object may not be null");
    if (props == null)
      props = new Properties();
    if (userFiles == null)
      userFiles = new Hashtable();

    this.server = server;
    this.props = props;
    this.userFiles = userFiles;
  }




  /**
   * Returns the User's properties.
   */

  Properties getProperties(){
    return props;
  }




  /**
   * Returns the Hashtable mapping user "file" names to
   * <code>MemoryFile</code> instances holding the data of those
   * "files".
   */

  Hashtable getUserFiles(){
    return userFiles;
  }




  /**
   * Returns a <code>MemoryFile</code> with the specified name. Returns
   * <code>null</code> if no <code>MemoryFile</code> with the specified name
   * exists.
   */

  public MemoryFile getFile(String name){
    return (MemoryFile)userFiles.get(name);
  }



  /**
   * Adds the specified <code>MemoryFile</code> to this User's files under the
   * specified name. If a <code>MemoryFile</code> under the specified name
   * already exists, it is removed and returned by this method.
   */

  public MemoryFile putFile(String name, MemoryFile file){
    return (MemoryFile)Utilities.put(userFiles, name, file);
  }



  /**
   * Returns the value of the property of the user with the given name or null
   * if the user has no property with the given name.
   */

  public String getProperty(String propertyName){
    String propertyValue = props.getProperty(propertyName);
    return propertyValue == null ? getServer().getProperty(propertyName) : propertyValue;
  }




  /**
   * Returns the value of the property of the user with the given name or the 
   * given default value if the user has no property with the given name.
   */

  public String getProperty(String propertyName, String defaultValue){
    String propertyValue = getProperty(propertyName);
    return propertyValue == null ? defaultValue : propertyValue;
  }





  /**
   * Returns the username of this user - the value of the "login.username"
   * property.
   */

  public String getUsername(){
    return getProperty("login.username");
  }




  /**
   * Sets this user's property with the given name to the given value, or, if
   * the specified property value is null, removes the property.
   * 
   * @param propertyName The name of the property.
   * @param propertyValue The new value of the property.
   */

  public void setProperty(String propertyName, String propertyValue){
    if (propertyValue == null)
      props.remove(propertyName);
    else
      props.put(propertyName, propertyValue);
  }



  /**
   * Returns the Server object represeting the serverthis user logs on to.
   */

  public Server getServer(){
    return server;
  }



  /**
   * Returns <code>true</code> if this user is the global guest account.
   */

  public boolean isGuest(){
    return getServer().isGuest(this);
  }




  /**
   * Returns a textual representation of the user.
   */

  public String toString(){
    return "User[server=\""+getServer().getLongName()+"\",login.username=\""+getUsername()+"\"]";
  } 

}
