/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Move;
import chess.engine.model.MoveComparator;
import chess.engine.utils.MoveGeneration;

import java.util.Arrays;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public class SimpleABSearch implements Searcher
{
  private BoardEvaluator eval;
  private MoveGeneration moveGeneration;
  private MoveComparator moveComparator = new MoveComparator();

  public SearchStats stats;


  public SearchStats getStats()
  {
    return stats;
  }


  public void setStats(SearchStats stats)
  {
    this.stats = stats;
  }


  public Move[] getPV()
  {
    return pv[0];
  }

  /* read write */
  public volatile boolean done = false;

  private Move[] currentLine = Move.createMoves(100);
  private Move[][] pv = new Move[100][100];
  private Move[][] moveLists = new Move[100][100];

  private int index;


  public SimpleABSearch(MoveGeneration moveGeneration, BoardEvaluator eval)
  {
    this.moveGeneration = moveGeneration;
    this.eval = eval;

    for(int i = 0;i < 100;i++)
    {
      moveLists[i] = Move.createMoves(100);
      pv[i] = Move.createMoves(100);

    }
  }

  public int search(Board board, int depth)
  {
    stats = new SearchStats();

    long start = System.currentTimeMillis();

    int score = simpleABSearch(-MATE, MATE, depth, board);

    long time = System.currentTimeMillis() - start;

    stats.time = (double)time / 1000;

    done = false;

    return score;
  }

  public int simpleABSearch(int alpha, int beta, int depth, Board board)
  {
/*
    if(Move.toString(currentLine).equals("rxa1 Kd2"))
    {
      int x = 1;
    }
*/

    if(depth == 0)
    {
      pv[index][index].moved = null;
      stats.evals++;
      return eval.scorePosition(board, alpha, beta);
    }

    Move[] moveList = moveLists[index];

    moveGeneration.generateFullMoves(moveList, board);
    Arrays.sort(moveList, moveComparator);

    int moveCount = 0;
    for(Move move : moveList)
    {
      if(move.moved == null)
      {
        break;
      }
      // make the move
      board.make(move);
      // unmake if we are in check
      if(moveGeneration.isSquareCheckedByColor(board, board.turn == 1 ? board.blackKing.square : board.whiteKing.square, board.turn ^ 1))
      {
        board.unmake(move);
        continue;
      }
      currentLine[index] = move;
      currentLine[index+1].moved = null;
      // if no check, count this move, and then recurse
      moveCount++;

      index++;
      int score = -simpleABSearch(-beta, -alpha, depth-1, board);
      index--;

      // unmake move
      board.unmake(move);

      if(done)
      {
        return -INFINITY;
      }

      // set up alpha
      if(score > alpha)
      {
        if(score >= beta)
        {
          return beta;
        }
        pv[index][index] = move;
        for(int t = index+1;t < 100;t++)
        {
          if((pv[index][t] = pv[index+1][t]) == null)
          {
            break;
          }
        }

        if(index == 0 && score - alpha > 100)
        {
          System.err.println("Improvement: " + Move.toString(getPV()));
        }

        alpha = score;
      }
    }

    if(moveCount == 0)
    {
      if(moveGeneration.isSquareCheckedByColor(board, board.turn == 1 ? board.whiteKing.square : board.blackKing.square, board.turn ^ 1))
      {
        pv[index][index].moved = null;
        return -(MATE - index);
      }
      else
      {
        pv[index][index].moved = null;
        return 0;
      }
    }

    return alpha;
  }


  public void stop()
  {
    done = true;
  }

  public boolean isDone()
  {
    return done;
  }


  public void reset() {
    
  }
}
