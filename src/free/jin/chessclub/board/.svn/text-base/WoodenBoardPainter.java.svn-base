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

package free.jin.chessclub.board;

import java.awt.Toolkit;
import free.chess.ImageBoardPainter;


/**
 * An implementation of <code>BoardPainter</code> which uses the wooden2 board
 * from blitzin. Note that the images of the board set belongs exlusively to
 * chessclub.com - you may use them only as part of Jin and only with the
 * chessclub.com server. You may *not* use it for any other purpose, 
 * commercial or otherwise.
 */

public class WoodenBoardPainter extends ImageBoardPainter{



  /**
   * Creates a new WoodenBoardPainter.
   */

  public WoodenBoardPainter(){
    super(preload(Toolkit.getDefaultToolkit().getImage(WoodenBoardPainter.class.getResource("wooden/light.gif"))),
          preload(Toolkit.getDefaultToolkit().getImage(WoodenBoardPainter.class.getResource("wooden/dark.gif"))), false);
  }


}
