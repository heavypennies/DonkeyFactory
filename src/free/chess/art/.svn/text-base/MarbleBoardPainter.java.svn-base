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

package free.chess.art;

import java.awt.Toolkit;
import free.chess.ImageBoardPainter;


/**
 * An implementation of <code>BoardPainter</code> which uses the marble board
 * from blitzin. The images themselves also appear in GIMP (The GNU Image
 * Manipulation Program - http://www.gimp.org/), so I'm assuming they are free
 * for use until notified otherwise.
 */

public class MarbleBoardPainter extends ImageBoardPainter{



  /**
   * Creates a new MarbleBoardPainter.
   */

  public MarbleBoardPainter(){
    super(preload(Toolkit.getDefaultToolkit().getImage(MarbleBoardPainter.class.getResource("marble/light.gif"))),
          preload(Toolkit.getDefaultToolkit().getImage(MarbleBoardPainter.class.getResource("marble/dark.gif"))), false);
  }


}
