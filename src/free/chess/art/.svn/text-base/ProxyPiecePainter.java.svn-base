/**
 * The chess framework library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2003 Alexander Maryanovsky.
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

package free.chess.art;

import java.awt.*;
import free.chess.PiecePainter;
import free.chess.Piece;


/**
 * A proxy <code>PiecePainter</code> which simply delegates all the calls to a
 * real PiecePainter specified on construction. This class allows for the
 * existance of a single "real" piece painter (assuming it's immutable) while at
 * the same time preserving the expected behaviour of being able to instantiate
 * a <code>PiecePainter</code> via a no-arg constructor.
 */

public class ProxyPiecePainter implements PiecePainter{


  /**
   * The "real" (delegate) piece painter.
   */

  private final PiecePainter delegate;



  /**
   * Creates a new <code>ProxyPiecePainter</code> with the specified "real"
   * (delegate) <code>PiecePainter</code>.
   */

  public ProxyPiecePainter(PiecePainter delegate){
    if (delegate == null)
      throw new IllegalArgumentException("The delegate PiecePainter may not be null");

    this.delegate = delegate;
  }




  /**
   * Returns the "real" (delegate) <code>PiecePainter</code>.
   */

  public PiecePainter getDelegate(){
    return delegate;
  }




  /**
   * Delegates the call to the "real" <code>PiecePainter</code>.
   */

  public Dimension getPreferredPieceSize(){
    return delegate.getPreferredPieceSize();
  }




  /**
   * Delegates the call to the "real" <code>PiecePainter</code>.
   */

  public void paintPiece(Piece piece, Graphics g, Component component, Rectangle rect,
      boolean shaded){
    delegate.paintPiece(piece, g, component, rect, shaded);
  }



  /**
   * Returns a textual representation of this <code>ProxyPiecePainter</code>.
   */

  public String toString(){
    return "ProxyPiecePainter for "+delegate;
  }


}



