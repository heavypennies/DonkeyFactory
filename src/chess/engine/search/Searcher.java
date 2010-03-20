/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Move;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public interface Searcher
{
  public static int INFINITY = 70000;
  public static int MATE = 60000;

  SearchStats getStats();
  void setStats(SearchStats stats);
  Move[] getPV();
  int search(Board board, int depth);
  boolean isDone();
  void stop();
  void reset();
}
