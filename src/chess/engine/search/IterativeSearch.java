/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Move;
import chess.engine.utils.MoveGeneration;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public class IterativeSearch implements Searcher
{
  private Searcher search;
  private MoveGeneration moveGeneration;
  private BoardEvaluator eval;
  private SearchStats stats;
  private volatile boolean done = false;

  public int score;
  private boolean running = false;


  public SearchStats getStats()
  {
    return stats;
  }


  public void setStats(SearchStats stats)
  {
    this.stats = stats;
  }


  public IterativeSearch(Searcher search, MoveGeneration moveGeneration, BoardEvaluator eval)
  {
    this.search = search;
    this.moveGeneration = moveGeneration;
    this.eval = eval;

    done = true;
  }

  public Move[] getPV()
  {
    return search.getPV();
  }


  public synchronized int search(Board board, int maxDepth)
  {
    done = false;
    running = true;


    // check opening book

    // check EGTBs

    
    int currentDepth = 0;
    score = -INFINITY;

    stats = new SearchStats();

    search.setStats(stats);
    search.reset();

    board.stats.originalMaterial = eval.getMaterial(board);
    board.stats.originalMaterialDifference = eval.getMaterialDifference(board);
    board.stats.whiteAttacking = eval.getBlackKingSafety(board) < -100;
    board.stats.blackAttacking = eval.getWhiteKingSafety(board) < -100;

    long start = System.currentTimeMillis();

    while(currentDepth < maxDepth && !done)
    {
      int maybeScore = search.search(board, currentDepth);
      if(maybeScore > -MATE && maybeScore < MATE)
      {
        score = maybeScore;
      }
      long time = System.currentTimeMillis() - start;

      stats.time = (double) time / 1000;

      if(currentDepth > 3)
      {
        System.err.println("d[" + currentDepth + "] Stats: " + search.getStats());
        System.err.println("Best: " + Move.toString(search.getPV()));
        System.err.println("Score: " + maybeScore);
      }
//      System.err.println("LineScore: " + new LineScorer(moveGeneration, eval).scoreLine(board, search.getPV()) + "\n");

      if(done)
      {
        break;
      }

      currentDepth++;
    }
    done = true;
    search.stop();
    running = false;
    return score;
  }


  public void stop()
  {
    done = true;
    search.stop();
  }

  public boolean isDone()
  {
    return done && search.isDone();
  }


  public void reset() {
    done = true;
    search.reset();
  }
}
