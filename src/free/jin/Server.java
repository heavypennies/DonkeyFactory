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
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;


/**
 * This class encapsulates the various properties of a chess server.
 */

public abstract class Server{



  /**
   * The properties of this server.
   */

  private Properties props;



  /**
   * The <code>URL</code> of the server's website.
   */

  private URL website;



  /**
   * The guest <code>User</code> of this server. Loaded/created lazily.
   */

  private User guest = null;



  /**
   * Creates a new Server object.
   */

  protected Server(){
    
  }



  /**
   * Initializes whatever the server needs. This is called immediately after
   * instantiation and may be used as a constructor.
   */

  protected void init(Properties props){
    this.props = props;

    String urlString = props.getProperty("website.url");
    try{
      website = new URL(urlString);
    } catch (MalformedURLException e){
        throw new IllegalArgumentException("Bad URL for server's website: "+urlString);
      }
  }



  /**
   * Loads a Server object from the given InputStream.
   */

  public static Server load(InputStream serverIn) throws IOException{
    Properties props = new Properties();
    props.load(serverIn);

    try{
      String classname = props.getProperty("classname");
      Server server = (Server)Class.forName(classname).newInstance();
      server.init(props);
      return server;
    } catch (InstantiationException e){
        e.printStackTrace();
      }
      catch (IllegalAccessException e){
        e.printStackTrace();
      }
      catch (ClassNotFoundException e){
        e.printStackTrace();
      }

    return null;
  }



  /**
   * Creates a new <code>User</code> with only the specified username.
   */

  public User createUser(String username){
    Properties props = new Properties();
    props.put("login.username", username);

    return createUser(props);
  }



  /**
   * Creates and returns a <code>User</code> for this <code>Server</code> with
   * the specified <code>Properties</code> and no User files.
   */

  protected User createUser(Properties props){
    return createUser(props, new Hashtable());
  }



  /**
   * Creates and returns a <code>User</code> for this <code>Server</code> with
   * the specified <code>Properties</code> and Hashtable containing the User
   * file names mapped to <code>MemoryFile</code> instances containing the
   * file's data.
   */

  protected User createUser(Properties props, Hashtable userFiles){
    return new User(this, props, userFiles);
  }




  /**
   * Returns the "guest" user <code>User</code>. Note that unlike the name
   * implies, guest preferences are actually kept between sessions
   * automatically.
   */

  public User getGuest(){
    if (guest == null){
      guest = Jin.loadGuest(this);
      if (guest == null)
        guest = createGuest();
    }

    return guest;
  }



  /**
   * Creates a new guest <code>User</code>.
   */

  protected abstract User createGuest();




  /**
   * Returns <code>true</code> if the specified <code>User</code> is the global
   * guest account.
   */

  boolean isGuest(User user){
    return user == guest;
  }




  /**
   * Creates and returns a new <code>LoginDialog</code> for this
   * <code>Server</code>.
   */

  public abstract LoginDialog createLoginDialog();




  /**
   * Creates and returns a new <code>LoginDialog</code> for this
   * <code>Server</code> using the specified <code>User</code> for the dialog
   * default.
   */

  public abstract LoginDialog createLoginDialog(User user);




  /**
   * Returns this server's property with the given name or <code>null</code> if
   * no such property exists for this server.
   */

  public String getProperty(String propertyName){
    return props.getProperty(propertyName);
  }




  /**
   * Returns an ID of this server. This should be a short, lowecase only string
   * without any whitespace. It should also be unique between all servers. It is
   * not necessary (although preferable) for it to be descriptive.
   */

  public String getID(){
    return getProperty("id");
  }



  /**
   * Returns the name of this server. The name is a descriptive string that can
   * be displayed to the user but is not necessarily the full name of the
   * server. The Free Internet Chess Server would be called here "FICS", for
   * example.
   */

  public String getName(){
    return getProperty("name");
  }




  /**
   * Returns a long server name. Example: "Internet Chess Club".
   */

  public String getLongName(){
    return getProperty("name.long");
  }



  /**
   * Returns the URL of the server's website.
   */

  public URL getWebsite(){
    return website;
  }




  /**
   * Returns a textual representation of this Server.
   */

  public String toString(){
    return getLongName()+" ("+getWebsite()+")";
  }

}
