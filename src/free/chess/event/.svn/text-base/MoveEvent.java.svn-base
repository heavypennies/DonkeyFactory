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

package free.chess.event;

import free.chess.*;
import java.util.*;



/**
 * The event sent when a move is made in a chess position.
 */

public class MoveEvent extends EventObject{


  /**
   * The move that was made.
   */

  private Move move;



  /**
   * The Position the move was made in.
   */

  private Position pos;




  /**
   * Creates a new MoveEvent with the given Move and the Position
   * it was made on.
   */

  public MoveEvent(Position pos, Move move){
    super(pos);
    this.move = move;
    this.pos = pos;
  }




  /**
   * Returns the Move that trigerred this MoveEvent.
   */

  public Move getMove(){
    return move;
  }



  /**
   * Returns the Position that changed as the result of this MoveEvent.
   */

  public Position getPosition(){
    return pos;
  }

}
