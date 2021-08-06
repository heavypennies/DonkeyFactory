/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Move;
import chess.engine.utils.MoveGeneration;

/**
 * @author Joshua Levine <levinester@gmail.com>
 * @version $Revision$ $Name$ $Date$
 */
public class IterativeSearch implements Searcher
{
  private Searcher search;
  private SearchWatcher searchWatcher;
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


  public IterativeSearch(Searcher search, MoveGeneration moveGeneration, BoardEvaluator eval, SearchWatcher searchWatcher)
  {
    this.search = search;
    this.moveGeneration = moveGeneration;
    this.eval = eval;
    this.searchWatcher = searchWatcher;

    done = true;
  }

  public Move[] getPV()
  {
    return search.getPV();
  }


  public int search(Board board, int maxDepth)
  {
    done = false;
    running = true;


    // check opening book

    // check EGTBs

    score = -INFINITY;

    stats = new SearchStats();
    search.setStats(stats);

    board.stats.originalMaterial = eval.getMaterial(board);
    board.stats.originalMaterialDifference = eval.getMaterialDifference(board);

    stats.startTime = System.currentTimeMillis();

    for(int currentDepth = 1; currentDepth < maxDepth + 1 && !done; currentDepth++)
    {
      stats.currentDepth = currentDepth;

      int maybeScore = search.search(board, currentDepth);
      if(maybeScore > -MATE && maybeScore < MATE)
      {
        score = maybeScore;
      }

      stats.score = score;

      searchWatcher.searchInfo(stats, getPV());

      if(done)
      {
        break;
      }
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
    search.stop();
    search.reset();
  }

  @Override
  public boolean isResearchAtRoot() {
    return search.isResearchAtRoot();
  }
}

