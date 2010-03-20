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

import java.io.IOException;
import free.chess.art.ResourceImagePiecePainter;
import free.chess.art.ProxyPiecePainter;

/**
 * An implementation of <code>PiecePainter</code> which draws the bookup piece
 * set from blitzin. Note that the piece set belongs exlusively to
 * chessclub.com - you may use it only as part of Jin and only with the
 * chessclub.com server. You may *not* use it for any other purpose, 
 * commercial or otherwise.
 */

public class BookupPiecePainter extends ProxyPiecePainter{


  /**
   * The sole instance of the "real" bookup piece set painter.
   */

  private static final ResourceImagePiecePainter piecePainter;



  /**
   * Creates the real <code>ResourceImagePiecePainter</code>.
   */

  static{
    try{
      piecePainter =
        ResourceImagePiecePainter.getInstance(BookupPiecePainter.class, "bookup", "gif");
    } catch (IOException e){
        throw new RuntimeException("Unable to load the bookup piece set: "+e.getMessage());
      }
  }



  /**
   * Creates a new BookupBoardPiecePainter.
   */

  public BookupPiecePainter() throws IOException{
    super(piecePainter);
  }

}
