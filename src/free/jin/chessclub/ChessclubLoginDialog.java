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

package free.jin.chessclub;

import javax.swing.*;
import free.jin.StandardLoginDialog;
import free.jin.JinConnection;
import free.jin.Server;
import free.jin.User;
import free.util.BrowserControl;


/**
 * The login dialog for the chessclub.com server.
 */

public class ChessclubLoginDialog extends StandardLoginDialog{



  /**
   * Creates a new ChessclubLoginDialog.
   */

  public ChessclubLoginDialog(Server server){
    super("Chessclub.com Login Information", server);
  }



  /**
   * Creates a new ChessclubLoginDialog.
   */

  public ChessclubLoginDialog(User user){
    super("Chessclub.com Login Information", user);
  }




  /**
   * Opens the chessclub.com registration page in a browser.
   */

  protected void registerActionPerformed(){
    String url = "http://www.chessclub.com/register";
    try{
      BrowserControl.displayURL(url);
    } catch (java.io.IOException e){
        JOptionPane.showMessageDialog(dialog, "Unable to display URL: "+url);
      }
  }



  
  /**
   * Opens the chessclub.com password retrival page in a browser.
   */

  protected void forgotPasswordActionPerformed(){
    String url = "http://www.chessclub.com/mailpassword";
    try{
      BrowserControl.displayURL(url);
    } catch (java.io.IOException e){
        JOptionPane.showMessageDialog(dialog, "Unable to display URL: "+url);
      }
  }




  /**
   * Checks that the username is valid.
   */

  public String findInputIllegalityReason(String username, String password, String hostname,
      String port, boolean checkUsernameAndPassword){

    if (checkUsernameAndPassword){
      int usernameLength = username.length();
      if ((usernameLength < 2) || (usernameLength > 15))
        return "Usernames must be between 2 and 15 characters long";

      boolean hasHyphenAppeared = false;
      for (int i = 0; i < usernameLength; i++){
        char c = username.charAt(i);
        if (c == '-')
          if (hasHyphenAppeared)
            return "A username must contain at most one hyphen ('-')";
          else
            hasHyphenAppeared = true;
        if (!isValidUsernameCharacter(c))
          return "Your username contains at least one illegal character: " + c;
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
    if ((val>=97)&&(val<=122)) // Lowercase characters.
      return true;
    if ((val>=65)&&(val<=90)) // Uppercase characters.
      return true;
    if ((val>=48)&&(val<=57)) // Digits
      return true;
    if (c=='-') // Hyphen
      return true;
    return false;
  }




  /**
   * Creates a <code>JinChessclubConnection</code> based on the hostname, port,
   * username and password chosen by the user.
   */

  protected JinConnection createConnectionImpl(String hostname, int port, 
      String username, String password){

    return new JinChessclubConnection(hostname, port, username, password);
  }


}
