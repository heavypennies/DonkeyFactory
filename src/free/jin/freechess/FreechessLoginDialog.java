/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002, 2003 Alexander Maryanovsky.
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

package free.jin.freechess;

import javax.swing.*;
import free.jin.StandardLoginDialog;
import free.jin.JinConnection;
import free.jin.Server;
import free.jin.User;
import free.util.BrowserControl;


/**
 * The login dialog for the freechess.org server.
 */

public class FreechessLoginDialog extends StandardLoginDialog{



  /**
   * Creates a new FreechessLoginDialog.
   */

  public FreechessLoginDialog(Server server){
    super("FICS login Information", server);
  }



  /**
   * Creates a new FreechessLoginDialog.
   */

  public FreechessLoginDialog(User user){
    super("FICS login Information", user);
  }




  /**
   * Opens the freechess.org registration page in a browser.
   */

  protected void registerActionPerformed(){
    String url = "http://www.freechess.org/Register/FICS_register.cgi";
    try{
      BrowserControl.displayURL(url);
    } catch (java.io.IOException e){
        JOptionPane.showMessageDialog(dialog, "Unable to display URL: "+url);
      }
  }




  /**
   * Returns <code>null</code>.
   */
  
  protected JButton createForgotPasswordButton(){
    return null;
  }




  /**
   * Throws an UnsupportedOperationException.
   */

  protected void forgotPasswordActionPerformed(){
    throw new free.util.UnsupportedOperationException();
  }




  /**
   * Checks that the username is valid.
   */

  public String findInputIllegalityReason(String username, String password, String hostname,
      String port, boolean checkUsernameAndPassword){

    if (checkUsernameAndPassword){
      int usernameLength = username.length();
      if ((usernameLength<3)||(usernameLength>17))
        return "Usernames must be between 3 and 17 characters long";

      for (int i=0;i<usernameLength;i++){
        char c = username.charAt(i);
        if (!isValidUsernameCharacter(c))
          return "Your username contains at least one illegal character: "+c;
      }

      if (username.equalsIgnoreCase("guest"))
        return "You should login as guest by clicking the \"Login as Guest\" button";
    }

    return super.findInputIllegalityReason(username, password, hostname, port,
      checkUsernameAndPassword);
  }




  /**
   * Returns true if the given character is a valid character for a username.
   */

  private boolean isValidUsernameCharacter(char c){
    int val = c;
    if ((val >= 97) && (val <= 122)) // Lowercase characters.
      return true;
    if ((val >= 65) && (val <= 90)) // Uppercase characters.
      return true;
    return false;
  }




  /**
   * Creates a <code>JinFreechessConnection</code> based on the hostname, port,
   * username and password chosen by the user.
   */

  protected JinConnection createConnectionImpl(String hostname, int port, 
      String username, String password){

    return new JinFreechessConnection(hostname, port, username, password);
  }

}
