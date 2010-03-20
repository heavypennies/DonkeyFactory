/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public interface BoardEvaluator
{

  public static int[][][] PIECE_VALUE_TABLES = new int[2][8][64];


  public int scorePosition(Board board, int alpha, int beta);
  public int getMaterial(Board board);
  public int getMaterialDifference(Board board);
  public int getKingSafety(Board board);

  public int getPawns(Board board);
  public int getPawnsDifference(Board board);

  public void reset();

  int getBlackKingSafety(Board board);
  int getWhiteKingSafety(Board board);

  int getLastWhiteKingSafety();
  int getLastBlackKingSafety();
}
