/* $Id$ */

package chess.engine.utils;

import chess.engine.model.Move;
import chess.engine.model.Board;
import chess.engine.search.BoardEvaluator;
import chess.engine.search.Searcher;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public class LineScorer
{
  MoveGeneration moveGeneration;
  BoardEvaluator boardEvaluator;

  public LineScorer(MoveGeneration moveGeneration, BoardEvaluator boardEvaluator)
  {
    this.moveGeneration = moveGeneration;
    this.boardEvaluator = boardEvaluator;
  }

  public int scoreLine(Board board, Move[] line)
  {
    int lineLength = 0;
    for(Move move : line)
    {
      if(move.moved == null)
      {
        break;
      }
      board.make(move);

      int score = boardEvaluator.scorePosition(board, -Searcher.INFINITY, Searcher.INFINITY);
//      System.err.println("LineScore ("+move.toString() + "): " + score);
      lineLength++;
    }

    int score = boardEvaluator.scorePosition(board, -Searcher.INFINITY, Searcher.INFINITY);

    for(int t = lineLength - 1;t >= 0;t--)
    {
      board.unmake(line[t]);
    }

    return score;
  }
}
