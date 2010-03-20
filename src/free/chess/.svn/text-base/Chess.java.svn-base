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


/**
 * An implementation of WildVariant for the game of chess (classic).
 */

public final class Chess extends ChesslikeGenericVariant{



  /**
   * The lexigraphic representation of the initial position in chess.
   */

  public static final String INITIAL_POSITION_LEXIGRAPHIC = "rnbqkbnrpppppppp--------------------------------PPPPPPPPRNBQKBNR";




  /**
   * The FEN representation of the initial position in chess.
   */

  public static final String INITIAL_POSITION_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";




  /**
   * The sole instance of this class.
   */

  private static Chess instance = new Chess();




  /**
   * Returns an instance of Chess.
   */

  public static Chess getInstance(){
    return instance;
  }




  /**
   * Creates a new instance of Chess.
   */

  private Chess(){
    super(INITIAL_POSITION_FEN, "Chess");
  }


}
