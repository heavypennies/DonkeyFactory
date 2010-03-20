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

package free.chess.variants.shuffleboth;

import free.chess.variants.BothSidesCastlingVariant;
import free.chess.*;


/**
 * Implements the "shuffle both" wild variant. Quoting an ICC help file:
 * <PRE>
 * In this variant both sides have the same set of pieces as in normal
 * chess.  The white king starts on d1 or e1 and the black king starts on d8
 * or e8, and the rooks are in their usual positions.  Bishops are always on
 * opposite colors.  Subject to these constraints the position is random.
 * Castling is similar to normal chess: o-o-o indicates long castling and o-o
 * short castling.
 * </PRE>
 * Note that castling o-o when the king is on d1/d8, moves the king to b2/b8.
 */

public class ShuffleBoth extends BothSidesCastlingVariant{



  /**
   * The sole instance of this class.
   */

  private static final ShuffleBoth instance = new ShuffleBoth();



  /**
   * Returns an instance of this class.
   */

  public static ShuffleBoth getInstance(){
    return instance;
  }



  /**
   * Creates an instance of ShuffleBoth.
   */

  private ShuffleBoth(){
    super(Chess.INITIAL_POSITION_FEN /* Not used anyway */, "Shuffle Both");
  }




  /**
   * Initializes the given position to a random state subject to the constraints
   * specified in the rules.
   *
   * @throws IllegalArgumentException If the given Position's wild variant is
   * not ShuffleBoth.
   */

  public void init(Position pos){
    checkPosition(pos);

    pos.setFEN(createRandomInitialFEN());
  }





  /**
   * Creates a random initial position subject to the constraints specified in
   * the rules of Shuffle Both. The position is encoded and returned in
   * FEN format.
   */

  private static String createRandomInitialFEN(){
    String whitePieces = createRandomPieceRow();
    String blackPieces = createRandomPieceRow().toLowerCase();

    return blackPieces + "/pppppppp/8/8/8/8/PPPPPPPP/" + whitePieces + " w KQkq - 0 1";
  }




  /**
   * Creates and returns a random row of pieces, subject to the Shuffle Both
   * rules.
   */

  private static String createRandomPieceRow(){
    StringBuffer pieces = new StringBuffer("R------R");

    // The king
    pieces.setCharAt(randomBoolean() ? 3 : 4, 'K');

    // The 1st bishop
    while(true){
      int pos = randomInt(6) + 1;
      if ((pos%2 == 0) && (pieces.charAt(pos) == '-')){
        pieces.setCharAt(pos, 'B');
        break;
      }
    } 

    // The 2nd bishop
    while(true){
      int pos = randomInt(6) + 1;
      if ((pos%2 == 1) && (pieces.charAt(pos) == '-')){
        pieces.setCharAt(pos, 'B');
        break;
      }
    } 
    
    // The queen
    while(true){
      int pos = randomInt(6) + 1;
      if (pieces.charAt(pos) == '-'){
        pieces.setCharAt(pos, 'Q');
        break;
      }
    }

    // The knights
    for (int i = 1; i < 7; i++)
      if (pieces.charAt(i) == '-')
        pieces.setCharAt(i, 'N');

    return pieces.toString();
  }





  /**
   * Randomly returns either <code>true</code> or false. This method is used
   * when creating the initial, random position.
   */

  private static boolean randomBoolean(){
    return (Double.doubleToLongBits(Math.random())&0x01000000)==0;
  }




  /**
   * Returns a random int, in the range [0..max).  This method is used
   * when creating the initial, random position.
   */

  private static int randomInt(int max){
    return (int)(Math.random()*max);
  }

}
