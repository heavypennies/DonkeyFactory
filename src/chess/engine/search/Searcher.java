/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Move;

/**
 * @author Joshua Levine <levinester@gmail.com>
 * @version $Revision$ $Name$ $Date$
 */
public interface Searcher
{
  public static int INFINITY = 10010000;
  public static int MATE = INFINITY - 10000;

  SearchStats getStats();
  void setStats(SearchStats stats);
  Move[] getPV();
  int search(Board board, int depth);
  boolean isDone();
  void stop();
  void reset();

  boolean isResearchAtRoot();

}
