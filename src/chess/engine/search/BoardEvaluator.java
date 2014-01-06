/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Square;

/**
 * @author Joshua Levine <levinester@gmail.com>
 * @version $Revision$ $Name$ $Date$
 */
public interface BoardEvaluator
{

  public static int[][][] PIECE_VALUE_TABLES = new int[2][8][64];
  public static int[][] CENTER_VALUE_TABLES = new int[2][64];


  public int scorePosition(Board board, int alpha, int beta);
  public int scoreAttackingPieces(Board board, Square kingSquare, int attackerColor);
  public int getMaterial(Board board);
  public int getMaterialDifference(Board board);
  public void reset();

}
