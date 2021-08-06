/* $Id$ */

package chess.engine.search;

import static chess.engine.search.Searcher.MATE;

/**
 * @author Joshua Levine <levinester@gmail.com>
 * @version $Revision$ $Name$ $Date$
 */
public class SearchStats {
  private static final String SPACES = "                                             ";

  public int score;
  public long startTime;
  public int nodes;
  public int pvNodes;
  public int zwNodes;
  public int qNodes;
  public int evals;
  public int reduceBoring;

  public int hashMisses;
  public int hardHashHits;
  public int qHashHits;
  public int softHashHits;

  public int reducePrune;
  public int reduceMargin;

  public int endgameExtensions;
  public int checkExtensions;
  public int doubleCheckExtensions;
  public int pawnPushExtensions;
  public int recaptureExtensions;
  public int nullThreatExtensions;
  public int threatExtensions;
  public int reduceFutile;
  public int currentDepth;

  public String toInfoString() {
    return "score " + formatScore(score) +
            " depth " + currentDepth +
            " time " + getTime() +
            " nodes " + nodes +
            " nps " + (nodes / Math.max(1,getTime()));
  }

  private long getTime() {
    return System.currentTimeMillis() - startTime;
  }

  public String formatScore(int score) {

    if (score > Searcher.MATE - 300) {
      return "mate " + (Searcher.MATE - score + 1) / 2;
    } else if (score < -Searcher.MATE + 300) {
      int mateDistance = (Searcher.MATE + score) / 2;
      return "mate " + -mateDistance;
    }

    return "cp " + score;
  }

  public String toString() {
    return new StringBuilder()
            .append("D[ ").append(pad(currentDepth, 3)).append(" ]")
            .append("    |   T: ").append(pad(getTime(), 8))
            .append("  E/S: ").append(pad((long) ((evals*1000L) / Math.max(1,getTime())), 8))
            .append("  N/S: ").append(pad((long) ((nodes*1000L) / Math.max(1,getTime())), 8))
            .append("    S: ").append(formatScore(score))
            .append("\nSearch      |   N: ").append(pad(nodes, 8))
            .append("    E: ").append(pad(evals, 8))
            .append("  PVN: ").append(pad(pvNodes, 8))
            .append("   ZWN: ").append(pad(zwNodes, 8))
            .append("    QN: ").append(pad(qNodes, 8))
            .append("\nHash        | HHH: ").append(pad(hardHashHits, 8))
            .append("  SHH: ").append(pad(softHashHits, 8))
            .append("  QHH: ").append(pad(qHashHits, 8))
            .append("  Miss: ").append(pad(hashMisses, 8))
            .append("\nExtensions  |  EX: ").append(pad(endgameExtensions, 8))
            .append("   PX: ").append(pad(pawnPushExtensions, 8))
            .append("   CX: ").append(pad(checkExtensions, 8))
            .append("   DCX: ").append(pad(doubleCheckExtensions, 8))
            .append("   RX: ").append(pad(recaptureExtensions, 8))
            .append("  NTX: ").append(pad(nullThreatExtensions, 8))
            .append("   TX: ").append(pad(threatExtensions, 8))
            .append("\nReductions  |   B: ").append(pad(reduceBoring, 8))
            .append("    M: ").append(pad(reduceMargin, 8))
            .append("    P: ").append(pad(reducePrune, 8))
            .append("     F: ").append(pad(reduceFutile, 8)).toString();
  }

  private String pad(Number value, int length) {
    StringBuffer buffer = new StringBuffer("" + value);
    return buffer.insert(0, SPACES.substring(0, Math.max(1, length - buffer.length()))).toString();
  }


  public void reset() {
    currentDepth = 0;
    score = 0;
    nodes = 0;
    evals = 0;
    reduceBoring = 0;

    hashMisses = 0;
    hardHashHits = 0;
    qHashHits = 0;
    softHashHits = 0;

    reducePrune = 0;
    reduceMargin = 0;

    endgameExtensions = 0;
    checkExtensions = 0;
    doubleCheckExtensions = 0;
    pawnPushExtensions = 0;
    recaptureExtensions = 0;
  }
}

