/**
 * The chess framework library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The chess framework library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The chess framework library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the chess framework library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package free.chess;

import javax.swing.Icon;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Rectangle;

public class PieceIcon implements Icon{


  /**
   * The Piece.
   */

  private final Piece piece;



  /**
   * The PiecePainter.
   */

  private final PiecePainter piecePainter;



  /**
   * The width of the icon.
   */

  private final int width;



  /**
   * The height of the icon.
   */

  private final int height;




  /**
   * Creates a new PieceIcon with the given Piece, PiecePainter, width and height.
   */

  public PieceIcon(Piece piece, PiecePainter piecePainter, int width, int height){
    this.piece = piece;
    this.piecePainter = piecePainter;
    this.width = width;
    this.height = height;
  }



  
  /**
   * Returns the width of the icon.
   */

  public int getIconWidth(){
    return width;
  }



  /**
   * Returns the height of the icon.
   */

  public int getIconHeight(){
    return height;
  }



  /**
   * Paints the icon on the given Graphics, at the given location.
   */

  public void paintIcon(Component component, Graphics g, int x, int y){
    piecePainter.paintPiece(piece, g, component, new Rectangle(x, y, width, height), false);
  }

}
