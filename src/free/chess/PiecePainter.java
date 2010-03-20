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

import java.awt.Graphics;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * An interface classes that know how to paint chess pieces should implement.
 */

public interface PiecePainter{


  /**
   * Returns the preferred size of the pieces. Should return a dimension that
   * has no area (width<=0 or height<=0) if this PiecePainter doesn't have a 
   * preferred piece size.
   */

  Dimension getPreferredPieceSize();
    


  /**
   * Paints the given piece at the given coordinates on the given
   * <code>Graphics</code> object scaled to the given size. The
   * <code>component</code> argument may be null. The <code>shaded</code>
   * argument specifies whether the piece should be drawn in a special, shaded
   * manner.
   */

  void paintPiece(Piece piece, Graphics g, Component component, Rectangle rect, boolean shaded);

}
