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

package free.jin.chessclub.console;

import free.jin.console.ConsoleTextField;


/**
 * An extension of ConsoleTextField which implements some ICC specific features.
 */

public class ChessclubConsoleTextField extends ConsoleTextField{




  /**
   * Creates a new ChessclubConsoleTextField which will be a part of the given
   * ChessclubConsole.
   */

  public ChessclubConsoleTextField(ChessclubConsole console){
    super(console);
  }



  
  /**
   * Sets the textfield to be ready to send a tell to the given player.
   * Sets the text in the textfield to <code>"tell "+playerName+"! "</code> and
   * puts the caret at the end of the text.
   */

  protected void setTellPersonState(String playerName){
    String text = "tell "+playerName+"! ";
    setText(text);
    setCaretPosition(text.length());
  }


}
